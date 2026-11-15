package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.SmartQuoteLine
import com.example.model.DossierQuoteSettings
import com.example.model.ProjectDossierItem
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ProjectItemEditorDialog(
    initialItem: ProjectDossierItem?,
    onSave: (ProjectDossierItem) -> Unit,
    onDismiss: () -> Unit
) {
    val isEdit = initialItem != null
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var category by remember { mutableStateOf(initialItem?.categoryName ?: "Hạng Mục Tùy Chỉnh") }
    var dimensions by remember { mutableStateOf(initialItem?.dimensions ?: "4.0m x 1.5m") }
    var qtyText by remember { mutableStateOf((initialItem?.quantity ?: 1).toString()) }
    var costText by remember { mutableStateOf((initialItem?.totalCost ?: 1500000.0).toLong().toString()) }
    var priceText by remember { mutableStateOf((initialItem?.finalPrice ?: 2200000.0).toLong().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isEdit) "Chỉnh Sửa Hạng Mục Dự Toán" else "Thêm Hạng Mục Tùy Chỉnh",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DialogTextField(name, { name = it }, "Tên hạng mục *", Modifier.testTag("dossier_item_name_input"))
                DialogTextField(category, { category = it }, "Chủng loại / Nhóm")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(dimensions, { dimensions = it }, "Kích thước", Modifier.weight(1.4f))
                    DialogTextField(
                        qtyText,
                        { qtyText = it },
                        "SL",
                        Modifier.weight(0.6f),
                        KeyboardType.Number
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(
                        costText,
                        { costText = it },
                        "Giá vốn (VNĐ)",
                        Modifier.weight(1f),
                        KeyboardType.Number
                    )
                    DialogTextField(
                        priceText,
                        { priceText = it },
                        "Giá báo khách (VNĐ)",
                        Modifier.weight(1f).testTag("dossier_item_price_input"),
                        KeyboardType.Number
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    if (trimmedName.isNotEmpty()) {
                        val qty = qtyText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                        val cost = costText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                        val price = priceText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                        onSave(
                            ProjectDossierItem(
                                id = initialItem?.id ?: System.currentTimeMillis(),
                                name = trimmedName,
                                categoryName = category.ifBlank { "Tùy chỉnh" },
                                dimensions = dimensions.ifBlank { "Theo thực tế" },
                                quantity = qty,
                                totalCost = cost,
                                finalPrice = price,
                                createdAt = initialItem?.createdAt ?: System.currentTimeMillis()
                            )
                        )
                    }
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("save_dossier_item_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
            ) {
                Text(if (isEdit) "Lưu Thay Đổi" else "Thêm Hạng Mục", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun DossierQuoteSettingsDialog(
    current: DossierQuoteSettings,
    onSave: (DossierQuoteSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var companyName by remember { mutableStateOf(current.companyName) }
    var companyPhone by remember { mutableStateOf(current.companyPhone) }
    var clientName by remember { mutableStateOf(current.clientName) }
    var discountText by remember { mutableStateOf(current.discountPercent.toInt().toString()) }
    var vatText by remember { mutableStateOf(current.vatPercent.toInt().toString()) }
    var warrantyText by remember { mutableStateOf(current.warrantyMonths.toString()) }
    var depositText by remember { mutableStateOf(current.depositPercent.toString()) }
    var notes by remember { mutableStateOf(current.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = { Text("Tùy Chỉnh Thông Tin Báo Giá", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DialogTextField(companyName, { companyName = it }, "Tên đơn vị / Xưởng thi công")
                DialogTextField(companyPhone, { companyPhone = it }, "Hotline liên hệ")
                DialogTextField(clientName, { clientName = it }, "Tên khách hàng / Công ty")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(discountText, { discountText = it }, "Chiết khấu (%)", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(vatText, { vatText = it }, "Thuế VAT (%)", Modifier.weight(1f), KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(warrantyText, { warrantyText = it }, "Bảo hành (tháng)", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(depositText, { depositText = it }, "Tạm ứng (%)", Modifier.weight(1f), KeyboardType.Number)
                }
                DialogTextField(notes, { notes = it }, "Ghi chú & điều khoản thêm", maxLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DossierQuoteSettings(
                            companyName = companyName.trim().ifBlank { "ĐỨC ANH ADVERTISEMENT PRO" },
                            companyPhone = companyPhone.trim(),
                            clientName = clientName.trim().ifBlank { "Quý Khách Hàng" },
                            discountPercent = (discountText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 90.0),
                            vatPercent = (vatText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 30.0),
                            warrantyMonths = (warrantyText.toIntOrNull() ?: 24).coerceIn(1, 120),
                            depositPercent = (depositText.toIntOrNull() ?: 40).coerceIn(0, 100),
                            notes = notes.trim()
                        )
                    )
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("save_dossier_settings_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
            ) {
                Text("Áp Dụng", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Đóng", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun SmartQuoteLineEditDialog(
    line: SmartQuoteLine,
    onSave: (SmartQuoteLine) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(line.title) }
    var widthMText by remember { mutableStateOf(line.widthMeters.toString()) }
    var heightMText by remember { mutableStateOf(line.heightMeters.toString()) }
    var qtyText by remember { mutableStateOf(line.quantity.toString()) }
    var unitPriceText by remember { mutableStateOf(line.unitPricePerM2.toLong().toString()) }
    var extraFeeText by remember { mutableStateOf(line.extraProcessingFeePerPiece.toLong().toString()) }
    var minOneM2 by remember { mutableStateOf(line.minOneM2PerPiece) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = { Text("Chỉnh Sửa Dòng Tính Giá", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DialogTextField(title, { title = it }, "Tên hạng mục")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(widthMText, { widthMText = it }, "Ngang (m)", Modifier.weight(1f), KeyboardType.Decimal)
                    DialogTextField(heightMText, { heightMText = it }, "Cao (m)", Modifier.weight(1f), KeyboardType.Decimal)
                    DialogTextField(qtyText, { qtyText = it }, "SL", Modifier.weight(0.7f), KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(unitPriceText, { unitPriceText = it }, "Đơn giá/m²", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(extraFeeText, { extraFeeText = it }, "Phụ phí/tấm", Modifier.weight(1f), KeyboardType.Number)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Làm tròn tối thiểu 1m² / tấm", fontSize = 12.sp, color = Color.White)
                    Switch(
                        checked = minOneM2,
                        onCheckedChange = { minOneM2 = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Cyan400)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = (widthMText.toDoubleOrNull() ?: line.widthMeters).coerceAtLeast(0.05)
                    val h = (heightMText.toDoubleOrNull() ?: line.heightMeters).coerceAtLeast(0.05)
                    val q = (qtyText.toIntOrNull() ?: line.quantity).coerceAtLeast(1)
                    val p = (unitPriceText.toDoubleOrNull() ?: line.unitPricePerM2).coerceAtLeast(0.0)
                    val extra = (extraFeeText.toDoubleOrNull() ?: line.extraProcessingFeePerPiece).coerceAtLeast(0.0)
                    onSave(
                        line.copy(
                            title = title.trim().ifBlank { line.title },
                            widthMeters = w,
                            heightMeters = h,
                            rawDisplaySize = "${String.format("%.2f", w)}x${String.format("%.2f", h)} m",
                            quantity = q,
                            unitPricePerM2 = p,
                            minOneM2PerPiece = minOneM2,
                            extraProcessingFeePerPiece = extra
                        )
                    )
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("save_smart_line_edit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
            ) {
                Text("Cập Nhật", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
internal fun DialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = maxLines == 1,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
