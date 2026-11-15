package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditableCategoryOption
import com.example.model.InstallLocationPreset
import com.example.model.LaborComplexity
import com.example.model.SignageCategory
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryOptionEditorDialog(
    editingOption: EditableCategoryOption?,
    onDismiss: () -> Unit,
    onSave: (EditableCategoryOption) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isNew = editingOption == null
    var shortName by remember(editingOption) { mutableStateOf(editingOption?.shortName ?: "") }
    var displayName by remember(editingOption) { mutableStateOf(editingOption?.displayName ?: "") }
    var baseCategory by remember(editingOption) {
        mutableStateOf(editingOption?.baseCategory ?: SignageCategory.HIFLEX)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isNew) "Thêm Loại Hạng Mục Mới" else "Chỉnh Sửa Loại Hạng Mục",
                color = Cyan400,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tên ngắn hiển thị (Báo giá / Hồ sơ) *",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = shortName,
                    onValueChange = {
                        shortName = it
                        if (displayName.isBlank() || isNew) {
                            displayName = it
                        }
                    },
                    placeholder = { Text("VD: Biển Hộp Đèn LED Siêu Mỏng") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("category_short_name_input"),
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

                Text(
                    text = "Tên đầy đủ trong danh sách chọn",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = { Text("VD: MỤC 6: Biển Hộp Đèn Siêu Mỏng / Tranh Điện") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("category_display_name_input"),
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

                Text(
                    text = "Công thức bóc tách vật tư nền áp dụng:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber400,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SignageCategory.values().forEach { cat ->
                        val selected = baseCategory == cat
                        FilterChip(
                            selected = selected,
                            onClick = { baseCategory = cat },
                            label = { Text(cat.shortName, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 40.dp),
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
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isNew && editingOption != null && !editingOption.isBuiltIn && onDelete != null) {
                    OutlinedButton(
                        onClick = {
                            onDelete(editingOption.id)
                            onDismiss()
                        },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("delete_category_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
                    ) {
                        Text("Xóa")
                    }
                }
                Button(
                    onClick = {
                        val trimmedShort = shortName.trim()
                        if (trimmedShort.isNotEmpty()) {
                            onSave(
                                EditableCategoryOption(
                                    id = editingOption?.id ?: "custom_cat_${System.currentTimeMillis()}",
                                    shortName = trimmedShort,
                                    displayName = displayName.trim().ifBlank { trimmedShort },
                                    baseCategory = baseCategory,
                                    isBuiltIn = editingOption?.isBuiltIn ?: false
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = shortName.trim().isNotEmpty(),
                    modifier = Modifier.heightIn(min = 48.dp).testTag("save_category_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
                ) {
                    Text(if (isNew) "Thêm Mới" else "Lưu Thay Đổi", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun ItemNamePresetEditorDialog(
    initialName: String?,
    categoryLabel: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isNew = initialName == null
    var text by remember(initialName) { mutableStateOf(initialName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isNew) "Thêm Mẫu Tên Hạng Mục" else "Chỉnh Sửa Tên Hạng Mục",
                color = Cyan400,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Thuộc phân loại: $categoryLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber400
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("VD: Biển Hộp Đèn Bạt 3M 2 Mặt Treo Trụ") },
                    modifier = Modifier.fillMaxWidth().testTag("item_preset_name_input"),
                    maxLines = 2,
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
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isNew && onDelete != null) {
                    OutlinedButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("delete_item_preset_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
                    ) {
                        Text("Xóa Mẫu")
                    }
                }
                Button(
                    onClick = {
                        if (text.trim().isNotEmpty()) {
                            onSave(text.trim())
                            onDismiss()
                        }
                    },
                    enabled = text.trim().isNotEmpty(),
                    modifier = Modifier.heightIn(min = 48.dp).testTag("save_item_preset_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
                ) {
                    Text(if (isNew) "Thêm Mẫu" else "Cập Nhật", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Đóng", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InstallLocationEditorDialog(
    editingLocation: InstallLocationPreset?,
    onDismiss: () -> Unit,
    onSave: (InstallLocationPreset) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isNew = editingLocation == null
    var locationName by remember(editingLocation) { mutableStateOf(editingLocation?.name ?: "") }
    var suggestedComplexity by remember(editingLocation) {
        mutableStateOf(editingLocation?.suggestedComplexity)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = if (isNew) "Thêm Vị Trí Lắp Đặt Mới" else "Chỉnh Sửa Vị Trí Lắp Đặt",
                color = Cyan400,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tên vị trí / khu vực thi công *",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    placeholder = { Text("VD: Mặt tiền Tầng 3 - Giàn giáo ngoài") },
                    modifier = Modifier.fillMaxWidth().testTag("install_location_editor_input"),
                    maxLines = 2,
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

                Text(
                    text = "Độ khó nhân công gợi ý khi chọn vị trí này:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber400,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = suggestedComplexity == null,
                        onClick = { suggestedComplexity = null },
                        label = { Text("Giữ nguyên", fontSize = 11.sp) },
                        modifier = Modifier.heightIn(min = 40.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Cyan900,
                            selectedLabelColor = Cyan400,
                            containerColor = Slate950,
                            labelColor = Color.White
                        )
                    )
                    LaborComplexity.values().forEach { comp ->
                        val shortLabel = when (comp) {
                            LaborComplexity.EASY -> "Dễ (<3m)"
                            LaborComplexity.MEDIUM -> "TB (Giàn giáo)"
                            LaborComplexity.HARD -> "Cao (Xe cẩu)"
                        }
                        FilterChip(
                            selected = suggestedComplexity == comp,
                            onClick = { suggestedComplexity = comp },
                            label = { Text(shortLabel, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 40.dp),
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
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isNew && editingLocation != null && onDelete != null) {
                    OutlinedButton(
                        onClick = {
                            onDelete(editingLocation.id)
                            onDismiss()
                        },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("delete_location_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
                    ) {
                        Text("Xóa")
                    }
                }
                Button(
                    onClick = {
                        if (locationName.trim().isNotEmpty()) {
                            onSave(
                                InstallLocationPreset(
                                    id = editingLocation?.id ?: "custom_loc_${System.currentTimeMillis()}",
                                    name = locationName.trim(),
                                    suggestedComplexity = suggestedComplexity,
                                    isBuiltIn = editingLocation?.isBuiltIn ?: false
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = locationName.trim().isNotEmpty(),
                    modifier = Modifier.heightIn(min = 48.dp).testTag("save_location_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black)
                ) {
                    Text(if (isNew) "Thêm Vị Trí" else "Lưu Vị Trí", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
