package com.montcomp.lendmoney.Base.DataBase.dao

import androidx.room.*
import com.montcomp.lendmoney.Base.DataBase.model.DbLending

@Dao
interface LendingDao {
    //region DbLending
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDbLending(dbLending: DbLending)

    @Delete
    fun deleteDbLending(dbLending: DbLending)

    @Update
    fun updateDbLending(dbLending: DbLending)

    @Query("SELECT * FROM prestamo where lendingId = :lendingCode")
    fun getDbLending(
        lendingCode: Int
    ) : DbLending?

    @Query("SELECT * FROM prestamo")
    fun getAllDbLending() : List<DbLending>

    @Query("SELECT * FROM prestamo where userId=:userCode")
    fun getAllDbLendingByUserId(
        userCode: Int
    ) : List<DbLending>

    @Query("DELETE FROM prestamo")
    fun deleteAllDbLending()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDbLending(listDbLending: List<DbLending>)
    //endregion
}