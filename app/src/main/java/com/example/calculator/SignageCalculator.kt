package com.example.calculator

import com.example.model.BreakdownItem
import com.example.model.EstimationInput
import com.example.model.EstimationResult
import com.example.model.SignageCategory
import kotlin.math.ceil
import kotlin.math.roundToLong

object SignageCalculator {

    /**
     * Tự động tính toán tổng giá dự toán dựa trên đơn giá vật tư và kích thước (chiều dài x chiều rộng).
     * Automatically calculates the estimated total price based on material unit price and dimensions (length x width).
     */
    fun calculateEstimatedTotalPrice(
        length: Double,
        width: Double,
        materialUnitPrice: Double,
        unit: String = "m²",
        quantity: Int = 1,
        wastagePercent: Double = 8.0,
        profitMarginPercent: Double = 35.0,
        includeIronFrame: Boolean = true,
        includeLighting: Boolean = true
    ): Double {
        if (length <= 0.0 || width <= 0.0 || quantity <= 0) return 0.0

        val l = length
        val w = width
        val q = quantity
        val netArea = l * w * q
        val grossArea = netArea * (1.0 + (wastagePercent.coerceAtLeast(0.0) / 100.0))
        val perimeter = (l + w) * 2.0 * q

        val faceMaterialCost = when {
            unit.contains("Tấm", ignoreCase = true) -> {
                val sheetCount = ceil(grossArea / 2.9768).toInt().coerceAtLeast(1)
                sheetCount * materialUnitPrice
            }
            unit.contains("Mét", ignoreCase = true) -> {
                perimeter * materialUnitPrice
            }
            else -> {
                grossArea * materialUnitPrice
            }
        }

        val frameCost = if (includeIronFrame) {
            val ribsCount = ceil(l / 1.2).toInt().coerceAtLeast(1)
            val steelLength = perimeter + (ribsCount * w * q)
            (steelLength * 24000.0) + (45000.0 * q)
        } else 0.0

        val lightingCost = if (includeLighting) {
            val ledCount = ceil(netArea * 0.28 * 45.0).toInt().coerceAtLeast(40)
            val powerUnits = ceil(ledCount / 220.0).toInt().coerceAtLeast(1)
            (ledCount * 5500.0) + (powerUnits * 135000.0) + (perimeter * 1.5 * 8500.0)
        } else 0.0

        val finishingCost = (ceil(grossArea / 2.8) * 65000.0) + (ceil(grossArea / 5.0) * 45000.0)
        val laborCost = (netArea * 45000.0 * 1.35 + 120000.0).roundToLong().toDouble()
        val shippingCost = 350000.0

        val totalCost = faceMaterialCost + frameCost + lightingCost + finishingCost + laborCost + shippingCost
        val profitAmount = totalCost * (profitMarginPercent.coerceAtLeast(0.0) / 100.0)
        return totalCost + profitAmount
    }

    fun calculate(
        input: EstimationInput,
        prices: Map<String, Double>
    ): EstimationResult {
        val w = if (input.width <= 0) 1.0 else input.width
        val h = if (input.height <= 0) 1.0 else input.height
        val qty = if (input.quantity <= 0) 1 else input.quantity
        val wastage = if (input.wastagePercent < 0) 0.0 else input.wastagePercent

        val netArea = w * h * qty
        val grossArea = netArea * (1.0 + wastage / 100.0)
        val perimeter = (w + h) * 2.0 * qty

        val rows = mutableListOf<BreakdownItem>()
        var totalMaterialCost = 0.0
        val engineeringTips = mutableListOf<String>()

        fun getPrice(key: String, fallback: Double): Double {
            return prices[key] ?: fallback
        }

        val hasCustomSelectedMaterial = input.selectedMaterialKey.isNotBlank() || input.customMaterialPrice != null
        val activeUnitPrice = input.customMaterialPrice ?: if (input.selectedMaterialKey.isNotBlank()) prices[input.selectedMaterialKey] ?: 360000.0 else null

        when (input.category) {
            SignageCategory.HIFLEX -> {
                val matKey = input.hiflexMaterialKey
                val matPrice = activeUnitPrice ?: getPrice(matKey, 40000.0)
                val matCost = grossArea * matPrice
                val inkPrice = getPrice("ink_cost_m2", 15000.0)
                val inkCost = netArea * inkPrice

                val matName = if (input.selectedMaterialName.isNotBlank() && input.selectedMaterialUnit == "m²") {
                    "${input.selectedMaterialName} (Tính mí dán)"
                } else when (matKey) {
                    "hiflex_mong_m2" -> "Bạt Hiflex Mỏng 0.32mm (Tính mí dán biên)"
                    "hiflex_36em_m2" -> "Bạt Hiflex Dày 0.36mm Chuẩn (Tính mí dán)"
                    "hiflex_dexam_m2" -> "Bạt Hiflex 2 Da Đế Xám Chống Xuyên Sáng"
                    "bat_khonggan_uv_m2" -> "Bạt Không Gân In UV Sắc Nét"
                    "bat_3m_trung_m2" -> "Bạt 3M Trung Quốc Hộp Đèn"
                    "bat_3m_us_m2" -> "Bạt 3M Panagraphics USA Chính Hãng"
                    else -> "Phôi Bạt Quảng Cáo In Ấn"
                }

                rows.add(BreakdownItem(matName, "m²", grossArea, matPrice, matCost))
                rows.add(BreakdownItem("Mực in phun KTS / UV chống bay màu", "m²", netArea, inkPrice, inkCost))
                totalMaterialCost += (matCost + inkCost)

                // Steel Frame
                if (input.steelTypeKey != "none") {
                    val steelPrice = getPrice(input.steelTypeKey, 24000.0)
                    val ribsCount = ceil(w / 1.2).toInt()
                    val steelLength = perimeter + (ribsCount * h * qty)
                    val steelCost = steelLength * steelPrice
                    val steelLabel = if (input.steelTypeKey.contains("hoaphat")) {
                        "Khung Sắt Kẽm Hòa Phát 30x30 1.4mm (Nan đan 1.2m)"
                    } else {
                        "Khung Sắt Kẽm Việt Thành 30x30 1.2mm (Nan đan 1.2m)"
                    }
                    rows.add(BreakdownItem(steelLabel, "mét", steelLength, steelPrice, steelCost))
                    totalMaterialCost += steelCost

                    // Hardware
                    val hardwareCost = 45000.0 * qty
                    rows.add(BreakdownItem("Vít tự khoan, que hàn & ke góc gia cố", "Gói", qty.toDouble(), 45000.0, hardwareCost))
                    totalMaterialCost += hardwareCost
                }

                // V Trim
                if (input.vTrimTypeKey != "none") {
                    val vPrice = getPrice(input.vTrimTypeKey, 18000.0)
                    val vCost = perimeter * vPrice
                    val vLabel = if (input.vTrimTypeKey.contains("inox")) "Nẹp viền V Inox sáng bóng" else "Nẹp viền V Nhôm định hình"
                    rows.add(BreakdownItem(vLabel, "mét", perimeter, vPrice, vCost))
                    totalMaterialCost += vCost
                }

                if (w > 5.0 || h > 2.5) {
                    engineeringTips.add("Biển khổ lớn (${w}x${h}m): Khuyến nghị tăng nan sắt đứng cách nhau 1.0m và giằng chéo tăng cứng tránh võng mặt bạt khi gió giật.")
                }
                if (input.hiflexMaterialKey == "bat_khonggan_uv_m2") {
                    engineeringTips.add("Bạt không gân in UV cho độ căng phẳng tuyệt đối, tuổi thọ ngoài trời đạt 3-4 năm không phai màu.")
                }
            }

            SignageCategory.TARPAULIN -> {
                val matKey = input.tarpaulinTypeKey
                val matPrice = getPrice(matKey, 9000.0)
                val matCost = grossArea * matPrice
                val matLabel = when (matKey) {
                    "bat_xanhcam_200g_m2" -> "Bạt Xanh Cam 2 Da 200g/m² Dày Siêu Bền"
                    "bat_dua_3soc_m2" -> "Bạt Dứa Sọc 3 Màu Che Chắn Công Trình"
                    else -> "Bạt Xanh Cam 2 Da 150g/m² Chống Thấm"
                }

                rows.add(BreakdownItem(matLabel, "m²", grossArea, matPrice, matCost))
                totalMaterialCost += matCost

                val eyeletCost = perimeter * 8000.0
                rows.add(BreakdownItem("May gấp viền chịu lực & dập khoen nhôm (1m/lỗ)", "mét", perimeter, 8000.0, eyeletCost))
                totalMaterialCost += eyeletCost

                val ropeCost = perimeter * 1.5 * 2500.0
                rows.add(BreakdownItem("Dây dù neo buộc bạt phi 6mm", "mét", perimeter * 1.5, 2500.0, ropeCost))
                totalMaterialCost += ropeCost

                engineeringTips.add("Khoen đóng 4 góc cần được may chèn thêm 2 lớp bạt ke góc chữ V để chịu lực kéo căng gió không rách mép bạt.")
            }

            SignageCategory.DECAL -> {
                val matKey = input.decalTypeKey
                val matPrice = getPrice(matKey, 30000.0)
                val matCost = grossArea * matPrice
                val inkPrice = getPrice("ink_cost_m2", 15000.0)
                val inkCost = netArea * inkPrice

                val decalLabel = when (matKey) {
                    "decal_trong_m2" -> "Phôi Decal Trong Dán Kính Cửa Hàng"
                    "decal_luoi_m2" -> "Phôi Decal Lưới 1 Chiều Văn Phòng"
                    "decal_3m_m2" -> "Phôi Decal 3M Cao Cấp Chuyên Dụng"
                    else -> "Phôi Decal Trắng Sữa Nhập Khẩu"
                }

                rows.add(BreakdownItem(decalLabel, "m²", grossArea, matPrice, matCost))
                rows.add(BreakdownItem("Mực in solvent / UV sắc nét 1440 DPI", "m²", netArea, inkPrice, inkCost))
                totalMaterialCost += (matCost + inkCost)

                val laminationCost = netArea * 12000.0
                rows.add(BreakdownItem("Cán màng bảo vệ bề mặt chống trầy UV", "m²", netArea, 12000.0, laminationCost))
                totalMaterialCost += laminationCost

                val cuttingCost = netArea * 10000.0
                rows.add(BreakdownItem("Cắt xén thành phẩm & băng dính định vị", "m²", netArea, 10000.0, cuttingCost))
                totalMaterialCost += cuttingCost

                engineeringTips.add("Khi dán kính hoặc dán tường, xịt nước pha xà phòng loãng (Baby Oil / Sunlight) để gạt phẳng bọt khí dễ dàng không nhăn màng.")
            }

            SignageCategory.ALU_LETTER -> {
                val fullSheets = ceil(grossArea / 2.9768).toInt().coerceAtLeast(1)
                val aluPrice = activeUnitPrice ?: getPrice(input.aluBrandKey, 360000.0)
                val aluCost = fullSheets * aluPrice
                val aluLabel = if (input.selectedMaterialName.isNotBlank() && (input.selectedMaterialUnit.contains("Tấm", ignoreCase = true) || input.selectedMaterialKey.contains("alu", ignoreCase = true))) {
                    "${input.selectedMaterialName} (1.22x2.44m)"
                } else if (input.aluBrandKey.contains("trieuchen")) {
                    "Tấm Alu Triều Chen PVDF 3mm (1.22x2.44m) Ngoài Trời"
                } else {
                    "Tấm Alu Alcorest PET 3mm (1.22x2.44m) Tiêu Chuẩn"
                }

                rows.add(BreakdownItem(aluLabel, "Tấm", fullSheets.toDouble(), aluPrice, aluCost))
                totalMaterialCost += aluCost

                // Skeleton iron frame 60x60cm
                val steelPrice = getPrice(input.steelTypeKey, 24000.0)
                val horizontalIron = (ceil(h / 0.6) + 1) * w * qty
                val verticalIron = (ceil(w / 0.6) + 1) * h * qty
                val totalIronMeters = horizontalIron + verticalIron
                val ironCost = totalIronMeters * steelPrice

                rows.add(BreakdownItem("Khung sắt hộp mạ kẽm đan nan 60x60cm", "mét", totalIronMeters, steelPrice, ironCost))
                totalMaterialCost += ironCost

                // Titebond glue & foam tape
                val glueTubes = ceil(grossArea / 2.8).toInt()
                val gluePrice = getPrice("glue_titebond", 65000.0)
                val glueCost = glueTubes * gluePrice
                rows.add(BreakdownItem("Keo dán Titebond Heavy Duty dán tấm Alu", "Tuýp", glueTubes.toDouble(), gluePrice, glueCost))
                totalMaterialCost += glueCost

                val siliconCost = ceil(grossArea / 5.0) * 45000.0
                rows.add(BreakdownItem("Silicon A500 dán chỉ ghép phay rãnh Alu", "Chai", ceil(grossArea / 5.0), 45000.0, siliconCost))
                totalMaterialCost += siliconCost

                // Letters (Inox / Mica)
                if (input.letterMaterialKey != "none") {
                    val letterArea = netArea * 0.28
                    val letterPrice = getPrice(input.letterMaterialKey, 480000.0)
                    val letterCost = letterArea * letterPrice
                    val letterName = if (input.letterMaterialKey.contains("gold")) {
                        "Bộ chữ Inox 304 Vàng Gương Cắt Laser Uốn Chân"
                    } else if (input.letterMaterialKey.contains("silver")) {
                        "Bộ chữ Inox 304 Trắng Hairline Cắt Laser Uốn Chân"
                    } else {
                        "Bộ chữ Mica Đài Loan FS 3mm Uốn Chân Nổi"
                    }
                    rows.add(BreakdownItem(letterName, "m² chữ", letterArea, letterPrice, letterCost))
                    totalMaterialCost += letterCost

                    // LED Modules
                    if (input.ledBrandKey != "none") {
                        val ledCount = ceil(letterArea * 45.0).toInt()
                        val ledPrice = getPrice(input.ledBrandKey, 5500.0)
                        val ledCost = ledCount * ledPrice
                        val ledName = if (input.ledBrandKey.contains("samsung")) {
                            "Module LED Samsung NC Korea 1.2W 3 mắt"
                        } else {
                            "Module LED 3 mắt có len tỏa siêu sáng"
                        }
                        rows.add(BreakdownItem(ledName, "Con", ledCount.toDouble(), ledPrice, ledCost))
                        totalMaterialCost += ledCost

                        // Rainproof Power Supply
                        val powerUnits = ceil(ledCount / 220.0).toInt()
                        val powerPrice = getPrice("power_rainproof_12v33a", 135000.0)
                        val powerCost = powerUnits * powerPrice
                        rows.add(BreakdownItem("Nguồn tổ ong chống nước ngoài trời 12V 33A", "Cái", powerUnits.toDouble(), powerPrice, powerCost))
                        totalMaterialCost += powerCost

                        val wireCost = perimeter * 1.5 * getPrice("wire_cadivi_2x15_m", 8500.0)
                        rows.add(BreakdownItem("Dây điện Cadivi đôi 2x1.5mm trục chính", "mét", perimeter * 1.5, 8500.0, wireCost))
                        totalMaterialCost += wireCost
                    }
                }

                engineeringTips.add("Mặt dựng Alu: Phay rãnh chữ V sâu chừa 0.8mm màng nhôm để gấp mép cạnh vuông vức, bắn vít giấu vào rãnh rồi bơm silicon A500 đều màu.")
                engineeringTips.add("Bộ chữ gắn ngoài trời: Cần khoan lỗ thoát nước phi 4mm dưới chân mỗi chữ để tránh đọng ẩm gây cháy chập LED.")
            }

            SignageCategory.CNC_LASER -> {
                val fullSheets = ceil(grossArea / 2.9768).toInt().coerceAtLeast(1)
                val micaPrice = activeUnitPrice ?: getPrice(input.cncMicaKey, 530000.0)
                val micaCost = fullSheets * micaPrice
                val micaLabel = if (input.selectedMaterialName.isNotBlank()) {
                    "${input.selectedMaterialName} (1.22x2.44m Cắt CNC)"
                } else {
                    "Tấm Mica Đài Loan FS 1.22x2.44m Cắt CNC"
                }
                rows.add(BreakdownItem(micaLabel, "Tấm", fullSheets.toDouble(), micaPrice, micaCost))
                totalMaterialCost += micaCost

                val framePrice = 35000.0
                val frameCost = perimeter * framePrice
                rows.add(BreakdownItem("Khung nhôm định hình uốn hộp đèn / nẹp viền", "mét", perimeter, framePrice, frameCost))
                totalMaterialCost += frameCost

                val cncCost = netArea * 15.0 * getPrice("cnc_running_min", 1500.0)
                rows.add(BreakdownItem("Thời gian chạy máy phay CNC / cắt Laser", "Phút", netArea * 15.0, 1500.0, cncCost))
                totalMaterialCost += cncCost

                val ledCost = netArea * 35.0 * 5500.0
                rows.add(BreakdownItem("LED rọi hông hộp đèn góc chiếu 160°", "Con", netArea * 35.0, 5500.0, ledCost))
                totalMaterialCost += ledCost

                val powerCost = 135000.0 * qty
                rows.add(BreakdownItem("Nguồn adapter 12V ổn định dòng", "Bộ", qty.toDouble(), 135000.0, powerCost))
                totalMaterialCost += powerCost

                engineeringTips.add("Mica FS hút nổi: Gia nhiệt mềm đều ở nhiệt độ 145°C - 160°C trước khi ép khuôn hút chân không để bề mặt căng mọng không nổi bọt rỗ.")
            }
        }

        // Labor cost calculation
        val laborRate = getPrice("labor_per_m2", 45000.0)
        val laborCost = (netArea * laborRate * input.laborComplexity.multiplier + 120000.0).roundToLong().toDouble()
        rows.add(BreakdownItem("Nhân công thợ thi công lắp ráp (${input.laborComplexity.displayName.substringBefore(":")})", "Gói", 1.0, laborCost, laborCost))

        // Shipping and accessories
        val shipping = input.shippingAndHardwareFee
        rows.add(BreakdownItem("Vận chuyển xe tải & phụ tùng lắp đặt công trình", "Chuyến", 1.0, shipping, shipping))

        val totalCost = totalMaterialCost + laborCost + shipping
        val profitMargin = if (input.profitMarginPercent < 0) 0.0 else input.profitMarginPercent
        val baseProfitAmount = totalCost * (profitMargin / 100.0)
        val baseFinalPrice = totalCost + baseProfitAmount

        val singlePieceArea = w * h
        val billablePieceArea = if (input.minOneM2PerPiece) singlePieceArea.coerceAtLeast(1.0) else singlePieceArea
        val billableArea = billablePieceArea * qty

        val customRate = input.customUnitPriceOverridePerM2?.takeIf { it > 0.0 }
        val subtotalCustomerPrice = when {
            customRate != null -> billableArea * customRate
            input.minOneM2PerPiece && singlePieceArea < 1.0 && netArea > 0.0 -> {
                (baseFinalPrice / netArea) * billableArea
            }
            else -> baseFinalPrice
        }

        val discountPct = input.discountPercent.coerceIn(0.0, 90.0)
        val discountAmount = subtotalCustomerPrice * (discountPct / 100.0)
        val afterDiscount = subtotalCustomerPrice - discountAmount

        val vatPct = input.vatPercent.coerceIn(0.0, 20.0)
        val vatAmount = afterDiscount * (vatPct / 100.0)
        val finalPrice = afterDiscount + vatAmount

        val profitAmount = if (customRate == null && !input.minOneM2PerPiece && discountPct == 0.0 && vatPct == 0.0) {
            baseProfitAmount
        } else {
            afterDiscount - totalCost
        }
        val unitPricePerM2 = if (billableArea > 0) afterDiscount / billableArea else 0.0
        val effectiveCategoryName = input.customCategoryLabel.ifBlank { input.category.shortName }

        engineeringTips.add("Kích thước hoàn thiện: ${w}m x ${h}m (Diện tích thực: ${String.format("%.2f", netArea)} m² | Tính tiền: ${String.format("%.2f", billableArea)} m²). Kết cấu đảm bảo quy chuẩn xưởng sản xuất.")

        // TỔNG HỢP TOÀN BỘ VẬT TƯ & THIẾT BỊ CẦN CHO QUẢNG CÁO (Comprehensive Advertising Synthesis)
        val fullSheets = ceil(grossArea / 2.9768).toInt().coerceAtLeast(1)
        val ribsCount = ceil(w / 1.2).toInt().coerceAtLeast(1)
        val steelLength = (perimeter + (ribsCount * h * qty)).toInt()
        val letterArea = netArea * 0.28
        val ledCount = ceil(letterArea * 45.0).toInt().coerceAtLeast(40)
        val powerUnits = ceil(ledCount / 220.0).toInt().coerceAtLeast(1)
        val glueTubes = ceil(grossArea / 2.8).toInt().coerceAtLeast(2)
        val siliconTubes = ceil(grossArea / 5.0).toInt().coerceAtLeast(1)

        val checklist = listOf(
            com.example.model.AdvertisingChecklistItem(
                id = "chk_face",
                name = if (input.selectedMaterialName.isNotBlank()) input.selectedMaterialName else "Tấm Phôi Bề Mặt Biển",
                category = "1. Phôi Mặt Biển",
                spec = "Kích thước phủ bì ${w}m x ${h}m (Tính cả ${input.wastagePercent}% mí gập/hao hụt)",
                quantity = if (input.selectedMaterialUnit.contains("Tấm", ignoreCase = true) || input.category == SignageCategory.ALU_LETTER || input.category == SignageCategory.CNC_LASER) {
                    "$fullSheets Tấm (1.22x2.44m)"
                } else {
                    "${String.format("%.2f", grossArea)} ${input.selectedMaterialUnit}"
                },
                unit = input.selectedMaterialUnit,
                estimatedCost = rows.firstOrNull()?.totalPrice ?: (grossArea * 85000.0),
                isIncludedInPrice = true,
                note = "Vật liệu chính mặt biển: kiểm tra độ phẳng, xước màng bảo vệ trước khi gia công"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_steel",
                name = "Thép Hộp Mạ Kẽm Đan Khung Xương",
                category = "2. Khung Xương Chịu Lực",
                spec = if (input.steelTypeKey.contains("hoaphat")) "Thép Hòa Phát 30x30 dày 1.4mm mạ kẽm nhúng nóng" else "Thép Việt Thành 30x30 dày 1.2mm tiêu chuẩn",
                quantity = "$steelLength mét (~${ceil(steelLength / 6.0).toInt()} cây 6m)",
                unit = "Mét",
                estimatedCost = steelLength * getPrice(input.steelTypeKey, 24000.0),
                isIncludedInPrice = input.includeIronFrame && input.steelTypeKey != "none",
                note = "Hàn đan nan xương 600x600mm hoặc 600x1200mm, sơn phủ chống rỉ toàn bộ mối hàn"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_hardware",
                name = "Vít Tự Khoan, Que Hàn & Bản Mã Ke Góc",
                category = "2. Khung Xương Chịu Lực",
                spec = "Vít đầu dù mạ kẽm mạ xi chống rỉ + ke tôn dày 1.5mm gia cố 4 góc",
                quantity = "${qty * 2} Gói",
                unit = "Gói",
                estimatedCost = 45000.0 * qty,
                isIncludedInPrice = true,
                note = "Khóa chặt góc vuông tránh vặn xoắn khung biển khi gió bão giật mạnh"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_letters",
                name = "Bộ Chữ Nổi & Cắt Gọt Tạo Hình CNC/Laser",
                category = "3. Chữ Nổi & Cắt CNC",
                spec = if (input.letterMaterialKey.contains("gold")) "Inox 304 vàng gương cắt Laser uốn chân 5cm" else "Mica Đài Loan FS 3mm uốn chân nổi",
                quantity = "${String.format("%.2f", letterArea)} m² mặt chữ",
                unit = "Bộ",
                estimatedCost = letterArea * getPrice(input.letterMaterialKey, 480000.0),
                isIncludedInPrice = input.letterMaterialKey != "none",
                note = "Lót lưng Formex 5mm / tôn lá bắt vít giấu chân, dán keo lạnh tàng hình không khói"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_led",
                name = "Module LED 12V Chiếu Sáng (Samsung NC / Lens Tỏa)",
                category = "4. Chiếu Sáng & Điện Năng",
                spec = "LED 12V 1.2W lens rộng 160° chống nước IP68 ngoài trời",
                quantity = "$ledCount Con",
                unit = "Con",
                estimatedCost = ledCount * getPrice(input.ledBrandKey, 5500.0),
                isIncludedInPrice = input.includeLighting,
                note = "Bố trí cách đều 6-8cm, đấu nối xương cá song song 20-25 con/chùm tránh sụt áp"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_power",
                name = "Nguồn Tổ Ong Ngoài Trời Chống Nước 12V 33A",
                category = "4. Chiếu Sáng & Điện Năng",
                spec = "Công suất danh định 400W vỏ nhôm đúc tản nhiệt chống mưa bão",
                quantity = "$powerUnits Cái",
                unit = "Cái",
                estimatedCost = powerUnits * getPrice("power_rainproof_12v33a", 135000.0),
                isIncludedInPrice = input.includeLighting,
                note = "Vận hành an toàn $\\le 80\\%$ công suất tải, quay đầu ốc dây nguồn chúc xuống dưới"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_wire",
                name = "Dây Điện Đôi Cadivi 2x1.5mm & Ống Gen Ruột Gà",
                category = "4. Chiếu Sáng & Điện Năng",
                spec = "Lõi đồng nguyên chất bọc nhựa PVC tiêu chuẩn TCVN",
                quantity = "${(perimeter * 1.5).toInt()} Mét",
                unit = "Mét",
                estimatedCost = (perimeter * 1.5) * getPrice("wire_cadivi_2x15_m", 8500.0),
                isIncludedInPrice = input.includeLighting,
                note = "Dây trục cấp nguồn chính quanh chu vi biển, bọc co nhiệt chống chập tiếp điểm"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_timer",
                name = "Bộ Rơ-le Timer Hẹn Giờ Tự Động KG316T",
                category = "4. Chiếu Sáng & Điện Năng",
                spec = "Đồng hồ hẹn giờ điện tử 16 chương trình bật tắt tự động",
                quantity = "$qty Bộ",
                unit = "Bộ",
                estimatedCost = qty * getPrice("timer_relay_kg316t", 120000.0),
                isIncludedInPrice = input.includeTimerRelay,
                note = "Cài đặt tự động bật đèn lúc 18h00 và tắt lúc 23h30 bảo vệ tuổi thọ thiết bị"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_glue",
                name = "Keo Dán Đa Năng Titebond Heavy Duty Dán Alu",
                category = "5. Hoàn Thiện & Keo Dán",
                spec = "Keo vàng siêu dính chuyên dùng liên kết tấm alu với xương sắt",
                quantity = "$glueTubes Tuýp",
                unit = "Tuýp",
                estimatedCost = glueTubes * getPrice("glue_titebond", 65000.0),
                isIncludedInPrice = true,
                note = "Bơm keo hình zíc-zắc kèm 2 cuộn băng keo xốp 2 mặt định vị giữ tấm phẳng"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_silicone",
                name = "Silicon Trung Tính Apollo A500 Chống Dột Mép",
                category = "5. Hoàn Thiện & Keo Dán",
                spec = "Silicon trung tính co giãn đàn hồi theo nhiệt độ thời tiết",
                quantity = "$siliconTubes Chai",
                unit = "Chai",
                estimatedCost = siliconTubes * getPrice("silicone_apollo_a500", 45000.0),
                isIncludedInPrice = true,
                note = "Miết kín khe phay rãnh alu và chống thấm nước mặt lưng trên đỉnh biển"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_trim",
                name = "Nẹp Viền V Nhôm / Inox Bo Cạnh Biển",
                category = "5. Hoàn Thiện & Keo Dán",
                spec = if (input.vTrimTypeKey.contains("inox")) "V Inox 304 trắng sáng bóng 20x20mm" else "V Nhôm định hình mạ anot 20x20mm",
                quantity = "${perimeter.toInt()} Mét",
                unit = "Mét",
                estimatedCost = perimeter * getPrice(input.vTrimTypeKey, 18000.0),
                isIncludedInPrice = input.includeEdgeTrim && input.vTrimTypeKey != "none",
                note = "Nẹp bo viền mép bạt/alu che đầu vít và tạo khung bao sang trọng"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_drain",
                name = "Khoan Lỗ Thoát Nước & Hơi Ẩm Đáy Biển",
                category = "5. Hoàn Thiện & Keo Dán",
                spec = "Mũi khoan sắt phi 4mm - 6mm dưới chân mỗi chữ và đáy biển",
                quantity = "Quy chuẩn",
                unit = "Điểm khoan",
                estimatedCost = 0.0,
                isIncludedInPrice = true,
                note = "Tránh ngưng tụ hơi ẩm khi mưa bão, bảo vệ an toàn tuyệt đối cho LED & nguồn"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_labor",
                name = "Nhân Công Thợ Hàn Xưởng & Lắp Đặt Hiện Trường",
                category = "6. Nhân Công & Vận Chuyển",
                spec = "${input.laborComplexity.displayName} (Trang bị bảo hộ, đai an toàn)",
                quantity = "1 Gói",
                unit = "Gói",
                estimatedCost = laborCost,
                isIncludedInPrice = true,
                note = "Gia công kết cấu tại xưởng và hoàn thiện bắt vít, đấu điện tại công trình"
            ),
            com.example.model.AdvertisingChecklistItem(
                id = "chk_transport",
                name = "Vận Chuyển Xe Tải & Phụ Tùng Lắp Đặt Công Trình",
                category = "6. Nhân Công & Vận Chuyển",
                spec = "Xe tải chở pano/biển hiệu cồng kềnh bọc màng PE chống xước",
                quantity = "1 Chuyến",
                unit = "Chuyến",
                estimatedCost = shipping,
                isIncludedInPrice = true,
                note = "Chèn lót mút xốp chống trầy xước bề mặt alu, bạt và mặt mica"
            )
        )

        return EstimationResult(
            itemName = input.itemName,
            category = input.category,
            width = w,
            height = h,
            quantity = qty,
            netArea = netArea,
            grossAreaWithWastage = grossArea,
            perimeter = perimeter,
            breakdownItems = rows,
            totalMaterialCost = totalMaterialCost,
            laborCost = laborCost,
            shippingCost = shipping,
            totalCost = totalCost,
            profitAmount = profitAmount,
            finalCustomerPrice = finalPrice,
            unitPricePerM2 = unitPricePerM2,
            aiEngineeringTips = engineeringTips,
            advertisingChecklist = checklist,
            singlePieceArea = singlePieceArea,
            billableArea = billableArea,
            subtotalCustomerPrice = subtotalCustomerPrice,
            discountAmount = discountAmount,
            vatAmount = vatAmount,
            effectiveCategoryName = effectiveCategoryName
        )
    }
}
