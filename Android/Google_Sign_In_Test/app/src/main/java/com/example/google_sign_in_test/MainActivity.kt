package com.example.google_sign_in_test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val RC_SIGN_IN = 9001

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mStatusTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        mStatusTextView = findViewById<TextView>(R.id.status)

        // Button listeners
        findViewById<View>(R.id.sign_in_button).setOnClickListener(this)
        findViewById<View>(R.id.sign_out_button).setOnClickListener(this)
        findViewById<View>(R.id.disconnect_button).setOnClickListener(this)

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT)
        // [END customize_button]
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
            R.id.disconnect_button -> revokeAccess()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                updateUI(null)
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                updateUI(null)
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Google sign in failed:" + e.statusCode, Toast.LENGTH_LONG).show()
            updateUI(null)
        }
    }


    // [END revokeAccess]
    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            // Toast.makeText(this, "Google sign in success" , Toast.LENGTH_LONG).show()
            mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.displayName)
            //mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.givenName)
            //mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.familyName)
            //mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.email)
            //mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.id)
            //mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.photoUrl)

            findViewById<View>(R.id.sign_in_button).visibility = View.GONE
            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.VISIBLE
        } else {
            mStatusTextView!!.text = getString(R.string.signed_out)
            findViewById<View>(R.id.sign_in_button).visibility = View.VISIBLE
            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.GONE
        }
    }
}
