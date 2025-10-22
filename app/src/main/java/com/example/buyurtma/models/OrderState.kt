package com.example.buyurtma.models

data class OrderState(
    val cartItems: List<CartItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: Int? = null,
    val totalAmount: Int = 0,
    val discountAmount: Int = 0,
    val finalAmount: Int = 0,
    val isProcessing: Boolean = false
)