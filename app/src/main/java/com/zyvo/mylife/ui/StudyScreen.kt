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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.data.Subject

@Composable
private fun TimerButton(
    label: String,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    onClick: () -> Unit
) {
    val p = LocalPalette.current
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (filled) p.accent else p.bg)
            .border(1.dp, if (filled) p.accent else p.line, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = if (filled) (if (p.dark) p.bg else Color.White) else p.ink
        )
    }
}

@Composable
fun StudyScreen(
    store: AppStore,
    secondsLeft: Int,
    mode: String,
    running: Boolean,
    sessionSeconds: Int,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onCheckout: () -> Unit
) {
    val p = LocalPalette.current
    var showPick by remember { mutableStateOf(false) }
    var showAddSubject by remember { mutableStateOf(false) }
    var editSubject by remember { mutableStateOf<Subject?>(null) }
    var deleteSubject by remember { mutableStateOf<Subject?>(null) }
    var confirmReset by remember { mutableStateOf(false) }
    var confirmCheckout by remember { mutableStateOf(false) }

    val total = if (mode == "focus") 25 * 60 else 5 * 60
    val elapsed = 1f - secondsLeft.toFloat() / total.toFloat()
    val focusTask = store.selectedTask()
    val mins = (sessionSeconds + 30) / 60

    ScreenColumn {
        PageHeader(
            kicker = "Ruang belajar",
            title = if (mode == "focus") "Fokus tanpa distraksi" else "Waktunya istirahat",
            subtitle = "25 menit fokus, 5 menit jeda."
        )

        SectionCard {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (mode == "focus") "FOKUS" else "ISTIRAHAT",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (mode == "focus") p.accent else p.blue
                )
                Text(
                    String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 62.sp),
                    color = p.ink
                )
                Bar(elapsed, if (mode == "focus") p.accent else p.blue, p.line, 8)
                Spacer(Modifier.size(12.dp))
                Text(
                    if (focusTask != null) "🎯 " + focusTask.title else "Belum ada tugas dipilih",
                    style = MaterialTheme.typography.bodyMedium,
                    color = p.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimerButton(
                        if (running) "Jeda" else "Mulai",
                        Modifier.weight(1f),
                        filled = true,
                        onClick = onToggle
                    )
                    TimerButton("Ambil tugas", Modifier.weight(1f)) { showPick = true }
                }
                Spacer(Modifier.size(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimerButton("Check-out", Modifier.weight(1f)) {
                        if (sessionSeconds >= 60 && mode == "focus") confirmCheckout = true else onCheckout()
                    }
                    TimerButton("Reset", Modifier.weight(1f)) {
                        if (sessionSeconds > 0) confirmReset = true else onReset()
                    }
                }
                Spacer(Modifier.size(10.dp))
                Text(
                    "Mulai → kerjakan → ambil tugas kapan saja → check-out untuk menyimpan sesi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = p.ink2,
                    textAlign = TextAlign.Center
                )
            }
        }

        SectionCard(title = "Statistik fokus") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBlock(store.focusToday().toString() + "m", "Hari ini", Modifier.weight(1f), p.accent)
                StatBlock(store.focusWeek().toString() + "m", "7 hari terakhir", Modifier.weight(1f))
                StatBlock(mins.toString() + "m", "Sesi ini", Modifier.weight(1f))
            }
        }

        SectionCard(
            title = "Mata pelajaran",
            action = { SmallAction("+ Tambah") { showAddSubject = true } }
        ) {
            if (store.subjects.isEmpty()) {
                EmptyState("Belum ada mata pelajaran.")
            } else {
                store.subjects.forEach { s ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { editSubject = s }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(s.color))
                        )
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                s.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                s.done.toString() + " / " + s.target + " sesi",
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                            Spacer(Modifier.size(6.dp))
                            Bar(
                                s.done.toFloat() / s.target.toFloat().coerceAtLeast(1f),
                                Color(s.color),
                                p.line,
                                6
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        SmallAction("+1") { store.incrementSubject(s.id) }
                    }
                }
            }
        }
    }

    if (showPick) {
        PickTaskDialog(
            tasks = store.tasks.filter { !it.done },
            selectedId = store.selectedTaskId,
            onDismiss = { showPick = false },
            onPick = { id -> store.selectTask(id) }
        )
    }
    if (showAddSubject || editSubject != null) {
        SubjectDialog(
            subject = editSubject,
            onDismiss = {
                showAddSubject = false
                editSubject = null
            },
            onSave = { s -> store.upsertSubject(s) },
            onDelete = { id -> deleteSubject = store.subjects.firstOrNull { it.id == id } }
        )
    }
    deleteSubject?.let { s ->
        ConfirmDialog(
            "Hapus pelajaran?",
            "\"" + s.name + "\" akan dihapus permanen.",
            onConfirm = { store.deleteSubject(s.id) },
            onDismiss = { deleteSubject = null }
        )
    }
    if (confirmReset) {
        ConfirmDialog(
            "Reset timer?",
            "Sesi yang belum di-check-out tidak akan dicatat.",
            onConfirm = onReset,
            onDismiss = { confirmReset = false }
        )
    }
    if (confirmCheckout) {
        ConfirmDialog(
            "Check-out sesi?",
            "Sesi " + mins + " menit akan disimpan ke riwayat dan statistik fokus.",
            onConfirm = onCheckout,
            onDismiss = { confirmCheckout = false }
        )
    }
}
