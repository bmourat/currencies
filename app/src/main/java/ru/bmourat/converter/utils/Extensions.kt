package ru.bmourat.converter.utils

import android.widget.TextView

fun TextView.setReadOnly(isReadOnly: Boolean) {
    isFocusable = !isReadOnly
    isFocusableInTouchMode = !isReadOnly
    isLongClickable = !isReadOnly
    isCursorVisible = !isReadOnly
}