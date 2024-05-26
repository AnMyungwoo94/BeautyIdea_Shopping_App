package com.myungwoo.shoppingmall_app.ui.tip

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.tipList.ContentListActivity

class TipFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    TipFragmentContent()
                }
            }
        }
    }
}

@Composable
fun TipFragmentContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
        ) {
            CategorySection()
        }
    }
}

@Composable
fun CategorySection() {
    val categories = listOf(
        Pair(R.drawable.category1_all, "categoryALl"),
        Pair(R.drawable.category2_lip, "categoryLip"),
        Pair(R.drawable.category3_blusher, "categoryBlusher"),
        Pair(R.drawable.category4_mascara, "categoryMascara"),
        Pair(R.drawable.category5_nail, "categoryNail"),
        Pair(R.drawable.category6_shadow, "categoryShadow"),
        Pair(R.drawable.category7_skin, "categorySkin"),
        Pair(R.drawable.category8_sun, "categorySun")
    )

    categories.chunked(3).forEachIndexed { index, row ->
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = if (index == categories.chunked(3).lastIndex) Arrangement.Start else Arrangement.SpaceBetween
            ) {
                row.forEach { (imageRes, category) ->
                    CategoryItem(imageRes, category)
                }
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.size(120.dp))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = if (index == categories.chunked(3).lastIndex) Arrangement.Start else Arrangement.SpaceBetween
            ) {
                row.forEach { (_, category) ->
                    Text(
                        text = category.replace("category", ""),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(imageRes: Int, category: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(120.dp)
            .clickable {
                val intent = Intent(context, ContentListActivity::class.java)
                intent.putExtra("category", category)
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipFragmentContentPreview() {
    MaterialTheme {
        TipFragmentContent()
    }
}
