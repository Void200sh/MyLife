package com.zyvo.mylife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.data.Dates
import com.zyvo.mylife.data.Goal

@Composable
fun ProgressScreen(store: AppStore, onOpenTheme: () -> Unit) {
    val p = LocalPalette.current
    var showExercise by remember { mutableStateOf(false) }
    var showAddGoal by remember { mutableStateOf(false) }
    var editGoal by remember { mutableStateOf<Goal?>(null) }
    var deleteGoal by remember { mutableStateOf<Goal?>(null) }
    var confirmClear by remember { mutableStateOf(false) }

    val moods = listOf("😄" to "Senang", "🙂" to "Baik", "😐" to "Biasa", "😕" to "Kurang", "😫" to "Berat")
    val todayMood = store.moods[Dates.key()]
    val exDays = store.exerciseDaysThisWeek()
    val week = Dates.last7()
    val maxMin = maxOf(store.exerciseMinutes * 2, 60)

    ScreenColumn {
        PageHeader(
            kicker = "Pantau diri",
            title = "Progres",
            subtitle = "Kebiasaan, target, dan riwayat.",
            action = { AccentAction("Tema") { onOpenTheme() } }
        )

        SectionCard(title = "Mood hari ini") {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                moods.forEach { (emoji, label) ->
                    val on = todayMood == emoji
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (on) p.accentSoft else p.bg)
                            .border(
                                if (on) 1.5.dp else 1.dp,
                                if (on) p.accent else p.line,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { store.setMood(emoji) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(emoji, fontSize = 22.sp)
                        Spacer(Modifier.height(3.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall, color = p.ink2)
                    }
                }
            }
            Text(
                if (todayMood == null) "Pilih mood untuk hari ini."
                else "Mood tercatat: " + todayMood,
                style = MaterialTheme.typography.bodySmall,
                color = p.ink2
            )
        }

        SectionCard(title = "Olahraga", action = { SmallAction("Atur") { showExercise = true } }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        exDays.toString() + " / " + store.exerciseTarget + " hari aktif",
                        style = MaterialTheme.typography.titleMedium,
                        color = p.ink
                    )
                    Text(
                        "🔥 " + store.exerciseStreak() + " hari beruntun",
                        style = MaterialTheme.typography.bodySmall,
                        color = p.ink2
                    )
                }
                Text(
                    store.exerciseMinutes.toString() + " mnt/sesi",
                    style = MaterialTheme.typography.bodySmall,
                    color = p.ink2
                )
            }
            Bar(exDays.toFloat() / store.exerciseTarget.toFloat().coerceAtLeast(1f), p.blue, p.line)
        }

        SectionCard(title = "7 hari terakhir") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                week.forEach { (key, label) ->
                    val mins = store.exerciseMinutesOn(key)
                    val frac = (mins.toFloat() / maxMin.toFloat()).coerceIn(0f, 1f)
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (mins > 0) mins.toString() else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = p.ink2,
                            fontSize = 9.sp
                        )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height((4f + frac * 68f).dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (mins > 0) p.accent else p.line)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall, color = p.ink2, fontSize = 10.sp)
                    }
                }
            }
            Text(
                "Total minggu ini: " + week.sumOf { store.exerciseMinutesOn(it.first) } + " menit",
                style = MaterialTheme.typography.bodySmall,
                color = p.ink2
            )
        }

        SectionCard(
            title = "Target jangka panjang",
            action = { SmallAction("+ Tambah") { showAddGoal = true } }
        ) {
            if (store.goals.isEmpty()) {
                EmptyState("Belum ada target jangka panjang.")
            } else {
                store.goals.forEach { g ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { editGoal = g }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                g.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                g.current.toInt().toString() + " / " + g.target.toInt() + " " + g.unit,
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                        Bar(
                            (g.current / g.target.coerceAtLeast(1.0)).toFloat(),
                            Color(g.color),
                            p.line,
                            7
                        )
                    }
                }
            }
        }

        SectionCard(
            title = "Riwayat aktivitas",
            action = { SmallAction("Bersihkan") { confirmClear = true } }
        ) {
            if (store.history.isEmpty()) {
                EmptyState("Belum ada riwayat. Aktivitas akan muncul di sini.")
            } else {
                store.history.take(12).forEach { h ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(p.bg)
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HistoryDot(h.type)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                h.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (h.detail.isNotBlank()) {
                                Text(h.detail, style = MaterialTheme.typography.bodySmall, color = p.ink2)
                            }
                        }
                        Text(
                            Dates.historyStamp(h.at),
                            style = MaterialTheme.typography.bodySmall,
                            color = p.ink2
                        )
                    }
                }
            }
        }

        SectionCard(
            title = "Tampilan",
            action = { SmallAction("Ganti") { onOpenTheme() } }
        ) {
            Text(store.themeId.label, style = MaterialTheme.typography.titleMedium, color = p.ink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                store.themeId.let { id ->
                    val t = id.palette
                    Box(Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(t.bg).border(1.dp, t.line, RoundedCornerShape(8.dp)))
                    Box(Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(t.card).border(1.dp, t.line, RoundedCornerShape(8.dp)))
                    Box(Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(t.accent))
                }
            }
            Text(
                "5 tema: Soft Sage, Paper, Porcelain, Chrome, Tactical.",
                style = MaterialTheme.typography.bodySmall,
                color = p.ink2
            )
        }
    }

    if (showExercise) {
        ExerciseDialog(
            target = store.exerciseTarget,
            minutes = store.exerciseMinutes,
            onDismiss = { showExercise = false },
            onSave = { target, minutes -> store.setExercise(target, minutes) }
        )
    }
    if (showAddGoal || editGoal != null) {
        GoalDialog(
            goal = editGoal,
            onDismiss = {
                showAddGoal = false
                editGoal = null
            },
            onSave = { g -> store.upsertGoal(g) },
            onDelete = { id -> deleteGoal = store.goals.firstOrNull { it.id == id } }
        )
    }
    deleteGoal?.let { g ->
        ConfirmDialog(
            "Hapus target?",
            "\"" + g.title + "\" akan dihapus permanen.",
            onConfirm = { store.deleteGoal(g.id) },
            onDismiss = { deleteGoal = null }
        )
    }
    if (confirmClear) {
        ConfirmDialog(
            "Bersihkan riwayat?",
            "Semua riwayat aktivitas akan dihapus.",
            onConfirm = {
                while (store.history.isNotEmpty()) store.history.removeAt(store.history.lastIndex)
                store.save()
            },
            onDismiss = { confirmClear = false }
        )
    }
}
