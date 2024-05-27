package com.myungwoo.shoppingmall_app.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedData = intent.getSerializableExtra("ITEM_DATA") as ProductModel
        setContent {
            MaterialTheme {
                ProductDetailScreen(receivedData)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: ProductModel) {
    val context = LocalContext.current
    var count by remember { mutableStateOf(1) }
    var countSum by remember { mutableStateOf(0) }
    var productPrice by remember { mutableStateOf(product.price.toInt()) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(product.key) {
        val storageRef = FirebaseStorage.getInstance().reference.child("${product.key}.png")
        imageUrl = storageRef.downloadUrl.await().toString()
    }

    LaunchedEffect(count) {
        countSum = count * productPrice
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = { Text("상세페이지") },
            navigationIcon = {
                IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back"
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                imageUrl?.let {
                    GlideImage(
                        imageModel = it,
                        contentDescription = null,
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${
                            NumberFormat.getNumberInstance(Locale.US).format(product.price.toInt())
                        } 원",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = if (product.deliveryFee != 0) "배송비 ${
                            NumberFormat.getNumberInstance(
                                Locale.US
                            ).format(product.deliveryFee)
                        } 원" else "배송비 무료",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = product.parcel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = product.parcelDay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "상세 설명입니다.",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.product_Detail),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "수량 합계: ${NumberFormat.getNumberInstance(Locale.US).format(countSum)} 원",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                ) {
                    IconButton(
                        onClick = { if (count > 1) count -= 1 },
                        modifier = Modifier.size(35.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.minus_btn),
                            contentDescription = "Minus",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = count.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { count += 1 },
                        modifier = Modifier.size(35.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus_btn),
                            contentDescription = "Plus",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    onClick = {
                        addToCart(product, count, countSum)
                        Toast.makeText(context, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "장바구니")
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    onClick = {
                        val intent = Intent(context, ProductPayActivity::class.java).apply {
                            putExtra("SELECTED_PRODUCT_PAY", product)
                            putExtra("COUNT", count)
                            putExtra("COUNT_SUM", countSum)
                            putExtra("origin", "DETAILS")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "구매하기")
                }
            }
        }
    }
}

fun addToCart(product: ProductModel, count: Int, countSum: Int) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.let {
        val uid = it.uid
        val key = product.key
        val updatedProduct = product.copy(count = count, count_sum = countSum)

        val database = FirebaseDatabase.getInstance().reference
        database.child("cart").child(uid).child(key).setValue(updatedProduct)
    }
}

@Composable
@Preview(showBackground = true)
fun ProductDetailScreenPreview() {
    val sampleProduct = ProductModel(
        key = "sample_key",
        name = "Sample Product",
        price = "10000",
        time = "2024-05-27",
        parcel = "Standard",
        deliveryFee = 0,
        parcelDay = "3 days",
        category = "sample_category",
        count = 1,
        count_sum = 1,
        isSelected = false
    )

    MaterialTheme {
        ProductDetailScreen(product = sampleProduct)
    }
}
