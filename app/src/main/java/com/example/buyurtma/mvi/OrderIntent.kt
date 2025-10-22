package com.example.buyurtma.mvi

import com.example.buyurtma.models.Product
import com.example.buyurtma.models.ProductStatus

sealed class OrderIntent {
    data class AddToCart(val product: Product) : OrderIntent()
    data class RemoveFromCart(val productId: Int) : OrderIntent()
    data class UpdateQuantity(val productId: Int, val quantity: Int) : OrderIntent()
    data class ChangeProductStatus(val productId: Int, val status: ProductStatus) : OrderIntent()
    data class SearchProducts(val query: String) : OrderIntent()
    data class SelectCategory(val categoryId: Int?) : OrderIntent()
    object ClearCart : OrderIntent()
    object SendToKitchen : OrderIntent()
    object PrintReceipt : OrderIntent()
    object Pay : OrderIntent()
}
