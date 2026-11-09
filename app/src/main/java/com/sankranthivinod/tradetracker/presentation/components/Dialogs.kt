package com.sankranthivinod.tradetracker.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "About Trade Tracker")
        },
        text = {
            Text(
                text = "Trade Tracker v1.0\n\n" +
                        "A native Android trade tracking application designed for traders to record and evaluate their trading performance across Stocks, Options, Futures, and Crypto.\n\n" +
                        "🔒 Local-First Privacy: All your trade data is stored strictly on your device in a local Room database. No data is uploaded to external servers."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ClearConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Clear All Trades?")
        },
        text = {
            Text(text = "Are you sure you want to permanently delete all trade records? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Clear All",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
