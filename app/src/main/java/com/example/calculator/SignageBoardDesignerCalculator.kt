package com.example.calculator

import com.example.model.BoardDesignFamily
import com.example.model.BoardDesignSpec
import com.example.model.BoardQuoteCalculation
import com.example.model.BoardQuoteLine
import com.example.model.DecalLaminationType
import com.example.model.TarpFinishMode
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

object SignageBoardDesignerCalculator {

    private const val STANDARD_SHEET_AREA_M2 = 1.22 * 2.44 // 2.9768 m2

    fun calculate(spec: BoardDesignSpec): BoardQuoteCalculation {
        return when (spec.family) {
            BoardDesignFamily.TARP_BANNER -> calculateTarp(spec)
            BoardDesignFamily.ALU_3D_LETTER -> calculateAlu(spec)
            BoardDesignFamily.DECAL_STICKER -> calculateDecal(spec)
        }
    }

    private fun calculateTarp(spec: BoardDesignSpec): BoardQuoteCalculation {
        val w = spec.widthMeters.coerceAtLeast(0.1)
        val h = spec.heightMeters.coerceAtLeast(0.1)
        val qty = spec.quantity.coerceAtLeast(1)

        val rawSingle = w * h
        val billableSingle = if (spec.minOneM2PerPiece) max(1.0, rawSingle) else rawSingle
        val totalRaw = rawSingle * qty
        val totalBillable = billableSingle * qty
        val perimeter = (w + h) * 2.0 * qty

        val defaultM2Rate = spec.tarpProduct.printOnlyRatePerM2 + spec.tarpFinish.frameAddPerM2
        val effectiveRate = (spec.customUnitPriceOverride ?: defaultM2Rate).coerceAtLeast(0.0)

        val lines = mutableListOf<BoardQuoteLine>()
        lines.add(
            BoardQuoteLine(
                id = 1L,
                name = "${spec.tarpProduct.label} (${spec.tarpFinish.label})",
                spec = "Nội dung: ${spec.brandText.ifBlank { "Biển bạt" }} • KT ${fmt2(w)}x${fmt2(h)}m (SL: $qty)",
                unit = "m²",
                quantity = totalBillable,
                unitPrice = effectiveRate
            )
        )

        var grommetCount = 0
        if (spec.tarpFinish == TarpFinishMode.GROMMET_OR_POLE) {
            val spacingM = (spec.tarpGrommetSpacingCm.coerceIn(20, 100) / 100.0)
            grommetCount = max(4, ceil(perimeter / spacingM).toInt())
            lines.add(
                BoardQuoteLine(
                    id = 2L,
                    name = "Gia công đóng khoen đồng chống rỉ / gấp mép",
                    spec = "Bước khoen ${spec.tarpGrommetSpacingCm}cm quanh chu vi ${fmt2(perimeter)}m",
                    unit = "Khoen",
                    quantity = grommetCount.toDouble(),
                    unitPrice = 1500.0
                )
            )
        }

        if (spec.tarpFinish.needsLighting) {
            val lightCount = if (spec.tarpFinish == TarpFinishMode.LED_LIGHTBOX) {
                ceil(totalRaw * 4.0).toInt().coerceAtLeast(4)
            } else {
                ceil(w / 1.5).toInt().coerceAtLeast(1) * qty
            }
            val lightUnitName = if (spec.tarpFinish == TarpFinishMode.LED_LIGHTBOX) "Thanh LED 12V + Nguồn" else "Đèn pha LED 50W + Cần sắt"
            val lightUnitPrice = if (spec.tarpFinish == TarpFinishMode.LED_LIGHTBOX) 95000.0 else 280000.0
            lines.add(
                BoardQuoteLine(
                    id = 3L,
                    name = lightUnitName,
                    spec = "Chiếu sáng đều bề mặt ${fmt2(totalRaw)}m²",
                    unit = if (spec.tarpFinish == TarpFinishMode.LED_LIGHTBOX) "Bộ" else "Cái",
                    quantity = lightCount.toDouble(),
                    unitPrice = lightUnitPrice
                )
            )
        }

        if (spec.extraDesignOrInstallFee > 0.0) {
            lines.add(
                BoardQuoteLine(
                    id = 4L,
                    name = "Phí thiết kế file in & lắp đặt tận nơi",
                    spec = "Lắp đặt hoàn thiện tại công trình",
                    unit = "Trọn gói",
                    quantity = 1.0,
                    unitPrice = spec.extraDesignOrInstallFee
                )
            )
        }

        // Roll optimization suggestion for Tarp (1.5m, 2.2m, 3.2m)
        val bestRoll = listOf(1.5, 2.2, 3.2).map { rw ->
            SmartMaterialCalculator.optimizeRollUsage(w, h, qty, rw, spec.tarpProduct.printOnlyRatePerM2)
        }.minByOrNull { if (it.needsTiling) 100.0 + it.wastePercent else it.wastePercent }

        val rollHint = if (bestRoll != null) {
            val tileText = if (bestRoll.needsTiling) " (ghép ${bestRoll.tilePanelsCount} mảnh)" else " (nguyên khổ không nối)"
            "Khổ cuộn bạt tối ưu: ${fmt2(bestRoll.rollWidthM)}m • Cần ${fmt2(bestRoll.linearMetersNeeded)}m dài • Hao hụt ${fmt1(bestRoll.wastePercent)}%$tileText"
        } else {
            "Khổ cuộn chuẩn 3.2m"
        }

        val ironBars = if (spec.tarpFinish.needsIronFrame) {
            ceil(((w + h) * 2.0 + ceil(w / 0.8) * h) * qty * 1.08 / 6.0).toInt().coerceAtLeast(1)
        } else 0

        val matSummary = if (spec.tarpFinish.needsIronFrame) {
            "Chu vi viền: ${fmt2(perimeter)}m • Dự kiến ~$ironBars cây sắt hộp 25x25 (6m)${if (grommetCount > 0) " • $grommetCount khoen" else ""}"
        } else {
            "Chu vi bạt: ${fmt2(perimeter)}m • Thành phẩm nhẹ dễ vận chuyển${if (grommetCount > 0) " • $grommetCount khoen" else ""}"
        }

        return finalizeSummary(
            spec = spec,
            rawSingle = rawSingle,
            billableSingle = billableSingle,
            totalRaw = totalRaw,
            totalBillable = totalBillable,
            perimeter = perimeter,
            materialPlanSummary = matSummary,
            rollOrSheetHint = rollHint,
            lines = lines,
            costRatio = 0.68
        )
    }

    private fun calculateAlu(spec: BoardDesignSpec): BoardQuoteCalculation {
        val w = spec.widthMeters.coerceAtLeast(0.1)
        val h = spec.heightMeters.coerceAtLeast(0.1)
        val qty = spec.quantity.coerceAtLeast(1)

        val rawSingle = w * h
        val billableSingle = if (spec.minOneM2PerPiece) max(1.0, rawSingle) else rawSingle
        val totalRaw = rawSingle * qty
        val totalBillable = billableSingle * qty
        val perimeter = (w + h) * 2.0 * qty

        val aluRate = (spec.customUnitPriceOverride ?: spec.aluPanel.installedRatePerM2).coerceAtLeast(0.0)
        val letterArea = (totalRaw * (spec.letterCoveragePercent.coerceIn(10, 80) / 100.0)).coerceAtLeast(0.3)

        val lines = mutableListOf<BoardQuoteLine>()
        lines.add(
            BoardQuoteLine(
                id = 11L,
                name = "Mặt dựng ${spec.aluPanel.label}",
                spec = if (spec.includeAluIronFrame) "Bao gồm khung thép hộp 25x25 mạ kẽm & keo Titebond/Silicone" else "Ốp Alu trực tiếp lên tường phẳng",
                unit = "m²",
                quantity = totalBillable,
                unitPrice = aluRate + (if (spec.includeAluIronFrame) 140000.0 else 0.0)
            )
        )

        lines.add(
            BoardQuoteLine(
                id = 12L,
                name = "Bộ chữ nổi 3D: ${spec.letter3d.label}",
                spec = "Nội dung '${spec.brandText.ifBlank { "Thương hiệu" }}' • Chân cao ${spec.letterExtrusionCm}cm • Phủ ${spec.letterCoveragePercent}% mặt biển",
                unit = "m² chữ",
                quantity = letterArea,
                unitPrice = spec.letter3d.ratePerLetterM2
            )
        )

        val ledCount = if (spec.letterLighting.hasLed) ceil(letterArea * 85.0).toInt().coerceAtLeast(25) else 0
        val powerSupplyCount = if (spec.letterLighting.hasLed) {
            val wattsNeeded = (ledCount * 1.2) / 0.8 // 80% safe load rule
            ceil(wattsNeeded / 400.0).toInt().coerceAtLeast(1)
        } else 0

        if (spec.letterLighting.hasLed) {
            lines.add(
                BoardQuoteLine(
                    id = 13L,
                    name = "Hệ thống LED 12V (${spec.letterLighting.label})",
                    spec = "~$ledCount module LED + $powerSupplyCount nguồn 12V-33A (dư tải 80%)${if (spec.includeTimerRelay) " + Timer hẹn giờ" else ""}",
                    unit = "m² chữ",
                    quantity = letterArea,
                    unitPrice = spec.letterLighting.ledPricePerLetterM2
                )
            )
        }

        if (spec.extraDesignOrInstallFee > 0.0) {
            lines.add(
                BoardQuoteLine(
                    id = 14L,
                    name = "Chi phí lắp đặt, giàn giáo & nẹp V bo viền",
                    spec = "Hoàn thiện tại mặt bằng & bàn giao nghiệm thu",
                    unit = "Trọn gói",
                    quantity = 1.0,
                    unitPrice = spec.extraDesignOrInstallFee
                )
            )
        }

        val aluSheets = ceil((totalRaw * 1.08) / STANDARD_SHEET_AREA_M2).toInt().coerceAtLeast(1)
        val steelBars = if (spec.includeAluIronFrame) {
            val cols = ceil(w / 0.61).toInt() + 1
            val rows = ceil(h / 0.61).toInt() + 1
            ceil(((cols * h + rows * w) * qty * 1.08) / 6.0).toInt().coerceAtLeast(1)
        } else 0
        val titebondTubes = ceil(aluSheets * 1.5).toInt().coerceAtLeast(2)

        val rollOrSheetHint = "Cần $aluSheets tấm Alu chuẩn 1.22x2.44m (đã tính 8% hao hụt cắt gập cạnh)"
        val matSummary = buildString {
            append("Tỷ lệ chữ 3D: ${fmt2(letterArea)}m² • Keo Titebond: $titebondTubes tuýp")
            if (steelBars > 0) append(" • Khung sắt 6m: $steelBars cây")
            if (ledCount > 0) append(" • $ledCount LED + $powerSupplyCount nguồn 400W")
        }

        return finalizeSummary(
            spec = spec,
            rawSingle = rawSingle,
            billableSingle = billableSingle,
            totalRaw = totalRaw,
            totalBillable = totalBillable,
            perimeter = perimeter,
            materialPlanSummary = matSummary,
            rollOrSheetHint = rollOrSheetHint,
            lines = lines,
            costRatio = 0.65
        )
    }

    private fun calculateDecal(spec: BoardDesignSpec): BoardQuoteCalculation {
        val w = spec.widthMeters.coerceAtLeast(0.1)
        val h = spec.heightMeters.coerceAtLeast(0.1)
        val qty = spec.quantity.coerceAtLeast(1)

        val rawSingle = w * h
        val billableSingle = if (spec.minOneM2PerPiece) max(1.0, rawSingle) else rawSingle
        val totalRaw = rawSingle * qty
        val totalBillable = billableSingle * qty
        val perimeter = (w + h) * 2.0 * qty

        val printAndLamRate = (spec.customUnitPriceOverride
            ?: (spec.decalProduct.printRatePerM2 + spec.decalLamination.addRatePerM2)).coerceAtLeast(0.0)

        val lines = mutableListOf<BoardQuoteLine>()
        lines.add(
            BoardQuoteLine(
                id = 21L,
                name = "${spec.decalProduct.label} (${spec.decalLamination.label})",
                spec = "Nội dung '${spec.brandText.ifBlank { "Decal" }}' • KT ${fmt2(w)}x${fmt2(h)}m (SL: $qty)",
                unit = "m²",
                quantity = totalBillable,
                unitPrice = printAndLamRate
            )
        )

        if (spec.decalBacking.addRatePerM2 > 0.0) {
            lines.add(
                BoardQuoteLine(
                    id = 22L,
                    name = "Gia công bồi nền cứng: ${spec.decalBacking.label}",
                    spec = "Cán phẳng chống bọt khí, cắt thành phẩm sắc nét",
                    unit = "m²",
                    quantity = totalBillable,
                    unitPrice = spec.decalBacking.addRatePerM2
                )
            )
        }

        if (spec.includeOnSiteInstall) {
            lines.add(
                BoardQuoteLine(
                    id = 23L,
                    name = "Nhân công dán kính / ốp bảng hoàn thiện tận nơi",
                    spec = "Vệ sinh bề mặt, xịt nước căn chỉnh chuẩn bọt khí",
                    unit = "m²",
                    quantity = totalBillable,
                    unitPrice = 65000.0
                )
            )
        }

        if (spec.extraDesignOrInstallFee > 0.0) {
            lines.add(
                BoardQuoteLine(
                    id = 24L,
                    name = "Phí thiết kế / cắt bế demi & phụ kiện treo",
                    spec = "Phí gia công bổ sung theo yêu cầu",
                    unit = "Trọn gói",
                    quantity = 1.0,
                    unitPrice = spec.extraDesignOrInstallFee
                )
            )
        }

        val bestDecalRoll = listOf(0.914, 1.07, 1.27, 1.52).map { rw ->
            SmartMaterialCalculator.optimizeRollUsage(w, h, qty, rw, spec.decalProduct.printRatePerM2)
        }.minByOrNull { if (it.needsTiling) 100.0 + it.wastePercent else it.wastePercent }

        val rollHint = if (bestDecalRoll != null) {
            val tileText = if (bestDecalRoll.needsTiling) " (chia ${bestDecalRoll.tilePanelsCount} dải)" else " (in nguyên tấm)"
            "Khổ cuộn Decal tối ưu: ${fmt2(bestDecalRoll.rollWidthM)}m • Cần ${fmt2(bestDecalRoll.linearMetersNeeded)}m dài • Hao hụt ${fmt1(bestDecalRoll.wastePercent)}%$tileText"
        } else {
            "Khổ cuộn Decal tiêu chuẩn 1.27m"
        }

        val backingSheets = if (spec.decalBacking.usesRigidSheet) {
            ceil((totalRaw * 1.08) / STANDARD_SHEET_AREA_M2).toInt().coerceAtLeast(1)
        } else 0

        val matSummary = buildString {
            append("Chu vi cắt: ${fmt2(perimeter)}m")
            if (spec.decalLamination != DecalLaminationType.NONE) {
                append(" • ${spec.decalLamination.label}")
            }
            if (backingSheets > 0) {
                append(" • Cần $backingSheets tấm nền 1.22x2.44m")
            }
        }

        return finalizeSummary(
            spec = spec,
            rawSingle = rawSingle,
            billableSingle = billableSingle,
            totalRaw = totalRaw,
            totalBillable = totalBillable,
            perimeter = perimeter,
            materialPlanSummary = matSummary,
            rollOrSheetHint = rollHint,
            lines = lines,
            costRatio = 0.62
        )
    }

    private fun finalizeSummary(
        spec: BoardDesignSpec,
        rawSingle: Double,
        billableSingle: Double,
        totalRaw: Double,
        totalBillable: Double,
        perimeter: Double,
        materialPlanSummary: String,
        rollOrSheetHint: String,
        lines: List<BoardQuoteLine>,
        costRatio: Double
    ): BoardQuoteCalculation {
        val subtotal = lines.sumOf { it.lineTotal }
        val discountAmount = subtotal * (spec.discountPercent.coerceIn(0.0, 90.0) / 100.0)
        val afterDiscount = (subtotal - discountAmount).coerceAtLeast(0.0)
        val vatAmount = afterDiscount * (spec.vatPercent.coerceIn(0.0, 30.0) / 100.0)
        val grandTotal = afterDiscount + vatAmount
        val estimatedCost = subtotal * costRatio
        val estimatedProfit = (grandTotal - vatAmount - estimatedCost).coerceAtLeast(0.0)
        val effectiveRatePerM2 = if (totalBillable > 0.0) grandTotal / totalBillable else 0.0

        return BoardQuoteCalculation(
            rawAreaPerPiece = rawSingle,
            billableAreaPerPiece = billableSingle,
            totalRawArea = totalRaw,
            totalBillableArea = totalBillable,
            perimeterMeters = perimeter,
            materialPlanSummary = materialPlanSummary,
            rollOrSheetHint = rollOrSheetHint,
            lines = lines,
            subtotal = subtotal,
            discountAmount = discountAmount,
            afterDiscount = afterDiscount,
            vatAmount = vatAmount,
            grandTotal = grandTotal,
            estimatedCost = estimatedCost,
            estimatedProfit = estimatedProfit,
            effectiveRatePerM2 = effectiveRatePerM2
        )
    }

    private fun fmt2(v: Double): String = String.format(Locale.US, "%.2f", v)
    private fun fmt1(v: Double): String = String.format(Locale.US, "%.1f", v)
}
