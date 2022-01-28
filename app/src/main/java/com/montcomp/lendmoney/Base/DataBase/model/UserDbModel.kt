package com.montcomp.lendmoney.Base.DataBase.model

import androidx.room.*
import com.cixsolution.jzc.pevoex.Utils.toFullStringDate
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "usuario"
)
class DbUser(
    @PrimaryKey(autoGenerate = true)
    val userId: Int,
    val nickName: String?,
    val password: String?,
    val passwordConfirm: String?,
    val age: Int?,
    val gender: Int?,
    val image: String?,
    val rol: Int?,
    val state: Boolean,
    val dateCreation: Long? = Date().time
): Serializable