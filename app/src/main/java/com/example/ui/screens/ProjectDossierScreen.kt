package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProjectDossierItem
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Formatters
import com.example.viewmodel.SignageViewModel

import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.foundation.layout.heightIn
import com.example.ui.dialogs.DossierQuoteSettingsDialog
import com.example.ui.dialogs.ProjectItemEditorDialog
import com.example.model.DossierQuoteSettings

@Composable
fun ProjectDossierScreen(
    viewModel: SignageViewModel,
    onShareDossier: (String) -> Unit
) {
    val title by viewModel.projectTitle.collectAsState()
    val items by viewModel.projectItems.collectAsState()
    val quoteSettings by viewModel.dossierQuoteSettings.collectAsState()
    val context = LocalContext.current

    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showQuoteSettingsDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ProjectDossierItem?>(null) }
    var showAddCustomItemDialog by remember { mutableStateOf(false) }

    val subtotalPrice = items.sumOf { it.finalPrice }
    val discountAmount = subtotalPrice * (quoteSettings.discountPercent.coerceIn(0.0, 90.0) / 100.0)
    val afterDiscount = (subtotalPrice - discountAmount).coerceAtLeast(0.0)
    val vatAmount = afterDiscount * (quoteSettings.vatPercent.coerceIn(0.0, 30.0) / 100.0)
    val grandTotalPrice = afterDiscount + vatAmount
    val grandTotalCost = items.sumOf { it.totalCost }
    val grandTotalProfit = grandTotalPrice - grandTotalCost

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Actions
        Card(
            modifier = Modifier.fillMaxWidth().testTag("project_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Amber400.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, tint = Amber400, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                IconButton(onClick = { showRenameDialog = true }, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Đổi tên", tint = Cyan400, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                text = "Hồ sơ dự toán tổng thể công trình",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { showNewProjectDialog = true },
                            modifier = Modifier.testTag("create_new_project_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Dự án mới", tint = Amber400)
                        }
                        if (items.isNotEmpty()) {
                            IconButton(
                                onClick = { showClearConfirmDialog = true },
                                modifier = Modifier.testTag("clear_project_btn")
                            ) {
                                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Làm trống", tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }

                // Grand Total Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Slate950, Slate850)
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TỔNG NGÂN SÁCH DỰ TOÁN (${items.size} hạng mục):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Formatters.formatVnd(grandTotalPrice),
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald400,
                                modifier = Modifier.testTag("project_grand_total_text")
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Giá vốn: ${Formatters.formatVnd(grandTotalCost)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Lợi nhuận: +${Formatters.formatVnd(grandTotalProfit)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Cyan400
                            )
                            if (quoteSettings.discountPercent > 0.0 || quoteSettings.vatPercent > 0.0) {
                                Text(
                                    text = "CK ${quoteSettings.discountPercent.toInt()}% • VAT ${quoteSettings.vatPercent.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber400
                                )
                            }
                        }
                    }
                }

                // Smart Customization Toolbar: Add Custom Item & Customize Quote Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAddCustomItemDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("add_custom_dossier_item_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald400.copy(alpha = 0.6f)))
                    ) {
                        Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Thêm Hạng Mục", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                    }

                    OutlinedButton(
                        onClick = { showQuoteSettingsDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("customize_dossier_quote_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Cyan400.copy(alpha = 0.6f)))
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tùy Chỉnh Báo Giá", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                    }
                }
            }
        }

        // Items List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items, key = { _, it -> it.id }) { index, item ->
                ProjectItemCard(
                    index = index + 1,
                    item = item,
                    onEdit = { editingItem = item },
                    onDelete = { viewModel.removeProjectItem(item.id) }
                )
            }

            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.5f)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📁",
                                fontSize = 36.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Chưa có hạng mục nào trong dự toán",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Chuyển sang tab 'Bóc Tách Realtime' để nhập kích thước và bấm 'Thêm Vào Hồ Sơ'.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Bottom Export Actions
        if (items.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val dossierInput = com.example.model.EstimationInput(
                                itemName = title,
                                clientName = quoteSettings.clientName,
                                projectNotes = quoteSettings.notes,
                                quantity = items.size
                            )
                            val dossierResult = com.example.model.EstimationResult(
                                itemName = title,
                                category = com.example.model.SignageCategory.ALU_LETTER,
                                width = 0.0,
                                height = 0.0,
                                quantity = items.size,
                                netArea = 0.0,
                                grossAreaWithWastage = 0.0,
                                perimeter = 0.0,
                                breakdownItems = items.map {
                                    com.example.model.BreakdownItem(
                                        name = "${it.name} (${it.categoryName})",
                                        unit = "Bộ",
                                        quantity = it.quantity.toDouble(),
                                        unitPrice = if (it.quantity > 0) it.finalPrice / it.quantity else it.finalPrice,
                                        totalPrice = it.finalPrice
                                    )
                                },
                                totalMaterialCost = grandTotalCost * 0.7,
                                laborCost = grandTotalCost * 0.25,
                                shippingCost = grandTotalCost * 0.05,
                                totalCost = grandTotalCost,
                                profitAmount = grandTotalProfit,
                                finalCustomerPrice = grandTotalPrice,
                                unitPricePerM2 = 0.0
                            )
                            com.example.util.ProjectExportManager.sharePdfQuotation(context, dossierInput, dossierResult, items)
                        },
                        modifier = Modifier.weight(1.2f).heightIn(min = 48.dp).testTag("print_export_dossier_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xuất PDF (A4)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val text = buildFullProjectDossierText(
                                title = title,
                                items = items,
                                settings = quoteSettings,
                                subtotal = subtotalPrice,
                                discount = discountAmount,
                                vat = vatAmount,
                                grandTotal = grandTotalPrice
                            )
                            onShareDossier(text)
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("share_dossier_text_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Zalo / Text",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        var newTitle by remember { mutableStateOf("Dự án Showroom ${System.currentTimeMillis() % 1000}") }
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Tạo Dự Án Mới", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Tên Dự Án") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Amber400
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.createNewProject(newTitle.trim())
                            showNewProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400, contentColor = Color.Black)
                ) {
                    Text("Khởi Tạo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Slate900
        )
    }

    // Rename Dialog
    if (showRenameDialog) {
        var renameText by remember { mutableStateOf(title) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Đổi Tên Dự Án", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Tên Dự Án Mới") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Cyan400
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            viewModel.renameProjectTitleOnly(renameText.trim())
                            showRenameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
                ) {
                    Text("Lưu Tên", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Slate900
        )
    }

    // Clear Confirmation
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Làm Trống Dự Toán?", color = Color.White) },
            text = { Text("Toàn bộ các hạng mục đã lưu trong dự án này sẽ bị xóa.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearProject()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                ) {
                    Text("Xóa Hết", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Không", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Slate900
        )
    }

    if (showAddCustomItemDialog) {
        ProjectItemEditorDialog(
            initialItem = null,
            onSave = { newItem ->
                viewModel.addCustomProjectItem(newItem)
                showAddCustomItemDialog = false
            },
            onDismiss = { showAddCustomItemDialog = false }
        )
    }

    editingItem?.let { target ->
        ProjectItemEditorDialog(
            initialItem = target,
            onSave = { updated ->
                viewModel.updateProjectItem(updated)
                editingItem = null
            },
            onDismiss = { editingItem = null }
        )
    }

    if (showQuoteSettingsDialog) {
        DossierQuoteSettingsDialog(
            current = quoteSettings,
            onSave = { updatedSettings ->
                viewModel.updateDossierQuoteSettings(updatedSettings)
                showQuoteSettingsDialog = false
            },
            onDismiss = { showQuoteSettingsDialog = false }
        )
    }
}

@Composable
private fun ProjectItemCard(
    index: Int,
    item: ProjectDossierItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                ) {
                    Text(
                        text = "#$index",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Amber400,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${item.categoryName} • ${item.dimensions} (SL: ${item.quantity})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Giá vốn: ${Formatters.formatVnd(item.totalCost)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatVnd(item.finalPrice),
                    style = MaterialTheme.typography.titleSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(48.dp).testTag("edit_dossier_item_${item.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Chỉnh sửa hạng mục", tint = Cyan400, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(48.dp).testTag("delete_dossier_item_${item.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa hạng mục", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

private fun buildFullProjectDossierText(
    title: String,
    items: List<ProjectDossierItem>,
    settings: DossierQuoteSettings,
    subtotal: Double,
    discount: Double,
    vat: Double,
    grandTotal: Double
): String {
    val sb = StringBuilder()
    sb.append("🏢 HỒ SƠ DỰ TOÁN BÁO GIÁ CÔNG TRÌNH\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("Công trình: $title\n")
    sb.append("Khách hàng: ${settings.clientName}\n")
    sb.append("Đơn vị thi công: ${settings.companyName} (${settings.companyPhone})\n")
    sb.append("Ngày lập: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("vi", "VN")).format(java.util.Date())}\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")

    items.forEachIndexed { idx, it ->
        sb.append("${idx + 1}. ${it.name}\n")
        sb.append("   • Chủng loại: ${it.categoryName}\n")
        sb.append("   • Kích thước: ${it.dimensions} (Số lượng: ${it.quantity})\n")
        sb.append("   • Thành tiền: ${Formatters.formatVnd(it.finalPrice)}\n\n")
    }

    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("Tạm tính: ${Formatters.formatVnd(subtotal)}\n")
    if (discount > 0.0) sb.append("Chiết khấu (${settings.discountPercent.toInt()}%): -${Formatters.formatVnd(discount)}\n")
    if (vat > 0.0) sb.append("Thuế VAT (${settings.vatPercent.toInt()}%): +${Formatters.formatVnd(vat)}\n")
    sb.append("TỔNG GIÁ TRỊ DỰ ÁN: ${Formatters.formatVnd(grandTotal)}\n")
    sb.append("Bảo hành: ${settings.warrantyMonths} tháng | Tạm ứng: ${settings.depositPercent}%\n")
    if (settings.notes.isNotBlank()) sb.append("Ghi chú: ${settings.notes}\n")
    return sb.toString()
}
