package com.sankranthivinod.tradetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.sankranthivinod.tradetracker.presentation.components.formatCurrency
import com.sankranthivinod.tradetracker.ui.theme.LossRed
import com.sankranthivinod.tradetracker.ui.theme.ProfitGreen

@Composable
fun AdminScreen(
    uiState: TradeUiState,
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var rulesEnabledState by remember(uiState.rulesEnabled) { mutableStateOf(uiState.rulesEnabled) }
    var maxLossTradeInput by remember(uiState.maxLossPerTrade) { mutableStateOf(uiState.maxLossPerTrade.toString()) }
    var maxLossDayInput by remember(uiState.maxLossPerDay) { mutableStateOf(uiState.maxLossPerDay.toString()) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Admin Status Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    modifier = Modifier
                        .height(44.dp)
                        .width(44.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Admin Risk Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.rulesEnabled) "Status: Rules Active & Enforced" else "Status: Rules Disabled (Default)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.rulesEnabled) ProfitGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Configuration Form Card
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
                    text = "Risk Limits & Rule Policy",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Toggle Rules Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Risk Rules",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Flag trades & days in RED when loss thresholds are violated.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = rulesEnabledState,
                        onCheckedChange = {
                            rulesEnabledState = it
                            saveSuccessMessage = null
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                AnimatedVisibility(visible = rulesEnabledState) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Max Loss Per Trade
                        OutlinedTextField(
                            value = maxLossTradeInput,
                            onValueChange = {
                                maxLossTradeInput = it
                                saveSuccessMessage = null
                                validationError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Max Loss Per Trade ($)") },
                            placeholder = { Text("e.g. 500.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Max Loss Per Day
                        OutlinedTextField(
                            value = maxLossDayInput,
                            onValueChange = {
                                maxLossDayInput = it
                                saveSuccessMessage = null
                                validationError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Max Loss Per Day ($)") },
                            placeholder = { Text("e.g. 1000.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (saveSuccessMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ProfitGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = saveSuccessMessage!!,
                            color = ProfitGreen,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Save Rules Button
                Button(
                    onClick = {
                        val tradeVal = maxLossTradeInput.toDoubleOrNull()
                        val dayVal = maxLossDayInput.toDoubleOrNull()

                        if (rulesEnabledState && (tradeVal == null || tradeVal <= 0)) {
                            validationError = "Please enter a valid positive amount for Max Loss Per Trade."
                            return@Button
                        }

                        if (rulesEnabledState && (dayVal == null || dayVal <= 0)) {
                            validationError = "Please enter a valid positive amount for Max Loss Per Day."
                            return@Button
                        }

                        viewModel.updateAdminRules(
                            enabled = rulesEnabledState,
                            maxLossTrade = tradeVal ?: uiState.maxLossPerTrade,
                            maxLossDay = dayVal ?: uiState.maxLossPerDay
                        )

                        validationError = null
                        saveSuccessMessage = "Rules updated successfully!"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Admin Rules",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Rule Explanation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "How Rule Violations Work",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "• Per-Trade Violation: Triggered when a trade loss exceeds ${formatCurrency(uiState.maxLossPerTrade)}.\n" +
                            "• Daily Violation: Triggered when the cumulative losses for a single date exceed ${formatCurrency(uiState.maxLossPerDay)}.\n" +
                            "• When rules are enabled, any violated trade or date is flagged in bold RED on the History & Dashboard screens.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
