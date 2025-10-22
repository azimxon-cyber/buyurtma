package com.example.buyurtma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buyurtma.mvi.OrderEffect
import com.example.buyurtma.mvi.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(viewModel: OrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrderEffect.OrderSent -> snackbarHostState.showSnackbar("Заказ отправлен на кухню", duration = SnackbarDuration.Short)
                is OrderEffect.PaymentCompleted -> snackbarHostState.showSnackbar("Оплата прошла успешно", duration = SnackbarDuration.Short)
                is OrderEffect.ReceiptPrinted -> snackbarHostState.showSnackbar("Чек распечатан", duration = SnackbarDuration.Short)
                is OrderEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
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
                    .padding(12.dp)
            )
        }
    }
}