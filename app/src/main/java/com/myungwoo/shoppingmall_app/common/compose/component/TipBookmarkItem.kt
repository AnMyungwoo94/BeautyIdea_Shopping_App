package com.myungwoo.shoppingmall_app.common.compose.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.BookmarkModel
import com.myungwoo.shoppingmall_app.data.ContentModel
import com.myungwoo.shoppingmall_app.ui.tip.ContentShowActivity
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun TipBookmarkItem(
    item: ContentModel,
    key: String,
    bookmarkIdList: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .background(Color.White)
            .clickable {
                val intent = Intent(context, ContentShowActivity::class.java)
                intent.putExtra("uri", item.webUrl)
                context.startActivity(intent)
            }
    ) {
        TipBookmarkContent(item, key, bookmarkIdList)
    }
}

@Composable
fun TipBookmarkContent(item: ContentModel, key: String, bookmarkIdList: List<String>) {
    GlideImage(
        imageModel = item.imageUrl,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        previewPlaceholder = R.drawable.home_img
    )
    Text(
        text = item.title,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    BookmarkIcon(key = key, bookmarkIdList = bookmarkIdList)
}

@Composable
fun BookmarkIcon(key: String, bookmarkIdList: List<String>) {
    val isBookmarked = bookmarkIdList.contains(key)

    val bookmarkIcon = if (isBookmarked) {
        R.drawable.bookmark_color
    } else {
        R.drawable.bookmark_white
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = bookmarkIcon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
                .clickable {
                    val uid = FBAuth.getUid()
                    val ref = FBRef.bookmarkRef
                        .child(uid)
                        .child(key)
                    if (isBookmarked) {
                        ref.removeValue()
                    } else {
                        ref.setValue(BookmarkModel(true))
                    }
                }
        )
    }
    Spacer(modifier = Modifier.padding(8.dp))
}

@Preview(showBackground = true)
@Composable
fun TipBookmarkItemPreview() {
    val item = ContentModel(
        title = "Sample Title",
        imageUrl = "https://via.placeholder.com/150",
        webUrl = "https://www.example.com"
    )
    val key = "sampleKey"
    val bookmarkIdList = listOf("sampleKey")

    MaterialTheme {
        TipBookmarkItem(item = item, key = key, bookmarkIdList = bookmarkIdList)
    }
}