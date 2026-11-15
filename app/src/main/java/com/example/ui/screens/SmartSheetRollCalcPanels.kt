package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.SmartMaterialCalculator
import com.example.model.MaterialItem
import com.example.ui.components.SheetCutPreviewCanvas
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmartSheetCuttingPanel(
    sheetMaterials: List<MaterialItem>,
    unitPrices: Map<String, Double>
) {
    var pieceWText by remember { mutableStateOf("600") }
    var pieceHText by remember { mutableStateOf("400") }
    var qtyText by remember { mutableStateOf("18") }
    var kerfText by remember { mutableStateOf("4") }
    var selectedSheetMat by remember(sheetMaterials) {
        mutableStateOf(sheetMaterials.firstOrNull())
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val sheetPrice = selectedSheetMat?.let { unitPrices[it.priceKey] ?: it.price } ?: 360000.0
    val pieceW = pieceWText.toDoubleOrNull()?.coerceAtLeast(10.0) ?: 600.0
    val pieceH = pieceHText.toDoubleOrNull()?.coerceAtLeast(10.0) ?: 400.0
    val qty = qtyText.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val kerf = kerfText.toDoubleOrNull()?.coerceIn(0.0, 20.0) ?: 4.0

    val result = remember(pieceW, pieceH, qty, kerf, sheetPrice) {
        SmartMaterialCalculator.optimizeSheetCutting(
            pieceWidthMm = pieceW,
            pieceHeightMm = pieceH,
            quantity = qty,
            sheetWidthMm = 1220.0,
            sheetHeightMm = 2440.0,
            kerfMm = kerf,
            sheetPrice = sheetPrice
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_sheet_input_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tối Ưu Cắt Tấm Chuẩn 1.22m x 2.44m",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Material Selector
                if (sheetMaterials.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSheetMat?.let { "${it.brand} (${Formatters.formatVnd(sheetPrice)}/tấm)" } ?: "Chọn tấm phôi",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Chọn Phôi Tấm (Alu / Mica / Formex)", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .testTag("sheet_material_dropdown"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            sheetMaterials.forEach { mat ->
                                val price = unitPrices[mat.priceKey] ?: mat.price
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${mat.brand} — ${Formatters.formatVnd(price)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedSheetMat = mat
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Quick Piece Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("600x400", "600", "400"),
                        Triple("800x600", "800", "600"),
                        Triple("400x300", "400", "300"),
                        Triple("1200x600", "1200", "600")
                    ).forEach { (label, w, h) ->
                        Surface(
                            onClick = {
                                pieceWText = w
                                pieceHText = h
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate700)),
                            modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$label mm", fontSize = 11.sp, color = Cyan400, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmartNumericField(
                        value = pieceWText,
                        onValueChange = { pieceWText = it },
                        label = "Rộng mảnh (mm)",
                        modifier = Modifier.weight(1f).testTag("sheet_piece_w_input")
                    )
                    SmartNumericField(
                        value = pieceHText,
                        onValueChange = { pieceHText = it },
                        label = "Cao mảnh (mm)",
                        modifier = Modifier.weight(1f).testTag("sheet_piece_h_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmartNumericField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = "Số lượng mảnh",
                        modifier = Modifier.weight(1f).testTag("sheet_qty_input")
                    )
                    SmartNumericField(
                        value = kerfText,
                        onValueChange = { kerfText = it },
                        label = "Mạch cắt/dao (mm)",
                        modifier = Modifier.weight(1f).testTag("sheet_kerf_input")
                    )
                }
            }
        }

        // Results Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_sheet_result_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400.copy(alpha = 0.5f)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Phương án tối ưu: ${result.layoutStrategyName}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )

                if (result.warningMessage.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Amber400.copy(alpha = 0.12f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, tint = Amber400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = result.warningMessage, fontSize = 12.sp, color = Amber400)
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    MetricBadge("Số Tấm Cần Nhập", "${result.sheetsNeeded} Tấm", Emerald400, Modifier.weight(1f))
                    MetricBadge("Mảnh / 1 Tấm", "${result.piecesPerSheet} mảnh", Cyan400, Modifier.weight(1f))
                    MetricBadge("Chi Phí Phôi Tấm", Formatters.formatVnd(result.totalSheetCost), Amber400, Modifier.weight(1f))
                    MetricBadge("Đề-xê Dư Tổng", "${String.format("%.2f", result.wasteAreaM2Total)} m²", Purple400, Modifier.weight(1f))
                }

                Text(
                    text = "So sánh xếp nhanh: Đặt thẳng ${result.normalCountPerSheet} mảnh/tấm • Xoay 90° ${result.rotatedCountPerSheet} mảnh/tấm • Tối ưu ${result.piecesPerSheet} mảnh/tấm.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Visual Canvas Diagram
        SheetCutPreviewCanvas(result = result)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartRollAndFramePanel(
    unitPrices: Map<String, Double>
) {
    var widthMText by remember { mutableStateOf("4.0") }
    var heightMText by remember { mutableStateOf("1.2") }
    var qtyText by remember { mutableStateOf("1") }
    var rollPriceText by remember { mutableStateOf("40000") }
    var ledCountText by remember { mutableStateOf("180") }
    var gridSpacingM by remember { mutableStateOf(0.6) }

    val wM = widthMText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 4.0
    val hM = heightMText.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.2
    val qty = qtyText.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val rollPrice = rollPriceText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 40000.0
    val ledCount = ledCountText.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val steelPrice = unitPrices["steel_vietthanh_30"] ?: 24000.0

    val rollWidths = listOf(1.27, 1.52, 2.2, 2.5, 3.2)
    val rollEvaluations = remember(wM, hM, qty, rollPrice) {
        rollWidths.map { rw ->
            SmartMaterialCalculator.optimizeRollUsage(wM, hM, qty, rw, rollPrice)
        }
    }
    val bestRoll = rollEvaluations.filterNot { it.needsTiling }.minByOrNull { it.wastePercent }
        ?: rollEvaluations.minByOrNull { it.totalUsedAreaM2 }

    val frameLedResult = remember(wM, hM, gridSpacingM, ledCount, steelPrice) {
        SmartMaterialCalculator.calculateFrameAndLed(
            widthM = wM,
            heightM = hM,
            gridSpacingM = gridSpacingM,
            ledCount = ledCount,
            ledWattPerUnit = 1.2,
            steelPricePerMeter = steelPrice
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Dimensions & Roll Price Input Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_roll_frame_input_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thông Số Biển / File In & Khung LED",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmartNumericField(widthMText, { widthMText = it }, "Chiều dài (m)", Modifier.weight(1f).testTag("roll_w_input"))
                    SmartNumericField(heightMText, { heightMText = it }, "Chiều cao (m)", Modifier.weight(1f).testTag("roll_h_input"))
                    SmartNumericField(qtyText, { qtyText = it }, "Số lượng", Modifier.weight(0.8f).testTag("roll_qty_input"))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmartNumericField(rollPriceText, { rollPriceText = it }, "Giá cuộn (đ/m²)", Modifier.weight(1f).testTag("roll_price_input"))
                    SmartNumericField(ledCountText, { ledCountText = it }, "Số bóng LED 1.2W", Modifier.weight(1f).testTag("roll_led_input"))
                }

                // Grid Spacing Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đan nan sắt:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(0.6 to "60x60cm (Alu)", 1.0 to "1.0m (Bạt lớn)", 1.2 to "1.2m (Chuẩn)").forEach { (sp, label) ->
                        val selected = gridSpacingM == sp
                        Surface(
                            onClick = { gridSpacingM = sp },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (selected) Cyan400 else Slate700)),
                            modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Roll Optimization Table
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_roll_comparison_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.45f)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "So Sánh Xếp Khổ Cuộn Bạt / Decal (Tự Động Xoay 90°)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                rollEvaluations.forEach { eval ->
                    val isBest = bestRoll?.rollWidthM == eval.rollWidthM
                    Surface(
                        color = if (isBest) Emerald400.copy(alpha = 0.12f) else Slate950,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (isBest) Emerald400 else Slate800)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Khổ ${String.format("%.2f", eval.rollWidthM)}m" + if (isBest) " ⭐ TỐT NHẤT" else "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBest) Emerald400 else Color.White
                                )
                                Text(
                                    text = "Chạy ${String.format("%.2f", eval.linearMetersNeeded)}m dài • Hao ${String.format("%.1f", eval.wastePercent)}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (eval.needsTiling) Amber400 else Cyan400
                                )
                            }
                            Text(
                                text = "${eval.orientationLabel} • Tốn ${String.format("%.2f", eval.totalUsedAreaM2)} m² (${Formatters.formatVnd(eval.estimatedCost)})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Steel 6m Bars + Keo + LED Power 80% Calculator Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_frame_led_result_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Amber400.copy(alpha = 0.45f)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Amber400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tính Cây Sắt 6m, Keo & Nguồn LED An Toàn 80%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    MetricBadge("Cây Sắt 6m (Gồm 5% hao)", "${frameLedResult.steelBars6m} cây (${String.format("%.1f", frameLedResult.totalSteelMeters)}m)", Cyan400, Modifier.weight(1f))
                    MetricBadge("Keo Titebond + Silicon", "${frameLedResult.titebondTubes} Titebond + ${frameLedResult.siliconeTubes} A500", Emerald400, Modifier.weight(1f))
                    MetricBadge("Nguồn 12V 33A (Hệ số 80%)", "${frameLedResult.recommendedPower12V33ACount} cái (${String.format("%.0f", frameLedResult.ledTotalWattsWithSafety)}W)", Amber400, Modifier.weight(1f))
                    MetricBadge("Dây Trục Cadivi 2x1.5", "${String.format("%.1f", frameLedResult.cadiviWireMeters)} mét", Purple400, Modifier.weight(1f))
                }

                Text(
                    text = "Tổng vật tư khung + keo + nguồn dây ước tính: ${Formatters.formatVnd(frameLedResult.estimatedHardwareCost)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Amber400
                )
            }
        }
    }
}

@Composable
fun MetricBadge(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Slate950,
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(accentColor.copy(alpha = 0.35f)))
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun SmartNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val sanitized = raw.replace(',', '.')
            if (sanitized.isEmpty() || sanitized.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(sanitized)
            }
        },
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan400,
            unfocusedBorderColor = Slate700,
            focusedContainerColor = Slate950,
            unfocusedContainerColor = Slate950,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    )
}
