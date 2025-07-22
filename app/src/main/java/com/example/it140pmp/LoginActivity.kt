package com.example.it140pmp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.it140pmp.ui.theme.IT140PMPTheme // Import your theme

import androidx.compose.runtime.saveable.rememberSaveable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.*
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IT140PMPTheme { // Apply the theme here
                LoginScreen()
            }
        }
    }

    @Preview(widthDp = 300, heightDp = 600)
    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        var username by rememberSaveable { mutableStateOf("") } // Use rememberSaveable for state persistence
        var password by rememberSaveable { mutableStateOf("") } // Use rememberSaveable for state persistence
        val scope = rememberCoroutineScope()

        // Error states for username and password
        var usernameError: String? by remember { mutableStateOf(null) }
        var passwordError: String? by remember { mutableStateOf(null) }

        val isLoginButtonEnabled = remember(username, password) {
            username.isNotBlank() && password.isNotBlank()
        }

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
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color(0xFF00796B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Login to Your Account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = Color(0xFF424242),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Reusing MyInputText for consistency, but adapting it for login fields
                LoginInputFields(
                    username = username,
                    onUsernameChange = {
                        username = it
                        usernameError = null // Clear error on change
                    },
                    password = password,
                    onPasswordChange = {
                        password = it
                        passwordError = null // Clear error on change
                    },
                    usernameError = usernameError,
                    passwordError = passwordError
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Login button, using the AppButton composable
                AppButton(
                    onClick = {
                        // Basic validation for empty fields
                        if (username.isBlank()) {
                            usernameError = "Username cannot be empty."
                        }
                        if (password.isBlank()) {
                            passwordError = "Password cannot be empty."
                        }

                        if (username.isNotBlank() && password.isNotBlank()) {
                            scope.launch {
                                loginRequest(username, password, context)
                            }
                        } else {
                            context.toast("Please enter your username and password.")
                        }
                    },
                    text = "Login",
                    enabled = isLoginButtonEnabled
                )

                Spacer(modifier = Modifier.height(16.dp))

                // "Don’t have an account? Sign up" section
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Don’t have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    TextButton(onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }) {
                        Text(
                            text = "Sign up",
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
    fun LoginInputFields(
        username: String,
        onUsernameChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        usernameError: String?,
        passwordError: String?
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
                placeholder = { Text("Enter your username") },
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
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.95f)
            )

            // --- Password TextField ---
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
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
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.95f)
            )
        }
    }

    private suspend fun loginRequest(username: String, password: String, context: Context) {
        val client = HttpClient(CIO)
        try {
            val response: HttpResponse = client.get("http://192.168.254.104/MaluPET/REST/login.php") {
                url {
                    parameters.append("username", username)
                    parameters.append("password", password)
                }
            }

            val body = response.body<String>()
            if ("success" in body.lowercase()) {
                context.toast("Login successful!")
                // TODO: Navigate to home screen
                // For example: context.startActivity(Intent(context, HomeScreenActivity::class.java))
            } else {
                context.toast("Invalid credentials.")
            }

        } catch (e: Exception) {
            val errorMessage = when (e) {
                is io.ktor.client.plugins.ClientRequestException -> "HTTP Error ${e.response.status.value}: ${e.response.status.description}"
                is java.net.UnknownHostException -> "Network error: Host unreachable. Check IP or internet connection."
                is java.net.ConnectException -> "Connection refused: Server might be down or IP is wrong."
                else -> "Login failed: ${e.message ?: "Unknown error"}"
            }
            context.toast(errorMessage)
            e.printStackTrace()
        } finally {
            client.close()
        }
    }

    private fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}