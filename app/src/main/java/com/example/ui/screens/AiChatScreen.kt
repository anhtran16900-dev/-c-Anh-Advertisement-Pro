package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Pink400
import com.example.ui.theme.Pink500
import com.example.ui.theme.Purple500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.viewmodel.SignageViewModel

import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Straighten

@Composable
fun AiChatScreen(viewModel: SignageViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val apiKey by viewModel.userApiKey.collectAsState()
    val turboMode by viewModel.chatTurboMode.collectAsState()
    val attachContext by viewModel.attachContextInChat.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.warmUpAiChat()
    }

    val lastMessageLength = messages.lastOrNull()?.text?.length ?: 0
    LaunchedEffect(messages.size, lastMessageLength, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val fastPrompts = listOf(
        "📊 Tính m² & giá biển bạt 5x1.8m Hiflex vs UV",
        "🏗️ Báo giá bảng Alu 6x1.5m chữ nổi Mica LED",
        "🏷️ Tính khổ cuộn Decal sữa dán kính 3.2x1.4m",
        "🖨️ Chữa máy in bạt bị sọc pass & lệch đầu phun",
        "⚡ Tính công suất nguồn 12V 33A cho 400 con LED"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Chat Header Banner
        Card(
            modifier = Modifier.fillMaxWidth().testTag("ai_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Pink500, Purple500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Đức Anh AI Specialist",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val isCustomKey = apiKey.isNotBlank()
                            Surface(
                                color = if (isCustomKey) Pink400.copy(alpha = 0.15f) else Emerald400.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isCustomKey) "Key riêng" else if (turboMode) "Siêu Tốc • 3.5" else "Chuyên Sâu • 3.8",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCustomKey) Pink400 else Emerald400,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (turboMode) {
                                "Phản hồi siêu nhanh < 2 giây • Bóc tách & báo giá tức thì"
                            } else {
                                "Phân tích chuyên sâu Gemini 3.8 Flash • Không cần API Key"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("ai_clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Làm mới cuộc hội thoại",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Smart Speed & Context Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { viewModel.setChatTurboMode(true) },
                modifier = Modifier.heightIn(min = 40.dp).testTag("chat_mode_turbo_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (turboMode) Emerald400.copy(alpha = 0.16f) else Slate900,
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (turboMode) Emerald400 else Slate800))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = if (turboMode) Emerald400 else Color.Gray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Siêu Tốc", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (turboMode) Emerald400 else Color.White)
                }
            }

            Surface(
                onClick = { viewModel.setChatTurboMode(false) },
                modifier = Modifier.heightIn(min = 40.dp).testTag("chat_mode_deep_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (!turboMode) Cyan900 else Slate900,
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (!turboMode) Cyan400 else Slate800))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = if (!turboMode) Cyan400 else Color.Gray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chuyên Sâu 3.8", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!turboMode) Cyan400 else Color.White)
                }
            }

            Surface(
                onClick = { viewModel.setAttachContextInChat(!attachContext) },
                modifier = Modifier.heightIn(min = 40.dp).testTag("chat_attach_context_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (attachContext) Pink500.copy(alpha = 0.18f) else Slate900,
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (attachContext) Pink400 else Slate800))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Straighten, contentDescription = null, tint = if (attachContext) Pink400 else Color.Gray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (attachContext) "Đang kèm thông số KT" else "Kèm KT đang tính",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (attachContext) Pink400 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }        
        // Fast Prompts Chips (ẩn sau khi đã bắt đầu trò chuyện để chat không bị che)
        if (messages.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fastPrompts.forEach { prompt ->
                    Surface(
                        onClick = { viewModel.sendChatMessage(prompt) },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        color = Slate900,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (isLoading) Slate800.copy(alpha = 0.5f) else Slate800)
                        )
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLoading) Cyan400.copy(alpha = 0.45f) else Cyan400,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    showUseFreeFallback = msg.isError && apiKey.isNotBlank(),
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Đức Anh AI", msg.text))
                        viewModel.notifyMessage("Đã sao chép nội dung trả lời")
                    },
                    onRetry = { query ->
                        viewModel.sendChatMessage(query)
                    },
                    onSwitchToFreeAndRetry = { query ->
                        viewModel.switchToFreeGeminiMode()
                        viewModel.sendChatMessage(query)
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Pink400.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = Pink400, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate900),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Pink400, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini AI đang phân tích câu hỏi...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Hỏi về bóc tách, máy in KTS, CNC...", fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_field"),
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Pink400,
                    unfocusedBorderColor = Slate700,
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        viewModel.sendChatMessage(text)
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank() && !isLoading) Brush.linearGradient(listOf(Pink500, Purple500)) else Brush.linearGradient(listOf(Slate800, Slate800)))
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gửi câu hỏi",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    showUseFreeFallback: Boolean,
    onCopy: () -> Unit,
    onRetry: (String) -> Unit,
    onSwitchToFreeAndRetry: (String) -> Unit
) {
    val isUser = message.isUser
    val isError = message.isError

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isError) Color(0xFFEF4444).copy(alpha = 0.2f) else Pink400.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFFCA5A5) else Pink400,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.90f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isUser -> Cyan900.copy(alpha = 0.7f)
                    isError -> Color(0xFF7F1D1D).copy(alpha = 0.28f)
                    else -> Slate900
                }
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(
                    when {
                        isUser -> Cyan400.copy(alpha = 0.4f)
                        isError -> Color(0xFFEF4444).copy(alpha = 0.65f)
                        else -> Slate800
                    }
                )
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 20.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isError && !message.retryQuery.isNullOrBlank()) {
                                OutlinedButton(
                                    onClick = { onRetry(message.retryQuery) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("ai_retry_button"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5)),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFFEF4444)))
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Thử lại", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (showUseFreeFallback && !message.retryQuery.isNullOrBlank()) {
                                Button(
                                    onClick = { onSwitchToFreeAndRetry(message.retryQuery) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("ai_use_free_button"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald400, contentColor = Color.Black)
                                ) {
                                    Text("Dùng AI Miễn Phí", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("ai_copy_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép nội dung",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Cyan500),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            }
        }
    }
}
