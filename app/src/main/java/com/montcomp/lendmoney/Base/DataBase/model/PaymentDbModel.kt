package com.montcomp.lendmoney.Base.DataBase.model

import androidx.room.*
import com.cixsolution.jzc.pevoex.Utils.toFullStringDate
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "pago",
    foreignKeys = [
        ForeignKey(
            entity = DbLending::class,
            parentColumns = ["lendingId"],
            childColumns = ["lendingId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class DbPayment(
    @PrimaryKey(autoGenerate = true)
    var paymentId: Int,
    var amount: Double,
    var paymentDes: String,
    var nameImagePay: String?,
    var imagePay: String?,
    val dateCreation: Long? = Date().time,
    var lendingId: Int,
) :Serializable