package com.montcomp.lendmoney.Login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.montcomp.lendmoney.Base.DataBase.model.DbUser
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Home.HomeActivity
import com.montcomp.lendmoney.Register.RegisterActivity
import com.montcomp.lendmoney.Utils.DesignBackground
import com.montcomp.lendmoney.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.cixsolution.jzc.pevoex.Utils.toString64
import com.squareup.picasso.Picasso
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import android.os.AsyncTask
import com.montcomp.lendmoney.Utils.FileUtils
import java.util.concurrent.ExecutionException
import android.app.Dialog
import java.lang.String
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.cixsolution.jzc.pevoex.Utils.toBitMap
import com.google.android.material.textfield.TextInputLayout
import com.montcomp.lendmoney.R
import java.net.MalformedURLException


class LoginActivity : DisposableActivity() {
    lateinit var binding : ActivityLoginBinding

    lateinit var mGoogleSignInClient: GoogleSignInClient
    var RC_SIGN_IN : Int = 0
    private var userGeneral : DbUser ?= null
    var ageGoogle:Int ?= null
    var genderGoogle:Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.root.addView(DesignBackground(this@LoginActivity),0)
        setContentView(binding.root)

        //TODO importante para que se pueda ejecutar
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        initViews()
        initActions()

    }

    private fun initViews() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private fun initActions() {
        binding.apply {
            cvGoogle.setOnClickListener {
                val acct = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
                if (acct == null){
                    var formgoogleDialog = Dialog(this@LoginActivity)
                    formgoogleDialog.setContentView(R.layout.custom_dialog_form_google)

                    val metrics = DisplayMetrics()
                    this@LoginActivity.windowManager?.defaultDisplay?.getMetrics(metrics)

                    var window = formgoogleDialog.window
                    var param : WindowManager.LayoutParams = window!!.attributes
                    param.gravity = Gravity.CENTER
                    window.attributes = param
                    window.setBackgroundDrawable(
                        ContextCompat.getDrawable(this@LoginActivity, R.drawable.round_cornered_window))

                    var edittextAge = formgoogleDialog.findViewById<TextInputLayout>(R.id.text_input_age)
                    var rbMale = formgoogleDialog.findViewById<RadioButton>(R.id.rb_male)
                    rbMale.isChecked = true

                    formgoogleDialog.findViewById<Button>(R.id.btn_to_accept).setOnClickListener {
                        if (!edittextAge.editText?.text.isNullOrBlank()){
                            ageGoogle = edittextAge.editText?.text.toString().toInt()
                            genderGoogle = if(rbMale.isChecked) 1 else 0
                            val signInIntent = mGoogleSignInClient.signInIntent
                            startActivityForResult(signInIntent, RC_SIGN_IN)
                            formgoogleDialog.dismiss()
                        }else{
                            Toast.makeText(this@LoginActivity, "Debe escribir una edad", Toast.LENGTH_SHORT).show()
                        }
                    }

                    formgoogleDialog.show()
                }else{
                    val signInIntent = mGoogleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }

            btnLogin.setOnClickListener {

                if (etNickname.text?.isBlank()==false){
                    if (etPassword.text?.isBlank()==false){
                        displayWaitingLogin()
                        CoroutineScope(Dispatchers.IO).launch {
                            userGeneral = Singleton.getInstance().database?.userDao()?.getDbUserByUserAndPassword(etNickname.text?.toString(),etPassword.text?.toString())
                            withContext(Dispatchers.Main){
                                clearFlag()
                                hideWaiting()
                                if (userGeneral!=null){
                                    Singleton.getInstance().globaluser = userGeneral
                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finishActivity()
                                }else{
                                    Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }else{
                        Toast.makeText(this@LoginActivity, "Debe escribir una contraseña", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@LoginActivity, "Debe escribir un usuario", Toast.LENGTH_SHORT).show()
                }
            }

            tvForgotPassword.setOnClickListener {
                Toast.makeText(this@LoginActivity, "No hace nada xd", Toast.LENGTH_SHORT).show()
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finishActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //updateUI(account)
            //Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_SHORT).show()

            val acct = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl

                Log.d("URIGOOGLE","${personPhoto?.path}-${acct.photoUrl}")
                Log.d("OTHER","${acct.account?.name}-${acct.account?.type}")
                var image: Bitmap? = null
                try{
                    val url = URL(personPhoto.toString())
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }catch (e : MalformedURLException){
                    Log.e("URI","${e}")
                }
                CoroutineScope(Dispatchers.IO).launch {
                    userGeneral = Singleton.getInstance().database?.userDao()?.getDbUserByUser(personName)
                    withContext(Dispatchers.Main){
                        if (userGeneral!=null){
                            CoroutineScope(Dispatchers.IO).launch {
                                Singleton.getInstance().database?.userDao()?.updateDbUser(
                                    DbUser(
                                        userGeneral!!.userId,
                                        userGeneral!!.nickName,
                                        userGeneral!!.password,
                                        userGeneral!!.passwordConfirm,
                                        ageGoogle,
                                        genderGoogle,
                                        userGeneral!!.image,
                                        userGeneral!!.rol,
                                        userGeneral!!.state,
                                        email = userGeneral!!.email
                                    )
                                )

                                userGeneral = Singleton.getInstance().database?.userDao()?.getDbUserByUser(personName)

                                withContext(Dispatchers.Main){
                                    Singleton.getInstance().globaluser = userGeneral
                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finishActivity()
                                }
                            }

                        }else{
                            CoroutineScope(Dispatchers.IO).launch {
                                Singleton.getInstance().database?.userDao()?.insertDbUser(
                                    DbUser(
                                        0,
                                        personName,
                                        null,
                                        null,
                                        ageGoogle,
                                        genderGoogle,
                                        image?.toString64(),
                                        1,
                                        false,
                                        email = personEmail
                                    )
                                )
                                withContext(Dispatchers.Main){
                                    Toast.makeText(this@LoginActivity, "Usuario añadido a la base de datos", Toast.LENGTH_SHORT).show()
                                    /*val i = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(i)
                                    finishActivity()*/
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }

}