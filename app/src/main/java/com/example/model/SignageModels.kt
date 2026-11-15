package com.example.model

enum class SignageCategory(val displayName: String, val shortName: String) {
    HIFLEX(
        displayName = "MỤC 1: Biển Bạt In KTS & UV (Hiflex / Đế Xám / 3M UV)",
        shortName = "Biển Bạt KTS / UV"
    ),
    TARPAULIN(
        displayName = "MỤC 2: Bạt Che Sự Kiện & Biển Bảng (Xanh Cam 2 Da / 3 Sọc)",
        shortName = "Bạt Che & Sự Kiện"
    ),
    DECAL(
        displayName = "MỤC 3: In Decal Quảng Cáo & Tem Bế (Decal Sữa / Trong / Lưới)",
        shortName = "Decal Quảng Cáo"
    ),
    ALU_LETTER(
        displayName = "MỤC 4: Biển Alu Ốp Chữ Nổi & LED (Alcorest / Inox / LED)",
        shortName = "Biển Alu & Chữ Nổi"
    ),
    CNC_LASER(
        displayName = "MỤC 5: Hộp Đèn Hút Nổi, Vách Kính Mica & CNC/Laser",
        shortName = "Hộp Đèn & CNC/Laser"
    )
}

enum class LaborComplexity(val displayName: String, val multiplier: Double) {
    EASY("Đơn giản: Dưới 3m, thi công mặt đất", 1.0),
    MEDIUM("Trung bình: Tầng 1-2, có giàn giáo", 1.35),
    HARD("Phức tạp: Đu dây, cẩu xe cao, ban đêm", 1.85)
}

data class BreakdownItem(
    val name: String,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double
)

data class AdvertisingChecklistItem(
    val id: String,
    val name: String,
    val category: String,
    val spec: String,
    val quantity: String,
    val unit: String,
    val estimatedCost: Double,
    val isIncludedInPrice: Boolean = true,
    val note: String = ""
)

data class EstimationInput(
    val category: SignageCategory = SignageCategory.HIFLEX,
    val selectedCategoryOptionId: String = "cat_hiflex",
    val customCategoryLabel: String = "",
    val itemName: String = "Biển Bạt In UV Không Gân Khung Sắt Kẽm",
    val width: Double = 6.0,
    val height: Double = 2.4,
    val quantity: Int = 1,
    val wastagePercent: Double = 8.0,
    val laborComplexity: LaborComplexity = LaborComplexity.MEDIUM,
    val profitMarginPercent: Double = 35.0,
    val shippingAndHardwareFee: Double = 350000.0,
    // Project Details:
    val clientName: String = "",
    val projectLocation: String = "",
    val installationDate: String = "",
    val projectNotes: String = "",
    // Area & Quotation terms:
    val minOneM2PerPiece: Boolean = false,
    val discountPercent: Double = 0.0,
    val vatPercent: Double = 0.0,
    val customUnitPriceOverridePerM2: Double? = null,
    // Dynamic Sub-options:
    val selectedMaterialKey: String = "",
    val selectedMaterialName: String = "",
    val selectedMaterialUnit: String = "m²",
    val customMaterialPrice: Double? = null,
    val hiflexMaterialKey: String = "bat_khonggan_uv_m2",
    val steelTypeKey: String = "steel_vietthanh_30",
    val vTrimTypeKey: String = "v_nhom_m",
    val aluBrandKey: String = "alu_alcorest_pet_360k",
    val letterMaterialKey: String = "inox_304_gold_m2",
    val ledBrandKey: String = "led_module_samsung",
    val decalTypeKey: String = "decal_trang_m2",
    val tarpaulinTypeKey: String = "bat_xanhcam_150g_m2",
    val cncMicaKey: String = "mica_dailoan_3mm_sheet",
    val useBacksheet: Boolean = true,
    val includeLighting: Boolean = true,
    val includeIronFrame: Boolean = true,
    val includeEdgeTrim: Boolean = true,
    val includeTimerRelay: Boolean = true
)

data class EstimationResult(
    val itemName: String,
    val category: SignageCategory,
    val width: Double,
    val height: Double,
    val quantity: Int,
    val netArea: Double,
    val grossAreaWithWastage: Double,
    val perimeter: Double,
    val breakdownItems: List<BreakdownItem>,
    val totalMaterialCost: Double,
    val laborCost: Double,
    val shippingCost: Double,
    val totalCost: Double,
    val profitAmount: Double,
    val finalCustomerPrice: Double,
    val unitPricePerM2: Double,
    val aiEngineeringTips: List<String> = emptyList(),
    val advertisingChecklist: List<AdvertisingChecklistItem> = emptyList(),
    val singlePieceArea: Double = if (quantity > 0) netArea / quantity else netArea,
    val billableArea: Double = netArea,
    val subtotalCustomerPrice: Double = finalCustomerPrice,
    val discountAmount: Double = 0.0,
    val vatAmount: Double = 0.0,
    val effectiveCategoryName: String = category.shortName
)

data class ProjectDossierItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val categoryName: String,
    val dimensions: String,
    val quantity: Int,
    val totalCost: Double,
    val finalPrice: Double,
    val createdAt: Long = System.currentTimeMillis()
)

data class MaterialItem(
    val id: String,
    val brand: String,
    val spec: String,
    val unit: String,
    val priceKey: String,
    val price: Double,
    val category: String
)

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val content: String,
    val techNote: String = "",
    val detailedChecklist: List<String> = emptyList(),
    val expectedResult: String = "",
    val proTip: String = ""
)

data class AcademyGuide(
    val id: String,
    val title: String,
    val category: String,
    val level: String,
    val iconName: String,
    val summary: String,
    val steps: List<GuideStep>,
    val durationMinutes: Int = 30,
    val prerequisites: String = "",
    val shortcutsAndSpecs: List<Pair<String, String>> = emptyList(),
    val commonMistakes: List<String> = emptyList(),
    val practiceExercise: String = ""
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val retryQuery: String? = null
)

data class AiSuggestedMaterial(
    val id: String,
    val name: String,
    val category: String,
    val spec: String,
    val unit: String,
    val estimatedQuantity: Double,
    val estimatedUnitPrice: Double,
    val reason: String,
    val isPrimaryMaterial: Boolean = false
)

data class AiMaterialSuggestionResult(
    val engineeringSummary: String,
    val constructionNotes: List<String>,
    val materials: List<AiSuggestedMaterial>
)

sealed interface AiSuggestionUiState {
    data object Idle : AiSuggestionUiState
    data object Loading : AiSuggestionUiState
    data class Success(val data: AiMaterialSuggestionResult) : AiSuggestionUiState
    data class Error(val message: String) : AiSuggestionUiState
}

