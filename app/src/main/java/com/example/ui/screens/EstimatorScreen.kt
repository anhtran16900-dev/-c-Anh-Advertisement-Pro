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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
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

    val activeCategoryOption = categoryOptions.firstOrNull {
        it.id == input.selectedCategoryOptionId
    } ?: categoryOptions.firstOrNull {
        it.baseCategory == input.category
    }

    val matchingItemPreset = presets.firstOrNull {
        it.trim().equals(input.itemName.trim(), ignoreCase = true)
    }

    val selectedLocationPreset = installLocations.firstOrNull {
        it.name.trim().equals(input.projectLocation.trim(), ignoreCase = true)
    }

    val context = LocalContext.current

    // Giữ nguyên chuỗi nhập trong lúc người dùng đang gõ.
    var widthFocused by remember { mutableStateOf(false) }
    var heightFocused by remember { mutableStateOf(false) }
    var quantityFocused by remember { mutableStateOf(false) }
    var wastageFocused by remember { mutableStateOf(false) }
    var profitMarginFocused by remember { mutableStateOf(false) }
    var shippingFeeFocused by remember { mutableStateOf(false) }

    var widthText by remember { mutableStateOf(input.width.toEditableNumber()) }
    var heightText by remember { mutableStateOf(input.height.toEditableNumber()) }
    var quantityText by remember { mutableStateOf(input.quantity.toString()) }
    var wastageText by remember { mutableStateOf(input.wastagePercent.toEditableNumber()) }
    var profitMarginText by remember { mutableStateOf(input.profitMarginPercent.toEditableNumber()) }
    var shippingFeeText by remember {
        mutableStateOf(input.shippingAndHardwareFee.toLong().toString())
    }

    // Đồng bộ dữ liệu từ ViewModel nhưng không làm mất nội dung đang nhập.
    LaunchedEffect(input.width, widthFocused) {
        if (!widthFocused) widthText = input.width.toEditableNumber()
    }
    LaunchedEffect(input.height, heightFocused) {
        if (!heightFocused) heightText = input.height.toEditableNumber()
    }
    LaunchedEffect(input.quantity, quantityFocused) {
        if (!quantityFocused) quantityText = input.quantity.toString()
    }
    LaunchedEffect(input.wastagePercent, wastageFocused) {
        if (!wastageFocused) wastageText = input.wastagePercent.toEditableNumber()
    }
    LaunchedEffect(input.profitMarginPercent, profitMarginFocused) {
        if (!profitMarginFocused) {
            profitMarginText = input.profitMarginPercent.toEditableNumber()
        }
    }
    LaunchedEffect(input.shippingAndHardwareFee, shippingFeeFocused) {
        if (!shippingFeeFocused) {
            shippingFeeText = input.shippingAndHardwareFee.toLong().toString()
        }
    }

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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("estimator_input_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Slate800)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

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
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(18.dp)
                            )
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
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                        )
                    ) {
                        Text(
                            text = "Bóc tách realtime",
                            style = MaterialTheme.typography.labelSmall,
                            color = Cyan400,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }

                // 1. Category Selector
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
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("edit_category_btn"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Slate700)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Sửa loại hạng mục",
                                    tint = Amber400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Sửa",
                                    fontSize = 11.sp,
                                    color = Amber400,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    editingCategoryOption = null
                                    showCategoryEditorDialog = true
                                },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("add_category_btn"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Emerald400)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Thêm loại hạng mục",
                                    tint = Emerald400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Thêm",
                                    fontSize = 11.sp,
                                    color = Emerald400,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    var categoryExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = {
                            categoryExpanded = !categoryExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = activeCategoryOption?.displayName
                                ?: input.category.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = categoryExpanded
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("category_dropdown"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
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
                                val isSelected =
                                    option.id == input.selectedCategoryOptionId

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
                                                fontWeight = if (isSelected) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Normal
                                                },
                                                color = if (isSelected) {
                                                    Cyan400
                                                } else {
                                                    Color.White
                                                },
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
                                        fontWeigh
                // 2. Item Name - Tên hạng mục
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
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("add_item_preset_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm mẫu tên",
                                tint = Emerald400,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "Thêm mẫu tên",
                                fontSize = 11.sp,
                                color = Emerald400,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = input.itemName,
                        onValueChange = { viewModel.setItemName(it) },
                        placeholder = {
                            Text("VD: Biển hiệu Showroom 6x2.4m...")
                        },
                        trailingIcon = {
                            if (input.itemName.isNotBlank() && matchingItemPreset == null) {
                                IconButton(
                                    onClick = {
                                        viewModel.addItemPreset(input.itemName.trim())
                                    },
                                    modifier = Modifier.testTag("save_current_item_preset_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "Lưu mẫu tên",
                                        tint = Emerald400
                                    )
                                }
                            } else if (matchingItemPreset != null) {
                                IconButton(
                                    onClick = {
                                        editingItemPresetOldName = matchingItemPreset
                                        showItemPresetDialog = true
                                    },
                                    modifier = Modifier.testTag("edit_selected_item_preset_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Sửa mẫu tên",
                                        tint = Amber400
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_name_input"),
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

                // Mẫu tên hạng mục
                Column {
                    Text(
                        text = "⚡ Mẫu tên hạng mục (chạm chọn • bấm ✏️ để sửa)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            val isSelected = input.itemName.equals(
                                preset,
                                ignoreCase = true
                            )

                            Surface(
                                onClick = { viewModel.setItemName(preset) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Cyan900 else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isSelected) Cyan400 else Slate700
                                    )
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 4.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) {
                                            Cyan400
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
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

                // 3. Installation Location
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
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("edit_install_location_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Sửa vị trí",
                                        tint = Amber400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        "Sửa",
                                        fontSize = 11.sp,
                                        color = Amber400,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            TextButton(
                                onClick = {
                                    editingLocationPreset = null
                                    showLocationEditorDialog = true
                                },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("add_install_location_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Thêm vị trí",
                                    tint = Emerald400,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "Thêm vị trí",
                                    fontSize = 11.sp,
                                    color = Emerald400,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = input.projectLocation,
                        onValueChange = {
                            viewModel.setProjectDetails(location = it)
                        },
                        placeholder = {
                            Text("VD: Mặt tiền tầng 1, 128 Nguyễn Trãi...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Amber400,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (
                                input.projectLocation.isNotBlank() &&
                                selectedLocationPreset == null
                            ) {
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
                                        contentDescription = "Lưu vị trí",
                                        tint = Emerald400
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("install_location_input"),
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
                            val isSelected = input.projectLocation.equals(
                                loc.name,
                                ignoreCase = true
                            )

                            Surface(
                                onClick = {
                                    viewModel.selectInstallLocation(loc)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) {
                                    Amber400.copy(alpha = 0.18f)
                                } else {
                                    Slate950
                                },
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isSelected) Amber400 else Slate700
                                    )
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 4.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    )
                                ) {
                                    Text(
                                        text = loc.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) {
                                            Amber400
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
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

                // 4. Dimensions - Kích thước và số lượng
                Text(
                    text = "4. KÍCH THƯỚC & SỐ LƯỢNG",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Cyan400,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Width
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ngang (m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = widthText,
                            onValueChange = { value ->
                                widthText = value.filter {
                                    it.isDigit() || it == '.' || it == ','
                                }
                                widthText.toDoubleOrNullSafe()?.let { v ->
                                    viewModel.setDimensions(
                                        v.coerceAtLeast(0.0),
                                        input.height
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("width_input")
                                .onFocusChanged {
                                    widthFocused = it.isFocused
                                },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                // 5. CẤU HÌNH VẬT TƯ THEO HẠNG MỤC
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Slate950
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Slate800)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (input.category) {

                            // =========================
                            // HIFLEX - BẠT VÀ KHUNG SẮT
                            // =========================
                            SignageCategory.HIFLEX -> {
                                Text(
                                    text = "Cấu Hình Bạt & Khung Sắt:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan400
                                )

                                // Loại bạt in
                                Column {
                                    Text(
                                        text = "Loại Bạt In",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "bat_khonggan_uv_m2" to "Bạt Không Gân UV",
                                            "hiflex_36em_m2" to "Hiflex Dày 0.36mm",
                                            "hiflex_dexam_m2" to "Bạt 2 Da Đế Xám",
                                            "bat_3m_trung_m2" to "Bạt 3M TQ",
                                            "hiflex_mong_m2" to "Hiflex 0.32mm"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.hiflexMaterialKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        hiflexKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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

                                // Khung thép
                                Column {
                                    Text(
                                        text = "Khung Thép Kẽm",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "steel_vietthanh_30" to "Thép Việt Thành 30x30",
                                            "steel_hoaphat_30" to "Thép Hòa Phát 30x30 1.4mm",
                                            "steel_20_20" to "Thép Kẽm 20x20",
                                            "none" to "Không Dùng Khung"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.steelTypeKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        steelKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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

                                // Nẹp viền
                                Column {
                                    Text(
                                        text = "Nẹp Viền",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.horizontalScroll(
                                            rememberScrollState()
                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "v_nhom_m" to "V Nhôm Nẹp Viền",
                                            "v_inox_silver_m" to "V Inox Trắng",
                                            "none" to "Không Nẹp"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.vTrimTypeKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        vTrimKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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

                            // =========================
                            // ALU - CHỮ NỔI
                            // =========================
                            SignageCategory.ALU_LETTER -> {
                                Text(
                                    text = "Cấu Hình Mặt Dựng Alu & Chữ Nổi:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan400
                                )

                                // Thương hiệu Alu
                                Column {
                                    Text(
                                        text = "Thương Hiệu Alu",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.horizontalScroll(
                                            rememberScrollState()
                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "alu_alcorest_pet_360k" to "Alu Alcorest PET 3mm",
                                            "alu_trieuchen_pvdf_650k" to "Alu Triều Chen PVDF",
                                            "alu_vertu_350k" to "Alu Vertu Giá Xưởng"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.aluBrandKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        aluKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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

                                // Mặt chữ nổi
                                Column {
                                    Text(
                                        text = "Mặt Chữ Nổi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.horizontalScroll(
                                            rememberScrollState()
                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "inox_304_gold_m2" to "Inox 304 Vàng Gương",
                                            "inox_304_silver_m2" to "Inox Trắng Xước",
                                            "mica_dailoan_3mm_sheet" to "Mica Đài Loan FS 3mm",
                                            "none" to "Không Chữ"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.letterMaterialKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        letterKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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
                                    Text(
                                        text = "Đèn LED Chiếu Sáng",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.horizontalScroll(
                                            rememberScrollState()
                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "led_module_samsung" to "LED Samsung NC Korea 1.2W",
                                            "led_module_3mat_co" to "LED 3 Mắt Có Len TQ",
                                            "none" to "Không Dùng LED"
                                        ).forEach { (key, label) ->
                                            FilterChip(
                                                selected = input.ledBrandKey == key,
                                                onClick = {
                                                    viewModel.updateSubOptions(
                                                        ledKey = key
                                                    )
                                                },
                                                label = {
                                                    Text(label, fontSize = 11.sp)
                                                },
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

                            // =========================
                            // DECAL
                            // =========================
                            SignageCategory.DECAL -> {
                                Text(
                                    text = "Chất Liệu Decal & Màng Cán:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan400
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "decal_trang_m2" to "Decal Trắng Sữa",
                                        "decal_trong_m2" to "Decal Trong Kính",
                                        "decal_luoi_m2" to "Decal Lưới 1 Chiều",
                                        "decal_3m_m2" to "Decal 3M Cao Cấp"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = input.decalTypeKey == key,
                                            onClick = {
                                                viewModel.updateSubOptions(
                                                    decalKey = key
                                                )
                                            },
                                            label = {
                                                Text(label, fontSize = 11.sp)
                                            },
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

                            // =========================
                            // BẠT CÔNG TRÌNH
                            // =========================
                            SignageCategory.TARPAULIN -> {
                                Text(
                                    text = "Chủng Loại Bạt Che & Công Trình:",
                                    style = MaterialTheme.typography.labelSmall,
                // 6. WORK & MARGIN PARAMETERS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Slate950
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Slate800)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "6. NHÂN CÔNG & CHI PHÍ SẢN XUẤT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Complexity
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = "Thi Công Lắp Đặt",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                var laborMenuOpen by remember {
                                    mutableStateOf(false)
                                }

                                ExposedDropdownMenuBox(
                                    expanded = laborMenuOpen,
                                    onExpandedChange = {
                                        laborMenuOpen = !laborMenuOpen
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = when (input.laborComplexity) {
                                            LaborComplexity.EASY -> "Dưới 3m, đất"
                                            LaborComplexity.MEDIUM -> "Tầng 1-2, giàn giáo"
                                            LaborComplexity.HARD -> "Đu dây, cẩu xe"
                                        },
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = laborMenuOpen
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        textStyle = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White
                                        ),
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
                                        onDismissRequest = {
                                            laborMenuOpen = false
                                        },
                                        modifier = Modifier.background(Slate900)
                                    ) {
                                        LaborComplexity.values().forEach { comp ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        comp.displayName,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White
                                                    )
                                                },
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
                                Text(
                                    text = "% Lợi Nhuận",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                OutlinedTextField(
                                    value = profitMarginText,
                                    onValueChange = { str ->
                                        profitMarginText = str

                                        if (str.isBlank()) {
                                            viewModel.setProfitMargin(0.0)
                                        } else {
                                            parseFlexibleDouble(str)
                                                ?.takeIf { it >= 0.0 }
                                                ?.let(viewModel::setProfitMargin)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profit_margin_input")
                                        .onFocusChanged { focus ->
                                            profitMarginFocused = focus.isFocused

                                            if (
                                                !focus.isFocused &&
                                                profitMarginText.isNotBlank() &&
                                                parseFlexibleDouble(profitMarginText)
                                                    ?.let { it >= 0.0 } != true
                                            ) {
                                                profitMarginText =
                                                    input.profitMarginPercent.toEditableNumber()
                                            }
                                        },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    ),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald400
                                    ),
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
                                Text(
                                    text = "Vận Chuyển (đ)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                OutlinedTextField(
                                    value = shippingFeeText,
                                    onValueChange = { str ->
                                        shippingFeeText = str

                                        if (str.isNotBlank()) {
                                            str.trim()
                                                .toLongOrNull()
                                                ?.takeIf { it >= 0L }
                                                ?.let { value ->
                                                    viewModel.setShippingFee(
                                                        value.toDouble()
                                                    )
                                                }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("shipping_fee_input")
                                        .onFocusChanged { focus ->
                                            shippingFeeFocused = focus.isFocused

                                            if (!focus.isFocused) {
                                                if (shippingFeeText.isBlank()) {
                                                    viewModel.setShippingFee(0.0)
                                                    shippingFeeText = "0"
                                                } else if (
                                                    shippingFeeText.toLongOrNull()
                                                        ?.let { it >= 0L } != true
                                                ) {
                                                    shippingFeeText =
                                                        input.shippingAndHardwareFee
                                                            .toLong().toString()
                                                }
                                            }
                                        },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
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
                    }
                }

                // 7. AREA SUMMARY & QUOTATION
                EstimatorAreaQuoteCard(
                    input = input,
                    result = result,
                    onToggleMinOneM2 = {
                        viewModel.setQuoteCalculationOptions(
                            minOneM2PerPiece = it
                        )
                    },
                    onDiscountChange = {
                        viewModel.setQuoteCalculationOptions(
                            discountPercent = it
                        )
                    },
                    onVatChange = {
                        viewModel.setQuoteCalculationOptions(
                            vatPercent = it
                        )
                    },
                    onCustomUnitPriceChange = { price, clear ->
                        viewModel.setQuoteCalculationOptions(
                            customUnitPriceOverridePerM2 = price,
                            clearCustomUnitPrice = clear
                        )
                    },
                    onSaveToProject = {
                        viewModel.saveCurrentToProject()
                    },
                    onExportPdf = {
                        com.example.util.ProjectExportManager.sharePdfQuotation(
                            context,
                            input,
                            result
                        )
                    },
                    onShareZalo = {
                        onShareQuote(
                            buildQuotationText(input, result)
                        )
                    },
                    onCopyQuote = {
                        val text = buildQuotationText(input, result)

                        val cm = context.getSystemService(
                            Context.CLIPBOARD_SERVICE
                        ) as? ClipboardManager

                        if (cm != null) {
                            cm.setPrimaryClip(
                                ClipData.newPlainText(
                                    "BaoGiaBocTach",
                                    text
                                )
                            )

                            viewModel.notifyMessage(
                                "Đã copy bảng tính m² & báo giá vào bộ nhớ tạm!"
                            )
                        } else {
                            viewModel.notifyMessage(
                                "Không thể truy cập bộ nhớ tạm trên thiết bị này."
                            )
                        }
                    }
                )

                // 8. AI ENGINEERING SUGGESTIONS
                if (result.aiEngineeringTips.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Slate900
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Slate800)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(16.dp)
                                )

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
                                        .background(
                                            Slate950,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "💡",
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                
    // 9. CATEGORY EDITOR
    if (showCategoryEditorDialog) {
        CategoryOptionEditorDialog(
            editingOption = editingCategoryOption,
            onDismiss = {
                showCategoryEditorDialog = false
            },
            onSave = { option ->
                viewModel.addOrUpdateCategoryOption(option)
                showCategoryEditorDialog = false
            },
            onDelete = { id ->
                viewModel.deleteCategoryOption(id)
                showCategoryEditorDialog = false
            }
        )
    }

    // 10. ITEM PRESET EDITOR
    if (showItemPresetDialog) {
        ItemNamePresetEditorDialog(
            initialName = editingItemPresetOldName,
            categoryLabel = activeCategoryOption?.shortName
                ?: input.category.shortName,
            onDismiss = {
                showItemPresetDialog = false
            },
            onSave = { newName ->
                val old = editingItemPresetOldName

                if (old == null) {
                    viewModel.addItemPreset(newName)
                } else {
                    viewModel.updateItemPreset(old, newName)
                }

                showItemPresetDialog = false
            },
            onDelete = editingItemPresetOldName?.let { target ->
                {
                    viewModel.deleteItemPreset(target)
                    showItemPresetDialog = false
                }
            }
        )
    }

    // 11. INSTALL LOCATION EDITOR
    if (showLocationEditorDialog) {
        InstallLocationEditorDialog(
            editingLocation = editingLocationPreset,
            onDismiss = {
                showLocationEditorDialog = false
            },
            onSave = { preset ->
                viewModel.addOrUpdateInstallLocation(preset)
                showLocationEditorDialog = false
            },
            onDelete = { id ->
                viewModel.deleteInstallLocation(id)
                showLocationEditorDialog = false
            }
        )
    }
}

// =====================================================
// 12. BUILD PROFESSIONAL QUOTATION TEXT
// =====================================================

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

    sb.append(
        "Quy cách: ${
            Formatters.formatNumber(result.width)
        }m x ${
            Formatters.formatNumber(result.height)
        }m (SL: ${result.quantity})\n"
    )

    sb.append(
        "DT thực tế: ${
            Formatters.formatArea(result.netArea)
        } | Tính tiền: ${
            Formatters.formatArea(result.billableArea)
        }\n\n"
    )

    sb.append("CHI TIẾT VẬT TƯ & SẢN XUẤT:\n")

    result.breakdownItems.forEach { item ->
        sb.append(
            "• ${item.name}: ${
                Formatters.formatNumber(item.quantity)
            } ${item.unit} -> ${
                Formatters.formatVnd(item.totalPrice)
            }\n"
        )
    }

    sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

    sb.append(
        "Đơn giá / m²: ${
            Formatters.formatVnd(result.unitPricePerM2)
        } / m²\n"
    )

    if (result.discountAmount > 0.0) {
        sb.append(
            "Tạm tính: ${
                Formatters.formatVnd(result.subtotalCustomerPrice)
            }\n"
        )

        sb.append(
            "Chiết khấu (${
                Formatters.formatNumber(input.discountPercent)
            }%): -${
                Formatters.formatVnd(result.discountAmount)
            }\n"
        )
    }

    if (result.vatAmount > 0.0) {
        sb.append(
            "Thuế VAT (${
                Formatters.formatNumber(input.vatPercent)
            }%): +${
                Formatters.formatVnd(result.vatAmount)
            }\n"
        )
    }

    sb.append(
        "TỔNG GIÁ XUẤT: ${
            Formatters.formatVnd(result.finalCustomerPrice)
        }\n"
    )

    return sb.toString()
}

// =====================================================
// 13. SAFE DECIMAL PARSER
// =====================================================

/**
 * Chấp nhận cả dấu phẩy và dấu chấm thập phân.
 * Không chuyển chuỗi đang nhập dở thành số 0.
 */
private fun parseFlexibleDouble(raw: String): Double? {

    val normalized = raw.trim().replace(',', '.')

    if (
        normalized.isEmpty() ||
        normalized == "." ||
        normalized == "-" ||
        normalized == "-." ||
        normalized.endsWith('.')
    ) {
        return null
    }

    return normalized.toDoubleOrNull()
        ?.takeIf { it.isFinite() }
}

// =====================================================
// 14. FORMAT EDITABLE NUMBER
// =====================================================

/**
 * Hiển thị số gọn gàng trong ô nhập liệu.
 */
private fun Double.toEditableNumber(): String {

    if (!isFinite() || this == 0.0) {
        return ""
    }

    return toString().removeSuffix(".0")
}

                                    
         
