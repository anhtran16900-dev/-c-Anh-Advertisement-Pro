package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BoardDesignFamily
import com.example.model.BoardDesignSpec
import com.example.model.DecalBackingType
import com.example.model.DecalLaminationType
import com.example.model.DecalProductType
import com.example.model.TarpFinishMode
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate950
import java.util.Locale
import kotlin.math.ceil

@Composable
fun SignageBoardPreviewCanvas(
    spec: BoardDesignSpec,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(spec.colorTheme.bgHexLong)
    val textColor = Color(spec.colorTheme.textHexLong)
    val accentColor = Color(spec.colorTheme.accentHexLong)
    val aspect = (spec.widthMeters / spec.heightMeters.coerceAtLeast(0.1)).toFloat().coerceIn(0.6f, 4.8f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate950)
            .padding(12.dp)
            .testTag("board_live_preview"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top width ruler label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MÔ PHỎNG THIẾT KẾ TỶ LỆ THỰC",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Cyan400
            )
            Surface(color = Slate800, shape = RoundedCornerShape(6.dp)) {
                Text(
                    text = "Ngang ${String.format(Locale.US, "%.2f", spec.widthMeters)}m × Cao ${String.format(Locale.US, "%.2f", spec.heightMeters)}m",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Amber400,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF070B14)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                val maxW = size.width
                val maxH = size.height
                var boardW = maxW
                var boardH = boardW / aspect
                if (boardH > maxH) {
                    boardH = maxH
                    boardW = boardH * aspect
                }
                val left = (maxW - boardW) / 2f
                val top = (maxH - boardH) / 2f

                // Outer glow for LED lightbox / backlit Alu
                val hasGlow = when (spec.family) {
                    BoardDesignFamily.TARP_BANNER -> spec.tarpFinish.needsLighting
                    BoardDesignFamily.ALU_3D_LETTER -> spec.letterLighting.hasLed
                    BoardDesignFamily.DECAL_STICKER -> false
                }
                if (hasGlow) {
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.22f),
                        topLeft = Offset(left - 6f, top - 6f),
                        size = Size(boardW + 12f, boardH + 12f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }

                // Clear glass / frosted texture for Decal if applicable
                if (spec.family == BoardDesignFamily.DECAL_STICKER &&
                    (spec.decalProduct == DecalProductType.DECAL_CLEAR || spec.decalProduct == DecalProductType.DECAL_FROSTED_GLASS)
                ) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF334155).copy(alpha = 0.55f), Color(0xFF1E293B).copy(alpha = 0.35f))
                        ),
                        topLeft = Offset(left, top),
                        size = Size(boardW, boardH)
                    )
                } else {
                    drawRect(
                        color = bgColor,
                        topLeft = Offset(left, top),
                        size = Size(boardW, boardH)
                    )
                }

                // Family-specific architectural details
                when (spec.family) {
                    BoardDesignFamily.TARP_BANNER -> {
                        if (spec.tarpFinish.needsIronFrame) {
                            val frameStroke = Stroke(width = 4f)
                            drawRect(color = Color(0xFF94A3B8), topLeft = Offset(left, top), size = Size(boardW, boardH), style = frameStroke)
                        }
                        if (spec.tarpFinish == TarpFinishMode.GROMMET_OR_POLE) {
                            val dotsX = ceil(spec.widthMeters / (spec.tarpGrommetSpacingCm / 100.0)).toInt().coerceIn(3, 12)
                            for (i in 0 until dotsX) {
                                val x = left + 8f + (boardW - 16f) * (i.toFloat() / (dotsX - 1).coerceAtLeast(1))
                                drawCircle(color = Amber400, radius = 3.2f, center = Offset(x, top + 7f))
                                drawCircle(color = Amber400, radius = 3.2f, center = Offset(x, top + boardH - 7f))
                            }
                        }
                        if (spec.tarpFinish == TarpFinishMode.FRAME_WITH_FLOODLIGHT) {
                            val lamps = ceil(spec.widthMeters / 1.5).toInt().coerceIn(1, 5)
                            for (i in 0 until lamps) {
                                val lx = left + boardW * ((i + 0.5f) / lamps)
                                drawLine(color = Color(0xFFCBD5E1), start = Offset(lx, top - 10f), end = Offset(lx, top), strokeWidth = 2.5f)
                                drawCircle(color = Color(0xFFFEF08A), radius = 4f, center = Offset(lx, top - 10f))
                            }
                        }
                    }

                    BoardDesignFamily.ALU_3D_LETTER -> {
                        // Draw 1.22m x 2.44m Alu sheet seam joints!
                        val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                        val cols = ceil(spec.widthMeters / 1.22).toInt().coerceAtLeast(1)
                        for (c in 1 until cols) {
                            val gx = left + ((c * 1.22f) / spec.widthMeters.toFloat()) * boardW
                            if (gx < left + boardW - 2f) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.25f),
                                    start = Offset(gx, top),
                                    end = Offset(gx, top + boardH),
                                    strokeWidth = 1.2f,
                                    pathEffect = dash
                                )
                            }
                        }
                        drawRect(color = accentColor.copy(alpha = 0.7f), topLeft = Offset(left, top), size = Size(boardW, boardH), style = Stroke(width = 3f))
                    }

                    BoardDesignFamily.DECAL_STICKER -> {
                        val bleedDash = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                        drawRect(
                            color = accentColor.copy(alpha = 0.75f),
                            topLeft = Offset(left + 4f, top + 4f),
                            size = Size((boardW - 8f).coerceAtLeast(4f), (boardH - 8f).coerceAtLeast(4f)),
                            style = Stroke(width = 1.5f, pathEffect = bleedDash)
                        )
                        if (spec.decalLamination == DecalLaminationType.GLOSSY) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.16f),
                                start = Offset(left + boardW * 0.15f, top),
                                end = Offset(left + boardW * 0.45f, top + boardH),
                                strokeWidth = 14f
                            )
                        }
                        if (spec.decalBacking != DecalBackingType.ROLL_ONLY) {
                            drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(left, top), size = Size(boardW, boardH), style = Stroke(width = 2.5f))
                        }
                    }
                }
            }

            // Crisp typography overlay for Brand + Slogan
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val shadow = if (spec.family == BoardDesignFamily.ALU_3D_LETTER) {
                    Shadow(
                        color = if (spec.letterLighting.hasLed) accentColor else Color.Black,
                        offset = Offset(2f, 3f),
                        blurRadius = if (spec.letterLighting.hasLed) 12f else 4f
                    )
                } else {
                    Shadow(color = Color.Black.copy(alpha = 0.5f), offset = Offset(1f, 2f), blurRadius = 3f)
                }

                Text(
                    text = spec.brandText.ifBlank { "TÊN THƯƠNG HIỆU" },
                    style = TextStyle(
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp,
                        shadow = shadow
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (spec.sloganText.isNotBlank()) {
                    Text(
                        text = spec.sloganText,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}
