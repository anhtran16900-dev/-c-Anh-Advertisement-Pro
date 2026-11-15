package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiMaterialSuggestionService
import com.example.calculator.SignageCalculator
import com.example.calculator.SmartMaterialCalculator
import com.example.calculator.SmartQuoteLine
import com.example.calculator.SmartQuoteSummary
import com.example.data.DefaultData
import com.example.data.SignageRepository
import com.example.model.AcademyGuide
import com.example.model.AiSuggestedMaterial
import com.example.model.AiSuggestionUiState
import com.example.model.ChatMessage
import com.example.model.DossierQuoteSettings
import com.example.model.EditableCategoryOption
import com.example.model.EstimationInput
import com.example.model.EstimationResult
import com.example.model.EstimatorCustomDefaults
import com.example.model.InstallLocationPreset
import com.example.model.LaborComplexity
import com.example.model.MaterialItem
import com.example.model.ProjectDossierItem
import com.example.model.SignageCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "SignageViewModel"

class SignageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SignageRepository(application)

    // Current screen navigation
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Estimator input & realtime result
    private val _estimationInput = MutableStateFlow(EstimationInput())
    val estimationInput: StateFlow<EstimationInput> = _estimationInput.asStateFlow()

    val unitPrices: StateFlow<Map<String, Double>> = repository.unitPrices
    val materialCatalog: StateFlow<List<MaterialItem>> = repository.materialCatalog
    val projectTitle: StateFlow<String> = repository.projectTitle
    val projectItems: StateFlow<List<ProjectDossierItem>> = repository.projectItems
    val dossierQuoteSettings: StateFlow<DossierQuoteSettings> = repository.dossierQuoteSettings
    val userApiKey: StateFlow<String> = repository.userApiKey
    val categoryOptions: StateFlow<List<EditableCategoryOption>> = repository.categoryOptions
    val itemPresetsByCategoryId: StateFlow<Map<String, List<String>>> = repository.itemPresetsByCategoryId
    val installLocations: StateFlow<List<InstallLocationPreset>> = repository.installLocations

    // Realtime calculated result
    val estimationResult: StateFlow<EstimationResult> = combine(
        _estimationInput,
        unitPrices
    ) { input, prices ->
        SignageCalculator.calculate(input, prices)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SignageCalculator.calculate(EstimationInput(), DefaultData.defaultUnitPrices)
    )

    // Material search filter
    private val _materialSearchQuery = MutableStateFlow("")
    val materialSearchQuery: StateFlow<String> = _materialSearchQuery.asStateFlow()

    // Filtered materials
    val filteredMaterials: StateFlow<List<MaterialItem>> = combine(
        materialCatalog,
        _materialSearchQuery
    ) { catalog, query ->
        if (query.isBlank()) {
            catalog
        } else {
            val q = query.lowercase().trim()
            catalog.filter {
                it.id.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.spec.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultData.defaultMaterials)

    // Academy Guides
    val academyGuides: List<AcademyGuide> = DefaultData.academyGuides
    private val _selectedGuide = MutableStateFlow<AcademyGuide?>(null)
    val selectedGuide: StateFlow<AcademyGuide?> = _selectedGuide.asStateFlow()
    val completedGuideSteps: StateFlow<Set<String>> = repository.completedGuideSteps

    // AI Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Xin chào! Tôi là Trợ Lý Kỹ Thuật AI Đức Anh PRO chuyên ngành quảng cáo. Tôi có thể giúp bạn bóc tách kết cấu biển hiệu, tư vấn vật tư, hoặc xử lý các sự cố máy in phun KTS, máy CNC Router và đấu nối nguồn LED. Bạn cần hỗ trợ gì hôm nay?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _chatTurboMode = MutableStateFlow(true)
    val chatTurboMode: StateFlow<Boolean> = _chatTurboMode.asStateFlow()

    private val _attachContextInChat = MutableStateFlow(false)
    val attachContextInChat: StateFlow<Boolean> = _attachContextInChat.asStateFlow()

    // Gemini 3.8 Flash Material Suggestion (Form -> Suggested Materials)
    private val geminiService by lazy { GeminiMaterialSuggestionService() }

    private val _advertisingDescription = MutableStateFlow("")
    val advertisingDescription: StateFlow<String> = _advertisingDescription.asStateFlow()

    private val _aiSuggestionState = MutableStateFlow<AiSuggestionUiState>(AiSuggestionUiState.Idle)
    val aiSuggestionState: StateFlow<AiSuggestionUiState> = _aiSuggestionState.asStateFlow()

    // Toast / feedback message
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    // Smart Pricing, m2 & Material Calculator Table
    private val _smartQuoteLines = MutableStateFlow<List<SmartQuoteLine>>(emptyList())
    val smartQuoteLines: StateFlow<List<SmartQuoteLine>> = _smartQuoteLines.asStateFlow()

    private val _smartDiscountPercent = MutableStateFlow(0.0)
    val smartDiscountPercent: StateFlow<Double> = _smartDiscountPercent.asStateFlow()

    private val _smartVatPercent = MutableStateFlow(0.0)
    val smartVatPercent: StateFlow<Double> = _smartVatPercent.asStateFlow()

    val smartQuoteSummary: StateFlow<SmartQuoteSummary> = combine(
        _smartQuoteLines,
        _smartDiscountPercent,
        _smartVatPercent
    ) { lines, discount, vat ->
        SmartMaterialCalculator.calculateQuoteSummary(lines, discount, vat)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SmartMaterialCalculator.calculateQuoteSummary(emptyList(), 0.0, 0.0)
    )

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun switchTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    // Input mutations
    fun setCategory(category: SignageCategory) {
        val matching = categoryOptions.value.firstOrNull { it.baseCategory == category }
        if (matching != null) {
            selectCategoryOption(matching)
        } else {
            val catId = EstimatorCustomDefaults.builtInCategoryId(category)
            val presets = repository.getItemPresets(catId, category)
            val defaultName = presets.firstOrNull() ?: category.shortName
            _estimationInput.value = _estimationInput.value.copy(
                category = category,
                selectedCategoryOptionId = catId,
                customCategoryLabel = category.shortName,
                itemName = defaultName
            )
        }
    }

    fun selectCategoryOption(option: EditableCategoryOption) {
        val presets = repository.getItemPresets(option.id, option.baseCategory)
        val defaultName = presets.firstOrNull() ?: option.shortName
        _estimationInput.value = _estimationInput.value.copy(
            category = option.baseCategory,
            selectedCategoryOptionId = option.id,
            customCategoryLabel = option.shortName,
            itemName = defaultName
        )
    }

    fun addOrUpdateCategoryOption(option: EditableCategoryOption) {
        val cleanShort = option.shortName.trim().ifBlank { option.baseCategory.shortName }
        val cleanDisplay = option.displayName.trim().ifBlank { cleanShort }
        val normalized = option.copy(shortName = cleanShort, displayName = cleanDisplay)
        repository.addOrUpdateCategoryOption(normalized)

        // Seed default presets for newly created custom categories if empty
        if (repository.itemPresetsByCategoryId.value[normalized.id].isNullOrEmpty()) {
            val basePresets = DefaultData.presetItemNames[normalized.baseCategory] ?: emptyList()
            basePresets.firstOrNull()?.let { firstPreset ->
                repository.addItemPreset(normalized.id, normalized.baseCategory, "$cleanShort - $firstPreset")
            }
        }
        selectCategoryOption(normalized)
        _userNotice.value = "Đã lưu loại hạng mục '${normalized.shortName}'"
    }

    fun deleteCategoryOption(id: String) {
        if (repository.deleteCategoryOption(id)) {
            if (_estimationInput.value.selectedCategoryOptionId == id) {
                categoryOptions.value.firstOrNull()?.let { selectCategoryOption(it) }
            }
            _userNotice.value = "Đã xóa loại hạng mục tùy chỉnh"
        } else {
            _userNotice.value = "Không thể xóa danh mục mặc định gốc (bạn có thể chỉnh sửa tên)"
        }
    }

    fun resetDefaultCategories() {
        repository.resetDefaultCategories()
        categoryOptions.value.firstOrNull()?.let { selectCategoryOption(it) }
        _userNotice.value = "Đã khôi phục danh sách loại hạng mục mặc định"
    }

    fun addItemPreset(name: String) {
        val curr = _estimationInput.value
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        repository.addItemPreset(curr.selectedCategoryOptionId, curr.category, trimmed)
        _estimationInput.value = curr.copy(itemName = trimmed)
        _userNotice.value = "Đã thêm mẫu tên '$trimmed'"
    }

    fun updateItemPreset(oldName: String, newName: String) {
        val curr = _estimationInput.value
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        repository.updateItemPreset(curr.selectedCategoryOptionId, curr.category, oldName, trimmed)
        if (curr.itemName == oldName) {
            _estimationInput.value = curr.copy(itemName = trimmed)
        }
        _userNotice.value = "Đã cập nhật mẫu tên thành '$trimmed'"
    }

    fun deleteItemPreset(name: String) {
        val curr = _estimationInput.value
        repository.deleteItemPreset(curr.selectedCategoryOptionId, curr.category, name)
        _userNotice.value = "Đã xóa mẫu '$name'"
    }

    fun selectInstallLocation(preset: InstallLocationPreset) {
        val curr = _estimationInput.value
        _estimationInput.value = curr.copy(
            projectLocation = preset.name,
            laborComplexity = preset.suggestedComplexity ?: curr.laborComplexity
        )
    }

    fun addOrUpdateInstallLocation(preset: InstallLocationPreset) {
        val trimmed = preset.name.trim()
        if (trimmed.isEmpty()) return
        val normalized = preset.copy(name = trimmed)
        repository.addOrUpdateInstallLocation(normalized)
        selectInstallLocation(normalized)
        _userNotice.value = "Đã lưu vị trí lắp đặt '${normalized.name}'"
    }

    fun deleteInstallLocation(id: String) {
        val target = installLocations.value.firstOrNull { it.id == id }
        repository.deleteInstallLocation(id)
        if (target != null && _estimationInput.value.projectLocation == target.name) {
            _estimationInput.value = _estimationInput.value.copy(projectLocation = "")
        }
        _userNotice.value = "Đã xóa vị trí lắp đặt"
    }

    fun setQuoteCalculationOptions(
        minOneM2PerPiece: Boolean? = null,
        discountPercent: Double? = null,
        vatPercent: Double? = null,
        customUnitPriceOverridePerM2: Double? = null,
        clearCustomUnitPrice: Boolean = false
    ) {
        val curr = _estimationInput.value
        _estimationInput.value = curr.copy(
            minOneM2PerPiece = minOneM2PerPiece ?: curr.minOneM2PerPiece,
            discountPercent = discountPercent?.coerceIn(0.0, 90.0) ?: curr.discountPercent,
            vatPercent = vatPercent?.coerceIn(0.0, 20.0) ?: curr.vatPercent,
            customUnitPriceOverridePerM2 = if (clearCustomUnitPrice) null else (customUnitPriceOverridePerM2 ?: curr.customUnitPriceOverridePerM2)
        )
    }

    fun setItemName(name: String) {
        _estimationInput.value = _estimationInput.value.copy(itemName = name)
    }

    fun setProjectDetails(
        itemName: String? = null,
        clientName: String? = null,
        location: String? = null,
        installationDate: String? = null,
        notes: String? = null
    ) {
        val curr = _estimationInput.value
        _estimationInput.value = curr.copy(
            itemName = itemName ?: curr.itemName,
            clientName = clientName ?: curr.clientName,
            projectLocation = location ?: curr.projectLocation,
            installationDate = installationDate ?: curr.installationDate,
            projectNotes = notes ?: curr.projectNotes
        )
    }

    fun resetForm() {
        _estimationInput.value = EstimationInput()
        _advertisingDescription.value = ""
        _aiSuggestionState.value = AiSuggestionUiState.Idle
        _userNotice.value = "Đã làm mới thông tin biểu mẫu dự toán"
    }

    fun setAdvertisingDescription(description: String) {
        _advertisingDescription.value = description
    }

    fun clearAiSuggestions() {
        _aiSuggestionState.value = AiSuggestionUiState.Idle
    }

    fun suggestMaterialsWithGemini() {
        val currentInput = _estimationInput.value
        val customDesc = _advertisingDescription.value.trim()
        val effectiveDescription = if (customDesc.isNotEmpty()) {
            customDesc
        } else {
            listOf(
                currentInput.itemName,
                currentInput.category.displayName,
                currentInput.projectNotes
            ).filter { it.isNotBlank() }.joinToString(". ")
        }

        if (effectiveDescription.isBlank()) {
            _aiSuggestionState.value = AiSuggestionUiState.Error(
                "Vui lòng nhập mô tả ngành quảng cáo hoặc tên hạng mục trước khi gọi Gemini 3.8 Flash."
            )
            return
        }

        viewModelScope.launch {
            _aiSuggestionState.value = AiSuggestionUiState.Loading
            try {
                val result = geminiService.suggestMaterials(
                    input = currentInput,
                    advertisingDescription = effectiveDescription,
                    existingCatalog = materialCatalog.value
                )
                _aiSuggestionState.value = AiSuggestionUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini material suggestion timed out", e)
                _aiSuggestionState.value = AiSuggestionUiState.Error(
                    "Gemini 3.8 Flash phản hồi quá 60 giây. Vui lòng thử lại."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini material suggestion failed", e)
                _aiSuggestionState.value = AiSuggestionUiState.Error(
                    e.localizedMessage ?: "Không thể kết nối dịch vụ Gemini 3.8 Flash."
                )
            }
        }
    }

    fun applySuggestedMaterialToForm(suggestion: AiSuggestedMaterial) {
        val item = suggestionToMaterialItem(suggestion)
        repository.addOrUpdateMaterial(item)
        val updatedPrices = repository.unitPrices.value.toMutableMap()
        if (suggestion.estimatedUnitPrice > 0.0) {
            updatedPrices[item.priceKey] = suggestion.estimatedUnitPrice
            repository.saveUnitPrices(updatedPrices)
        }
        setSelectedMaterial(item)
        _userNotice.value = "Đã áp dụng vật tư gợi ý '${suggestion.name}' vào biểu mẫu!"
    }

    fun saveSuggestedMaterialToCatalog(suggestion: AiSuggestedMaterial) {
        val item = suggestionToMaterialItem(suggestion)
        repository.addOrUpdateMaterial(item)
        if (suggestion.estimatedUnitPrice > 0.0) {
            val updatedPrices = repository.unitPrices.value.toMutableMap()
            updatedPrices[item.priceKey] = suggestion.estimatedUnitPrice
            repository.saveUnitPrices(updatedPrices)
        }
        _userNotice.value = "Đã lưu '${suggestion.name}' vào Kho Vật Tư xưởng"
    }

    fun saveAllSuggestedMaterialsToCatalog(suggestions: List<AiSuggestedMaterial>) {
        if (suggestions.isEmpty()) return
        val updatedPrices = repository.unitPrices.value.toMutableMap()
        suggestions.forEach { suggestion ->
            val item = suggestionToMaterialItem(suggestion)
            repository.addOrUpdateMaterial(item)
            if (suggestion.estimatedUnitPrice > 0.0) {
                updatedPrices[item.priceKey] = suggestion.estimatedUnitPrice
            }
        }
        repository.saveUnitPrices(updatedPrices)
        _userNotice.value = "Đã lưu ${suggestions.size} vật tư gợi ý Gemini vào Kho Vật Tư!"
    }

    private fun suggestionToMaterialItem(suggestion: AiSuggestedMaterial): MaterialItem {
        val slug = suggestion.name.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .take(24)
            .ifBlank { "ai_material" }
        val key = "ai_${slug}_${suggestion.name.hashCode().toUInt()}"
        return MaterialItem(
            id = "AI-${suggestion.name.hashCode().toUInt().toString().takeLast(4)}",
            brand = suggestion.name,
            spec = suggestion.spec,
            unit = suggestion.unit,
            priceKey = key,
            price = suggestion.estimatedUnitPrice,
            category = suggestion.category
        )
    }

    fun setDimensions(width: Double, height: Double) {
        _estimationInput.value = _estimationInput.value.copy(width = width, height = height)
    }

    fun setQuantity(qty: Int) {
        if (qty > 0) {
            _estimationInput.value = _estimationInput.value.copy(quantity = qty)
        }
    }

    fun setWastage(wastage: Double) {
        _estimationInput.value = _estimationInput.value.copy(wastagePercent = wastage)
    }

    fun setLaborComplexity(complexity: LaborComplexity) {
        _estimationInput.value = _estimationInput.value.copy(laborComplexity = complexity)
    }

    fun setProfitMargin(margin: Double) {
        _estimationInput.value = _estimationInput.value.copy(profitMarginPercent = margin)
    }

    fun setShippingFee(fee: Double) {
        _estimationInput.value = _estimationInput.value.copy(shippingAndHardwareFee = fee)
    }

    fun updateSubOptions(
        hiflexKey: String? = null,
        steelKey: String? = null,
        vTrimKey: String? = null,
        aluKey: String? = null,
        letterKey: String? = null,
        ledKey: String? = null,
        decalKey: String? = null,
        tarpaulinKey: String? = null,
        cncMicaKey: String? = null
    ) {
        val curr = _estimationInput.value
        _estimationInput.value = curr.copy(
            hiflexMaterialKey = hiflexKey ?: curr.hiflexMaterialKey,
            steelTypeKey = steelKey ?: curr.steelTypeKey,
            vTrimTypeKey = vTrimKey ?: curr.vTrimTypeKey,
            aluBrandKey = aluKey ?: curr.aluBrandKey,
            letterMaterialKey = letterKey ?: curr.letterMaterialKey,
            ledBrandKey = ledKey ?: curr.ledBrandKey,
            decalTypeKey = decalKey ?: curr.decalTypeKey,
            tarpaulinTypeKey = tarpaulinKey ?: curr.tarpaulinTypeKey,
            cncMicaKey = cncMicaKey ?: curr.cncMicaKey
        )
    }

    fun setSelectedMaterial(material: MaterialItem) {
        val curr = _estimationInput.value
        val cat = when {
            material.category.contains("Mica", ignoreCase = true) -> SignageCategory.CNC_LASER
            material.category.contains("Alu", ignoreCase = true) -> SignageCategory.ALU_LETTER
            material.category.contains("PVC", ignoreCase = true) || material.category.contains("Formex", ignoreCase = true) -> SignageCategory.CNC_LASER
            material.category.contains("Bạt", ignoreCase = true) -> SignageCategory.HIFLEX
            material.category.contains("Decal", ignoreCase = true) -> SignageCategory.DECAL
            material.category.contains("Kim Loại", ignoreCase = true) || material.category.contains("Inox", ignoreCase = true) -> SignageCategory.ALU_LETTER
            else -> curr.category
        }
        _estimationInput.value = curr.copy(
            selectedMaterialKey = material.priceKey,
            selectedMaterialName = "${material.brand} (${material.spec})",
            selectedMaterialUnit = material.unit,
            customMaterialPrice = material.price,
            category = cat
        )
        _userNotice.value = "Đã chọn vật tư: ${material.brand}"
    }

    fun toggleAdvertisingOption(
        ironFrame: Boolean? = null,
        lighting: Boolean? = null,
        edgeTrim: Boolean? = null,
        timerRelay: Boolean? = null
    ) {
        val curr = _estimationInput.value
        _estimationInput.value = curr.copy(
            includeIronFrame = ironFrame ?: curr.includeIronFrame,
            includeLighting = lighting ?: curr.includeLighting,
            includeEdgeTrim = edgeTrim ?: curr.includeEdgeTrim,
            includeTimerRelay = timerRelay ?: curr.includeTimerRelay
        )
    }

    fun updateSelectedMaterialPrice(newPrice: Double) {
        val curr = _estimationInput.value
        if (curr.selectedMaterialKey.isNotBlank()) {
            val updatedPrices = repository.unitPrices.value.toMutableMap()
            updatedPrices[curr.selectedMaterialKey] = newPrice
            repository.saveUnitPrices(updatedPrices)
            _estimationInput.value = curr.copy(customMaterialPrice = newPrice)
            _userNotice.value = "Đã cập nhật đơn giá vật tư thành ${newPrice.toLong()} đ"
        }
    }

    // Project Dossier
    fun saveCurrentToProject() {
        val res = estimationResult.value
        val inp = _estimationInput.value
        val displayTitle = if (inp.projectLocation.isNotBlank()) {
            "${res.itemName} (${inp.projectLocation})"
        } else {
            res.itemName
        }
        val item = ProjectDossierItem(
            name = displayTitle,
            categoryName = res.effectiveCategoryName.ifBlank { res.category.shortName },
            dimensions = "${res.width}m x ${res.height}m (${String.format("%.2f", res.billableArea)} m²)",
            quantity = res.quantity,
            totalCost = res.totalCost,
            finalPrice = res.finalCustomerPrice
        )
        repository.addProjectItem(item)
        _userNotice.value = "Đã thêm '${res.itemName}' vào Hồ sơ dự toán!"
    }

    fun addCustomProjectItem(item: ProjectDossierItem) {
        repository.addProjectItem(item)
        _userNotice.value = "Đã thêm hạng mục '${item.name}' vào Hồ sơ dự toán!"
    }

    fun updateProjectItem(updated: ProjectDossierItem) {
        repository.updateProjectItem(updated)
        _userNotice.value = "Đã cập nhật '${updated.name}'"
    }

    fun addBatchItemsToDossier(projectLabel: String?, items: List<ProjectDossierItem>) {
        if (items.isEmpty()) return
        if (!projectLabel.isNullOrBlank() && repository.projectItems.value.isEmpty()) {
            repository.updateProjectTitle(projectLabel)
        }
        repository.addProjectItems(items)
        _userNotice.value = "Đã lưu ${items.size} hạng mục thiết kế 3D vào Hồ Sơ Dự Toán!"
    }

    fun updateDossierQuoteSettings(settings: DossierQuoteSettings) {
        repository.saveDossierQuoteSettings(settings)
        _userNotice.value = "Đã cập nhật cấu hình báo giá hồ sơ!"
    }

    fun renameProjectTitleOnly(title: String) {
        val trimmed = title.trim()
        if (trimmed.isNotEmpty()) {
            repository.updateProjectTitle(trimmed)
            _userNotice.value = "Đã đổi tên dự án thành '$trimmed'"
        }
    }

    fun removeProjectItem(id: Long) {
        repository.removeProjectItem(id)
    }

    fun clearProject() {
        repository.clearProject()
        _userNotice.value = "Đã làm trống hồ sơ dự toán"
    }

    fun createNewProject(title: String) {
        repository.createNewProject(title)
        _userNotice.value = "Đã khởi tạo dự án: $title"
    }

    // Smart Calculator Table Actions
    fun addSmartQuoteLine(line: SmartQuoteLine) {
        _smartQuoteLines.value = _smartQuoteLines.value + line
        _userNotice.value = "Đã thêm '${line.title}' vào bảng tính giá"
    }

    fun updateSmartQuoteLine(updated: SmartQuoteLine) {
        _smartQuoteLines.value = _smartQuoteLines.value.map {
            if (it.id == updated.id) updated else it
        }
        _userNotice.value = "Đã cập nhật dòng '${updated.title}'"
    }

    fun removeSmartQuoteLine(id: Long) {
        _smartQuoteLines.value = _smartQuoteLines.value.filterNot { it.id == id }
    }

    fun clearSmartQuoteLines() {
        _smartQuoteLines.value = emptyList()
        _smartDiscountPercent.value = 0.0
        _smartVatPercent.value = 0.0
        _userNotice.value = "Đã xóa trắng bảng tính giá nhanh"
    }

    fun setSmartDiscount(percent: Double) {
        _smartDiscountPercent.value = percent.coerceIn(0.0, 90.0)
    }

    fun setSmartVat(percent: Double) {
        _smartVatPercent.value = percent.coerceIn(0.0, 20.0)
    }

    fun saveSmartLinesToDossier() {
        val lines = _smartQuoteLines.value
        if (lines.isEmpty()) return
        lines.forEach { line ->
            val factor = (1.0 - _smartDiscountPercent.value / 100.0) * (1.0 + _smartVatPercent.value / 100.0)
            val finalLinePrice = line.lineTotal * factor
            repository.addProjectItem(
                ProjectDossierItem(
                    name = line.title,
                    categoryName = "Tính Nhanh m²",
                    dimensions = "${String.format("%.2f", line.widthMeters)}m x ${String.format("%.2f", line.heightMeters)}m",
                    quantity = line.quantity,
                    totalCost = finalLinePrice * 0.72,
                    finalPrice = finalLinePrice
                )
            )
        }
        _userNotice.value = "Đã lưu ${lines.size} hạng mục từ bảng tính vào Hồ Sơ Dự Toán!"
    }

    fun applyDimensionsFromSmartCalc(title: String, widthM: Double, heightM: Double, qty: Int) {
        _estimationInput.value = _estimationInput.value.copy(
            itemName = title.ifBlank { _estimationInput.value.itemName },
            width = widthM.coerceAtLeast(0.1),
            height = heightM.coerceAtLeast(0.1),
            quantity = qty.coerceAtLeast(1)
        )
        _userNotice.value = "Đã chuyển kích thước ${String.format("%.2f", widthM)}m x ${String.format("%.2f", heightM)}m sang Bóc Tách"
    }

    // Material search & management
    fun setMaterialSearchQuery(query: String) {
        _materialSearchQuery.value = query
    }

    fun addOrUpdateMaterial(item: MaterialItem) {
        repository.addOrUpdateMaterial(item)
        _userNotice.value = "Đã lưu vật tư: ${item.brand}"
    }

    fun deleteMaterial(id: String) {
        repository.deleteMaterial(id)
        _userNotice.value = "Đã xóa vật tư $id"
    }

    // Settings
    fun updateUnitPrices(prices: Map<String, Double>) {
        repository.saveUnitPrices(prices)
        _userNotice.value = "Đã lưu cấu hình đơn giá phôi xưởng!"
    }

    fun resetDefaultPrices() {
        repository.resetDefaultPrices()
        _userNotice.value = "Đã khôi phục bảng giá mặc định"
    }

    fun saveUserApiKey(key: String) {
        val trimmed = key.trim()
        repository.saveUserApiKey(trimmed)
        _userNotice.value = if (trimmed.isEmpty()) {
            "Đã bật chế độ Gemini 3.8 Flash Miễn Phí (không cần API Key)"
        } else {
            "Đã lưu API Key Gemini riêng"
        }
    }

    fun switchToFreeGeminiMode() {
        repository.saveUserApiKey("")
        _userNotice.value = "Đã chuyển sang Gemini AI Miễn Phí (Firebase AI Logic)"
    }

    fun notifyMessage(message: String) {
        _userNotice.value = message
    }

    // Academy
    fun selectGuide(guide: AcademyGuide?) {
        _selectedGuide.value = guide
    }

    fun toggleGuideStep(guideId: String, stepNumber: Int) {
        repository.toggleGuideStep(guideId, stepNumber)
    }

    fun markGuideCompleted(guide: AcademyGuide) {
        repository.markGuideCompleted(guide.id, guide.steps.map { it.stepNumber })
        _userNotice.value = "Chúc mừng! Đã hoàn thành giáo trình '${guide.title.substringBefore(" —")}'"
    }

    fun resetGuideProgress(guideId: String) {
        repository.resetGuideProgress(guideId)
        _userNotice.value = "Đã đặt lại tiến độ thực hành của bài học"
    }

    fun askAiAboutGuide(guide: AcademyGuide) {
        _selectedGuide.value = null
        _currentTab.value = 1
        if (!_isAiLoading.value) {
            sendChatMessage(
                "Tôi đang thực hành bài '${guide.title}'. Hãy cho tôi 3 câu hỏi kiểm tra nhanh kiến thức thực tế tại xưởng và 2 mẹo xử lý lỗi nâng cao hay gặp nhất."
            )
        }
    }

    fun setChatTurboMode(enabled: Boolean) {
        _chatTurboMode.value = enabled
        _userNotice.value = if (enabled) {
            "Đã bật Chat Siêu Tốc (Gemini 3.5 Flash-Lite)"
        } else {
            "Đã bật Chat Chuyên Sâu (Gemini 3.8 Flash)"
        }
    }

    fun setAttachContextInChat(enabled: Boolean) {
        _attachContextInChat.value = enabled
    }

    fun warmUpAiChat() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                geminiService.warmUpChat()
            } catch (_: Exception) {}
        }
    }

    // AI Chat
    fun sendChatMessage(text: String) {
        val userQuery = text.trim()
        if (userQuery.isEmpty() || _isAiLoading.value) return

        val priorMessages = _chatMessages.value
        val userMsg = ChatMessage(isUser = true, text = userQuery)
        _chatMessages.value = priorMessages + userMsg

        val effectiveQuery = if (_attachContextInChat.value) {
            val inp = _estimationInput.value
            val res = estimationResult.value
            val locNote = if (inp.projectLocation.isNotBlank()) ", vị trí ${inp.projectLocation}" else ""
            "[Ngữ cảnh biểu mẫu đang tính: ${inp.itemName} (${res.effectiveCategoryName})$locNote, KT ${inp.width}m x ${inp.height}m, SL ${inp.quantity}, thực ${String.format("%.2f", res.netArea)}m², tính tiền ${String.format("%.2f", res.billableArea)}m², giá dự kiến ${res.finalCustomerPrice.toLong()} VNĐ]\n$userQuery"
        } else {
            userQuery
        }

        val useTurbo = _chatTurboMode.value

        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val apiKey = repository.userApiKey.value.trim()

                if (apiKey.isNotEmpty()) {
                    try {
                        val aiResponse = callGeminiApi(effectiveQuery, apiKey, useTurbo)
                        val aiMsg = ChatMessage(isUser = false, text = aiResponse)
                        _chatMessages.value = _chatMessages.value + aiMsg
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(TAG, "Direct Gemini REST call failed", e)
                        val errorMsg = ChatMessage(
                            isUser = false,
                            text = "⚠️ Lỗi API Key riêng: ${e.localizedMessage ?: "Không xác định"}. Bạn có thể xóa key để dùng miễn phí.",
                            isError = true,
                            retryQuery = userQuery
                        )
                        _chatMessages.value = _chatMessages.value + errorMsg
                    }
                } else {
                    val aiMsgId = java.util.UUID.randomUUID().toString()
                    var messageInserted = false
                    try {
                        geminiService.streamTechnicalAdvice(
                            query = effectiveQuery,
                            history = priorMessages,
                            useTurboMode = useTurbo
                        ).collect { partialText ->
                            val streamedMsg = ChatMessage(
                                id = aiMsgId,
                                isUser = false,
                                text = partialText
                            )
                            if (!messageInserted) {
                                _chatMessages.value = _chatMessages.value + streamedMsg
                                messageInserted = true
                            } else {
                                _chatMessages.value = _chatMessages.value.map {
                                    if (it.id == aiMsgId) streamedMsg else it
                                }
                            }
                        }
                        if (!messageInserted) {
                            val emptyErr = ChatMessage(
                                isUser = false,
                                text = "⚠️ Gemini AI miễn phí không trả về nội dung. Vui lòng bấm Thử lại.",
                                isError = true,
                                retryQuery = userQuery
                            )
                            _chatMessages.value = _chatMessages.value + emptyErr
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(TAG, "Firebase AI Logic chat failed", e)
                        val friendly = GeminiMaterialSuggestionService.formatFriendlyAiError(e)
                        val errorMsg = ChatMessage(
                            isUser = false,
                            text = "⚠️ $friendly",
                            isError = true,
                            retryQuery = userQuery
                        )
                        _chatMessages.value = _chatMessages.value + errorMsg
                    }
                }
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    private suspend fun callGeminiApi(query: String, apiKey: String, useTurbo: Boolean): String = withContext(Dispatchers.IO) {
        val systemInstruction = "Bạn là Trợ lý AI Đức Anh PRO, chuyên gia tư vấn bóc tách vật tư quảng cáo (biển bạt, decal, alu chữ nổi, hộp đèn, LED, CNC/Laser) và xử lý sự cố máy in RIP (Maintop, Photoprint) cho xưởng quảng cáo tại Việt Nam. Trả lời nhanh, súc tích bằng gạch đầu dòng thực tế."

        val jsonBody = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", query))
                    })
                })
            }
            put("contents", contentsArr)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstruction))
                })
            })
        }

        val modelId = if (useTurbo) "gemini-3.5-flash-lite" else "gemini-3.8-flash"
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            val errObj = try { JSONObject(responseBody).optJSONObject("error") } catch (_: Exception) { null }
            val errMsg = errObj?.optString("message") ?: "HTTP ${response.code}"
            throw Exception(errMsg)
        }

        val resJson = JSONObject(responseBody)
        val candidates = resJson.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        text ?: "Không có phản hồi từ AI."
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                isUser = false,
                text = "Đã làm mới cuộc hội thoại. Bạn có thể gửi câu hỏi hoặc chọn các câu hỏi gợi ý nhanh!"
            )
        )
    }
}
