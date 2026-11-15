package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.BoardDesignFamily
import com.example.model.BoardDesignSpec
import com.example.model.BoardQuoteCalculation
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BoardQuotePdfExporter {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private val fileDate = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)

    fun generateBoardQuotePdf(
        context: Context,
        spec: BoardDesignSpec,
        calc: BoardQuoteCalculation
    ): File {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = spec.brandText.ifBlank { spec.family.shortTitle }
            .replace(Regex("[^a-zA-Z0-9_]"), "_")
            .take(24)
        val file = File(dir, "Bao_Gia_${spec.family.name}_${safeName}_${fileDate.format(Date())}.pdf")

        try {
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val canvas = page.canvas

            val left = 36f
            val right = 559f
            val contentW = right - left
            var y = 32f

            val navyPaint = Paint().apply { color = Color.rgb(15, 23, 42); style = Paint.Style.FILL }
            val whiteBold = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subHeaderPaint = Paint().apply { isAntiAlias = true; color = Color.rgb(148, 163, 184); textSize = 8.5f }
            val cyanRight = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(34, 211, 238)
                textSize = 8.5f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val sectionPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(15, 23, 42)
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint().apply { isAntiAlias = true; color = Color.rgb(30, 41, 59); textSize = 8.3f }
            val boldPaint = Paint(bodyPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
            val mutedPaint = Paint().apply { isAntiAlias = true; color = Color.rgb(100, 116, 139); textSize = 7.6f }
            val borderPaint = Paint().apply { color = Color.rgb(203, 213, 225); style = Paint.Style.STROKE; strokeWidth = 0.9f }

            // Header
            canvas.drawRoundRect(RectF(left, y, right, y + 52f), 8f, 8f, navyPaint)
            canvas.drawText(spec.companyName.ifBlank { "ĐỨC ANH ADVERTISEMENT PRO" }, left + 12f, y + 20f, whiteBold)
            canvas.drawText(
                "BẢNG THIẾT KẾ & BÁO GIÁ: ${spec.family.fullTitle.uppercase(Locale.getDefault())}",
                left + 12f,
                y + 36f,
                subHeaderPaint
            )
            canvas.drawText("Ngày: ${dateFormat.format(Date())}", right - 12f, y + 20f, cyanRight)
            canvas.drawText("Hotline: ${spec.companyPhone}", right - 12f, y + 36f, subHeaderPaint.apply { textAlign = Paint.Align.RIGHT })
            subHeaderPaint.textAlign = Paint.Align.LEFT
            y += 62f

            // Customer & Dimension Meta
            val infoFill = Paint().apply { color = Color.rgb(248, 250, 252); style = Paint.Style.FILL }
            canvas.drawRoundRect(RectF(left, y, right, y + 44f), 6f, 6f, infoFill)
            canvas.drawRoundRect(RectF(left, y, right, y + 44f), 6f, 6f, borderPaint)
            canvas.drawText("Khách hàng: ${spec.clientName.ifBlank { "Quý Khách Hàng" }}   |   Số lượng: ${spec.quantity} bộ", left + 10f, y + 15f, boldPaint)
            canvas.drawText(
                "Kích thước mỗi bảng: ${String.format(Locale.US, "%.2f", spec.widthMeters)}m x ${String.format(Locale.US, "%.2f", spec.heightMeters)}m  •  Tổng diện tích: ${String.format(Locale.US, "%.2f", calc.totalRawArea)} m² (Tính tiền: ${String.format(Locale.US, "%.2f", calc.totalBillableArea)} m²)",
                left + 10f,
                y + 28f,
                bodyPaint
            )
            canvas.drawText(calc.rollOrSheetHint.take(92), left + 10f, y + 39f, mutedPaint)
            y += 54f

            // Vector Schematic Preview Box
            canvas.drawText("1. BẢN VẼ PHÁC THẢO KÍCH THƯỚC & BỐ CỤC", left, y, sectionPaint)
            y += 8f
            val previewBoxH = 138f
            val boxRect = RectF(left, y, right, y + previewBoxH)
            canvas.drawRoundRect(boxRect, 6f, 6f, infoFill)
            canvas.drawRoundRect(boxRect, 6f, 6f, borderPaint)

            val aspect = (spec.widthMeters / spec.heightMeters.coerceAtLeast(0.1)).toFloat().coerceIn(0.4f, 5.5f)
            val maxBoardW = contentW - 80f
            val maxBoardH = previewBoxH - 40f
            var drawW = maxBoardW
            var drawH = drawW / aspect
            if (drawH > maxBoardH) {
                drawH = maxBoardH
                drawW = drawH * aspect
            }
            val bx = left + (contentW - drawW) / 2f
            val by = y + (previewBoxH - drawH) / 2f
            val boardRect = RectF(bx, by, bx + drawW, by + drawH)

            val bgInt = spec.colorTheme.bgHexLong.toInt()
            val txtInt = spec.colorTheme.textHexLong.toInt()
            val boardFill = Paint().apply { color = bgInt; style = Paint.Style.FILL }
            val boardStroke = Paint().apply { color = Color.rgb(51, 65, 85); style = Paint.Style.STROKE; strokeWidth = 1.6f }
            canvas.drawRect(boardRect, boardFill)
            canvas.drawRect(boardRect, boardStroke)

            if (spec.family == BoardDesignFamily.ALU_3D_LETTER) {
                val gridPaint = Paint().apply {
                    color = Color.argb(90, 255, 255, 255)
                    strokeWidth = 0.7f
                    pathEffect = DashPathEffect(floatArrayOf(4f, 3f), 0f)
                }
                val cols = kotlin.math.ceil(spec.widthMeters / 1.22).toInt().coerceAtLeast(1)
                for (c in 1 until cols) {
                    val gx = bx + (c * 1.22f / spec.widthMeters.toFloat()) * drawW
                    if (gx < bx + drawW) canvas.drawLine(gx, by, gx, by + drawH, gridPaint)
                }
            }

            val brandPaint = Paint().apply {
                isAntiAlias = true
                color = txtInt
                textAlign = Paint.Align.CENTER
                textSize = (drawH * 0.28f).coerceIn(11f, 24f)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val sloganPaint = Paint().apply {
                isAntiAlias = true
                color = txtInt
                alpha = 210
                textAlign = Paint.Align.CENTER
                textSize = (drawH * 0.13f).coerceIn(7.5f, 12f)
            }
            canvas.drawText(spec.brandText.ifBlank { "NỘI DUNG BIỂN HIỆU" }.take(28), bx + drawW / 2f, by + drawH * 0.52f, brandPaint)
            if (spec.sloganText.isNotBlank()) {
                canvas.drawText(spec.sloganText.take(40), bx + drawW / 2f, by + drawH * 0.76f, sloganPaint)
            }

            val dimPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(2, 132, 199)
                textSize = 7.8f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("← Ngang: ${String.format(Locale.US, "%.2f", spec.widthMeters)} m →", bx + drawW / 2f, by - 5f, dimPaint)
            canvas.drawText("Cao: ${String.format(Locale.US, "%.2f", spec.heightMeters)} m", bx + drawW + 26f, by + drawH / 2f, dimPaint)

            y += previewBoxH + 14f

            // Quotation Table
            canvas.drawText("2. BẢNG BÁO GIÁ CHI TIẾT & THÔNG SỐ m² (VNĐ)", left, y, sectionPaint)
            y += 7f

            val tblHead = Paint().apply { color = Color.rgb(30, 41, 59); style = Paint.Style.FILL }
            val whiteCol = Paint().apply { isAntiAlias = true; color = Color.WHITE; textSize = 7.8f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
            canvas.drawRect(left, y, right, y + 18f, tblHead)

            val cStt = left + 14f
            val cName = left + 30f
            val cUnit = 322f
            val cQty = 374f
            val cPrice = 454f
            val cTotal = right - 8f

            whiteCol.textAlign = Paint.Align.CENTER
            canvas.drawText("STT", cStt, y + 12f, whiteCol)
            whiteCol.textAlign = Paint.Align.LEFT
            canvas.drawText("Hạng Mục & Quy Cách Thiết Kế", cName, y + 12f, whiteCol)
            whiteCol.textAlign = Paint.Align.CENTER
            canvas.drawText("ĐVT", cUnit, y + 12f, whiteCol)
            whiteCol.textAlign = Paint.Align.RIGHT
            canvas.drawText("SL / m²", cQty, y + 12f, whiteCol)
            canvas.drawText("Đơn Giá", cPrice, y + 12f, whiteCol)
            canvas.drawText("Thành Tiền", cTotal, y + 12f, whiteCol)
            y += 18f

            val zebra = Paint().apply { color = Color.rgb(241, 245, 249); style = Paint.Style.FILL }
            val rowLine = Paint().apply { color = Color.rgb(226, 232, 240); strokeWidth = 0.7f }
            val rightBody = Paint(bodyPaint).apply { textAlign = Paint.Align.RIGHT }
            val rightBold = Paint(boldPaint).apply { textAlign = Paint.Align.RIGHT }
            val centerBody = Paint(bodyPaint).apply { textAlign = Paint.Align.CENTER }

            calc.lines.take(8).forEachIndexed { idx, line ->
                val rowH = 23f
                if (idx % 2 == 1) canvas.drawRect(left, y, right, y + rowH, zebra)
                canvas.drawLine(left, y + rowH, right, y + rowH, rowLine)
                canvas.drawText("${idx + 1}", cStt, y + 12f, centerBody)
                canvas.drawText(line.name.take(46), cName, y + 10.5f, boldPaint)
                canvas.drawText(line.spec.take(54), cName, y + 20f, mutedPaint)
                canvas.drawText(line.unit, cUnit, y + 13f, centerBody)
                canvas.drawText(String.format(Locale.US, "%.2f", line.quantity), cQty, y + 13f, rightBody)
                canvas.drawText(Formatters.formatVnd(line.unitPrice), cPrice, y + 13f, rightBody)
                canvas.drawText(Formatters.formatVnd(line.lineTotal), cTotal, y + 13f, rightBold)
                y += rowH
            }

            y += 12f

            // Totals & Terms
            val boxH = 78f
            val sumLeft = right - 245f
            canvas.drawRoundRect(RectF(left, y, sumLeft - 10f, y + boxH), 6f, 6f, infoFill)
            canvas.drawRoundRect(RectF(left, y, sumLeft - 10f, y + boxH), 6f, 6f, borderPaint)
            canvas.drawText("THÔNG SỐ VẬT TƯ & BẢO HÀNH", left + 8f, y + 14f, boldPaint)
            canvas.drawText("• ${calc.materialPlanSummary.take(58)}", left + 8f, y + 28f, mutedPaint)
            canvas.drawText("• Bảo hành kỹ thuật & độ bền màu: ${spec.warrantyMonths} tháng.", left + 8f, y + 41f, mutedPaint)
            canvas.drawText("• Đơn giá trung bình: ${Formatters.formatVnd(calc.effectiveRatePerM2)} / m²", left + 8f, y + 54f, mutedPaint)
            canvas.drawText("• Cam kết đúng chuẩn độ dày vật tư & tiến độ thi công.", left + 8f, y + 67f, mutedPaint)

            val greenFill = Paint().apply { color = Color.rgb(240, 253, 244); style = Paint.Style.FILL }
            val greenStroke = Paint().apply { color = Color.rgb(134, 239, 172); style = Paint.Style.STROKE; strokeWidth = 1f }
            val greenTotal = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(5, 150, 105)
                textSize = 11f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawRoundRect(RectF(sumLeft, y, right, y + boxH), 6f, 6f, greenFill)
            canvas.drawRoundRect(RectF(sumLeft, y, right, y + boxH), 6f, 6f, greenStroke)

            var sy = y + 15f
            canvas.drawText("Tạm tính:", sumLeft + 10f, sy, bodyPaint)
            canvas.drawText(Formatters.formatVnd(calc.subtotal), right - 10f, sy, rightBody)
            sy += 13f
            if (calc.discountAmount > 0.0) {
                canvas.drawText("Chiết khấu (${spec.discountPercent.toInt()}%):", sumLeft + 10f, sy, bodyPaint)
                canvas.drawText("-${Formatters.formatVnd(calc.discountAmount)}", right - 10f, sy, rightBody)
                sy += 13f
            }
            if (calc.vatAmount > 0.0) {
                canvas.drawText("VAT (${spec.vatPercent.toInt()}%):", sumLeft + 10f, sy, bodyPaint)
                canvas.drawText("+${Formatters.formatVnd(calc.vatAmount)}", right - 10f, sy, rightBody)
                sy += 13f
            }
            canvas.drawLine(sumLeft + 10f, y + 48f, right - 10f, y + 48f, rowLine)
            canvas.drawText("TỔNG BÁO GIÁ:", sumLeft + 10f, y + 64f, boldPaint)
            canvas.drawText(Formatters.formatVnd(calc.grandTotal), right - 10f, y + 64f, greenTotal)

            val footerPaint = Paint(mutedPaint).apply { textAlign = Paint.Align.CENTER }
            canvas.drawText("${spec.companyName} • Bản vẽ thiết kế & báo giá chuyên nghiệp", (left + right) / 2f, 822f, footerPaint)

            pdf.finishPage(page)
            FileOutputStream(file).use { pdf.writeTo(it) }
            pdf.close()
        } catch (_: Throwable) {
            val stream = "BT /F1 11 Tf 48 780 Td (BAO GIA ${spec.family.name} - ${spec.brandText}) Tj 0 -18 Td (Tong: ${Formatters.formatVnd(calc.grandTotal)}) Tj ET"
            val len = stream.toByteArray(Charsets.US_ASCII).size
            val fallback = "%PDF-1.4\n1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>endobj\n4 0 obj<< /Length $len >>stream\n$stream\nendstream\nendobj\n5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\nxref\n0 6\n0000000000 65535 f \ntrailer<< /Size 6 /Root 1 0 R >>\nstartxref\n320\n%%EOF"
            file.writeBytes(fallback.toByteArray(Charsets.US_ASCII))
        }
        return file
    }

    fun shareBoardQuotePdf(
        context: Context,
        spec: BoardDesignSpec,
        calc: BoardQuoteCalculation
    ) {
        val pdfFile = generateBoardQuotePdf(context, spec, calc)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Báo Giá ${spec.family.shortTitle} - ${spec.brandText}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Gửi Báo Giá PDF Qua"))
    }

    fun buildBoardZaloQuote(spec: BoardDesignSpec, calc: BoardQuoteCalculation): String = buildString {
        appendLine("📋 BÁO GIÁ ${spec.family.fullTitle.uppercase(Locale.getDefault())}")
        appendLine("🏢 ${spec.companyName} | Hotline: ${spec.companyPhone}")
        appendLine("👤 Kính gửi: ${spec.clientName}")
        appendLine("🏷️ Nội dung biển: ${spec.brandText.ifBlank { spec.family.shortTitle }}")
        appendLine("📐 Kích thước: ${String.format(Locale.US, "%.2f", spec.widthMeters)}m x ${String.format(Locale.US, "%.2f", spec.heightMeters)}m (SL: ${spec.quantity})")
        appendLine("📏 Tổng m²: ${String.format(Locale.US, "%.2f", calc.totalRawArea)} m² (m² tính giá: ${String.format(Locale.US, "%.2f", calc.totalBillableArea)} m²)")
        appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        calc.lines.forEachIndexed { idx, line ->
            appendLine("${idx + 1}. ${line.name}")
            appendLine("   ${String.format(Locale.US, "%.2f", line.quantity)} ${line.unit} x ${Formatters.formatVnd(line.unitPrice)} = ${Formatters.formatVnd(line.lineTotal)}")
        }
        appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        appendLine("• Vật tư dự kiến: ${calc.rollOrSheetHint}")
        appendLine("• Tạm tính: ${Formatters.formatVnd(calc.subtotal)}")
        if (calc.discountAmount > 0.0) {
            appendLine("• Chiết khấu (${spec.discountPercent.toInt()}%): -${Formatters.formatVnd(calc.discountAmount)}")
        }
        if (calc.vatAmount > 0.0) {
            appendLine("• Thuế VAT (${spec.vatPercent.toInt()}%): +${Formatters.formatVnd(calc.vatAmount)}")
        }
        appendLine("👉 TỔNG THANH TOÁN: ${Formatters.formatVnd(calc.grandTotal)}")
        appendLine("🛡️ Bảo hành: ${spec.warrantyMonths} tháng")
    }
}
