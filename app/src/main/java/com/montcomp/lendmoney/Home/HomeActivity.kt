package com.montcomp.lendmoney.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.cixsolution.jzc.pevoex.GenericModule.GenericActivity
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.hcr.hcr_android.Drawer.Drawer
import com.montcomp.lendmoney.Base.DataBase.LendMoneyDataBase
import com.montcomp.lendmoney.Base.DataBase.model.DbLending
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.R
import com.montcomp.lendmoney.databinding.ActivityDrawerHomeBinding
import com.montcomp.lendmoney.databinding.ActivityHomeBinding
import com.montcomp.lendmoney.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : DisposableActivity() {
    lateinit var binding : ActivityDrawerHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = Drawer(this@HomeActivity)
        drawer.configureBurgerMenu()
        val toolbarTitle = this.findViewById(R.id.toolbar_title) as TextView?
        toolbarTitle?.setText(R.string.home)
        toolbarTitle?.setTextColor(this.resources.getColor(R.color.white))

    }
}