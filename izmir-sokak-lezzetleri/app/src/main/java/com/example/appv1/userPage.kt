package com.example.appv1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.appv1.ui.theme.YourAppTheme
import kotlinx.coroutines.launch

class userPage : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            YourAppTheme {
                val sharedPreferencesHelper = SharedPreferencesHelper(this)
                val key3 = sharedPreferencesHelper.getEmail()
                key3?.let { userViewModel.fetchUserInfo(it) }
                val userPoints by userViewModel.userPoints.observeAsState(0)
                key3?.let { Page2(userPoints, it) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview7() {
    YourAppTheme {
        Page2(userPoints = 5 , email = "a@gmail.com")
    }
}


@Composable
fun Page2(userPoints: Int, email: String) {

    val context =  LocalContext.current
    val items = remember { mutableStateOf<List<Place>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items.value = fetchJsonData2()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(innerPadding)
                    .background(color = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.White)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE9F9F9))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(1f)
                        ){
                            Column {
                                Text(text = " Welcome Back",fontWeight = FontWeight.W400)
                                Text(text = " $email",fontSize = 23.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE9F9F9))
                                    .clickable {
                                        val userGomain = Intent(context, MainActivity::class.java)
                                        context.startActivity(userGomain)
                                        (context as Activity).finish()
                                    }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                            }

                        }



                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color(0xFFF0FDFD))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.surprise),
                                    contentDescription = "Surprise",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    text = " Point : $userPoints",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
                LocPage(items.value)
            }
        }
    )
}




@Composable
fun LocPage(items: List<Place>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0xFFF9FFFF)  //0xFFFAF3E0 back itemler
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












