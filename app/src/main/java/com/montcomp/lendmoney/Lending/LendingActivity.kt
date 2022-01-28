package com.montcomp.lendmoney.Lending

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cixsolution.jzc.pevoex.Utils.toFullDate
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.hcr.hcr_android.Drawer.Drawer
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.FormLending.FormLendingActivity
import com.montcomp.lendmoney.Lending.adapter.LendingListAdapter
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ActivityDrawerLendingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.stream.Collectors

class LendingActivity : DisposableActivity() {
    lateinit var binding : ActivityDrawerLendingBinding

    var getLending:ArrayList<DbLending> = ArrayList()
    var displayList:ArrayList<DbLending> = ArrayList()

    var search_people: SearchView? = null

    private var amouLendingTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerLendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = Drawer(this)
        drawer.configureBurgerMenu()
        val toolbarTitle = this.findViewById(R.id.toolbar_title) as TextView?
        toolbarTitle?.setText(R.string.lending)
        toolbarTitle?.setTextColor(this.resources.getColor(R.color.white))

        intiViews()
        initActions()
    }

    private fun intiViews() {
        binding.actLending.apply {
            rvLending.layoutManager = LinearLayoutManager(this@LendingActivity, LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun initActions() {
        binding.actLending.apply {
            fbAddLending.setOnClickListener {
                val i = Intent(this@LendingActivity,FormLendingActivity::class.java)
                startActivity(i)
                finishActivity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showListLending()
    }

    fun showListLending(){
        binding.actLending.apply {
            CoroutineScope(Dispatchers.IO).launch {
                amouLendingTotal = 0.0
                getLending.clear()
                getLending = Singleton.getInstance().database?.lendingDao()?.getAllDbLendingByUserId(Singleton.getInstance().globaluser?.userId!!) as ArrayList<DbLending>

                getLending.forEach {
                    amouLendingTotal += it.amountEnd
                }

                withContext(Dispatchers.Main){

                    tvViewLendingTotal.text = String.format("%.2f", amouLendingTotal)

                    rvLending.adapter = LendingListAdapter(object : LendingListAdapter.ElementClick{
                        override fun onDeleteItem(item: DbLending) {

                            val builder = AlertDialog.Builder(this@LendingActivity)
                            val title = "¿Esta seguro de eliminar el préstamo de ${item.nameLend}?"
                            builder.setMessage(title)
                            val okText = "Si"
                            val noText = "No"
                            builder.setPositiveButton(okText) { dialog, which ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    Singleton.getInstance().database?.lendingDao()?.deleteDbLending(item)
                                    withContext(Dispatchers.Main){
                                        showListLending()
                                    }
                                }
                                dialog.dismiss()
                            }
                            builder.setNegativeButton(noText) { dialog, which ->
                                dialog.dismiss()
                            }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                    })
                    (rvLending.adapter as LendingListAdapter).submitList(getLending.sortedByDescending { it.dateCreation }.toMutableList())

                    if (getLending.isNullOrEmpty()){
                        noReportsLayoutPeople.visibility = View.VISIBLE
                    }else{
                        noReportsLayoutPeople.visibility = View.GONE
                    }
                }
            }
        }
    }

}