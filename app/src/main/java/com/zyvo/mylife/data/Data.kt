package com.zyvo.mylife.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppThemeId(val label: String, val dark: Boolean) {
    SAGE("Soft Sage", false),
    PAPER("Paper", false),
    PORCELAIN("Porcelain", false),
    CHROME("Chrome", true),
    TACTICAL("Tactical", true)
}

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val emoji: String,
    val exercise: Boolean,
    val log: Map<String, Int> = emptyMap()
)

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val priority: String = "mid",
    val cat: String = "Umum",
    val due: String = "",
    val done: Boolean = false
)

data class Subject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Long = 0xFF5E8B6A,
    val target: Int = 4,
    val done: Int = 0
)

data class Tx(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: String = "out",
    val emoji: String = "💸",
    val date: Long = System.currentTimeMillis()
)

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val current: Double = 0.0,
    val target: Double = 10.0,
    val unit: String = "sesi",
    val color: Long = 0xFF5E8B6A
)

data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val title: String,
    val detail: String,
    val at: Long = System.currentTimeMillis()
)

const val DEFAULT_QUOTE = "Konsistensi kecil yang rutin mengalahkan semangat besar yang sebentar."

object Dates {
    private val dayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)
    private val humanFmt = SimpleDateFormat("EEEE, d MMMM", Locale("id", "ID"))
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.US)
    private val shortDayFmt = SimpleDateFormat("EE", Locale("id", "ID"))
    private val shortDateFmt = SimpleDateFormat("d/M", Locale.US)

    fun key(date: Date = Date()): String = dayFmt.format(date)

    fun monthKey(date: Date = Date()): String = monthFmt.format(date)

    fun addDays(date: Date, days: Int): Date =
        Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, days)
        }.time

    fun last7(): List<Pair<String, String>> {
        val out = mutableListOf<Pair<String, String>>()
        for (i in 6 downTo 0) {
            val d = addDays(Date(), -i)
            out.add(key(d) to shortDayFmt.format(d))
        }
        return out
    }

    fun todayHuman(): String = humanFmt.format(Date())

    fun greeting(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            h < 11 -> "Selamat pagi"
            h < 15 -> "Selamat siang"
            h < 19 -> "Selamat sore"
            else -> "Selamat malam"
        }
    }

    fun historyStamp(ts: Long): String {
        val d = Date(ts)
        return if (key(d) == key()) timeFmt.format(d) else shortDateFmt.format(d)
    }
}

class AppStore(context: Context) {
    private val prefs = context.getSharedPreferences("mylife_store", Context.MODE_PRIVATE)

    val habits = mutableStateListOf<Habit>()
    val tasks = mutableStateListOf<TaskItem>()
    val subjects = mutableStateListOf<Subject>()
    val txs = mutableStateListOf<Tx>()
    val goals = mutableStateListOf<Goal>()
    val history = mutableStateListOf<HistoryEntry>()

    var themeId by mutableStateOf(AppThemeId.SAGE)
        private set
    var moods by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    var focus by mutableStateOf<Map<String, Int>>(emptyMap())
        private set
    var budget by mutableStateOf(0.0)
        private set
    var quote by mutableStateOf(DEFAULT_QUOTE)
        private set
    var selectedTaskId by mutableStateOf<String?>(null)
        private set
    var exerciseTarget by mutableStateOf(4)
        private set
    var exerciseMinutes by mutableStateOf(30)
        private set

    init {
        load()
    }

    fun save() {
        val o = JSONObject()
        o.put("theme", themeId.name)
        o.put("budget", budget)
        o.put("quote", quote)
        o.put("exerciseTarget", exerciseTarget)
        o.put("exerciseMinutes", exerciseMinutes)
        if (selectedTaskId == null) o.put("selectedTaskId", JSONObject.NULL) else o.put("selectedTaskId", selectedTaskId)
        o.put("moods", strMapToJson(moods))
        o.put("focus", intMapToJson(focus))

        val hArr = JSONArray()
        habits.forEach { h ->
            hArr.put(JSONObject().apply {
                put("id", h.id)
                put("name", h.name)
                put("emoji", h.emoji)
                put("exercise", h.exercise)
                put("log", intMapToJson(h.log))
            })
        }
        o.put("habits", hArr)

        val tArr = JSONArray()
        tasks.forEach { t ->
            tArr.put(JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("priority", t.priority)
                put("cat", t.cat)
                put("due", t.due)
                put("done", t.done)
            })
        }
        o.put("tasks", tArr)

        val sArr = JSONArray()
        subjects.forEach { s ->
            sArr.put(JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("color", s.color)
                put("target", s.target)
                put("done", s.done)
            })
        }
        o.put("subjects", sArr)

        val xArr = JSONArray()
        txs.forEach { x ->
            xArr.put(JSONObject().apply {
                put("id", x.id)
                put("title", x.title)
                put("amount", x.amount)
                put("type", x.type)
                put("emoji", x.emoji)
                put("date", x.date)
            })
        }
        o.put("txs", xArr)

        val gArr = JSONArray()
        goals.forEach { g ->
            gArr.put(JSONObject().apply {
                put("id", g.id)
                put("title", g.title)
                put("current", g.current)
                put("target", g.target)
                put("unit", g.unit)
                put("color", g.color)
            })
        }
        o.put("goals", gArr)

        val histArr = JSONArray()
        history.forEach { h ->
            histArr.put(JSONObject().apply {
                put("id", h.id)
                put("type", h.type)
                put("title", h.title)
                put("detail", h.detail)
                put("at", h.at)
            })
        }
        o.put("history", histArr)
        prefs.edit().putString("state", o.toString()).apply()
    }

    private fun load() {
        val raw = prefs.getString("state", null) ?: return
        try {
            val o = JSONObject(raw)
            themeId = runCatching { AppThemeId.valueOf(o.optString("theme", AppThemeId.SAGE.name)) }
                .getOrDefault(AppThemeId.SAGE)
            budget = o.optDouble("budget", 0.0)
            quote = o.optString("quote", DEFAULT_QUOTE)
            exerciseTarget = o.optInt("exerciseTarget", 4).coerceIn(1, 7)
            exerciseMinutes = o.optInt("exerciseMinutes", 30).coerceAtLeast(5)
            selectedTaskId = if (o.isNull("selectedTaskId")) null else o.optString("selectedTaskId").ifBlank { null }
            moods = jsonToStrMap(o.optJSONObject("moods"))
            focus = jsonToIntMap(o.optJSONObject("focus"))

            val hArr = o.optJSONArray("habits")
            if (hArr != null) for (i in 0 until hArr.length()) {
                val h = hArr.optJSONObject(i) ?: continue
                habits.add(
                    Habit(
                        id = h.optString("id", UUID.randomUUID().toString()),
                        name = h.optString("name", "Kebiasaan"),
                        emoji = h.optString("emoji", "✅"),
                        exercise = h.optBoolean("exercise", false),
                        log = jsonToIntMap(h.optJSONObject("log"))
                    )
                )
            }

            val tArr = o.optJSONArray("tasks")
            if (tArr != null) for (i in 0 until tArr.length()) {
                val t = tArr.optJSONObject(i) ?: continue
                tasks.add(
                    TaskItem(
                        id = t.optString("id", UUID.randomUUID().toString()),
                        title = t.optString("title", "Tugas"),
                        priority = t.optString("priority", "mid"),
                        cat = t.optString("cat", "Umum"),
                        due = t.optString("due", ""),
                        done = t.optBoolean("done", false)
                    )
                )
            }

            val sArr = o.optJSONArray("subjects")
            if (sArr != null) for (i in 0 until sArr.length()) {
                val s = sArr.optJSONObject(i) ?: continue
                subjects.add(
                    Subject(
                        id = s.optString("id", UUID.randomUUID().toString()),
                        name = s.optString("name", "Pelajaran"),
                        color = s.optLong("color", 0xFF5E8B6A),
                        target = s.optInt("target", 4),
                        done = s.optInt("done", 0)
                    )
                )
            }

            val xArr = o.optJSONArray("txs")
            if (xArr != null) for (i in 0 until xArr.length()) {
                val x = xArr.optJSONObject(i) ?: continue
                txs.add(
                    Tx(
                        id = x.optString("id", UUID.randomUUID().toString()),
                        title = x.optString("title", "Transaksi"),
                        amount = x.optDouble("amount", 0.0),
                        type = x.optString("type", "out"),
                        emoji = x.optString("emoji", "💸"),
                        date = x.optLong("date", System.currentTimeMillis())
                    )
                )
            }

            val gArr = o.optJSONArray("goals")
            if (gArr != null) for (i in 0 until gArr.length()) {
                val g = gArr.optJSONObject(i) ?: continue
                goals.add(
                    Goal(
                        id = g.optString("id", UUID.randomUUID().toString()),
                        title = g.optString("title", "Target"),
                        current = g.optDouble("current", 0.0),
                        target = g.optDouble("target", 1.0),
                        unit = g.optString("unit", "sesi"),
                        color = g.optLong("color", 0xFF5E8B6A)
                    )
                )
            }

            val histArr = o.optJSONArray("history")
            if (histArr != null) for (i in 0 until histArr.length()) {
                val h = histArr.optJSONObject(i) ?: continue
                history.add(
                    HistoryEntry(
                        id = h.optString("id", UUID.randomUUID().toString()),
                        type = h.optString("type", "habit"),
                        title = h.optString("title", ""),
                        detail = h.optString("detail", ""),
                        at = h.optLong("at", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
        }
    }

    fun setTheme(id: AppThemeId) {
        themeId = id
        save()
    }

    fun logHistory(type: String, title: String, detail: String) {
        history.add(0, HistoryEntry(type = type, title = title, detail = detail))
        while (history.size > 200) history.removeAt(history.lastIndex)
        save()
    }

    // ---------- habits ----------
    fun addHabit(name: String, emoji: String, exercise: Boolean) {
        habits.add(Habit(name = name, emoji = emoji, exercise = exercise))
        save()
    }

    fun updateHabit(habit: Habit) {
        val i = habits.indexOfFirst { it.id == habit.id }
        if (i >= 0) habits[i] = habit
        save()
    }

    fun deleteHabit(id: String) {
        habits.removeAll { it.id == id }
        save()
    }

    fun toggleHabit(id: String) {
        val i = habits.indexOfFirst { it.id == id }
        if (i < 0) return
        val h = habits[i]
        val k = Dates.key()
        val log = h.log.toMutableMap()
        if (log.containsKey(k)) {
            log.remove(k)
        } else {
            val mins = exerciseMinutes
            log[k] = if (h.exercise) mins else 1
            if (h.exercise) {
                logHistory("exercise", "Olahraga", h.name + " · " + mins + " menit")
            } else {
                logHistory("habit", "Kebiasaan", h.name)
            }
        }
        habits[i] = h.copy(log = log)
        save()
    }

    fun habitStreak(h: Habit): Int {
        var d = Date()
        if (!h.log.containsKey(Dates.key(d))) d = Dates.addDays(d, -1)
        var n = 0
        while (h.log.containsKey(Dates.key(d))) {
            n++
            d = Dates.addDays(d, -1)
        }
        return n
    }

    // ---------- tasks ----------
    fun upsertTask(task: TaskItem) {
        val i = tasks.indexOfFirst { it.id == task.id }
        if (i >= 0) tasks[i] = task else tasks.add(0, task)
        if (task.done && selectedTaskId == task.id) selectedTaskId = null
        save()
    }

    fun deleteTask(id: String) {
        tasks.removeAll { it.id == id }
        if (selectedTaskId == id) selectedTaskId = null
        save()
    }

    fun setTaskDone(id: String, done: Boolean) {
        val i = tasks.indexOfFirst { it.id == id }
        if (i < 0) return
        val t = tasks[i].copy(done = done)
        tasks[i] = t
        if (done) {
            logHistory("task", "Tugas selesai", t.title)
            if (selectedTaskId == id) selectedTaskId = null
        }
        save()
    }

    fun selectTask(id: String?) {
        selectedTaskId = id?.takeIf { tasks.any { t -> t.id == it && !t.done } }
        save()
    }

    fun selectedTask(): TaskItem? = tasks.firstOrNull { it.id == selectedTaskId && !it.done }

    // ---------- subjects ----------
    fun upsertSubject(subject: Subject) {
        val i = subjects.indexOfFirst { it.id == subject.id }
        if (i >= 0) subjects[i] = subject else subjects.add(subject)
        save()
    }

    fun deleteSubject(id: String) {
        subjects.removeAll { it.id == id }
        save()
    }

    fun incrementSubject(id: String) {
        val i = subjects.indexOfFirst { it.id == id }
        if (i < 0) return
        val s = subjects[i].copy(done = subjects[i].done + 1)
        subjects[i] = s
        logHistory("study", "Belajar", s.name)
        save()
    }

    // ---------- money ----------
    fun addTx(title: String, amount: Double, type: String, emoji: String) {
        txs.add(0, Tx(title = title, amount = amount, type = type, emoji = emoji))
        save()
    }

    fun deleteTx(id: String) {
        txs.removeAll { it.id == id }
        save()
    }

    fun setBudget(value: Double) {
        budget = value.coerceAtLeast(0.0)
        save()
    }

    fun incomeTotal(): Double = txs.filter { it.type == "in" }.sumOf { it.amount }

    fun outcomeTotal(): Double = txs.filter { it.type == "out" }.sumOf { it.amount }

    fun balance(): Double = incomeTotal() - outcomeTotal()

    fun spentThisMonth(): Double {
        val mk = Dates.monthKey()
        return txs.filter { it.type == "out" && Dates.monthKey(Date(it.date)) == mk }.sumOf { it.amount }
    }

    // ---------- goals ----------
    fun upsertGoal(goal: Goal) {
        val i = goals.indexOfFirst { it.id == goal.id }
        if (i >= 0) goals[i] = goal else goals.add(goal)
        save()
    }

    fun deleteGoal(id: String) {
        goals.removeAll { it.id == id }
        save()
    }

    // ---------- mood / focus / exercise ----------
    fun setMood(emoji: String) {
        moods = moods + (Dates.key() to emoji)
        logHistory("mood", "Mood", emoji)
        save()
    }

    fun addFocus(minutes: Int, taskTitle: String?) {
        val m = minutes.coerceAtLeast(1)
        val k = Dates.key()
        focus = focus + (k to ((focus[k] ?: 0) + m))
        logHistory("focus", "Sesi fokus", m.toString() + " menit" + if (taskTitle != null) " · " + taskTitle else "")
        save()
    }

    fun focusToday(): Int = focus[Dates.key()] ?: 0

    fun focusWeek(): Int = Dates.last7().sumOf { focus[it.first] ?: 0 }

    fun exerciseMinutesOn(key: String): Int =
        habits.filter { it.exercise }.sumOf { it.log[key] ?: 0 }

    fun exerciseDaysThisWeek(): Int = Dates.last7().count { exerciseMinutesOn(it.first) > 0 }

    fun exerciseStreak(): Int {
        var d = Date()
        if (exerciseMinutesOn(Dates.key(d)) == 0) d = Dates.addDays(d, -1)
        var n = 0
        while (exerciseMinutesOn(Dates.key(d)) > 0) {
            n++
            d = Dates.addDays(d, -1)
        }
        return n
    }

    fun setExercise(target: Int, minutes: Int) {
        exerciseTarget = target.coerceIn(1, 7)
        exerciseMinutes = minutes.coerceAtLeast(5)
        save()
    }

    fun setQuote(value: String) {
        quote = value.ifBlank { DEFAULT_QUOTE }
        save()
    }

    private fun intMapToJson(map: Map<String, Int>): JSONObject {
        val o = JSONObject()
        map.forEach { (k, v) -> o.put(k, v) }
        return o
    }

    private fun strMapToJson(map: Map<String, String>): JSONObject {
        val o = JSONObject()
        map.forEach { (k, v) -> o.put(k, v) }
        return o
    }

    private fun jsonToIntMap(o: JSONObject?): Map<String, Int> {
        if (o == null) return emptyMap()
        val m = mutableMapOf<String, Int>()
        val it = o.keys()
        while (it.hasNext()) {
            val k = it.next()
            m[k] = o.optInt(k)
        }
        return m
    }

    private fun jsonToStrMap(o: JSONObject?): Map<String, String> {
        if (o == null) return emptyMap()
        val m = mutableMapOf<String, String>()
        val it = o.keys()
        while (it.hasNext()) {
            val k = it.next()
            m[k] = o.optString(k)
        }
        return m
    }
}
