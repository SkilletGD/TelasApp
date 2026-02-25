package com.example.telasapp.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.telasapp.R
import com.example.telasapp.navigation.Screen
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController
){
    LaunchedEffect(key1 = true){
        delay(4000)
        navController.popBackStack()
        navController.navigate(Screen.Inventario.route)
    }
    Splash()
}

@Composable
fun Splash() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "'Empresa'",
//            fontSize = 30.sp,
//            textAlign = TextAlign.Center
//        )

    }
}

@Preview
@Composable
fun SplashScreenPreview(showBackground: Boolean = true){
    SplashScreen(
        navController = NavController(LocalContext.current)
    )
}
