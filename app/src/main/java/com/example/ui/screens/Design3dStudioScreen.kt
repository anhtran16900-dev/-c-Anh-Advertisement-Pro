package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Design3dQuoteLine
import com.example.model.Design3dRenderUiState
import com.example.model.Design3dSpecResult
import com.example.model.Design3dSpecUiState
import com.example.model.Design3dStyle
import com.example.model.LightingMode3d
import com.example.model.ProjectDossierItem
import com.example.ui.dialogs.Design3dQuoteLineDialog
import com.example.ui.dialogs.Design3dQuoteTermsDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Design3dPdfExporter
import com.example.util.Formatters
import com.example.viewmodel.Design3dViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Design3dStudioScreen(
    onNotify: (String) -> Unit,
    onShareQuoteText: (String) -> Unit,
    onSaveToDossier: (String, List<ProjectDossierItem>) -> Unit,
    designViewModel: Design3dViewModel = viewModel()
) {
    val config by designViewModel.config.collectAsState()
    val refBitmap by designViewModel.referenceBitmap.collectAsState()
    val specState by designViewModel.specUiState.collectAsState()
    val renderState by designViewModel.renderUiState.collectAsState()
    val quoteLines by designViewModel.quoteLines.collectAsState()
    val quoteTerms by designViewModel.quoteTerms.collectAsState()
    val quoteSummary by designViewModel.quoteSummary.collectAsState()
    val studioNotice by designViewModel.studioNotice.collectAsState()
    val context = LocalContext.current

    var widthText by remember(config.widthM) { mutableStateOf(config.widthM.toString()) }
    var heightText by remember(config.heightM) { mutableStateOf(config.heightM.toString()) }
    var editingLine by remember { mutableStateOf<Design3dQuoteLine?>(null) }
    var showAddLineDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(studioNotice) {
        studioNotice?.let {
            onNotify(it)
            designViewModel.clearNotice()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            designViewModel.loadReferencePhoto(uri)
        }
    }

    val currentSpec: Design3dSpecResult? = (specState as? Design3dSpecUiState.Success)?.data
    val renderedBitmap = (renderState as? Design3dRenderUiState.Success)?.bitmap

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Banner
        Card(
            modifier = Modifier.fillMaxWidth().testTag("design3d_hero_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Purple400.copy(alpha = 0.45f)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2E1065).copy(alpha = 0.7f), Slate900)))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Purple400.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = Purple400, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Studio Thiết Kế 3D & Báo Giá Thông Minh",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tải ảnh mặt tiền tham chiếu hoặc nhập yêu cầu -> AI phân tích 3D + dựng phối cảnh + báo giá PDF chuyên nghiệp.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Step 1: Reference Photo Upload Card (Zero-permission Photo Picker)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("design3d_reference_photo_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1. Ảnh Mặt Tiền / Mẫu Tham Chiếu (Tùy chọn)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (refBitmap != null) "Đã đính kèm ảnh - AI sẽ phân tích & ghép biển 3D lên mặt tiền này" else "Chụp mặt tiền trống hoặc chọn ảnh mẫu khách gửi để AI dựng 3D khớp hiện trạng",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (refBitmap != null) {
                        IconButton(
                            onClick = { designViewModel.clearReferencePhoto() },
                            modifier = Modifier.size(48.dp).testTag("clear_ref_photo_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Xóa ảnh tham chiếu", tint = Amber400)
                        }
                    }
                }

                if (refBitmap != null) {
                    Image(
                        bitmap = refBitmap!!.asImageBitmap(),
                        contentDescription = "Ảnh mặt tiền tham chiếu",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("pick_ref_photo_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.7f)))
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (refBitmap != null) "Đổi Ảnh Tham Chiếu Khác" else "Chọn Ảnh Tham Chiếu Từ Thư Viện",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cyan400
                    )
                }
            }
        }

        // Step 2: Client Requirements & 3D Style Form
        Card(
            modifier = Modifier.fillMaxWidth().testTag("design3d_requirements_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "2. Yêu Cầu Dựng Biển Hiệu 3D Của Khách Hàng",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                StudioField(
                    value = config.brandText,
                    onValueChange = { txt -> designViewModel.updateConfig { it.copy(brandText = txt) } },
                    label = "Tên Thương Hiệu / Nội Dung Chính Trên Biển *",
                    modifier = Modifier.testTag("design3d_brand_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioField(
                        value = config.slogan,
                        onValueChange = { txt -> designViewModel.updateConfig { it.copy(slogan = txt) } },
                        label = "Slogan / Hotline phụ",
                        modifier = Modifier.weight(1.2f)
                    )
                    StudioField(
                        value = config.businessType,
                        onValueChange = { txt -> designViewModel.updateConfig { it.copy(businessType = txt) } },
                        label = "Ngành nghề",
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudioField(
                        value = widthText,
                        onValueChange = { txt ->
                            widthText = txt
                            txt.toDoubleOrNull()?.let { w -> designViewModel.updateConfig { it.copy(widthM = w.coerceAtLeast(0.2)) } }
                        },
                        label = "Chiều Ngang (m)",
                        modifier = Modifier.weight(1f).testTag("design3d_width_input")
                    )
                    StudioField(
                        value = heightText,
                        onValueChange = { txt ->
                            heightText = txt
                            txt.toDoubleOrNull()?.let { h -> designViewModel.updateConfig { it.copy(heightM = h.coerceAtLeast(0.2)) } }
                        },
                        label = "Chiều Cao (m)",
                        modifier = Modifier.weight(1f).testTag("design3d_height_input")
                    )
                }

                Text("Phong cách kiến trúc 3D:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Cyan400)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Design3dStyle.entries.forEach { st ->
                        val selected = config.style == st
                        Surface(
                            onClick = { designViewModel.updateConfig { it.copy(style = st) } },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("design3d_style_${st.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (selected) Cyan400 else Slate700))
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = st.shortLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Cyan400 else Color.White
                                )
                            }
                        }
                    }
                }

                Text("Ánh sáng & Thời điểm phối cảnh:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Amber400)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LightingMode3d.entries.forEach { lm ->
                        val selected = config.lightingMode == lm
                        Surface(
                            onClick = { designViewModel.updateConfig { it.copy(lightingMode = lm) } },
                            modifier = Modifier.heightIn(min = 44.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) Color(0xFF451A03) else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (selected) Amber400 else Slate700))
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(lm.label, fontSize = 11.sp, color = if (selected) Amber400 else Color.White)
                            }
                        }
                    }
                }

                StudioField(
                    value = config.primaryColors,
                    onValueChange = { txt -> designViewModel.updateConfig { it.copy(primaryColors = txt) } },
                    label = "Tông màu chủ đạo (VD: Đen nhám - Vàng Gold - Trắng)"
                )
                StudioField(
                    value = config.extraNotes,
                    onValueChange = { txt -> designViewModel.updateConfig { it.copy(extraNotes = txt) } },
                    label = "Yêu cầu đặc biệt (Mái hiên, hướng nắng, ngân sách dự kiến...)",
                    maxLines = 2
                )

                // Primary AI Generation Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { designViewModel.analyzeAndDesign3dSpec() },
                        enabled = specState !is Design3dSpecUiState.Loading,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("generate_3d_spec_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (specState is Design3dSpecUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dựng Phương Án 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { designViewModel.render3dMockup() },
                        enabled = renderState !is Design3dRenderUiState.Loading,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("render_3d_mockup_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (renderState is Design3dRenderUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                        } else {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tạo Ảnh 3D AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AI Rendered 3D Mockup Viewer (Strict no-mock policy)
        when (val rState = renderState) {
            is Design3dRenderUiState.Loading -> {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("design3d_render_loading"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Purple400.copy(alpha = 0.6f)))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = Purple400)
                        Text("Đang tổng hợp ảnh phối cảnh 3D (30-60s)...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = if (refBitmap != null) "Đang ghép biển hiệu 3D trực tiếp lên ảnh mặt tiền của bạn" else "Đang kết xuất mô hình kiến trúc mặt tiền 3D",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is Design3dRenderUiState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("design3d_render_success"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Phối Cảnh 3D AI Đề Xuất (Sẵn sàng chèn vào PDF)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400
                        )
                        Image(
                            bitmap = rState.bitmap.asImageBitmap(),
                            contentDescription = "Ảnh phối cảnh 3D biển hiệu do AI tạo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp, max = 260.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
            is Design3dRenderUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("design3d_render_error"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFEF4444)))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Không thể tạo ảnh phối cảnh 3D AI", fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5), fontSize = 13.sp)
                        Text(rState.message, fontSize = 11.sp, color = Color(0xFFFEE2E2), lineHeight = 16.sp)
                    }
                }
            }
            Design3dRenderUiState.Idle -> Unit
        }

        // Structured 3D Engineering Spec Output
        when (val sState = specState) {
            is Design3dSpecUiState.Loading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = Cyan400)
                        Text("Gemini 3.8 Flash đang phân tích mặt tiền, tỷ lệ 3D & lập bảng báo giá...", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
            is Design3dSpecUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("design3d_spec_error"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A))
                ) {
                    Text(sState.message, modifier = Modifier.padding(14.dp), color = Color(0xFFFCA5A5), fontSize = 12.sp)
                }
            }
            is Design3dSpecUiState.Success -> {
                val spec = sState.data
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("design3d_spec_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.6f)))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(spec.conceptTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Cyan400)
                        SpecBlock("Phân tích mặt tiền & bố cục:", spec.facadeAnalysis)
                        SpecBlock("Quy cách dựng khối 3D:", spec.visual3dSpec)
                        SpecBlock("Phương án ánh sáng LED ngày/đêm:", spec.lightingPlan)
                        SpecBlock("Kết cấu lắp đặt (${spec.estimatedDays} ngày):", spec.installationMethod)
                    }
                }
            }
            Design3dSpecUiState.Idle -> Unit
        }

        // Step 3: Customizable Professional Client Quotation Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("design3d_quote_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400.copy(alpha = 0.5f)))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "3. Báo Giá Tùy Chỉnh Gửi Khách (${quoteLines.size} mục)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Khách: ${quoteTerms.clientName} • CK ${quoteTerms.discountPercent.toInt()}% • VAT ${quoteTerms.vatPercent.toInt()}% • BH ${quoteTerms.warrantyMonths}th",
                            fontSize = 11.sp,
                            color = Amber400
                        )
                    }
                    OutlinedButton(
                        onClick = { showTermsDialog = true },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("design3d_edit_terms_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Điều Khoản", fontSize = 11.sp, color = Cyan400, fontWeight = FontWeight.Bold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showAddLineDialog = true },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("design3d_add_line_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thêm Hạng Mục", fontSize = 11.sp, color = Emerald400, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { designViewModel.regenerateDefaultLinesFromDimensions() },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("design3d_recalc_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tính Theo KT", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                quoteLines.forEachIndexed { idx, line ->
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("${idx + 1}. ${line.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(line.spec, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", line.quantity)} ${line.unit} x ${Formatters.formatVnd(line.unitPrice)}",
                                    fontSize = 11.sp,
                                    color = Cyan400
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = Formatters.formatVnd(line.lineTotal),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                                Row {
                                    IconButton(
                                        onClick = { editingLine = line },
                                        modifier = Modifier.size(48.dp).testTag("edit_3d_line_${line.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa hạng mục", tint = Cyan400, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { designViewModel.removeQuoteLine(line.id) },
                                        modifier = Modifier.size(48.dp).testTag("delete_3d_line_${line.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa hạng mục", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Slate800)

                SummaryLineRow("Tạm tính:", Formatters.formatVnd(quoteSummary.subtotal), Color.White)
                if (quoteSummary.discountAmount > 0.0) {
                    SummaryLineRow("Chiết khấu (${quoteTerms.discountPercent.toInt()}%):", "-${Formatters.formatVnd(quoteSummary.discountAmount)}", Amber400)
                }
                if (quoteSummary.vatAmount > 0.0) {
                    SummaryLineRow("Thuế VAT (${quoteTerms.vatPercent.toInt()}%):", "+${Formatters.formatVnd(quoteSummary.vatAmount)}", Color.White)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("TỔNG THANH TOÁN:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = Formatters.formatVnd(quoteSummary.grandTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = Emerald400,
                        modifier = Modifier.testTag("design3d_grand_total_text")
                    )
                }
                Text(
                    text = "Tạm ứng ${quoteTerms.depositPercent}%: ${Formatters.formatVnd(quoteSummary.depositAmount)} • Còn lại: ${Formatters.formatVnd(quoteSummary.balanceAmount)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Client Delivery Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            Design3dPdfExporter.share3dQuotePdf(
                                context = context,
                                config = config,
                                spec = currentSpec,
                                mockupBitmap = renderedBitmap,
                                lines = quoteLines,
                                terms = quoteTerms,
                                summary = quoteSummary
                            )
                        },
                        enabled = quoteLines.isNotEmpty(),
                        modifier = Modifier.weight(1.1f).heightIn(min = 48.dp).testTag("design3d_export_pdf_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gửi PDF 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val text = Design3dPdfExporter.buildDesign3dZaloQuote(
                                config = config,
                                spec = currentSpec,
                                lines = quoteLines,
                                terms = quoteTerms,
                                summary = quoteSummary
                            )
                            onShareQuoteText(text)
                        },
                        enabled = quoteLines.isNotEmpty(),
                        modifier = Modifier.weight(0.95f).heightIn(min = 48.dp).testTag("design3d_share_zalo_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Zalo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                    }

                    Button(
                        onClick = {
                            onSaveToDossier(
                                "Thiết Kế 3D - ${config.brandText.ifBlank { "Biển Hiệu" }}",
                                designViewModel.toDossierItems()
                            )
                        },
                        enabled = quoteLines.isNotEmpty(),
                        modifier = Modifier.weight(0.95f).heightIn(min = 48.dp).testTag("design3d_save_dossier_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lưu Hồ Sơ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showAddLineDialog) {
        Design3dQuoteLineDialog(
            initialLine = null,
            onSave = { line ->
                designViewModel.addOrUpdateQuoteLine(line)
                showAddLineDialog = false
            },
            onDismiss = { showAddLineDialog = false }
        )
    }

    editingLine?.let { target ->
        Design3dQuoteLineDialog(
            initialLine = target,
            onSave = { updated ->
                designViewModel.addOrUpdateQuoteLine(updated)
                editingLine = null
            },
            onDismiss = { editingLine = null }
        )
    }

    if (showTermsDialog) {
        Design3dQuoteTermsDialog(
            current = quoteTerms,
            onSave = { updated ->
                designViewModel.updateQuoteTerms(updated)
                showTermsDialog = false
            },
            onDismiss = { showTermsDialog = false }
        )
    }
}

@Composable
private fun StudioField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = maxLines == 1,
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan400,
            unfocusedBorderColor = Slate700,
            focusedContainerColor = Slate950,
            unfocusedContainerColor = Slate950,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun SpecBlock(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Amber400)
        Text(body, fontSize = 12.sp, color = Color.White, lineHeight = 17.sp)
    }
}

@Composable
private fun SummaryLineRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
