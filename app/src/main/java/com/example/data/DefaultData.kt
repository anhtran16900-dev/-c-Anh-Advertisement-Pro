package com.example.data

import com.example.model.AcademyGuide
import com.example.model.MaterialItem
import com.example.model.SignageCategory

object DefaultData {

    val defaultUnitPrices: Map<String, Double> = mapOf(
        "steel_vietthanh_30" to 24000.0,
        "steel_asia_30" to 23000.0,
        "steel_hoaphat_30" to 27000.0,
        "steel_20_20" to 19000.0,
        "steel_40_40" to 38000.0,
        "alu_alcorest_pet_360k" to 360000.0,
        "alu_verker_390k" to 390000.0,
        "alu_vertu_350k" to 310000.0,
        "alu_trieuchen_pvdf_650k" to 650000.0,
        "alu_gold_mirror_520k" to 520000.0,
        "pvc_formex_2mm_sheet" to 75000.0,
        "pvc_formex_3mm_sheet" to 110000.0,
        "pvc_formex_5mm_sheet" to 170000.0,
        "pvc_formex_8mm_sheet" to 265000.0,
        "pvc_formex_10mm_sheet" to 330000.0,
        "pvc_pima_stone_sheet" to 290000.0,
        "mica_dailoan_2mm_sheet" to 420000.0,
        "mica_dailoan_3mm_sheet" to 530000.0,
        "mica_dailoan_5mm_sheet" to 820000.0,
        "mica_dailoan_8mm_sheet" to 1350000.0,
        "mica_dailoan_10mm_sheet" to 1750000.0,
        "mica_gold_mirror_sheet" to 680000.0,
        "inox_304_gold_m2" to 480000.0,
        "inox_304_silver_m2" to 420000.0,
        "inox_304_gold_brushed_m2" to 510000.0,
        "aluminum_f_profile_m" to 45000.0,
        "hiflex_mong_m2" to 35000.0,
        "hiflex_36em_m2" to 40000.0,
        "hiflex_dexam_m2" to 50000.0,
        "bat_khonggan_dau_m2" to 95000.0,
        "bat_khonggan_uv_m2" to 180000.0,
        "bat_3m_trung_m2" to 320000.0,
        "bat_3m_us_m2" to 750000.0,
        "bat_xanhcam_150g_m2" to 9000.0,
        "bat_xanhcam_200g_m2" to 13000.0,
        "bat_dua_3soc_m2" to 5500.0,
        "decal_trang_m2" to 30000.0,
        "decal_trong_m2" to 32000.0,
        "decal_luoi_m2" to 60000.0,
        "decal_3m_m2" to 180000.0,
        "decal_pp_trongnha_m2" to 45000.0,
        "decal_pp_ngoaitroi_m2" to 65000.0,
        "decal_reflective_3m_m2" to 220000.0,
        "v_nhom_m" to 18000.0,
        "v_inox_silver_m" to 28000.0,
        "led_module_3mat_co" to 2800.0,
        "led_module_samsung" to 5500.0,
        "led_strip_12v_roll" to 45000.0,
        "power_rainproof_12v33a" to 135000.0,
        "timer_relay_kg316t" to 120000.0,
        "wire_cadivi_2x15_m" to 8500.0,
        "glue_titebond" to 65000.0,
        "glue_mica_502" to 14000.0,
        "silicone_apollo_a500" to 45000.0,
        "foam_tape_roll" to 25000.0,
        "rivet_screws_pack" to 35000.0,
        "ink_cost_m2" to 15000.0,
        "cnc_running_min" to 1500.0,
        "labor_per_m2" to 45000.0
    )

    val unitPriceLabels: Map<String, String> = mapOf(
        "steel_vietthanh_30" to "Sắt hộp mạ kẽm Việt Thành 30x30 1.2mm (đ/m)",
        "steel_hoaphat_30" to "Sắt hộp Hòa Phát 30x30 1.4mm (đ/m)",
        "steel_20_20" to "Sắt hộp mạ kẽm 20x20 1.2mm (đ/m)",
        "steel_40_40" to "Sắt hộp mạ kẽm 40x40 1.4mm đòn tay (đ/m)",
        "alu_alcorest_pet_360k" to "Tấm Alu Alcorest PET 3mm (đ/tấm 1.22x2.44m)",
        "alu_trieuchen_pvdf_650k" to "Tấm Alu Triều Chen PVDF ngoài trời (đ/tấm)",
        "alu_verker_390k" to "Tấm Alu Verker kháng cháy B1 (đ/tấm)",
        "alu_vertu_350k" to "Tấm Alu Vertu giá xưởng (đ/tấm)",
        "alu_gold_mirror_520k" to "Tấm Alu Gương Vàng Alcorest (đ/tấm)",
        "pvc_formex_2mm_sheet" to "Tấm Formex PVC 2mm (đ/tấm 1.22x2.44m)",
        "pvc_formex_3mm_sheet" to "Tấm Formex PVC 3mm bồi decal (đ/tấm)",
        "pvc_formex_5mm_sheet" to "Tấm Formex PVC 5mm lót chân chữ (đ/tấm)",
        "pvc_formex_8mm_sheet" to "Tấm Formex PVC 8mm cắt CNC (đ/tấm)",
        "pvc_formex_10mm_sheet" to "Tấm Formex PVC 10mm chữ khối (đ/tấm)",
        "pvc_pima_stone_sheet" to "Tấm PVC Vân Đá Cẩm Thạch Pima 3mm (đ/tấm)",
        "mica_dailoan_2mm_sheet" to "Tấm Mica Đài Loan FS 2mm (đ/tấm)",
        "mica_dailoan_3mm_sheet" to "Tấm Mica Đài Loan FS 3mm trong suốt/sữa (đ/tấm)",
        "mica_dailoan_5mm_sheet" to "Tấm Mica Đài Loan FS 5mm (đ/tấm)",
        "mica_dailoan_8mm_sheet" to "Tấm Mica Đài Loan FS 8mm dày (đ/tấm)",
        "mica_dailoan_10mm_sheet" to "Tấm Mica Đài Loan FS 10mm khối chữ (đ/tấm)",
        "mica_gold_mirror_sheet" to "Tấm Mica Gương Vàng phản chiếu (đ/tấm)",
        "inox_304_gold_m2" to "Tấm Inox 304 Vàng Gương Laser (đ/m²)",
        "inox_304_silver_m2" to "Tấm Inox 304 Trắng Xước Hairline (đ/m²)",
        "inox_304_gold_brushed_m2" to "Tấm Inox 304 Vàng Xước (đ/m²)",
        "aluminum_f_profile_m" to "Thanh nhôm uốn chân chữ gờ F bản 6cm (đ/m)",
        "hiflex_mong_m2" to "Bạt Hiflex 0.32mm mỏng in phướn (đ/m²)",
        "hiflex_36em_m2" to "Bạt Hiflex 0.36mm dày tiêu chuẩn (đ/m²)",
        "hiflex_dexam_m2" to "Bạt Hiflex 2 da đế xám chống xuyên sáng (đ/m²)",
        "bat_khonggan_uv_m2" to "Bạt Không Gân in UV cao cấp (đ/m²)",
        "bat_3m_trung_m2" to "Bạt 3M Trung Quốc in UV (đ/m²)",
        "bat_3m_us_m2" to "Bạt 3M USA chính hãng (đ/m²)",
        "bat_xanhcam_150g_m2" to "Bạt Xanh Cam 2 Da 150g (đ/m²)",
        "bat_xanhcam_200g_m2" to "Bạt Xanh Cam 2 Da 200g siêu bền (đ/m²)",
        "bat_dua_3soc_m2" to "Bạt Dứa 3 Sọc che công trình (đ/m²)",
        "decal_trang_m2" to "Decal Trắng Sữa in mực dầu (đ/m²)",
        "decal_trong_m2" to "Decal Trong dán kính showroom (đ/m²)",
        "decal_luoi_m2" to "Decal Lưới 1 chiều văn phòng (đ/m²)",
        "decal_3m_m2" to "Decal 3M xuyên sáng cao cấp (đ/m²)",
        "decal_pp_trongnha_m2" to "Decal PP trong nhà (đ/m²)",
        "decal_pp_ngoaitroi_m2" to "Decal PP ngoài trời chống nước (đ/m²)",
        "decal_reflective_3m_m2" to "Decal Phản Quang Tổ Ong 3M 3900 (đ/m²)",
        "v_nhom_m" to "Nẹp V Nhôm viền biển (đ/m)",
        "v_inox_silver_m" to "Nẹp V Inox trắng viền biển (đ/m)",
        "led_module_samsung" to "Module LED Samsung NC Korea 3 mắt 12V (đ/con)",
        "led_module_3mat_co" to "Module LED 3 mắt có len tỏa TQ (đ/con)",
        "led_strip_12v_roll" to "Cuộn LED Dây Dán 12V 5 mét siêu sáng (đ/cuộn)",
        "power_rainproof_12v33a" to "Nguồn tổ ong chống nước ngoài trời 12V 33A (đ/cái)",
        "timer_relay_kg316t" to "Bộ Rơ-le Timer Hẹn Giờ Tự Động KG316T (đ/bộ)",
        "wire_cadivi_2x15_m" to "Dây điện Cadivi đôi 2x1.5mm (đ/m)",
        "glue_titebond" to "Keo Titebond Heavy Duty dán Alu (đ/tuýp)",
        "glue_mica_502" to "Keo Lạnh Mica Không Khói (đ/chai)",
        "silicone_apollo_a500" to "Silicon Apollo A500 Trung Tính Chống Dột (đ/chai)",
        "foam_tape_roll" to "Băng Keo Xốp Định Vị 2 Mặt 10m (đ/cuộn)",
        "rivet_screws_pack" to "Vít tự khoan mạ kẽm & ke góc (đ/gói)",
        "ink_cost_m2" to "Chi phí mực in KTS / UV (đ/m²)",
        "labor_per_m2" to "Chi phí nhân công thợ xưởng cơ bản (đ/m²)"
    )

    val presetItemNames: Map<SignageCategory, List<String>> = mapOf(
        SignageCategory.HIFLEX to listOf(
            "Biển Bạt In UV Không Gân Khung Sắt Kẽm",
            "Biển Bạt Hiflex 36 Dày Viền V Nhôm Pano",
            "Backdrop Sự Kiện Bạt 2 Da Đế Xám Chống Xuyên Sáng",
            "Hộp Đèn Bạt Không Gân In UV Sáng Đèn LED",
            "Pano Bạt 3M Xuyên Sáng Khung Thép Chịu Lực"
        ),
        SignageCategory.TARPAULIN to listOf(
            "Bạt Xanh Cam 2 Da Che Gian Hàng Hội Chợ",
            "Bạt Dứa 3 Sọc Che Bao Che Công Trình",
            "Bạt Che Nắng Mưa May Viền Đóng Khoen 1m/lỗ",
            "Bạt Nhựa Chống Thấm Lót Bể & Thi Công"
        ),
        SignageCategory.DECAL to listOf(
            "Decal Sữa Cán Màng Bóng Dán Tường & Alu",
            "Decal Trong Dán Kính Cửa Hàng Showroom",
            "Decal Lưới 1 Chiều Kính Văn Phòng Tòa Nhà",
            "Tem Bế Decal Phản Quang & Decal 3M Cao Cấp",
            "Decal PP Cán Màng Mờ Bồi Formex 5mm"
        ),
        SignageCategory.ALU_LETTER to listOf(
            "Biển Mặt Tiền Alu Alcorest Chữ Inox Vàng LED",
            "Biển Alu Triều Chen PVDF Chữ Mica Hắt Sáng",
            "Bộ Chữ Nổi Inox 304 Cắt Laser Uốn Chân",
            "Chữ Mica Đài Loan Hút Nổi LED Samsung NC",
            "Mặt Dựng Alu Showroom Ô Tô & Shop Thời Trang"
        ),
        SignageCategory.CNC_LASER to listOf(
            "Hộp Đèn Hút Nổi Tròn Phi 60cm Khung Nhôm LED",
            "Hộp Đèn Mica Vuông 60x60cm Chân Treo Tường",
            "Vách Ngăn Trang Trí Mica Trong FS Cắt Laser",
            "Bảng Tên Công Ty Inox Ăn Mòn Kim Loại",
            "Khay Đựng Menu & Kệ Mica Uốn Nhiệt Theo Yêu Cầu"
        )
    )

    val defaultMaterials: List<MaterialItem> = listOf(
        // 1. TẤM MICA
        MaterialItem("MC01", "Mica Đài Loan FS 3mm", "Tấm 1.22x2.44m, độ trong 93%, uốn chữ hút nổi", "Tấm", "mica_dailoan_3mm_sheet", 530000.0, "Mica"),
        MaterialItem("MC02", "Mica Đài Loan FS 5mm", "Tấm 1.22x2.44m, phay CNC & chữ nổi khối", "Tấm", "mica_dailoan_5mm_sheet", 820000.0, "Mica"),
        MaterialItem("MC03", "Mica Đài Loan FS 2mm", "Tấm 1.22x2.44m, mỏng dẻo làm mặt hộp đèn", "Tấm", "mica_dailoan_2mm_sheet", 420000.0, "Mica"),
        MaterialItem("MC04", "Mica Đài Loan FS 8mm", "Tấm 1.22x2.44m, cắt laser vách ngăn dày", "Tấm", "mica_dailoan_8mm_sheet", 1350000.0, "Mica"),
        MaterialItem("MC05", "Mica Đài Loan FS 10mm", "Tấm 1.22x2.44m, phay chữ nổi nguyên khối", "Tấm", "mica_dailoan_10mm_sheet", 1750000.0, "Mica"),
        MaterialItem("MC06", "Mica Gương Vàng FS", "Tấm 1.22x2.44m, tráng gương vàng sáng bóng", "Tấm", "mica_gold_mirror_sheet", 680000.0, "Mica"),

        // 2. TẤM ALUMINUM / ALU
        MaterialItem("AL01", "Alu Alcorest PET 3mm", "Tấm 1.22x2.44m, nhôm 0.06mm trong nhà/mái hiên", "Tấm", "alu_alcorest_pet_360k", 360000.0, "Alu (Aluminum)"),
        MaterialItem("AL02", "Alu Triều Chen PVDF", "Tấm 1.22x2.44m, nhôm 0.21mm ngoài trời chống nắng", "Tấm", "alu_trieuchen_pvdf_650k", 650000.0, "Alu (Aluminum)"),
        MaterialItem("AL03", "Alu Verker Chống Cháy", "Tấm Alu kháng cháy chuẩn B1 dùng cho dự án", "Tấm", "alu_verker_390k", 390000.0, "Alu (Aluminum)"),
        MaterialItem("AL04", "Alu Gương Vàng Alcorest", "Tấm 1.22x2.44m, bề mặt tráng gương vàng sang trọng", "Tấm", "alu_gold_mirror_520k", 520000.0, "Alu (Aluminum)"),
        MaterialItem("AL05", "Alu Vertu Tiết Kiệm", "Tấm 1.22x2.44m, giá xưởng kinh tế cho biển nhỏ", "Tấm", "alu_vertu_350k", 310000.0, "Alu (Aluminum)"),

        // 3. TẤM PVC / FORMEX / PIMA
        MaterialItem("PVC01", "Formex PVC 2mm", "Tấm 1.22x2.44m, bồi decal, làm mô hình kiến trúc", "Tấm", "pvc_formex_2mm_sheet", 75000.0, "PVC / Formex"),
        MaterialItem("PVC02", "Formex PVC 3mm", "Tấm 1.22x2.44m, uốn chân chữ, bồi PP quảng cáo", "Tấm", "pvc_formex_3mm_sheet", 110000.0, "PVC / Formex"),
        MaterialItem("PVC03", "Formex PVC 5mm", "Tấm 1.22x2.44m, lót lưng chữ mica/inox gắn LED", "Tấm", "pvc_formex_5mm_sheet", 170000.0, "PVC / Formex"),
        MaterialItem("PVC04", "Formex PVC 8mm", "Tấm 1.22x2.44m, cắt vách hoa văn CNC trang trí", "Tấm", "pvc_formex_8mm_sheet", 265000.0, "PVC / Formex"),
        MaterialItem("PVC05", "Formex PVC 10mm", "Tấm 1.22x2.44m, chữ khối cứng cáp không cong vênh", "Tấm", "pvc_formex_10mm_sheet", 330000.0, "PVC / Formex"),
        MaterialItem("PVC06", "PVC Cẩm Thạch Pima 3mm", "Tấm 1.22x2.44m, vân đá cẩm thạch ốp quầy lễ tân", "Tấm", "pvc_pima_stone_sheet", 290000.0, "PVC / Formex"),

        // 4. BẠT IN KTS & UV
        MaterialItem("BT01", "Hiflex Mỏng 0.32mm", "In phướn, băng rôn sự kiện ngắn ngày", "m²", "hiflex_mong_m2", 35000.0, "Bạt In"),
        MaterialItem("BT02", "Hiflex Dày 0.36mm", "In biển pano ngoài trời độ dày chuẩn", "m²", "hiflex_36em_m2", 40000.0, "Bạt In"),
        MaterialItem("BT03", "Hiflex 2 Da Đế Xám", "Mặt sau xám chống xuyên nắng, cản sáng", "m²", "hiflex_dexam_m2", 50000.0, "Bạt In"),
        MaterialItem("BT04", "Bạt Không Gân Mực Dầu", "Bề mặt phẳng lì không gân chỉ, in mực dầu", "m²", "bat_khonggan_dau_m2", 95000.0, "Bạt In"),
        MaterialItem("BT05", "Bạt Không Gân In UV", "In mực UV sắc nét, bảo hành 3 năm ngoài trời", "m²", "bat_khonggan_uv_m2", 180000.0, "Bạt In"),
        MaterialItem("BT06", "Bạt 3M Trung Quốc", "Xuyên sáng đồng đều, chuyên dùng hộp đèn", "m²", "bat_3m_trung_m2", 320000.0, "Bạt In"),
        MaterialItem("BT07", "Bạt 3M Panagraphics USA", "Chính hãng 3M Mỹ dẻo dai độ bền 5 năm", "m²", "bat_3m_us_m2", 750000.0, "Bạt In"),
        MaterialItem("BT08", "Bạt Xanh Cam 2 Da 150g", "Bạt che chắn gian hàng, công trình, kho bãi", "m²", "bat_xanhcam_150g_m2", 9000.0, "Bạt In"),
        MaterialItem("BT09", "Bạt Xanh Cam 2 Da 200g", "Bạt nhựa ép dày tráng phủ chống thấm siêu bền", "m²", "bat_xanhcam_200g_m2", 13000.0, "Bạt In"),
        MaterialItem("BT10", "Bạt Dứa 3 Sọc", "Bạt bao che giàn giáo công trình xây dựng", "m²", "bat_dua_3soc_m2", 5500.0, "Bạt In"),

        // 5. DECAL QUẢNG CÁO
        MaterialItem("DC01", "Decal Trắng Sữa Indo", "Màng dẻo bám dính cao in mực dầu / UV", "m²", "decal_trang_m2", 30000.0, "Decal"),
        MaterialItem("DC02", "Decal Trong Dán Kính", "Màng PVC trong suốt in lật ngược kính", "m²", "decal_trong_m2", 32000.0, "Decal"),
        MaterialItem("DC03", "Decal Lưới Lỗ Nhỏ", "Nhìn xuyên 1 chiều cách nhiệt nắng văn phòng", "m²", "decal_luoi_m2", 60000.0, "Decal"),
        MaterialItem("DC04", "Decal 3M Xuyên Sáng", "Dán hộp đèn ngân hàng, chuẩn màu quốc tế", "m²", "decal_3m_m2", 180000.0, "Decal"),
        MaterialItem("DC05", "Decal PP Ngoài Trời", "Bề mặt mịn không sớ sợi, cán màng mờ/bóng", "m²", "decal_pp_ngoaitroi_m2", 65000.0, "Decal"),
        MaterialItem("DC06", "Decal Phản Quang 3M", "Tổ ong 3M 3900 biển báo chỉ dẫn ban đêm", "m²", "decal_reflective_3m_m2", 220000.0, "Decal"),

        // 6. INOX & KIM LOẠI
        MaterialItem("IX01", "Inox 304 Vàng Gương", "Khổ tấm 1.22x2.44m cắt laser chữ nổi không rỉ", "m²", "inox_304_gold_m2", 480000.0, "Inox & Kim Loại"),
        MaterialItem("IX02", "Inox 304 Trắng Xước", "Bề mặt hairline xước mịn sang trọng", "m²", "inox_304_silver_m2", 420000.0, "Inox & Kim Loại"),
        MaterialItem("IX03", "Inox 304 Vàng Xước", "Chữ nổi lễ tân, bảng hiệu cao cấp chống ố", "m²", "inox_304_gold_brushed_m2", 510000.0, "Inox & Kim Loại"),
        MaterialItem("IX04", "Nhôm Gờ F Uốn Chân", "Cuộn uốn chân chữ bản 6cm có rãnh kẹp mica", "Mét", "aluminum_f_profile_m", 45000.0, "Inox & Kim Loại"),

        // 7. SẮT HỘP MẠ KẼM
        MaterialItem("ST01", "Thép Hộp Kẽm Việt Thành", "Quy cách 30x30 dày 1.2mm tiêu chuẩn chịu lực", "Mét", "steel_vietthanh_30", 24000.0, "Sắt Thép & Khung"),
        MaterialItem("ST02", "Thép Hộp Hòa Phát", "Quy cách 30x30 dày 1.4mm mạ nhúng nóng chống bão", "Mét", "steel_hoaphat_30", 27000.0, "Sắt Thép & Khung"),
        MaterialItem("ST03", "Thép Hộp Kẽm 20x20", "Quy cách 20x20 dày 1.2mm nan đan phụ, biển nhỏ", "Mét", "steel_20_20", 19000.0, "Sắt Thép & Khung"),
        MaterialItem("ST04", "Thép Hộp Kẽm 40x40", "Quy cách 40x40 dày 1.4mm cột trụ pano, dầm chính", "Mét", "steel_40_40", 38000.0, "Sắt Thép & Khung"),

        // 8. ĐÈN LED & NGUỒN ĐIỆN
        MaterialItem("LED01", "LED Samsung NC Korea", "Module 3 mắt 12V 1.2W lens góc rộng 160°", "Con", "led_module_samsung", 5500.0, "LED & Nguồn Điện"),
        MaterialItem("LED02", "LED Module 3 Mắt Có Len", "Module 12V phổ thông xưởng quảng cáo", "Con", "led_module_3mat_co", 2800.0, "LED & Nguồn Điện"),
        MaterialItem("LED03", "LED Dây Dán 12V Cuộn 5m", "Dây led 2835 ánh sáng ấm/trắng hắt khe biển", "Cuộn", "led_strip_12v_roll", 45000.0, "LED & Nguồn Điện"),
        MaterialItem("NG01", "Nguồn Chống Nước 12V 33A", "Nguồn tổ ong đúc nhôm chống mưa bão 400W", "Cái", "power_rainproof_12v33a", 135000.0, "LED & Nguồn Điện"),
        MaterialItem("NG02", "Timer Hẹn Giờ KG316T", "Rơ-le điện tử tự động bật tắt đèn biển theo giờ", "Bộ", "timer_relay_kg316t", 120000.0, "LED & Nguồn Điện"),
        MaterialItem("NG03", "Dây Điện Cadivi 2x1.5mm", "Dây đôi đồng nguyên chất bọc PVC trục chính", "Mét", "wire_cadivi_2x15_m", 8500.0, "LED & Nguồn Điện"),

        // 9. KEO & PHỤ KIỆN HOÀN THIỆN
        MaterialItem("PK01", "Keo Dán Đa Năng Titebond", "Tuýp 296ml dán Alu bám chắc khung sắt", "Tuýp", "glue_titebond", 65000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK02", "Keo Lạnh Mica Không Khói", "Chuyên dán cạnh chữ mica không để lại vệt đục", "Chai", "glue_mica_502", 14000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK03", "Silicon Apollo A500 Trung Tính", "Chai 300ml dán khe phay alu & chống dột", "Chai", "silicone_apollo_a500", 45000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK04", "Băng Keo Xốp 2 Mặt 10m", "Bản 2cm định vị tấm alu trước khi bơm keo", "Cuộn", "foam_tape_roll", 25000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK05", "Nẹp V Nhôm Định Hình", "Thanh viền V nhôm 20x20mm nẹp mép biển hiệu", "Mét", "v_nhom_m", 18000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK06", "Nẹp V Inox 304 Trắng Gương", "Thanh nẹp inox 20x20mm viền biển chống rỉ", "Mét", "v_inox_silver_m", 28000.0, "Keo & Phụ Kiện"),
        MaterialItem("PK07", "Vít Tự Khoan & Ke Góc", "Gói 100 con vít đầu dù mạ kẽm + tôn ke gia cố", "Gói", "rivet_screws_pack", 35000.0, "Keo & Phụ Kiện")
    )

    val academyGuides: List<AcademyGuide> =
        AcademyCurriculumData.softwareGuides + AcademyCncLedData.productionGuides

    // Pre-computed intelligent AI advice for signage inquiries:
    fun getInstantAiResponse(query: String): String {
        val q = query.lowercase().trim()
        return when {
            q.contains("bóc tách") && (q.contains("alu") || q.contains("chữ")) -> """
                📋 **BÓC TÁCH VẬT TƯ BIỂN ALU & CHỮ NỔI MICA LED (15m² thực tế):**
                
                1. **Khung Xương Sắt Chịu Lực:**
                   - Sắt hộp mạ kẽm 30x30x1.2mm hoặc 25x25x1.2mm (Việt Thành / Hòa Phát).
                   - Khoảng cách đan xương tiêu chuẩn: 600mm x 600mm hoặc 600mm x 1200mm.
                   - Khối lượng ước tính: ~55 - 65 mét sắt kẽm (~4-5 cây dài 6m).
                   - Vật tư phụ: Vít tự khoan rãnh, tôn ke góc, que hàn.
                
                2. **Mặt Dựng Alu:**
                   - Tấm Alu Alcorest PET 3mm 0.06mm (trong nhà/mái hiên) hoặc Triều Chen PVDF 3mm 0.21mm (ngoài trời chịu nắng).
                   - Kích thước tấm: 1.22m x 2.44m = 2.976 m²/tấm.
                   - Số tấm cần: 15m² / 2.976 = ~5.1 tấm -> Thực tế cần 6 tấm (tính cả phôi hao hụt mí ghép rãnh).
                   - Keo dán: 4-5 tuýp Titebond Heavy Duty + 2 cuộn băng keo xốp 2 mặt định vị + 3 chai silicon A500 chống dột mép.
                
                3. **Bộ Chữ Nổi & Hệ Thống LED:**
                   - Mặt chữ: Mica Đài Loan FS 3mm (trắng sữa / màu) cắt CNC router.
                   - Chân chữ: Nhôm định hình uốn chân cao 5cm hoặc chân inox 304 vàng gương.
                   - Đèn LED: Module LED Samsung NC 3 mắt (1.2W) bố trí cách nhau 5-7cm (~120 - 150 con LED).
                   - Nguồn cấp: 1 nguồn tổ ong ngoài trời chống nước 12V 33A (400W), kéo dây đôi Cadivi 2x1.5mm.
            """.trimIndent()

            q.contains("3m") || (q.contains("so sánh") && q.contains("bạt")) -> """
                🔍 **SO SÁNH BẠT 3M USA (MỸ) VÀ BẠT 3M TRUNG QUỐC:**
                
                1. **Bạt 3M Panagraphics III (Chính Hãng Mỹ):**
                   - Cấu tạo: Màng bạt tráng vinyl gia cố sợi polyester đặc chế, chịu bão gió cấp 11.
                   - Độ bền màu: Từ 5 đến 7 năm ngoài trời khi in mực UV chuẩn 3M MCS.
                   - Khả năng xuyên sáng: Quang phổ xuyên sáng cực kỳ mịn, không thấy đốm LED, màu sắc ban đêm và ban ngày đồng nhất.
                   - Đơn giá phôi nhập: ~750.000đ - 950.000đ/m². Thường chỉ dùng cho ngân hàng (Techcombank, Vietinbank, TPBank) và showroom quốc tế.
                
                2. **Bạt 3M Trung Quốc (Bạt Không Gân / Bạt Hộp Đèn):**
                   - Cấu tạo: Màng nhựa PVC ép không gân lưới nhìn thấy bằng mắt thường.
                   - Độ bền màu: 2 đến 3 năm khi in mực UV.
                   - Khả năng xuyên sáng: Đạt 85-90% so với 3M Mỹ, giá thành kinh tế hơn rất nhiều.
                   - Đơn giá phôi nhập: ~320.000đ - 380.000đ/m². Rất phổ biến cho chuỗi trà sữa, nhà thuốc, shop thời trang.
            """.trimIndent()

            q.contains("sọc") || q.contains("đầu phun") || q.contains("máy in") -> """
                🛠️ **CÁCH KHẮC PHỤC LỖI MÁY IN PHUN BẠT BỊ SỌC NGANG (PASSING):**
                
                1. **Kiểm Tra Nozzle Check (Tia Mực Đầu Phun):**
                   - Nhấn Test đầu phun. Nếu bị khuyết tia mực: Bấm hút mực (Purge/Clean) và ngâm dung dịch rửa đầu phun 5-10 phút.
                   - Lau cần gạt mực (Wiper blade) và cốc xả (Capping station) bằng tăm bông chuyên dụng.
                
                2. **Lỗi Bước Tiến Bạt (Feed / Step Offset):**
                   - Nếu sọc trắng xuất hiện đều đặn theo từng vòng quay trục cuốn: Máy đang cuộn bạt quá nhanh (bước tiến quá đà). Vào phần mềm in giảm thông số Step xuống 2-5 đơn vị.
                   - Nếu thấy vệt mực đậm đè lên nhau (sọc đen): Máy cuộn bạt chậm, cần tăng giá trị Step.
                
                3. **Cân Chỉnh Hai Chiều (Bi-directional Calibration):**
                   - Chạy chức năng Bi-direction test. Nếu chiều đi và chiều về của đầu phun không trùng khớp sẽ gây nhòe và sọc rạn.
                
                4. **Nhiệt Độ Sấy & Điện Áp Đầu Phun:**
                   - Bật sấy bạt trước và sau ở mức 38°C - 42°C. Tránh luồng gió quạt hoặc máy lạnh thổi thẳng vào đầu phun gây khô nghẹt lỗ phun cực nhỏ.
            """.trimIndent()

            q.contains("nguồn") || q.contains("led") || q.contains("sụt áp") -> """
                ⚡ **TƯ VẤN CHỌN NGUỒN LED & TRÁNH SỤT ÁP QUẢNG CÁO:**
                
                - **Quy tắc 80%:** Công suất thực của nguồn = Công suất danh định x 0.8. Ví dụ nguồn 12V 33A (400W) chỉ nên tải tối đa 320W.
                - **Sụt áp DC:** Nguồn 12V truyền xa hơn 5 mét sẽ bị giảm còn 10.5V - 11V làm LED cuối dây bị tối hoặc ố màu.
                - **Khắc phục:** Kéo dây nguồn trục chính 2x1.5mm hoặc 2x2.5mm chạy vòng khép kín quanh khung biển và cấp nguồn hai đầu dải LED.
                - **Chống nước:** Đặt nguồn trong hộp kỹ thuật có khe thoát khí, hoặc dùng nguồn nhôm đúc nguyên khối chuẩn IP67 ngoài trời.
            """.trimIndent()

            else -> """
                Chào bạn! Tôi là Trợ Lý Kỹ Thuật & Bóc Tách Vật Tư Đức Anh PRO.
                
                Dựa trên yêu cầu của bạn:
                - Đối với các hạng mục biển bạt KTS, hộp đèn, alu chữ nổi và decal, bạn có thể nhập kích thước (Ngang x Cao) tại tab **Bóc Tách Realtime** để có bảng chiết tính vật tư và giá vốn chính xác theo giá phôi xưởng hiện tại.
                - Về thi công, luôn lưu ý đan khung sắt hộp kẽm 30x30 dày từ 1.2mm trở lên để chống bão gió, chia nguồn LED mạch xương cá và sấy bạt 40°C trước khi in để mực bám dính tốt nhất.
                
                Nếu bạn cần bóc tách công trình cụ thể, hãy cung cấp kích thước hoặc bấm các câu hỏi gợi ý bên trên!
            """.trimIndent()
        }
    }
}
