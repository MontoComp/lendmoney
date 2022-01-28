package com.montcomp.lendmoney.Register

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.montcomp.lendmoney.Base.DataBase.model.DbUser
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.Login.LoginActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.Utils.DesignBackground
import com.montcomp.lendmoney.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import com.cixsolution.jzc.pevoex.Utils.toString64
import android.content.DialogInterface





class RegisterActivity : DisposableActivity() {
    private val pickImage = 100
    private val cameraImage = 1
    var newImage : String ?= null

    lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initActions()

    }

    private fun initView() {
        binding.apply {
            rbMale.isChecked = true
            commonLayoutBack.toolbarTitleBack?.setText(R.string.register)
            commonLayoutBack.toolbarTitleBack?.setTextColor(this@RegisterActivity.resources.getColor(R.color.white))
        }
    }

    private fun initActions() {
        binding.apply {
            commonLayoutBack.ivBack.setOnClickListener {
                onBackPressed()
            }

            ivChooseImage.setOnClickListener {
                showAlertDialogButtonClicked()
            }

            btnSaveUser.setOnClickListener {
                if (etNickname.text?.isBlank()==false){
                    if (etPassword.text?.isBlank()==false){
                        if (etPasswordConfirm.text?.isBlank()==false){
                            if (etAge.text?.isBlank()==false){
                                if (etPassword.text.toString() == etPasswordConfirm.text.toString()){
                                    displayWaiting()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Singleton.getInstance().database?.userDao()?.insertDbUser(
                                            DbUser(
                                                0,
                                                etNickname.text?.toString(),
                                                etPassword.text?.toString(),
                                                etPasswordConfirm.text?.toString(),
                                                etAge.text?.toString()?.toInt(),
                                                if(rbMale.isChecked) 1 else 0,
                                                newImage,
                                                1,
                                                false
                                            )
                                        )
                                        withContext(Dispatchers.Main){
                                            hideWaiting()
                                            Toast.makeText(this@RegisterActivity, "Usuario creado", Toast.LENGTH_SHORT).show()
                                            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                                            startActivity(i)
                                            finishActivity()
                                        }
                                    }
                                }else{
                                    Toast.makeText(this@RegisterActivity, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this@RegisterActivity, "Debe escribir su edad", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@RegisterActivity, "Debe escribir la confirmación de su contraseña", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@RegisterActivity, "Debe escribir una contraseña", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@RegisterActivity, "Debe escribir un usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(i)
        finishActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
            newImage = bitmap.toString64()
            binding.ivImageUser.setImageBitmap(bitmap)
        }
        if (resultCode == RESULT_OK && requestCode == cameraImage) {
            val bitmap = data?.extras?.get("data") as Bitmap
            newImage = bitmap.toString64()
            binding.ivImageUser.setImageBitmap(bitmap)
        }
    }

    fun showAlertDialogButtonClicked() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setTitle("Selecciona")

        val options = arrayOf("Cámara", "Galería")
        builder.setItems(options,
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {
                        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(camera, cameraImage)
                    }
                    1 -> {
                        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        startActivityForResult(gallery, pickImage)
                    }
                    else -> {
                        Toast.makeText(this@RegisterActivity, "Nada", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}