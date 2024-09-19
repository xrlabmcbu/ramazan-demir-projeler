package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.appv1.ui.theme.YourAppTheme
import kotlinx.coroutines.launch

data class Place(val name: String, val location: String, val image: String)

class loc_Page : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                MyScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview6() {
    YourAppTheme {
        MyScreen()
    }
}


@Composable
fun MyScreen() {
    val items = remember { mutableStateOf<List<Place>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items.value = fetchJsonData()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        locPage2(items.value)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth() ) {
            Text(text = "                    Restaurants",fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(46.dp)
                    .clickable {
                        val userGomain = Intent(context, MainActivity::class.java)
                        context.startActivity(userGomain)
                        (context as Activity).finish()
                    }
            ) {
                Icon(modifier = Modifier.padding(10.dp),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color.Black
                )
            }
        }

    }
}

@Composable
fun locPage2(items: List<Place>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDFD))
            .padding(top = 56.dp) // Bu boşluk, üstteki ikonun boyutu için
    ) {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0xFFF8FFFF)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = item.image,
                            builder = {
                                listener(
                                    onError = { _, _ ->
                                        Log.e("ImageError", "Image loading failed: ${item.image}")
                                    }
                                )
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .padding(end = 10.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = item.location,
                        )
                    }
                }
            }
        }
    }
}
