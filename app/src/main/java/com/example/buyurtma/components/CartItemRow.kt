package com.example.buyurtma.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyurtma.extensions.formatPrice
import com.example.buyurtma.models.CartItem
import com.example.buyurtma.models.ProductStatus

@Composable
fun CartItemRow(item: CartItem, onQuantityChange: (Int) -> Unit, onStatusChange: (ProductStatus) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF9FFF9), RoundedCornerShape(8.dp))
        .padding(10.dp)
        .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Medium)
                Text((item.product.price).formatPrice(), color = Color.Gray, fontSize = 12.sp)
            }

            Surface(shape = RoundedCornerShape(12.dp), color = when (item.product.status) {
                ProductStatus.DONE -> Color(0xFFFFF9C4)
                ProductStatus.READY -> Color(0xFFC8E6C9)
                ProductStatus.CANCELLED -> Color(0xFFFFCDD2)
            }, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                val next = when (item.product.status) {
                    ProductStatus.DONE -> ProductStatus.READY
                    ProductStatus.READY -> ProductStatus.CANCELLED
                    ProductStatus.CANCELLED -> ProductStatus.DONE
                }
                onStatusChange(next)
            }) {
                Text(
                    when (item.product.status) {
                        ProductStatus.DONE -> "Сделано"
                        ProductStatus.READY -> "Готово"
                        ProductStatus.CANCELLED -> "Отменено"
                    }, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        onQuantityChange(item.quantity - 1)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("−", fontSize = 20.sp)
                }

                Text(item.quantity.toString(), modifier = Modifier.widthIn(min = 28.dp), textAlign = TextAlign.Center)

                IconButton(
                    onClick = {
                        onQuantityChange(item.quantity + 1)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("+", fontSize = 20.sp)
                }
            }

            Text((item.product.price * item.quantity).formatPrice(), fontWeight = FontWeight.Medium)
        }
    }
}
