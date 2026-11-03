package com.zoro.keyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 40, 28, 28)
            setBackgroundColor(0xFF080D0A.toInt())
        }
        val title = TextView(this).apply {
            text = "لوحة مفاتيح زورو الخارقة"
            textSize = 25f
            setTextColor(0xFF50FF91.toInt())
        }
        val info = TextView(this).apply {
            text = "\nمرحباً بك في ZORO KEYBOARD PRO\n\n1) فعّل لوحة المفاتيح من إعدادات النظام.\n2) اخترها كلوحة المفاتيح الافتراضية.\n\nالإصدار الأول يوفّر الكتابة العربية والإنجليزية والتبديل بينهما."
            textSize = 16f
            setTextColor(0xFFE5F5EA.toInt())
        }
        val enable = Button(this).apply {
            text = "تفعيل لوحة المفاتيح"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        }
        val choose = Button(this).apply {
            text = "اختيار لوحة زورو"
            setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }
        layout.addView(title); layout.addView(info); layout.addView(enable); layout.addView(choose)
        setContentView(layout)
    }
}
