package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.SheetOptimizationResult
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate950

@Composable
fun SheetCutPreviewCanvas(
    result: SheetOptimizationResult,
    modifier: Modifier = Modifier
) {
    val ratio = (result.sheetHeightMm / result.sheetWidthMm).toFloat().coerceIn(0.4f, 3.0f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sheet_cut_preview_canvas"),
        shape = RoundedCornerShape(14.dp),
        color = Slate950
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sơ Đồ Chia Tấm Phôi (${result.sheetWidthMm.toInt()} x ${result.sheetHeightMm.toInt()} mm)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Hiệu suất ${String.format("%.1f", result.efficiencyPercent)}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (result.efficiencyPercent >= 75.0) Emerald400 else Amber400
                )
            }

            // Landscape orientation wrapper for compact screen preview (2440 horizontal x 1220 vertical)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate800.copy(alpha = 0.45f))
                    .padding(6.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio.coerceAtLeast(1.3f))
                ) {
                    val sheetDrawW = size.width
                    val sheetDrawH = size.height

                    // Draw outer sheet border (drawn horizontally: sheetHeightMm as X, sheetWidthMm as Y for mobile friendliness)
                    drawRect(
                        color = Slate700,
                        topLeft = Offset.Zero,
                        size = Size(sheetDrawW, sheetDrawH),
                        style = Stroke(width = 3f)
                    )

                    if (result.sampleSheetPieces.isEmpty()) {
                        drawRect(
                            color = Amber400.copy(alpha = 0.25f),
                            topLeft = Offset(4f, 4f),
                            size = Size((sheetDrawW - 8f).coerceAtLeast(1f), (sheetDrawH - 8f).coerceAtLeast(1f))
                        )
                    } else {
                        val scaleX = sheetDrawW / result.sheetHeightMm.toFloat()
                        val scaleY = sheetDrawH / result.sheetWidthMm.toFloat()

                        result.sampleSheetPieces.forEach { piece ->
                            // Map sheet Y (along 2440mm) to horizontal canvas X, and sheet X (along 1220mm) to vertical canvas Y
                            val left = piece.yMm.toFloat() * scaleX
                            val top = piece.xMm.toFloat() * scaleY
                            val rectW = piece.hMm.toFloat() * scaleX
                            val rectH = piece.wMm.toFloat() * scaleY

                            val fillColor = if (piece.rotated) {
                                Emerald400.copy(alpha = 0.32f)
                            } else {
                                Cyan400.copy(alpha = 0.30f)
                            }
                            val strokeColor = if (piece.rotated) Emerald400 else Cyan400

                            drawRect(
                                color = fillColor,
                                topLeft = Offset(left, top),
                                size = Size(rectW.coerceAtLeast(1f), rectH.coerceAtLeast(1f))
                            )
                            drawRect(
                                color = strokeColor,
                                topLeft = Offset(left, top),
                                size = Size(rectW.coerceAtLeast(1f), rectH.coerceAtLeast(1f)),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendChip(color = Cyan400, label = "Xếp thẳng")
                LegendChip(color = Emerald400, label = "Xoay 90°")
                LegendChip(color = Slate700, label = "Đề-xê dư")
            }
        }
    }
}

@Composable
private fun LegendChip(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
