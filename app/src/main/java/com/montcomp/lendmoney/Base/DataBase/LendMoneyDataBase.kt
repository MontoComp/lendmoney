package com.montcomp.lendmoney.Base.DataBase


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.montcomp.lendmoney.Base.DataBase.dao.LendingDao
import com.montcomp.lendmoney.Base.DataBase.dao.PaymentDao
import com.montcomp.lendmoney.Base.DataBase.dao.TesterDao
import com.montcomp.lendmoney.Base.DataBase.dao.UserDao
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment
import com.montcomp.lendmoney.Base.DataBase.model.DbTester
import com.montcomp.lendmoney.Base.DataBase.model.DbUser

@Database(entities = [DbLending::class,DbPayment::class,DbUser::class,DbTester::class], version = 1,exportSchema = false)
abstract class LendMoneyDataBase : RoomDatabase() {

    abstract fun lendingDao(): LendingDao
    abstract fun userDao(): UserDao
    abstract fun paymentDao(): PaymentDao
    abstract fun testerDao(): TesterDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("")
            }
        }

        @Volatile
        private var INSTANCE: LendMoneyDataBase? = null

        fun getDatabase(context: Context): LendMoneyDataBase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LendMoneyDataBase::class.java,
                    "app_database"
                ).createFromAsset("databases/lend_money.db")
                    //.fallbackToDestructiveMigration()
                .build()
                //   /|\   /|\  .fallbackToDestructiveMigration()
                INSTANCE = instance

                return instance
            }
        }
    }
}