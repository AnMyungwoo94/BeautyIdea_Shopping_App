package com.myungwoo.shoppingmall_app.ui.product

import android.content.Context
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
        val receivedData = intent.getSerializableExtra("ITEM_DATA", ProductModel::class.java)
        setContent {
            MaterialTheme {
                if (receivedData != null) {
                    ProductDetailScreen(receivedData)
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(product: ProductModel) {
    val context = LocalContext.current
    var count by remember { mutableStateOf(1) }
    var countSum by remember { mutableStateOf(0) }
    var productPrice by remember { mutableStateOf(product.price.toInt()) }
    var imageUrl by remember { mutableStateOf<String>("") }

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
                .verticalScroll(rememberScrollState())
        ) {
            ProductInfo(product = product, imageUrl = imageUrl)
            ProductDescription()
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        ProductActionScreen(
            count = count,
            countSum = countSum,
            onCountChange = { count = it },
            product = product
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
fun ProductInfo(product: ProductModel, imageUrl: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            GlideImage(
                imageModel = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.weight(1f)) {
                ProductInfoText(product)
            }
        }
    }
}

@Composable
fun ProductInfoText(product: ProductModel) {
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

@Composable
fun ProductDescription() {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
    ) {
        ProductText(
            text = stringResource(id = R.string.product_detail_product_description_title),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        ProductText(
            text = stringResource(id = R.string.product_Detail),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun ProductActionScreen(
    count: Int,
    countSum: Int,
    onCountChange: (Int) -> Unit,
    product: ProductModel,
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ProductQuantityModifier(count, countSum, onCountChange)
        Spacer(modifier = Modifier.padding(16.dp))
        ProductActionButtons(count, countSum, product)
    }
}

@Composable
fun ProductQuantityModifier(
    count: Int,
    countSum: Int,
    onCountChange: (Int) -> Unit
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
}

@Composable
fun ProductActionButtons(
    count: Int,
    countSum: Int,
    product: ProductModel,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        ActionButton(
            text = stringResource(id = R.string.product_detail_add_to_cart),
            onClick = {
                addToCart(product, count, countSum)
                Toast.makeText(
                    context,
                    R.string.product_detail_added_to_cart,
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            text = stringResource(id = R.string.product_detail_purchase),
            onClick = {
                val intent = Intent(context, ProductPayActivity::class.java).apply {
                    putExtra("SELECTED_PRODUCT_PAY", product)
                    putExtra("COUNT", count)
                    putExtra("COUNT_SUM", countSum)
                    putExtra("origin", "DETAILS")
                }
                context.startActivity(intent)
            },
            backgroundColor = Color.DarkGray,
            contentColor = Color.White,
            modifier = Modifier.weight(1f)
        )
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

@Composable
fun PreviewSampleProductModel(): ProductModel {
    return ProductModel(
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
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val sampleProduct = PreviewSampleProductModel()

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
    val sampleProduct = PreviewSampleProductModel()

    MaterialTheme {
        ProductInfo(product = sampleProduct, imageUrl = "")
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
fun ProductActionScreenPreview() {
    val sampleProduct = PreviewSampleProductModel()

    MaterialTheme {
        ProductActionScreen(
            count = 1,
            countSum = 10000,
            onCountChange = {},
            product = sampleProduct
        )
    }
}