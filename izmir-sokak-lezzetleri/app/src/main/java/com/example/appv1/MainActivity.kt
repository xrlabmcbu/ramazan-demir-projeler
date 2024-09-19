package com.example.appv1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appv1.ui.theme.YourAppTheme

class MainActivity : ComponentActivity() {
    private val CAMERA_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kamera izni kontrolü ve isteği
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }

        setContent {
            YourAppTheme {
                MyScaffold()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, ilgili işlemleri yapabilirsiniz
            } else {
                // İzin verilmedi, kullanıcıya bilgi verin
                Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YourAppTheme {
        MyScaffold()
    }
}

@Composable
fun MyScaffold() {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            isLoading = true
            val intent = Intent(context, LoadingActivity::class.java)
            context.startActivity(intent)
            val service = Service(context) { detectedClass ->
                uploadPhotoToFirebase(it) { url ->
                    isLoading = false
                    val intent = Intent(context, firstPage::class.java)
                    intent.putExtra("bitmapPath", url)
                    intent.putExtra("detected", detectedClass)
                    context.startActivity(intent)
                }
            }
            service.uploadImage(it)
        } ?: run {
            Toast.makeText(context, "Failed photo shoot", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF0FDFD),
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val intent_Konum = Intent(context, loc_Page::class.java)
                        context.startActivity(intent_Konum)
                        (context as Activity).finish()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        modifier = Modifier.size(45.dp),
                        onClick = {
                            launcher.launch()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.camera2),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        val person_info = Intent(context, userPage::class.java)
                        context.startActivity(person_info)
                        (context as Activity).finish()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.White),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .fillMaxHeight(0.25f)
            ) {
                Image(
                    painterResource(id = R.drawable.test),
                    contentDescription = "Person",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .alpha(0.7f),
                    contentScale = ContentScale.Crop
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Who are we?",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(7.dp)
                    )
                    FilledCardExample(text2 = "Leziz is an application set out to promote and share the unique local flavors of Izmir. We aim to help you discover the richness of Izmir cuisine.",
                        containerColor = Color(0xFFD3E4FF))

                    Text(
                        text = "What are we doing?",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(7.dp)
                    )

                    FilledCardExample2(text = " Food Recognition ", text2 = "The application allows you to instantly recognize and get detailed information by taking photos of the food you have. It offers a fast and practical solution.", containerColor = Color(0xFF9ED8D5), imageResId = R.drawable.assa)
                    FilledCardExample2(text = " Point System", text2 = "You can earn points for every activity you perform in the app and get rewards as you progress. This process is quite fun.", containerColor = Color(0xFFD1C4E9), imageResId = R.drawable.gif)
                    FilledCardExample2(text = " Winning a Prize", text2 = "When you reach points targets, you can win surprise rewards. These rewards include discount vouchers and special recipe access.", containerColor = Color(0xFFFAF3E0), imageResId = R.drawable.surprise)
                }
            }
        }
    }
}

@Composable
fun FilledCardExample2(text: String, text2: String, containerColor: Color, imageResId: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .padding(6.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = text2,
                    modifier = Modifier
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Image(
                painterResource(id = imageResId),
                contentDescription = "Person",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}

@Composable
fun FilledCardExample(text2: String, containerColor: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = text2,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                textAlign = TextAlign.Start
            )
            Image(
                painterResource(id = R.drawable.teamwork),
                contentDescription = "Person",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}
