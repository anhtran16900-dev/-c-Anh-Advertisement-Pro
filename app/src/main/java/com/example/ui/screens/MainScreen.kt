package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ViewQuilt
import com.example.model.BoardDesignFamily
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Cyan600
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Pink400
import com.example.ui.theme.Pink500
import com.example.ui.theme.Purple400
import com.example.ui.theme.Purple500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.viewmodel.SignageViewModel

@Composable
fun MainScreen(viewModel: SignageViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userNotice by viewModel.userNotice.collectAsState()
    val unitPrices by viewModel.unitPrices.collectAsState()
    val userApiKey by viewModel.userApiKey.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var estimatorSubTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(userNotice) {
        userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    fun shareText(content: String, subject: String = "Báo Giá Quảng Cáo Đức Anh PRO") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(intent, "Chia Sẻ Báo Giá Qua"))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Slate950,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBrandBar(
                onOpenSettings = { showSettingsDialog = true }
            )
        },
        bottomBar = {
            WorkshopBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.switchTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SubTabPill(
                                    label = "Biểu Mẫu",
                                    icon = Icons.Default.Assignment,
                                    selected = estimatorSubTab == 0,
                                    testTag = "subtab_form_btn",
                                    onClick = { estimatorSubTab = 0 }
                                )
                                SubTabPill(
                                    label = "Bảng Bạt",
                                    icon = Icons.Default.ViewQuilt,
                                    selected = estimatorSubTab == 1,
                                    testTag = "subtab_tarp_btn",
                                    onClick = { estimatorSubTab = 1 }
                                )
                                SubTabPill(
                                    label = "Alu Chữ Nổi",
                                    icon = Icons.Default.Layers,
                                    selected = estimatorSubTab == 2,
                                    testTag = "subtab_alu_btn",
                                    onClick = { estimatorSubTab = 2 }
                                )
                                SubTabPill(
                                    label = "Bảng Decal",
                                    icon = Icons.Default.Style,
                                    selected = estimatorSubTab == 3,
                                    testTag = "subtab_decal_btn",
                                    onClick = { estimatorSubTab = 3 }
                                )
                                SubTabPill(
                                    label = "Tính m² & Giá",
                                    icon = Icons.Default.SquareFoot,
                                    selected = estimatorSubTab == 4,
                                    testTag = "subtab_smart_calc_btn",
                                    onClick = { estimatorSubTab = 4 }
                                )
                                SubTabPill(
                                    label = "Thiết Kế 3D",
                                    icon = Icons.Default.ViewInAr,
                                    selected = estimatorSubTab == 5,
                                    testTag = "subtab_design_3d_btn",
                                    onClick = { estimatorSubTab = 5 }
                                )
                                SubTabPill(
                                    label = "Bóc Tách",
                                    icon = Icons.Default.Calculate,
                                    selected = estimatorSubTab == 6,
                                    testTag = "subtab_breakdown_btn",
                                    onClick = { estimatorSubTab = 6 }
                                )
                            }

                            when (estimatorSubTab) {
                                0 -> ProjectEstimateFormScreen(
                                    viewModel = viewModel,
                                    onNavigateToBreakdown = { estimatorSubTab = 6 },
                                    onOpenBoardDesigner = { fam ->
                                        estimatorSubTab = when (fam) {
                                            BoardDesignFamily.TARP_BANNER -> 1
                                            BoardDesignFamily.ALU_3D_LETTER -> 2
                                            BoardDesignFamily.DECAL_STICKER -> 3
                                        }
                                    }
                                )
                                1, 2, 3 -> {
                                    val activeFamily = when (estimatorSubTab) {
                                        1 -> BoardDesignFamily.TARP_BANNER
                                        2 -> BoardDesignFamily.ALU_3D_LETTER
                                        else -> BoardDesignFamily.DECAL_STICKER
                                    }
                                    SignageBoardDesignerScreen(
                                        selectedFamily = activeFamily,
                                        onSelectFamily = { fam ->
                                            estimatorSubTab = when (fam) {
                                                BoardDesignFamily.TARP_BANNER -> 1
                                                BoardDesignFamily.ALU_3D_LETTER -> 2
                                                BoardDesignFamily.DECAL_STICKER -> 3
                                            }
                                        },
                                        onShareQuoteText = { text -> shareText(text, "Báo Giá ${activeFamily.fullTitle}") },
                                        onSaveToDossier = { label, items ->
                                            viewModel.addBatchItemsToDossier(label, items)
                                        }
                                    )
                                }
                                4 -> SmartCalculatorScreen(
                                    viewModel = viewModel,
                                    onJumpToBreakdown = { estimatorSubTab = 6 }
                                )
                                5 -> Design3dStudioScreen(
                                    onNotify = { msg -> viewModel.notifyMessage(msg) },
                                    onShareQuoteText = { text -> shareText(text, "Thiết Kế 3D & Báo Giá Quảng Cáo") },
                                    onSaveToDossier = { label, items ->
                                        viewModel.addBatchItemsToDossier(label, items)
                                    }
                                )
                                else -> EstimatorScreen(
                                    viewModel = viewModel,
                                    onShareQuote = { text -> shareText(text, "Báo Giá Hạng Mục Quảng Cáo") }
                                )
                            }
                        }
                    }
                    1 -> AiChatScreen(viewModel = viewModel)
                    2 -> MaterialCatalogScreen(viewModel = viewModel)
                    3 -> ProjectDossierScreen(
                        viewModel = viewModel,
                        onShareDossier = { text -> shareText(text, "Hồ Sơ Dự Toán Báo Giá Công Trình") }
                    )
                    4 -> AcademyScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentPrices = unitPrices,
            currentApiKey = userApiKey,
            onSavePrices = { viewModel.updateUnitPrices(it) },
            onSaveApiKey = { viewModel.saveUserApiKey(it) },
            onResetDefaults = { viewModel.resetDefaultPrices() },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
private fun TopBrandBar(
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = Slate900.copy(alpha = 0.95f),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Cyan500, Color(0xFF2563EB), Color(0xFF4F46E5))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Đức Anh ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Advertisement PRO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Cyan400
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Cyan500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Cyan500.copy(alpha = 0.3f)))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Cyan400,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Realtime AI",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan400,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = "Bóc tách vật tư kỹ thuật & báo giá chuyên nghiệp",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate800)
                    .size(38.dp)
                    .testTag("top_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Cài đặt đơn giá phôi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkshopBottomNav(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Slate900,
        tonalElevation = 8.dp
    ) {
        // Tab 0: Bóc Tách
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(imageVector = Icons.Default.Calculate, contentDescription = "Bóc Tách") },
            label = { Text("Bóc Tách", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan400,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_estimator")
        )

        // Tab 1: Trợ Lý AI
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                BadgedBox(
                    badge = {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Pink500)
                        )
                    }
                ) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Trợ Lý AI")
                }
            },
            label = { Text("Trợ Lý AI", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Pink400,
                indicatorColor = Pink500,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_ai")
        )

        // Tab 2: Kho Vật Tư
        NavigationBarItem(
            selected = currentTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(imageVector = Icons.Default.Inventory2, contentDescription = "Kho Vật Tư") },
            label = { Text("Kho Vật Tư", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Purple400,
                indicatorColor = Purple500,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_materials")
        )

        // Tab 3: Hồ Sơ Dự Toán
        NavigationBarItem(
            selected = currentTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = "Dự Toán") },
            label = { Text("Dự Toán", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Emerald400,
                indicatorColor = Emerald400,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_project")
        )

        // Tab 4: Đào Tạo
        NavigationBarItem(
            selected = currentTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(imageVector = Icons.Default.School, contentDescription = "Đào Tạo") },
            label = { Text("Kỹ Thuật", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan400,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_academy")
        )
    }
}

@Composable
private fun SubTabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Cyan900 else Slate900,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (selected) Cyan400 else Slate800)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

