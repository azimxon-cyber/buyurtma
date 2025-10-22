package com.example.buyurtma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyurtma.extensions.formatPrice
import com.example.buyurtma.models.OrderState
import com.example.buyurtma.mvi.OrderIntent

@Composable
fun CartPanel(state: OrderState, onIntent: (OrderIntent) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Buyurtma", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Surface(
                shape = CircleShape,
                color = Color(0xFFE0E0E0),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(state.cartItems.size.toString())
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (state.cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Корзина пуста", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.cartItems, key = { it.product.id }) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { qty ->
                            onIntent(OrderIntent.UpdateQuantity(item.product.id, qty))
                        },
                        onStatusChange = { status ->
                            onIntent(OrderIntent.ChangeProductStatus(item.product.id, status))
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Общая сумма", color = Color.Gray)
                Text(state.totalAmount.formatPrice())
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Скидка", color = Color.Gray)
                Text("-${state.discountAmount.formatPrice()}", color = Color(0xFFE53935))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Итого", fontWeight = FontWeight.Bold)
                Text(state.finalAmount.formatPrice(), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton(
                text = "Отменить\nзаказ",
                icon = "🗑",
                modifier = Modifier.weight(1f),
                onClick = { onIntent(OrderIntent.ClearCart) }
            )
            ActionButton(
                text = "Отправить\nна кухню",
                icon = "👨‍🍳",
                modifier = Modifier.weight(1f),
                onClick = { onIntent(OrderIntent.SendToKitchen) }
            )
            ActionButton(
                text = "Распечатать\nсчет",
                icon = "🖨",
                modifier = Modifier.weight(1f),
                onClick = { onIntent(OrderIntent.PrintReceipt) }
            )
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { onIntent(OrderIntent.Pay) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Оплата", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}
