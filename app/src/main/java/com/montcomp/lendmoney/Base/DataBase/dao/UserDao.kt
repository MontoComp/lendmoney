package com.montcomp.lendmoney.Base.DataBase.dao

import androidx.room.*
import com.montcomp.lendmoney.Base.DataBase.model.DbUser


@Dao
interface UserDao {
    //region DbUser
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDbUser(dbUser: DbUser)

    @Delete
    fun deleteDbUser(dbUser: DbUser)

    @Update
    fun updateDbUser(dbUser: DbUser)

    @Query("SELECT * FROM usuario where userId = :userCode")
    fun getDbUser(
        userCode: Int
    ) : DbUser?

    @Query("SELECT * FROM usuario where nickName = :userName and password= :password")
    fun getDbUserByUserAndPassword(
        userName: String?,
        password: String?
    ) : DbUser?

    @Query("SELECT * FROM usuario where nickName = :userName")
    fun getDbUserByUser(
        userName: String?
    ) : DbUser?

    @Query("SELECT * FROM usuario")
    fun getAllDbUser() : List<DbUser>

    @Query("DELETE FROM usuario")
    fun deleteAllDbUser()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDbUser(listDbUser: List<DbUser>)
    //endregion
}