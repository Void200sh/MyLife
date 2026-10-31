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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.data.Dates
import com.zyvo.mylife.data.Tx

@Composable
fun MoneyScreen(store: AppStore) {
    val p = LocalPalette.current
    var showAdd by remember { mutableStateOf(false) }
    var showBudget by remember { mutableStateOf(false) }
    var deleteTx by remember { mutableStateOf<Tx?>(null) }

    val spent = store.spentThisMonth()
    val left = store.budget - spent
    val over = left < 0

    ScreenColumn {
        PageHeader(
            kicker = "Keuangan",
            title = "Uang",
            subtitle = "Catat pemasukan dan pengeluaran.",
            action = { AccentAction("+ Catat") { showAdd = true } }
        )

        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("SISA BUDGET BULAN INI", style = MaterialTheme.typography.labelSmall, color = p.ink2)
                    Text(
                        formatRupiah(if (store.budget <= 0.0) 0.0 else left),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (over) MaterialTheme.colorScheme.error else p.ink
                    )
                }
                SmallAction(if (store.budget <= 0.0) "Atur" else "Ubah") { showBudget = true }
            }
            if (store.budget > 0.0) {
                Bar(
                    (spent / store.budget).toFloat(),
                    if (over) MaterialTheme.colorScheme.error else p.accent,
                    p.line,
                    9
                )
                Text(
                    "Terpakai " + formatRupiah(spent) + " dari " + formatRupiah(store.budget),
                    style = MaterialTheme.typography.bodySmall,
                    color = p.ink2
                )
            } else {
                Text(
                    "Budget belum diatur. Tekan Atur untuk mengisi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = p.ink2
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBlock(formatRupiah(store.incomeTotal()), "Pemasukan", Modifier.weight(1f), p.accent)
                StatBlock(formatRupiah(store.outcomeTotal()), "Pengeluaran", Modifier.weight(1f), p.amber)
                StatBlock(formatRupiah(store.balance()), "Saldo", Modifier.weight(1f))
            }
        }

        SectionCard(title = "Transaksi") {
            if (store.txs.isEmpty()) {
                EmptyState("Belum ada transaksi.")
            } else {
                store.txs.forEach { x ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(p.bg)
                            .clickable { deleteTx = x }
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
                            Text(x.emoji, fontSize = 17.sp)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                x.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = p.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                (if (x.type == "in") "Pemasukan" else "Pengeluaran") + " · " + Dates.historyStamp(x.date),
                                style = MaterialTheme.typography.bodySmall,
                                color = p.ink2
                            )
                        }
                        Text(
                            (if (x.type == "in") "+" else "−") + formatRupiah(x.amount),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (x.type == "in") p.accent else p.ink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        TxDialog(
            onDismiss = { showAdd = false },
            onSave = { title, amount, type, emoji -> store.addTx(title, amount, type, emoji) }
        )
    }
    if (showBudget) {
        BudgetDialog(
            budget = store.budget,
            onDismiss = { showBudget = false },
            onSave = { store.setBudget(it) }
        )
    }
    deleteTx?.let { x ->
        ConfirmDialog(
            "Hapus transaksi?",
            "\"" + x.title + "\" (" + formatRupiah(x.amount) + ") akan dihapus.",
            onConfirm = { store.deleteTx(x.id) },
            onDismiss = { deleteTx = null }
        )
    }
}
