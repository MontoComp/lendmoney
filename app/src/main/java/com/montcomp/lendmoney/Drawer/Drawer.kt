package com.hcr.hcr_android.Drawer

import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.cixsolution.jzc.pevoex.Utils.toBitMap
import com.everis.zentros.Base.NetworkLayer.DisposableActivity
import com.google.android.material.navigation.NavigationView
import com.montcomp.lendmoney.Base.PersistentData.Singleton
import com.montcomp.lendmoney.Home.HomeActivity
import com.montcomp.lendmoney.Lending.LendingActivity
import com.montcomp.lendmoney.Login.LoginActivity
import com.montcomp.lendmoney.Payment.PaymentActivity
import com.montcomp.lendmoney.R

open class Drawer(val outContext: DisposableActivity): NavigationView.OnNavigationItemSelectedListener {

    var context: DisposableActivity = outContext
    internal lateinit var drawer: DrawerLayout
    internal lateinit var toggle: ActionBarDrawerToggle

    fun configureBurgerMenu() {
        val toolbar = context.findViewById(R.id.common_toolbar) as? Toolbar
        val tb = toolbar ?: return
        tb.setNavigationIcon(R.drawable.ic_menu)
        toolbar?.navigationIcon?.setColorFilter(outContext.resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        toolbar?.navigationIcon?.minimumWidth
        context.setSupportActionBar(tb)

        //drawer
        drawer = context.findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(context, drawer, tb, R.string.home, R.string.home)
        drawer?.addDrawerListener(toggle!!)
        context.supportActionBar?.setHomeButtonEnabled(true)

        //configureNavigation
        val navigationView: NavigationView? = context.findViewById(R.id.nav_view_container)
        val headerView = navigationView?.getHeaderView(0)
        val navUsername = headerView?.findViewById(R.id.nav_header_textView) as TextView?
        val navUsericon = headerView?.findViewById(R.id.icon_drawer_type) as ImageView?
        val navUserImage = headerView?.findViewById(R.id.iv_image_user) as ImageView?
        val nav_Menu = navigationView?.getMenu()

        if (Singleton.getInstance().globaluser?.gender == 1){
            navUsericon?.setImageResource(R.drawable.hombre)
        }else{
            navUsericon?.setImageResource(R.drawable.mujer)
        }
        navUsericon?.setColorFilter(outContext.resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)

        if (Singleton.getInstance().globaluser?.image !=null){
            navUserImage?.setImageBitmap(Singleton.getInstance().globaluser?.image?.toBitMap())
        }else{
            if (Singleton.getInstance().globaluser?.gender == 1){
                when(Singleton.getInstance().globaluser?.age){
                    in 0..5->{
                        navUserImage?.setImageResource(R.mipmap.user_babyboy)
                    }
                    in 6..15->{
                        navUserImage?.setImageResource(R.mipmap.user_boy)
                    }
                    in 16..50->{
                        navUserImage?.setImageResource(R.mipmap.user_male)
                    }
                    in 51..999->{
                        navUserImage?.setImageResource(R.mipmap.user_old_male)
                    }
                    else ->{
                        navUserImage?.setImageResource(R.mipmap.user_general)
                    }
                }
            }else{
                when(Singleton.getInstance().globaluser?.age){
                    in 0..5->{
                        navUserImage?.setImageResource(R.mipmap.user_babygirl)
                    }
                    in 6..15->{
                        navUserImage?.setImageResource(R.mipmap.user_girl)
                    }
                    in 16..50->{
                        navUserImage?.setImageResource(R.mipmap.user_female)
                    }
                    in 51..999->{
                        navUserImage?.setImageResource(R.mipmap.user_old_female)
                    }
                    else ->{
                        navUserImage?.setImageResource(R.mipmap.user_general)
                    }
                }
            }
        }

        val userName =  Singleton.getInstance().globaluser?.nickName?.capitalize()
        navUsername?.text = userName

        navigationView?.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Home -> {
                checkSameClass(HomeActivity::class.java)
            }
            R.id.Lending -> {
                checkSameClass(LendingActivity::class.java)
            }
            R.id.logout -> {
                logout()
            }
        }
        return true
    }

    private fun checkSameClass(classVar: Class<out DisposableActivity>) {
        if(context.javaClass == classVar) {
            context.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawers()
        } else {
            context.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawers()
            val activity = Intent(context, classVar)
            context.startActivity(activity)
        }
    }

    private fun logout() {
        val builder = AlertDialog.Builder(context)
        val title = "¿Quieres cerrar la sesión?"
        builder.setMessage(title)
        val okText = "Si"
        val noText = "No"
        builder.setPositiveButton(okText) { dialog, which ->
            val intent = Intent(context, LoginActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
            dialog.dismiss()
            (context as? DisposableActivity)?.finish()
            /*mylogout(Keys.UserIdData.USERID.value, Keys.UserData.TOKEN.value,{
                (context as? DisposableActivity)?.finish()
            },{
                (context as? DisposableActivity)?.finish()
            })*/
            /*DrawerRouter.configure(this)
            presenter?.logout()*/

        }
        builder.setNegativeButton(noText) { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun mylogout(userID: Int, token: String, success: () -> Unit, failed: () -> Unit) {

        /*val request = LogoutRequest(userID, token)
        disposable = apiService.logout(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            result ->
                            success()
                        },
                        { error ->
                            failed()
                        }
                )*/
    }
}
