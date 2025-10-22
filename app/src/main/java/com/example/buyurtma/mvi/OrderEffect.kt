package com.example.buyurtma.mvi

sealed class OrderEffect {
    object OrderSent : OrderEffect()
    object ReceiptPrinted : OrderEffect()
    object PaymentCompleted : OrderEffect()
    data class ShowMessage(val message: String) : OrderEffect()
}