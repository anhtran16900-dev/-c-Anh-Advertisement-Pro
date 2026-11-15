package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.Design3dPromptConfig
import com.example.model.Design3dQuoteLine
import com.example.model.Design3dQuoteSummary
import com.example.model.Design3dQuoteTerms
import com.example.model.Design3dSpecResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Design3dPdfExporter {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private val fileTimestampFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)

    fun generate3dQuotePdf(
        context: Context,
        config: Design3dPromptConfig,
        spec: Design3dSpecResult?,
        mockupBitmap: Bitmap?,
        lines: List<Design3dQuoteLine>,
        terms: Design3dQuoteTerms,
        summary: Design3dQuoteSummary
    ): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeBrand = config.brandText.ifBlank { "Thiet_Ke_3D" }
            .replace(Regex("[^a-zA-Z0-9_]"), "_")
            .take(26)
        val file = File(exportDir, "Bao_Gia_3D_${safeBrand}_${fileTimestampFormat.format(Date())}.pdf")

        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val left = 36f
            val right = 559f
            val contentW = right - left
            var y = 34f

            val navyPaint = Paint().apply {
                color = Color.rgb(15, 23, 42)
                style = Paint.Style.FILL
            }
            val headerTitlePaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = 13.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val headerSubPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(148, 163, 184)
                textSize = 8.5f
            }
            val badgePaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(34, 211, 238)
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }
            val sectionTitlePaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(15, 23, 42)
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(30, 41, 59)
                textSize = 8.2f
            }
            val boldBodyPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(15, 23, 42)
                textSize = 8.2f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val mutedPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(100, 116, 139)
                textSize = 7.5f
            }
            val greenBoldPaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(5, 150, 105)
                textSize = 11f
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            // 1. Top Banner
            canvas.drawRoundRect(RectF(left, y, right, y + 54f), 8f, 8f, navyPaint)
            canvas.drawText(terms.companyName.ifBlank { "ĐỨC ANH ADVERTISEMENT PRO" }, left + 14f, y + 21f, headerTitlePaint)
            canvas.drawText(
                "HỒ SƠ THIẾT KẾ 3D & BÁO GIÁ THI CÔNG • Hotline: ${terms.companyPhone}",
                left + 14f,
                y + 37f,
                headerSubPaint
            )
            canvas.drawText("Ngày: ${dateFormat.format(Date())}", right - 14f, y + 21f, badgePaint)
            canvas.drawText(
                "KT: ${String.format(Locale.US, "%.2f", config.widthM)}m x ${String.format(Locale.US, "%.2f", config.heightM)}m",
                right - 14f,
                y + 37f,
                headerSubPaint.apply { textAlign = Paint.Align.RIGHT }
            )
            headerSubPaint.textAlign = Paint.Align.LEFT
            y += 66f

            // 2. Client & Concept Metadata Box
            val infoBoxPaint = Paint().apply {
                color = Color.rgb(248, 250, 252)
                style = Paint.Style.FILL
            }
            val borderPaint = Paint().apply {
                color = Color.rgb(203, 213, 225)
                style = Paint.Style.STROKE
                strokeWidth = 0.9f
            }
            canvas.drawRoundRect(RectF(left, y, right, y + 48f), 6f, 6f, infoBoxPaint)
            canvas.drawRoundRect(RectF(left, y, right, y + 48f), 6f, 6f, borderPaint)

            val clientLine = buildString {
                append("Khách hàng: ${terms.clientName.ifBlank { "Quý Khách Hàng" }}")
                if (terms.clientPhone.isNotBlank()) append("  |  SĐT: ${terms.clientPhone}")
            }
            canvas.drawText(clientLine, left + 10f, y + 15f, boldBodyPaint)
            canvas.drawText(
                "Thương hiệu: ${config.brandText.ifBlank { "Biển hiệu 3D" }}  •  Phong cách: ${config.style.shortLabel}",
                left + 10f,
                y + 28f,
                bodyPaint
            )
            val addr = if (terms.projectAddress.isNotBlank()) "Địa điểm: ${terms.projectAddress}" else "Phương án: ${spec?.conceptTitle ?: "Thiết kế 3D chuyên nghiệp"}"
            canvas.drawText(addr.take(86), left + 10f, y + 41f, mutedPaint)
            y += 58f

            // 3. Optional 3D Mockup Preview Bitmap
            if (mockupBitmap != null) {
                canvas.drawText("1. PHỐI CẢNH THIẾT KẾ 3D ĐỀ XUẤT", left, y, sectionTitlePaint)
                y += 6f
                val maxImgH = 165f
                val aspect = mockupBitmap.width.toFloat() / mockupBitmap.height.coerceAtLeast(1).toFloat()
                var drawW = contentW
                var drawH = drawW / aspect
                if (drawH > maxImgH) {
                    drawH = maxImgH
                    drawW = drawH * aspect
                }
                val imgLeft = left + (contentW - drawW) / 2f
                val destRect = RectF(imgLeft, y, imgLeft + drawW, y + drawH)
                canvas.drawBitmap(mockupBitmap, null, destRect, null)
                canvas.drawRect(destRect, borderPaint)
                y += drawH + 12f
            }

            // 4. 3D Spec Highlights (Compact 2-3 lines)
            if (spec != null) {
                canvas.drawText(
                    if (mockupBitmap != null) "2. QUY CÁCH KỸ THUẬT 3D & CHIẾU SÁNG" else "1. QUY CÁCH KỸ THUẬT 3D & CHIẾU SÁNG",
                    left,
                    y,
                    sectionTitlePaint
                )
                y += 12f
                drawWrappedText(canvas, "• Tạo hình 3D: ${spec.visual3dSpec}", left, y, contentW, bodyPaint, maxLines = 2)
                y += 22f
                drawWrappedText(canvas, "• Chiếu sáng LED: ${spec.lightingPlan}", left, y, contentW, bodyPaint, maxLines = 2)
                y += 22f
                drawWrappedText(canvas, "• Kết cấu lắp đặt: ${spec.installationMethod}", left, y, contentW, mutedPaint, maxLines = 2)
                y += 24f
            }

            // 5. Quotation Table
            val tableSectionNum = when {
                mockupBitmap != null && spec != null -> "3."
                mockupBitmap != null || spec != null -> "2."
                else -> "1."
            }
            canvas.drawText("$tableSectionNum BẢNG BÁO GIÁ CHI TIẾT HẠNG MỤC (VNĐ)", left, y, sectionTitlePaint)
            y += 7f

            val headerPaint = Paint().apply { color = Color.rgb(30, 41, 59); style = Paint.Style.FILL }
            val whiteColPaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = 7.8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawRect(left, y, right, y + 18f, headerPaint)
            val cStt = left + 14f
            val cName = left + 30f
            val cUnit = 325f
            val cQty = 375f
            val cPrice = 455f
            val cTotal = right - 8f

            whiteColPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("STT", cStt, y + 12f, whiteColPaint)
            whiteColPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Hạng Mục & Quy Cách", cName, y + 12f, whiteColPaint)
            whiteColPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("ĐVT", cUnit, y + 12f, whiteColPaint)
            whiteColPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("SL", cQty, y + 12f, whiteColPaint)
            canvas.drawText("Đơn Giá", cPrice, y + 12f, whiteColPaint)
            canvas.drawText("Thành Tiền", cTotal, y + 12f, whiteColPaint)
            y += 18f

            val zebraPaint = Paint().apply { color = Color.rgb(241, 245, 249); style = Paint.Style.FILL }
            val linePaint = Paint().apply { color = Color.rgb(226, 232, 240); strokeWidth = 0.7f }
            val rightBody = Paint(bodyPaint).apply { textAlign = Paint.Align.RIGHT }
            val rightBold = Paint(boldBodyPaint).apply { textAlign = Paint.Align.RIGHT }
            val centerBody = Paint(bodyPaint).apply { textAlign = Paint.Align.CENTER }

            val maxRows = if (mockupBitmap != null) 8 else 12
            lines.take(maxRows).forEachIndexed { idx, line ->
                val rowH = 22f
                if (idx % 2 == 1) canvas.drawRect(left, y, right, y + rowH, zebraPaint)
                canvas.drawLine(left, y + rowH, right, y + rowH, linePaint)

                canvas.drawText("${idx + 1}", cStt, y + 11f, centerBody)
                canvas.drawText(fitText(line.name, boldBodyPaint, 265f), cName, y + 10f, boldBodyPaint)
                canvas.drawText(fitText(line.spec, mutedPaint, 265f), cName, y + 19f, mutedPaint)
                canvas.drawText(line.unit, cUnit, y + 13f, centerBody)
                canvas.drawText(String.format(Locale.US, "%.1f", line.quantity), cQty, y + 13f, rightBody)
                canvas.drawText(Formatters.formatVnd(line.unitPrice), cPrice, y + 13f, rightBody)
                canvas.drawText(Formatters.formatVnd(line.lineTotal), cTotal, y + 13f, rightBold)
                y += rowH
            }

            if (lines.size > maxRows) {
                canvas.drawText("... và ${lines.size - maxRows} hạng mục phụ trợ khác (xem chi tiết bản Zalo/text).", cName, y + 13f, mutedPaint)
                y += 18f
            }

            y += 10f

            // 6. Totals & Terms Side-by-Side
            val boxTop = y
            val termsWidth = 275f
            val sumLeft = left + termsWidth + 12f
            val boxH = 82f

            canvas.drawRoundRect(RectF(left, boxTop, left + termsWidth, boxTop + boxH), 6f, 6f, infoBoxPaint)
            canvas.drawRoundRect(RectF(left, boxTop, left + termsWidth, boxTop + boxH), 6f, 6f, borderPaint)
            canvas.drawText("ĐIỀU KHOẢN THI CÔNG & BẢO HÀNH", left + 8f, boxTop + 13f, boldBodyPaint)
            canvas.drawText("• Tiến độ hoàn thiện: ${terms.productionDays} ngày làm việc kể từ khi chốt duyệt 3D.", left + 8f, boxTop + 27f, mutedPaint)
            canvas.drawText("• Bảo hành kết cấu & LED: ${terms.warrantyMonths} tháng tận công trình.", left + 8f, boxTop + 39f, mutedPaint)
            canvas.drawText("• Tạm ứng sản xuất: ${terms.depositPercent}% (${Formatters.formatVnd(summary.depositAmount)})", left + 8f, boxTop + 51f, mutedPaint)
            drawWrappedText(canvas, "• Ghi chú: ${terms.extraNotes}", left + 8f, boxTop + 63f, termsWidth - 16f, mutedPaint, maxLines = 2)

            val sumFill = Paint().apply { color = Color.rgb(240, 253, 244); style = Paint.Style.FILL }
            val sumStroke = Paint().apply { color = Color.rgb(134, 239, 172); style = Paint.Style.STROKE; strokeWidth = 1f }
            canvas.drawRoundRect(RectF(sumLeft, boxTop, right, boxTop + boxH), 6f, 6f, sumFill)
            canvas.drawRoundRect(RectF(sumLeft, boxTop, right, boxTop + boxH), 6f, 6f, sumStroke)

            var sy = boxTop + 15f
            canvas.drawText("Tạm tính:", sumLeft + 10f, sy, bodyPaint)
            canvas.drawText(Formatters.formatVnd(summary.subtotal), right - 10f, sy, rightBody)
            sy += 13f
            if (summary.discountAmount > 0.0) {
                canvas.drawText("Chiết khấu (${terms.discountPercent.toInt()}%):", sumLeft + 10f, sy, bodyPaint)
                canvas.drawText("-${Formatters.formatVnd(summary.discountAmount)}", right - 10f, sy, rightBody)
                sy += 13f
            }
            if (summary.vatAmount > 0.0) {
                canvas.drawText("Thuế VAT (${terms.vatPercent.toInt()}%):", sumLeft + 10f, sy, bodyPaint)
                canvas.drawText("+${Formatters.formatVnd(summary.vatAmount)}", right - 10f, sy, rightBody)
                sy += 13f
            }
            canvas.drawLine(sumLeft + 10f, boxTop + 50f, right - 10f, boxTop + 50f, linePaint)
            canvas.drawText("TỔNG THANH TOÁN:", sumLeft + 10f, boxTop + 66f, boldBodyPaint)
            canvas.drawText(Formatters.formatVnd(summary.grandTotal), right - 10f, boxTop + 66f, greenBoldPaint)

            // Footer
            val footerCenter = Paint(mutedPaint).apply { textAlign = Paint.Align.CENTER }
            canvas.drawText(
                "Hồ sơ thiết kế 3D & dự toán lập bởi ${terms.companyName.ifBlank { "Đức Anh Ad PRO" }} • Trang 1/1",
                (left + right) / 2f,
                822f,
                footerCenter
            )

            pdfDocument.finishPage(page)
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            pdfDocument.close()
        } catch (_: Throwable) {
            val stream = "BT /F1 11 Tf 48 780 Td (BAO GIA THIET KE 3D - ${config.brandText}) Tj 0 -18 Td (Tong thanh toan: ${Formatters.formatVnd(summary.grandTotal)}) Tj ET"
            val len = stream.toByteArray(Charsets.US_ASCII).size
            val fallbackPdf = "%PDF-1.4\n1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n" +
                "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n" +
                "3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>endobj\n" +
                "4 0 obj<< /Length $len >>stream\n$stream\nendstream\nendobj\n" +
                "5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n" +
                "xref\n0 6\n0000000000 65535 f \ntrailer<< /Size 6 /Root 1 0 R >>\nstartxref\n320\n%%EOF"
            file.writeBytes(fallbackPdf.toByteArray(Charsets.US_ASCII))
        }
        return file
    }

    fun share3dQuotePdf(
        context: Context,
        config: Design3dPromptConfig,
        spec: Design3dSpecResult?,
        mockupBitmap: Bitmap?,
        lines: List<Design3dQuoteLine>,
        terms: Design3dQuoteTerms,
        summary: Design3dQuoteSummary
    ) {
        val pdfFile = generate3dQuotePdf(context, config, spec, mockupBitmap, lines, terms, summary)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Báo Giá & Thiết Kế 3D - ${config.brandText.ifBlank { "Biển Quảng Cáo" }}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Gửi Báo Giá 3D PDF Qua"))
    }

    fun buildDesign3dZaloQuote(
        config: Design3dPromptConfig,
        spec: Design3dSpecResult?,
        lines: List<Design3dQuoteLine>,
        terms: Design3dQuoteTerms,
        summary: Design3dQuoteSummary
    ): String = buildString {
        appendLine("✨ ĐỀ XUẤT THIẾT KẾ 3D & BÁO GIÁ THI CÔNG")
        appendLine("🏢 Đơn vị: ${terms.companyName} | Hotline: ${terms.companyPhone}")
        appendLine("👤 Kính gửi: ${terms.clientName}${if (terms.clientPhone.isNotBlank()) " (${terms.clientPhone})" else ""}")
        appendLine("🏷️ Thương hiệu biển: ${config.brandText.ifBlank { "Biển hiệu 3D" }}")
        appendLine("📐 Kích thước: ${String.format(Locale.US, "%.2f", config.widthM)}m x ${String.format(Locale.US, "%.2f", config.heightM)}m | Phong cách: ${config.style.shortLabel}")
        if (spec != null) {
            appendLine("🎨 Phương án: ${spec.conceptTitle}")
            appendLine("💡 Chiếu sáng: ${spec.lightingPlan}")
        }
        appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        lines.forEachIndexed { idx, line ->
            appendLine("${idx + 1}. ${line.name} (${line.spec})")
            appendLine("   ${String.format(Locale.US, "%.1f", line.quantity)} ${line.unit} x ${Formatters.formatVnd(line.unitPrice)} = ${Formatters.formatVnd(line.lineTotal)}")
        }
        appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        appendLine("• Tạm tính: ${Formatters.formatVnd(summary.subtotal)}")
        if (summary.discountAmount > 0.0) {
            appendLine("• Chiết khấu (${terms.discountPercent.toInt()}%): -${Formatters.formatVnd(summary.discountAmount)}")
        }
        if (summary.vatAmount > 0.0) {
            appendLine("• Thuế VAT (${terms.vatPercent.toInt()}%): +${Formatters.formatVnd(summary.vatAmount)}")
        }
        appendLine("👉 TỔNG THANH TOÁN: ${Formatters.formatVnd(summary.grandTotal)}")
        appendLine("⏳ Tiến độ: ${terms.productionDays} ngày | Bảo hành: ${terms.warrantyMonths} tháng")
        appendLine("💳 Tạm ứng ${terms.depositPercent}%: ${Formatters.formatVnd(summary.depositAmount)}")
    }

    private fun fitText(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var trimmed = text
        while (trimmed.length > 4 && paint.measureText("$trimmed...") > maxWidth) {
            trimmed = trimmed.dropLast(1)
        }
        return "$trimmed..."
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint,
        maxLines: Int = 2
    ) {
        val words = text.split(" ")
        var line = ""
        var curY = startY
        var count = 0
        for (w in words) {
            val test = if (line.isEmpty()) w else "$line $w"
            if (paint.measureText(test) > maxWidth) {
                canvas.drawText(line, x, curY, paint)
                count++
                if (count >= maxLines) return
                curY += paint.textSize + 2.5f
                line = w
            } else {
                line = test
            }
        }
        if (line.isNotEmpty() && count < maxLines) {
            canvas.drawText(line, x, curY, paint)
        }
    }
}
