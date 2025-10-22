package com.example.buyurtma.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyurtma.models.Category

@Composable
fun CategoryCard(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0)),
        modifier = Modifier.size(120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                when (category.icon) {
                    "discount" -> "🏷️"
                    "hot" -> "🔥"
                    "favorite" -> "⭐"
                    else -> "🍜"
                }, fontSize = 28.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(category.name, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}
