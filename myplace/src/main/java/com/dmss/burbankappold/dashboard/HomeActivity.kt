package com.dmss.burbankappold.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ActivityHomeBinding
import com.dmss.burbankappold.homecare.HomeCareDashboardActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView(){

        binding.rlBuildProcess.setOnClickListener {
        startActivity(Intent(this, DashboardNewActivity::class.java))

        }
        binding.rlHomeCare.setOnClickListener {
            startActivity(Intent(this, HomeCareDashboardActivity::class.java))
            finish()

        }
    }
}