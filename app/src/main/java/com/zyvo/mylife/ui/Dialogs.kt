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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.zyvo.mylife.data.AppThemeId
import com.zyvo.mylife.data.Goal
import com.zyvo.mylife.data.Habit
import com.zyvo.mylife.data.Subject
import com.zyvo.mylife.data.TaskItem

@Composable
fun HabitDialog(
    habit: Habit?,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit,
    onDelete: (String) -> Unit
) {
    val p = LocalPalette.current
    var name by remember(habit?.id) { mutableStateOf(habit?.name ?: "") }
    var emoji by remember(habit?.id) { mutableStateOf(habit?.emoji ?: "✅") }
    var exercise by remember(habit?.id) { mutableStateOf(habit?.exercise ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (habit == null) "Kebiasaan baru" else "Ubah kebiasaan") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LabeledField("Nama", name, { name = it }, "Misal: Bangun jam 6")
                Text("Ikon", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                EmojiRow(listOf("🌅", "💧", "📖", "🏃", "🧘", "🥗", "✅", "🌙"), emoji) { emoji = it }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(p.bg)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Olahraga", style = MaterialTheme.typography.titleMedium, color = p.ink)
                        Text(
                            "Hitung menit per sesi",
                            style = MaterialTheme.typography.bodySmall,
                            color = p.ink2
                        )
                    }
                    Switch(checked = exercise, onCheckedChange = { exercise = it })
                }
                if (habit != null) {
                    TextButton(
                        onClick = {
                            onDelete(habit.id)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus kebiasaan", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val n = name.ifBlank { "Kebiasaan baru" }
                val base = habit ?: Habit(name = n, emoji = emoji, exercise = exercise)
                onSave(base.copy(name = n, emoji = emoji, exercise = exercise))
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun TaskDialog(
    task: TaskItem?,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit,
    onDelete: (String) -> Unit
) {
    val p = LocalPalette.current
    var title by remember(task?.id) { mutableStateOf(task?.title ?: "") }
    var priority by remember(task?.id) { mutableStateOf(task?.priority ?: "mid") }
    var cat by remember(task?.id) { mutableStateOf(task?.cat ?: "Umum") }
    var due by remember(task?.id) { mutableStateOf(task?.due ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Tugas baru" else "Ubah tugas") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LabeledField("Judul", title, { title = it }, "Apa yang mau dikerjakan?")
                Text("Prioritas", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                ChoiceChips(
                    listOf("high" to "Tinggi", "mid" to "Sedang", "low" to "Rendah"),
                    priority
                ) { priority = it }
                Text("Kategori", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                ChoiceChips(
                    listOf(
                        "Umum" to "Umum",
                        "Belajar" to "Belajar",
                        "Pribadi" to "Pribadi",
                        "Belanja" to "Belanja",
                        "Kesehatan" to "Sehat"
                    ),
                    cat
                ) { cat = it }
                LabeledField("Tenggat", due, { due = it }, "Hari ini · 19:00")
                if (task != null) {
                    TextButton(
                        onClick = {
                            onDelete(task.id)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus tugas", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val t = title.ifBlank { "Tugas baru" }
                val base = task ?: TaskItem(title = t)
                onSave(base.copy(title = t, priority = priority, cat = cat, due = due))
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun SubjectDialog(
    subject: Subject?,
    onDismiss: () -> Unit,
    onSave: (Subject) -> Unit,
    onDelete: (String) -> Unit
) {
    val p = LocalPalette.current
    var name by remember(subject?.id) { mutableStateOf(subject?.name ?: "") }
    var target by remember(subject?.id) { mutableStateOf((subject?.target ?: 4).toString()) }
    var done by remember(subject?.id) { mutableStateOf((subject?.done ?: 0).toString()) }
    var color by remember(subject?.id) { mutableStateOf(subject?.color ?: 0xFF5E8B6A) }
    val colors = listOf(0xFF5E8B6A, 0xFF5B7FA6, 0xFFE8A13C, 0xFF9B7BB8, 0xFFA64B2A)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (subject == null) "Mata pelajaran baru" else "Ubah pelajaran") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LabeledField("Nama", name, { name = it }, "Misal: Matematika")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledField("Target", target, { target = it }, numeric = true, modifier = Modifier.weight(1f))
                    LabeledField("Selesai", done, { done = it }, numeric = true, modifier = Modifier.weight(1f))
                }
                Text("Warna", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { c ->
                        Box(
                            Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .border(
                                    if (c == color) 3.dp else 1.dp,
                                    if (c == color) p.ink else p.line,
                                    CircleShape
                                )
                                .clickable { color = c }
                        )
                    }
                }
                if (subject != null) {
                    TextButton(
                        onClick = {
                            onDelete(subject.id)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus pelajaran", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val n = name.ifBlank { "Pelajaran" }
                val base = subject ?: Subject(name = n)
                onSave(
                    base.copy(
                        name = n,
                        target = target.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        done = done.toIntOrNull()?.coerceAtLeast(0) ?: 0,
                        color = color
                    )
                )
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun TxDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, type: String, emoji: String) -> Unit
) {
    val p = LocalPalette.current
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("out") }
    var emoji by remember { mutableStateOf("💸") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat transaksi") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Jenis", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                ChoiceChips(
                    listOf("in" to "Pemasukan", "out" to "Pengeluaran"),
                    type
                ) { type = it }
                LabeledField("Nama", title, { title = it }, "Misal: Makan siang")
                LabeledField("Nominal", amount, { amount = it }, "25000", numeric = true)
                Text("Ikon", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                EmojiRow(listOf("💰", "🍜", "🛵", "📚", "🎮", "💡", "🛒", "💸"), emoji) { emoji = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val a = amount.toDoubleOrNull() ?: 0.0
                if (a > 0.0) {
                    onSave(title.ifBlank { "Transaksi" }, a, type, emoji)
                    onDismiss()
                }
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun GoalDialog(
    goal: Goal?,
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit,
    onDelete: (String) -> Unit
) {
    val p = LocalPalette.current
    var title by remember(goal?.id) { mutableStateOf(goal?.title ?: "") }
    var target by remember(goal?.id) { mutableStateOf((goal?.target ?: 10.0).toInt().toString()) }
    var current by remember(goal?.id) { mutableStateOf((goal?.current ?: 0.0).toInt().toString()) }
    var unit by remember(goal?.id) { mutableStateOf(goal?.unit ?: "sesi") }
    var color by remember(goal?.id) { mutableStateOf(goal?.color ?: 0xFF5E8B6A) }
    val colors = listOf(0xFF5E8B6A, 0xFF5B7FA6, 0xFFE8A13C, 0xFF9B7BB8, 0xFFA64B2A)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (goal == null) "Target baru" else "Ubah target") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LabeledField("Nama target", title, { title = it }, "Misal: Baca 12 buku")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledField("Target", target, { target = it }, numeric = true, modifier = Modifier.weight(1f))
                    LabeledField("Sekarang", current, { current = it }, numeric = true, modifier = Modifier.weight(1f))
                }
                Text("Satuan", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                ChoiceChips(
                    listOf("buku" to "buku", "km" to "km", "jam" to "jam", "sesi" to "sesi"),
                    unit
                ) { unit = it }
                Text("Warna", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { c ->
                        Box(
                            Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .border(
                                    if (c == color) 3.dp else 1.dp,
                                    if (c == color) p.ink else p.line,
                                    CircleShape
                                )
                                .clickable { color = c }
                        )
                    }
                }
                if (goal != null) {
                    TextButton(
                        onClick = {
                            onDelete(goal.id)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus target", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val n = title.ifBlank { "Target baru" }
                val t = (target.toDoubleOrNull() ?: 10.0).coerceAtLeast(1.0)
                val c = (current.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                val base = goal ?: Goal(title = n)
                onSave(base.copy(title = n, target = t, current = c, unit = unit, color = color))
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun BudgetDialog(
    budget: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var value by remember { mutableStateOf(if (budget > 0) budget.toInt().toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Budget bulanan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LabeledField("Nominal", value, { value = it }, "2000000", numeric = true)
                Text(
                    "Budget dipakai untuk menghitung sisa uang bulan ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalPalette.current.ink2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(value.toDoubleOrNull() ?: 0.0)
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun ExerciseDialog(
    target: Int,
    minutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    val p = LocalPalette.current
    var t by remember { mutableStateOf(target.toString()) }
    var m by remember { mutableStateOf(minutes.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pengaturan olahraga") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LabeledField("Target hari / minggu", t, { t = it }, "4", numeric = true)
                LabeledField("Menit per sesi", m, { m = it }, "30", numeric = true)
                Text(
                    "Centang kebiasaan bertipe Olahraga di Beranda untuk mencatat menit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = p.ink2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    (t.toIntOrNull() ?: 4).coerceIn(1, 7),
                    (m.toIntOrNull() ?: 30).coerceAtLeast(5)
                )
                onDismiss()
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun PickTaskDialog(
    tasks: List<TaskItem>,
    selectedId: String?,
    onDismiss: () -> Unit,
    onPick: (String?) -> Unit
) {
    val p = LocalPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ambil tugas fokus") },
        text = {
            if (tasks.isEmpty()) {
                Text("Belum ada tugas aktif. Tambahkan dulu di tab Tugas.", color = p.ink2)
            } else {
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tasks.forEach { t ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (t.id == selectedId) p.accentSoft else p.bg)
                                .border(
                                    1.dp,
                                    if (t.id == selectedId) p.accent else p.line,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onPick(t.id)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(t.title, style = MaterialTheme.typography.titleMedium, color = p.ink)
                                Text(
                                    t.cat + if (t.due.isNotBlank()) " · " + t.due else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = p.ink2
                                )
                            }
                            Box(
                                Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(if (t.id == selectedId) p.accent else p.line)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onPick(null)
                onDismiss()
            }) { Text("Tanpa tugas") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Tutup") } }
    )
}

@Composable
fun ThemeDialog(
    current: AppThemeId,
    onDismiss: () -> Unit,
    onSelect: (AppThemeId) -> Unit
) {
    val p = LocalPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tampilan") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppThemeId.entries.forEach { id ->
                    val t = id.palette
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (id == current) p.accentSoft else p.card)
                            .border(
                                if (id == current) 1.5.dp else 1.dp,
                                if (id == current) p.accent else p.line,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSelect(id)
                                onDismiss()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, t.line, RoundedCornerShape(8.dp))
                        ) {
                            Box(Modifier.size(18.dp).background(t.bg))
                            Box(Modifier.size(18.dp).background(t.card))
                            Box(Modifier.size(18.dp).background(t.accent))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(id.label, style = MaterialTheme.typography.titleMedium, color = p.ink)
                            Text(
                                if (t.dark) "Gelap" else "Terang",
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                        }
                        Text(
                            if (id == current) "✓" else "",
                            color = p.accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}
