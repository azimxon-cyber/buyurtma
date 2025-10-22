package com.example.buyurtma.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyurtma.R
import com.example.buyurtma.models.CartItem
import com.example.buyurtma.models.Category
import com.example.buyurtma.models.OrderState
import com.example.buyurtma.models.Product
import com.example.buyurtma.models.ProductStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OrderEffect>()
    val effect: SharedFlow<OrderEffect> = _effect.asSharedFlow()

    private var allProducts by mutableStateOf(listOf<Product>())

    init {
        loadInitialData()
    }

    fun handleIntent(intent: OrderIntent) {
        when (intent) {
            is OrderIntent.AddToCart -> addToCart(intent.product)
            is OrderIntent.RemoveFromCart -> removeFromCart(intent.productId)
            is OrderIntent.UpdateQuantity -> updateQuantity(intent.productId, intent.quantity)
            is OrderIntent.ChangeProductStatus -> changeStatus(intent.productId, intent.status)
            is OrderIntent.SearchProducts -> searchProducts(intent.query)
            is OrderIntent.SelectCategory -> selectCategory(intent.categoryId)
            is OrderIntent.ClearCart -> clearCart()
            is OrderIntent.SendToKitchen -> sendToKitchen()
            is OrderIntent.PrintReceipt -> printReceipt()
            is OrderIntent.Pay -> pay()
        }
    }

    private fun loadInitialData() {
        val categories = listOf(
            Category(1, "Товары со скидкой", "discount"),
            Category(2, "Горячие блюда", "hot"),
            Category(3, "Избранные товары", "favorite"),
            Category(4, "Супы", "soup")
        )

        val products = listOf(
            Product(
                1,
                "Чебурек с сыром",
                48000,
                quantity = 10,
                discountPercent = 10,
                image = R.drawable.mini_lavash
            ),
            Product(2, "Мужской каприз 250 г", 45000, quantity = 0, image = R.drawable.first),
            Product(3, "Хрустящие баклажаны", 40000, quantity = 10, image = R.drawable.lavash),
            Product(4, "Феттучине Альфредо", 46000, quantity = 10, image = R.drawable.stake2),
            Product(5, "Большое кёфте Калкан", 125000, quantity = 10, image = R.drawable.burger),
            Product(
                6,
                "Коктейль Мохито 1л",
                50000,
                quantity = 10,
                image = R.drawable.soup_with_cheese
            ),
            Product(8, "Pizza", 32000, quantity = 14, image = R.drawable.stake),
            Product(
                9,
                "Soup with cheese",
                32000,
                quantity = 14,
                image = R.drawable.soup_with_cheese
            ),
            Product(10, "Palov", 32000, quantity = 14, image = R.drawable.plov),
            Product(11, "Samsa", 32000, quantity = 14, image = R.drawable.burger),
            Product(12, "Lagman soup", 32000, quantity = 14, image = R.drawable.images),

            )

        val cartItems = listOf(
            CartItem(products[3], 1),
            CartItem(products[1], 1),
            CartItem(products[5], 2)
        )

        allProducts = products

        _state.update { it.copy(categories = categories, products = products, cartItems = cartItems) }
        calculateTotals()
    }

    private fun addToCart(product: Product) {
        val current = _state.value.cartItems.toMutableList()
        val existing = current.find { it.product.id == product.id }
        if (existing != null) {
            val idx = current.indexOf(existing)
            current[idx] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        _state.update { it.copy(cartItems = current) }
        calculateTotals()
    }

    private fun removeFromCart(productId: Int) {
        val current = _state.value.cartItems.filterNot { it.product.id == productId }
        _state.update { it.copy(cartItems = current) }
        calculateTotals()
    }

    private fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        val current = _state.value.cartItems.toMutableList()
        val idx = current.indexOfFirst { it.product.id == productId }
        if (idx != -1) {
            current[idx] = current[idx].copy(quantity = quantity)
            _state.update { it.copy(cartItems = current) }
            calculateTotals()
        }
    }

    private fun changeStatus(productId: Int, status: ProductStatus) {
        val current = _state.value.cartItems.toMutableList()
        val idx = current.indexOfFirst { it.product.id == productId }
        if (idx != -1) {
            val updated = current[idx].product.copy(status = status)
            current[idx] = current[idx].copy(product = updated)
            _state.update { it.copy(cartItems = current) }
        }
    }

    private fun searchProducts(query: String) {
        _state.update { state ->
            val filtered = allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true)
            }

            state.copy(
                searchQuery = query,
                products = filtered
            )
        }
    }

    private fun selectCategory(categoryId: Int?) {
        _state.update { it.copy(selectedCategory = categoryId) }
    }

    private fun clearCart(fromPay: Boolean = false) {
        _state.update { it.copy(cartItems = emptyList()) }
        calculateTotals()

        if(fromPay.not())
            viewModelScope.launch { _effect.emit(OrderEffect.ShowMessage("Заказ отменён")) }
    }

    private fun sendToKitchen() {
        viewModelScope.launch { _effect.emit(OrderEffect.OrderSent) }
    }

    private fun printReceipt() {
        viewModelScope.launch { _effect.emit(OrderEffect.ReceiptPrinted) }
    }

    private fun pay() {
        // simple simulation of payment
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            // simulate network/payment delay in real app
            _effect.emit(OrderEffect.PaymentCompleted)
            clearCart(true)
            _state.update { it.copy(isProcessing = false) }
        }
    }

    private fun calculateTotals() {
        val total = _state.value.cartItems.sumOf { it.product.price * it.quantity }
        // discount as combination of product discounts + global discount
        val perItemDiscount = _state.value.cartItems.sumOf { (it.product.price * it.product.discountPercent / 100) * it.quantity }
        val globalDiscount = (total * 0.05).toInt() // example: 5% global
        val discount = perItemDiscount + globalDiscount
        val final = (total - discount).coerceAtLeast(0)

        _state.update { it.copy(totalAmount = total, discountAmount = discount, finalAmount = final) }
    }
}