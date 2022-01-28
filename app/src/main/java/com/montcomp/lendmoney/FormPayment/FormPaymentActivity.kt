package com.montcomp.lendmoney.FormPayment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cixsolution.jzc.pevoex.Utils.limitDecimal
import com.cixsolution.jzc.pevoex.Utils.pdfToBase64
import com.cixsolution.jzc.pevoex.Utils.toString64
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.hcr.hcr_android.Drawer.Drawer
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.Login.LoginActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.Utils.FileUtils
import com.montcomp.lendmoney.databinding.ActivityDrawerHomeBinding
import com.montcomp.lendmoney.databinding.ActivityFormLendingBinding
import com.montcomp.lendmoney.databinding.ActivityFormPaymentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception

class FormPaymentActivity : DisposableActivity() {

    var sizeFiles:Long =0
    val MAXIMO_TAMANIO_BYTES = 1048576; // 1MB = 1024 millón de bytes  1KB = 1024 de bytes

    private val REQUEST_CODE_GPS_PERMISSION =10
    val FILE_REQUEST_CODE = 100
    private val MY_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private var contentImg :String =""
    private var nameImg :String =""

    lateinit var binding : ActivityFormPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = Drawer(this@FormPaymentActivity)
        drawer.configureBurgerMenu()

        initView()
        initActions()

    }

    private fun initView() {
        binding.apply {
            etAmount.limitDecimal()
            commonLayoutBack.toolbarTitleBack?.setText(R.string.form_payment)
            commonLayoutBack.toolbarTitleBack?.setTextColor(this@FormPaymentActivity.resources.getColor(R.color.white))
        }
    }

    private fun initActions() {
        binding.apply {
            commonLayoutBack.ivBack.setOnClickListener {
                onBackPressed()
            }

            imgVoucher.setOnClickListener {
                if (!hasPermissions(this@FormPaymentActivity)) {
                    Toast.makeText(this@FormPaymentActivity, "Abriendo galería", Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(this@FormPaymentActivity, MY_PERMISSIONS, 1)
                }else{
                    pickUpFile()
                }
            }

            btnSendPayment.setOnClickListener {
                if (Singleton.getInstance().tmpgloballending?.amountEnd!!-etAmount.text.toString().toDouble() >=0){
                    if (textInputAmount.editText?.text?.isBlank() == false){
                        if (textInputDescription.editText?.text?.isBlank() == false){
                            if (etAmount.text.toString().toDouble()>0){
                                if (contentImg != ""){
                                    Log.e("SIZE","${sizeFiles/1024} KB")
                                    if (sizeFiles<=MAXIMO_TAMANIO_BYTES){
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                Singleton.getInstance().database?.paymentDao()?.insertDbPayment(
                                                    DbPayment(
                                                        paymentId = 0,
                                                        amount = etAmount.text.toString().toDouble(),
                                                        paymentDes = etDescription.text.toString(),
                                                        nameImagePay = nameImg,
                                                        imagePay =contentImg,
                                                        lendingId = Singleton.getInstance().tmpgloballending?.lendingId!!
                                                    )
                                                )
                                                Singleton.getInstance().database?.lendingDao()?.updateDbLending(
                                                    DbLending(
                                                        lendingId = Singleton.getInstance().tmpgloballending?.lendingId!!,
                                                        nameLend= Singleton.getInstance().tmpgloballending?.nameLend!!,
                                                        amountStart = Singleton.getInstance().tmpgloballending?.amountStart!!,
                                                        amountEnd = Singleton.getInstance().tmpgloballending?.amountEnd!!-etAmount.text.toString().toDouble(),
                                                        phone = Singleton.getInstance().tmpgloballending?.phone,
                                                        nameImageLending = Singleton.getInstance().tmpgloballending?.nameImageLending,
                                                        imageLending = Singleton.getInstance().tmpgloballending?.imageLending,
                                                        dateEnd = Singleton.getInstance().tmpgloballending?.dateEnd,
                                                        userId = Singleton.getInstance().tmpgloballending?.userId!!
                                                    )
                                                )
                                            }catch (e: Exception){
                                                Log.e("EXCEPTION",e.toString())
                                            }
                                            withContext(Dispatchers.Main){
                                                Toast.makeText(this@FormPaymentActivity, "Pago realizado", Toast.LENGTH_SHORT).show()
                                                val i = Intent(this@FormPaymentActivity,LendingActivity::class.java)
                                                startActivity(i)
                                                finishActivity()
                                            }
                                        }
                                    }else{
                                        Toast.makeText(this@FormPaymentActivity, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    val builder = AlertDialog.Builder(this@FormPaymentActivity)
                                    val title = "¿Quieres continuar sin añadir una imagen?"
                                    builder.setMessage(title)
                                    val okText = "Si"
                                    val noText = "No"
                                    builder.setPositiveButton(okText) { dialog, which ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                Singleton.getInstance().database?.paymentDao()?.insertDbPayment(
                                                    DbPayment(
                                                        paymentId = 0,
                                                        amount = etAmount.text.toString().toDouble(),
                                                        paymentDes = etDescription.text.toString(),
                                                        nameImagePay = nameImg,
                                                        imagePay =contentImg,
                                                        lendingId = Singleton.getInstance().tmpgloballending?.lendingId!!
                                                    )
                                                )
                                                Singleton.getInstance().database?.lendingDao()?.updateDbLending(
                                                    DbLending(
                                                        lendingId = Singleton.getInstance().tmpgloballending?.lendingId!!,
                                                        nameLend= Singleton.getInstance().tmpgloballending?.nameLend!!,
                                                        amountStart = Singleton.getInstance().tmpgloballending?.amountStart!!,
                                                        amountEnd = Singleton.getInstance().tmpgloballending?.amountEnd!!-etAmount.text.toString().toDouble(),
                                                        phone = Singleton.getInstance().tmpgloballending?.phone,
                                                        nameImageLending = Singleton.getInstance().tmpgloballending?.nameImageLending,
                                                        imageLending = Singleton.getInstance().tmpgloballending?.imageLending,
                                                        dateEnd = Singleton.getInstance().tmpgloballending?.dateEnd,
                                                        userId = Singleton.getInstance().tmpgloballending?.userId!!
                                                    )
                                                )
                                            }catch (e: Exception){
                                                Log.e("EXCEPTION",e.toString())
                                            }
                                            withContext(Dispatchers.Main){
                                                Toast.makeText(this@FormPaymentActivity, "Pago realizado", Toast.LENGTH_SHORT).show()
                                                val i = Intent(this@FormPaymentActivity,LendingActivity::class.java)
                                                startActivity(i)
                                                finishActivity()
                                            }
                                        }
                                        dialog.dismiss()
                                    }
                                    builder.setNegativeButton(noText) { dialog, which ->
                                        dialog.dismiss()
                                    }
                                    val dialog: AlertDialog = builder.create()
                                    dialog.show()
                                }
                            }else{
                                Toast.makeText(this@FormPaymentActivity, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@FormPaymentActivity, "Debe escribir una descripción", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@FormPaymentActivity, "Debe escribir un monto", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@FormPaymentActivity, "El monto a pagar supera la cantidad restante del préstamo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun getLogo(name:String):Int{
        return when(name.split(".").last()){
            "pdf"->R.drawable.ic_logo_pdf
            "doc","docx"->R.drawable.ic_logo_word
            "jpg","png","gif","jpeg","iso"->R.drawable.ic_logo_image
            "mp3","wav"->R.drawable.ic_logo_audio
            "xls","xlsx"->R.drawable.ic_logo_excel
            else ->R.drawable.ic_logo_file_generic
        }
    }

    fun getFileName(uri: Uri, contentResolver: ContentResolver): String {

        var result: String? = null
        if (uri.getScheme().equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data?: return

                try{
                    var bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, uri)
                    var converted = bitmap.toString64()
                    val fileName = getFileName(uri,this?.contentResolver!!)?: return

                    ////
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val imageInByte: ByteArray = stream.toByteArray()
                    sizeFiles = imageInByte.size.toLong()
                    ////

                    contentImg = converted
                    nameImg = fileName

                    binding.imgVoucher.setImageDrawable(resources.getDrawable(getLogo(fileName)))
                    binding.tvVoucher.text = fileName

                }catch (e: Throwable){
                    var path = FileUtils.getRealPath(this as Activity, uri)
                    var filetmp = java.io.File(path)
                    var file = ""
                    try{
                        file = filetmp.pdfToBase64()
                    }catch (e: Throwable){
                    }
                    var fileName = filetmp.name

                    contentImg = file
                    nameImg = fileName
                    sizeFiles = filetmp.length()

                    binding.imgVoucher.setImageDrawable(resources.getDrawable(getLogo(fileName)))
                    binding.tvVoucher.text = fileName
                }
            }
        }
    }

    fun pickUpFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val i = Intent.createChooser(intent, "File")
        startActivityForResult(i, FILE_REQUEST_CODE)
    }

    fun hasPermissions(ctx: Context): Boolean {
        var hasPermissions = true
        for (permission in MY_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED && Manifest.permission.CHANGE_NETWORK_STATE != permission) {
                hasPermissions = false
            }
        }
        return hasPermissions
    }

    override fun onBackPressed() {
        val i = Intent(this@FormPaymentActivity,LendingActivity::class.java)
        startActivity(i)
        finishActivity()
    }
}