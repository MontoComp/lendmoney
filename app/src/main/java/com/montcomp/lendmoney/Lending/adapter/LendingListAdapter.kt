package com.montcomp.lendmoney.Lending.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cixsolution.jzc.pevoex.Utils.betweenDays
import com.cixsolution.jzc.pevoex.Utils.gone
import com.cixsolution.jzc.pevoex.Utils.visible
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.DetailLending.DetailLendingActivity
import com.montcomp.lendmoney.FormPayment.FormPaymentActivity
import com.montcomp.lendmoney.Payment.PaymentActivity
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ItemLendingBinding
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class LendingListAdapter (
    private val itemClick: ElementClick
        ) : ListAdapter<DbLending, LendingListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
            .from(parent?.context)
            .inflate(R.layout.item_lending, parent, false)
        return ViewHolder(v, itemClick)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDbLending(getItem(position),position)
    }

    inner class ViewHolder(
        override val containerView: View,
        private val itemClick: ElementClick,
    ): RecyclerView.ViewHolder(containerView), LayoutContainer{
        fun bindDbLending(item:DbLending, posc: Int){
            val binding = ItemLendingBinding.bind(containerView)
            binding.apply {
                if (item.amountEnd >0){
                    itemCuest.background.setColorFilter(containerView.resources.getColor(R.color.white_200),PorterDuff.Mode.SRC_IN)
                    lyPay.visible()
                }else{
                    itemCuest.background.setColorFilter(containerView.resources.getColor(R.color.purple_200),PorterDuff.Mode.SRC_IN)
                    lyPay.gone()
                }
                if (item.dateEnd != null && betweenDays(item.dateEnd!!, Date().time) <0){
                    itemCuest.background.setColorFilter(containerView.resources.getColor(R.color.red),PorterDuff.Mode.SRC_IN)
                }
                tvEntityTitle.text=item.nameLend.toString()
                tvEntityDesc.text = String.format("%.2f", item.amountEnd)
                lyPay.setOnClickListener {
                    Singleton.getInstance().tmpgloballending = item
                    val i = Intent(containerView.context,FormPaymentActivity::class.java)
                    containerView.context.startActivity(i)
                }
                lyDetailLending.setOnClickListener {
                    Singleton.getInstance().tmpgloballending = item
                    val i = Intent(containerView.context,DetailLendingActivity::class.java)
                    containerView.context.startActivity(i)
                }
                lyViewPays.setOnClickListener {
                    Singleton.getInstance().tmpgloballending = item
                    val i = Intent(containerView.context,PaymentActivity::class.java)
                    containerView.context.startActivity(i)
                }

                ivDeleteLending.setOnClickListener {
                    itemClick.onDeleteItem(item)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DbLending>(){
            override fun areItemsTheSame(oldItem: DbLending, newItem: DbLending): Boolean {
                return oldItem.lendingId == newItem.lendingId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DbLending, newItem: DbLending): Boolean {
                return oldItem == newItem
            }

        }
    }
    interface ElementClick{
        fun onDeleteItem(item: DbLending)
    }
}
