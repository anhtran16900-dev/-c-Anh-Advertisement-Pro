package com.example.model

data class EditableCategoryOption(
    val id: String,
    val shortName: String,
    val displayName: String,
    val baseCategory: SignageCategory,
    val isBuiltIn: Boolean = false
)

data class InstallLocationPreset(
    val id: String,
    val name: String,
    val suggestedComplexity: LaborComplexity? = null,
    val isBuiltIn: Boolean = false
)

object EstimatorCustomDefaults {

    fun builtInCategoryId(category: SignageCategory): String = when (category) {
        SignageCategory.HIFLEX -> "cat_hiflex"
        SignageCategory.TARPAULIN -> "cat_tarpaulin"
        SignageCategory.DECAL -> "cat_decal"
        SignageCategory.ALU_LETTER -> "cat_alu_letter"
        SignageCategory.CNC_LASER -> "cat_cnc_laser"
    }

    fun defaultCategoryOptions(): List<EditableCategoryOption> = SignageCategory.values().map { cat ->
        EditableCategoryOption(
            id = builtInCategoryId(cat),
            shortName = cat.shortName,
            displayName = cat.displayName,
            baseCategory = cat,
            isBuiltIn = true
        )
    }

    val defaultInstallLocations: List<InstallLocationPreset> = listOf(
        InstallLocationPreset(
            id = "loc_facade_t1",
            name = "Mặt tiền tầng 1 (Dưới 3m)",
            suggestedComplexity = LaborComplexity.EASY,
            isBuiltIn = true
        ),
        InstallLocationPreset(
            id = "loc_facade_t2",
            name = "Mặt tiền tầng 2 (Dùng giàn giáo)",
            suggestedComplexity = LaborComplexity.MEDIUM,
            isBuiltIn = true
        ),
        InstallLocationPreset(
            id = "loc_rooftop_crane",
            name = "Trên cao / Mái tôn (Xe cẩu / Đu dây)",
            suggestedComplexity = LaborComplexity.HARD,
            isBuiltIn = true
        ),
        InstallLocationPreset(
            id = "loc_indoor_showroom",
            name = "Trong nhà / Showroom / Quầy lễ tân",
            suggestedComplexity = LaborComplexity.EASY,
            isBuiltIn = true
        ),
        InstallLocationPreset(
            id = "loc_glass_office",
            name = "Vách kính cửa hàng / Văn phòng",
            suggestedComplexity = LaborComplexity.EASY,
            isBuiltIn = true
        ),
        InstallLocationPreset(
            id = "loc_outdoor_pillar",
            name = "Cột trụ Pano / Hông tường ngoài trời",
            suggestedComplexity = LaborComplexity.MEDIUM,
            isBuiltIn = true
        )
    )
}
