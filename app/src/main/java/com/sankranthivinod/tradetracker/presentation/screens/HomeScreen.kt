package com.sankranthivinod.tradetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sankranthivinod.tradetracker.presentation.TradeUiState
import com.sankranthivinod.tradetracker.presentation.TradeViewModel
import com.sankranthivinod.tradetracker.presentation.components.SummaryHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val instrumentTypes = remember { listOf("Options", "Stocks", "Futures", "Crypto") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Summary Header
        SummaryHeader(uiState = uiState)

        // Trade Entry Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Log New Trade",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Instrument Type Selector & Option Toggle
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Instrument Type",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        instrumentTypes.forEach { type ->
                            FilterChip(
                                selected = uiState.instrumentTypeInput == type,
                                onClick = { viewModel.onInstrumentTypeChanged(type) },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                // Conditional Sub-selector for Options (Call vs Put)
                AnimatedVisibility(visible = uiState.instrumentTypeInput == "Options") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Option Type",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = uiState.optionTypeInput == "Call",
                                onClick = { viewModel.onOptionTypeChanged("Call") },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) {
                                Text("Call")
                            }
                            SegmentedButton(
                                selected = uiState.optionTypeInput == "Put",
                                onClick = { viewModel.onOptionTypeChanged("Put") },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) {
                                Text("Put")
                            }
                        }
                    }
                }

                // Trade Amount Input Field
                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = { viewModel.onAmountInputChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Trade P&L Amount ($)") },
                    placeholder = { Text("e.g. 320.00 or -150.50") },
                    helperTextOrError = {
                        if (uiState.amountError != null) {
                            Text(
                                text = uiState.amountError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Text(
                                text = "Enter positive for profit, negative (-) for loss",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    isError = uiState.amountError != null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                // Ticker / Note Field
                OutlinedTextField(
                    value = uiState.tickerInput,
                    onValueChange = { viewModel.onTickerInputChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ticker / Note (Optional)") },
                    placeholder = { Text("e.g. SPY, TSLA, BTC") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    singleLine = true
                )

                // Save Trade Button
                Button(
                    onClick = { viewModel.saveTrade() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Trade Entry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    helperTextOrError: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = label,
            placeholder = placeholder,
            isError = isError,
            leadingIcon = leadingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp)
        )
        if (helperTextOrError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            helperTextOrError()
        }
    }
}
