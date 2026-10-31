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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.data.TaskItem

@Composable
fun TasksScreen(store: AppStore) {
    val p = LocalPalette.current
    var filter by remember { mutableStateOf("all") }
    var showAdd by remember { mutableStateOf(false) }
    var editTask by remember { mutableStateOf<TaskItem?>(null) }
    var deleteTask by remember { mutableStateOf<TaskItem?>(null) }
    var confirmToggle by remember { mutableStateOf<TaskItem?>(null) }

    val visible = store.tasks.filter {
        when (filter) {
            "active" -> !it.done
            "done" -> it.done
            else -> true
        }
    }
    val activeCount = store.tasks.count { !it.done }

    ScreenColumn {
        PageHeader(
            kicker = "Daftar kerja",
            title = "Tugas",
            subtitle = activeCount.toString() + " aktif · " + (store.tasks.size - activeCount) + " selesai",
            action = { AccentAction("+ Tugas") { showAdd = true } }
        )

        ChoiceChips(
            listOf("all" to "Semua", "active" to "Aktif", "done" to "Selesai"),
            filter
        ) { filter = it }

        SectionCard {
            if (visible.isEmpty()) {
                EmptyState(if (store.tasks.isEmpty()) "Belum ada tugas. Tambahkan satu." else "Tidak ada tugas di filter ini.")
            } else {
                visible.forEach { t ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { editTask = t }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (t.done) p.accent else Color.Transparent)
                                .border(
                                    1.5.dp,
                                    if (t.done) p.accent else priorityColor(p, t.priority),
                                    CircleShape
                                )
                                .clickable { confirmToggle = t },
                            contentAlignment = Alignment.Center
                        ) {
                            if (t.done) Text(
                                "✓",
                                color = if (p.dark) p.bg else Color.White,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                t.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (t.done) p.ink2 else p.ink,
                                textDecoration = if (t.done) TextDecoration.LineThrough else null,
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
                            color = if (t.done) p.ink2 else priorityColor(p, t.priority)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "⋯",
                            color = p.ink2,
                            modifier = Modifier
                                .clickable { editTask = t }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        SectionCard(title = "Alur singkat") {
            Text(
                "1. Tambah tugas dengan prioritas.\n2. Buka tab Belajar, tekan Ambil tugas.\n3. Check-out sesi supaya tercatat di riwayat.",
                style = MaterialTheme.typography.bodyMedium,
                color = p.ink2
            )
        }
    }

    if (showAdd || editTask != null) {
        TaskDialog(
            task = editTask,
            onDismiss = {
                showAdd = false
                editTask = null
            },
            onSave = { t -> store.upsertTask(t) },
            onDelete = { id -> deleteTask = store.tasks.firstOrNull { it.id == id } }
        )
    }
    deleteTask?.let { t ->
        ConfirmDialog(
            "Hapus tugas?",
            "\"" + t.title + "\" akan dihapus permanen.",
            onConfirm = { store.deleteTask(t.id) },
            onDismiss = { deleteTask = null }
        )
    }
    confirmToggle?.let { t ->
        val markDone = !t.done
        ConfirmDialog(
            if (markDone) "Selesaikan tugas?" else "Buka tugas lagi?",
            "\"" + t.title + "\" akan ditandai " + (if (markDone) "selesai" else "belum selesai") + ".",
            onConfirm = { store.setTaskDone(t.id, markDone) },
            onDismiss = { confirmToggle = null }
        )
    }
}
