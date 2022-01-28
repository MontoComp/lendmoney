package com.montcomp.lendmoney.Payment

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cixsolution.jzc.pevoex.Utils.toFullDate
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.hcr.hcr_android.Drawer.Drawer
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.DataBase.model.DbPayment
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.Lending.adapter.LendingListAdapter
import com.montcomp.lendmoney.Payment.adapter.PaymentListAdapter
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ActivityPaymentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.text.Spanned

import android.text.InputFilter




class PaymentActivity : DisposableActivity() {

    var getPayment:ArrayList<DbPayment> = ArrayList()
    var displayList:ArrayList<DbPayment> = ArrayList()

    var search_people: SearchView? = null

    lateinit var binding : ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = Drawer(this)
        drawer.configureBurgerMenu()

        initView()
        initActions()
    }

    private fun initView() {
        binding.apply {
            tvViewLending.text = String.format("%.2f", Singleton.getInstance().tmpgloballending?.amountEnd)
            commonLayoutBack.toolbarTitleBack?.setText(R.string.payment)
            commonLayoutBack.toolbarTitleBack?.setTextColor(this@PaymentActivity.resources.getColor(R.color.white))
        }
    }

    private fun initActions() {
        binding.apply {
            commonLayoutBack.ivBack.setOnClickListener {
                onBackPressed()
            }

            rvPayment.layoutManager = LinearLayoutManager(this@PaymentActivity, LinearLayoutManager.VERTICAL,false)

            CoroutineScope(Dispatchers.IO).launch {
                getPayment.clear()
                getPayment = Singleton.getInstance().database?.paymentDao()?.getAllDbPaymentByLendingId(Singleton.getInstance().tmpgloballending?.lendingId!!) as ArrayList<DbPayment>
                withContext(Dispatchers.Main){
                    rvPayment.adapter = PaymentListAdapter(this@PaymentActivity,getPayment.sortedByDescending { it.dateCreation }){
                            position ->
                        Toast.makeText(this@PaymentActivity, "El monto es: ${getPayment[position].paymentDes}", Toast.LENGTH_SHORT).show()
                    }
                    if (getPayment.isNullOrEmpty()){
                        noReportsLayoutPeople.visibility = View.VISIBLE
                    }else{
                        noReportsLayoutPeople.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val i = Intent(this@PaymentActivity, LendingActivity::class.java)
        startActivity(i)
        finishActivity()
    }
}