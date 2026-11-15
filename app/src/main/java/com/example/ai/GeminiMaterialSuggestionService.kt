package com.example.ai

import android.util.Log
import com.example.model.AiMaterialSuggestionResult
import com.example.model.AiSuggestedMaterial
import com.example.model.ChatMessage
import com.example.model.EstimationInput
import com.example.model.MaterialItem
import com.example.util.Formatters
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.Locale

private const val TAG = "GeminiMaterialService"
private const val MODEL_NAME = "gemini-3.8-flash"
private const val FAST_CHAT_MODEL_NAME = "gemini-3.5-flash-lite"
private const val REQUEST_TIMEOUT_MS = 60_000L
private const val FIRST_TOKEN_WATCHDOG_MS = 4_500L
private const val FAST_FALLBACK_TIMEOUT_MS = 25_000L

class GeminiMaterialSuggestionService {

    companion object {
        val CATALOG_CATEGORIES = listOf(
            "Mica",
            "Alu (Aluminum)",
            "PVC / Formex",
            "Bạt In",
            "Decal",
            "Inox & Kim Loại",
            "Sắt Thép & Khung",
            "LED & Nguồn Điện",
            "Keo & Phụ Kiện"
        )

        fun buildMaterialSuggestionPrompt(
            input: EstimationInput,
            advertisingDescription: String,
            existingCatalog: List<MaterialItem>
        ): String {
            val netArea = (input.width * input.height * input.quantity).coerceAtLeast(0.0)
            val grossArea = netArea * (1.0 + input.wastagePercent / 100.0)
            val perimeter = 2.0 * (input.width + input.height) * input.quantity

            val catalogReference = existingCatalog
                .take(16)
                .joinToString("\n") { item ->
                    "- [${item.category}] ${item.brand} | ${item.spec} | Đơn vị: ${item.unit} | Giá xưởng: ${Formatters.formatVnd(item.price)}"
                }

            return buildString {
                appendLine("Bạn là Kỹ sư Trưởng Xưởng Quảng Cáo Đức Anh Ad PRO tại Việt Nam, chuyên bóc tách vật tư biển bảng quảng cáo thực tế.")
                appendLine("Nhiệm vụ: Phân tích dữ liệu biểu mẫu dự toán dưới đây kết hợp mô tả ngành quảng cáo của người dùng để đề xuất danh sách vật tư đầy đủ, chính xác, phù hợp thi công thực tế tại Việt Nam.")
                appendLine()
                appendLine("=== DỮ LIỆU TỪ BIỂU MẪU DỰ TOÁN ===")
                appendLine("- Tên hạng mục / công trình: ${input.itemName.ifBlank { "Biển hiệu quảng cáo" }}")
                appendLine("- Nhóm biển hiệu: ${input.category.displayName}")
                appendLine("- Kích thước mỗi biển: Dài ${String.format(Locale.US, "%.2f", input.width)}m x Rộng ${String.format(Locale.US, "%.2f", input.height)}m")
                appendLine("- Số lượng: ${input.quantity} bộ/biển")
                appendLine("- Diện tích tịnh: ${String.format(Locale.US, "%.2f", netArea)} m² | Diện tích tính hao hụt (${input.wastagePercent}%): ${String.format(Locale.US, "%.2f", grossArea)} m² | Chu vi: ${String.format(Locale.US, "%.2f", perimeter)} m")
                appendLine("- Vật tư chính đang chọn trên form: ${input.selectedMaterialName.ifBlank { "Chưa chọn cụ thể" }}")
                appendLine("- Khách hàng: ${input.clientName.ifBlank { "Khách lẻ / Doanh nghiệp" }}")
                appendLine("- Địa điểm thi công: ${input.projectLocation.ifBlank { "Chưa nêu rõ" }}")
                appendLine("- Độ phức tạp thi công: ${input.laborComplexity.displayName}")
                appendLine("- Ghi chú hiện trường: ${input.projectNotes.ifBlank { "Không có" }}")
                appendLine("- Tùy chọn hệ thống đi kèm trên form:")
                appendLine("  * Khung sắt hộp mạ kẽm: ${if (input.includeIronFrame) "Có" else "Không"}")
                appendLine("  * Hệ thống LED & nguồn 12V: ${if (input.includeLighting) "Có" else "Không"}")
                appendLine("  * Nẹp V nhôm/inox bo viền: ${if (input.includeEdgeTrim) "Có" else "Không"}")
                appendLine("  * Rơ-le hẹn giờ Timer KG316T: ${if (input.includeTimerRelay) "Có" else "Không"}")
                appendLine()
                appendLine("=== MÔ TẢ NGÀNH QUẢNG CÁO & YÊU CẦU KHÁCH HÀNG ===")
                appendLine(advertisingDescription.trim())
                appendLine()
                if (catalogReference.isNotBlank()) {
                    appendLine("=== THAM CHIẾU BẢNG GIÁ PHÔI XƯỞNG HIỆN CÓ ===")
                    appendLine(catalogReference)
                    appendLine()
                }
                appendLine("Yêu cầu đầu ra:")
                appendLine("1. Trả về từ 5 đến 8 vật tư thiết yếu nhất theo đúng quy chuẩn thợ quảng cáo Việt Nam (phôi mặt biển, khung xương thép hộp, chữ nổi/LED/nguồn 12V nếu có chiếu sáng, nẹp V bo viền, keo/vít/phụ kiện lắp đặt).")
                appendLine("2. Tính toán `estimatedQuantity` sát với diện tích ${String.format(Locale.US, "%.2f", grossArea)} m² và chu vi ${String.format(Locale.US, "%.2f", perimeter)} m (tấm tiêu chuẩn 1.22x2.44m = 2.9768 m², thanh sắt dài 6m).")
                appendLine("3. Đưa ra `estimatedUnitPrice` thực tế bằng VNĐ (số nguyên dương).")
                appendLine("4. Đánh dấu `isPrimaryMaterial = true` cho đúng 1 vật tư mặt biển chính quan trọng nhất.")
                appendLine("5. Sử dụng tiếng Việt chuyên ngành rõ ràng, dễ hiểu cho chủ xưởng và thợ.")
            }
        }

        fun parseSuggestionResponseJson(jsonString: String): AiMaterialSuggestionResult {
            val root = JSONObject(jsonString)
            val summary = root.optString("engineeringSummary", "").trim()
            val notesArray = root.optJSONArray("constructionNotes")
            val notes = if (notesArray != null) {
                (0 until notesArray.length())
                    .mapNotNull { idx -> notesArray.optString(idx, "").trim().takeIf { it.isNotEmpty() } }
            } else {
                emptyList()
            }

            val materialsArray = root.optJSONArray("suggestedMaterials")
                ?: throw IllegalStateException("Phản hồi AI thiếu trường suggestedMaterials.")

            val materials = (0 until materialsArray.length()).mapNotNull { index ->
                val obj = materialsArray.optJSONObject(index) ?: return@mapNotNull null
                val name = obj.optString("name", "").trim()
                if (name.isEmpty()) return@mapNotNull null

                AiSuggestedMaterial(
                    id = "ai_${index + 1}_${name.hashCode().toUInt()}",
                    name = name,
                    category = obj.optString("category", "Keo & Phụ Kiện").ifBlank { "Keo & Phụ Kiện" },
                    spec = obj.optString("spec", "Quy cách tiêu chuẩn xưởng").ifBlank { "Quy cách tiêu chuẩn xưởng" },
                    unit = obj.optString("unit", "Cái").ifBlank { "Cái" },
                    estimatedQuantity = obj.optDouble("estimatedQuantity", 1.0).coerceAtLeast(0.1),
                    estimatedUnitPrice = obj.optDouble("estimatedUnitPrice", 0.0).coerceAtLeast(0.0),
                    reason = obj.optString("reason", "").trim(),
                    isPrimaryMaterial = obj.optBoolean("isPrimaryMaterial", index == 0)
                )
            }

            if (materials.isEmpty()) {
                throw IllegalStateException("Gemini 3.8 Flash không trả về vật tư gợi ý nào.")
            }

            return AiMaterialSuggestionResult(
                engineeringSummary = summary.ifBlank {
                    "Phương án vật tư đề xuất bởi Gemini 3.8 Flash dựa trên thông số biểu mẫu."
                },
                constructionNotes = notes,
                materials = materials
            )
        }

        /**
         * Keeps recent alternating (user -> model) non-error turns for multi-turn Gemini `startChat`.
         */
        fun sanitizeChatTurns(
            messages: List<ChatMessage>,
            maxPairs: Int = 4
        ): List<Pair<Boolean, String>> {
            val clean = messages
                .filter { !it.isError && it.text.isNotBlank() }

            val pairs = mutableListOf<Pair<Boolean, String>>()
            var i = 0
            while (i < clean.size - 1) {
                val current = clean[i]
                val next = clean[i + 1]
                if (current.isUser && !next.isUser) {
                    pairs.add(true to current.text.trim())
                    pairs.add(false to next.text.trim())
                    i += 2
                } else {
                    i++
                }
            }

            val maxMessages = maxPairs * 2
            return if (pairs.size > maxMessages) pairs.takeLast(maxMessages) else pairs
        }

        fun formatFriendlyAiError(throwable: Throwable): String {
            val raw = throwable.localizedMessage ?: throwable.message ?: ""
            return when {
                throwable is TimeoutCancellationException || raw.contains("quá thời gian", ignoreCase = true) ->
                    "Kết nối Gemini AI miễn phí bị quá thời gian chờ. Vui lòng kiểm tra mạng và bấm Thử lại."
                raw.contains("403") || raw.contains("AppCheck", ignoreCase = true) || raw.contains("App Check", ignoreCase = true) ->
                    "Lỗi xác thực App Check (HTTP 403): $raw"
                raw.contains("429") || raw.contains("quota", ignoreCase = true) || raw.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ->
                    "Hệ thống AI miễn phí đang quá tải lượt gọi tạm thời. Vui lòng đợi vài giây rồi bấm Thử lại."
                raw.contains("Unable to resolve host", ignoreCase = true) || raw.contains("UnknownHostException", ignoreCase = true) || raw.contains("timeout", ignoreCase = true) ->
                    "Không có kết nối Internet hoặc mạng không ổn định ($raw). Vui lòng kiểm tra Wi-Fi/4G và thử lại."
                raw.isNotBlank() ->
                    "Lỗi Gemini AI (Firebase AI Logic): $raw"
                else ->
                    "Không thể kết nối dịch vụ Gemini AI miễn phí. Vui lòng thử lại."
            }
        }
    }

    private val materialSuggestionSchema = Schema.obj(
        properties = mapOf(
            "engineeringSummary" to Schema.string(
                description = "Tóm tắt phương án kết cấu, lựa chọn phôi và lưu ý kỹ thuật tổng thể bằng tiếng Việt"
            ),
            "constructionNotes" to Schema.array(
                items = Schema.string(),
                description = "Danh sách 2-4 lưu ý thi công ngoài trời, gia cố gió bão, đấu nối điện/LED an toàn"
            ),
            "suggestedMaterials" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "name" to Schema.string(description = "Tên vật tư quảng cáo cụ thể, ví dụ: Tấm Alu Alcorest PVDF 3mm ngoài trời"),
                        "category" to Schema.enumeration(
                            values = CATALOG_CATEGORIES,
                            description = "Nhóm danh mục vật tư chuẩn của xưởng"
                        ),
                        "spec" to Schema.string(description = "Quy cách, kích thước chuẩn, độ dày hoặc công suất"),
                        "unit" to Schema.string(description = "Đơn vị tính như Tấm, m², Cây 6m, Con, Bộ, Tuýp"),
                        "estimatedQuantity" to Schema.double(description = "Số lượng ước tính phù hợp với kích thước trên biểu mẫu"),
                        "estimatedUnitPrice" to Schema.double(description = "Đơn giá tham khảo tại xưởng tính bằng VNĐ"),
                        "reason" to Schema.string(description = "Lý do chọn vật tư này cho mô tả ngành quảng cáo và hiện trường"),
                        "isPrimaryMaterial" to Schema.boolean(description = "true nếu là vật tư bề mặt chính của biển hiệu")
                    )
                ),
                description = "Danh sách 5-8 vật tư quảng cáo được đề xuất"
            )
        )
    )

    private val technicalSystemInstruction: Content by lazy {
        content {
            text(
                "Bạn là Trợ lý Kỹ thuật & Báo giá AI Đức Anh Ad PRO tại Việt Nam, chuyên ngành quảng cáo thực chiến: " +
                    "biển bạt (Hiflex/Đế xám/Không gân UV/3M/Hộp đèn), bảng Alu chữ nổi (Alcorest/Triều Chen + Mica/Inox/LED 12V), " +
                    "bảng Decal (Sữa/Trong/Lưới/Mờ kính/PP/Bế demi/Formex), tối ưu cắt tấm 1.22x2.44m & khổ cuộn, sửa lỗi máy in KTS (Maintop/PhotoPrint) và CNC/Laser. " +
                    "Quy tắc trả lời: Đi thẳng vào trọng tâm bằng gạch đầu dòng ngắn gọn, nêu rõ thông số/m²/công thức/đơn giá VNĐ tham khảo và mẹo thợ thực tế."
            )
        }
    }

    private val structuredModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = materialSuggestionSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private val chatModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                maxOutputTokens = 1800
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            },
            systemInstruction = technicalSystemInstruction
        )
    }

    // Ultra-fast free-tier model (billing not required) with minimal thinking latency for instant chat streaming.
    private val fastChatModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = FAST_CHAT_MODEL_NAME,
            generationConfig = generationConfig {
                maxOutputTokens = 1400
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.MINIMAL
                }
            },
            systemInstruction = technicalSystemInstruction
        )
    }

    fun warmUpChat() {
        fastChatModel
        chatModel
    }

    private fun buildHistoryContents(messages: List<ChatMessage>): List<Content> {
        return sanitizeChatTurns(messages).map { (isUser, text) ->
            content(role = if (isUser) "user" else "model") {
                text(text)
            }
        }
    }

    suspend fun suggestMaterials(
        input: EstimationInput,
        advertisingDescription: String,
        existingCatalog: List<MaterialItem>
    ): AiMaterialSuggestionResult = withContext(Dispatchers.IO) {
        val prompt = buildMaterialSuggestionPrompt(
            input = input,
            advertisingDescription = advertisingDescription,
            existingCatalog = existingCatalog
        )
        try {
            val response = withTimeout(REQUEST_TIMEOUT_MS) {
                structuredModel.generateContent(prompt)
            }
            val jsonText = response.text
            if (jsonText.isNullOrBlank()) {
                throw IllegalStateException("Gemini 3.8 Flash trả về phản hồi trống.")
            }
            parseSuggestionResponseJson(jsonText)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini 3.8 Flash request timed out", e)
            throw IllegalStateException("Yêu cầu Gemini 3.8 Flash quá thời gian 60 giây. Vui lòng thử lại.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to suggest materials via Gemini 3.8 Flash", e)
            throw e
        }
    }

    fun streamTechnicalAdvice(
        query: String,
        history: List<ChatMessage> = emptyList(),
        useTurboMode: Boolean = true
    ): Flow<String> = channelFlow {
        val accumulated = StringBuilder()
        val historyContents = buildHistoryContents(history.takeLast(6))
        val primaryModel = if (useTurboMode) fastChatModel else chatModel
        var firstTokenReceived = false
        var watchdogTriggered = false

        val streamJob = launch {
            try {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    val stream = if (historyContents.isEmpty()) {
                        primaryModel.generateContentStream(query)
                    } else {
                        primaryModel.startChat(history = historyContents).sendMessageStream(query)
                    }
                    stream.collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            firstTokenReceived = true
                            accumulated.append(chunkText)
                            send(accumulated.toString())
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                if (accumulated.isNotBlank()) {
                    Log.w(TAG, "Gemini chat stream reached timeout after partial output (${accumulated.length} chars)")
                    send(accumulated.toString())
                } else {
                    Log.w(TAG, "Gemini chat stream timed out before first chunk; will try fast unary fallback")
                }
            }
        }

        val watchdogJob = launch {
            delay(FIRST_TOKEN_WATCHDOG_MS)
            if (!firstTokenReceived && streamJob.isActive) {
                watchdogTriggered = true
                Log.w(
                    TAG,
                    "No stream chunk within ${FIRST_TOKEN_WATCHDOG_MS}ms; switching to fast unary $FAST_CHAT_MODEL_NAME"
                )
                streamJob.cancel(CancellationException("First token watchdog triggered"))
            }
        }

        try {
            streamJob.join()
        } finally {
            watchdogJob.cancel()
        }

        if (accumulated.isBlank()) {
            Log.i(
                TAG,
                "Falling back to fast unary Firebase AI generation ($FAST_CHAT_MODEL_NAME, watchdog=$watchdogTriggered)"
            )
            try {
                val response = withTimeout(FAST_FALLBACK_TIMEOUT_MS) {
                    if (historyContents.isEmpty()) {
                        fastChatModel.generateContent(query)
                    } else {
                        fastChatModel.startChat(history = historyContents).sendMessage(query)
                    }
                }
                val fallbackText = response.text
                if (!fallbackText.isNullOrBlank()) {
                    send(fallbackText)
                } else {
                    throw IllegalStateException("Gemini AI miễn phí trả về phản hồi trống.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Fast unary fallback also timed out", e)
                throw IllegalStateException(
                    "Phản hồi Gemini AI quá thời gian chờ. Vui lòng kiểm tra kết nối mạng và thử lại.",
                    e
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Fast unary fallback failed", e)
                throw e
            }
        }
    }.flowOn(Dispatchers.IO)
}
