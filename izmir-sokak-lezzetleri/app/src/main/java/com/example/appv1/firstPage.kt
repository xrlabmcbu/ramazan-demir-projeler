package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.appv1.ui.theme.YourAppTheme

class firstPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bitmapPath = intent.getStringExtra("bitmapPath")
        val detected = intent.getStringExtra("detected")
        setContent {
            YourAppTheme {
                Page(bitmapPath, detected)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    YourAppTheme {
        Page("https://example.com/image.jpg", "Detected Food")
    }
}

@Composable
fun Page(bitmapPath: String? = null, detected: String? = null) {
    val brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFF1FAFF), Color.White)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush))

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Resmi yüklemek için Coil kullan
        bitmapPath?.let { url ->
            Image(
                painter = rememberImagePainter(url),
                contentDescription = "Uploaded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .fillMaxHeight(0.80f)
            )
        }

        Text(
            text = "Recognized food : $detected",
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier
                .weight(0.1f)
                .padding(vertical = 16.dp)
        )


        Row{
            Button(
                onClick = {
                    if(detected != "Class not found"){
                        val secondIntent = Intent(context, secondPage::class.java)
                        secondIntent.putExtra("detect_object", detected)
                        context.startActivity(secondIntent)
                        (context as Activity).finish()
                    }else {
                        Toast.makeText(context,"No detected, back to home ",Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.40f)
                    .height(40.dp)
                    .padding(3.dp),
            ) {
                Text(text = "Start Quiz")
            }
            Button(
                onClick = {
                    val secondIntent = Intent(context,MainActivity::class.java)
                    context.startActivity(secondIntent)
                    (context as Activity).finish()
                },
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .height(40.dp)
                    .padding(3.dp),
            ) {
                Text(text = "Back to Home")
            }
        }

    }
}
