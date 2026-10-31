package com.zyvo.mylife

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.zyvo.mylife.data.AppStore
import com.zyvo.mylife.ui.LocalPalette
import com.zyvo.mylife.ui.MyLifeApp
import com.zyvo.mylife.ui.MyLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val store = remember { AppStore(applicationContext) }
            MyLifeTheme(store.themeId) {
                val palette = LocalPalette.current
                val view = LocalView.current
                SideEffect {
                    val window = (view.context as? Activity)?.window ?: return@SideEffect
                    window.statusBarColor = palette.bg.toArgb()
                    window.navigationBarColor = palette.bg.toArgb()
                    val controller = WindowCompat.getInsetsController(window, view)
                    controller.isAppearanceLightStatusBars = !palette.dark
                    controller.isAppearanceLightNavigationBars = !palette.dark
                }
                MyLifeApp(store)
            }
        }
    }
}
