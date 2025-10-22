package com.example.buyurtma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.buyurtma.models.OrderState
import com.example.buyurtma.mvi.OrderIntent
import kotlinx.coroutines.flow.filter

@Composable
fun ProductsPanel(state: OrderState, onIntent: (OrderIntent) -> Unit, modifier: Modifier = Modifier, adaptiveMinCellSize: Dp = 160.dp) {
    Column(modifier = modifier.background(Color.White, RoundedCornerShape(12.dp)).padding(16.dp)) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onIntent(OrderIntent.SearchProducts(it)) },
            placeholder = { Text("Поиск продукта") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { Text("🔍") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.categories) { category ->
                    CategoryCard(
                        category = category,
                        isSelected = state.selectedCategory == category.id,
                        onClick = {
                            val newSel =
                                if (state.selectedCategory == category.id) null else category.id
                            onIntent(OrderIntent.SelectCategory(newSel))
                        }
                    )

                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val filtered = remember(state.products, state.searchQuery, state.selectedCategory) {
            state.products.filter { p ->
                val matchesCategory = state.selectedCategory?.let { catId ->
                    when (catId) {
                        1 -> p.discountPercent > 0
                        2 -> true
                        3 -> true
                        4 -> p.name.contains("суп", ignoreCase = true)
                        else -> true
                    }
                } ?: true

                val matchesQuery = state.searchQuery.isBlank() || p.name.contains(state.searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = adaptiveMinCellSize), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
            items(filtered, key = { it.id }) { product ->
                ProductCard(product = product, onClick = { if (product.quantity > 0) onIntent(OrderIntent.AddToCart(product)) })
            }
        }
    }
}
