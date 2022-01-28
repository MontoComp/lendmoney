package com.everis.zentros.Base.NetworkLayer

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.R
import java.io.*


open class DisposableActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Singleton.getInstance().ctx = this
        Singleton.getInstance().database = LendMoneyDataBase.getDatabase(this)
        val toolbar = findViewById<View>(R.id.common_toolbar) as? Toolbar
        setSupportActionBar(toolbar)
    }

    fun displayWaiting() {
        val progressBarHolder = findViewById(R.id.common_waiting_container) as? RelativeLayout
        val inAnimation = AlphaAnimation(0f, 1f)
        progressBarHolder?.animation = inAnimation
        progressBarHolder?.visibility = View.VISIBLE

    }

    fun displayWaitingLogin() {
        displayWaiting()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
    fun clearFlag(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun hideWaiting() {
        val progressBarHolder = findViewById<View>(R.id.common_waiting_container) as? RelativeLayout
        val outAnimation = AlphaAnimation(1f, 0f)
        progressBarHolder?.animation = outAnimation
        progressBarHolder?.visibility = View.GONE
    }


    fun enableToolbarHomeButton() {
        getSupportActionBar()?.setHomeButtonEnabled(true)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    fun imageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun finishActivity(){
        finish()
    }

    override fun onPause() {
        super.onPause()
        //disposable?.dispose()
    }
}