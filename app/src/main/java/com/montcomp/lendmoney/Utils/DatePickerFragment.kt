package com.montcomp.lendmoney.Utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import com.cixsolution.jzc.pevoex.Utils.normalizeDate
import java.util.*


open class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var composed:String=""
    var edittext:EditText?=null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val yy = calendar.get(Calendar.YEAR)
        val mm = calendar.get(Calendar.MONTH)
        val dd = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(activity, this, yy, mm, dd)
        datePickerDialog.datePicker.minDate = Date().time
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        composed  = (dayOfMonth).toString().normalizeDate() +"/"+(month + 1).toString().normalizeDate()+"/"+year.toString().normalizeDate()

        this.edittext?.setText(composed)
    }

    fun asignarview(edittext:EditText){
        this.edittext=edittext
    }

}





