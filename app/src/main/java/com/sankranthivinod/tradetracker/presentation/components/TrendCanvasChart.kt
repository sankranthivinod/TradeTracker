package com.sankranthivinod.tradetracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sankranthivinod.tradetracker.presentation.CumulativePoint
import com.sankranthivinod.tradetracker.ui.theme.BluePrimary
import com.sankranthivinod.tradetracker.ui.theme.LossRed
import com.sankranthivinod.tradetracker.ui.theme.ProfitGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun TrendCanvasChart(
    points: List<CumulativePoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No trade data available for selected filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var selectedPointIndex by remember(points) { mutableStateOf<Int?>(null) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    // Pre-calculate min/max values including 0 baseline reference
    val maxPnL = max(0.0, points.maxOf { it.cumulativePnL }) * 1.15
    val minPnL = min(0.0, points.minOf { it.cumulativePnL }) * 1.15
    val rangePnL = if (maxPnL == minPnL) 1.0 else maxPnL - minPnL

    val lineColor = if ((points.lastOrNull()?.cumulativePnL ?: 0.0) >= 0) ProfitGreen else LossRed

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cumulative P&L Trend",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${points.size} entries",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val paddingLeft = 40f
                                val paddingRight = 40f
                                val chartWidth = width - paddingLeft - paddingRight

                                if (points.size == 1) {
                                    selectedPointIndex = 0
                                    return@detectTapGestures
                                }

                                val stepX = chartWidth / (points.size - 1)
                                var closestIdx = 0
                                var minDistance = Float.MAX_VALUE

                                points.forEachIndexed { index, _ ->
                                    val pointX = paddingLeft + index * stepX
                                    val distance = kotlin.math.abs(tapOffset.x - pointX)
                                    if (distance < minDistance) {
                                        minDistance = distance
                                        closestIdx = index
                                    }
                                }
                                selectedPointIndex = closestIdx
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val paddingTop = 20f
                    val paddingBottom = 40f
                    val paddingLeft = 40f
                    val paddingRight = 40f

                    val chartWidth = width - paddingLeft - paddingRight
                    val chartHeight = height - paddingTop - paddingBottom

                    // Convert cumulative P&L to Y coordinate
                    fun getY(value: Double): Float {
                        val norm = (maxPnL - value) / rangePnL
                        return paddingTop + (norm * chartHeight).toFloat()
                    }

                    // Zero baseline Y
                    val zeroY = getY(0.0)

                    // Draw Zero Baseline (Dashed Line)
                    drawPath(
                        path = Path().apply {
                            moveTo(paddingLeft, zeroY)
                            lineTo(width - paddingRight, zeroY)
                        },
                        color = Color.Gray.copy(alpha = 0.5f),
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    // Compute points coordinates
                    val pointOffsets = mutableListOf<Offset>()
                    val stepX = if (points.size > 1) chartWidth / (points.size - 1) else 0f

                    points.forEachIndexed { index, point ->
                        val x = if (points.size > 1) paddingLeft + index * stepX else width / 2f
                        val y = getY(point.cumulativePnL)
                        pointOffsets.add(Offset(x, y))
                    }

                    // Draw Gradient Area under/above line
                    if (pointOffsets.isNotEmpty()) {
                        val fillPath = Path().apply {
                            moveTo(pointOffsets.first().x, zeroY)
                            pointOffsets.forEach { offset ->
                                lineTo(offset.x, offset.y)
                            }
                            lineTo(pointOffsets.last().x, zeroY)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    lineColor.copy(alpha = 0.35f),
                                    lineColor.copy(alpha = 0.05f)
                                )
                            )
                        )

                        // Draw P&L trend line path
                        val linePath = Path().apply {
                            moveTo(pointOffsets.first().x, pointOffsets.first().y)
                            for (i in 1 until pointOffsets.size) {
                                lineTo(pointOffsets[i].x, pointOffsets[i].y)
                            }
                        }

                        drawPath(
                            path = linePath,
                            color = lineColor,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw data point dots
                        pointOffsets.forEachIndexed { idx, offset ->
                            val isSelected = selectedPointIndex == idx
                            drawCircle(
                                color = if (isSelected) BluePrimary else lineColor,
                                radius = if (isSelected) 8.dp.toPx() else 4.dp.toPx(),
                                center = offset
                            )
                            if (isSelected) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 4.dp.toPx(),
                                    center = offset
                                )
                            }
                        }
                    }
                }

                // Tooltip display on tap
                selectedPointIndex?.let { index ->
                    if (index in points.indices) {
                        val pt = points[index]
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.inverseSurface,
                            tonalElevation = 4.dp
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${pt.ticker} (${pt.instrumentType}) • ${dateFormat.format(Date(pt.timestamp))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.inverseOnSurface
                                )
                                Row {
                                    Text(
                                        text = "Trade: ${formatCurrency(pt.tradeAmount)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (pt.tradeAmount >= 0) ProfitGreen else LossRed
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Cumulative: ${formatCurrency(pt.cumulativePnL)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.inverseOnSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap on chart points to view trade details",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
