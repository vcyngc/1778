package com.aplikasi.a1778

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.aplikasi.a1778.api.BaseRetrofit
import com.aplikasi.a1778.response.login.LoginResponse
import com.aplikasi.a1778.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    companion object{
        lateinit var sessionManager: SessionManager
        private lateinit var context: Context
    }

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        val loginStatus = sessionManager.getBoolean("LOGIN_STATUS")
        if (loginStatus) {
            val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(moveIntent)
            finish()
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        btnLogin.setOnClickListener{

            api.login(txtEmail.text.toString(),txtPassword.text.toString()).enqueue(object :
                Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.e("LoginData",response.toString())
                    val correct = response.body()!!.success

                    if (correct){
                        val token = response.body()!!.data.token

                        sessionManager.saveString("TOKEN", "Bearer $token")
                        sessionManager.saveBoolean("LOGIN_STATUS",true)

                        sessionManager.saveString("ADMIN_ID", response.body()!!.data.admin.id)

                        val moveIntent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(moveIntent)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"User dan password salah",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginError",t.toString())
                }
            })

        }
    }
}