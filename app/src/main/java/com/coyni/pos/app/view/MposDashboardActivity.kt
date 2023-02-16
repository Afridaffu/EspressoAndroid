package com.coyni.pos.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityMposDashboardBinding
import com.coyni.pos.app.fragments.dashboard_frag

class MposDashboardActivity : AppCompatActivity() {

        private lateinit var appBarConfiguration: AppBarConfiguration
        private lateinit var binding: ActivityMposDashboardBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMposDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)

            initFields();
        }

        private fun initFields() {
            val screen = intent.getStringExtra("screen")
            if( screen == "qr"){
//                showfrag(sale_fragment())
            }else{
                showfrag(dashboard_frag())
            }

            binding.arrowButton.setOnClickListener { view: View? ->
                if (binding.hiddenView.visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                    binding.consLL.setBackgroundResource(R.drawable.dash_bottom_radius)
                    binding.hiddenView.visibility = View.GONE
                    binding.cvProfileSmall.visibility = View.VISIBLE
                    binding.dbaNameTV.visibility = View.VISIBLE
                    binding.arrowButton.setImageResource(R.drawable.ic_x)

                } else {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                    binding.consLL.setBackgroundResource(R.color.logout_bg)
                    binding.hiddenView.visibility = View.VISIBLE
                    binding.cvProfileSmall.visibility = View.GONE
                    binding.dbaNameTV.visibility = View.GONE
                    binding.arrowButton.setImageResource(R.drawable.ic_x_white)
                }
            }

            binding.logoutLL?.setOnClickListener {
                startActivity(Intent(applicationContext, OnboardActivity::class.java))
            }

        }

        private fun showfrag(fragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
        }
}