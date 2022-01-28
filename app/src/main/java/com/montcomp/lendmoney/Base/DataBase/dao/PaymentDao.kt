package com.montcomp.lendmoney.Base.DataBase.dao

import androidx.room.*
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment


@Dao
interface PaymentDao {
    //region DbPayment
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDbPayment(dbPayment: DbPayment)

    @Delete
    fun deleteDbPayment(dbUser: DbPayment)

    @Update
    fun updateDbPayment(dbUser: DbPayment)

    @Query("SELECT * FROM pago where paymentId = :paymentCode")
    fun getDbPayment(
        paymentCode: Int
    ) : DbPayment?

    @Query("SELECT * FROM pago where lendingId = :lendingCode")
    fun getAllDbPaymentByLendingId(
        lendingCode: Int
    ) : List<DbPayment>

    @Query("SELECT * FROM pago")
    fun getAllDbPayment() : List<DbPayment>

    @Query("DELETE FROM pago")
    fun deleteAllDbPayment()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDbPayment(listDbPayment: List<DbPayment>)
    //endregion
}