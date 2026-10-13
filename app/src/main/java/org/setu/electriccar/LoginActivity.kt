package org.setu.electriccar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.setu.electriccar.models.User

class LoginActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var messageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scroll = createUserInterface()

        ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createUserInterface(): ScrollView {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "EV Charging"
            textSize = 30f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Log in or register"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 32)
        }

        nameInput = EditText(this).apply {
            hint = "Name (only needed to register)"
        }

        emailInput = EditText(this).apply {
            hint = "Email"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordInput = EditText(this).apply {
            hint = "Password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        messageText = TextView(this).apply {
            textSize = 14f
            setPadding(0, 16, 0, 16)
        }

        val loginButton = Button(this).apply {
            text = "Log In"
            setOnClickListener {
                login()
            }
        }

        val registerButton = Button(this).apply {
            text = "Register"
            setOnClickListener {
                register()
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(nameInput)
        root.addView(emailInput)
        root.addView(passwordInput)
        root.addView(messageText)
        root.addView(loginButton)
        root.addView(registerButton)

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        return scroll
    }

    private fun login() {

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            return
        }

        val user = AppData.users.findByEmail(email)

        if (user == null || user.password != password) {
            messageText.text = "Wrong email or password."
            return
        }

        openApp(user)
    }

    private fun register() {

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (name.isEmpty()) {
            nameInput.error = "Name is required to register"
            return
        }

        if (!email.contains("@")) {
            emailInput.error = "Enter a valid email"
            return
        }

        if (password.length < 4) {
            passwordInput.error = "Password must be at least 4 characters"
            return
        }

        if (AppData.users.findByEmail(email) != null) {
            emailInput.error = "This email is already registered"
            return
        }

        val user = User(
            name = name,
            email = email,
            password = password
        )

        AppData.users.create(user)

        openApp(user)
    }

    private fun openApp(user: User) {
        AppData.currentUser = user
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}