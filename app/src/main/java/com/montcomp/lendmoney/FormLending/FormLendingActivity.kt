package com.montcomp.lendmoney.FormLending

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.hcr.hcr_android.Drawer.Drawer
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.Utils.FileUtils
import com.montcomp.lendmoney.databinding.ActivityDrawerHomeBinding
import com.montcomp.lendmoney.databinding.ActivityFormLendingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.DecimalFormatSymbols
import android.graphics.Bitmap

import android.R.attr.bitmap
import android.content.DialogInterface
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.cixsolution.jzc.pevoex.Utils.*
import com.montcomp.lendmoney.Utils.DatePickerFragment
import java.io.ByteArrayOutputStream


class FormLendingActivity : DisposableActivity() {

    var sizeFiles:Long =0
    val MAXIMO_TAMANIO_BYTES = 1048576; // 1MB = 1024 millón de bytes  1KB = 1024 de bytes

    val CONTACT_PERMISSION_CODE = 1
    val CONTACT_PICK_CODE = 2

    var listContacts = arrayListOf<String>()


    private val REQUEST_CODE_GPS_PERMISSION =10
    val FILE_REQUEST_CODE = 100
    private val MY_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private var contentImg :String =""
    private var nameImg :String =""

    lateinit var binding : ActivityFormLendingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = Drawer(this@FormLendingActivity)
        drawer.configureBurgerMenu()

        initView()
        initActions()

    }

    private fun initView() {
        binding.apply {
            //etDateEnd.isFocusable = false
            textInputDateEnd.gone()
            etAmount.limitDecimal()
            commonLayoutBack.toolbarTitleBack?.setText(R.string.form_lending)
            commonLayoutBack.toolbarTitleBack?.setTextColor(this@FormLendingActivity.resources.getColor(R.color.white))
        }
    }

    private fun initActions() {
        binding.apply {
            commonLayoutBack.ivBack.setOnClickListener {
                onBackPressed()
            }

            ivContacts.setOnClickListener {
                pickContact()
            }

            swDateEnd.setOnCheckedChangeListener { compoundButton, b ->
                if (b){
                    textInputDateEnd.visible()
                }else{
                    etDateEnd.setText("")
                    textInputDateEnd.gone()
                }
            }

            etDateEnd.let {
                it.isFocusable = false
                it.setOnClickListener {
                    var newFragment = DatePickerFragment()
                    val fragmentManager = (this@FormLendingActivity as FragmentActivity).fragmentManager
                    newFragment.show(fragmentManager, "DatePicker")
                    newFragment.asignarview(etDateEnd as EditText)
                }
            }

            imgFile.setOnClickListener {
                if (!hasPermissions(this@FormLendingActivity)) {
                    Toast.makeText(this@FormLendingActivity, "Abriendo galería", Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(this@FormLendingActivity, MY_PERMISSIONS, 1)
                }else{
                    pickUpFile()
                }
            }

            btnSendLending.setOnClickListener {
                if (textInputName.editText?.text?.isBlank() == false){
                    if (textInputAmount.editText?.text?.isBlank() == false){
                        if (etAmount.text.toString().toDouble()>0){
                            if (textInputPhone.editText?.text?.isBlank() == false){
                                countryCodePicker.registerCarrierNumberEditText(etPhone)
                                if (swDateEnd.isChecked && etDateEnd.text?.isBlank()==true){
                                    Toast.makeText(this@FormLendingActivity, "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
                                }else{
                                    if (contentImg != ""){
                                        Log.e("SIZE","${sizeFiles/1024} KB")
                                        if (sizeFiles<=MAXIMO_TAMANIO_BYTES){
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    Singleton.getInstance().database?.lendingDao()?.insertDbLending(
                                                        DbLending(
                                                            lendingId=0,
                                                            nameLend = etName.text.toString(),
                                                            amountStart = etAmount.text.toString().toDouble(),
                                                            amountEnd = etAmount.text.toString().toDouble(),
                                                            phone = countryCodePicker.fullNumber,
                                                            nameImageLending = nameImg,
                                                            imageLending = contentImg,
                                                            userId = Singleton.getInstance().globaluser?.userId!!,
                                                            dateEnd = if (etDateEnd.text?.isBlank()==true) null else etDateEnd.text.toString().toDate().time
                                                        )
                                                    )
                                                    withContext(Dispatchers.Main){
                                                        Toast.makeText(this@FormLendingActivity, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                                                        val i = Intent(this@FormLendingActivity,LendingActivity::class.java)
                                                        startActivity(i)
                                                        finishActivity()
                                                    }
                                                }catch (e: Exception){
                                                    withContext(Dispatchers.Main){
                                                        Toast.makeText(this@FormLendingActivity, "Ha ocurrido un error con la imagen", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }else{
                                            Toast.makeText(this@FormLendingActivity, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        val builder = AlertDialog.Builder(this@FormLendingActivity)
                                        val title = "¿Quieres continuar sin añadir una imagen?"
                                        builder.setMessage(title)
                                        val okText = "Si"
                                        val noText = "No"
                                        builder.setPositiveButton(okText) { dialog, which ->
                                            CoroutineScope(Dispatchers.IO).launch {
                                                Singleton.getInstance().database?.lendingDao()?.insertDbLending(
                                                    DbLending(
                                                        lendingId=0,
                                                        nameLend = etName.text.toString(),
                                                        amountStart = etAmount.text.toString().toDouble(),
                                                        amountEnd = etAmount.text.toString().toDouble(),
                                                        phone = countryCodePicker.fullNumber,
                                                        nameImageLending = nameImg,
                                                        imageLending = contentImg,
                                                        userId = Singleton.getInstance().globaluser?.userId!!,
                                                        dateEnd = if (etDateEnd.text?.isBlank()==true) null else etDateEnd.text.toString().toDate().time
                                                    )
                                                )
                                                withContext(Dispatchers.Main){
                                                    Toast.makeText(this@FormLendingActivity, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                                                    val i = Intent(this@FormLendingActivity,LendingActivity::class.java)
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
                                }
                            }else{
                                Toast.makeText(this@FormLendingActivity, "Debe escribir un numero de teléfono", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@FormLendingActivity, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@FormLendingActivity, "Debe escribir un monto", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@FormLendingActivity, "Debe escribir un nombre", Toast.LENGTH_SHORT).show()
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
        if (requestCode == CONTACT_PICK_CODE){
            if (resultCode == Activity.RESULT_OK) {
                listContacts.clear()//
                var cursor1: Cursor ?= null
                var cursor2: Cursor ?= null
                val uri = data?.data
                cursor1 = contentResolver.query(uri!!,null,null,null,null)

                if (cursor1?.moveToFirst() == true){
                    val id = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val photo = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
                    val phoneNumber = (cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                    //binding.tvTestPhone.setText("$phoneNumber")

                    //example
                    if (photo !=null){
                        //binding.ivImage.setImageURI(Uri.parse(photo))
                    }else{
                        //binding.ivImage.setImageResource(R.drawable.ic_person)
                    }


                    //Esto es si un usuario tiene mas de un numero, de momento no se usara
                    if (phoneNumber == 1){
                        cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+id,null,null)

                        while (cursor2?.moveToNext() == true){
                            val contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            listContacts.add(contactNumber)
                            //binding.tvTestPhone.append("\n $contactNumber")
                        }

                        cursor2?.close()

                        showAlertDialogContactsClicked(listContacts.toTypedArray())
                    }
                    cursor1.close()
                }
            }
        }
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

                    binding.imgFile.setImageDrawable(resources.getDrawable(getLogo(fileName)))
                    binding.tvFile.text = fileName

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

                    binding.imgFile.setImageDrawable(resources.getDrawable(getLogo(fileName)))
                    binding.tvFile.text = fileName
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
        val i = Intent(this@FormLendingActivity,LendingActivity::class.java)
        startActivity(i)
        finishActivity()
    }

    private fun pickContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@FormLendingActivity,arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACT_PERMISSION_CODE)
        } else {
            val intent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent,CONTACT_PICK_CODE)
        }
    }

    fun showAlertDialogContactsClicked(listOptions: Array<String>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@FormLendingActivity)
        builder.setTitle("Selecciona")

        builder.setItems(listOptions,
            DialogInterface.OnClickListener { dialog, which ->
                binding.etPhone.setText(listOptions[which])
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}