package com.example.calculator

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

enum class DimensionUnit(val label: String, val toMetersFactor: Double, val toMmFactor: Double) {
    M("Mét (m)", 1.0, 1000.0),
    CM("Cm", 0.01, 10.0),
    MM("Mm", 0.001, 1.0)
}

data class SmartQuoteLine(
    val id: Long = System.nanoTime(),
    val title: String,
    val widthMeters: Double,
    val heightMeters: Double,
    val rawDisplaySize: String,
    val quantity: Int,
    val unitPricePerM2: Double,
    val minOneM2PerPiece: Boolean,
    val extraProcessingFeePerPiece: Double = 0.0
) {
    val singleRawArea: Double get() = widthMeters * heightMeters
    val billableSingleArea: Double
        get() = if (minOneM2PerPiece && singleRawArea > 0.0) max(1.0, singleRawArea) else singleRawArea
    val totalRawArea: Double get() = singleRawArea * quantity
    val totalBillableArea: Double get() = billableSingleArea * quantity
    val perimeterMeters: Double get() = (widthMeters + heightMeters) * 2.0 * quantity
    val lineTotal: Double
        get() = (totalBillableArea * unitPricePerM2) + (extraProcessingFeePerPiece * quantity)
}

data class SmartQuoteSummary(
    val totalQuantity: Int,
    val totalRawArea: Double,
    val totalBillableArea: Double,
    val totalPerimeter: Double,
    val subtotal: Double,
    val discountAmount: Double,
    val vatAmount: Double,
    val grandTotal: Double
)

data class PlacedCutPiece(
    val xMm: Double,
    val yMm: Double,
    val wMm: Double,
    val hMm: Double,
    val rotated: Boolean
)

data class SheetOptimizationResult(
    val sheetWidthMm: Double,
    val sheetHeightMm: Double,
    val pieceWidthMm: Double,
    val pieceHeightMm: Double,
    val requestedQuantity: Int,
    val kerfMm: Double,
    val piecesPerSheet: Int,
    val sheetsNeeded: Int,
    val layoutStrategyName: String,
    val efficiencyPercent: Double,
    val wasteAreaM2Total: Double,
    val totalSheetCost: Double,
    val normalCountPerSheet: Int,
    val rotatedCountPerSheet: Int,
    val sampleSheetPieces: List<PlacedCutPiece>,
    val fitsSingleSheet: Boolean,
    val warningMessage: String = ""
)

data class RollOptionEval(
    val rollWidthM: Double,
    val orientationLabel: String,
    val acrossCount: Int,
    val linearMetersNeeded: Double,
    val totalUsedAreaM2: Double,
    val wastePercent: Double,
    val needsTiling: Boolean,
    val tilePanelsCount: Int,
    val estimatedCost: Double
)

data class FrameAndLedCalcResult(
    val netAreaM2: Double,
    val perimeterM: Double,
    val totalSteelMeters: Double,
    val steelBars6m: Int,
    val steelTotalCost: Double,
    val titebondTubes: Int,
    val siliconeTubes: Int,
    val ledTotalWattsWithSafety: Double,
    val recommendedPower12V33ACount: Int,
    val cadiviWireMeters: Double,
    val estimatedHardwareCost: Double
)

object SmartMaterialCalculator {

    fun calculateQuoteSummary(
        lines: List<SmartQuoteLine>,
        discountPercent: Double,
        vatPercent: Double
    ): SmartQuoteSummary {
        val totalQty = lines.sumOf { it.quantity }
        val totalRaw = lines.sumOf { it.totalRawArea }
        val totalBillable = lines.sumOf { it.totalBillableArea }
        val totalPerim = lines.sumOf { it.perimeterMeters }
        val subtotal = lines.sumOf { it.lineTotal }
        val discount = subtotal * (discountPercent.coerceIn(0.0, 100.0) / 100.0)
        val afterDiscount = (subtotal - discount).coerceAtLeast(0.0)
        val vat = afterDiscount * (vatPercent.coerceIn(0.0, 30.0) / 100.0)
        return SmartQuoteSummary(
            totalQuantity = totalQty,
            totalRawArea = totalRaw,
            totalBillableArea = totalBillable,
            totalPerimeter = totalPerim,
            subtotal = subtotal,
            discountAmount = discount,
            vatAmount = vat,
            grandTotal = afterDiscount + vat
        )
    }

    /**
     * Tối ưu hóa sơ đồ cắt tấm chuẩn (1220x2440mm) so sánh 3 chiến lược:
     * 1. Xếp thẳng (Normal)
     * 2. Xoay ngang 90° (Rotated)
     * 3. Xếp ghép hỗn hợp tận dụng phần biên thừa (Mixed Guillotine)
     */
    fun optimizeSheetCutting(
        pieceWidthMm: Double,
        pieceHeightMm: Double,
        quantity: Int,
        sheetWidthMm: Double = 1220.0,
        sheetHeightMm: Double = 2440.0,
        kerfMm: Double = 4.0,
        sheetPrice: Double = 360000.0
    ): SheetOptimizationResult {
        val pw = pieceWidthMm.coerceAtLeast(10.0)
        val ph = pieceHeightMm.coerceAtLeast(10.0)
        val sw = sheetWidthMm.coerceAtLeast(100.0)
        val sh = sheetHeightMm.coerceAtLeast(100.0)
        val qty = quantity.coerceAtLeast(1)
        val kerf = kerfMm.coerceAtLeast(0.0)

        fun packUniform(w: Double, h: Double, rotated: Boolean): List<PlacedCutPiece> {
            val cols = floor((sw + kerf) / (w + kerf)).toInt().coerceAtLeast(0)
            val rows = floor((sh + kerf) / (h + kerf)).toInt().coerceAtLeast(0)
            val list = ArrayList<PlacedCutPiece>(cols * rows)
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    list.add(
                        PlacedCutPiece(
                            xMm = c * (w + kerf),
                            yMm = r * (h + kerf),
                            wMm = w,
                            hMm = h,
                            rotated = rotated
                        )
                    )
                }
            }
            return list
        }

        // Strategy 3: Mixed horizontal/vertical guillotine strip fill
        fun packMixed(primaryW: Double, primaryH: Double, primaryRotated: Boolean): List<PlacedCutPiece> {
            val cols = floor((sw + kerf) / (primaryW + kerf)).toInt().coerceAtLeast(0)
            val rows = floor((sh + kerf) / (primaryH + kerf)).toInt().coerceAtLeast(0)
            if (cols == 0 || rows == 0) return emptyList()

            val result = ArrayList<PlacedCutPiece>()
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    result.add(
                        PlacedCutPiece(
                            xMm = c * (primaryW + kerf),
                            yMm = r * (primaryH + kerf),
                            wMm = primaryW,
                            hMm = primaryH,
                            rotated = primaryRotated
                        )
                    )
                }
            }

            val secW = primaryH
            val secH = primaryW
            val secRotated = !primaryRotated

            // Remaining right strip
            val usedWidth = cols * primaryW + max(0, cols - 1) * kerf
            val remRightW = sw - usedWidth - kerf
            if (remRightW >= secW) {
                val extraCols = floor((remRightW + kerf) / (secW + kerf)).toInt().coerceAtLeast(0)
                val extraRows = floor((sh + kerf) / (secH + kerf)).toInt().coerceAtLeast(0)
                val startX = usedWidth + kerf
                for (r in 0 until extraRows) {
                    for (c in 0 until extraCols) {
                        result.add(
                            PlacedCutPiece(
                                xMm = startX + c * (secW + kerf),
                                yMm = r * (secH + kerf),
                                wMm = secW,
                                hMm = secH,
                                rotated = secRotated
                            )
                        )
                    }
                }
            }

            // Remaining bottom strip under main grid (width = usedWidth)
            val usedHeight = rows * primaryH + max(0, rows - 1) * kerf
            val remBottomH = sh - usedHeight - kerf
            if (remBottomH >= secH) {
                val extraCols = floor((usedWidth + kerf) / (secW + kerf)).toInt().coerceAtLeast(0)
                val extraRows = floor((remBottomH + kerf) / (secH + kerf)).toInt().coerceAtLeast(0)
                val startY = usedHeight + kerf
                for (r in 0 until extraRows) {
                    for (c in 0 until extraCols) {
                        result.add(
                            PlacedCutPiece(
                                xMm = c * (secW + kerf),
                                yMm = startY + r * (secH + kerf),
                                wMm = secW,
                                hMm = secH,
                                rotated = secRotated
                            )
                        )
                    }
                }
            }
            return result
        }

        val normalLayout = packUniform(pw, ph, rotated = false)
        val rotatedLayout = packUniform(ph, pw, rotated = true)
        val mixedA = packMixed(pw, ph, primaryRotated = false)
        val mixedB = packMixed(ph, pw, primaryRotated = true)

        val candidates = listOf(
            "Xếp thẳng tiêu chuẩn" to normalLayout,
            "Xoay ngang 90° tiết kiệm" to rotatedLayout,
            "Ghép hỗn hợp dọc + xoay tận dụng rìa" to mixedA,
            "Ghép hỗn hợp ngang + xoay tận dụng rìa" to mixedB
        )

        val best = candidates.maxByOrNull { it.second.size } ?: candidates.first()
        val perSheet = best.second.size

        if (perSheet == 0) {
            val singlePieceAreaM2 = (pw * ph) / 1_000_000.0
            val sheetAreaM2 = (sw * sh) / 1_000_000.0
            val sheetsForOversize = ceil((singlePieceAreaM2 * qty) / sheetAreaM2).toInt().coerceAtLeast(1)
            return SheetOptimizationResult(
                sheetWidthMm = sw,
                sheetHeightMm = sh,
                pieceWidthMm = pw,
                pieceHeightMm = ph,
                requestedQuantity = qty,
                kerfMm = kerf,
                piecesPerSheet = 0,
                sheetsNeeded = sheetsForOversize,
                layoutStrategyName = "Kích thước vượt khổ 1 tấm nguyên (cần ghép nối)",
                efficiencyPercent = ((singlePieceAreaM2 * qty) / (sheetsForOversize * sheetAreaM2) * 100.0).coerceIn(0.0, 100.0),
                wasteAreaM2Total = max(0.0, (sheetsForOversize * sheetAreaM2) - (singlePieceAreaM2 * qty)),
                totalSheetCost = sheetsForOversize * sheetPrice,
                normalCountPerSheet = 0,
                rotatedCountPerSheet = 0,
                sampleSheetPieces = emptyList(),
                fitsSingleSheet = false,
                warningMessage = "Mảnh cắt (${pw.toInt()}x${ph.toInt()}mm) lớn hơn khổ tấm (${sw.toInt()}x${sh.toInt()}mm). Cần chia ghép mí trên khung xương."
            )
        }

        val sheetsNeeded = ceil(qty.toDouble() / perSheet.toDouble()).toInt().coerceAtLeast(1)
        val sheetAreaM2 = (sw * sh) / 1_000_000.0
        val totalBoughtArea = sheetsNeeded * sheetAreaM2
        val totalUsefulArea = ((pw * ph) / 1_000_000.0) * qty
        val efficiency = ((totalUsefulArea / totalBoughtArea) * 100.0).coerceIn(0.0, 100.0)
        val wasteArea = (totalBoughtArea - totalUsefulArea).coerceAtLeast(0.0)
        val displayedPieces = if (qty < perSheet) best.second.take(qty) else best.second

        return SheetOptimizationResult(
            sheetWidthMm = sw,
            sheetHeightMm = sh,
            pieceWidthMm = pw,
            pieceHeightMm = ph,
            requestedQuantity = qty,
            kerfMm = kerf,
            piecesPerSheet = perSheet,
            sheetsNeeded = sheetsNeeded,
            layoutStrategyName = best.first,
            efficiencyPercent = efficiency,
            wasteAreaM2Total = wasteArea,
            totalSheetCost = sheetsNeeded * sheetPrice,
            normalCountPerSheet = normalLayout.size,
            rotatedCountPerSheet = rotatedLayout.size,
            sampleSheetPieces = displayedPieces,
            fitsSingleSheet = true
        )
    }

    /**
     * Tính tối ưu xếp khổ cuộn (Bạt 1.8m/2.2m/2.5m/3.2m hoặc Decal 1.07m/1.27m/1.52m)
     */
    fun optimizeRollUsage(
        pieceWidthM: Double,
        pieceHeightM: Double,
        quantity: Int,
        rollWidthM: Double,
        pricePerM2: Double
    ): RollOptionEval {
        val w = pieceWidthM.coerceAtLeast(0.05)
        val h = pieceHeightM.coerceAtLeast(0.05)
        val qty = quantity.coerceAtLeast(1)
        val rw = rollWidthM.coerceAtLeast(0.3)

        val minSide = min(w, h)
        if (minSide > rw) {
            // Needs tiling across roll width
            val panels = ceil(minSide / (rw - 0.03)).toInt().coerceAtLeast(2)
            val lengthSide = max(w, h)
            val linearM = panels * lengthSide * qty
            val usedArea = linearM * rw
            val netArea = w * h * qty
            val wastePct = (((usedArea - netArea) / usedArea) * 100.0).coerceIn(0.0, 99.0)
            return RollOptionEval(
                rollWidthM = rw,
                orientationLabel = "Vượt khổ cuộn -> Chia $panels mảnh nối mí 3cm",
                acrossCount = 1,
                linearMetersNeeded = linearM,
                totalUsedAreaM2 = usedArea,
                wastePercent = wastePct,
                needsTiling = true,
                tilePanelsCount = panels,
                estimatedCost = usedArea * pricePerM2
            )
        }

        // Option A: w across roll width, h along roll length
        val acrossA = if (w <= rw) floor(rw / w).toInt().coerceAtLeast(1) else 0
        val linearA = if (acrossA > 0) ceil(qty.toDouble() / acrossA).toInt() * h else Double.MAX_VALUE

        // Option B: h across roll width, w along roll length (rotate 90°)
        val acrossB = if (h <= rw) floor(rw / h).toInt().coerceAtLeast(1) else 0
        val linearB = if (acrossB > 0) ceil(qty.toDouble() / acrossB).toInt() * w else Double.MAX_VALUE

        val useA = linearA <= linearB
        val bestAcross = if (useA) acrossA else acrossB
        val bestLinear = if (useA) linearA else linearB
        val label = if (useA) {
            "Xếp ngang ${String.format("%.2f", w)}m dọc khổ ${String.format("%.2f", rw)}m ($bestAcross tấm/hàng)"
        } else {
            "Xoay 90° xếp ${String.format("%.2f", h)}m dọc khổ ${String.format("%.2f", rw)}m ($bestAcross tấm/hàng)"
        }

        val usedArea = bestLinear * rw
        val netArea = w * h * qty
        val wastePct = if (usedArea > 0) (((usedArea - netArea) / usedArea) * 100.0).coerceIn(0.0, 99.0) else 0.0

        return RollOptionEval(
            rollWidthM = rw,
            orientationLabel = label,
            acrossCount = bestAcross,
            linearMetersNeeded = bestLinear,
            totalUsedAreaM2 = usedArea,
            wastePercent = wastePct,
            needsTiling = false,
            tilePanelsCount = 1,
            estimatedCost = usedArea * pricePerM2
        )
    }

    /**
     * Tính cây sắt hộp 6m, keo dán và bộ nguồn LED chuẩn an toàn 80%
     */
    fun calculateFrameAndLed(
        widthM: Double,
        heightM: Double,
        gridSpacingM: Double,
        ledCount: Int,
        ledWattPerUnit: Double = 1.2,
        steelPricePerMeter: Double = 24000.0
    ): FrameAndLedCalcResult {
        val w = widthM.coerceAtLeast(0.1)
        val h = heightM.coerceAtLeast(0.1)
        val spacing = gridSpacingM.coerceIn(0.3, 2.0)

        val netArea = w * h
        val perimeter = (w + h) * 2.0
        val horizBars = ceil(h / spacing).toInt() + 1
        val vertBars = ceil(w / spacing).toInt() + 1
        val rawSteelM = (horizBars * w) + (vertBars * h)
        val steelWithWastageM = rawSteelM * 1.05
        val bars6m = ceil(steelWithWastageM / 6.0).toInt().coerceAtLeast(1)
        val steelCost = bars6m * 6.0 * steelPricePerMeter

        val titebond = ceil(netArea / 2.8).toInt().coerceAtLeast(1)
        val silicone = ceil(netArea / 5.0).toInt().coerceAtLeast(1)

        val totalLedWatts = ledCount.coerceAtLeast(0) * ledWattPerUnit.coerceAtLeast(0.1)
        val safeWattsNeeded = if (totalLedWatts > 0) totalLedWatts / 0.8 else 0.0
        // 12V 33A = 400W nominal
        val powerCount = if (safeWattsNeeded > 0) ceil(safeWattsNeeded / 400.0).toInt().coerceAtLeast(1) else 0
        val wireMeters = perimeter * 1.5
        val hwCost = steelCost + (titebond * 65000.0) + (silicone * 45000.0) + (powerCount * 135000.0) + (wireMeters * 8500.0)

        return FrameAndLedCalcResult(
            netAreaM2 = netArea,
            perimeterM = perimeter,
            totalSteelMeters = steelWithWastageM,
            steelBars6m = bars6m,
            steelTotalCost = steelCost,
            titebondTubes = titebond,
            siliconeTubes = silicone,
            ledTotalWattsWithSafety = safeWattsNeeded,
            recommendedPower12V33ACount = powerCount,
            cadiviWireMeters = wireMeters,
            estimatedHardwareCost = hwCost
        )
    }
}
