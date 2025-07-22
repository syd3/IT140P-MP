package com.example.it140pmp

import com.example.it140pmp.ui.theme.IT140PMPTheme

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import android.content.Intent

import android.util.Patterns

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.body
import io.ktor.http.*

import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var usernameInput: String by mutableStateOf("")
    private var passwordInput: String by mutableStateOf("")
    private var confirmPasswordInput: String by mutableStateOf("")
    private var emailInput: String by mutableStateOf("")

    private var usernameError: String? by mutableStateOf(null)
    private var passwordError: String? by mutableStateOf(null)
    private var confirmPasswordError: String? by mutableStateOf(null)
    private var emailError: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IT140PMPTheme {
                MyLayoutApp()
            }
        }
    }

    @Preview(widthDp = 300, heightDp = 600)
    @Composable
    fun MyLayoutApp() {
        val mContext = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "MaluPET",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        letterSpacing = 2.sp
                    ),
                    color = Color(0xFF004D40),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Your Pet's Best Friend",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color(0xFF00796B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Create Your Account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MyInputText(
                    username = usernameInput,
                    onUsernameChange = { usernameInput = it; usernameError = null },
                    password = passwordInput,
                    onPasswordChange = { passwordInput = it; passwordError = null },
                    confirmPassword = confirmPasswordInput,
                    onConfirmPasswordChange = { confirmPasswordInput = it; confirmPasswordError = null },
                    email = emailInput,
                    onEmailChange = { emailInput = it; emailError = null },
                    usernameError = usernameError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    emailError = emailError
                )

                Spacer(modifier = Modifier.height(32.dp))

                AddButton()


                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    TextButton(onClick = {
                        mContext.startActivity(Intent(mContext, LoginActivity::class.java))
                    }) {

                        Text(
                            text = "Sign in",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF00796B) // Use your primary teal color for the link
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AppButton(
        onClick: () -> Unit,
        text: String,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFF00897B)
            ),
            modifier = modifier
                .padding(horizontal = 8.dp)
                .height(64.dp)
                .fillMaxWidth(0.9f)
        ) {
            Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    fun AddButton() {
        val mContext = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val isFormValid = remember(usernameInput, passwordInput, confirmPasswordInput, emailInput) {
            usernameInput.isNotBlank() &&
                    passwordInput.isNotBlank() && passwordInput.length >= 8 &&
                    confirmPasswordInput.isNotBlank() && confirmPasswordInput == passwordInput &&
                    emailInput.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
        }

        AppButton(
            onClick = {
                if (validateInput()) {
                    coroutineScope.launch {
                        kTORadd(mContext)
                    }
                } else {
                    mContext.toast("Please correct the errors in the form.")
                }
            },
            text = "Create Account",
            enabled = isFormValid
        )
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (usernameInput.isBlank()) {
            usernameError = "Username cannot be empty."
            isValid = false
        } else {
            usernameError = null
        }

        if (passwordInput.isBlank()) {
            passwordError = "Password cannot be empty."
            isValid = false
        } else if (passwordInput.length < 8) {
            passwordError = "Password must be at least 8 characters long."
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPasswordInput.isBlank()) {
            confirmPasswordError = "Confirm password cannot be empty."
            isValid = false
        } else if (confirmPasswordInput != passwordInput) {
            confirmPasswordError = "Passwords do not match."
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (emailInput.isBlank()) {
            emailError = "Email cannot be empty."
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailError = "Invalid email format."
            isValid = false
        } else {
            emailError = null
        }

        return isValid
    }

    @Composable
    private fun customAppTextFieldColors(): TextFieldColors {
        val containerBgColor = Color(0xFFFBFBFB)
        val focusedAccentColor = Color(0xFF00796B)
        val unfocusedBorderColor = Color(0xFFB0BEC5)
        val labelAndPlaceholderColor = Color(0xFF757575)
        val errorColor = MaterialTheme.colorScheme.error

        return TextFieldDefaults.colors(
            focusedContainerColor = containerBgColor,
            unfocusedContainerColor = containerBgColor,
            disabledContainerColor = containerBgColor,
            focusedIndicatorColor = focusedAccentColor,
            unfocusedIndicatorColor = unfocusedBorderColor,
            cursorColor = focusedAccentColor,
            focusedLabelColor = focusedAccentColor,
            unfocusedLabelColor = labelAndPlaceholderColor,
            focusedPlaceholderColor = focusedAccentColor.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = labelAndPlaceholderColor,
            errorIndicatorColor = errorColor,
            errorLabelColor = errorColor,
            errorSupportingTextColor = errorColor
        )
    }


    @Composable
    fun MyInputText(
        username: String,
        onUsernameChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        confirmPassword: String,
        onConfirmPasswordChange: (String) -> Unit,
        email: String,
        onEmailChange: (String) -> Unit,
        usernameError: String?,
        passwordError: String?,
        confirmPasswordError: String?,
        emailError: String?
    ) {
        val textFieldColors = customAppTextFieldColors()
        val errorColor = MaterialTheme.colorScheme.error

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Username TextField ---
            TextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                placeholder = { Text("e.g., pawsome_owner") },
                singleLine = true,
                isError = usernameError != null,
                supportingText = {
                    if (usernameError != null) {
                        Text(text = usernameError!!, color = errorColor)
                    }
                },
                colors = textFieldColors,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp) // Lessened spacing here
                    .fillMaxWidth(0.95f)
            )

            // --- Password TextField ---
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                placeholder = { Text("Minimum 8 characters") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = errorColor)
                    }
                },
                colors = textFieldColors,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp) // Lessened spacing here
                    .fillMaxWidth(0.95f)
            )

            // --- Confirm Password TextField ---
            TextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter your password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(text = confirmPasswordError!!, color = errorColor)
                    }
                },
                colors = textFieldColors,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp) // Lessened spacing here
                    .fillMaxWidth(0.95f)
            )

            // --- Email TextField ---
            TextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("e.g., your.email@example.com") },
                singleLine = true,
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(text = emailError!!, color = errorColor)
                    }
                },
                colors = textFieldColors,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp) // Lessened spacing here
                    .fillMaxWidth(0.95f)
            )
        }
    }

    private suspend fun kTORadd(context: Context) {
        val client = HttpClient(CIO)
        println("Sending data (GET request): Username='$usernameInput', Password='${passwordInput.take(2)}...', Email='$emailInput'")
        try {
            val response: HttpResponse = client.get("http://192.168.254.104/MaluPET/REST/add_record1.php") {
                url {
                    parameters.append("username", usernameInput)
                    parameters.append("password", passwordInput)
                    parameters.append("email", emailInput)
                }
            }
            val stringBody: String = response.body<String>()
            println("HTTP Status: ${response.status.toString()}")
            context.toast(stringBody)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is io.ktor.client.plugins.ClientRequestException -> "HTTP Error ${e.response.status.value}: ${e.response.status.description}"
                is java.net.UnknownHostException -> "Network error: Host unreachable. Check IP or internet connection."
                is java.net.ConnectException -> "Connection refused: Server might be down or IP is wrong."
                else -> "Error adding account: ${e.message ?: "Unknown error"}"
            }
            context.toast(errorMessage)
            e.printStackTrace()
        } finally {
            client.close()
        }
    }

    private fun Context.toast(message: CharSequence) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}