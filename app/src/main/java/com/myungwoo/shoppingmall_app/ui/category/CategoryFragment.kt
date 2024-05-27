package com.myungwoo.shoppingmall_app.ui.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.ui.product.ProductDetailActivity
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.tasks.await

class CategoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    CategoryScreen()
                }
            }
        }
    }
}

@Composable
fun CategoryScreen() {
    val categories = ShopCategory.entries
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Black
                )
            },
        ) {
            categories.forEachIndexed { index, category ->
                Log.e("카테고리 인덱스", "$index 은 $category ")
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(stringResource(id = category.resId)) }
                )
            }
        }
        CategoryContentScreen(category = categories[selectedTabIndex])
        Log.e("categories[selectedTabIndex]", categories[selectedTabIndex].toString())
    }
}

@Composable
fun CategoryContentScreen(category: ShopCategory) {
    Log.e("category", category.toString())
    val context = LocalContext.current
    var items by remember { mutableStateOf(listOf<ProductModel>()) }
    val dbRef = FirebaseDatabase.getInstance().getReference("product")

    LaunchedEffect(category) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryItems =
                    snapshot.children.mapNotNull { it.getValue(ProductModel::class.java) }
                items = categoryItems
                Log.e("categoryItems", items.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoryContentScreen", "loadCategoryData:onCancelled", error.toException())
            }
        }
        dbRef.orderByChild("category").equalTo(category.name.lowercase())
            .addValueEventListener(postListener)
    }

    LazyColumn(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        ProductItem(item) {
                            val intent = Intent(context, ProductDetailActivity::class.java)
                            intent.putExtra("ITEM_DATA", it)
                            context.startActivity(intent)
                        }
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

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