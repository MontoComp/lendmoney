package com.cixsolution.jzc.pevoex.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.R

@SuppressLint("HardwareIds")
fun getIdAndroid() = Settings.Secure.getString(Singleton.getInstance().ctx?.contentResolver, Settings.Secure.ANDROID_ID);

fun Int.playAudio() {
    val mediaPlayer = MediaPlayer.create(
        Singleton.getInstance().ctx,
        when(this){
            1-> R.raw.correct
            else-> R.raw.alert
        })
    mediaPlayer.start()
}