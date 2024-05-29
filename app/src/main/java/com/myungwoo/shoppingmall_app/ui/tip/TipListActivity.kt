package com.myungwoo.shoppingmall_app.ui.tip

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.common.compose.component.TipBookmarkItem
import com.myungwoo.shoppingmall_app.data.ContentModel
import com.myungwoo.shoppingmall_app.ui.category.ShopCategory
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef

class TipListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TipListScreen()
            }
        }
    }
}

@Composable
fun TipListScreen() {
    val context = LocalContext.current as Activity

    var items by remember { mutableStateOf(listOf<ContentModel>()) }
    var itemKeyList by remember { mutableStateOf(listOf<String>()) }
    var bookmarkIdList by remember { mutableStateOf(listOf<String>()) }

    val category = context.intent.getStringExtra("category")
    val shopCategory = ShopCategory.entries.find { it.firebaseCategoryName == category }
    val myRef: DatabaseReference? = shopCategory?.let { FBRef.content.child(it.firebaseCategoryName) }

    LaunchedEffect(Unit) {
        if (myRef != null) {
            loadData(myRef) { newItems, newItemKeyList ->
                items = newItems
                itemKeyList = newItemKeyList
            }
        }
        getBookmarkData { bookmarks ->
            bookmarkIdList = bookmarks
        }
    }

    ContentListItem(items, itemKeyList, bookmarkIdList)
}

@Composable
fun ContentListItem(items: List<ContentModel>, itemKeyList: List<String>, bookmarkIdList: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.tip_category),
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(items.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { item ->
                        TipBookmarkItem(
                            item = item,
                            key = itemKeyList[items.indexOf(item)],
                            bookmarkIdList = bookmarkIdList,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun loadData(myRef: DatabaseReference, onDataLoaded: (List<ContentModel>, List<String>) -> Unit) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val newItems = mutableListOf<ContentModel>()
            val newItemKeyList = mutableListOf<String>()
            for (dataModel in dataSnapshot.children) {
                val item = dataModel.getValue(ContentModel::class.java)
                if (item != null) {
                    newItems.add(item)
                    newItemKeyList.add(dataModel.key.toString())
                }
            }
            onDataLoaded(newItems, newItemKeyList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
        }
    }
    myRef.addValueEventListener(postListener)
}

private fun getBookmarkData(bookmarkIdList: (List<String>) -> Unit) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val bookmarks = mutableListOf<String>()
            for (dataModel in dataSnapshot.children) {
                bookmarks.add(dataModel.key.toString())
            }
            bookmarkIdList(bookmarks)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
        }
    }
    FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
}


@Preview(showBackground = true)
@Composable
fun ContentListItemPreview() {
    val items = listOf(
        ContentModel(
            title = "Sample Title 1",
            imageUrl = "https://via.placeholder.com/150",
            webUrl = "https://www.example.com"
        ),
        ContentModel(
            title = "Sample Title 2",
            imageUrl = "https://via.placeholder.com/150",
            webUrl = "https://www.example.com"
        )
    )
    val itemKeyList = listOf("sampleKey1", "sampleKey2")
    val bookmarkIdList = listOf("sampleKey1")

    MaterialTheme {
        ContentListItem(items = items, itemKeyList = itemKeyList, bookmarkIdList = bookmarkIdList)
    }
}