package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val TAG = AuthenticationActivity::class.java.simpleName

    private val viewModel by viewModels<AuthenticationViewModel>()

    private lateinit var binding: ActivityAuthenticationBinding

    private val SIGN_IN_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeAuthentication()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        binding.btnLogin.setOnClickListener {
            openSignInPage()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        observeAuthentication()
    }

    private fun observeAuthentication() {
        viewModel.authenticationState.observe(this, Observer{ authenticateState ->
            when (authenticateState) {
                AuthenticationState.AUTHENTICATED -> {
                    // Go to Reminder Activity
                    Log.d(TAG, "onCreate: Authenticated")
                    startRemindersActivity()
                }
                else -> {
                    // Do nothing
                    Log.d(TAG, "onCreate: Unauthenticated")
                }
            }
        })

    }


    private fun openSignInPage() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.custom_auth_layout)
            .setGoogleButtonId(R.id.btn_google)
            .setEmailButtonId(R.id.btn_email)
            .build()

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAuthMethodPickerLayout(customLayout)
            .build()

        startActivityForResult(intent, SIGN_IN_REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                startRemindersActivity()
            } else {
                // Sign in failed
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")

            }
        }
    }

    private fun startRemindersActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
    }

}
