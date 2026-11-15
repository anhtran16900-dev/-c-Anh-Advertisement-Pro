package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiSuggestedMaterial
import com.example.model.AiSuggestionUiState
import com.example.model.EstimationInput
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Pink500
import com.example.ui.theme.Purple400
import com.example.ui.theme.Purple500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.util.Formatters
import java.util.Locale

@Composable
fun GeminiMaterialSuggestionCard(
    input: EstimationInput,
    advertisingDescription: String,
    suggestionState: AiSuggestionUiState,
    onDescriptionChange: (String) -> Unit,
    onSuggestClick: () -> Unit,
    onApplyMaterial: (AiSuggestedMaterial) -> Unit,
    onSaveMaterial: (AiSuggestedMaterial) -> Unit,
    onSaveAllMaterials: (List<AiSuggestedMaterial>) -> Unit,
    onClear: () -> Unit
) {
    val quickTemplates = listOf(
        "Mặt tiền Showroom ngoài trời" to "Biển mặt tiền Showroom ngoài trời hứng nắng gió hướng Tây, cần chống phai màu, chữ nổi hắt sáng ban đêm và khung thép chịu lực.",
        "Hộp đèn Cafe ban đêm" to "Biển hộp đèn quán Cafe phong cách hiện đại nổi bật ban đêm, ánh sáng đều không lộ bóng LED, dễ vệ sinh bảo trì.",
        "Chữ Inox văn phòng" to "Biển tên công ty gắn sảnh lễ tân trong nhà, chữ nổi Inox vàng gương sang trọng kết hợp nền Mica/Alu phẳng.",
        "Pano bạt khổ lớn" to "Pano Hiflex ngoài trời kích thước lớn lắp mặt đường, cần bạt dày chống rách gió bão và đèn pha rọi biển."
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gemini_material_suggestion_section"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(Purple400, Pink500, Cyan400))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Brush.linearGradient(listOf(Purple500, Pink500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini 3.8 Flash",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GỢI Ý VẬT TƯ — GEMINI 3.8 FLASH",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Purple400,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Đọc dữ liệu form + mô tả ngành quảng cáo để đề xuất vật tư tối ưu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (suggestionState !is AiSuggestionUiState.Idle) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.testTag("btn_clear_gemini_suggestions")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa kết quả gợi ý",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Form Context Pill
            Surface(
                color = Slate950,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Form gửi AI: ${String.format(Locale.US, "%.1f", input.width)}m x ${String.format(Locale.US, "%.1f", input.height)}m • SL ${input.quantity} • ${input.category.shortName}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Cyan400
                    )
                }
            }

            // Quick Industry Templates
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickTemplates.forEach { (label, templateText) ->
                    val selected = advertisingDescription == templateText
                    Surface(
                        onClick = { onDescriptionChange(templateText) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) Purple500.copy(alpha = 0.25f) else Slate950,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (selected) Purple400 else Slate800)
                        )
                    ) {
                        Text(
                            text = "+ $label",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) Purple400 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Advertising Industry Description Input
            Column {
                Text(
                    text = "Mô Tả Ngành Quảng Cáo & Yêu Cầu Thi Công *",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = advertisingDescription,
                    onValueChange = onDescriptionChange,
                    placeholder = {
                        Text(
                            text = "VD: Biển mặt tiền tiệm vàng ngoài trời hướng nắng gắt, muốn mặt Alu bền màu, chữ nổi Mica hắt sáng LED ban đêm...",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_ai_ad_description_input"),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Trigger Button
            Button(
                onClick = onSuggestClick,
                enabled = suggestionState !is AiSuggestionUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_gemini_suggest_materials"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple500,
                    contentColor = Color.White,
                    disabledContainerColor = Slate800,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (suggestionState is AiSuggestionUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini 3.8 Flash đang phân tích...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gợi Ý Danh Sách Vật Tư (Gemini 3.8 Flash)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // State Content
            when (suggestionState) {
                is AiSuggestionUiState.Idle -> {
                    Text(
                        text = "💡 Hệ thống sẽ gửi toàn bộ kích thước, loại biển, tùy chọn khung/LED và mô tả của bạn lên Gemini 3.8 Flash để đề xuất danh sách vật tư chuẩn xác.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is AiSuggestionUiState.Loading -> {
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Purple400.copy(alpha = 0.5f)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Purple400,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Đang bóc tách phôi mặt biển, khung xương, LED và phụ kiện thi công từ Gemini 3.8 Flash...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }

                is AiSuggestionUiState.Error -> {
                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFEF4444)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Lỗi",
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lỗi gọi Gemini 3.8 Flash",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5)
                                )
                            }
                            Text(
                                text = suggestionState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            OutlinedButton(
                                onClick = onSuggestClick,
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_retry_gemini_suggest"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFEF4444)))
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Thử Lại", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                is AiSuggestionUiState.Success -> {
                    val result = suggestionState.data

                    // Engineering summary
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.5f)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "PHƯƠNG ÁN KỸ THUẬT TỪ GEMINI 3.8 FLASH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Cyan400
                            )
                            Text(
                                text = result.engineeringSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            if (result.constructionNotes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                result.constructionNotes.forEach { note ->
                                    Text(
                                        text = "• $note",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Amber400
                                    )
                                }
                            }
                        }
                    }

                    // Header + Save All Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vật tư gợi ý (${result.materials.size} mục)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400
                        )
                        OutlinedButton(
                            onClick = { onSaveAllMaterials(result.materials) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("btn_save_all_ai_materials"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald400),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400))
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Lưu Tất Cả Vào Kho", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Suggested Material Cards
                    result.materials.forEachIndexed { index, item ->
                        Surface(
                            color = Slate950,
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (item.isPrimaryMaterial) Cyan400 else Slate800)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_suggested_material_card_$index")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(color = Slate800, shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = item.category,
                                                fontSize = 10.sp,
                                                color = Cyan400,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (item.isPrimaryMaterial) {
                                            Surface(color = Cyan900, shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    text = "Vật tư chính",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Amber400,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = if (item.estimatedUnitPrice > 0) {
                                            "${Formatters.formatVnd(item.estimatedUnitPrice)} / ${item.unit}"
                                        } else {
                                            item.unit
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald400
                                    )
                                }

                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "Quy cách: ${item.spec} • Định lượng dự kiến: ${String.format(Locale.US, "%.1f", item.estimatedQuantity)} ${item.unit}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber400
                                )

                                if (item.reason.isNotBlank()) {
                                    Text(
                                        text = "💡 ${item.reason}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onApplyMaterial(item) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .testTag("btn_apply_ai_material_$index"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Cyan400,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Áp Dụng Vào Biển",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { onSaveMaterial(item) },
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("btn_save_ai_material_$index"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald400),
                                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Emerald400.copy(alpha = 0.6f)))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Lưu Kho", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
