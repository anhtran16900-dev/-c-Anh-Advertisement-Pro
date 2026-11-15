package com.example.model

enum class BoardDesignFamily(
    val shortTitle: String,
    val fullTitle: String,
    val badgeText: String
) {
    TARP_BANNER(
        shortTitle = "Bảng Bạt",
        fullTitle = "Thiết Kế Bảng Bạt Các Loại",
        badgeText = "Hiflex • Không gân UV • 3M • Hộp đèn"
    ),
    ALU_3D_LETTER(
        shortTitle = "Alu Chữ Nổi",
        fullTitle = "Thiết Kế Bảng Alu Chữ Nổi Các Loại",
        badgeText = "Alu Alcorest/Triều Chen • Inox • Mica • LED"
    ),
    DECAL_STICKER(
        shortTitle = "Bảng Decal",
        fullTitle = "Thiết Kế Bảng Decal Các Loại",
        badgeText = "Decal Sữa/Trong/Lưới/Mờ • PP • Bồi Formex"
    )
}

enum class BoardColorTheme(
    val label: String,
    val bgHexLong: Long,
    val textHexLong: Long,
    val accentHexLong: Long
) {
    NAVY_GOLD("Xanh Navy - Chữ Vàng", 0xFF0F172A, 0xFFFACC15, 0xFF38BDF8),
    BLACK_GOLD("Đen Nhám - Vàng Gold", 0xFF111827, 0xFFFBBF24, 0xFFFDE68A),
    RED_WHITE("Đỏ Đô - Trắng Sáng", 0xFF7F1D1D, 0xFFFFFFFF, 0xFFFDE047),
    ROYAL_BLUE_WHITE("Xanh Dương - Trắng", 0xFF1E3A8A, 0xFFFFFFFF, 0xFF22D3EE),
    EMERALD_CREAM("Xanh Ngọc - Kem Vàng", 0xFF064E3B, 0xFFFEF3C7, 0xFF34D399),
    WHITE_NAVY("Trắng Sứ - Chữ Đen/Xanh", 0xFFF1F5F9, 0xFF0F172A, 0xFF0284C7)
}

// === PART 1: TARP / HIFLEX BANNER ===
enum class TarpProductType(
    val label: String,
    val spec: String,
    val printOnlyRatePerM2: Double
) {
    HIFLEX_STANDARD("Bạt Hiflex 0.32mm", "In KTS 4 màu, dùng băng rôn / biển ngắn hạn", 55000.0),
    HIFLEX_GREY_BACK("Bạt Đế Xám 2 Da", "Chống xuyên sáng che mặt tiền cũ cực tốt", 85000.0),
    NO_SEAM_UV("Bạt Không Gân UV", "Bạt phẳng mịn, in UV 2 lớp siêu bền màu 3-5 năm", 180000.0),
    THREE_M_BACKLIT("Bạt 3M Mỹ Xuyên Sáng", "Bạt hộp đèn cao cấp Showroom/Ngân hàng", 480000.0),
    EVENT_BANNER("Băng Rôn / Phướn Sự Kiện", "Cắt gấp mép xỏ cây hoặc đóng khoen treo cột", 65000.0),
    DOUBLE_FACE_LIGHTBOX("Hộp Đèn Bạt 2 Mặt", "Hộp đèn bạt xuyên sáng đứng/treo hông", 210000.0)
}

enum class TarpFinishMode(
    val label: String,
    val frameAddPerM2: Double,
    val needsIronFrame: Boolean,
    val needsLighting: Boolean
) {
    PRINT_ONLY("Chỉ in bạt thành phẩm", 0.0, false, false),
    GROMMET_OR_POLE("Đóng khoen / Gấp mép xỏ cây", 15000.0, false, false),
    IRON_FRAME_SIGN("Căng khung sắt kẽm 25x25 + Viền V", 145000.0, true, false),
    FRAME_WITH_FLOODLIGHT("Khung sắt kẽm + Đèn pha LED rọi", 185000.0, true, true),
    LED_LIGHTBOX("Hộp đèn tôn/alu + LED thanh hắt trong", 430000.0, true, true)
}

// === PART 2: ALU + 3D LETTERS ===
enum class AluPanelType(
    val label: String,
    val spec: String,
    val installedRatePerM2: Double
) {
    ALCOREST_INDOOR("Alu Alcorest Trong Nhà (PE)", "Độ dày tấm 2-3mm, nhôm 0.06mm, ốp mặt dựng nội thất", 420000.0),
    ALCOREST_OUTDOOR("Alu Alcorest Ngoài Trời (PVDF)", "Tấm 3mm, nhôm 0.21mm chịu nắng mưa bền 7-10 năm", 680000.0),
    TRIEU_CHEN_PRO("Alu Triều Chen / Vertu PVDF", "Phẳng mịn cao cấp, phay rãnh gấp góc sắc nét", 780000.0),
    MIRROR_OR_BRUSHED("Alu Gương Vàng / Xước Bạc", "Bề mặt kim loại sang trọng cho Spa, Vàng bạc, Showroom", 920000.0),
    LAM_SONG_COMBO("Lam Sóng Ngoài Trời + Viền Alu", "Lam sóng nhựa giả gỗ phối Alu phẳng hiện đại", 750000.0)
}

enum class Letter3dType(
    val label: String,
    val spec: String,
    val ratePerLetterM2: Double
) {
    MICA_FORMEX("Mặt Mica + Chân Formex", "Mặt Mica Đài Loan 2-3mm, uốn chân Formex tiết kiệm", 950000.0),
    FULL_MICA_LED("Full Mica Đài Loan 100%", "Mặt & chân Mica cắt CNC/Laser uốn hộp hắt sáng đều", 1450000.0),
    INOX_304_GOLD("Inox 304 Vàng Gương / Xước", "Chữ Inox 304 không gỉ cao cấp, hắt LED chân ấm", 2450000.0),
    INOX_MICA_LUXURY("Viền Inox Lồng Mặt Mica LED", "Đỉnh cao thẩm mỹ: viền Inox 304 + mặt Mica sáng rực", 2850000.0),
    TON_POWDER_COATED("Chữ Tôn Sơn Tĩnh Điện Lớn", "Dùng cho bảng cỡ đại trên nóc tòa nhà, siêu bền", 1650000.0),
    ALU_CNC_NO_LED("Chữ Alu / Formex CNC Không Đèn", "Cắt nổi 2-3cm dán mặt Alu, tối ưu chi phí ban ngày", 650000.0)
}

enum class LetterLightingStyle(val label: String, val ledPricePerLetterM2: Double, val hasLed: Boolean) {
    NO_LED("Không gắn đèn LED", 0.0, false),
    FACE_LIT("Sáng mặt chữ (LED âm trong chữ)", 420000.0, true),
    HALO_BACKLIT("Sáng hắt chân chữ lên nền Alu", 480000.0, true),
    DUAL_FACE_HALO("Sáng cả mặt & hắt chân 2 chiều", 690000.0, true)
}

// === PART 3: DECAL & STICKERS ===
enum class DecalProductType(
    val label: String,
    val spec: String,
    val printRatePerM2: Double
) {
    DECAL_MILK_WHITE("Decal Sữa Trắng In KTS", "Chống nước, độ bám keo cao, dán tường/biển/kính", 95000.0),
    DECAL_CLEAR("Decal Trong Suốt", "In xuôi hoặc in ngược dán kính xuyên thấu 2 mặt", 115000.0),
    DECAL_PERFORATED("Decal Lưới Dán Kính", "Bên trong nhìn ra ngoài rõ, bên ngoài nhìn vào là hình", 155000.0),
    DECAL_FROSTED_GLASS("Decal Mờ Cát Văn Phòng", "Cắt line sọc hoặc logo trên kính vách ngăn văn phòng", 130000.0),
    DECAL_3M_REFLECTIVE("Decal 3M / Phản Quang", "Chuyên dán xe tải, thùng container, biển báo giao thông", 380000.0),
    DECAL_DIE_CUT_STICKER("Decal Bế Tem Nhãn Demi", "In và cắt bế theo đường viền logo/tem sản phẩm", 140000.0),
    PP_POSTER("In PP Cán Màng (Trong/Ngoài Trời)", "Độ phân giải siêu nét cho Standee, Poster, Tranh", 85000.0)
}

enum class DecalLaminationType(val label: String, val addRatePerM2: Double) {
    NONE("Không cán màng", 0.0),
    GLOSSY("Cán màng bóng bảo vệ", 25000.0),
    MATTE("Cán màng mờ chống chói", 30000.0)
}

enum class DecalBackingType(
    val label: String,
    val addRatePerM2: Double,
    val usesRigidSheet: Boolean
) {
    ROLL_ONLY("Chỉ giao cuộn Decal (Không bồi)", 0.0, false),
    FORMEX_3MM("Bồi tấm Formex 3mm", 110000.0, true),
    FORMEX_5MM("Bồi tấm Formex 5mm cứng cáp", 155000.0, true),
    MICA_2MM("Bồi tấm Mica 2mm làm bảng treo", 290000.0, true),
    ALU_3MM("Bồi tấm Alu 3mm làm biển ngoài trời", 360000.0, true)
}

data class BoardDesignSpec(
    val family: BoardDesignFamily = BoardDesignFamily.TARP_BANNER,
    val brandText: String = "ĐỨC ANH STUDIO",
    val sloganText: String = "Chuyên Nghiệp • Uy Tín • 0988.xxx.xxx",
    val widthMeters: Double = 4.0,
    val heightMeters: Double = 1.2,
    val quantity: Int = 1,
    val minOneM2PerPiece: Boolean = true,
    val colorTheme: BoardColorTheme = BoardColorTheme.NAVY_GOLD,
    // Tarp fields
    val tarpProduct: TarpProductType = TarpProductType.NO_SEAM_UV,
    val tarpFinish: TarpFinishMode = TarpFinishMode.IRON_FRAME_SIGN,
    val tarpGrommetSpacingCm: Int = 40,
    // Alu fields
    val aluPanel: AluPanelType = AluPanelType.ALCOREST_OUTDOOR,
    val letter3d: Letter3dType = Letter3dType.FULL_MICA_LED,
    val letterLighting: LetterLightingStyle = LetterLightingStyle.FACE_LIT,
    val letterCoveragePercent: Int = 30,
    val letterExtrusionCm: Int = 5,
    val includeAluIronFrame: Boolean = true,
    val includeTimerRelay: Boolean = true,
    // Decal fields
    val decalProduct: DecalProductType = DecalProductType.DECAL_MILK_WHITE,
    val decalLamination: DecalLaminationType = DecalLaminationType.MATTE,
    val decalBacking: DecalBackingType = DecalBackingType.FORMEX_5MM,
    val includeOnSiteInstall: Boolean = false,
    // Quote customization
    val companyName: String = "ĐỨC ANH ADVERTISEMENT PRO",
    val companyPhone: String = "0988.xxx.xxx",
    val clientName: String = "Quý Khách Hàng",
    val discountPercent: Double = 0.0,
    val vatPercent: Double = 0.0,
    val warrantyMonths: Int = 24,
    val customUnitPriceOverride: Double? = null,
    val extraDesignOrInstallFee: Double = 0.0
)

data class BoardQuoteLine(
    val id: Long,
    val name: String,
    val spec: String,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double
) {
    val lineTotal: Double get() = quantity * unitPrice
}

data class BoardQuoteCalculation(
    val rawAreaPerPiece: Double,
    val billableAreaPerPiece: Double,
    val totalRawArea: Double,
    val totalBillableArea: Double,
    val perimeterMeters: Double,
    val materialPlanSummary: String,
    val rollOrSheetHint: String,
    val lines: List<BoardQuoteLine>,
    val subtotal: Double,
    val discountAmount: Double,
    val afterDiscount: Double,
    val vatAmount: Double,
    val grandTotal: Double,
    val estimatedCost: Double,
    val estimatedProfit: Double,
    val effectiveRatePerM2: Double
)
