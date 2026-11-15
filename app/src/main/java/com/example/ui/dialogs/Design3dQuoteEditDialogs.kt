package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.Design3dQuoteLine
import com.example.model.Design3dQuoteTerms
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate900

@Composable
fun Design3dQuoteLineDialog(
    initialLine: Design3dQuoteLine?,
    onSave: (Design3dQuoteLine) -> Unit,
    onDismiss: () -> Unit
) {
    val isEdit = initialLine != null
    var name by remember { mutableStateOf(initialLine?.name ?: "") }
    var spec by remember { mutableStateOf(initialLine?.spec ?: "") }
    var unit by remember { mutableStateOf(initialLine?.unit ?: "m²") }
    var qtyText by remember { mutableStateOf((initialLine?.quantity ?: 1.0).toString()) }
    var priceText by remember { mutableStateOf((initialLine?.unitPrice ?: 500000.0).toLong().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isEdit) "Chỉnh Sửa Hạng Mục Báo Giá 3D" else "Thêm Hạng Mục Báo Giá 3D",
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
                DialogTextField(name, { name = it }, "Tên hạng mục *", Modifier.testTag("design3d_line_name_input"))
                DialogTextField(spec, { spec = it }, "Quy cách kỹ thuật chi tiết", maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(unit, { unit = it }, "ĐVT (m², Bộ...)", Modifier.weight(0.9f))
                    DialogTextField(qtyText, { qtyText = it }, "Khối lượng", Modifier.weight(1.1f), KeyboardType.Decimal)
                }
                DialogTextField(
                    priceText,
                    { priceText = it },
                    "Đơn giá báo khách (VNĐ)",
                    Modifier.testTag("design3d_line_price_input"),
                    KeyboardType.Number
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) {
                        val qty = (qtyText.toDoubleOrNull() ?: 1.0).coerceAtLeast(0.1)
                        val price = (priceText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                        onSave(
                            Design3dQuoteLine(
                                id = initialLine?.id ?: System.nanoTime(),
                                name = trimmed,
                                spec = spec.trim().ifBlank { "Theo quy cách thiết kế 3D" },
                                unit = unit.trim().ifBlank { "Bộ" },
                                quantity = qty,
                                unitPrice = price
                            )
                        )
                    }
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("save_design3d_line_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
            ) {
                Text(if (isEdit) "Lưu Thay Đổi" else "Thêm Dòng", fontWeight = FontWeight.Bold)
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
fun Design3dQuoteTermsDialog(
    current: Design3dQuoteTerms,
    onSave: (Design3dQuoteTerms) -> Unit,
    onDismiss: () -> Unit
) {
    var companyName by remember { mutableStateOf(current.companyName) }
    var companyPhone by remember { mutableStateOf(current.companyPhone) }
    var clientName by remember { mutableStateOf(current.clientName) }
    var clientPhone by remember { mutableStateOf(current.clientPhone) }
    var address by remember { mutableStateOf(current.projectAddress) }
    var discountText by remember { mutableStateOf(current.discountPercent.toInt().toString()) }
    var vatText by remember { mutableStateOf(current.vatPercent.toInt().toString()) }
    var warrantyText by remember { mutableStateOf(current.warrantyMonths.toString()) }
    var daysText by remember { mutableStateOf(current.productionDays.toString()) }
    var depositText by remember { mutableStateOf(current.depositPercent.toString()) }
    var notes by remember { mutableStateOf(current.extraNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text("Tùy Chỉnh Báo Giá Gửi Khách", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DialogTextField(companyName, { companyName = it }, "Tên xưởng / Công ty báo giá")
                DialogTextField(companyPhone, { companyPhone = it }, "Hotline xưởng")
                DialogTextField(clientName, { clientName = it }, "Tên khách hàng / Chủ đầu tư")
                DialogTextField(clientPhone, { clientPhone = it }, "SĐT khách hàng")
                DialogTextField(address, { address = it }, "Địa chỉ lắp đặt công trình")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(discountText, { discountText = it }, "Chiết khấu (%)", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(vatText, { vatText = it }, "VAT (%)", Modifier.weight(1f), KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogTextField(daysText, { daysText = it }, "Số ngày làm", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(warrantyText, { warrantyText = it }, "Bảo hành (tháng)", Modifier.weight(1f), KeyboardType.Number)
                    DialogTextField(depositText, { depositText = it }, "Tạm ứng (%)", Modifier.weight(1f), KeyboardType.Number)
                }
                DialogTextField(notes, { notes = it }, "Ghi chú / Cam kết báo giá", maxLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Design3dQuoteTerms(
                            companyName = companyName.trim().ifBlank { "ĐỨC ANH ADVERTISEMENT PRO" },
                            companyPhone = companyPhone.trim(),
                            clientName = clientName.trim().ifBlank { "Quý Khách Hàng" },
                            clientPhone = clientPhone.trim(),
                            projectAddress = address.trim(),
                            warrantyMonths = (warrantyText.toIntOrNull() ?: 24).coerceIn(1, 120),
                            productionDays = (daysText.toIntOrNull() ?: 5).coerceIn(1, 60),
                            depositPercent = (depositText.toIntOrNull() ?: 40).coerceIn(0, 100),
                            discountPercent = (discountText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 90.0),
                            vatPercent = (vatText.toDoubleOrNull() ?: 8.0).coerceIn(0.0, 30.0),
                            extraNotes = notes.trim()
                        )
                    )
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("save_design3d_terms_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
            ) {
                Text("Áp Dụng Cấu Hình", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Đóng", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
