package com.myungwoo.shoppingmall_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.myungwoo.model.CouponModel
import com.myungwoo.model.ProductModel
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.product.ProductInputActivity
import com.myungwoo.shoppingmall_app.ui.search.SearchResultFragment
import com.myungwoo.shoppingmall_app.utils.FBRef
import kotlinx.coroutines.delay

class ShopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ShopScreen()
                }
            }
        }
    }
}

@Composable
fun ShopScreen() {
    val context = LocalContext.current
    val productList = remember { mutableStateListOf<ProductModel>() }
    val couponList = remember { mutableStateListOf<CouponModel>() }
    val itemKeyList = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        getCategoryDataProduct(productList, itemKeyList)
        getCouponData(couponList)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail == "admin1@admin1@") {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, ProductInputActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.End)

            ) {
                Text("+")
            }
        }
        SearchBar()

        val images = listOf(R.drawable.shop_img, R.drawable.shop_img2, R.drawable.shop_img3)
        ImageSlider(images)

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
//            items(items.chunked(2)) { coupon ->
//                CouponItem(coupon)
//            }
        }
    }
}

@Composable
fun SearchBar() {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("상품 검색") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .clickable {
                val intent = Intent(context, SearchResultFragment::class.java)
                intent.putExtra("searchQuery", searchText)
                context.startActivity(intent)
            },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
        )
    )
}

@Composable
fun CouponItem(coupon: CouponModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coupon.image)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = coupon.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = coupon.discount,
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun ImageSlider(images: List<Int>) {
    val sliderState = remember { mutableIntStateOf(0) }
    val handler = rememberUpdatedState(newValue = Runnable {
        sliderState.intValue = (sliderState.intValue + 1) % images.size
    })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            handler.value.run()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = images[sliderState.intValue]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            images.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(
                            if (sliderState.intValue == index) Color.Black else Color.Gray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

private fun getCouponData(couponList: MutableList<CouponModel>) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (dataModel in dataSnapshot.children) {
                val item = dataModel.getValue(CouponModel::class.java)
                couponList.add(item!!)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("Coupon Load", "loadPost:onCancelled", databaseError.toException())
        }
    }
    FBRef.couponRef.addValueEventListener(postListener)
}

private fun getCategoryDataProduct(
    productList: MutableList<ProductModel>,
    itemKeyList: MutableList<String>
) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            productList.clear()
            for (dataModel in dataSnapshot.children) {
                val item = dataModel.getValue(ProductModel::class.java)
                itemKeyList.add(dataModel.key.toString())
                productList.add(item!!)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("Product Load", "loadPost:onCancelled", databaseError.toException())
        }
    }
    FBRef.productRef.addValueEventListener(postListener)
}

@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    MaterialTheme {
        ShopScreen()
    }
}