package com.zoro.keyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import android.content.SharedPreferences

class ZoroInputMethodService : InputMethodService() {
    private var english = false
    private var symbols = false
    private var dark = true
    private var suggestions = true
    private lateinit var prefs: SharedPreferences
    private val ar = listOf("ض ص ث ق ف غ ع ه خ ح ج د", "ش س ي ب ل ا ت ن م ك ط", "ئ ء ؤ ر لا ى ة و ز ظ")
    private val en = listOf("q w e r t y u i o p", "a s d f g h j k l", "z x c v b n m")
    private val nums = listOf("1 2 3 4 5 6 7 8 9 0", "@ # $ % & - + ( ) /", "* \" ' : ; ! ?")
    private val words = listOf(" the ", " and ", " to ", " of ", " في ", " من ", " على ", " هذا ")

    override fun onCreate() { super.onCreate(); prefs = getSharedPreferences("zoro_ime", Context.MODE_PRIVATE) }
    override fun onCreateInputView(): View = keyboard()
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        if (::prefs.isInitialized) { dark = prefs.getBoolean("dark", true); suggestions = prefs.getBoolean("suggestions", true) }
    }
    private fun keyboard(): View {
        val bg = if (dark) 0xFF080F0A.toInt() else 0xFFE9F3EC.toInt()
        val fg = if (dark) 0xFF50FF91.toInt() else 0xFF146B3A.toInt()
        val root = LinearLayout(this).apply { orientation=1; setPadding(5,7,5,7); setBackgroundColor(bg) }
        val top = row()
        top.addView(key("ZORO ${if(english) "EN" else "AR"}",2,fg,bg) { english=!english; refresh() })
        top.addView(key("⚙",1,fg,bg) { settingsRow(root,fg,bg) })
        top.addView(key("⌫",1,fg,bg) { currentInputConnection?.deleteSurroundingText(1,0) })
        top.addView(key("×",1,fg,bg) { currentInputConnection?.deleteSurroundingText(1,0) })
        root.addView(top)
        if (suggestions) {
            val suggest = row()
            listOf("↶", "↷", "📋", "✓").forEach { s -> suggest.addView(key(s,1,fg,bg) {
                when(s) {
                    "↶" -> currentInputConnection?.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_LEFT))
                    "↷" -> currentInputConnection?.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_RIGHT))
                    "📋" -> pasteClipboard()
                    else -> currentInputConnection?.commitText(" ",1)
                }
            }) }
            root.addView(suggest)
        }
        val rows = if(symbols) nums else if(english) en else ar
        rows.forEach { r -> val line=row(); r.split(" ").forEach { label -> line.addView(key(label,1,fg,bg) { currentInputConnection?.commitText(label,1) }) }; root.addView(line) }
        val bottom=row()
        bottom.addView(key(if(symbols) "ABC" else "?123",1,fg,bg) { symbols=!symbols; refresh() })
        bottom.addView(key("🌐",1,fg,bg) { english=!english; symbols=false; refresh() })
        bottom.addView(key(",",1,fg,bg) { currentInputConnection?.commitText(if(english) "," else "،",1) })
        bottom.addView(key("مسافة",4,fg,bg) { currentInputConnection?.commitText(" ",1) })
        bottom.addView(key(".",1,fg,bg) { currentInputConnection?.commitText(".",1) })
        bottom.addView(key("↵",1,fg,bg) { val ic=currentInputConnection; val action= currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
            if(action!=EditorInfo.IME_ACTION_NONE && action!=EditorInfo.IME_ACTION_UNSPECIFIED) ic?.performEditorAction(action) else ic?.commitText("\n",1) })
        root.addView(bottom)
        return root
    }
    private fun settingsRow(root: LinearLayout, fg:Int, bg:Int) {
        root.addView(row().apply {
            addView(key(if(dark) "ثيم فاتح" else "ثيم داكن",2,fg,bg) { dark=!dark; prefs.edit().putBoolean("dark",dark).apply(); refresh() })
            addView(key(if(suggestions) "إخفاء الشريط" else "إظهار الشريط",2,fg,bg) { suggestions=!suggestions; prefs.edit().putBoolean("suggestions",suggestions).apply(); refresh() })
            addView(key("مسح الكل",1,fg,bg) { currentInputConnection?.deleteSurroundingText(1000,1000) })
        })
    }
    private fun pasteClipboard() {
        val cm=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text=cm.primaryClip?.getItemAt(0)?.coerceToText(this)
        if(text!=null) currentInputConnection?.commitText(text,1) else Toast.makeText(this,"الحافظة فارغة",Toast.LENGTH_SHORT).show()
    }
    private fun refresh(){ setInputView(keyboard()) }
    private fun row()=LinearLayout(this).apply { gravity=Gravity.CENTER; orientation=0 }
    private fun key(label:String, weight:Int, fg:Int, bg:Int, action:()->Unit)=Button(this).apply {
        text=label; textSize=if(label.length>3) 12f else 17f; setTextColor(fg); setTypeface(typeface,Typeface.BOLD)
        setBackgroundColor(if(dark) 0xFF17251C.toInt() else 0xFFD4E5D9.toInt()); isAllCaps=false
        layoutParams=LinearLayout.LayoutParams(0,50,weight.toFloat()).apply { setMargins(2,3,2,3) }
        setOnClickListener { action() }
    }
}
