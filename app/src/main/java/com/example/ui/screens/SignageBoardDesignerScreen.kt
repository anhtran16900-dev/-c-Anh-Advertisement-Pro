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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.SignageBoardDesignerCalculator
import com.example.model.AluPanelType
import com.example.model.BoardColorTheme
import com.example.model.BoardDesignFamily
import com.example.model.BoardDesignSpec
import com.example.model.DecalBackingType
import com.example.model.DecalLaminationType
import com.example.model.DecalProductType
import com.example.model.Letter3dType
import com.example.model.LetterLightingStyle
import com.example.model.ProjectDossierItem
import com.example.model.TarpFinishMode
import com.example.model.TarpProductType
import com.example.ui.components.SignageBoardPreviewCanvas
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.BoardQuotePdfExporter
import com.example.util.Formatters
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignageBoardDesignerScreen(
    selectedFamily: BoardDesignFamily,
    onSelectFamily: (BoardDesignFamily) -> Unit,
    onShareQuoteText: (String) -> Unit,
    onSaveToDossier: (String, List<ProjectDossierItem>) -> Unit
) {
    val context = LocalContext.current

    var spec by remember { mutableStateOf(BoardDesignSpec(family = selectedFamily)) }
    LaunchedEffect(selectedFamily) {
        if (spec.family != selectedFamily) {
            val defaultBrand = when (selectedFamily) {
                BoardDesignFamily.TARP_BANNER -> "SIÊU THỊ ĐIỆN MÁY ĐỨC ANH"
                BoardDesignFamily.ALU_3D_LETTER -> "SHOWROOM LUXURY BEAUTY"
                BoardDesignFamily.DECAL_STICKER -> "COFFEE & BAKERY • OPEN"
            }
            spec = spec.copy(
                family = selectedFamily,
                brandText = if (spec.brandText == "ĐỨC ANH STUDIO") defaultBrand else spec.brandText,
                customUnitPriceOverride = null
            )
        }
    }

    var widthText by remember(spec.family) { mutableStateOf(spec.widthMeters.toString()) }
    var heightText by remember(spec.family) { mutableStateOf(spec.heightMeters.toString()) }
    var qtyText by remember(spec.family) { mutableStateOf(spec.quantity.toString()) }
    var customPriceText by remember(spec.family) { mutableStateOf("") }
    var extraFeeText by remember(spec.family) { mutableStateOf("0") }

    val calc = remember(spec) { SignageBoardDesignerCalculator.calculate(spec) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 3-Family Selector Strip (Bảng Bạt | Alu Chữ Nổi | Bảng Decal)
        Row(
            modifier = Modifier.fillMaxWidth().testTag("board_family_tabs"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BoardDesignFamily.entries.forEach { fam ->
                val active = spec.family == fam
                Surface(
                    onClick = { onSelectFamily(fam) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("family_tab_${fam.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (active) Cyan900 else Slate900,
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (active) Cyan400 else Slate800))
                ) {
                    Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = fam.shortTitle,
                            fontSize = 11.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            color = if (active) Cyan400 else Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Cyan900.copy(alpha = 0.5f), Slate900)))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Cyan400.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = Cyan400, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(spec.family.fullTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(spec.family.badgeText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Live Interactive Blueprint Preview
        SignageBoardPreviewCanvas(spec = spec)

        // Realtime m2 & Pricing Strip
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            BoardMetricChip("Diện tích thực", "${String.format(Locale.US, "%.2f", calc.totalRawArea)} m²", Cyan400, Modifier.weight(1f))
            BoardMetricChip("m² tính báo giá", "${String.format(Locale.US, "%.2f", calc.totalBillableArea)} m²", Purple400, Modifier.weight(1f))
            BoardMetricChip("Chu vi viền", "${String.format(Locale.US, "%.2f", calc.perimeterMeters)} m", Amber400, Modifier.weight(1f))
            BoardMetricChip("Tổng báo giá", Formatters.formatVnd(calc.grandTotal), Emerald400, Modifier.weight(1f))
        }

        // Content, Color & Dimensions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("1. Nội Dung, Màu Sắc & Kích Thước (m)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)

                BoardField(spec.brandText, { spec = spec.copy(brandText = it) }, "Nội dung chữ chính trên biển", Modifier.testTag("board_brand_input"))
                BoardField(spec.sloganText, { spec = spec.copy(sloganText = it) }, "Slogan / Dòng phụ / SĐT")

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardColorTheme.entries.forEach { theme ->
                        val sel = spec.colorTheme == theme
                        Surface(
                            onClick = { spec = spec.copy(colorTheme = theme) },
                            modifier = Modifier.heightIn(min = 44.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (sel) Cyan400 else Slate700))
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(theme.bgHexLong)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(theme.label, fontSize = 11.sp, color = if (sel) Cyan400 else Color.White)
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardNumField(widthText, {
                        widthText = it
                        it.toDoubleOrNull()?.let { w -> spec = spec.copy(widthMeters = w.coerceAtLeast(0.1)) }
                    }, "Ngang (m)", Modifier.weight(1f).testTag("board_width_input"))
                    BoardNumField(heightText, {
                        heightText = it
                        it.toDoubleOrNull()?.let { h -> spec = spec.copy(heightMeters = h.coerceAtLeast(0.1)) }
                    }, "Cao (m)", Modifier.weight(1f).testTag("board_height_input"))
                    BoardNumField(qtyText, {
                        qtyText = it
                        it.toIntOrNull()?.let { q -> spec = spec.copy(quantity = q.coerceAtLeast(1)) }
                    }, "SL", Modifier.weight(0.65f).testTag("board_qty_input"))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Slate950).padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Làm tròn tối thiểu 1m² / bảng nhỏ", fontSize = 12.sp, color = Color.White)
                    Switch(
                        checked = spec.minOneM2PerPiece,
                        onCheckedChange = { spec = spec.copy(minOneM2PerPiece = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Cyan400)
                    )
                }
            }
        }

        // Family-Specific Engineering Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                when (spec.family) {
                    BoardDesignFamily.TARP_BANNER -> {
                        Text("2. Chủng Loại Bạt & Quy Cách Gia Công", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TarpProductType.entries.forEach { prod ->
                                SelectableSpecChip(
                                    title = prod.label,
                                    subtitle = "${Formatters.formatVnd(prod.printOnlyRatePerM2)}/m²",
                                    selected = spec.tarpProduct == prod,
                                    onClick = { spec = spec.copy(tarpProduct = prod) }
                                )
                            }
                        }
                        Text("Chế độ khung & hoàn thiện:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Amber400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TarpFinishMode.entries.forEach { fin ->
                                SelectableSpecChip(
                                    title = fin.label,
                                    subtitle = if (fin.frameAddPerM2 > 0) "+${Formatters.formatVnd(fin.frameAddPerM2)}/m²" else "In cuộn",
                                    selected = spec.tarpFinish == fin,
                                    onClick = { spec = spec.copy(tarpFinish = fin) }
                                )
                            }
                        }
                    }

                    BoardDesignFamily.ALU_3D_LETTER -> {
                        Text("2. Chủng Loại Nền Alu & Chữ Nổi 3D", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AluPanelType.entries.forEach { alu ->
                                SelectableSpecChip(
                                    title = alu.label,
                                    subtitle = "${Formatters.formatVnd(alu.installedRatePerM2)}/m²",
                                    selected = spec.aluPanel == alu,
                                    onClick = { spec = spec.copy(aluPanel = alu) }
                                )
                            }
                        }
                        Text("Chất liệu chữ nổi 3D:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Amber400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Letter3dType.entries.forEach { lt ->
                                SelectableSpecChip(
                                    title = lt.label,
                                    subtitle = "${Formatters.formatVnd(lt.ratePerLetterM2)}/m² chữ",
                                    selected = spec.letter3d == lt,
                                    onClick = { spec = spec.copy(letter3d = lt) }
                                )
                            }
                        }
                        Text("Chiếu sáng LED & Mật độ chữ:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Emerald400)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LetterLightingStyle.entries.forEach { ls ->
                                SelectableSpecChip(
                                    title = ls.label,
                                    subtitle = if (ls.hasLed) "+${Formatters.formatVnd(ls.ledPricePerLetterM2)}/m²" else "Không LED",
                                    selected = spec.letterLighting == ls,
                                    onClick = { spec = spec.copy(letterLighting = ls) }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Phủ chữ:", fontSize = 11.sp, color = Color.White)
                            listOf(20, 30, 40, 50).forEach { pct ->
                                MiniChoiceChip("${pct}%", spec.letterCoveragePercent == pct) {
                                    spec = spec.copy(letterCoveragePercent = pct)
                                }
                            }
                        }
                    }

                    BoardDesignFamily.DECAL_STICKER -> {
                        Text("2. Chủng Loại Decal, Cán Màng & Bồi Nền", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DecalProductType.entries.forEach { dp ->
                                SelectableSpecChip(
                                    title = dp.label,
                                    subtitle = "${Formatters.formatVnd(dp.printRatePerM2)}/m²",
                                    selected = spec.decalProduct == dp,
                                    onClick = { spec = spec.copy(decalProduct = dp) }
                                )
                            }
                        }
                        Text("Cán màng bảo vệ:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Amber400)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DecalLaminationType.entries.forEach { lam ->
                                MiniChoiceChip(lam.label, spec.decalLamination == lam) {
                                    spec = spec.copy(decalLamination = lam)
                                }
                            }
                        }
                        Text("Bồi nền cứng thành bảng:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Emerald400)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DecalBackingType.entries.forEach { back ->
                                SelectableSpecChip(
                                    title = back.label,
                                    subtitle = if (back.addRatePerM2 > 0) "+${Formatters.formatVnd(back.addRatePerM2)}/m²" else "Không bồi",
                                    selected = spec.decalBacking == back,
                                    onClick = { spec = spec.copy(decalBacking = back) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bao gồm công thợ dán tận nơi (+65k/m²)", fontSize = 12.sp, color = Color.White)
                            Switch(
                                checked = spec.includeOnSiteInstall,
                                onCheckedChange = { spec = spec.copy(includeOnSiteInstall = it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Emerald400)
                            )
                        }
                    }
                }

                // Optimization Summary Note
                Surface(color = Slate950, shape = RoundedCornerShape(10.dp), border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.45f)))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(calc.rollOrSheetHint, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                        Text(calc.materialPlanSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Step 3: Professional Customizable Quotation Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("board_quote_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400.copy(alpha = 0.5f)))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("3. Báo Giá Chuyên Nghiệp Tùy Chỉnh", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Emerald400)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardField(spec.clientName, { spec = spec.copy(clientName = it) }, "Tên khách hàng", Modifier.weight(1f))
                    BoardField(spec.companyPhone, { spec = spec.copy(companyPhone = it) }, "Hotline xưởng", Modifier.weight(0.9f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardNumField(customPriceText, {
                        customPriceText = it
                        spec = spec.copy(customUnitPriceOverride = it.toDoubleOrNull()?.coerceAtLeast(0.0))
                    }, "Sửa đơn giá chính (đ/m²)", Modifier.weight(1f))
                    BoardNumField(extraFeeText, {
                        extraFeeText = it
                        spec = spec.copy(extraDesignOrInstallFee = it.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0)
                    }, "Phí lắp đặt / TK (đ)", Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("CK:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(0.0, 5.0, 10.0).forEach { d ->
                        MiniChoiceChip("${d.toInt()}%", spec.discountPercent == d) { spec = spec.copy(discountPercent = d) }
                    }
                    Text("VAT:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf(0.0, 8.0, 10.0).forEach { v ->
                        MiniChoiceChip("${v.toInt()}%", spec.vatPercent == v) { spec = spec.copy(vatPercent = v) }
                    }
                }

                calc.lines.forEachIndexed { idx, line ->
                    Surface(color = Slate950, shape = RoundedCornerShape(10.dp), border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${idx + 1}. ${line.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", line.quantity)} ${line.unit} × ${Formatters.formatVnd(line.unitPrice)}",
                                    fontSize = 11.sp,
                                    color = Cyan400
                                )
                            }
                            Text(Formatters.formatVnd(line.lineTotal), fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Emerald400)
                        }
                    }
                }

                HorizontalDivider(color = Slate800)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TỔNG THANH TOÁN:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = Formatters.formatVnd(calc.grandTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = Emerald400,
                        modifier = Modifier.testTag("board_grand_total_text")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { BoardQuotePdfExporter.shareBoardQuotePdf(context, spec, calc) },
                        modifier = Modifier.weight(1.1f).heightIn(min = 48.dp).testTag("board_export_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PDF Báo Giá", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onShareQuoteText(BoardQuotePdfExporter.buildBoardZaloQuote(spec, calc)) },
                        modifier = Modifier.weight(0.95f).heightIn(min = 48.dp).testTag("board_share_zalo_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Zalo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                    }
                    Button(
                        onClick = {
                            val dossierItem = ProjectDossierItem(
                                id = System.currentTimeMillis(),
                                name = "${spec.family.shortTitle}: ${spec.brandText.ifBlank { spec.family.fullTitle }}",
                                categoryName = spec.family.shortTitle,
                                dimensions = "${String.format(Locale.US, "%.2f", spec.widthMeters)}m x ${String.format(Locale.US, "%.2f", spec.heightMeters)}m",
                                quantity = spec.quantity,
                                totalCost = calc.estimatedCost,
                                finalPrice = calc.grandTotal
                            )
                            onSaveToDossier(spec.brandText.ifBlank { spec.family.fullTitle }, listOf(dossierItem))
                        },
                        modifier = Modifier.weight(0.95f).heightIn(min = 48.dp).testTag("board_save_dossier_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lưu Hồ Sơ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardMetricChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Slate900, shape = RoundedCornerShape(10.dp), border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun SelectableSpecChip(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Cyan900 else Slate950,
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (selected) Cyan400 else Slate700))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Cyan400 else Color.White)
            Text(subtitle, fontSize = 10.sp, color = if (selected) Emerald400 else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Cyan900 else Slate950,
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (selected) Cyan400 else Slate700))
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Cyan400 else Color.White)
        }
    }
}

@Composable
private fun BoardField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
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

@Composable
private fun BoardNumField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth(),
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
