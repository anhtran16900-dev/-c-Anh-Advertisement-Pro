package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AcademyGuide
import com.example.model.GuideStep
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan900
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Purple400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

private enum class LessonSectionFilter(val label: String) {
    ALL("Tất Cả"),
    STEPS("Từng Bước"),
    SPECS_ERRORS("Thông Số & Lỗi"),
    PRACTICE("Bài Tập")
}

@Composable
fun AcademyLessonDialog(
    guide: AcademyGuide,
    completedSteps: Set<String>,
    onToggleStep: (Int) -> Unit,
    onMarkAllCompleted: () -> Unit,
    onResetProgress: () -> Unit,
    onCopyLesson: () -> Unit,
    onAskAiTutor: () -> Unit,
    onDismiss: () -> Unit
) {
    val totalSteps = guide.steps.size.coerceAtLeast(1)
    val doneCount = guide.steps.count { "${guide.id}_${it.stepNumber}" in completedSteps }
    val progressRatio = doneCount.toFloat() / totalSteps.toFloat()
    val isAllDone = doneCount == guide.steps.size && guide.steps.isNotEmpty()

    var activeFilter by remember(guide.id) { mutableStateOf(LessonSectionFilter.ALL) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 18.dp)
                .testTag("guide_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate700))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = guide.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${guide.category} • ${guide.level}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Cyan400
                            )
                            Surface(
                                color = Amber400.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Amber400,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${guide.durationMinutes} phút",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Amber400
                                    )
                                }
                            }
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onCopyLesson,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("guide_copy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép toàn bộ giáo trình",
                                tint = Cyan400
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("guide_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Mastery Progress Bar
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Slate800))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAllDone) "✅ Đã thành thạo toàn bộ bài học!" else "Tiến độ thực hành: $doneCount / ${guide.steps.size} bước",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllDone) Emerald400 else Color.White
                            )
                            Text(
                                text = "${(progressRatio * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllDone) Emerald400 else Cyan400
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isAllDone) Emerald400 else Cyan400,
                            trackColor = Slate800
                        )
                    }
                }

                // Section Filter Pills
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LessonSectionFilter.entries.forEach { section ->
                        val selected = activeFilter == section
                        Surface(
                            onClick = { activeFilter = section },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) Cyan900 else Slate950,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (selected) Cyan400 else Slate800)
                            ),
                            modifier = Modifier
                                .heightIn(min = 40.dp)
                                .testTag("lesson_tab_${section.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = section.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Cyan400 else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Main Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Prerequisites
                    if (activeFilter == LessonSectionFilter.ALL && guide.prerequisites.isNotBlank()) {
                        LessonInfoCard(
                            title = "🧰 Chuẩn Bị Trước Khi Thực Hành",
                            accentColor = Cyan400,
                            body = guide.prerequisites
                        )
                    }

                    // Step-by-Step Curriculum
                    if (activeFilter == LessonSectionFilter.ALL || activeFilter == LessonSectionFilter.STEPS) {
                        guide.steps.forEach { step ->
                            val isStepDone = "${guide.id}_${step.stepNumber}" in completedSteps
                            LessonStepCard(
                                guideId = guide.id,
                                step = step,
                                isDone = isStepDone,
                                onToggleDone = { onToggleStep(step.stepNumber) }
                            )
                        }
                    }

                    // Shortcuts & Golden Specs Table
                    if ((activeFilter == LessonSectionFilter.ALL || activeFilter == LessonSectionFilter.SPECS_ERRORS) &&
                        guide.shortcutsAndSpecs.isNotEmpty()
                    ) {
                        ShortcutsAndSpecsCard(items = guide.shortcutsAndSpecs)
                    }

                    // Common Mistakes & Fixes
                    if ((activeFilter == LessonSectionFilter.ALL || activeFilter == LessonSectionFilter.SPECS_ERRORS) &&
                        guide.commonMistakes.isNotEmpty()
                    ) {
                        CommonMistakesCard(mistakes = guide.commonMistakes)
                    }

                    // Practice Exercise
                    if ((activeFilter == LessonSectionFilter.ALL || activeFilter == LessonSectionFilter.PRACTICE) &&
                        guide.practiceExercise.isNotBlank()
                    ) {
                        LessonInfoCard(
                            title = "🛠️ Bài Tập Thực Hành Tốt Nghiệp Bài",
                            accentColor = Emerald400,
                            body = guide.practiceExercise
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isAllDone) {
                            OutlinedButton(
                                onClick = onResetProgress,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("guide_reset_progress_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Học Lại",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Button(
                                onClick = onMarkAllCompleted,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("guide_complete_all_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Emerald400,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hoàn Thành Bài",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onAskAiTutor,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("guide_ask_ai_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple400.copy(alpha = 0.22f),
                                contentColor = Purple400
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hỏi Gia Sư AI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("guide_confirm_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan400, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Đóng Giáo Trình", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonStepCard(
    guideId: String,
    step: GuideStep,
    isDone: Boolean,
    onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate950),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (isDone) Emerald400.copy(alpha = 0.6f) else Slate800)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Step Title + Interactive Done Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (isDone) Emerald400 else Cyan400,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Bước ${step.stepNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDone) Emerald400.copy(alpha = 0.16f) else Slate900,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(if (isDone) Emerald400 else Slate700)
                    ),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onToggleDone)
                        .testTag("guide_step_toggle_${guideId}_${step.stepNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isDone) "Đã thực hành xong bước ${step.stepNumber}" else "Đánh dấu hoàn thành bước ${step.stepNumber}",
                            tint = if (isDone) Emerald400 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDone) "Đã xong" else "Đánh dấu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDone) Emerald400 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = step.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Detailed Sub-Checklist
            if (step.detailedChecklist.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate900)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "📋 Thao Tác Chi Tiết Từng Nhấp Chuột:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Cyan400
                    )
                    step.detailedChecklist.forEachIndexed { idx, action ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Cyan400.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan400
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = action,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f),
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (step.expectedResult.isNotBlank()) {
                HighlightBox(
                    emoji = "✅",
                    label = "Kiểm tra đạt chuẩn:",
                    text = step.expectedResult,
                    accentColor = Emerald400
                )
            }

            if (step.proTip.isNotBlank()) {
                HighlightBox(
                    emoji = "💡",
                    label = "Bí quyết thợ cả:",
                    text = step.proTip,
                    accentColor = Cyan400
                )
            }

            if (step.techNote.isNotBlank()) {
                HighlightBox(
                    emoji = "⚠️",
                    label = "Cảnh báo kỹ thuật:",
                    text = step.techNote,
                    accentColor = Amber400
                )
            }
        }
    }
}

@Composable
private fun HighlightBox(
    emoji: String,
    label: String,
    text: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.09f))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "$emoji $label",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun LessonInfoCard(
    title: String,
    accentColor: Color,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate950),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(accentColor.copy(alpha = 0.45f))
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.92f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ShortcutsAndSpecsCard(items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate950),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Cyan400.copy(alpha = 0.4f)))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⌨️ Bảng Phím Tắt & Thông Số Vàng Cần Thuộc Lòng",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Cyan400
            )
            items.forEach { (key, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Cyan900,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = key,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.weight(1f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CommonMistakesCard(mistakes: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate950),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Amber400.copy(alpha = 0.45f)))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🚨 Lỗi Học Viên Hay Mắc & Cách Sửa Tại Xưởng",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Amber400
            )
            mistakes.forEachIndexed { idx, mistake ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Amber400.copy(alpha = 0.08f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Amber400,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = mistake,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.92f),
                        lineHeight = 19.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
