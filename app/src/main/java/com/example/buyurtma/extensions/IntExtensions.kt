package com.example.buyurtma.extensions

import java.text.NumberFormat
import java.util.Locale

fun Int.formatPrice(): String {
    val fmt = NumberFormat.getIntegerInstance(Locale("ru"))
    return fmt.format(this) + " сум"
}
