package com.myungwoo.shoppingmall_app.ui.board

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.BoardModel
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.tasks.await

class TalkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    TalkScreen()
                }
            }
        }
    }
}

@Composable
fun TalkScreen() {
    val context = LocalContext.current
    val boardDataList = remember { mutableStateListOf<BoardModel>() }
    val boardKeyList = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        getFBBoardData(boardDataList, boardKeyList)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(boardDataList) { boardItem ->
                val position = boardDataList.indexOf(boardItem)

                BoardData(boardItem, boardKeyList[position]) {
                    val intent = Intent(context, BoardInsideActivity::class.java)
                    intent.putExtra("key", boardKeyList[position])
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun BoardData(boardItem: BoardModel, key: String, onClick: () -> Unit) {
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key) {
        val storageRef = Firebase.storage.reference.child("$key.png")
        imageUrl = try {
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
    BoardItem(boardItem, imageUrl, onClick)
}

@Composable
fun BoardItem(boardItem: BoardModel, imageUrl: String?, onClick: () -> Unit) {
    Spacer(modifier = Modifier.padding(3.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(250.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (boardItem.uid == FBAuth.getUid()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFD400))
                )
            }
            GlideImage(
                imageModel = imageUrl ?: R.drawable.home_img,
                contentDescription = "커뮤니티 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop,
                placeHolder = painterResource(id = R.drawable.home_img),
                error = painterResource(id = R.drawable.home_img)
            )
            BoardItemText(text = boardItem.title, fontSize = 17, modifier = Modifier.padding(top = 8.dp))
            BoardItemText(text = boardItem.content, fontSize = 13, modifier = Modifier.padding(top = 4.dp))
            BoardItemText(text = boardItem.time, fontSize = 10, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun BoardItemText(text: String, fontSize: Int, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = if (fontSize == 17) FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
    )
}

fun getFBBoardData(
    boardDataList: SnapshotStateList<BoardModel>,
    boardKeyList: SnapshotStateList<String>
) {
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            boardDataList.clear()
            for (dataModel in dataSnapshot.children) {
                val item = dataModel.getValue(BoardModel::class.java)
                if (item != null) {
                    boardDataList.add(item)
                    boardKeyList.add(dataModel.key.toString())
                }
            }
            boardDataList.reverse()
            boardKeyList.reverse()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TalkFragment", "loadPost:onCancelled", databaseError.toException())
        }
    }
    FBRef.boardRef.addValueEventListener(postListener)
}

@Preview(showBackground = true)
@Composable
fun TalkScreenPreview() {
    MaterialTheme {
        TalkScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun BoardItemPreview() {
    val sampleBoardModel = BoardModel(
        uid = "sampleUid",
        title = "Sample Title",
        content = "Sample content of the board item.",
        time = "12:00 PM"
    )
    MaterialTheme {
        BoardItem(boardItem = sampleBoardModel, imageUrl = null, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun BoardItemTextPreview() {
    MaterialTheme {
        BoardItemText(text = "Sample Text", fontSize = 17)
    }
}