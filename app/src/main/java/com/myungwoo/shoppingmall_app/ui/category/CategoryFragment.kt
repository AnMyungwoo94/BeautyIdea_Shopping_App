package com.myungwoo.shoppingmall_app.ui.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.common.compose.component.ProductItem
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.ui.product.ProductDetailActivity

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
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(stringResource(id = category.resId)) }
                )
            }
        }
        CategoryData(category = categories[selectedTabIndex])
    }
}

@Composable
fun CategoryData(category: ShopCategory) {
    val context = LocalContext.current
    var items by remember { mutableStateOf(listOf<ProductModel>()) }
    val dbRef = FirebaseDatabase.getInstance().getReference("product")

    LaunchedEffect(category) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryItems =
                    snapshot.children.mapNotNull { it.getValue(ProductModel::class.java) }
                items = categoryItems
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoryContentScreen", "loadCategoryData:onCancelled", error.toException())
            }
        }
        dbRef.orderByChild("category").equalTo(category.name.lowercase()).addValueEventListener(postListener)
    }

    CategoryItem(items = items) { item ->
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra("ITEM_DATA", item)
        context.startActivity(intent)
    }
}

@Composable
fun CategoryItem(items: List<ProductModel>, onItemClick: (ProductModel) -> Unit) {
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
                            onItemClick(item)
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

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    val sampleItems = listOf(
        ProductModel(key = "id1", name = "Product 1", price = 1000.toString(), time = "12:00 PM", parcel = "Parcel 1", deliveryFee = 100, parcelDay = "1 Day", category = "category1"),
        ProductModel(key = "id2", name = "Product 2", price = 2000.toString(), time = "1:00 PM", parcel = "Parcel 2", deliveryFee = 200, parcelDay = "2 Days", category = "category1")
    )
    MaterialTheme {
        CategoryItem(items = sampleItems, onItemClick = {})
    }
}