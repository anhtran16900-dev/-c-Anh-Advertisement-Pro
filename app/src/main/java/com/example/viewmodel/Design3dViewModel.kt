package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiDesign3dService
import com.example.ai.GeminiMaterialSuggestionService
import com.example.model.Design3dPromptConfig
import com.example.model.Design3dQuoteLine
import com.example.model.Design3dQuoteSummary
import com.example.model.Design3dQuoteTerms
import com.example.model.Design3dRenderUiState
import com.example.model.Design3dSpecUiState
import com.example.model.Design3dStyle
import com.example.model.LightingMode3d
import com.example.model.ProjectDossierItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private const val TAG = "Design3dViewModel"

class Design3dViewModel(application: Application) : AndroidViewModel(application) {

    private val designService by lazy { GeminiDesign3dService() }

    private val _config = MutableStateFlow(
        Design3dPromptConfig(
            brandText = "COFFEE & TEA HOUSE",
            slogan = "Specialty Coffee • Hotline 0988.xxx.xxx",
            businessType = "Quán Cà Phê & Trà Sữa",
            widthM = 5.0,
            heightM = 1.6,
            style = Design3dStyle.ALU_INOX_LED,
            primaryColors = "Đen nhám - Vàng Gold - Trắng ấm",
            lightingMode = LightingMode3d.NIGHT_LUX
        )
    )
    val config: StateFlow<Design3dPromptConfig> = _config.asStateFlow()

    private val _referenceBitmap = MutableStateFlow<Bitmap?>(null)
    val referenceBitmap: StateFlow<Bitmap?> = _referenceBitmap.asStateFlow()

    private val _specUiState = MutableStateFlow<Design3dSpecUiState>(Design3dSpecUiState.Idle)
    val specUiState: StateFlow<Design3dSpecUiState> = _specUiState.asStateFlow()

    private val _renderUiState = MutableStateFlow<Design3dRenderUiState>(Design3dRenderUiState.Idle)
    val renderUiState: StateFlow<Design3dRenderUiState> = _renderUiState.asStateFlow()

    private val _quoteLines = MutableStateFlow<List<Design3dQuoteLine>>(buildInitialDefaultLines(5.0, 1.6))
    val quoteLines: StateFlow<List<Design3dQuoteLine>> = _quoteLines.asStateFlow()

    private val _quoteTerms = MutableStateFlow(Design3dQuoteTerms())
    val quoteTerms: StateFlow<Design3dQuoteTerms> = _quoteTerms.asStateFlow()

    val quoteSummary: StateFlow<Design3dQuoteSummary> = combine(
        _quoteLines,
        _quoteTerms
    ) { lines, terms ->
        Design3dQuoteSummary.calculate(lines, terms)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Design3dQuoteSummary.calculate(_quoteLines.value, _quoteTerms.value)
    )

    private val _studioNotice = MutableStateFlow<String?>(null)
    val studioNotice: StateFlow<String?> = _studioNotice.asStateFlow()

    fun clearNotice() {
        _studioNotice.value = null
    }

    fun updateConfig(transform: (Design3dPromptConfig) -> Design3dPromptConfig) {
        _config.value = transform(_config.value)
    }

    fun loadReferencePhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                val bmp = withContext(Dispatchers.IO) {
                    val resolver = getApplication<Application>().contentResolver
                    val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(resolver, uri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.isMutableRequired = false
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                    } else {
                        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                    }
                    raw?.let { GeminiDesign3dService.scaleBitmapForGemini(it, 1280) }
                }
                if (bmp != null) {
                    _referenceBitmap.value = bmp
                    _studioNotice.value = "Đã tải ảnh mặt tiền / mẫu tham chiếu thành công!"
                } else {
                    _studioNotice.value = "Không thể đọc tệp ảnh đã chọn."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode reference image", e)
                _studioNotice.value = "Lỗi tải ảnh: ${e.localizedMessage ?: "Không xác định"}"
            }
        }
    }

    fun clearReferencePhoto() {
        _referenceBitmap.value = null
        _studioNotice.value = "Đã gỡ ảnh tham chiếu (chế độ dựng 3D theo yêu cầu)"
    }

    fun analyzeAndDesign3dSpec() {
        val curr = _config.value
        if (curr.brandText.isBlank() && _referenceBitmap.value == null) {
            _specUiState.value = Design3dSpecUiState.Error(
                "Vui lòng nhập Tên thương hiệu / nội dung biển hiệu hoặc chọn 1 ảnh mặt tiền tham chiếu."
            )
            return
        }

        viewModelScope.launch {
            _specUiState.value = Design3dSpecUiState.Loading
            try {
                val result = designService.analyzeAndDesign3dSpec(
                    config = curr,
                    referenceBitmap = _referenceBitmap.value
                )
                _specUiState.value = Design3dSpecUiState.Success(result)
                _quoteLines.value = result.suggestedLines
                _quoteTerms.value = _quoteTerms.value.copy(productionDays = result.estimatedDays)
                _studioNotice.value = "Đã dựng phương án 3D & tự động điền ${result.suggestedLines.size} hạng mục báo giá!"
            } catch (e: TimeoutCancellationException) {
                _specUiState.value = Design3dSpecUiState.Error(
                    "Gemini 3.8 Flash phản hồi quá 60 giây. Vui lòng thử lại."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "3D spec design failed", e)
                _specUiState.value = Design3dSpecUiState.Error(
                    GeminiMaterialSuggestionService.formatFriendlyAiError(e)
                )
            }
        }
    }

    fun render3dMockup() {
        val curr = _config.value
        val existingSpec = (_specUiState.value as? Design3dSpecUiState.Success)?.data
        if (curr.brandText.isBlank() && _referenceBitmap.value == null && existingSpec == null) {
            _renderUiState.value = Design3dRenderUiState.Error(
                "Vui lòng nhập tên biển hiệu hoặc chọn ảnh tham chiếu trước khi tạo ảnh 3D."
            )
            return
        }

        viewModelScope.launch {
            _renderUiState.value = Design3dRenderUiState.Loading
            try {
                val bmp = designService.generateOrEdit3dMockup(
                    renderPromptEn = existingSpec?.renderPromptEn.orEmpty(),
                    config = curr,
                    referenceBitmap = _referenceBitmap.value
                )
                _renderUiState.value = Design3dRenderUiState.Success(bmp)
                _studioNotice.value = "Đã hoàn tất ảnh phối cảnh 3D!"
            } catch (e: TimeoutCancellationException) {
                _renderUiState.value = Design3dRenderUiState.Error(
                    "Tạo ảnh phối cảnh 3D quá 60 giây. Vui lòng kiểm tra mạng và thử lại."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "3D render failed", e)
                val raw = e.localizedMessage ?: e.message ?: "Không thể tạo ảnh phối cảnh 3D."
                val msg = if (raw.contains("billing", ignoreCase = true) ||
                    raw.contains("Blaze", ignoreCase = true) ||
                    raw.contains("403") ||
                    raw.contains("quota", ignoreCase = true)
                ) {
                    "Tạo ảnh AI (gemini-nano-banana-2.1) yêu cầu nâng cấp Firebase Blaze hoặc vượt giới hạn lượt gọi: $raw\n\nGợi ý: Bạn vẫn có thể dùng miễn phí 'Phân Tích & Dựng Phương Án 3D (Gemini 3.8 Flash)' và xuất báo giá PDF/Zalo chuyên nghiệp bên dưới."
                } else {
                    "Lỗi tạo ảnh 3D: $raw"
                }
                _renderUiState.value = Design3dRenderUiState.Error(msg)
            }
        }
    }

    fun addOrUpdateQuoteLine(line: Design3dQuoteLine) {
        val current = _quoteLines.value
        val exists = current.any { it.id == line.id }
        _quoteLines.value = if (exists) {
            current.map { if (it.id == line.id) line else it }
        } else {
            current + line
        }
        _studioNotice.value = if (exists) "Đã cập nhật '${line.name}'" else "Đã thêm '${line.name}' vào báo giá"
    }

    fun removeQuoteLine(id: Long) {
        _quoteLines.value = _quoteLines.value.filterNot { it.id == id }
    }

    fun updateQuoteTerms(terms: Design3dQuoteTerms) {
        _quoteTerms.value = terms
        _studioNotice.value = "Đã cập nhật điều khoản & thông tin báo giá!"
    }

    fun regenerateDefaultLinesFromDimensions() {
        val c = _config.value
        _quoteLines.value = buildInitialDefaultLines(c.widthM, c.heightM)
        _studioNotice.value = "Đã tính lại bảng giá theo kích thước ${String.format(Locale.US, "%.2f", c.widthM)}m x ${String.format(Locale.US, "%.2f", c.heightM)}m"
    }

    fun toDossierItems(): List<ProjectDossierItem> {
        val c = _config.value
        val t = _quoteTerms.value
        val factor = (1.0 - t.discountPercent.coerceIn(0.0, 90.0) / 100.0) *
            (1.0 + t.vatPercent.coerceIn(0.0, 30.0) / 100.0)
        val dim = "${String.format(Locale.US, "%.2f", c.widthM)}m x ${String.format(Locale.US, "%.2f", c.heightM)}m"
        return _quoteLines.value.mapIndexed { idx, line ->
            val finalPrice = line.lineTotal * factor
            ProjectDossierItem(
                id = System.currentTimeMillis() + idx + 1L,
                name = "${line.name} (${line.spec})",
                categoryName = "3D • ${c.style.shortLabel}",
                dimensions = "$dim (${String.format(Locale.US, "%.1f", line.quantity)} ${line.unit})",
                quantity = 1,
                totalCost = finalPrice * 0.72,
                finalPrice = finalPrice
            )
        }
    }

    companion object {
        fun buildInitialDefaultLines(widthM: Double, heightM: Double): List<Design3dQuoteLine> {
            val area = (widthM * heightM).coerceAtLeast(1.0)
            return listOf(
                Design3dQuoteLine(
                    id = 101L,
                    name = "Khung thép mạ kẽm & Mặt dựng nền Alu PVDF 3mm",
                    spec = "Sắt hộp 25x25x1.2mm chống gỉ, ốp Alu Alcorest ngoài trời phay rãnh gập cạnh",
                    unit = "m²",
                    quantity = area,
                    unitPrice = 680000.0
                ),
                Design3dQuoteLine(
                    id = 102L,
                    name = "Bộ chữ nổi 3D Inox Vàng Gương / Mica mặt sáng",
                    spec = "Uốn nổi chân cao 4-6cm, cắt CNC/Laser sắc nét theo font thiết kế",
                    unit = "Bộ",
                    quantity = 1.0,
                    unitPrice = area * 380000.0
                ),
                Design3dQuoteLine(
                    id = 103L,
                    name = "Hệ thống LED 12V chống nước & Bộ đổi nguồn + Timer",
                    spec = "LED Module 3 bóng Samsung/Hàn Quốc + Nguồn 12V33A + Rơ-le hẹn giờ tự động",
                    unit = "Trọn gói",
                    quantity = 1.0,
                    unitPrice = 1450000.0
                ),
                Design3dQuoteLine(
                    id = 104L,
                    name = "Nhân công lắp đặt, vận chuyển & hoàn thiện keo Silicone",
                    spec = "Thi công tại công trình, đi dây âm an toàn và bàn giao nghiệm thu",
                    unit = "Trọn gói",
                    quantity = 1.0,
                    unitPrice = 1200000.0
                )
            )
        }
    }
}
