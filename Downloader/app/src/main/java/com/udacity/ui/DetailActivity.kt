package com.udacity.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.EXTRA_DOWNLOAD_STATUS
import com.udacity.EXTRA_SELECTED_LINK
import com.udacity.R
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val link = intent.extras?.getString(EXTRA_SELECTED_LINK)
        val status = intent.extras?.getBoolean(EXTRA_DOWNLOAD_STATUS)?.or(false)

        txt_link.text = link
        val statusText: String
        val statusColor: Int

        if (status != null && status) {
            statusText = getString(R.string.success_status)
            statusColor = R.color.colorPrimaryDark
        } else {
            statusText = getString(R.string.failed_status)
            statusColor = R.color.colorFailed
        }

        txt_status.apply {
            text = statusText
            setTextColor(ContextCompat.getColor(context, statusColor))
        }

        btn_ok.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

}
