package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appv1.ui.theme.APPV1Theme
import com.example.appv1.ui.theme.YourAppTheme

class login_Page : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                LoginPage()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    APPV1Theme {
        YourAppTheme {
             LoginPage()
        }
    }
}

@Composable
fun LoginPage(){

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White))

    var email by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = painterResource(id = R.drawable.leziz) , contentDescription = "logo",
            modifier = Modifier
                .size(210.dp)
                .padding(8.dp))


            OutlinedTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(1f),
                value = email,
                onValueChange = {email =it},
                label = { Text("Email") },
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "email") },

                )
            OutlinedTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(1f),
                value = password,
                onValueChange = {password = it},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password") }
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        signInUser(email.text, password.text) { success, message ->
                            if (success) {
                                val intentMain = Intent(context, MainActivity::class.java)
                                val sharedPreferencesHelper = SharedPreferencesHelper(context)
                                sharedPreferencesHelper.saveEmail(email.text)
                                 context.startActivity(intentMain)
                            } else {
                                Toast.makeText(context, message ?: "Giriş başarısız", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),

                    ) {
                    Text(text = "SIGN IN" )
                }
            }
            Row {
                Text(
                    text = "    Don't have an account ? ",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 18.sp,
                    color = Color(0xFF005B5B)
                )

                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .clickable {
                            val kayitoLIntent = Intent(context, sign_up::class.java)
                            context.startActivity(kayitoLIntent)
                            (context as Activity).finish()
                        }
                ) {
                    Text(" SIGN UP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005B5B))
                }

            }



    }
}

