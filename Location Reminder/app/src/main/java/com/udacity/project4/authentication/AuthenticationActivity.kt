package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val TAG = AuthenticationActivity::class.java.simpleName

    private val SIGN_IN_RESULT_CODE = 1001

    private val viewModel by viewModels<AuthenticationViewModel>()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        val response = IdpResponse.fromResultIntent(it.data)
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d(
                TAG,
                "Successfully signed in user: ${FirebaseAuth.getInstance().currentUser?.displayName}"
            )
        } else {
            Log.d(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeAuthentication()
        setContentView(R.layout.activity_authentication)

        btn_login.setOnClickListener {
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
                    finish()
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

        activityResultLauncher.launch(intent)

    }

}
