package com.adz.mlimivisual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.adz.mlimivisual.databinding.ActivityAdminBinding
import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader

class AdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminBinding
    private val handler = Handler()
    private lateinit var cirLoader: CircularDotsLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cirLoader = binding.cirLoader
        cirLoader.visibility = View.GONE
        cirLoader.defaultColor = ContextCompat.getColor(this, R.color.colorGrey)
        cirLoader.selectedColor = ContextCompat.getColor(this, R.color.colorGreyLt)
        cirLoader.bigCircleRadius = 42
        cirLoader.radius = 11
        cirLoader.animDur = 100
        cirLoader.firstShadowColor = ContextCompat.getColor(this, R.color.colorGreen)
        cirLoader.secondShadowColor = ContextCompat.getColor(this, R.color.colorGreen)
        cirLoader.showRunningShadow = true

        fun performLoading() {
            Thread {
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                handler.post {
                    val i = Intent(this, UserlistActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }.start()
        }

        binding.job.setOnClickListener {
            cirLoader.visibility = View.VISIBLE
            binding.job.visibility = View.GONE
            performLoading()
        }
    }
}