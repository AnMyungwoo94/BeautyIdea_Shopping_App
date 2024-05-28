package com.myungwoo.shoppingmall_app.common.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.google.firebase.storage.FirebaseStorage
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
    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick(item) }
            .padding(8.dp)
    ) {
        imageUrl?.let {
            GlideImage(
                imageModel = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = item.name,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black
        )
        Text(
            text = item.price,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black
        )
    }
}