package com.example.buyurtma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.buyurtma.extensions.formatPrice
import com.example.buyurtma.models.OrderState
import com.example.buyurtma.mvi.OrderEffect
import com.example.buyurtma.mvi.OrderIntent
import com.example.buyurtma.mvi.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(viewModel: OrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrderEffect.OrderSent -> snackbarHostState.showSnackbar("Заказ отправлен на кухню")
                is OrderEffect.PaymentCompleted -> snackbarHostState.showSnackbar("Оплата прошла успешно")
                is OrderEffect.ReceiptPrinted -> snackbarHostState.showSnackbar("Чек распечатан")
                is OrderEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            val maxW = maxWidth
            val isCompact = maxW < 600.dp
            var showCartDialog by remember { mutableStateOf(false) }

            if (isCompact) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ProductsPanel(
                        state = state,
                        onIntent = viewModel::handleIntent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(12.dp),
                        adaptiveMinCellSize = 150.dp
                    )

                    CompactCartBar(
                        state = state,
                        onIntent = viewModel::handleIntent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        showCartDialog = true
                    }
                }

                if (showCartDialog) {
                    Dialog(onDismissRequest = { showCartDialog = false }) {
                        Surface(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            CartPanel(state = state, onIntent = viewModel::handleIntent, modifier = Modifier.fillMaxHeight(0.85f).padding(12.dp))
                        }
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    CartPanel(
                        state = state,
                        onIntent = viewModel::handleIntent,
                        modifier = Modifier
                            .weight(0.36f)
                            .fillMaxHeight()
                            .padding(12.dp)
                    )

                    ProductsPanel(
                        state = state,
                        onIntent = viewModel::handleIntent,
                        modifier = Modifier
                            .weight(0.64f)
                            .fillMaxHeight()
                            .padding(12.dp),
                        adaptiveMinCellSize = 180.dp
                    )
                }
            }
        }
    }
}

@Composable
fun CompactCartBar(state: OrderState, onIntent: (OrderIntent) -> Unit, modifier: Modifier = Modifier, onOpenCart: () -> Unit) {
    Row(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text("Корзина: ${state.cartItems.size} ", fontWeight = FontWeight.Medium)
            Text("Итого: ${state.finalAmount.formatPrice()}", color = Color.Gray, fontSize = 12.sp)
        }

        Row(modifier = Modifier.padding(end = 8.dp)) {
            OutlinedButton(onClick = { onOpenCart() }) { Text("Открыть") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onIntent(OrderIntent.Pay) }) { Text("Оплатить") }
        }
    }
}
