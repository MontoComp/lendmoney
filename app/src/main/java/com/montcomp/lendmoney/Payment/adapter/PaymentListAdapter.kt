package com.montcomp.lendmoney.Payment.adapter

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cixsolution.jzc.pevoex.Utils.invisible
import com.cixsolution.jzc.pevoex.Utils.toBitMap
import com.cixsolution.jzc.pevoex.Utils.visible
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Payment.PaymentActivity
import com.montcomp.lendmoney.R

class  PaymentListAdapter(val context: Context, var list: List<DbPayment>, val listener: (position: Int) -> Unit): RecyclerView.Adapter<PaymentListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_pay_timeline, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var itemdata=list[position]
        if (list.size == 1){
            holder.vTop.invisible()
            holder.vBottom.invisible()
        }else{
            when(position){
                list.indexOf(list.first()) -> {
                    holder.vTop.invisible()
                    holder.vBottom.visible()
                }
                list.indexOf(list.last()) -> {
                    holder.vTop.visible()
                    holder.vBottom.invisible()
                }
                else -> {
                    holder.vTop.visible()
                    holder.vBottom.visible()
                }
            }
        }
        holder.txtTitle.text= String.format("%.2f", itemdata.amount)
        holder.txtSubtitle.text=itemdata.paymentDes
        var suffix = itemdata.nameImagePay?.split(".")?.last()
        if (!itemdata.imagePay.equals("")){
            when(suffix){
                "pdf"->holder.ivPay.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_pdf))
                "doc","docx"->holder.ivPay.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_word))
                "jpg","png","gif","jpeg","iso"-> holder.ivPay.setImageBitmap(itemdata.imagePay!!.toBitMap())
                "mp3","wav"->holder.ivPay.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_audio))
                "xls","xlsx"->holder.ivPay.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_excel))
                else ->holder.ivPay.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_file_generic))
            }
        }
        holder.itemView.setOnClickListener { listener(position) }
        holder.ivPay.setOnClickListener {
            var expandedImageDialog = Dialog(context)
            expandedImageDialog.setContentView(R.layout.custom_dialog_image)

            val metrics = DisplayMetrics()
            (context as PaymentActivity)?.windowManager?.defaultDisplay?.getMetrics(metrics)

            var window = expandedImageDialog.window
            var param : WindowManager.LayoutParams = window!!.attributes
            param.gravity = Gravity.CENTER
            window.attributes = param
            window.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.round_cornered_window))

            var ivImage = expandedImageDialog.findViewById<ImageView>(R.id.iv_expanded_image)
            var tvImage = expandedImageDialog.findViewById<TextView>(R.id.tv_image)
            if (itemdata.imagePay != ""){
                ivImage.setImageBitmap(itemdata.imagePay?.toBitMap())
                tvImage.text = itemdata.nameImagePay
            }else{
                ivImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_logo_image))
                tvImage.text = "No hay imagen"
            }

            expandedImageDialog.findViewById<Button>(R.id.btn_to_accept).setOnClickListener {
                expandedImageDialog.dismiss()
            }
            expandedImageDialog.show()
        }
    }

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val txtTitle = itemView.findViewById<TextView>(R.id.tv_entity_title)
        val txtSubtitle = itemView.findViewById<TextView>(R.id.tv_entity_desc)
        val ivPay = itemView.findViewById<ImageView>(R.id.iv_pay)

        val vTop = itemView.findViewById<View>(R.id.v_top)
        val vBottom = itemView.findViewById<View>(R.id.v_bottom)
    }
}