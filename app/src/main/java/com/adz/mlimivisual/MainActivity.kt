package com.adz.mlimivisual

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.adz.mlimivisual.Repository.AppRepo
import com.adz.mlimivisual.databinding.ActivityMainBinding
import com.adz.mlimivisual.fragments.*
import com.adz.mlimivisual.utils.AppUtil
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HomeFragment.MainActivityInterface {

    lateinit var navigationView: NavigationView
    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    lateinit var menu: Menu
    var fragment: Fragment? = null
    lateinit var drawerLayout: DrawerLayout
    private val appUtil = AppUtil()
    private var appRepo = AppRepo.StaticFunction.getInstance()

    companion object {
        fun getInstance(): MainActivity = MainActivity()
    }

     override fun currentUserExist():Boolean {
        if (appUtil.getUID() != null){
            return true
        }else{
            Snackbar.make(
                findViewById(android.R.id.content),
                "Kindly login from the menu ",
                Snackbar.LENGTH_LONG
            ).setAction("Log In") {
                // Handle the action click, if required
            }.show()


        }
        return false
    }

    private fun currentUserExist2():Boolean {
        if (appUtil.getUID() != null){
            return true
        }
        return false
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    private fun checknetwork(): Boolean {
        if (!isNetworkConnected(this)) {
            val rootView = findViewById<View>(android.R.id.content)
            showSnackbar(rootView, "No internet connection")
            return false
        }
        return true
    }

    //private val viewModel: LogInViewModel by activityViewModels()

    val mainVM: SharedViewModel by lazy { ViewModelProvider(this)[SharedViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())

        navController = findNavController(R.id.fragmentContainerView)

        val homeFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? HomeFragment
        homeFragment?.mainActivityInterface = this

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.side_navigation)
        toolbar = findViewById(R.id.tool_bar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        navigationView.bringToFront()
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        navigationView.setCheckedItem(R.id.nav_home)

        menu = navigationView.menu


        if (currentUserExist()) {
            menu.findItem(R.id.nav_login).isVisible = true
        }else{
            menu.findItem(R.id.nav_logout).isVisible = false
            menu.findItem(R.id.nav_profile).isVisible = false
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_chat -> {
                    if(checknetwork()) {
                        if (currentUserExist()) {
                            navController.navigate(R.id.chatFragment)
                            fragment = ChatFragment()
                        }
                    }
                    true
                }
                R.id.nav_profile -> {
                    if(checknetwork()) {
                        if (currentUserExist()) {
                            navController.navigate(R.id.profileFragment)
                            fragment = ProfileFragment()
                        }
                    }
                    true
                }
                R.id.nav_search -> {
                    if(checknetwork()) {
                        if (currentUserExist()) {
                            navController.navigate(R.id.ContactUsFragment)
                            fragment = ContactUsFragment()
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
           super.onBackPressed()
        }

    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {

            R.id.nav_home -> {
                lifecycleScope.launch {
                    navController.navigate(R.id.homeFragment)
                }
            }

            R.id.nav_appointments -> {
                val i = Intent(applicationContext, VisionActivity::class.java)
                startActivity(i)
            }

            R.id.nav_login -> { navController.navigate(R.id.GetUserNumberFragment)
                if(currentUserExist2()) {
                    menu.findItem(R.id.nav_logout).isVisible = true
                    menu.findItem(R.id.nav_profile).isVisible = true
                    menu.findItem(R.id.nav_login).isVisible = false
                }
            }
            R.id.nav_logout -> {
                appUtil.signOut()
                menu.findItem(R.id.nav_logout).isVisible = false
                menu.findItem(R.id.nav_profile).isVisible = false
                menu.findItem(R.id.nav_login).isVisible = true
            }

            R.id.nav_profile -> {
                lifecycleScope.launch {
                    navController.navigate(R.id.profileFragment)
                }
            }

            R.id.nav_setting -> {
                val i = Intent(applicationContext, AboutActivity::class.java)
                startActivity(i)
            }

            R.id.nav_workWithUs -> {
                val i = Intent(applicationContext, AgentActivity::class.java)
                startActivity(i)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView,fragment)
        transaction.commit()
    }



}