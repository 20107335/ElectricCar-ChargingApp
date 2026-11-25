package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import org.setu.electriccar.models.User

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppData.setup(applicationContext)

        if (AppData.currentUser != null) {
            openMenu()
            return
        }

        enableEdgeToEdge()

        val title = TextView(this).apply {
            text = "Waterford Electric Car Parking"
            textSize = 26f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }
        val nameInput = EditText(this).apply { hint = "Name (only needed to register)" }
        val emailInput = EditText(this).apply {
            hint = "Email"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val passwordInput = EditText(this).apply {
            hint = "Password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val messageText = TextView(this).apply { setPadding(0, 16, 0, 16) }
        val loginButton = MaterialButton(this).apply { text = "Login" }
        val registerButton = MaterialButton(this).apply { text = "Register" }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val user = AppData.store.findUser(email)
            if (user == null || user.password != password) {
                messageText.text = "Wrong email or password."
            } else {
                AppData.login(user)
                openMenu()
            }
        }

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messageText.text = "Fill in name, email and password to register."
            } else if (AppData.store.findUser(email) != null) {
                messageText.text = "That email is already registered."
            } else {
                val user = User(name = name, email = email, password = password)
                AppData.store.createUser(user)
                AppData.login(user)
                openMenu()
            }
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(title)
            addView(nameInput)
            addView(emailInput)
            addView(passwordInput)
            addView(messageText)
            addView(loginButton)
            addView(registerButton)
        }
        setContentView(root)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 48, systemBars.top + 48, systemBars.right + 48, systemBars.bottom + 48)
            insets
        }
    }

    private fun openMenu() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}