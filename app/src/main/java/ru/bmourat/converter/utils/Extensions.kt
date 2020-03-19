package ru.bmourat.converter.utils

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService


fun TextView.setReadOnly(isReadOnly: Boolean) {
    isFocusable = !isReadOnly
    isFocusableInTouchMode = !isReadOnly
    isLongClickable = !isReadOnly
    isCursorVisible = !isReadOnly
}

fun EditText.showKeyboard() {
    if (requestFocus()) {
        val imm: InputMethodManager? = getSystemService(this.context, InputMethodManager::class.java)
        imm?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }
}