package com.montcomp.lendmoney.Base.DataBase.dao

import androidx.room.*
import com.montcomp.lendmoney.Base.DataBase.model.DbTester


@Dao
interface TesterDao {
    //region DbTester
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDbTester(dbTester: DbTester)

    @Delete
    fun deleteDbTester(dbTester: DbTester)

    @Update
    fun updateDbTester(dbTester: DbTester)

    @Query("SELECT * FROM tester where testerCod = :testerCode")
    fun getDbTester(
        testerCode: Int
    ) : DbTester?

    @Query("SELECT * FROM tester")
    fun getAllDbTester() : List<DbTester>

    @Query("DELETE FROM tester")
    fun deleteAllDbTester()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDbTester(listDbTester: List<DbTester>)
    //endregion
}