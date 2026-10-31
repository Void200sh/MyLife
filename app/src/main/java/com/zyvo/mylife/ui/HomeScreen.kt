package com.zyvo.mylife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.data.Dates
import com.zyvo.mylife.data.Habit
import com.zyvo.mylife.data.TaskItem

@Composable
fun HomeScreen(
    store: AppStore,
    onOpenTheme: () -> Unit,
    onOpenTasks: () -> Unit
) {
    val p = LocalPalette.current
    var showAddHabit by remember { mutableStateOf(false) }
    var editHabit by remember { mutableStateOf<Habit?>(null) }
    var deleteHabit by remember { mutableStateOf<Habit?>(null) }
    var confirmTask by remember { mutableStateOf<TaskItem?>(null) }
    var showExercise by remember { mutableStateOf(false) }

    val activeTasks = store.tasks.filter { !it.done }
        .sortedBy {
            when (it.priority) {
                "high" -> 0
                "mid" -> 1
                else -> 2
            }
        }
        .take(4)
    val exDays = store.exerciseDaysThisWeek()
    val exTarget = store.exerciseTarget

    ScreenColumn {
        PageHeader(
            kicker = Dates.todayHuman(),
            title = Dates.greeting() + " 👋",
            subtitle = "Mulai kecil, tetap jalan.",
            action = {
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(p.card)
                        .border(1.dp, p.line, CircleShape)
                        .clickable { onOpenTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 20.sp)
                }
            }
        )

        SectionCard {
            Text(
                "\u201C" + store.quote + "\u201D",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = p.ink
            )
        }

        SectionCard {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBlock(store.tasks.count { !it.done }.toString(), "Tugas aktif", Modifier.weight(1f))
                StatBlock(store.focusToday().toString() + "m", "Fokus hari ini", Modifier.weight(1f))
                StatBlock(exDays.toString() + "/" + exTarget, "Olahraga", Modifier.weight(1f))
            }
        }

        SectionCard(
            title = "Kebiasaan hari ini",
            action = { SmallAction("+ Tambah") { showAddHabit = true } }
        ) {
            if (store.habits.isEmpty()) {
                EmptyState("Belum ada kebiasaan. Tambahkan satu untuk mulai.")
            } else {
                store.habits.forEach { h ->
                    val done = h.log.containsKey(Dates.key())
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { editHabit = h }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(p.card)
                                .border(1.dp, p.line, RoundedCornerShape(11.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(h.emoji, fontSize = 17.sp)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                h.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                if (h.exercise) "Olahraga · " + store.exerciseMinutes + " menit"
                                else if (store.habitStreak(h) > 0) "🔥 " + store.habitStreak(h) + " hari"
                                else "Belum dimulai",
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                        }
                        Box(
                            Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(if (done) p.accent else p.card)
                                .border(1.5.dp, if (done) p.accent else p.line, CircleShape)
                                .clickable { store.toggleHabit(h.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Text("✓", color = if (p.dark) p.bg else androidx.compose.ui.graphics.Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        SectionCard(title = "Olahraga", action = { SmallAction("Atur") { showExercise = true } }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        exDays.toString() + " / " + exTarget + " hari aktif",
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
            Bar(
                exDays.toFloat() / exTarget.toFloat().coerceAtLeast(1f),
                p.blue,
                p.line
            )
        }

        SectionCard(title = "Tugas prioritas", action = { SmallAction("Lihat semua") { onOpenTasks() } }) {
            if (activeTasks.isEmpty()) {
                EmptyState("Semua tugas beres. Tambah tugas baru di tab Tugas.")
            } else {
                activeTasks.forEach { t ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { confirmTask = t }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, priorityColor(p, t.priority), CircleShape)
                        )
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                t.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                t.cat + if (t.due.isNotBlank()) " · " + t.due else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                        }
                        Text(
                            priorityLabel(t.priority),
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor(p, t.priority)
                        )
                    }
                }
            }
        }
    }

    if (showAddHabit || editHabit != null) {
        HabitDialog(
            habit = editHabit,
            onDismiss = {
                showAddHabit = false
                editHabit = null
            },
            onSave = { h ->
                if (editHabit != null) store.updateHabit(h) else store.addHabit(h.name, h.emoji, h.exercise)
            },
            onDelete = { id ->
                deleteHabit = store.habits.firstOrNull { it.id == id }
            }
        )
    }
    deleteHabit?.let { h ->
        ConfirmDialog(
            "Hapus kebiasaan?",
            "\"" + h.name + "\" akan dihapus permanen.",
            onConfirm = { store.deleteHabit(h.id) },
            onDismiss = { deleteHabit = null }
        )
    }
    confirmTask?.let { t ->
        ConfirmDialog(
            "Selesaikan tugas?",
            "\"" + t.title + "\" akan ditandai selesai.",
            onConfirm = { store.setTaskDone(t.id, true) },
            onDismiss = { confirmTask = null }
        )
    }
    if (showExercise) {
        ExerciseDialog(
            target = store.exerciseTarget,
            minutes = store.exerciseMinutes,
            onDismiss = { showExercise = false },
            onSave = { target, minutes -> store.setExercise(target, minutes) }
        )
    }
}
