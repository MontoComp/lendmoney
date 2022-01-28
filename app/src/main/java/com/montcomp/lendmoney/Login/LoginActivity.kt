package com.montcomp.lendmoney.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.cixsolution.jzc.pevoex.GenericModule.GenericActivity
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbUser
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Home.HomeActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.Register.RegisterActivity
import com.montcomp.lendmoney.Utils.DesignBackground
import com.montcomp.lendmoney.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : DisposableActivity() {
    lateinit var binding : ActivityLoginBinding


    private var userGeneral : DbUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.root.addView(DesignBackground(this@LoginActivity),0)
        setContentView(binding.root)

        initActions()

    }

    private fun initActions() {
        binding.apply {
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
}