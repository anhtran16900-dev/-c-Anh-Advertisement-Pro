package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EstimationInput
import com.example.model.EstimationResult
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Formatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EstimatorAreaQuoteCard(
    input: EstimationInput,
    result: EstimationResult,
    onToggleMinOneM2: (Boolean) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onVatChange: (Double) -> Unit,
    onCustomUnitPriceChange: (Double?, Boolean) -> Unit,
    onSaveToProject: () -> Unit,
    onExportPdf: () -> Unit,
    onShareZalo: () -> Unit,
    onCopyQuote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cost_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(Cyan400, Color(0xFF3B82F6)))
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Cyan900.copy(alpha = 0.4f), Color.Transparent),
                        radius = 650f
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.SquareFoot,
                            contentDescription = null,
                            tint = Cyan400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TÍNH MÉT VUÔNG (m²) & BÁO GIÁ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                        )
                    ) {
                        Text(
                            text = result.effectiveCategoryName.ifBlank { result.category.shortName },
                            style = MaterialTheme.typography.labelSmall,
                            color = Amber400,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Selected Item & Location context strip
                if (input.projectLocation.isNotBlank()) {
                    Text(
                        text = "📍 Vị trí lắp đặt: ${input.projectLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                // m2 Breakdown Grid (2x2 metrics)
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Slate800)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("area_calculation_box")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AreaMetricBox(
                                label = "DT 1 biển (${result.width}x${result.height}m)",
                                value = Formatters.formatArea(result.singlePieceArea),
                                accent = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            AreaMetricBox(
                                label = "Tổng m² thực (×${result.quantity})",
                                value = Formatters.formatArea(result.netArea),
                                accent = Cyan400,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AreaMetricBox(
                                label = if (input.minOneM2PerPiece) "m² tính tiền (≥1m²/cái)" else "m² tính báo giá",
                                value = Formatters.formatArea(result.billableArea),
                                accent = Emerald400,
                                modifier = Modifier.weight(1f)
                            )
                            AreaMetricBox(
                                label = "Phôi (+${input.wastagePercent}%) | Chu vi",
                                value = "${Formatters.formatArea(result.grossAreaWithWastage)} | ${Formatters.formatNumber(result.perimeter)}m",
                                accent = Amber400,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Min 1m2 switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Làm tròn tối thiểu 1m² / biển nhỏ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Áp dụng đơn giá tối thiểu 1.0 m² cho biển dưới 1m²",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = input.minOneM2PerPiece,
                        onCheckedChange = onToggleMinOneM2,
                        modifier = Modifier.testTag("min_one_m2_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Cyan400,
                            uncheckedTrackColor = Slate800
                        )
                    )
                }

                // Custom Unit Price / m2 Mode
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Đơn Giá Báo Khách / m²",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = input.customUnitPriceOverridePerM2 == null,
                                onClick = { onCustomUnitPriceChange(null, true) },
                                label = { Text("Tự tính", fontSize = 11.sp) },
                                modifier = Modifier.heightIn(min = 36.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Cyan900,
                                    selectedLabelColor = Cyan400,
                                    containerColor = Slate950,
                                    labelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = input.customUnitPriceOverridePerM2 != null,
                                onClick = {
                                    val seed = input.customUnitPriceOverridePerM2
                                        ?: result.unitPricePerM2.takeIf { it > 0 }?.toLong()?.toDouble()
                                        ?: 450000.0
                                    onCustomUnitPriceChange(seed, false)
                                },
                                label = { Text("Tự nhập đ/m²", fontSize = 11.sp) },
                                modifier = Modifier.heightIn(min = 36.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Cyan900,
                                    selectedLabelColor = Cyan400,
                                    containerColor = Slate950,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    if (input.customUnitPriceOverridePerM2 != null) {
                        OutlinedTextField(
                            value = input.customUnitPriceOverridePerM2.toLong().toString(),
                            onValueChange = { raw ->
                                val parsed = raw.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                                onCustomUnitPriceChange(parsed, false)
                            },
                            label = { Text("Nhập đơn giá báo khách (VNĐ / m²)", color = Cyan400) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("custom_unit_price_m2_input"),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Amber400
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Discount & VAT quick selectors
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Chiết Khấu & Thuế VAT Báo Giá",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.0, 5.0, 10.0, 15.0).forEach { disc ->
                            FilterChip(
                                selected = input.discountPercent == disc,
                                onClick = { onDiscountChange(disc) },
                                label = { Text(if (disc == 0.0) "CK 0%" else "-${disc.toInt()}%", fontSize = 11.sp) },
                                modifier = Modifier.heightIn(min = 36.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Amber400.copy(alpha = 0.2f),
                                    selectedLabelColor = Amber400,
                                    containerColor = Slate950,
                                    labelColor = Color.White
                                )
                            )
                        }
                        listOf(0.0, 8.0, 10.0).forEach { vat ->
                            FilterChip(
                                selected = input.vatPercent == vat,
                                onClick = { onVatChange(vat) },
                                label = { Text("VAT ${vat.toInt()}%", fontSize = 11.sp) },
                                modifier = Modifier.heightIn(min = 36.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Cyan900,
                                    selectedLabelColor = Cyan400,
                                    containerColor = Slate950,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Slate800)
                )

                // Financial lines
                QuoteSummaryLine("Tổng Giá Vốn Xưởng (Vật tư + Công + VC):", Formatters.formatVnd(result.totalCost), Color.White)
                QuoteSummaryLine(
                    "Lợi Nhuận Dự Kiến:",
                    "${if (result.profitAmount >= 0) "+" else ""}${Formatters.formatVnd(result.profitAmount)}",
                    if (result.profitAmount >= 0) Emerald400 else Color(0xFFF87171)
                )
                if (result.discountAmount > 0.0 || result.vatAmount > 0.0) {
                    QuoteSummaryLine("Tạm tính trước CK/VAT:", Formatters.formatVnd(result.subtotalCustomerPrice), Color.White)
                }
                if (result.discountAmount > 0.0) {
                    QuoteSummaryLine("Chiết khấu (${input.discountPercent.toInt()}%):", "-${Formatters.formatVnd(result.discountAmount)}", Amber400)
                }
                if (result.vatAmount > 0.0) {
                    QuoteSummaryLine("Thuế VAT (${input.vatPercent.toInt()}%):", "+${Formatters.formatVnd(result.vatAmount)}", Cyan400)
                }

                // Grand Total Price
                Column {
                    Text(
                        text = "TỔNG BÁO GIÁ GỬI KHÁCH HÀNG",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Formatters.formatVnd(result.finalCustomerPrice),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Cyan400,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("final_price_text")
                    )
                    Text(
                        text = "Đơn giá / m² (${Formatters.formatArea(result.billableArea)}): ${Formatters.formatVnd(result.unitPricePerM2)} / m²",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald400,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("unit_price_m2_text")
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSaveToProject,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("add_to_project_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Lưu Hồ Sơ", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = onExportPdf,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("estimator_export_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Xuất PDF", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onShareZalo,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("share_quote_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Gửi Zalo Báo Giá", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onCopyQuote,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("copy_quote_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Amber400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copy Báo Giá m²", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AreaMetricBox(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Slate900, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}

@Composable
private fun QuoteSummaryLine(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
