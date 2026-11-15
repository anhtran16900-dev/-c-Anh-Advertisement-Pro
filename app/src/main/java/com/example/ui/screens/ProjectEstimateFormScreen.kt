package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultData
import com.example.model.LaborComplexity
import com.example.model.MaterialItem
import com.example.model.SignageCategory
import com.example.ui.components.GeminiMaterialSuggestionCard
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Purple500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.ExportPackage
import com.example.util.Formatters
import com.example.util.ProjectExportManager
import com.example.viewmodel.SignageViewModel
import java.util.Locale
import kotlin.math.ceil

import androidx.compose.foundation.layout.heightIn
import com.example.model.BoardDesignFamily

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectEstimateFormScreen(
    viewModel: SignageViewModel,
    onNavigateToBreakdown: () -> Unit,
    onOpenBoardDesigner: (BoardDesignFamily) -> Unit = {}
) {
    val input by viewModel.estimationInput.collectAsState()
    val result by viewModel.estimationResult.collectAsState()
    val catalog by viewModel.materialCatalog.collectAsState()
    val unitPrices by viewModel.unitPrices.collectAsState()
    val projectItems by viewModel.projectItems.collectAsState()
    val advertisingDescription by viewModel.advertisingDescription.collectAsState()
    val aiSuggestionState by viewModel.aiSuggestionState.collectAsState()
    val itemPresetsMap by viewModel.itemPresetsByCategoryId.collectAsState()
    val installLocations by viewModel.installLocations.collectAsState()
    val presets = itemPresetsMap[input.selectedCategoryOptionId]
        ?: DefaultData.presetItemNames[input.category]
        ?: emptyList()
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    // State for Exposed Dropdown Menu
    var isMaterialDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("Tất Cả") }

    // Dialog state for Adding & Editing Material
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var showEditMaterialDialog by remember { mutableStateOf(false) }

    // Dialog state for "Xuất Tất Cả File"
    var showExportAllDialog by remember { mutableStateOf(false) }
    var exportPackage by remember { mutableStateOf<ExportPackage?>(null) }

    // Local checklist checked state for inspection
    val checkedChecklistItems = remember { mutableStateMapOf<String, Boolean>() }

    // Active material unit price
    val activeMaterialUnitPrice = input.customMaterialPrice
        ?: (if (input.selectedMaterialKey.isNotBlank()) unitPrices[input.selectedMaterialKey] else null)
        ?: 360000.0

    val currentSelectedMaterial: MaterialItem? = catalog.firstOrNull { it.priceKey == input.selectedMaterialKey }
        ?: catalog.firstOrNull { it.brand.contains("Alu", ignoreCase = true) }

    // Realtime calculated values
    val calculatedNetArea = input.width * input.height * input.quantity
    val calculatedGrossArea = calculatedNetArea * (1.0 + (input.wastagePercent / 100.0))
    val isSheetBased = input.selectedMaterialUnit.contains("Tấm", ignoreCase = true) || input.category == SignageCategory.ALU_LETTER || input.category == SignageCategory.CNC_LASER
    val calculatedSheetCount = ceil(calculatedGrossArea / 2.9768).toInt().coerceAtLeast(1)
    val calculatedFaceCost = if (isSheetBased) {
        calculatedSheetCount * activeMaterialUnitPrice
    } else {
        calculatedGrossArea * activeMaterialUnitPrice
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // FORM TITLE & RESET HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("estimate_form_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(Cyan400, Color(0xFF3B82F6)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Assignment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biểu Mẫu Bóc Tách Dự Toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tự động tính tổng giá theo đơn giá & kích thước (Dài x Rộng), xuất trọn bộ file",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.resetForm() },
                    modifier = Modifier.testTag("reset_form_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Làm mới", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // QUICK ACCESS TO 3 DEDICATED BOARD DESIGN & m2 QUOTE STUDIOS
        Card(
            modifier = Modifier.fillMaxWidth().testTag("quick_board_studios_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Cyan400.copy(alpha = 0.4f)))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3 Xưởng Thiết Kế Bảng, Tính m² & Báo Giá Chuyên Nghiệp",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Cyan400
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoardDesignFamily.entries.forEach { fam ->
                        OutlinedButton(
                            onClick = { onOpenBoardDesigner(fam) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("quick_open_${fam.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                        ) {
                            Text(
                                text = fam.shortTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // SECTION 1: Project & Client Details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("project_details_form_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Amber400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1. THÔNG TIN DỰ ÁN & KHÁCH HÀNG",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Amber400,
                        letterSpacing = 1.sp
                    )
                }

                // Project / Item Title
                Column {
                    Text(text = "Tên Hạng Mục / Công Trình *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = input.itemName,
                        onValueChange = { viewModel.setProjectDetails(itemName = it) },
                        placeholder = { Text("VD: Biển hiệu Showroom VinFast, Pano ngoài trời...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_item_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber400,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Preset Name Suggestions
                if (presets.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.take(3).forEach { suggestion ->
                            Surface(
                                onClick = { viewModel.setItemName(suggestion) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (input.itemName == suggestion) Amber400.copy(alpha = 0.2f) else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (input.itemName == suggestion) Amber400 else Slate800)
                                )
                            ) {
                                Text(
                                    text = "+ $suggestion",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (input.itemName == suggestion) Amber400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // Client Name & Installation Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Khách Hàng / Đơn Vị", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.clientName,
                            onValueChange = { viewModel.setProjectDetails(clientName = it) },
                            placeholder = { Text("Anh Hoàng / Cty ABC") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_client_name"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Amber400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Tiến Độ / Ngày Lắp", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.installationDate,
                            onValueChange = { viewModel.setProjectDetails(installationDate = it) },
                            placeholder = { Text("15/11/2026") },
                            leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_installation_date"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Amber400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Location / Address
                Column {
                    Text(text = "Địa Điểm Thi Công / Lắp Đặt", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = input.projectLocation,
                        onValueChange = { viewModel.setProjectDetails(location = it) },
                        placeholder = { Text("VD: Tầng 2, 128 Nguyễn Trãi, Thanh Xuân, Hà Nội") },
                        leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_project_location"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber400,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (installLocations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            installLocations.take(4).forEach { loc ->
                                val selected = input.projectLocation == loc.name
                                Surface(
                                    onClick = { viewModel.selectInstallLocation(loc) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) Amber400.copy(alpha = 0.2f) else Slate950,
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(if (selected) Amber400 else Slate800)
                                    )
                                ) {
                                    Text(
                                        text = "📍 ${loc.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) Amber400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Technical Notes
                Column {
                    Text(text = "Ghi Chú Kỹ Thuật / Khảo Sát Hiện Trường", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = input.projectNotes,
                        onValueChange = { viewModel.setProjectDetails(notes = it) },
                        placeholder = { Text("VD: Thi công ban đêm sau 22h, chuẩn bị giàn giáo 3 tầng, câu nguồn từ hộp điện tổng...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Slate700, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_project_notes"),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber400,
                            unfocusedBorderColor = Slate700,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // GEMINI 3.8 FLASH AI MATERIAL SUGGESTION FROM FORM DATA & ADVERTISING DESCRIPTION
        GeminiMaterialSuggestionCard(
            input = input,
            advertisingDescription = advertisingDescription,
            suggestionState = aiSuggestionState,
            onDescriptionChange = { viewModel.setAdvertisingDescription(it) },
            onSuggestClick = { viewModel.suggestMaterialsWithGemini() },
            onApplyMaterial = { viewModel.applySuggestedMaterialToForm(it) },
            onSaveMaterial = { viewModel.saveSuggestedMaterialToCatalog(it) },
            onSaveAllMaterials = { viewModel.saveAllSuggestedMaterialsToCatalog(it) },
            onClear = { viewModel.clearAiSuggestions() }
        )

        // SECTION 2: EXPOSED DROPDOWN MENU FOR MATERIAL SELECTION & EDITING
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("material_types_form_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. CHỦNG LOẠI VẬT TƯ QUẢNG CÁO",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        onClick = { showAddMaterialDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Emerald400.copy(alpha = 0.15f),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald400))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Thêm Mục Mới", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        }
                    }
                }

                // EXPOSED DROPDOWN MENU BOX
                val categories = listOf("Tất Cả", "Mica", "Alu (Aluminum)", "PVC / Formex", "Bạt In", "Decal", "Inox & Kim Loại", "Sắt Thép & Khung", "LED & Nguồn Điện", "Keo & Phụ Kiện")

                ExposedDropdownMenuBox(
                    expanded = isMaterialDropdownExpanded,
                    onExpandedChange = { isMaterialDropdownExpanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("material_exposed_dropdown_box")
                ) {
                    OutlinedTextField(
                        value = if (input.selectedMaterialName.isNotBlank()) {
                            "${input.selectedMaterialName} - ${Formatters.formatVnd(activeMaterialUnitPrice)} / ${input.selectedMaterialUnit}"
                        } else {
                            "Chọn vật tư (Mica, Alu, PVC, Bạt in, Decal, Inox...)"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chủng Loại Vật Tư Quảng Cáo *", color = Cyan400) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMaterialDropdownExpanded) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("material_dropdown_input"),
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

                    ExposedDropdownMenu(
                        expanded = isMaterialDropdownExpanded,
                        onDismissRequest = { isMaterialDropdownExpanded = false },
                        modifier = Modifier
                            .background(Slate900)
                            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "➕ Thêm Vật Tư Mới Vào Bảng Giá Xưởng", color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            },
                            onClick = {
                                isMaterialDropdownExpanded = false
                                showAddMaterialDialog = true
                            }
                        )

                        if (input.selectedMaterialKey.isNotBlank()) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Amber400, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "✏️ Chỉnh Sửa Giá / Quy Cách Vật Tư Đang Chọn", color = Amber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    isMaterialDropdownExpanded = false
                                    showEditMaterialDialog = true
                                }
                            )
                        }

                        // Category Filter Strip inside dropdown
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategoryFilter == cat
                                Surface(
                                    onClick = { selectedCategoryFilter = cat },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Cyan900 else Slate950,
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Cyan400 else Slate800)
                                    )
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        val displayList = if (selectedCategoryFilter == "Tất Cả") {
                            catalog
                        } else {
                            catalog.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
                        }

                        displayList.forEach { matItem ->
                            val currentPrice = unitPrices[matItem.priceKey] ?: matItem.price
                            val isChosen = input.selectedMaterialKey == matItem.priceKey

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = Slate800,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = matItem.category,
                                                        fontSize = 9.sp,
                                                        color = Cyan400,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = matItem.brand,
                                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isChosen) Amber400 else Color.White,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            Text(
                                                text = matItem.spec,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "${Formatters.formatVnd(currentPrice)} / ${matItem.unit}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Emerald400
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.setSelectedMaterial(matItem)
                                    isMaterialDropdownExpanded = false
                                },
                                modifier = if (isChosen) Modifier.background(Cyan900.copy(alpha = 0.3f)) else Modifier
                            )
                        }
                    }
                }

                // Quick Material Management Strip (Add & Edit Buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAddMaterialDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_add_material_dialog"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald400),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald400.copy(alpha = 0.5f)))
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thêm Vật Tư Mới", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showEditMaterialDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_edit_material_dialog"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber400),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Amber400.copy(alpha = 0.5f)))
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sửa Giá Phôi Này", fontSize = 11.sp)
                    }
                }
            }
        }

        // SECTION 3: DIMENSIONS (LENGTH x WIDTH) WITH STEPPERS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dimensions_form_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. KÍCH THƯỚC (DÀI x RỘNG)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                    ) {
                        Text(
                            text = "Net: ${Formatters.formatArea(result.netArea)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Quick Dimension Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "3.0 x 1.2m" to Pair(3.0, 1.2),
                        "5.0 x 2.0m" to Pair(5.0, 2.0),
                        "6.0 x 2.4m" to Pair(6.0, 2.4),
                        "8.0 x 3.0m" to Pair(8.0, 3.0),
                        "12.0 x 4.0m" to Pair(12.0, 4.0)
                    ).forEach { (label, dims) ->
                        Surface(
                            onClick = { viewModel.setDimensions(dims.first, dims.second) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (input.width == dims.first && input.height == dims.second) Emerald400.copy(alpha = 0.2f) else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (input.width == dims.first && input.height == dims.second) Emerald400 else Slate800)
                            )
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = if (input.width == dims.first && input.height == dims.second) Emerald400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Width (Chiều Dài / Ngang) & Height (Chiều Rộng / Cao) with Steppers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Length / Width Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Chiều Dài / Ngang (m)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = if (input.width == 0.0) "" else input.width.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setDimensions(v, input.height)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_width_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                onClick = {
                                    val next = (input.width - 0.5).coerceAtLeast(0.5)
                                    viewModel.setDimensions(Math.round(next * 10.0) / 10.0, input.height)
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Slate950
                            ) {
                                Text("-0.5m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                            Surface(
                                onClick = {
                                    val next = input.width + 0.5
                                    viewModel.setDimensions(Math.round(next * 10.0) / 10.0, input.height)
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Slate950
                            ) {
                                Text("+0.5m", style = MaterialTheme.typography.labelSmall, color = Cyan400, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }

                    // Width / Height Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Chiều Rộng / Cao (m)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = if (input.height == 0.0) "" else input.height.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setDimensions(input.width, v)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_height_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                onClick = {
                                    val next = (input.height - 0.2).coerceAtLeast(0.2)
                                    viewModel.setDimensions(input.width, Math.round(next * 10.0) / 10.0)
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Slate950
                            ) {
                                Text("-0.2m", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                            Surface(
                                onClick = {
                                    val next = input.height + 0.2
                                    viewModel.setDimensions(input.width, Math.round(next * 10.0) / 10.0)
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = Slate950
                            ) {
                                Text("+0.2m", style = MaterialTheme.typography.labelSmall, color = Cyan400, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }
                }

                // Quantity & Wastage Margin
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Quantity
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Số Lượng (Biển/Bộ)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate950)
                                .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.setQuantity((input.quantity - 1).coerceAtLeast(1)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Giảm", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = input.quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = { viewModel.setQuantity(input.quantity + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Tăng", tint = Cyan400, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Wastage
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "% Phôi Hao Hụt & Mí Dán", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.wastagePercent.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setWastage(v)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_wastage_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Amber400),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Amber400,
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

        // DEDICATED LIVE AUTOMATIC PRICE CALCULATION FORMULA CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auto_calculation_formula_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Cyan400, Amber400)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TỰ ĐỘNG TÍNH TOÁN GIÁ THEO KÍCH THƯỚC & ĐƠN GIÁ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        color = Cyan900.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Tự Động Realtime",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Step-by-Step Calculation Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate950)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "1. Kích thước (Dài x Rộng):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${input.width}m x ${input.height}m (SL: ${input.quantity}) = ${String.format(Locale.US, "%.2f", calculatedNetArea)} m²",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "2. Khối lượng tính cả mí (+${input.wastagePercent}%):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (isSheetBased) "$calculatedSheetCount Tấm (1.22x2.44m)" else "${String.format(Locale.US, "%.2f", calculatedGrossArea)} ${input.selectedMaterialUnit}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Amber400
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3. Đơn giá vật tư phôi đã chọn:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${Formatters.formatVnd(activeMaterialUnitPrice)} / ${input.selectedMaterialUnit}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "➔ Chi phí phôi mặt biển:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Formatters.formatVnd(calculatedFaceCost),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate800))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "➔ Khung sắt + LED + Hoàn thiện + Nhân công:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Formatters.formatVnd(result.totalCost - calculatedFaceCost),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TỔNG GIÁ XUẤT CHO KHÁCH (+${input.profitMarginPercent}% Lời):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                        Text(
                            text = Formatters.formatVnd(result.finalCustomerPrice),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = Emerald400
                        )
                    }
                }
            }
        }

        // SECTION 4: TÙY CHỌN HỆ THỐNG QUẢNG CÁO ĐI KÈM
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("advertising_options_form_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4. CẤU HÌNH HỆ THỐNG QUẢNG CÁO ĐI KÈM",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Cyan400,
                        letterSpacing = 1.sp
                    )
                }

                // Option 1: Khung sắt kẽm
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Khung Sắt Hộp Mạ Kẽm Chống Bão", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Nan đan 60x60cm, tôn ke gia cố 4 góc & sơn chống rỉ mối hàn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = input.includeIronFrame,
                        onCheckedChange = { viewModel.toggleAdvertisingOption(ironFrame = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Cyan400, checkedTrackColor = Cyan900)
                    )
                }

                // Option 2: LED & Nguồn ngoài trời
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Hệ Thống Đèn LED & Nguồn Ngoài Trời 12V", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Module LED Samsung NC 3 mắt + Nguồn tổ ong chống nước 12V 33A", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = input.includeLighting,
                        onCheckedChange = { viewModel.toggleAdvertisingOption(lighting = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Cyan400, checkedTrackColor = Cyan900)
                    )
                }

                // Option 3: Nẹp V nhôm / inox viền biển
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Nẹp Viền V Nhôm / Inox Bo Cạnh", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Che đầu vít, nẹp bo mép bạt/alu tăng độ bền thẩm mỹ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = input.includeEdgeTrim,
                        onCheckedChange = { viewModel.toggleAdvertisingOption(edgeTrim = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Cyan400, checkedTrackColor = Cyan900)
                    )
                }

                // Option 4: Timer hẹn giờ tự động
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Rơ-le Hẹn Giờ Tự Động Timer KG316T", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Tự động bật 18h00 - tắt 23h30 bảo vệ tuổi thọ LED & tiết kiệm điện", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = input.includeTimerRelay,
                        onCheckedChange = { viewModel.toggleAdvertisingOption(timerRelay = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Cyan400, checkedTrackColor = Cyan900)
                    )
                }
            }
        }

        // SECTION 5: TỔNG HỢP HẾT TẤT CẢ VẬT TƯ & THIẾT BỊ CẦN CHO QUẢNG CÁO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("advertising_synthesis_checklist_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Cyan400, Emerald400)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "5. TỔNG HỢP TRỌN BỘ VẬT TƯ QUẢNG CÁO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Bảng bóc tách kiểm tra trước khi xuất xưởng & thi công lắp đặt",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald400))
                    ) {
                        Text(
                            text = "${result.advertisingChecklist.size} Hạng Mục",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                val groupedChecklist = result.advertisingChecklist.groupBy { it.category }

                groupedChecklist.forEach { (catName, items) ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(Cyan400))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Cyan400,
                                fontSize = 11.sp
                            )
                        }

                        items.forEach { chkItem ->
                            val isChecked = checkedChecklistItems[chkItem.id] ?: false

                            Surface(
                                onClick = { checkedChecklistItems[chkItem.id] = !isChecked },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isChecked) Slate800 else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (isChecked) Emerald400 else Slate800)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checkedChecklistItems[chkItem.id] = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Emerald400,
                                            checkmarkColor = Color.Black,
                                            uncheckedColor = Slate700
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = chkItem.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChecked) Emerald400 else Color.White
                                            )
                                            Text(
                                                text = chkItem.quantity,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = Amber400
                                            )
                                        }

                                        Text(
                                            text = chkItem.spec,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        if (chkItem.note.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "💡 ${chkItem.note}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 6: Commercial & Labor Parameters
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("commercial_form_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Handyman, contentDescription = null, tint = Purple400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "6. ĐIỀU KIỆN THI CÔNG & LỢI NHUẬN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Purple400,
                        letterSpacing = 1.sp
                    )
                }

                // Complexity
                Column {
                    Text(text = "Độ Phức Tạp Lắp Đặt Hiện Trường", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LaborComplexity.values().forEach { comp ->
                            val isSelected = input.laborComplexity == comp
                            Surface(
                                onClick = { viewModel.setLaborComplexity(comp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Purple500.copy(alpha = 0.2f) else Slate950,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Purple400 else Slate800)
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when (comp) {
                                            LaborComplexity.EASY -> "Dưới 3m"
                                            LaborComplexity.MEDIUM -> "Tầng 1-2"
                                            LaborComplexity.HARD -> "Đu dây"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Purple400 else Color.White
                                    )
                                    Text(
                                        text = "x${comp.multiplier}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Profit & Shipping Fee
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "% Lợi Nhuận Kỳ Vọng", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.profitMarginPercent.toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setProfitMargin(v)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_profit_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Emerald400),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Phí Vận Chuyển & Cẩu (đ)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = input.shippingAndHardwareFee.toLong().toString(),
                            onValueChange = { str ->
                                val v = str.toDoubleOrNull() ?: 0.0
                                viewModel.setShippingFee(v)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_shipping_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
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

        // SECTION 7: LIVE COST ESTIMATION PREVIEW & BOTTOM EXPORT ACTION BAR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_cost_preview_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Cyan400, Emerald400)))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KẾT QUẢ DỰ TOÁN BÁO GIÁ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Cyan400,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Đơn giá: ${Formatters.formatVnd(result.unitPricePerM2)} / m²",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tổng Chi Phí Giá Vốn Xưởng:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = Formatters.formatVnd(result.totalCost), style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Lợi Nhuận Kỳ Vọng (+${input.profitMarginPercent}%):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "+${Formatters.formatVnd(result.profitAmount)}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Emerald400)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate800))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TỔNG GIÁ XUẤT CHO KHÁCH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Formatters.formatVnd(result.finalCustomerPrice),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = Cyan400
                        )
                    }

                    Button(
                        onClick = { viewModel.saveCurrentToProject() },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("form_save_to_dossier_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Lưu Dự Toán", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // NÚT XUẤT FILE BÁO GIÁ PDF CHUYÊN NGHIỆP CHO KHÁCH HÀNG (A4)
                Button(
                    onClick = {
                        ProjectExportManager.sharePdfQuotation(context, input, result, projectItems)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_export_pdf_quotation"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📄 Xuất Báo Giá PDF Chuyên Nghiệp (A4 Gửi Khách)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // PRIMARY EXPORT ALL FILES BUTTON (TÍNH NĂNG XUẤT TẤT CẢ FILE)
                Button(
                    onClick = {
                        val pkg = ProjectExportManager.exportAllFiles(context, input, result, projectItems)
                        exportPackage = pkg
                        showExportAllDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_export_all_files"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📤 Xuất Tất Cả File (PDF, In HTML, Excel, Checklist, ZIP)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToBreakdown,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_view_breakdown_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Xem Bảng Bóc Tách Chi Tiết Từng Phôi Vật Tư", fontSize = 12.sp)
                }
            }
        }
    }

    // DIALOG: XUẤT TẤT CẢ FILE (EXPORT ALL FILES MODAL)
    if (showExportAllDialog && exportPackage != null) {
        val pkg = exportPackage!!

        AlertDialog(
            onDismissRequest = { showExportAllDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Archive, contentDescription = null, tint = Emerald400, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Xuất Tất Cả File Dự Toán", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                        Text(text = "Dự án: ${pkg.projectTitle}", fontSize = 11.sp, color = Emerald400)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Hệ thống đã tự động xuất trọn bộ hồ sơ gồm file in ấn A4, bảng kê Excel, checklist thi công và file nén ZIP:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Master Action 0: Chia sẻ PDF Báo Giá A4
                    Button(
                        onClick = {
                            ProjectExportManager.sharePdfQuotation(context, input, result, projectItems)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📄 Chia Sẻ File Báo Giá PDF (A4 Chuẩn In)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Master Action 1: Chia Sẻ Trọn Bộ File Nén ZIP
                    Button(
                        onClick = {
                            ProjectExportManager.shareSingleFile(context, pkg.zipFile, "Hồ Sơ Dự Án Quảng Cáo - ${pkg.projectTitle}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🚀 Chia Sẻ Trọn Gói File Nén (.ZIP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Master Action 2: Gửi đồng thời cả 4 file
                    OutlinedButton(
                        onClick = {
                            ProjectExportManager.shareMultipleFiles(context, pkg.files, "Bộ Hồ Sơ Dự Toán - ${pkg.projectTitle}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald400),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald400)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📦 Chia Sẻ Đồng Thời Cả 4 File Cùng Lúc", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate800))

                    Text(text = "Hoặc chia sẻ từng file riêng lẻ:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Amber400)

                    // Individual Files
                    pkg.files.forEach { fileItem ->
                        Surface(
                            onClick = {
                                ProjectExportManager.shareSingleFile(context, fileItem, fileItem.title)
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = when {
                                            fileItem.mimeType.contains("html") -> Icons.Default.Print
                                            fileItem.mimeType.contains("csv") -> Icons.Default.TableChart
                                            else -> Icons.Default.Description
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            fileItem.mimeType.contains("html") -> Cyan400
                                            fileItem.mimeType.contains("csv") -> Emerald400
                                            else -> Amber400
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = fileItem.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = fileItem.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    }
                                }

                                Icon(imageVector = Icons.Default.Share, contentDescription = "Chia sẻ", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Copy Quick Zalo Text
                    OutlinedButton(
                        onClick = {
                            val textQuote = ProjectExportManager.generateZaloTextQuote(input, result)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Báo Giá Zalo", textQuote)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Đã sao chép văn bản báo giá vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Sao Chép Báo Giá Zalo / SMS", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportAllDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White)
                ) {
                    Text("Đóng")
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // DIALOG 1: ADD CUSTOM ADVERTISING MATERIAL
    if (showAddMaterialDialog) {
        var newMatCode by remember { mutableStateOf("VT${System.currentTimeMillis() % 1000}") }
        var newMatBrand by remember { mutableStateOf("") }
        var newMatSpec by remember { mutableStateOf("") }
        var newMatUnit by remember { mutableStateOf("Tấm") }
        var newMatPriceStr by remember { mutableStateOf("350000") }
        var newMatCategory by remember { mutableStateOf("Mica") }

        AlertDialog(
            onDismissRequest = { showAddMaterialDialog = false },
            title = { Text(text = "Thêm Vật Tư Mới Vào Danh Mục", fontWeight = FontWeight.Bold, color = Emerald400) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Nhập thông tin vật tư quảng cáo (Mica, Alu, PVC, Formex, Bạt, Decal, v.v.):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Category Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Mica", "Alu (Aluminum)", "PVC / Formex", "Bạt In", "Decal", "Inox & Kim Loại", "Sắt Thép & Khung", "LED & Nguồn Điện", "Keo & Phụ Kiện").forEach { cat ->
                            Surface(
                                onClick = { newMatCategory = cat },
                                shape = RoundedCornerShape(6.dp),
                                color = if (newMatCategory == cat) Cyan900 else Slate900,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (newMatCategory == cat) Cyan400 else Slate700)
                                )
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (newMatCategory == cat) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newMatBrand,
                        onValueChange = { newMatBrand = it },
                        label = { Text("Tên / Thương Hiệu Vật Tư (VD: Mica FS Sữa 3mm)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald400, unfocusedBorderColor = Slate700)
                    )

                    OutlinedTextField(
                        value = newMatSpec,
                        onValueChange = { newMatSpec = it },
                        label = { Text("Quy Cách / Thông Số Kỹ Thuật (VD: Khổ 1.22x2.44m)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald400, unfocusedBorderColor = Slate700)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newMatUnit,
                            onValueChange = { newMatUnit = it },
                            label = { Text("Đơn Vị (Tấm, m², mét, con)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald400, unfocusedBorderColor = Slate700)
                        )

                        OutlinedTextField(
                            value = newMatPriceStr,
                            onValueChange = { newMatPriceStr = it },
                            label = { Text("Đơn Giá Phôi (đ)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Emerald400, unfocusedBorderColor = Slate700)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = newMatPriceStr.toDoubleOrNull() ?: 0.0
                        if (newMatBrand.isNotBlank() && p > 0) {
                            val newItem = MaterialItem(
                                id = newMatCode,
                                brand = newMatBrand.trim(),
                                spec = newMatSpec.ifBlank { "Quy cách chuẩn xưởng" },
                                unit = newMatUnit.ifBlank { "Tấm" },
                                priceKey = "custom_${newMatCode.lowercase()}",
                                price = p,
                                category = newMatCategory
                            )
                            viewModel.addOrUpdateMaterial(newItem)
                            viewModel.setSelectedMaterial(newItem)
                            showAddMaterialDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
                ) {
                    Text("Lưu & Chọn Ngay", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMaterialDialog = false }) {
                    Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIALOG 2: EDIT SELECTED MATERIAL PRICE & SPECS
    if (showEditMaterialDialog) {
        val currentPrice = activeMaterialUnitPrice
        var editPriceStr by remember { mutableStateOf(currentPrice.toLong().toString()) }
        var editBrandName by remember { mutableStateOf(currentSelectedMaterial?.brand ?: input.selectedMaterialName) }

        AlertDialog(
            onDismissRequest = { showEditMaterialDialog = false },
            title = { Text(text = "Chỉnh Sửa Vật Tư Đang Chọn", fontWeight = FontWeight.Bold, color = Amber400) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Cập nhật đơn giá phôi xưởng để tính toán tự động lại toàn bộ chi phí dự toán:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editBrandName,
                        onValueChange = { editBrandName = it },
                        label = { Text("Tên Vật Tư") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber400, unfocusedBorderColor = Slate700)
                    )

                    OutlinedTextField(
                        value = editPriceStr,
                        onValueChange = { editPriceStr = it },
                        label = { Text("Đơn Giá Phôi Mới (đ / ${input.selectedMaterialUnit})") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber400, unfocusedBorderColor = Slate700)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = editPriceStr.toDoubleOrNull() ?: 0.0
                        if (p > 0) {
                            viewModel.updateSelectedMaterialPrice(p)
                            if (currentSelectedMaterial != null) {
                                viewModel.addOrUpdateMaterial(currentSelectedMaterial.copy(brand = editBrandName, price = p))
                            }
                            showEditMaterialDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400, contentColor = Color.Black)
                ) {
                    Text("Cập Nhật", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditMaterialDialog = false }) {
                    Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
