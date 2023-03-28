package com.example.googleroom
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.googleroom.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google sign-in options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.btnSignOut.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                viewModel.onSignOutClick()
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.user.observe(this, Observer { user ->
            if (user != null) {
                binding.tvName.text = user.name
                binding.tvEmail.text = user.email
                binding.btnSignIn.isEnabled = false
                binding.btnSignOut.isEnabled = true
            } else {
                binding.tvName.text = ""
                binding.tvEmail.text = ""
                binding.btnSignIn.isEnabled = true
                binding.btnSignOut.isEnabled = false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the result of the Google sign-in activity
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Signed in successfully, show authenticated UI.
                val account = task.getResult(ApiException::class.java)!!
                viewModel.onSignInClick(account)
            } catch (e: ApiException) {

                Toast.makeText(this, "Google sign in failed: $e", Toast.LENGTH_SHORT).show()
                Log.i("Here",e.message.toString())
            }
        }
    }
}
