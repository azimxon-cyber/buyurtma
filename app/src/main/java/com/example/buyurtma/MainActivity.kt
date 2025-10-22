package com.example.buyurtma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import com.example.buyurtma.components.OrderScreen
import com.example.buyurtma.mvi.OrderViewModel

class MainActivity : ComponentActivity() {
    private val vm: OrderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                OrderScreen(viewModel = vm)
            }
        }
    }
}
