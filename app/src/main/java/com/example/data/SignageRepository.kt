package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ChatMessage
import com.example.model.DossierQuoteSettings
import com.example.model.EditableCategoryOption
import com.example.model.EstimatorCustomDefaults
import com.example.model.InstallLocationPreset
import com.example.model.LaborComplexity
import com.example.model.MaterialItem
import com.example.model.ProjectDossierItem
import com.example.model.SignageCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SignageRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("duc_anh_ad_pro_prefs", Context.MODE_PRIVATE)

    private val _unitPrices = MutableStateFlow<Map<String, Double>>(loadUnitPrices())
    val unitPrices: StateFlow<Map<String, Double>> = _unitPrices.asStateFlow()

    private val _materialCatalog = MutableStateFlow<List<MaterialItem>>(loadMaterials())
    val materialCatalog: StateFlow<List<MaterialItem>> = _materialCatalog.asStateFlow()

    private val _projectTitle = MutableStateFlow(loadProjectTitle())
    val projectTitle: StateFlow<String> = _projectTitle.asStateFlow()

    private val _projectItems = MutableStateFlow<List<ProjectDossierItem>>(loadProjectItems())
    val projectItems: StateFlow<List<ProjectDossierItem>> = _projectItems.asStateFlow()

    private val _userApiKey = MutableStateFlow(loadUserApiKey())
    val userApiKey: StateFlow<String> = _userApiKey.asStateFlow()

    private val _completedGuideSteps = MutableStateFlow<Set<String>>(loadCompletedGuideSteps())
    val completedGuideSteps: StateFlow<Set<String>> = _completedGuideSteps.asStateFlow()

    private val _dossierQuoteSettings = MutableStateFlow(loadDossierQuoteSettings())
    val dossierQuoteSettings: StateFlow<DossierQuoteSettings> = _dossierQuoteSettings.asStateFlow()

    private val _categoryOptions = MutableStateFlow(loadCategoryOptions())
    val categoryOptions: StateFlow<List<EditableCategoryOption>> = _categoryOptions.asStateFlow()

    private val _itemPresetsByCategoryId = MutableStateFlow(loadItemPresetsByCategoryId())
    val itemPresetsByCategoryId: StateFlow<Map<String, List<String>>> = _itemPresetsByCategoryId.asStateFlow()

    private val _installLocations = MutableStateFlow(loadInstallLocations())
    val installLocations: StateFlow<List<InstallLocationPreset>> = _installLocations.asStateFlow()

    private fun loadUnitPrices(): Map<String, Double> {
        val jsonStr = prefs.getString("custom_unit_prices", null)
        if (jsonStr.isNullOrEmpty()) return DefaultData.defaultUnitPrices

        val map = DefaultData.defaultUnitPrices.toMutableMap()
        try {
            val json = JSONObject(jsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.optDouble(key, map[key] ?: 0.0)
            }
        } catch (_: Exception) {}
        return map
    }

    fun saveUnitPrices(prices: Map<String, Double>) {
        _unitPrices.value = prices
        try {
            val json = JSONObject()
            for ((k, v) in prices) {
                json.put(k, v)
            }
            prefs.edit().putString("custom_unit_prices", json.toString()).apply()
        } catch (_: Exception) {}
    }

    fun resetDefaultPrices() {
        prefs.edit().remove("custom_unit_prices").apply()
        _unitPrices.value = DefaultData.defaultUnitPrices
    }

    private fun loadMaterials(): List<MaterialItem> {
        val jsonStr = prefs.getString("custom_materials", null)
        if (jsonStr.isNullOrEmpty()) return DefaultData.defaultMaterials

        try {
            val list = mutableListOf<MaterialItem>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MaterialItem(
                        id = obj.getString("id"),
                        brand = obj.getString("brand"),
                        spec = obj.getString("spec"),
                        unit = obj.getString("unit"),
                        priceKey = obj.getString("priceKey"),
                        price = obj.getDouble("price"),
                        category = obj.optString("category", "Vật Tư")
                    )
                )
            }
            return if (list.isNotEmpty()) list else DefaultData.defaultMaterials
        } catch (_: Exception) {
            return DefaultData.defaultMaterials
        }
    }

    fun saveMaterials(materials: List<MaterialItem>) {
        _materialCatalog.value = materials
        try {
            val array = JSONArray()
            for (m in materials) {
                val obj = JSONObject().apply {
                    put("id", m.id)
                    put("brand", m.brand)
                    put("spec", m.spec)
                    put("unit", m.unit)
                    put("priceKey", m.priceKey)
                    put("price", m.price)
                    put("category", m.category)
                }
                array.put(obj)
            }
            prefs.edit().putString("custom_materials", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addOrUpdateMaterial(item: MaterialItem) {
        val current = _materialCatalog.value.toMutableList()
        val index = current.indexOfFirst { it.id.equals(item.id, ignoreCase = true) }
        if (index >= 0) {
            current[index] = item
        } else {
            current.add(0, item)
        }
        saveMaterials(current)

        // Also update prices map
        val updatedPrices = _unitPrices.value.toMutableMap()
        updatedPrices[item.priceKey] = item.price
        saveUnitPrices(updatedPrices)
    }

    fun deleteMaterial(id: String) {
        val current = _materialCatalog.value.filterNot { it.id.equals(id, ignoreCase = true) }
        saveMaterials(current)
    }

    private fun loadProjectTitle(): String {
        return prefs.getString("project_title", "Dự Án Showroom Quảng Cáo") ?: "Dự Án Showroom Quảng Cáo"
    }

    fun updateProjectTitle(title: String) {
        _projectTitle.value = title
        prefs.edit().putString("project_title", title).apply()
    }

    private fun loadProjectItems(): List<ProjectDossierItem> {
        val jsonStr = prefs.getString("project_items", null) ?: return emptyList()
        try {
            val list = mutableListOf<ProjectDossierItem>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ProjectDossierItem(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        categoryName = obj.getString("categoryName"),
                        dimensions = obj.getString("dimensions"),
                        quantity = obj.getInt("quantity"),
                        totalCost = obj.getDouble("totalCost"),
                        finalPrice = obj.getDouble("finalPrice"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            return list
        } catch (_: Exception) {
            return emptyList()
        }
    }

    private fun saveProjectItemsInternal(items: List<ProjectDossierItem>) {
        _projectItems.value = items
        try {
            val array = JSONArray()
            for (it in items) {
                val obj = JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("categoryName", it.categoryName)
                    put("dimensions", it.dimensions)
                    put("quantity", it.quantity)
                    put("totalCost", it.totalCost)
                    put("finalPrice", it.finalPrice)
                    put("createdAt", it.createdAt)
                }
                array.put(obj)
            }
            prefs.edit().putString("project_items", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addProjectItem(item: ProjectDossierItem) {
        val current = _projectItems.value.toMutableList()
        current.add(item)
        saveProjectItemsInternal(current)
    }

    fun addProjectItems(items: List<ProjectDossierItem>) {
        if (items.isEmpty()) return
        val current = _projectItems.value.toMutableList()
        current.addAll(items)
        saveProjectItemsInternal(current)
    }

    fun updateProjectItem(updated: ProjectDossierItem) {
        val current = _projectItems.value.map { existing ->
            if (existing.id == updated.id) updated else existing
        }
        saveProjectItemsInternal(current)
    }

    fun removeProjectItem(id: Long) {
        val current = _projectItems.value.filterNot { it.id == id }
        saveProjectItemsInternal(current)
    }

    fun clearProject() {
        saveProjectItemsInternal(emptyList())
    }

    fun createNewProject(title: String) {
        updateProjectTitle(title)
        clearProject()
    }

    private fun loadDossierQuoteSettings(): DossierQuoteSettings {
        val jsonStr = prefs.getString("dossier_quote_settings", null) ?: return DossierQuoteSettings()
        return try {
            val obj = JSONObject(jsonStr)
            DossierQuoteSettings(
                companyName = obj.optString("companyName", "ĐỨC ANH ADVERTISEMENT PRO"),
                companyPhone = obj.optString("companyPhone", "0988.xxx.xxx"),
                clientName = obj.optString("clientName", "Quý Khách Hàng"),
                discountPercent = obj.optDouble("discountPercent", 0.0),
                vatPercent = obj.optDouble("vatPercent", 0.0),
                warrantyMonths = obj.optInt("warrantyMonths", 24),
                depositPercent = obj.optInt("depositPercent", 40),
                notes = obj.optString("notes", "Báo giá có hiệu lực trong 15 ngày kể từ ngày phát hành.")
            )
        } catch (_: Exception) {
            DossierQuoteSettings()
        }
    }

    fun saveDossierQuoteSettings(settings: DossierQuoteSettings) {
        _dossierQuoteSettings.value = settings
        try {
            val obj = JSONObject().apply {
                put("companyName", settings.companyName)
                put("companyPhone", settings.companyPhone)
                put("clientName", settings.clientName)
                put("discountPercent", settings.discountPercent)
                put("vatPercent", settings.vatPercent)
                put("warrantyMonths", settings.warrantyMonths)
                put("depositPercent", settings.depositPercent)
                put("notes", settings.notes)
            }
            prefs.edit().putString("dossier_quote_settings", obj.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadUserApiKey(): String {
        return prefs.getString("user_gemini_api_key", "") ?: ""
    }

    fun saveUserApiKey(key: String) {
        _userApiKey.value = key
        prefs.edit().putString("user_gemini_api_key", key).apply()
    }

    private fun loadCompletedGuideSteps(): Set<String> {
        val stored = prefs.getStringSet("completed_guide_steps", null) ?: return emptySet()
        return HashSet(stored)
    }

    private fun persistCompletedGuideSteps(next: Set<String>) {
        val snapshot = HashSet(next)
        _completedGuideSteps.value = snapshot
        prefs.edit().putStringSet("completed_guide_steps", snapshot).apply()
    }

    fun toggleGuideStep(guideId: String, stepNumber: Int) {
        val key = "${guideId}_$stepNumber"
        val updated = _completedGuideSteps.value.toMutableSet()
        if (!updated.add(key)) {
            updated.remove(key)
        }
        persistCompletedGuideSteps(updated)
    }

    fun markGuideCompleted(guideId: String, stepNumbers: List<Int>) {
        val updated = _completedGuideSteps.value.toMutableSet()
        stepNumbers.forEach { stepNumber ->
            updated.add("${guideId}_$stepNumber")
        }
        persistCompletedGuideSteps(updated)
    }

    fun resetGuideProgress(guideId: String) {
        val prefix = "${guideId}_"
        val updated = _completedGuideSteps.value.filterNot { it.startsWith(prefix) }.toSet()
        persistCompletedGuideSteps(updated)
    }

    // --- Editable Categories ---

    private fun loadCategoryOptions(): List<EditableCategoryOption> {
        val jsonStr = prefs.getString("custom_category_options", null)
        if (jsonStr.isNullOrEmpty()) return EstimatorCustomDefaults.defaultCategoryOptions()
        return try {
            val list = mutableListOf<EditableCategoryOption>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val baseName = obj.optString("baseCategory", SignageCategory.HIFLEX.name)
                val baseCat = SignageCategory.values().firstOrNull { it.name == baseName } ?: SignageCategory.HIFLEX
                list.add(
                    EditableCategoryOption(
                        id = obj.getString("id"),
                        shortName = obj.getString("shortName"),
                        displayName = obj.getString("displayName"),
                        baseCategory = baseCat,
                        isBuiltIn = obj.optBoolean("isBuiltIn", false)
                    )
                )
            }
            if (list.isNotEmpty()) list else EstimatorCustomDefaults.defaultCategoryOptions()
        } catch (_: Exception) {
            EstimatorCustomDefaults.defaultCategoryOptions()
        }
    }

    private fun saveCategoryOptions(options: List<EditableCategoryOption>) {
        _categoryOptions.value = options
        try {
            val array = JSONArray()
            for (opt in options) {
                val obj = JSONObject().apply {
                    put("id", opt.id)
                    put("shortName", opt.shortName)
                    put("displayName", opt.displayName)
                    put("baseCategory", opt.baseCategory.name)
                    put("isBuiltIn", opt.isBuiltIn)
                }
                array.put(obj)
            }
            prefs.edit().putString("custom_category_options", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addOrUpdateCategoryOption(option: EditableCategoryOption) {
        val current = _categoryOptions.value.toMutableList()
        val idx = current.indexOfFirst { it.id == option.id }
        if (idx >= 0) {
            current[idx] = option
        } else {
            current.add(option)
        }
        saveCategoryOptions(current)
    }

    fun deleteCategoryOption(id: String): Boolean {
        val current = _categoryOptions.value
        val target = current.firstOrNull { it.id == id } ?: return false
        if (target.isBuiltIn || current.size <= 1) return false
        saveCategoryOptions(current.filterNot { it.id == id })
        return true
    }

    fun resetDefaultCategories() {
        prefs.edit().remove("custom_category_options").apply()
        _categoryOptions.value = EstimatorCustomDefaults.defaultCategoryOptions()
    }

    // --- Editable Item Name Presets ---

    private fun defaultItemPresetsMap(): Map<String, List<String>> {
        return DefaultData.presetItemNames.mapKeys { (cat, _) ->
            EstimatorCustomDefaults.builtInCategoryId(cat)
        }
    }

    private fun loadItemPresetsByCategoryId(): Map<String, List<String>> {
        val defaults = defaultItemPresetsMap().toMutableMap()
        val jsonStr = prefs.getString("custom_item_presets_map", null) ?: return defaults
        try {
            val obj = JSONObject(jsonStr)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val arr = obj.optJSONArray(key) ?: continue
                val items = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    val str = arr.optString(i, "").trim()
                    if (str.isNotEmpty()) items.add(str)
                }
                defaults[key] = items
            }
        } catch (_: Exception) {}
        return defaults
    }

    private fun saveItemPresetsByCategoryId(map: Map<String, List<String>>) {
        _itemPresetsByCategoryId.value = map
        try {
            val obj = JSONObject()
            for ((k, list) in map) {
                val arr = JSONArray()
                list.forEach { arr.put(it) }
                obj.put(k, arr)
            }
            prefs.edit().putString("custom_item_presets_map", obj.toString()).apply()
        } catch (_: Exception) {}
    }

    fun getItemPresets(categoryId: String, baseCategory: SignageCategory): List<String> {
        return _itemPresetsByCategoryId.value[categoryId]
            ?: DefaultData.presetItemNames[baseCategory]
            ?: emptyList()
    }

    fun addItemPreset(categoryId: String, baseCategory: SignageCategory, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val map = _itemPresetsByCategoryId.value.toMutableMap()
        val list = getItemPresets(categoryId, baseCategory).toMutableList()
        if (list.none { it.equals(trimmed, ignoreCase = true) }) {
            list.add(0, trimmed)
            map[categoryId] = list
            saveItemPresetsByCategoryId(map)
        }
    }

    fun updateItemPreset(categoryId: String, baseCategory: SignageCategory, oldName: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        val map = _itemPresetsByCategoryId.value.toMutableMap()
        val list = getItemPresets(categoryId, baseCategory).map { existing ->
            if (existing == oldName) trimmed else existing
        }.distinct()
        map[categoryId] = list
        saveItemPresetsByCategoryId(map)
    }

    fun deleteItemPreset(categoryId: String, baseCategory: SignageCategory, name: String) {
        val map = _itemPresetsByCategoryId.value.toMutableMap()
        val list = getItemPresets(categoryId, baseCategory).filterNot { it == name }
        map[categoryId] = list
        saveItemPresetsByCategoryId(map)
    }

    // --- Editable Installation Locations ---

    private fun loadInstallLocations(): List<InstallLocationPreset> {
        val jsonStr = prefs.getString("custom_install_locations", null)
        if (jsonStr.isNullOrEmpty()) return EstimatorCustomDefaults.defaultInstallLocations
        return try {
            val list = mutableListOf<InstallLocationPreset>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val compName = obj.optString("suggestedComplexity", "")
                val comp = LaborComplexity.values().firstOrNull { it.name == compName }
                list.add(
                    InstallLocationPreset(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        suggestedComplexity = comp,
                        isBuiltIn = obj.optBoolean("isBuiltIn", false)
                    )
                )
            }
            if (list.isNotEmpty()) list else EstimatorCustomDefaults.defaultInstallLocations
        } catch (_: Exception) {
            EstimatorCustomDefaults.defaultInstallLocations
        }
    }

    private fun saveInstallLocations(locations: List<InstallLocationPreset>) {
        _installLocations.value = locations
        try {
            val array = JSONArray()
            for (loc in locations) {
                val obj = JSONObject().apply {
                    put("id", loc.id)
                    put("name", loc.name)
                    put("suggestedComplexity", loc.suggestedComplexity?.name ?: "")
                    put("isBuiltIn", loc.isBuiltIn)
                }
                array.put(obj)
            }
            prefs.edit().putString("custom_install_locations", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addOrUpdateInstallLocation(preset: InstallLocationPreset) {
        val trimmedName = preset.name.trim()
        if (trimmedName.isEmpty()) return
        val normalized = preset.copy(name = trimmedName)
        val current = _installLocations.value.toMutableList()
        val idx = current.indexOfFirst { it.id == normalized.id }
        if (idx >= 0) {
            current[idx] = normalized
        } else {
            current.add(0, normalized)
        }
        saveInstallLocations(current)
    }

    fun deleteInstallLocation(id: String) {
        val current = _installLocations.value
        if (current.size <= 1) return
        saveInstallLocations(current.filterNot { it.id == id })
    }
}
