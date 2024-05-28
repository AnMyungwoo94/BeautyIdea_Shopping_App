package com.myungwoo.shoppingmall_app.ui.tipList

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.data.ContentModel
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef

class ContentListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ContentListScreen()
            }
        }
    }
}

@Composable
fun ContentListScreen() {
    val context = LocalContext.current as Activity
    var items by remember { mutableStateOf(listOf<ContentModel>()) }
    var itemKeyList by remember { mutableStateOf(listOf<String>()) }
    var bookmarkIdList by remember { mutableStateOf(listOf<String>()) }

    val myRef: DatabaseReference = when (context.intent.getStringExtra("category")) {
        "categoryALl" -> FBRef.content.child("categoryALl")
        "categoryLip" -> FBRef.content.child("categoryLip")
        "categoryBlusher" -> FBRef.content.child("categoryBlusher")
        "categoryMascara" -> FBRef.content.child("categoryMascara")
        "categoryNail" -> FBRef.content.child("categoryNail")
        "categoryShadow" -> FBRef.content.child("categoryShadow")
        "categorySkin" -> FBRef.content.child("categorySkin")
        "categorySun" -> FBRef.content.child("categorySun")
        else -> FBRef.content.child("categoryALl")
    }

    LaunchedEffect(Unit) {
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
                items = newItems
                itemKeyList = newItemKeyList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "ContentListActivity",
                    "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        }
        myRef.addValueEventListener(postListener)
        getBookmarkData { bookmarks ->
            bookmarkIdList = bookmarks
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "카테고리",
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
                        TipContentItem(
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
fun ContentListScreenPreview() {
    MaterialTheme {
        ContentListScreen()
    }
}