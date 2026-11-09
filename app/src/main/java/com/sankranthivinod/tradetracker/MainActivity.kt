package com.sankranthivinod.tradetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sankranthivinod.tradetracker.presentation.TradeViewModel
import com.sankranthivinod.tradetracker.presentation.navigation.MainScreen
import com.sankranthivinod.tradetracker.ui.theme.TradeTrackerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TradeViewModel by viewModels {
        TradeViewModel.provideFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeTrackerTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
