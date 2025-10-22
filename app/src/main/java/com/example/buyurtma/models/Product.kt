package com.example.buyurtma.models

import com.example.buyurtma.ProductStatus

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val image: Int,
    val quantity: Int = 0,
    val status: ProductStatus = ProductStatus.DONE,
    val discountPercent: Int = 0
)