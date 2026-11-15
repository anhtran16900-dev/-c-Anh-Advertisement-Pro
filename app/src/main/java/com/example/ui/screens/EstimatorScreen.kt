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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultData
import com.example.model.EditableCategoryOption
import com.example.model.InstallLocationPreset
import com.example.model.LaborComplexity
import com.example.model.SignageCategory
import com.example.ui.components.EstimatorAreaQuoteCard
import com.example.ui.dialogs.CategoryOptionEditorDialog
import com.example.ui.dialogs.InstallLocationEditorDialog
import com.example.ui.dialogs.ItemNamePresetEditorDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Formatters
import com.example.viewmodel.SignageViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EstimatorScreen(
    viewModel: SignageViewModel,
    onShareQuote: (String) -> Unit
) {
    val input by viewModel.estimationInput.collectAsState()
    val result by viewModel.estimationResult.collectAsState()
    val categoryOptions by viewModel.categoryOptions.collectAsState()
    val itemPresetsMap by viewModel.itemPresetsByCategoryId.collectAsState()
    val installLocations by viewModel.installLocations.collectAsState()
    val presets = itemPresetsMap[input.selectedCategoryOptionId]
        ?: DefaultData.presetItemNames[input.category]
        ?: emptyList()
    val activeCategoryOption = categoryOptions.firstOrNull { it.id == input.selectedCategoryOptionId }
        ?: categoryOptions.firstOrNull { it.baseCategory == input.category }
    val selectedLocationPreset = installLocations.firstOrNull { it.name == input.projectLocation }
    val context = LocalContext.current

    var showCategoryEditorDialog by remember { mutableStateOf(false) }
    var editingCategoryOption by remember { mutableStateOf<EditableCategoryOption?>(null) }

    var showItemPresetDialog by remember { mutableStateOf(false) }
    var editingItemPresetOldName by remember { mutableStateOf<String?>(null) }

    var showLocationEditorDialog by remember { mutableStateOf(false) }
    var editingLocationPreset by remember { mutableStateOf<InstallLocationPreset?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Section Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("estimator_input_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Card Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Cyan900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Thông Số Hạng Mục Quảng Cáo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(20.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                    ) {
                        Text(
                            text = "Bóc tách realtime",
                            style = MaterialTheme.typography.labelSmall,
                            color = Cyan400,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // 1. Category Selector (Addable & Editable)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. PHÂN LOẠI HẠNG MỤC SẢN XUẤT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    editingCategoryOption = activeCategoryOption
                                    showCategoryEditorDialog = true
                                },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("edit_category_btn"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa loại hạng mục", tint = Amber400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sửa", fontSize = 11.sp, color = Amber400, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    editingCategoryOption = null
                                    showCategoryEditorDialog = true
                                },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("add_category_btn"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Emerald400)
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm loại hạng mục", tint = Emerald400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Thêm", fontSize = 11.sp, color = Emerald400, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    var categoryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = activeCategoryOption?.displayName ?: input.category.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("category_dropdown"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            categoryOptions.forEach { option ->
                                val isSelected = option.id == input.selectedCategoryOptionId
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option.displayName,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Cyan400 else Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (!option.isBuiltIn) {
                                                Text(
                                                    text = "Tùy chỉnh",
                                                    fontSize = 10.sp,
                                                    color = Emerald400,
                                                    modifier = Modifier.padding(start = 6.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectCategoryOption(option)
                                        categoryExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "➕ Thêm loại hạng mục mới...",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald400
                                    )
                                },
                                onClick = {
                                    categoryExpanded = false
                                    editingCategoryOption = null
                                    showCategoryEditorDialog = true
                                }
                            )
                        }
                    }
                }

                // 2. Item Name (Addable & Editable Presets)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. TÊN HẠNG MỤC SẢN XUẤT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                editingItemPresetOldName = null
                                showItemPresetDialog = true
                            },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("add_item_preset_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm mẫu tên hạng mục", tint = Emerald400, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Thêm mẫu tên", fontSize = 11.sp, color = Emerald400, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedTextField(
                        value = input.itemName,
                        onValueChange = { viewModel.setItemName(it) },
                        placeholder = { Text("VD: Biển hiệu Showroom 6x2.4m...") },
                        trailingIcon = {
                            if (input.itemName.isNotBlank() && presets.none { it.equals(input.itemName.trim(), ignoreCase = true) }) {
                                IconButton(
                                    onClick = { viewModel.addItemPreset(input.itemName) },
                                    modifier = Modifier.testTag("save_current_item_preset_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "Lưu tên này vào mẫu chọn nhanh",
                                        tint = Emerald400
                                    )
                                }
                            } else if (presets.contains(input.itemName)) {
                                IconButton(
                                    onClick = {
                                        editingItemPresetOldName = input.itemName
                                        showItemPresetDialog = true
                                    },
                                    modifier = Modifier.testTag("edit_selected_item_preset_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Chỉnh sửa mẫu tên đang chọn",
                                        tint = Amber400
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("item_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan400,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Customizable Item Name Presets
                Column {
                    Text(
                        text = "⚡ Mẫu tên hạng mục (chạm chọn • bấm ✏️ để sửa/xóa):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            val isSelected = input.itemName == preset
                            Surface(
                                onClick = { viewModel.setItemName(preset) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Cyan900 else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Cyan400 else Slate700)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    IconButton(
                                        onClick = {
                                            editingItemPresetOldName = preset
                                            showItemPresetDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Sửa mẫu $preset",
                                            tint = Amber400,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Installation Location (Addable & Editable)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. VỊ TRÍ LẮP ĐẶT & THI CÔNG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (selectedLocationPreset != null) {
                                TextButton(
                                    onClick = {
                                        editingLocationPreset = selectedLocationPreset
                                        showLocationEditorDialog = true
                                    },
                                    modifier = Modifier.heightIn(min = 48.dp).testTag("edit_install_location_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa vị trí", tint = Amber400, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Sửa", fontSize = 11.sp, color = Amber400, fontWeight = FontWeight.Bold)
                                }
                            }
                            TextButton(
                                onClick = {
                                    editingLocationPreset = null
                                    showLocationEditorDialog = true
                                },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("add_install_location_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm vị trí lắp đặt", tint = Emerald400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Thêm vị trí", fontSize = 11.sp, color = Emerald400, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = input.projectLocation,
                        onValueChange = { viewModel.setProjectDetails(location = it) },
                        placeholder = { Text("VD: Mặt tiền Tầng 1, 128 Nguyễn Trãi...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Amber400, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (input.projectLocation.isNotBlank() && installLocations.none { it.name.equals(input.projectLocation.trim(), ignoreCase = true) }) {
                                IconButton(
                                    onClick = {
                                        viewModel.addOrUpdateInstallLocation(
                                            InstallLocationPreset(
                                                id = "custom_loc_${System.currentTimeMillis()}",
                                                name = input.projectLocation.trim(),
                                                suggestedComplexity = input.laborComplexity,
                                                isBuiltIn = false
                                            )
                                        )
                                    },
                                    modifier = Modifier.testTag("save_current_location_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "Lưu vị trí lắp đặt này",
                                        tint = Emerald400
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("install_location_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber400,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        installLocations.forEach { loc ->
                            val isSelected = input.projectLocation == loc.name
                            Surface(
                                onClick = { viewModel.selectInstallLocation(loc) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Amber400.copy(alpha = 0.18f) else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Amber400 else Slate700)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = loc.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Amber400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    IconButton(
                                        onClick = {
                                            editingLocationPreset = loc
                                            showLocationEditorDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Sửa vị trí ${loc.name}",
                                            tint = Cyan400,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Dimensions (W, H, Qty, Wastage)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Width
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ngang (m)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = if (input.width == 0.0) "" else input.width.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setDimensions(v, input.height)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("width_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Height
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Cao (m)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = if (input.height == 0.0) "" else input.height.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setDimensions(input.width, v)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("height_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Quantity
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(text = "Số Lượng", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.quantity.toString(),
                            onValueChange = { str ->
                                val v = str.toIntOrNull() ?: 1
                                viewModel.setQuantity(v)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("quantity_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Wastage
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(text = "% Hao Hụt", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.wastagePercent.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setWastage(v)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("wastage_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
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

                // Dynamic Category Sub-options
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate950),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        when (input.category) {
                            SignageCategory.HIFLEX -> {
                                Text(text = "Cấu Hình Bạt & Khung Sắt:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)

                                // Bạt
                                Column {
                                    Text(text = "Loại Bạt In", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "bat_khonggan_uv_m2" to "Bạt Không Gân UV",
                                            "hiflex_36em_m2" to "Hiflex Dày 0.36mm",
                                            "hiflex_dexam_m2" to "Bạt 2 Da Đế Xám",
                                            "bat_3m_trung_m2" to "Bạt 3M TQ",
                                            "hiflex_mong_m2" to "Hiflex 0.32mm"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.hiflexMaterialKey == k,
                                                onClick = { viewModel.updateSubOptions(hiflexKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }

                                // Khung sắt
                                Column {
                                    Text(text = "Khung Thép Kẽm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "steel_vietthanh_30" to "Thép Việt Thành 30x30",
                                            "steel_hoaphat_30" to "Thép Hòa Phát 30x30 1.4mm",
                                            "steel_20_20" to "Thép Kẽm 20x20",
                                            "none" to "Không Dùng Khung"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.steelTypeKey == k,
                                                onClick = { viewModel.updateSubOptions(steelKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }

                                // V Nẹp
                                Column {
                                    Text(text = "Nẹp Viền", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "v_nhom_m" to "V Nhôm Nẹp Viền",
                                            "v_inox_silver_m" to "V Inox Trắng",
                                            "none" to "Không Nẹp"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.vTrimTypeKey == k,
                                                onClick = { viewModel.updateSubOptions(vTrimKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            SignageCategory.ALU_LETTER -> {
                                Text(text = "Cấu Hình Mặt Dựng Alu & Chữ Nổi:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)

                                // Alu brand
                                Column {
                                    Text(text = "Thương Hiệu Alu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "alu_alcorest_pet_360k" to "Alu Alcorest PET 3mm",
                                            "alu_trieuchen_pvdf_650k" to "Alu Triều Chen PVDF",
                                            "alu_vertu_350k" to "Alu Vertu Giá Xưởng"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.aluBrandKey == k,
                                                onClick = { viewModel.updateSubOptions(aluKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }

                                // Letter
                                Column {
                                    Text(text = "Mặt Chữ Nổi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "inox_304_gold_m2" to "Inox 304 Vàng Gương",
                                            "inox_304_silver_m2" to "Inox Trắng Xước",
                                            "mica_dailoan_3mm_sheet" to "Mica Đài Loan FS 3mm",
                                            "none" to "Không Chữ"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.letterMaterialKey == k,
                                                onClick = { viewModel.updateSubOptions(letterKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }

                                // LED
                                Column {
                                    Text(text = "Đèn LED Chiếu Sáng", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(
                                            "led_module_samsung" to "LED Samsung NC Korea 1.2W",
                                            "led_module_3mat_co" to "LED 3 Mắt Có Len TQ",
                                            "none" to "Không Dùng LED"
                                        ).forEach { (k, label) ->
                                            FilterChip(
                                                selected = input.ledBrandKey == k,
                                                onClick = { viewModel.updateSubOptions(ledKey = k) },
                                                label = { Text(label, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Cyan900,
                                                    selectedLabelColor = Cyan400,
                                                    containerColor = Slate900,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            SignageCategory.DECAL -> {
                                Text(text = "Chất Liệu Decal & Màng Cán:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        "decal_trang_m2" to "Decal Trắng Sữa",
                                        "decal_trong_m2" to "Decal Trong Kính",
                                        "decal_luoi_m2" to "Decal Lưới 1 Chiều",
                                        "decal_3m_m2" to "Decal 3M Cao Cấp"
                                    ).forEach { (k, label) ->
                                        FilterChip(
                                            selected = input.decalTypeKey == k,
                                            onClick = { viewModel.updateSubOptions(decalKey = k) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Cyan900,
                                                selectedLabelColor = Cyan400,
                                                containerColor = Slate900,
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            SignageCategory.TARPAULIN -> {
                                Text(text = "Chủng Loại Bạt Che & Công Trình:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        "bat_xanhcam_150g_m2" to "Xanh Cam 150g",
                                        "bat_xanhcam_200g_m2" to "Xanh Cam 200g Dày",
                                        "bat_dua_3soc_m2" to "Bạt Dứa Sọc 3 Màu"
                                    ).forEach { (k, label) ->
                                        FilterChip(
                                            selected = input.tarpaulinTypeKey == k,
                                            onClick = { viewModel.updateSubOptions(tarpaulinKey = k) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Cyan900,
                                                selectedLabelColor = Cyan400,
                                                containerColor = Slate900,
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            SignageCategory.CNC_LASER -> {
                                Text(text = "Độ Dày Tấm Mica FS & Hộp Đèn:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        "mica_dailoan_3mm_sheet" to "Mica FS 3mm",
                                        "mica_dailoan_5mm_sheet" to "Mica FS 5mm Dày",
                                        "mica_dailoan_2mm_sheet" to "Mica FS 2mm Mỏng"
                                    ).forEach { (k, label) ->
                                        FilterChip(
                                            selected = input.cncMicaKey == k,
                                            onClick = { viewModel.updateSubOptions(cncMicaKey = k) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Cyan900,
                                                selectedLabelColor = Cyan400,
                                                containerColor = Slate900,
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Work & Margin Parameters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Complexity
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(text = "Thi Công Lắp Đặt", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        var laborMenuOpen by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = laborMenuOpen,
                            onExpandedChange = { laborMenuOpen = !laborMenuOpen }
                        ) {
                            OutlinedTextField(
                                value = when (input.laborComplexity) {
                                    LaborComplexity.EASY -> "Dưới 3m, đất"
                                    LaborComplexity.MEDIUM -> "Tầng 1-2, giàn giáo"
                                    LaborComplexity.HARD -> "Đu dây, cẩu xe"
                                },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = laborMenuOpen) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan400,
                                    unfocusedBorderColor = Slate700,
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = laborMenuOpen,
                                onDismissRequest = { laborMenuOpen = false },
                                modifier = Modifier.background(Slate900)
                            ) {
                                LaborComplexity.values().forEach { comp ->
                                    DropdownMenuItem(
                                        text = { Text(comp.displayName, style = MaterialTheme.typography.bodySmall, color = Color.White) },
                                        onClick = {
                                            viewModel.setLaborComplexity(comp)
                                            laborMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Profit Margin
                    Column(modifier = Modifier.weight(0.9f)) {
                        Text(text = "% Lợi Nhuận", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.profitMarginPercent.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setProfitMargin(v)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("profit_margin_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Emerald400),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Shipping & Hardware
                    Column(modifier = Modifier.weight(1.1f)) {
                        Text(text = "Vận Chuyển (đ)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.shippingAndHardwareFee.toLong().toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setShippingFee(v)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("shipping_fee_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
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
            }
        }

        // Summary m2 & Quotation Card
        EstimatorAreaQuoteCard(
            input = input,
            result = result,
            onToggleMinOneM2 = { viewModel.setQuoteCalculationOptions(minOneM2PerPiece = it) },
            onDiscountChange = { viewModel.setQuoteCalculationOptions(discountPercent = it) },
            onVatChange = { viewModel.setQuoteCalculationOptions(vatPercent = it) },
            onCustomUnitPriceChange = { price, clear ->
                viewModel.setQuoteCalculationOptions(
                    customUnitPriceOverridePerM2 = price,
                    clearCustomUnitPrice = clear
                )
            },
            onSaveToProject = { viewModel.saveCurrentToProject() },
            onExportPdf = {
                com.example.util.ProjectExportManager.sharePdfQuotation(context, input, result)
            },
            onShareZalo = {
                onShareQuote(buildQuotationText(input, result))
            },
            onCopyQuote = {
                val text = buildQuotationText(input, result)
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("BaoGiaBocTach", text))
                viewModel.notifyMessage("Đã copy bảng tính m² & báo giá vào bộ nhớ tạm!")
            }
        )

        // AI Engineering Suggestions
        if (result.aiEngineeringTips.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Amber400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI ĐỀ XUẤT KẾT CẤU KỸ THUẬT XƯỞNG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Amber400,
                            letterSpacing = 1.sp
                        )
                    }

                    result.aiEngineeringTips.forEach { tip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate950, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "💡", fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(text = tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // Material Breakdown Table
        Card(
            modifier = Modifier.fillMaxWidth().testTag("breakdown_table_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ListAlt, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bóc Tách Chi Tiết Phôi & Vật Tư",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${result.breakdownItems.size} mục",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate950, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Vật tư / Khoản mục", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.8f))
                    Text(text = "ĐVT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    Text(text = "SL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                    Text(text = "Thành Tiền", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                }

                // Table Rows
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    result.breakdownItems.forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (idx % 2 == 0) Slate850.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.weight(1.8f)
                            )
                            Text(
                                text = item.unit,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(0.7f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = Formatters.formatNumber(item.quantity),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Cyan400,
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = Formatters.formatVnd(item.totalPrice),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400,
                                modifier = Modifier.weight(1.3f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCategoryEditorDialog) {
        CategoryOptionEditorDialog(
            editingOption = editingCategoryOption,
            onDismiss = { showCategoryEditorDialog = false },
            onSave = { option -> viewModel.addOrUpdateCategoryOption(option) },
            onDelete = { id -> viewModel.deleteCategoryOption(id) }
        )
    }

    if (showItemPresetDialog) {
        ItemNamePresetEditorDialog(
            initialName = editingItemPresetOldName,
            categoryLabel = activeCategoryOption?.shortName ?: input.category.shortName,
            onDismiss = { showItemPresetDialog = false },
            onSave = { newName ->
                val old = editingItemPresetOldName
                if (old == null) {
                    viewModel.addItemPreset(newName)
                } else {
                    viewModel.updateItemPreset(old, newName)
                }
            },
            onDelete = editingItemPresetOldName?.let { target ->
                { viewModel.deleteItemPreset(target) }
            }
        )
    }

    if (showLocationEditorDialog) {
        InstallLocationEditorDialog(
            editingLocation = editingLocationPreset,
            onDismiss = { showLocationEditorDialog = false },
            onSave = { preset -> viewModel.addOrUpdateInstallLocation(preset) },
            onDelete = { id -> viewModel.deleteInstallLocation(id) }
        )
    }
}

private fun buildQuotationText(
    input: com.example.model.EstimationInput,
    result: com.example.model.EstimationResult
): String {
    val sb = StringBuilder()
    sb.append("📋 BẢNG BÁO GIÁ HẠNG MỤC QUẢNG CÁO\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("Đơn vị: Đức Anh Advertisement PRO\n")
    sb.append("Loại hạng mục: ${result.effectiveCategoryName}\n")
    sb.append("Hạng mục: ${result.itemName}\n")
    if (input.projectLocation.isNotBlank()) {
        sb.append("Vị trí lắp đặt: ${input.projectLocation}\n")
    }
    sb.append("Quy cách: ${result.width}m x ${result.height}m (SL: ${result.quantity})\n")
    sb.append("DT thực tế: ${Formatters.formatArea(result.netArea)} | Tính tiền: ${Formatters.formatArea(result.billableArea)}\n\n")
    sb.append("CHI TIẾT VẬT TƯ & SẢN XUẤT:\n")
    result.breakdownItems.forEach {
        sb.append("• ${it.name}: ${Formatters.formatNumber(it.quantity)} ${it.unit} -> ${Formatters.formatVnd(it.totalPrice)}\n")
    }
    sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("Đơn giá / m²: ${Formatters.formatVnd(result.unitPricePerM2)} / m²\n")
    if (result.discountAmount > 0.0) {
        sb.append("Tạm tính: ${Formatters.formatVnd(result.subtotalCustomerPrice)}\n")
        sb.append("Chiết khấu (${input.discountPercent.toInt()}%): -${Formatters.formatVnd(result.discountAmount)}\n")
    }
    if (result.vatAmount > 0.0) {
        sb.append("Thuế VAT (${input.vatPercent.toInt()}%): +${Formatters.formatVnd(result.vatAmount)}\n")
    }
    sb.append("TỔNG GIÁ XUẤT: ${Formatters.formatVnd(result.finalCustomerPrice)}\n")
    return sb.toString()
}
