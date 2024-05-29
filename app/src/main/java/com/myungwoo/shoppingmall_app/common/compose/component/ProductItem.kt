package com.myungwoo.shoppingmall_app.common.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.storage.FirebaseStorage
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.tasks.await

@Composable
fun ProductItem(item: ProductModel, onClick: (ProductModel) -> Unit) {
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(item.key) {
        val storageRef = FirebaseStorage.getInstance().reference.child("${item.key}.png")
        imageUrl = storageRef.downloadUrl.await().toString()
    }
    ProductItemContent(item = item, imageUrl = imageUrl, onClick = onClick)
}

@Composable
fun ProductItemContent(item: ProductModel, imageUrl: String?, onClick: (ProductModel) -> Unit) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick(item) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Gray)
        ) {
            imageUrl?.let {
                GlideImage(
                    imageModel = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    previewPlaceholder = R.drawable.home_img
                )
            }
        }
        ProductItemText(item.name)
        ProductItemText(item.price)
    }
}

@Composable
fun ProductItemText(item: String) {
    Text(
        text = item,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        color = Color.Black,
    )
}

@Preview(showBackground = true)
@Composable
fun ProductItemPreview() {
    val sampleItem = ProductModel(
        key = "sampleKey",
        name = "Sample Product",
        price = "15000"
    )
    ProductItemContent(
        item = sampleItem,
        imageUrl = "https://via.placeholder.com/150/",
        onClick = {}
    )
}