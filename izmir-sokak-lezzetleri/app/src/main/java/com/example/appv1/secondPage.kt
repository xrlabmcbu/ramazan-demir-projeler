package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appv1.ui.theme.YourAppTheme

class secondPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                val sharedPreferencesHelper = SharedPreferencesHelper(this)
                val id = sharedPreferencesHelper.getEmail()
                val detected_object = intent.getStringExtra("detect_object") ?: ""
                id?.let { Screen(detected_object, it) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    YourAppTheme {
        Screen("cat","null")
    }
}

data class Sorular(val question: String, val answer: Boolean)

@Composable
fun Screen(detectedObject: String,id : String) {
    val context = LocalContext.current
    val brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE9F9F9), Color.White)
    )
    val questionList = remember { mutableStateOf(getQuestions(detectedObject)) }
    val currentIndex = remember { mutableStateOf(0) }
    val currentQuestion = questionList.value[currentIndex.value]

    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(400.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = "${currentIndex.value + 1}/${questionList.value.size}",
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = currentQuestion.question, // Display the question
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = {
                    if (!currentQuestion.answer) {
                        updateUserPointsByEmail(id,10){success ,errorMessage ->
                            if (success){
                                Toast.makeText(context, "Correct answer!", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                                println(errorMessage)
                            }

                        }
                    } else {
                        Toast.makeText(context, "Incorrect answer!", Toast.LENGTH_SHORT).show()
                    }
                    goToNextQuestion(currentIndex, questionList.value.size, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61)),
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "False")
            }
            Button(
                onClick = {
                    if (currentQuestion.answer) {
                        updateUserPointsByEmail(id,10){success ,errorMessage ->
                            if (success){
                                Toast.makeText(context, "Correct answer!", Toast.LENGTH_SHORT).show()
                            }else{
                                println(errorMessage)
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            }

                        }
                    } else {
                        Toast.makeText(context, "Incorrect answer!", Toast.LENGTH_SHORT).show()
                    }
                    goToNextQuestion(currentIndex, questionList.value.size, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF77DD77)),
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "True")
            }
        }
    }
}

fun goToNextQuestion(currentIndex: MutableState<Int>, totalQuestions: Int, context: Context) {
    Log.d("Navigation", "Current Index: ${currentIndex.value}, Total Questions: $totalQuestions")
    if (currentIndex.value < totalQuestions - 1) {
        currentIndex.value += 1
        Log.d("Navigation", "Next question index: ${currentIndex.value}")
    } else {
        Log.d("Navigation", "All questions answered. Navigating to MainActivity.")
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as Activity).finish()

    }
}
fun getQuestions(detectedObject: String): List<Sorular> {
    return when (detectedObject) {
        "boyoz" -> listOf(
            Sorular("Boyoz is a pastry unique to the Izmir region.", true),
            Sorular("Boyoz is only made as a dessert.", false),
            Sorular("Boyoz is typically consumed at breakfast.", true)
        )
        "sambali" -> listOf(
            Sorular("Sambali is a pastry unique to Izmir.", true),
            Sorular("Sambali is only prepared as a dessert.", true),
            Sorular("Şambali dessert is a Turkish dessert made with flour, semolina, and sugar.", true)
        )
        "izmir bomba" -> listOf(
            Sorular("Izmir bomba is a dessert filled with chocolate.", true),
            Sorular("Izmir bomba is only consumed during the summer.", false),
            Sorular("Izmir Bomb is a flavor of Izmir cuisine.", true)
        )
        else -> listOf(Sorular("Invalid input.", false))

    }
}
