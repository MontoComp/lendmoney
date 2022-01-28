package com.cixsolution.jzc.pevoex.Utils

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import kotlin.math.roundToInt

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.dpToPx(): Int = (this * (Resources.getSystem().displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()

fun View?.visible(){
    this  ?: return
    this.visibility = View.VISIBLE
}

fun View?.invisible(){
    this  ?: return
    this.visibility = View.INVISIBLE
}

fun View?.gone(){
    this  ?: return
    this.visibility = View.GONE
}

fun View.showOrGone(visible :Boolean = true){
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.showOrInvisible(visible :Boolean = true){
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun EditText.onTextChangedGeneral(listener: (textChanged: String?) -> Unit){
    this.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            listener(p0?.toString())
        }

    })
}