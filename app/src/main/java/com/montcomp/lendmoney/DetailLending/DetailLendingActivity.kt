package com.montcomp.lendmoney.DetailLending

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.TimeUnit
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.telephony.SmsManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cixsolution.jzc.pevoex.Utils.*
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.google.android.material.textfield.TextInputEditText
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ActivityDetailLendingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.xml.datatype.DatatypeConstants.DAYS


class DetailLendingActivity : DisposableActivity() {

    var lendingResult: DbLending ?= null
    val SMS_CODE = 1
    val PHONE_CODE = 42
    val WRITE_CODE = 100
    private val MY_PERMISSION_SMS = arrayOf(Manifest.permission.SEND_SMS)
    private val MY_PERMISSION_PHONE = arrayOf(Manifest.permission.CALL_PHONE)
    private val MY_PERMISSION_WRITE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    lateinit var binding : ActivityDetailLendingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initActions()

    }

    private fun initView() {
        binding.apply {
            commonLayoutBack.toolbarTitleBack?.setText(R.string.detail_lending)
            commonLayoutBack.toolbarTitleBack?.setTextColor(this@DetailLendingActivity.resources.getColor(R.color.white))
        }
    }

    @SuppressLint("NewApi")
    private fun initActions() {
        binding.apply {
            commonLayoutBack.ivBack.setOnClickListener {
                onBackPressed()
            }

            CoroutineScope(Dispatchers.IO).launch {
                lendingResult = Singleton.getInstance().database?.lendingDao()?.getDbLending(Singleton.getInstance().tmpgloballending?.lendingId!!)
                withContext(Dispatchers.Main){
                    if (lendingResult != null){
                        if (lendingResult?.dateEnd != null){
                            //igualar el dia convertirlo al formato 31/12/2021 y verificar si es el dia
                            if (betweenDays(lendingResult?.dateEnd!!, Date().time)>=0){
                                tvTimeFinal.text = "Tiempo restante: ${betweenDays(lendingResult?.dateEnd!!, Date().time)} dia(s)"
                            }else{
                                tvTimeFinal.text = "Se acabo el tiempo"
                            }
                        }else{
                            tvTimeFinal.text = "Tiempo restante: Sin limite de tiempo"
                        }
                        tvName.text = lendingResult?.nameLend
                        tvAmountStart.text = "Préstamo: ${String.format("%.2f", lendingResult?.amountStart)}"
                        if (lendingResult?.imageLending !=""){
                            ivLending.setImageBitmap(lendingResult?.imageLending?.toBitMap())
                            tvNameImageLending.text = lendingResult?.nameImageLending
                        }
                        tvAmountEnd.text = "Deuda: ${String.format("%.2f", lendingResult?.amountEnd)}"
                    }
                }
            }

            ivSms.setOnClickListener {
                hasPermissionsSMS()
            }

            ivPhone.setOnClickListener {
                hasPermissionsPhone()
            }

            ivWsp.setOnClickListener {
                if (lendingResult != null){
                    var expandedEditTextDialog = Dialog(this@DetailLendingActivity)
                    expandedEditTextDialog.setContentView(R.layout.custom_dialog_edittext)

                    val metrics = DisplayMetrics()
                    this@DetailLendingActivity?.windowManager?.defaultDisplay?.getMetrics(metrics)

                    var window = expandedEditTextDialog.window
                    var param : WindowManager.LayoutParams = window!!.attributes
                    param.gravity = Gravity.CENTER
                    window.attributes = param
                    window.setBackgroundDrawable(ContextCompat.getDrawable(this@DetailLendingActivity,R.drawable.round_cornered_window))

                    var etSMS = expandedEditTextDialog.findViewById<TextInputEditText>(R.id.et_sms)

                    expandedEditTextDialog.findViewById<Button>(R.id.btn_to_accept).setOnClickListener {
                        if (etSMS.text?.isBlank()==false){
                            if (isWhatsappInstalled("com.whatsapp")){
                                var url = "https://api.whatsapp.com/send?phone="+"${lendingResult?.phone}"+"&text="+ URLEncoder.encode("${etSMS.text.toString()}","UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setPackage("com.whatsapp")
                                intent.data = Uri.parse(url)
                                startActivity(intent)

                            }else{
                                if (isWhatsappInstalled("com.whatsapp.w4b")){
                                    var url = "https://api.whatsapp.com/send?phone="+"${lendingResult?.phone}"+"&text="+ URLEncoder.encode("${etSMS.text.toString()}","UTF-8")
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setPackage("com.whatsapp.w4b")
                                    intent.data = Uri.parse(url)
                                    startActivity(intent)
                                }else{
                                    Toast.makeText(
                                        applicationContext, "Necesita instalar Whatsapp",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            expandedEditTextDialog.dismiss()
                        }else{
                            Toast.makeText(
                                applicationContext, "Necesita escribir un mensaje",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    expandedEditTextDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                        expandedEditTextDialog.dismiss()
                    }
                    expandedEditTextDialog.show()
                }else{
                    Toast.makeText(
                        applicationContext, "Ocurrio un problema intente mas tarde",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            ivLending.setOnClickListener {
                var expandedImageDialog = Dialog(this@DetailLendingActivity)
                expandedImageDialog.setContentView(R.layout.custom_dialog_image_share)

                val metrics = DisplayMetrics()
                this@DetailLendingActivity.windowManager?.defaultDisplay?.getMetrics(metrics)

                var window = expandedImageDialog.window
                var param : WindowManager.LayoutParams = window!!.attributes
                param.gravity = Gravity.CENTER
                window.attributes = param
                window.setBackgroundDrawable(ContextCompat.getDrawable(this@DetailLendingActivity,R.drawable.round_cornered_window))

                var ivImage = expandedImageDialog.findViewById<ImageView>(R.id.iv_expanded_image)
                var tvImage = expandedImageDialog.findViewById<TextView>(R.id.tv_image)
                if (lendingResult?.imageLending != ""){
                    ivImage.setImageBitmap(lendingResult?.imageLending?.toBitMap())
                    tvImage.text = lendingResult?.nameImageLending
                }else{
                    ivImage.setImageDrawable(this@DetailLendingActivity.resources.getDrawable(R.drawable.ic_logo_image))
                    tvImage.text = "No hay imagen"
                }

                expandedImageDialog.findViewById<Button>(R.id.btn_to_accept).setOnClickListener {
                    expandedImageDialog.dismiss()
                }
                expandedImageDialog.findViewById<ImageView>(R.id.iv_share_image).setOnClickListener {
                    hasPermissionsWrite(lendingResult?.imageLending.toString(),
                        lendingResult?.nameImageLending.toString()
                    )
                    //expandedImageDialog.dismiss()
                }

                expandedImageDialog.show()
            }
        }
    }

    fun hasPermissionsSMS() {
        if (ContextCompat.checkSelfPermission(this@DetailLendingActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DetailLendingActivity,
                MY_PERMISSION_SMS,
                SMS_CODE)
        }else{
            if (lendingResult != null){
                var expandedEditTextDialog = Dialog(this@DetailLendingActivity)
                expandedEditTextDialog.setContentView(R.layout.custom_dialog_edittext)

                val metrics = DisplayMetrics()
                this@DetailLendingActivity?.windowManager?.defaultDisplay?.getMetrics(metrics)

                var window = expandedEditTextDialog.window
                var param : WindowManager.LayoutParams = window!!.attributes
                param.gravity = Gravity.CENTER
                window.attributes = param
                window.setBackgroundDrawable(ContextCompat.getDrawable(this@DetailLendingActivity,R.drawable.round_cornered_window))

                var etSMS = expandedEditTextDialog.findViewById<TextInputEditText>(R.id.et_sms)

                expandedEditTextDialog.findViewById<Button>(R.id.btn_to_accept).setOnClickListener {
                    if (etSMS.text?.isBlank()==false){
                        val smsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage("${lendingResult?.phone}", null, "${etSMS.text}", null, null)
                        Toast.makeText(
                            applicationContext, "SMS sent.",
                            Toast.LENGTH_SHORT
                        ).show()
                        expandedEditTextDialog.dismiss()
                    }else{
                        Toast.makeText(
                            applicationContext, "Necesita escribir un mensaje",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                expandedEditTextDialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                    expandedEditTextDialog.dismiss()
                }
                expandedEditTextDialog.show()

            }else{
                Toast.makeText(
                    applicationContext, "Ocurrio un problema intente mas tarde",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun hasPermissionsPhone() {
        if (ContextCompat.checkSelfPermission(this@DetailLendingActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DetailLendingActivity,
                MY_PERMISSION_PHONE,
                PHONE_CODE)
        }else{
            if (lendingResult != null){
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "${lendingResult?.phone}"))
                startActivity(intent)
            }else{
                Toast.makeText(
                    applicationContext, "Ocurrio un problema intente mas tarde",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isWhatsappInstalled(name: String): Boolean{
        val packageManager = packageManager
        return try {
            packageManager.getPackageInfo(name,PackageManager.GET_ACTIVITIES)
            true
        }catch (e: PackageManager.NameNotFoundException){
            try{
                packageManager.getPackageInfo(name,PackageManager.GET_ACTIVITIES)
                true
            }catch (e: PackageManager.NameNotFoundException){
                false
            }
        }
    }

    fun hasPermissionsWrite(content: String, name:String) {
        if (ContextCompat.checkSelfPermission(this@DetailLendingActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DetailLendingActivity,
                MY_PERMISSION_WRITE,
                WRITE_CODE)
        }else{
            shareImageLending(content, name)
        }
    }

    fun shareImageLending(content: String, name:String){
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val i = Intent(Intent.ACTION_SEND)
        try{
            when(name.split(".").last()){
                "pdf"-> {
                    i.type = "application/pdf"
                }
                "doc","docx"-> {
                    i.type = "application/msword"
                }
                "jpg","png","gif","jpeg","iso"-> {
                    i.type = "image/*"
                }
                "mp3","wav"-> {
                    i.type = "audio/*"
                }
                "xls","xlsx"-> {
                    i.type = "application/vnd.ms-excel"
                }
                else -> {
                    i.type = "application/*"
                }
            }
            val bitmap = content.toBitMap()
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(contentResolver, bitmap, name, null)
            val imageUri = Uri.parse(path)
            i.putExtra(Intent.EXTRA_STREAM,imageUri)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }catch (e: Exception){
            throw RuntimeException(e)
        }
        startActivity(Intent.createChooser(i,"Compartir imagen"))
    }

    override fun onBackPressed() {
        val i = Intent(this@DetailLendingActivity, LendingActivity::class.java)
        startActivity(i)
        finishActivity()
    }

}