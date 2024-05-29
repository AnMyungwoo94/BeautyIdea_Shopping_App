package com.myungwoo.shoppingmall_app.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.setting.SettingActivity

class ProductSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductSuccessScreen()
        }
    }
}

@Composable
fun ProductSuccessScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        ProductSuccessContent()
    }
}

@Composable
fun ProductSuccessContent() {
    val context = LocalContext.current
    Image(
        painter = painterResource(id = R.drawable.delivery),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(30.dp))
    Text(
        text = stringResource(id = R.string.success_payment_success),
        fontSize = 23.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = stringResource(id = R.string.success_payment_success_message),
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(70.dp))
    Button(
        onClick = {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(text = stringResource(id = R.string.success_confirm_button))
    }
}

@Preview
@Composable
fun ProductSuccessScreenPreview() {
    MaterialTheme {
        ProductSuccessScreen()
    }
}
