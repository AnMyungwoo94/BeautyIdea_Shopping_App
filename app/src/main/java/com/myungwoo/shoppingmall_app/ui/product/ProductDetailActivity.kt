package com.myungwoo.shoppingmall_app.ui.product

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
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
import androidx.compose.material.TopAppBar
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
        loadImage(product.key) { url -> imageUrl = url }
    }

    LaunchedEffect(count) {
        countSum = count * productPrice
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ProductAppBar { (context as? ComponentActivity)?.finish() }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProductInfo(product = product, imageUrl = imageUrl)
            Spacer(modifier = Modifier.height(16.dp))
            ProductDescription()
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        ProductActionButtons(
            count = count,
            countSum = countSum,
            onCountChange = { count = it },
            onAddToCart = {
                addToCart(product, count, countSum)
                Toast.makeText(
                    context,
                    R.string.product_detail_added_to_cart,
                    Toast.LENGTH_SHORT
                ).show()
            },
            onPurchase = {
                val intent = Intent(context, ProductPayActivity::class.java).apply {
                    putExtra("SELECTED_PRODUCT_PAY", product)
                    putExtra("COUNT", count)
                    putExtra("COUNT_SUM", countSum)
                    putExtra("origin", "DETAILS")
                }
                context.startActivity(intent)
            },

            )
    }
}

@Composable
fun ProductAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color.White,
        title = { Text(stringResource(id = R.string.product_detail_app_bar)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun ProductInfo(product: ProductModel, imageUrl: String?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
            Column(modifier = Modifier.weight(1f)) {
                ProductText(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                ProductText(
                    text = stringResource(
                        id = R.string.product_detail_price_format,
                        product.price.toInt()
                    )
                )
                ProductText(
                    text = if (product.deliveryFee != 0) stringResource(
                        id = R.string.product_detail_delivery_fee_format,
                        product.deliveryFee
                    ) else stringResource(id = R.string.product_detail_delivery_free)
                )
                ProductText(text = product.parcel)
                ProductText(text = product.parcelDay)
            }
        }
    }
}

@Composable
fun ProductDescription() {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        ProductText(
            text = stringResource(id = R.string.product_detail_product_description_title),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        ProductText(
            text = stringResource(id = R.string.product_detail_product_description),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun ProductActionButtons(
    count: Int,
    countSum: Int,
    onCountChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    onPurchase: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProductText(
                text = stringResource(id = R.string.product_detail_quantity_sum_format, countSum),
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CountButton(
                    iconRes = R.drawable.minus_btn,
                    onClick = { if (count > 1) onCountChange(count - 1) }
                )
                ProductText(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                CountButton(
                    iconRes = R.drawable.plus_btn,
                    onClick = { onCountChange(count + 1) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ActionButton(
                text = stringResource(id = R.string.product_detail_add_to_cart),
                onClick = onAddToCart,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = stringResource(id = R.string.product_detail_purchase),
                onClick = onPurchase,
                backgroundColor = Color.DarkGray,
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProductText(
    text: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style,
        modifier = modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun CountButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(35.dp)) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Button(
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = modifier
            .height(50.dp),
        onClick = onClick
    ) {
        Text(text = text)
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

fun loadImage(productKey: String, onImageLoaded: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("$productKey.png")
    storageRef.downloadUrl.addOnSuccessListener { uri ->
        onImageLoaded(uri.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val sampleProduct = ProductModel(
        key = "sample_key",
        name = stringResource(id = R.string.product_detail_sample_product_name),
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

@Preview(showBackground = true)
@Composable
fun ProductAppBarPreview() {
    MaterialTheme {
        ProductAppBar(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ProductInfoPreview() {
    val sampleProduct = ProductModel(
        key = "sample_key",
        name = stringResource(id = R.string.product_detail_sample_product_name),
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
        ProductInfo(product = sampleProduct, imageUrl = null)
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDescriptionPreview() {
    MaterialTheme {
        ProductDescription()
    }
}

@Preview(showBackground = true)
@Composable
fun ProductActionButtonsPreview() {
    MaterialTheme {
        ProductActionButtons(
            count = 1,
            countSum = 10000,
            onCountChange = {},
            onAddToCart = {},
            onPurchase = {}
        )
    }
}
