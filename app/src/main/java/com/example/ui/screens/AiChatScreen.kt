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

    // Only scroll when the message count or loading state changes.
    // Do not trigger animations for every streamed character.
    LaunchedEffect(messages.size, isLoading) {
        val lastIndex = when {
            isLoading -> messages.size
            messages.isNotEmpty() -> messages.size - 1
            else -> -1
        }

        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    val fastPrompts = remember {
        listOf(
            "📊 Tính m² & giá biển bạt 5x1.8m Hiflex vs UV",
            "🏗️ Báo giá bảng Alu 6x1.5m chữ nổi Mica LED",
            "🏷️ Tính khổ cuộn Decal sữa dán kính 3.2x1.4m",
            "🖨️ Chữa máy in bạt bị sọc pass & lệch đầu phun",
            "⚡ Tính công suất nguồn 12V 33A cho 400 con LED"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .imePadding()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = 6.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Slate900
            ),
            border = CardDefaults.outlinedCardBorder()
                .copy(brush = SolidColor(Slate800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Pink500, Purple500)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Đức Anh AI Specialist",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(Modifier.height(3.dp))

                        val isCustomKey = apiKey.isNotBlank()

                        Surface(
                            color = if (isCustomKey)
                                Pink400.copy(alpha = 0.15f)
                            else Emerald400.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isCustomKey) "Key riêng"
                                else if (turboMode) "Siêu Tốc • 3.5"
                                else "Chuyên Sâu • 3.8",
                                fontSize = 10.sp,
                                color = if (isCustomKey) Pink400
                                else Emerald400,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(
                                    horizontal = 7.dp,
                                    vertical = 3.dp
                                )
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = if (turboMode)
                                "Phản hồi siêu nhanh • Bóc tách & báo giá"
                            else
                                "Gemini 3.8 Flash • Phân tích chuyên sâu",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("ai_clear_chat_button")
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Xóa hội thoại",
                        tint = Color.LightGray
                    )
                }
            }
        }

        // AI MODE TOOLBAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { viewModel.setChatTurboMode(true) },
                modifier = Modifier.heightIn(min = 40.dp)
                    .testTag("chat_mode_turbo_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (turboMode)
                    Emerald400.copy(alpha = 0.16f)
                else Slate900,
                border = CardDefaults.outlinedCardBorder()
                    .copy(
                        brush = SolidColor(
                            if (turboMode) Emerald400 else Slate800
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        null,
                        tint = if (turboMode) Emerald400 else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Siêu Tốc",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Surface(
                onClick = { viewModel.setChatTurboMode(false) },
                modifier = Modifier.heightIn(min = 40.dp)
                    .testTag("chat_mode_deep_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (!turboMode) Cyan900 else Slate900,
                border = CardDefaults.outlinedCardBorder()
                    .copy(
                        brush = SolidColor(
                            if (!turboMode) Cyan400 else Slate800
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        null,
                        tint = if (!turboMode) Cyan400 else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Chuyên Sâu 3.8",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Surface(
                onClick = {
                    viewModel.setAttachContextInChat(!attachContext)
                },
                modifier = Modifier.heightIn(min = 40.dp)
                    .testTag("chat_attach_context_btn"),
                shape = RoundedCornerShape(10.dp),
                color = if (attachContext)
                    Pink500.copy(alpha = 0.18f)
                else Slate900,
                border = CardDefaults.outlinedCardBorder()
                    .copy(
                        brush = SolidColor(
                            if (attachContext) Pink400 else Slate800
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Straighten,
                        null,
                        tint = if (attachContext) Pink400 else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (attachContext) "Đang kèm thông số KT"
                        else "Kèm KT đang tính",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }

        // QUICK PROMPTS
        if (messages.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fastPrompts.forEach { prompt ->
                    Surface(
                        onClick = {
                            if (!isLoading) {
                                viewModel.sendChatMessage(prompt)
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        color = Slate900,
                        border = CardDefaults.outlinedCardBorder()
                            .copy(brush = SolidColor(Slate800))
                    ) {
                        Text(
                            prompt,
                            fontSize = 11.sp,
                            color = Cyan400,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // INDEPENDENT CHAT SCROLL AREA
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { msg ->
                ChatBubble(
                    message = msg,
                    showUseFreeFallback =
                        msg.isError && apiKey.isNotBlank(),
                    onCopy = {
                        val clipboard = context.getSystemService(
                            Context.CLIPBOARD_SERVICE
                        ) as ClipboardManager

                        clipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "Đức Anh AI",
                                msg.text
                            )
                        )

                        viewModel.notifyMessage(
                            "Đã sao chép nội dung trả lời"
                        )
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
                item(key = "ai_loading_indicator") {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Pink400,
                            strokeWidth = 2.dp
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            "Gemini AI đang phân tích...",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // FIXED INPUT AREA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "Hỏi về bóc tách, máy in KTS, CNC...",
                        fontSize = 13.sp
                    )
                },
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
                    val text = inputText.trim()

                    if (text.isNotEmpty() && !isLoading) {
                        inputText = ""
                        viewModel.sendChatMessage(text)
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank() && !isLoading)
                            Brush.linearGradient(
                                listOf(Pink500, Purple500)
                            )
                        else
                            SolidColor(Slate800)
                    )
                    .testTag("ai_send_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gửi câu hỏi",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
