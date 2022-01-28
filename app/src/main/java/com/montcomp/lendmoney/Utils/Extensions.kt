package com.cixsolution.jzc.pevoex.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.text.format.Formatter
import android.util.Base64
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.cixsolution.jzc.pevoex.Base.common.ImagenesPrecargadas
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import java.io.*
import java.nio.file.Files
import java.text.DecimalFormatSymbols

fun Bitmap.toByteArray(): ByteArray{
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG,90,stream)
    return stream.toByteArray()
}

fun Bitmap.toString64(): String{
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG,100,stream)
    val bytearray = stream.toByteArray()
    return Base64.encodeToString(bytearray,Base64.NO_WRAP)
}

fun String.toBitMap(): Bitmap{
    return try {
        val decodedString = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.count())
    }catch (e: Exception){
        var errorImage = Base64.decode(ImagenesPrecargadas.images.tipo_TipoDefault, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(errorImage, 0, errorImage.count())
    }
}
fun File.pdfToBase64():String{
    var byteArray:ByteArray?=null
    byteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Files.readAllBytes(this.toPath())
    } else {
        this.read()
    }
    return Base64.encodeToString(byteArray as ByteArray?, Base64.NO_WRAP)
}

fun File.read(): ByteArray? {
    var ous: ByteArrayOutputStream? = null
    var ios: InputStream? = null
    try {
        val buffer = ByteArray(4096)
        ous = ByteArrayOutputStream()
        ios = FileInputStream(this)
        var read = 0
        while (ios.read(buffer).also({ read = it }) != -1) {
            ous.write(buffer, 0, read)
        }
    } finally {
        try {
            ous?.close()
        } catch (e: IOException) {
        }
        try {
            if (ios != null) ios.close()
        } catch (e: IOException) {
        }
    }
    return ous!!.toByteArray()
}

fun isNetworkAvailable(): Boolean {
    if (Singleton.getInstance().ctx == null) return false
    val connectivityManager = Singleton.getInstance().ctx?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun getIpGeneral(): String{
    return if (isNetworkAvailable()){
        val wm = Singleton.getInstance().ctx?.applicationContext?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }else{
        "null"
    }
}

fun EditText.limitDecimal(){
    this.filters = arrayOf<InputFilter>(object : InputFilter {
        var decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols()
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val indexPoint: Int =
                dest.toString().indexOf(decimalFormatSymbols.decimalSeparator)
            if (indexPoint == -1) return source
            val decimals = dend - (indexPoint + 1)
            return if (decimals < 2) source else ""
        }
    }
    )
}

