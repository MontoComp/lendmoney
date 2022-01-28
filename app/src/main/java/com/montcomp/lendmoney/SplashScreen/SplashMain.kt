package com.montcomp.lendmoney.SplashScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.cixsolution.jzc.pevoex.GenericModule.GenericActivity

import com.cixsolution.jzc.pevoex.Utils.dpToPx
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Login.LoginActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ActivitySplashMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashMain : GenericActivity() {

    private lateinit var binding: ActivitySplashMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.green_black)
        }

        val database = LendMoneyDataBase.getDatabase(this@SplashMain)

        CoroutineScope(Dispatchers.IO).launch {
            var result = database.userDao().getAllDbUser()
            withContext(Dispatchers.Main){
                if (!result.isNullOrEmpty()){
                    Toast.makeText(this@SplashMain, "Con usuarios", Toast.LENGTH_SHORT).show()
                    result.forEach {
                        Log.d("TESTERS","${it.nickName}")
                    }
                }else{
                    Toast.makeText(this@SplashMain, "Sin usuarios", Toast.LENGTH_SHORT).show()
                }
            }
        }

        initSplashAnimation()
    }

    private fun initSplashAnimation() {

        binding.apply {
            splashContainer.postDelayed({
                val intent = Intent(this@SplashMain,LoginActivity::class.java)
                startActivity(intent)
                finish()
            },2000)
            //aclarar
            splashlogo.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

            //agrandar
            splashlogo.animate()
                .scaleX(0.75f)
                .scaleY(0.75f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(600)
                .setDuration(150)
                .start()

            //a la derecha
            splashlogo.animate()
                .translationY((125.dpToPx().toFloat()))
                //.translationX((125.dpToPx().toFloat()))
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(700)
                .setDuration(500)
                .start()

            //mover
            splashtext.animate()
                .alpha(1f)
                .translationY((-100.dpToPx().toFloat()))
                //.translationX((-100.dpToPx().toFloat()))
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(900)
                .setDuration(300)
                .start()
        }
    }
}