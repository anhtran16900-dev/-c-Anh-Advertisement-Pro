package com.example.model

import android.graphics.Bitmap

enum class Design3dStyle(
    val shortLabel: String,
    val fullName: String,
    val englishRenderHint: String
) {
    ALU_INOX_LED(
        shortLabel = "Alu + Inox LED",
        fullName = "Mặt dựng Alu PVDF + Chữ nổi Inox vàng/trắng LED hắt chân",
        englishRenderHint = "modern brushed dark aluminum composite facade panel, 3D mirror gold stainless steel channel letters with warm white halo LED backlighting"
    ),
    MICA_HUT_NOI(
        shortLabel = "Mica Hút Nổi",
        fullName = "Biển nền Alu/Fomex + Chữ nổi Mica Đài Loan mặt sáng LED",
        englishRenderHint = "sleek matte architectural facade with luminous 3D thermoformed acrylic face-lit channel letters glowing vividly"
    ),
    HIFLEX_UV_3M(
        shortLabel = "Hộp Đèn Bạt 3M/UV",
        fullName = "Hộp đèn xuyên sáng bạt 3M / Không gân in UV siêu sắc nét",
        englishRenderHint = "premium tensioned seamless UV backlit lightbox signage cabinet framed with slim anodized aluminum trim and crisp illuminated typography"
    ),
    WOOD_LAM_SONG(
        shortLabel = "Lam Sóng Gỗ LED",
        fullName = "Nền lam sóng nhựa giả gỗ ngoài trời + Chữ nổi LED sang trọng",
        englishRenderHint = "contemporary outdoor wood-slat architectural louver cladding storefront with contrasting 3D matte black and gold illuminated signage letters"
    ),
    PANO_HO_TRO(
        shortLabel = "Pano Khổ Lớn",
        fullName = "Biển Pano / Mặt tiền cỡ lớn khung thép + Đèn pha LED rọi",
        englishRenderHint = "large-format commercial storefront steel-framed billboard fascia with high-output architectural floodlights and bold high-contrast graphics"
    )
}

enum class LightingMode3d(val label: String, val englishHint: String) {
    NIGHT_LUX("Phối cảnh ban đêm sáng rực LED", "dusk twilight architectural exterior lighting, glowing LED signage and warm storefront interior light"),
    DAY_CLEAN("Phối cảnh ban ngày nắng tự nhiên", "bright natural daylight architectural photography, crisp shadows and metallic material reflections"),
    BOTH_CONTRAST("Ban ngày góc nghiêng 3D nổi khối", "late golden hour architectural exterior shot highlighting 3D letter extrusion depth and subtle glowing LED accents")
}

data class Design3dPromptConfig(
    val brandText: String = "",
    val slogan: String = "",
    val businessType: String = "",
    val widthM: Double = 5.0,
    val heightM: Double = 1.8,
    val style: Design3dStyle = Design3dStyle.ALU_INOX_LED,
    val primaryColors: String = "Đen nhám - Vàng Gold - Trắng sáng",
    val lightingMode: LightingMode3d = LightingMode3d.NIGHT_LUX,
    val extraNotes: String = ""
)

data class Design3dQuoteLine(
    val id: Long = System.nanoTime(),
    val name: String,
    val spec: String,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double
) {
    val lineTotal: Double
        get() = (quantity.coerceAtLeast(0.0) * unitPrice.coerceAtLeast(0.0))
}

data class Design3dQuoteTerms(
    val companyName: String = "ĐỨC ANH ADVERTISEMENT PRO",
    val companyPhone: String = "0988.xxx.xxx",
    val clientName: String = "Quý Khách Hàng",
    val clientPhone: String = "",
    val projectAddress: String = "",
    val warrantyMonths: Int = 24,
    val productionDays: Int = 5,
    val depositPercent: Int = 40,
    val discountPercent: Double = 0.0,
    val vatPercent: Double = 8.0,
    val extraNotes: String = "Đã bao gồm chi phí thiết kế 3D, gia công tại xưởng và lắp đặt hoàn thiện."
)

data class Design3dQuoteSummary(
    val subtotal: Double,
    val discountAmount: Double,
    val afterDiscount: Double,
    val vatAmount: Double,
    val grandTotal: Double,
    val depositAmount: Double,
    val balanceAmount: Double
) {
    companion object {
        fun calculate(
            lines: List<Design3dQuoteLine>,
            terms: Design3dQuoteTerms
        ): Design3dQuoteSummary {
            val subtotal = lines.sumOf { it.lineTotal }
            val discPct = terms.discountPercent.coerceIn(0.0, 90.0)
            val discountAmount = subtotal * (discPct / 100.0)
            val afterDiscount = (subtotal - discountAmount).coerceAtLeast(0.0)
            val vatPct = terms.vatPercent.coerceIn(0.0, 30.0)
            val vatAmount = afterDiscount * (vatPct / 100.0)
            val grandTotal = afterDiscount + vatAmount
            val depPct = terms.depositPercent.coerceIn(0, 100)
            val depositAmount = grandTotal * (depPct / 100.0)
            val balanceAmount = (grandTotal - depositAmount).coerceAtLeast(0.0)
            return Design3dQuoteSummary(
                subtotal = subtotal,
                discountAmount = discountAmount,
                afterDiscount = afterDiscount,
                vatAmount = vatAmount,
                grandTotal = grandTotal,
                depositAmount = depositAmount,
                balanceAmount = balanceAmount
            )
        }
    }
}

data class Design3dSpecResult(
    val conceptTitle: String,
    val facadeAnalysis: String,
    val visual3dSpec: String,
    val lightingPlan: String,
    val installationMethod: String,
    val estimatedDays: Int,
    val renderPromptEn: String,
    val suggestedLines: List<Design3dQuoteLine>
)

sealed interface Design3dSpecUiState {
    data object Idle : Design3dSpecUiState
    data object Loading : Design3dSpecUiState
    data class Success(val data: Design3dSpecResult) : Design3dSpecUiState
    data class Error(val message: String) : Design3dSpecUiState
}

sealed interface Design3dRenderUiState {
    data object Idle : Design3dRenderUiState
    data object Loading : Design3dRenderUiState
    data class Success(val bitmap: Bitmap) : Design3dRenderUiState
    data class Error(val message: String) : Design3dRenderUiState
}

data class DossierQuoteSettings(
    val companyName: String = "ĐỨC ANH ADVERTISEMENT PRO",
    val companyPhone: String = "0988.xxx.xxx",
    val clientName: String = "Quý Khách Hàng",
    val discountPercent: Double = 0.0,
    val vatPercent: Double = 0.0,
    val warrantyMonths: Int = 24,
    val depositPercent: Int = 40,
    val notes: String = "Báo giá có hiệu lực trong 15 ngày kể từ ngày phát hành."
)
