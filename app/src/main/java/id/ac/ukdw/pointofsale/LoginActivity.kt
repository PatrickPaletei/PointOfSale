package id.ac.ukdw.pointofsale

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.ac.ukdw.pointofsale.api.Service.ApiClient
import id.ac.ukdw.pointofsale.api.request.LoginRequest
import id.ac.ukdw.pointofsale.api.response.LoginResponse
import id.ac.ukdw.pointofsale.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startNextActivity()
            finish()
        } else {
            binding.btnLogin.setOnClickListener {
                val username = binding.email.text.toString()
                val pass = binding.password.text.toString()

                lifecycleScope.launch {
                    binding.btnLogin.startAnimation()
                    val responseCode = login(username, pass)
                    val body = responseCode
                    if (responseCode == 200) {
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()
                        startNextActivity()
                    } else {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun startNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun login(email: String, password: String): Int? {
        return suspendCoroutine { continuation ->
            val request = LoginRequest(
                username = email,
                password = password
            )

            ApiClient.instance.login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val code = response.code()
                        val body = response.body()
                        if (code == 200) {
                            Log.d("login", "onResponse: $response ")
                            val editor = sharedPreferences.edit()
                            if (body != null) {
                                editor.putString("nama", body.username)
                                editor.putString("id", body.id_user.toString())
                                editor.putString("token",body.plainTextToken)
                                editor.putString("role",body.message)
                                editor.putString("image",body.image)
                                //send pake sf bearer token nya
                            }
                            editor.apply()
                            continuation.resume(code)
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.d("login", "onResponse: $t ")
                        continuation.resume(null)
                    }
                })
        }
    }
}