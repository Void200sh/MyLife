package com.zyvo.mylife.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvo.mylife.data.AppStore
import kotlinx.coroutines.delay

private data class NavItem(val id: String, val label: String, val emoji: String)

private val navItems = listOf(
    NavItem("home", "Beranda", "🏠"),
    NavItem("study", "Belajar", "⏱"),
    NavItem("tasks", "Tugas", "✅"),
    NavItem("money", "Uang", "💰"),
    NavItem("progress", "Progres", "📈")
)

@Composable
fun MyLifeApp(store: AppStore) {
    val p = LocalPalette.current
    val context = LocalContext.current
    val toaster: (String) -> Unit = { msg ->
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    var tab by rememberSaveable { mutableStateOf("home") }
    var showTheme by remember { mutableStateOf(false) }

    var running by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf("focus") }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var sessionSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000L)
            if (!running) break
            if (secondsLeft > 0) {
                secondsLeft -= 1
                if (mode == "focus") sessionSeconds += 1
            }
            if (secondsLeft <= 0) {
                if (mode == "focus") {
                    store.addFocus(25, store.selectedTask()?.title)
                    mode = "break"
                    secondsLeft = 5 * 60
                    sessionSeconds = 0
                    running = false
                    toaster("Sesi fokus selesai! +25 menit ⏱")
                } else {
                    mode = "focus"
                    secondsLeft = 25 * 60
                    running = false
                    toaster("Istirahat selesai, lanjut fokus 💪")
                }
            }
        }
    }

    val checkout = {
        if (mode != "focus") {
            toaster("Check-out hanya saat mode fokus")
        } else if (sessionSeconds < 60) {
            toaster("Minimal 1 menit untuk check-out")
        } else {
            val mins = (sessionSeconds + 30) / 60
            store.addFocus(mins, store.selectedTask()?.title)
            running = false
            mode = "focus"
            secondsLeft = 25 * 60
            sessionSeconds = 0
            toaster("Check-out · +" + mins + " menit fokus")
        }
    }

    val reset = {
        running = false
        mode = "focus"
        secondsLeft = 25 * 60
        sessionSeconds = 0
        toaster("Timer di-reset")
    }

    Scaffold(
        containerColor = p.bg,
        bottomBar = {
            NavigationBar(containerColor = p.card, tonalElevation = 0.dp) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = tab == item.id,
                        onClick = { tab = item.id },
                        icon = { Text(item.emoji, fontSize = 17.sp) },
                        label = { Text(item.label, fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = p.accent,
                            selectedTextColor = p.accent,
                            indicatorColor = p.accentSoft,
                            unselectedIconColor = p.ink2,
                            unselectedTextColor = p.ink2
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when (tab) {
                "home" -> HomeScreen(
                    store = store,
                    onOpenTheme = { showTheme = true },
                    onOpenTasks = { tab = "tasks" }
                )
                "study" -> StudyScreen(
                    store = store,
                    secondsLeft = secondsLeft,
                    mode = mode,
                    running = running,
                    sessionSeconds = sessionSeconds,
                    onToggle = { running = !running },
                    onReset = reset,
                    onCheckout = checkout
                )
                "tasks" -> TasksScreen(store)
                "money" -> MoneyScreen(store)
                else -> ProgressScreen(store) { showTheme = true }
            }
        }
    }

    if (showTheme) {
        ThemeDialog(
            current = store.themeId,
            onDismiss = { showTheme = false },
            onSelect = { store.setTheme(it) }
        )
    }
}
