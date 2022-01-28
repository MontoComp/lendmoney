package com.montcomp.lendmoney.Base.DataBase.model

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "tester"
)
class DbTester(
    @PrimaryKey
    val testerCod: Int,
    val nickName: String,
    val password: String,
    val rol: Int
): Serializable