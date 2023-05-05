package com.adz.mlimivisual

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat

class Enquiry : Activity() {
    var contact: TextView? = null
    var web: TextView? = null
    var email: ImageView? = null
    var call: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enquiry)
        web = findViewById(R.id.web)
        email = findViewById(R.id.email)
        call = findViewById(R.id.call)

        call!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919812589181"))
            if (ActivityCompat.checkSelfPermission(
                    this@Enquiry,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(intent)
                finish()
            }
        }
        web!!.setOnClickListener {
            val url = "http://www.hospital.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            Intent.createChooser(intent, "Open URL")
            startActivity(intent)
        }

        email!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, "addresses")
            intent.putExtra(Intent.EXTRA_SUBJECT, "subject")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

    }
}
