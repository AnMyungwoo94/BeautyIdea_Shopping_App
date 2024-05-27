package com.myungwoo.shoppingmall_app.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.data.ContentModel
import com.myungwoo.shoppingmall_app.ui.tipList.TipContentItem
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef

class BookmarkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    BookmarkScreen()
                }
            }
        }
    }
}

@Composable
fun BookmarkScreen() {
    var bookmarkList by remember { mutableStateOf(listOf<String>()) }
    var items by remember { mutableStateOf(listOf<Pair<String, ContentModel>>()) }

    LaunchedEffect(Unit) {
        loadBookmarks { bookmarks ->
            bookmarkList = bookmarks
            loadAllCategories(bookmarks) { allItems ->
                items = allItems
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        LazyColumn {
            items(items.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { (key, item) ->
                        TipContentItem(
                            item = item,
                            key = key,
                            bookmarkIdList = bookmarkList,
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

private fun loadBookmarks(onDataReceived: (List<String>) -> Unit) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val bookmarks = dataSnapshot.children.mapNotNull { it.key }
            onDataReceived(bookmarks)
            Log.e("item", bookmarks.toString())
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("BookmarkFragment", "loadPost:onCancelled", databaseError.toException())
        }
    }
    FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
}

private fun loadAllCategories(
    bookmarkList: List<String>,
    onDataReceived: (List<Pair<String, ContentModel>>) -> Unit
) {
    val allItems = mutableListOf<Pair<String, ContentModel>>()
    val categories = listOf(
        "categoryALl",
        "categoryLip",
        "categoryBlusher",
        "categoryMascara",
        "categoryNail",
        "categoryShadow",
        "categorySkin",
        "categorySun"
    )
    var loadedCount = 0

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val items = dataSnapshot.children.mapNotNull { dataModel ->
                val item = dataModel.getValue(ContentModel::class.java)
                if (bookmarkList.contains(dataModel.key)) dataModel.key!! to item!! else null
            }
            allItems.addAll(items)
            loadedCount++
            if (loadedCount == categories.size) {
                onDataReceived(allItems)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("BookmarkFragment", "loadPost:onCancelled", databaseError.toException())
        }
    }

    categories.forEach { category ->
        FBRef.content.child(category).addListenerForSingleValueEvent(postListener)
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkScreenPreview() {
    MaterialTheme {
        BookmarkScreen()
    }
}
