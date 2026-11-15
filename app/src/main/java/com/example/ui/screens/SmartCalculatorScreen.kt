package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.DimensionUnit
import com.example.calculator.SmartQuoteLine
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.dialogs.SmartQuoteLineEditDialog
import com.example.util.Formatters
import com.example.viewmodel.SignageViewModel
import kotlin.math.max

private enum class SmartCalcMode(val label: String) {
    AREA_PRICE("Tính m² & Giá"),
    SHEET_CUT("Cắt Tấm 1.22x2.44"),
    ROLL_FRAME("Cuộn & Khung LED")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartCalculatorScreen(
    viewModel: SignageViewModel,
    onJumpToBreakdown: () -> Unit
) {
    val materialCatalog by viewModel.materialCatalog.collectAsState()
    val unitPrices by viewModel.unitPrices.collectAsState()
    val quoteLines by viewModel.smartQuoteLines.collectAsState()
    val discountPercent by viewModel.smartDiscountPercent.collectAsState()
    val vatPercent by viewModel.smartVatPercent.collectAsState()
    val quoteSummary by viewModel.smartQuoteSummary.collectAsState()
    val context = LocalContext.current

    var activeMode by remember { mutableStateOf(SmartCalcMode.AREA_PRICE) }

    val sheetMaterials = remember(materialCatalog) {
        materialCatalog.filter {
            it.unit.contains("Tấm", ignoreCase = true) ||
                it.category.contains("Alu", ignoreCase = true) ||
                it.category.contains("Mica", ignoreCase = true) ||
                it.category.contains("PVC", ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Banner
        Card(
            modifier = Modifier.fillMaxWidth().testTag("smart_calc_header"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Cyan900.copy(alpha = 0.5f), Slate900)
                        )
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Cyan400.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SquareFoot, contentDescription = null, tint = Cyan400, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bảng Tính Giá, m² & Vật Liệu Thông Minh",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Đổi đơn vị m/cm/mm • Bảng giá đa dòng • Sơ đồ cắt tấm 1.22x2.44m • Tối ưu cuộn & LED",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3-Mode Selector Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmartCalcMode.entries.forEach { mode ->
                val selected = activeMode == mode
                val icon = when (mode) {
                    SmartCalcMode.AREA_PRICE -> Icons.Default.Calculate
                    SmartCalcMode.SHEET_CUT -> Icons.Default.GridView
                    SmartCalcMode.ROLL_FRAME -> Icons.Default.Layers
                }
                Surface(
                    onClick = { activeMode = mode },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("smart_mode_${mode.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) Cyan900 else Slate900,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(if (selected) Cyan400 else Slate800)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mode.label,
                            tint = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        when (activeMode) {
            SmartCalcMode.AREA_PRICE -> {
                SmartAreaAndMultiPricePanel(
                    quoteLines = quoteLines,
                    discountPercent = discountPercent,
                    vatPercent = vatPercent,
                    summary = quoteSummary,
                    onAddLine = { viewModel.addSmartQuoteLine(it) },
                    onUpdateLine = { viewModel.updateSmartQuoteLine(it) },
                    onRemoveLine = { viewModel.removeSmartQuoteLine(it) },
                    onClearLines = { viewModel.clearSmartQuoteLines() },
                    onSetDiscount = { viewModel.setSmartDiscount(it) },
                    onSetVat = { viewModel.setSmartVat(it) },
                    onSaveToDossier = { viewModel.saveSmartLinesToDossier() },
                    onApplyToBreakdown = { title, w, h, q ->
                        viewModel.applyDimensionsFromSmartCalc(title, w, h, q)
                        onJumpToBreakdown()
                    },
                    onCopySummary = { copyText ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Bảng tính giá & m²", copyText))
                        viewModel.notifyMessage("Đã sao chép bảng tính giá & m² gửi Zalo!")
                    }
                )
            }
            SmartCalcMode.SHEET_CUT -> {
                SmartSheetCuttingPanel(
                    sheetMaterials = sheetMaterials,
                    unitPrices = unitPrices
                )
            }
            SmartCalcMode.ROLL_FRAME -> {
                SmartRollAndFramePanel(unitPrices = unitPrices)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SmartAreaAndMultiPricePanel(
    quoteLines: List<SmartQuoteLine>,
    discountPercent: Double,
    vatPercent: Double,
    summary: com.example.calculator.SmartQuoteSummary,
    onAddLine: (SmartQuoteLine) -> Unit,
    onUpdateLine: (SmartQuoteLine) -> Unit,
    onRemoveLine: (Long) -> Unit,
    onClearLines: () -> Unit,
    onSetDiscount: (Double) -> Unit,
    onSetVat: (Double) -> Unit,
    onSaveToDossier: () -> Unit,
    onApplyToBreakdown: (String, Double, Double, Int) -> Unit,
    onCopySummary: (String) -> Unit
) {
    var editingQuoteLine by remember { mutableStateOf<SmartQuoteLine?>(null) }
    var titleText by remember { mutableStateOf("Biển Bạt In UV") }
    var unit by remember { mutableStateOf(DimensionUnit.M) }
    var widthText by remember { mutableStateOf("3.5") }
    var heightText by remember { mutableStateOf("1.2") }
    var qtyText by remember { mutableStateOf("1") }
    var pricePerM2Text by remember { mutableStateOf("180000") }
    var extraFeeText by remember { mutableStateOf("0") }
    var minOneM2 by remember { mutableStateOf(true) }

    val rawW = widthText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val rawH = heightText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val widthM = rawW * unit.toMetersFactor
    val heightM = rawH * unit.toMetersFactor
    val qty = qtyText.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val pricePerM2 = pricePerM2Text.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val extraFee = extraFeeText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0

    val singleRawArea = widthM * heightM
    val billableSingle = if (minOneM2 && singleRawArea > 0.0) max(1.0, singleRawArea) else singleRawArea
    val totalArea = singleRawArea * qty
    val totalBillable = billableSingle * qty
    val perimeter = (widthM + heightM) * 2.0 * qty
    val instantTotal = (totalBillable * pricePerM2) + (extraFee * qty)

    val quickRatePresets = listOf(
        Triple("Bạt Hiflex 85k", "Biển Bạt Hiflex Khung Sắt", 85000.0),
        Triple("Bạt UV 180k", "Biển Bạt Không Gân In UV", 180000.0),
        Triple("Decal Cán 140k", "Decal Sữa Cán Màng", 140000.0),
        Triple("Alu Chữ 1.15tr", "Biển Alu Chữ Nổi", 1150000.0),
        Triple("Hộp Đèn 950k", "Hộp Đèn Mica LED", 950000.0)
    )

    // Input & Instant Result Card
    Card(
        modifier = Modifier.fillMaxWidth().testTag("smart_area_input_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Unit Switcher (m / cm / mm)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. Nhập Kích Thước & Đơn Giá",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DimensionUnit.entries.forEach { u ->
                        val selected = unit == u
                        Surface(
                            onClick = {
                                if (unit != u) {
                                    // Convert existing values cleanly when user switches unit!
                                    val factor = unit.toMetersFactor / u.toMetersFactor
                                    val nextW = rawW * factor
                                    val nextH = rawH * factor
                                    widthText = if (nextW > 0) formatCleanNum(nextW) else widthText
                                    heightText = if (nextH > 0) formatCleanNum(nextH) else heightText
                                    unit = u
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (selected) Cyan400 else Slate700)
                            ),
                            modifier = Modifier
                                .heightIn(min = 40.dp)
                                .testTag("unit_btn_${u.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = u.name.lowercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Quick Service Price Presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickRatePresets.forEach { (chipLabel, fullName, presetPrice) ->
                    Surface(
                        onClick = {
                            titleText = fullName
                            pricePerM2Text = presetPrice.toLong().toString()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Slate950,
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate700)),
                        modifier = Modifier.heightIn(min = 40.dp)
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(text = chipLabel, fontSize = 11.sp, color = Cyan400, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Tên hạng mục (VD: Biển chính, Băng rôn, Tem...)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("smart_item_title_input"),
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumericField(widthText, { widthText = it }, "Ngang (${unit.name.lowercase()})", Modifier.weight(1f).testTag("smart_width_input"))
                SmartNumericField(heightText, { heightText = it }, "Cao (${unit.name.lowercase()})", Modifier.weight(1f).testTag("smart_height_input"))
                SmartNumericField(qtyText, { qtyText = it }, "SL", Modifier.weight(0.65f).testTag("smart_qty_input"))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmartNumericField(pricePerM2Text, { pricePerM2Text = it }, "Đơn giá (đ/m²)", Modifier.weight(1f).testTag("smart_unit_price_input"))
                SmartNumericField(extraFeeText, { extraFeeText = it }, "Phụ phí/tấm (đ)", Modifier.weight(1f).testTag("smart_extra_fee_input"))
            }

            // Min 1m2 toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate950)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Làm tròn tối thiểu 1m² / tấm",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Quy chuẩn xưởng cho tem/biển nhỏ dưới 1m²",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = minOneM2,
                    onCheckedChange = { minOneM2 = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Cyan400),
                    modifier = Modifier.testTag("smart_min_1m2_switch")
                )
            }

            // Realtime Instant Readout
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                MetricBadge("Diện tích thực", "${String.format("%.2f", totalArea)} m²", Cyan400, Modifier.weight(1f))
                MetricBadge("m² tính tiền", "${String.format("%.2f", totalBillable)} m²", Purple400, Modifier.weight(1f))
                MetricBadge("Chu vi viền/khung", "${String.format("%.2f", perimeter)} mét", Amber400, Modifier.weight(1f))
                MetricBadge("Thành tiền dòng này", Formatters.formatVnd(instantTotal), Emerald400, Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (widthM > 0.0 && heightM > 0.0) {
                            onAddLine(
                                SmartQuoteLine(
                                    title = titleText.ifBlank { "Hạng mục quảng cáo" },
                                    widthMeters = widthM,
                                    heightMeters = heightM,
                                    rawDisplaySize = "${formatCleanNum(rawW)}x${formatCleanNum(rawH)} ${unit.name.lowercase()}",
                                    quantity = qty,
                                    unitPricePerM2 = pricePerM2,
                                    minOneM2PerPiece = minOneM2,
                                    extraProcessingFeePerPiece = extraFee
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("smart_add_line_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Thêm Vào Bảng Giá", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        if (widthM > 0.0 && heightM > 0.0) {
                            onApplyToBreakdown(titleText, widthM, heightM, qty)
                        }
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("smart_send_to_breakdown_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bóc Tách", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                }
            }
        }
    }

    // Multi-line Quote Table Card
    Card(
        modifier = Modifier.fillMaxWidth().testTag("smart_quote_table_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400.copy(alpha = 0.45f)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Bảng Tổng Hợp Giá & m² (${quoteLines.size} dòng)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (quoteLines.isNotEmpty()) {
                    IconButton(
                        onClick = onClearLines,
                        modifier = Modifier.size(48.dp).testTag("smart_clear_table_btn")
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Xóa bảng tính", tint = Amber400)
                    }
                }
            }

            if (quoteLines.isEmpty()) {
                Text(
                    text = "Chưa có dòng nào trong bảng tính. Hãy nhập kích thước phía trên và bấm 'Thêm Vào Bảng Giá' để cộng dồn nhiều kích thước cùng lúc.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            } else {
                quoteLines.forEachIndexed { idx, line ->
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "${idx + 1}. ${line.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${line.rawDisplaySize} x ${line.quantity} • ${String.format("%.2f", line.totalBillableArea)} m² x ${Formatters.formatVnd(line.unitPricePerM2)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = Formatters.formatVnd(line.lineTotal),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400
                            )
                            IconButton(
                                onClick = { editingQuoteLine = line },
                                modifier = Modifier.size(48.dp).testTag("edit_smart_line_${line.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Chỉnh sửa dòng", tint = Cyan400, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onRemoveLine(line.id) },
                                modifier = Modifier.size(48.dp).testTag("delete_smart_line_${line.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa dòng", tint = Amber400, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Discount & VAT Quick Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Chiết khấu:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(0.0, 5.0, 10.0, 15.0).forEach { disc ->
                        val sel = discountPercent == disc
                        Surface(
                            onClick = { onSetDiscount(disc) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (sel) Cyan400 else Slate700)),
                            modifier = Modifier.heightIn(min = 38.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(text = "${disc.toInt()}%", fontSize = 11.sp, color = if (sel) Cyan400 else Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Thuế VAT:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(0.0, 8.0, 10.0).forEach { vat ->
                        val sel = vatPercent == vat
                        Surface(
                            onClick = { onSetVat(vat) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (sel) Cyan400 else Slate700)),
                            modifier = Modifier.heightIn(min = 38.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
                                Text(text = "${vat.toInt()}%", fontSize = 11.sp, color = if (sel) Cyan400 else Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = Slate800)

                // Summary totals
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng diện tích (${summary.totalQuantity} tấm):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.2f", summary.totalBillableArea)} m² (Thực ${String.format("%.2f", summary.totalRawArea)} m²)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng chu vi viền:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.2f", summary.totalPerimeter)} mét", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
                if (summary.discountAmount > 0.0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Chiết khấu (${discountPercent.toInt()}%):", fontSize = 12.sp, color = Amber400)
                        Text("-${Formatters.formatVnd(summary.discountAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Amber400)
                    }
                }
                if (summary.vatAmount > 0.0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("VAT (${vatPercent.toInt()}%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("+${Formatters.formatVnd(summary.vatAmount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TỔNG THANH TOÁN:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(Formatters.formatVnd(summary.grandTotal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Emerald400)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSaveToDossier,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("smart_save_dossier_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu Dự Toán", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val text = buildSmartQuoteShareText(quoteLines, discountPercent, vatPercent, summary)
                            onCopySummary(text)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("smart_copy_quote_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chép Báo Giá", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                    }
                }
            }
        }
    }

    editingQuoteLine?.let { target ->
        SmartQuoteLineEditDialog(
            line = target,
            onSave = { updated ->
                onUpdateLine(updated)
                editingQuoteLine = null
            },
            onDismiss = { editingQuoteLine = null }
        )
    }
}

private fun formatCleanNum(value: Double): String {
    val rounded = kotlin.math.round(value * 100.0) / 100.0
    return if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString()
}

private fun buildSmartQuoteShareText(
    lines: List<SmartQuoteLine>,
    discountPercent: Double,
    vatPercent: Double,
    summary: com.example.calculator.SmartQuoteSummary
): String = buildString {
    appendLine("📋 BẢNG TÍNH BÁO GIÁ & m² - ĐỨC ANH AD PRO")
    appendLine("----------------------------------------")
    lines.forEachIndexed { i, l ->
        appendLine("${i + 1}. ${l.title}")
        appendLine("   KT: ${l.rawDisplaySize} | SL: ${l.quantity} | m² tính: ${String.format("%.2f", l.totalBillableArea)} m²")
        appendLine("   Đơn giá: ${Formatters.formatVnd(l.unitPricePerM2)}/m² -> ${Formatters.formatVnd(l.lineTotal)}")
    }
    appendLine("----------------------------------------")
    appendLine("Tổng SL: ${summary.totalQuantity} | Tổng m²: ${String.format("%.2f", summary.totalBillableArea)} m²")
    if (summary.discountAmount > 0.0) {
        appendLine("Chiết khấu (${discountPercent.toInt()}%): -${Formatters.formatVnd(summary.discountAmount)}")
    }
    if (summary.vatAmount > 0.0) {
        appendLine("VAT (${vatPercent.toInt()}%): +${Formatters.formatVnd(summary.vatAmount)}")
    }
    appendLine("👉 TỔNG CỘNG: ${Formatters.formatVnd(summary.grandTotal)}")
}
