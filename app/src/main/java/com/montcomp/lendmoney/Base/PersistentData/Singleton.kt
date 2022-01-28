package com.montcomp.lendmoney.Base.PersistentData

import android.content.Context
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbUser

class Singleton {
    var ctx: Context? = null
    var globaluser: DbUser ?= null
    var tmpgloballending: DbLending ?= null
    var database : LendMoneyDataBase ?= null
    companion object{
        private val instance = Singleton()
        fun getInstance(): Singleton{
            return instance
        }
    }
}