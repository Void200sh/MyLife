package com.zyvo.mylife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import java.util.Locale

fun formatRupiah(amount: Double): String =
    "Rp " + String.format(Locale("id", "ID"), "%,.0f", amount)

@Composable
fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        content()
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
fun PageHeader(
    kicker: String,
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    val p = LocalPalette.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                kicker.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = p.accent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                color = p.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = p.ink2)
            }
        }
        if (action != null) {
            Spacer(Modifier.width(12.dp))
            action()
        }
    }
}

@Composable
fun SectionCard(
    title: String? = null,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val p = LocalPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(p.card)
            .border(1.dp, p.line, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (title != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = p.ink,
                    modifier = Modifier.weight(1f)
                )
                action?.invoke()
            }
        }
        content()
    }
}

@Composable
fun SmallAction(label: String, onClick: () -> Unit) {
    val p = LocalPalette.current
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = p.ink2,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, p.line, RoundedCornerShape(50))
            .background(p.bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp)
    )
}

@Composable
fun AccentAction(label: String, onClick: () -> Unit) {
    val p = LocalPalette.current
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = if (p.dark) p.bg else Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(p.accent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Composable
fun EmptyState(text: String) {
    val p = LocalPalette.current
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(p.bg)
            .padding(horizontal = 14.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = p.ink2)
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Ya, lanjut",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    numeric: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun EmojiRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    val p = LocalPalette.current
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { emoji ->
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (emoji == selected) p.accentSoft else p.bg)
                    .border(1.5.dp, if (emoji == selected) p.accent else p.line, RoundedCornerShape(12.dp))
                    .clickable { onSelect(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ChoiceChips(
    options: List<Pair<String, String>>,
    selected: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = value == selected,
                onClick = { onSelect(value) },
                label = { Text(label, style = MaterialTheme.typography.bodySmall) }
            )
        }
    }
}

@Composable
fun Bar(
    fraction: Float,
    color: Color,
    track: Color,
    height: Int = 8
) {
    val f = fraction.coerceIn(0f, 1f)
    Box(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50))
            .background(track)
    ) {
        Box(
            Modifier
                .fillMaxWidth(f)
                .height(height.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

@Composable
fun StatBlock(value: String, label: String, modifier: Modifier = Modifier, color: Color? = null) {
    val p = LocalPalette.current
    Column(modifier) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            color = color ?: p.ink
        )
        Text(label, style = MaterialTheme.typography.bodySmall, color = p.ink2)
    }
}

@Composable
fun HistoryDot(type: String) {
    val p = LocalPalette.current
    val color = when (type) {
        "task" -> p.amber
        "exercise" -> p.blue
        "focus" -> p.accent
        "study" -> p.purple
        "mood" -> p.amber
        else -> p.accent
    }
    Box(
        Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

fun priorityColor(p: Palette, priority: String): Color = when (priority) {
    "high" -> p.amber
    "low" -> p.ink2
    else -> p.accent
}

fun priorityLabel(priority: String): String = when (priority) {
    "high" -> "Tinggi"
    "low" -> "Rendah"
    else -> "Sedang"
}

@Composable
fun DividerLine() {
    val p = LocalPalette.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(p.line)
    )
}

@Composable
fun RowScope.SoftButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
