package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.model.Design3dPromptConfig
import com.example.model.Design3dQuoteLine
import com.example.model.Design3dSpecResult
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AspectRatio
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImageConfig
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImageSize
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

private const val TAG = "GeminiDesign3dService"
private const val SPEC_MODEL_NAME = "gemini-3.8-flash"
private const val RENDER_MODEL_NAME = "gemini-nano-banana-2.1"
private const val REQUEST_TIMEOUT_MS = 60_000L

class GeminiDesign3dService {

    companion object {
        fun scaleBitmapForGemini(bitmap: Bitmap, maxDimension: Int = 1280): Bitmap {
            val w = bitmap.width
            val h = bitmap.height
            if (w <= maxDimension && h <= maxDimension) return bitmap
            val scale = maxDimension.toFloat() / max(w, h).toFloat()
            val targetW = (w * scale).roundToInt().coerceAtLeast(1)
            val targetH = (h * scale).roundToInt().coerceAtLeast(1)
            return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
        }

        fun buildDesign3dPrompt(
            config: Design3dPromptConfig,
            hasReferenceImage: Boolean
        ): String {
            val area = (config.widthM * config.heightM).coerceAtLeast(0.0)
            return buildString {
                appendLine("Bạn là Kiến Trúc Sư Thiết Kế Biển Hiệu 3D & Giám Đốc Kỹ Thuật Xưởng Quảng Cáo Đức Anh Ad PRO tại Việt Nam.")
                if (hasReferenceImage) {
                    appendLine("Người dùng ĐÃ ĐÍNH KÈM ẢNH HIỆN TRẠNG MẶT TIỀN / MẪU THAM CHIẾU. Hãy phân tích kỹ kết cấu kiến trúc, màu tường, vỉa hè, đường dây/mái hiên, tỷ lệ mặt tiền trong ảnh để thiết kế phương án 3D phù hợp thực tế nhất.")
                } else {
                    appendLine("Người dùng yêu cầu DỰNG MỚI PHƯƠNG ÁN 3D THEO THÔNG SỐ KHÁCH HÀNG dưới đây.")
                }
                appendLine()
                appendLine("=== THÔNG SỐ YÊU CẦU KHÁCH HÀNG ===")
                appendLine("- Tên thương hiệu / Nội dung chính trên biển: ${config.brandText.ifBlank { "Thương hiệu khách hàng" }}")
                appendLine("- Slogan / Dòng phụ / SĐT: ${config.slogan.ifBlank { "Không có" }}")
                appendLine("- Ngành nghề kinh doanh: ${config.businessType.ifBlank { "Cửa hàng / Showroom thương mại" }}")
                appendLine("- Kích thước dự kiến: Ngang ${String.format(Locale.US, "%.2f", config.widthM)}m x Cao ${String.format(Locale.US, "%.2f", config.heightM)}m (Diện tích: ${String.format(Locale.US, "%.2f", area)} m²)")
                appendLine("- Phong cách & Kết cấu mong muốn: ${config.style.fullName}")
                appendLine("- Bảng màu chủ đạo: ${config.primaryColors}")
                appendLine("- Chế độ ánh sáng: ${config.lightingMode.label}")
                appendLine("- Ghi chú thêm: ${config.extraNotes.ifBlank { "Không có" }}")
                appendLine()
                appendLine("Yêu cầu đầu ra JSON chính xác:")
                appendLine("1. `conceptTitle`: Tên phương án thiết kế 3D chuyên nghiệp, thu hút khách hàng.")
                appendLine("2. `facadeAnalysis`: Phân tích mặt tiền / bố cục tổng thể (tỷ lệ vàng chữ so với nền, vị trí đặt logo, điểm nhìn từ xa).")
                appendLine("3. `visual3dSpec`: Quy cách dựng 3D chi tiết (độ dày chân nổi cm, quy cách Alu/Lam sóng/Hộp đèn, font chữ, mã màu gợi ý).")
                appendLine("4. `lightingPlan`: Phương án chiếu sáng LED ban ngày & ban đêm (LED hắt chân hay sáng mặt, nhiệt độ màu 3000K/6500K, nguồn 12V chống nước + Timer tự động).")
                appendLine("5. `installationMethod`: Giải pháp khung thép mạ kẽm, gia cố an toàn và thời gian thi công.")
                appendLine("6. `estimatedDays`: Số ngày hoàn thiện cả gia công + lắp đặt (số nguyên từ 2 đến 14).")
                appendLine("7. `renderPromptEn`: Một đoạn prompt tiếng Anh chi tiết (~60-90 từ) mô tả phối cảnh kiến trúc 3D thực tế để đưa vào mô hình tạo ảnh AI dựng lại chính xác biển hiệu '${config.brandText.ifBlank { "LUXURY STORE" }}' theo phong cách ${config.style.englishRenderHint} và ánh sáng ${config.lightingMode.englishHint}. Nếu có ảnh tham chiếu, mô tả giữ nguyên cấu trúc ngôi nhà và chỉ nâng cấp mặt tiền biển hiệu 3D.")
                appendLine("8. `suggestedLines`: Từ 4 đến 7 hạng mục báo giá chi tiết chuẩn thị trường Việt Nam để gửi thẳng cho khách hàng (Mặt dựng nền, Khung thép gia cố, Bộ chữ nổi 3D, Hệ thống LED + nguồn 12V, Viền/đèn rọi, Nhân công lắp đặt hoàn thiện) với `quantity` và `unitPrice` (VNĐ) sát diện tích ${String.format(Locale.US, "%.2f", area)} m².")
            }
        }

        fun buildRenderInstruction(
            renderPromptEn: String,
            config: Design3dPromptConfig,
            hasReferenceImage: Boolean
        ): String {
            val brand = config.brandText.ifBlank { "BRAND STUDIO" }
            val sloganPart = if (config.slogan.isNotBlank()) " with smaller clean subtext '${config.slogan}'" else ""
            val baseSpec = renderPromptEn.ifBlank {
                "Photorealistic architectural 3D storefront signage mockup featuring ${config.style.englishRenderHint}, crisp 3D signage text '$brand'$sloganPart, color palette ${config.primaryColors}, ${config.lightingMode.englishHint}, ultra-detailed commercial architecture photography, sharp typography, 8k."
            }
            return if (hasReferenceImage) {
                "Edit the storefront facade in this reference photo to install a custom architectural 3D shopfront signboard displaying '$brand'$sloganPart. Keep the surrounding building structure, street perspective, and neighboring elements realistic and consistent, replacing only the old/empty signage fascia with: $baseSpec. Ensure clean 3D depth, realistic shadows, and accurate lighting."
            } else {
                "Architectural 3D exterior visualization photography of a modern commercial storefront featuring a ${String.format(Locale.US, "%.1f", config.widthM)}m x ${String.format(Locale.US, "%.1f", config.heightM)}m 3D signage board displaying '$brand'$sloganPart. $baseSpec. Clean street-level perspective, realistic materials, no watermarks."
            }
        }

        fun parseDesign3dSpecJson(jsonString: String): Design3dSpecResult {
            val root = JSONObject(jsonString)
            val conceptTitle = root.optString("conceptTitle", "Phương Án Thiết Kế Biển Hiệu 3D").trim()
            val facadeAnalysis = root.optString("facadeAnalysis", "").trim()
            val visual3dSpec = root.optString("visual3dSpec", "").trim()
            val lightingPlan = root.optString("lightingPlan", "").trim()
            val installationMethod = root.optString("installationMethod", "").trim()
            val estimatedDays = root.optInt("estimatedDays", 5).coerceIn(1, 30)
            val renderPromptEn = root.optString("renderPromptEn", "").trim()

            val linesArray = root.optJSONArray("suggestedLines")
                ?: throw IllegalStateException("Phản hồi Gemini thiếu danh sách hạng mục báo giá (suggestedLines).")

            val lines = (0 until linesArray.length()).mapNotNull { idx ->
                val obj = linesArray.optJSONObject(idx) ?: return@mapNotNull null
                val name = obj.optString("name", "").trim()
                if (name.isEmpty()) return@mapNotNull null
                Design3dQuoteLine(
                    id = System.nanoTime() + idx + 1L,
                    name = name,
                    spec = obj.optString("spec", "Theo tiêu chuẩn xưởng Đức Anh PRO").ifBlank { "Theo tiêu chuẩn xưởng" },
                    unit = obj.optString("unit", "m²").ifBlank { "m²" },
                    quantity = obj.optDouble("quantity", 1.0).coerceAtLeast(0.1),
                    unitPrice = obj.optDouble("unitPrice", 0.0).coerceAtLeast(0.0)
                )
            }

            if (lines.isEmpty()) {
                throw IllegalStateException("Gemini không trả về hạng mục báo giá nào.")
            }

            return Design3dSpecResult(
                conceptTitle = conceptTitle.ifBlank { "Phương Án Thiết Kế Biển Hiệu 3D" },
                facadeAnalysis = facadeAnalysis.ifBlank { "Bố cục cân đối mặt tiền, ưu tiên nhận diện thương hiệu ở tầm nhìn 15-30m." },
                visual3dSpec = visual3dSpec.ifBlank { "Nền Alu/khung thép kết hợp chữ nổi 3D uốn chân chuẩn tỷ lệ." },
                lightingPlan = lightingPlan.ifBlank { "Hệ thống LED 12V chống nước IP68, nguồn MeanWell/CL và Timer bật tắt tự động." },
                installationMethod = installationMethod.ifBlank { "Khung sắt hộp mạ kẽm gia cố bản mã chắc chắn, xử lý silicone chống thấm." },
                estimatedDays = estimatedDays,
                renderPromptEn = renderPromptEn,
                suggestedLines = lines
            )
        }
    }

    private val design3dSchema = Schema.obj(
        properties = mapOf(
            "conceptTitle" to Schema.string(description = "Tên phương án thiết kế 3D chuyên nghiệp bằng tiếng Việt"),
            "facadeAnalysis" to Schema.string(description = "Phân tích hiện trạng mặt tiền & tỷ lệ bố cục chữ nổi"),
            "visual3dSpec" to Schema.string(description = "Quy cách dựng 3D: vật liệu mặt, chân chữ dày bao nhiêu cm, màu sắc"),
            "lightingPlan" to Schema.string(description = "Phương án chiếu sáng LED ngày/đêm, nhiệt độ màu, nguồn 12V"),
            "installationMethod" to Schema.string(description = "Giải pháp kết cấu khung sắt, liên kết tường và chống thấm"),
            "estimatedDays" to Schema.integer(description = "Số ngày dự kiến sản xuất và lắp đặt hoàn thiện"),
            "renderPromptEn" to Schema.string(description = "Prompt tiếng Anh chi tiết mô tả kiến trúc 3D của biển hiệu để sinh ảnh mockup"),
            "suggestedLines" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "name" to Schema.string(description = "Tên hạng mục báo giá cho khách hàng"),
                        "spec" to Schema.string(description = "Quy cách vật tư chi tiết đi kèm hạng mục"),
                        "unit" to Schema.string(description = "Đơn vị tính: m², Bộ, Mét dài, Trọn gói"),
                        "quantity" to Schema.double(description = "Khối lượng dự kiến tính theo kích thước khách hàng"),
                        "unitPrice" to Schema.double(description = "Đơn giá bán cho khách hàng tính bằng VNĐ")
                    )
                ),
                description = "Danh sách 4-7 hạng mục báo giá hoàn thiện để gửi khách hàng"
            )
        )
    )

    private val specModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = SPEC_MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = design3dSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private val renderModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = RENDER_MODEL_NAME,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
                imageConfig = ImageConfig.builder()
                    .setAspectRatio(AspectRatio.LANDSCAPE_16x9)
                    .setImageSize(ImageSize.SIZE_1K)
                    .build()
            }
        )
    }

    suspend fun analyzeAndDesign3dSpec(
        config: Design3dPromptConfig,
        referenceBitmap: Bitmap?
    ): Design3dSpecResult = withContext(Dispatchers.IO) {
        val prompt = buildDesign3dPrompt(config, hasReferenceImage = referenceBitmap != null)
        val scaledBitmap = referenceBitmap?.let { scaleBitmapForGemini(it) }

        val response = withTimeout(REQUEST_TIMEOUT_MS) {
            if (scaledBitmap != null) {
                specModel.generateContent(
                    content {
                        image(scaledBitmap)
                        text(prompt)
                    }
                )
            } else {
                specModel.generateContent(prompt)
            }
        }

        val jsonText = response.text?.trim()
            ?: throw IllegalStateException("Gemini 3.8 Flash không trả về phương án 3D.")

        parseDesign3dSpecJson(jsonText)
    }

    suspend fun generateOrEdit3dMockup(
        renderPromptEn: String,
        config: Design3dPromptConfig,
        referenceBitmap: Bitmap?
    ): Bitmap = withContext(Dispatchers.IO) {
        val hasRef = referenceBitmap != null
        val instruction = buildRenderInstruction(renderPromptEn, config, hasReferenceImage = hasRef)
        val scaledRef = referenceBitmap?.let { scaleBitmapForGemini(it) }

        try {
            val response = withTimeout(REQUEST_TIMEOUT_MS) {
                if (scaledRef != null) {
                    renderModel.generateContent(
                        content {
                            image(scaledRef)
                            text(instruction)
                        }
                    )
                } else {
                    renderModel.generateContent(instruction)
                }
            }

            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            imagePart?.image
                ?: throw IllegalStateException("Mô hình AI không trả về hình ảnh 3D (có thể cần bật gói Firebase Blaze hoặc thử lại mô tả khác).")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "3D image rendering failed", e)
            throw e
        }
    }
}
