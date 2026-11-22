package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AcademyGuide
import com.example.ui.dialogs.AcademyLessonDialog
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.viewmodel.SignageViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AcademyScreen(viewModel: SignageViewModel) {
    val guides = viewModel.academyGuides
    val selectedGuide by viewModel.selectedGuide.collectAsState()
    val completedGuideSteps by viewModel.completedGuideSteps.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tất Cả") }

    val categories = remember(guides) {
        listOf("Tất Cả") + guides.map { it.category }.distinct()
    }

    val totalAllSteps = remember(guides) {
        guides.sumOf { it.steps.size }.coerceAtLeast(1)
    }
    val totalCompletedSteps = remember(guides, completedGuideSteps) {
        guides.sumOf { guide ->
            guide.steps.count { "${guide.id}_${it.stepNumber}" in completedGuideSteps }
        }
    }
    val masteryPercent = (totalCompletedSteps * 100) / totalAllSteps

    val filteredGuides = remember(guides, searchQuery, selectedCategory) {
        val q = searchQuery.trim().lowercase()
        guides.filter { guide ->
            val matchesCategory = selectedCategory == "Tất Cả" || guide.category == selectedCategory
            val matchesSearch = q.isEmpty() ||
                guide.title.lowercase().contains(q) ||
                guide.summary.lowercase().contains(q) ||
                guide.category.lowercase().contains(q) ||
                guide.practiceExercise.lowercase().contains(q) ||
                guide.shortcutsAndSpecs.any { (k, v) ->
                    k.lowercase().contains(q) || v.lowercase().contains(q)
                } ||
                guide.commonMistakes.any { it.lowercase().contains(q) } ||
                guide.steps.any {
                    it.title.lowercase().contains(q) ||
                        it.content.lowercase().contains(q) ||
                        it.detailedChecklist.any { c -> c.lowercase().contains(q) }
                }
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .imePadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
        // Header + Overall Workshop Mastery Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("academy_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Emerald400.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Học Viện Thực Chiến In & Quảng Cáo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Giáo trình cầm tay chỉ việc ${guides.size} phần mềm & máy xưởng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Mastery Progress Bar
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiến độ học nghề quảng cáo: $totalCompletedSteps / $totalAllSteps bước",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "$masteryPercent%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (masteryPercent == 100) Emerald400 else Cyan400
                            )
                        }
                        LinearProgressIndicator(
                            progress = { totalCompletedSteps.toFloat() / totalAllSteps.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (masteryPercent == 100) Emerald400 else Cyan400,
                            trackColor = Slate800
                        )
                    }
                }
            }
        }
        } // end header item

        item {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Tìm giáo trình: Corel, Maintop, PhotoPrint, UV, CNC...", fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Tìm kiếm", tint = Cyan400) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa từ khóa",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("academy_search_input"),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cyan400,
                unfocusedBorderColor = Slate700,
                focusedContainerColor = Slate900,
                unfocusedContainerColor = Slate900,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
        } // end search item

        item {
        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val selected = selectedCategory == category
                Surface(
                    onClick = { selectedCategory = category },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) Cyan900 else Slate900,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(if (selected) Cyan400 else Slate800)
                    ),
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .testTag("academy_filter_$category")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        } // end category filter item

        // Guides List or Empty State
        if (filteredGuides.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Không tìm thấy giáo trình phù hợp",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Thử tìm với từ khóa như Corel, Maintop, PhotoPrint, UV, CNC hoặc LED.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "Tất Cả"
                            },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text("Hiển Thị Tất Cả", color = Cyan400, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredGuides, key = { it.id }) { guide ->
                GuideCardItem(
                    guide = guide,
                    completedSteps = completedGuideSteps,
                    onOpen = { viewModel.selectGuide(guide) }
                )
            }
        }
    }

    // Masterclass Detail Dialog
    selectedGuide?.let { guide ->
        AcademyLessonDialog(
            guide = guide,
            completedSteps = completedGuideSteps,
            onToggleStep = { stepNumber -> viewModel.toggleGuideStep(guide.id, stepNumber) },
            onMarkAllCompleted = { viewModel.markGuideCompleted(guide) },
            onResetProgress = { viewModel.resetGuideProgress(guide.id) },
            onCopyLesson = {
                val fullText = buildFullGuideCopyText(guide)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(guide.title, fullText))
                viewModel.notifyMessage("Đã sao chép toàn bộ giáo trình '${guide.title}'")
            },
            onAskAiTutor = { viewModel.askAiAboutGuide(guide) },
            onDismiss = { viewModel.selectGuide(null) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GuideCardItem(
    guide: AcademyGuide,
    completedSteps: Set<String>,
    onOpen: () -> Unit
) {
    val icon = when (guide.iconName) {
        "print" -> Icons.Default.Print
        "design" -> Icons.Default.Brush
        "uv" -> Icons.Default.AutoAwesome
        "cnc" -> Icons.Default.PrecisionManufacturing
        "settings" -> Icons.Default.Settings
        "bolt" -> Icons.Default.Bolt
        else -> Icons.Default.Build
    }

    val accentColor = when (guide.iconName) {
        "design" -> Amber400
        "uv" -> Purple400
        "cnc" -> Emerald400
        else -> Cyan400
    }

    val doneSteps = guide.steps.count { "${guide.id}_${it.stepNumber}" in completedSteps }
    val isMastered = doneSteps == guide.steps.size && guide.steps.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guide_card_${guide.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (isMastered) Emerald400.copy(alpha = 0.5f) else Slate800)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate700))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = guide.category, style = MaterialTheme.typography.labelSmall, color = accentColor)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Amber400.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizon
