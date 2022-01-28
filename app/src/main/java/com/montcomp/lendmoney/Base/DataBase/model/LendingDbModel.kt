package com.montcomp.lendmoney.Base.DataBase.model

import androidx.room.*
import com.cixsolution.jzc.pevoex.Utils.toFullStringDate
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "prestamo",
    foreignKeys = [
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class DbLending(
    @PrimaryKey(autoGenerate = true)
    var lendingId: Int,
    var nameLend: String?,
    var amountStart: Double,
    var amountEnd: Double,
    var phone: String?,
    var nameImageLending: String?,
    var imageLending: String?,
    val dateCreation: Long? = Date().time,
    var dateEnd: Long?,
    var userId: Int,
): Serializable