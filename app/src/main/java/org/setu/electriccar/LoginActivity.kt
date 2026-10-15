package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.setu.electriccar.models.User
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppData.setup(applicationContext)

        if (AppData.currentUser != null) {
            openApp()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 160, 48, 48)
        }

        val title = TextView(this).apply {
            text = "Waterford Electric Car Parking"
            textSize = 26f
        }

        nameInput = EditText(this).apply { hint = "Name (only to register)" }
        emailInput = EditText(this).apply { hint = "Email" }
        passwordInput = EditText(this).apply {
            hint = "Password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        messageText = TextView(this)

        val loginButton = MaterialButton(this).apply {
            text = "Log In"
            setOnClickListener { login() }
        }

        val registerButton = MaterialButton(this).apply {
            text = "Register"
            setOnClickListener { register() }
        }

        root.addView(title)
        root.addView(nameInput)
        root.addView(emailInput)
        root.addView(passwordInput)
        root.addView(messageText)
        root.addView(loginButton)
        root.addView(registerButton)

        setContentView(root)
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        val user = AppData.users.findByEmail(email)

        if (user == null || user.password != password) {
            messageText.text = "Wrong email or password."
            return
        }

        AppData.login(user)
        openApp()
    }

    private fun register() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageText.text = "Fill in name, email and password to register."
            return
        }

        if (AppData.users.findByEmail(email) != null) {
            messageText.text = "That email is already registered."
            return
        }

        val user = User(name = name, email = email, password = password)
        AppData.users.create(user)

        AppData.login(user)
        openApp()
    }

    private fun openApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}