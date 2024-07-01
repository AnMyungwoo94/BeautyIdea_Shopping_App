package com.myungwoo.shoppingmall_app.paging.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PagingScreen()
            }
        }
    }
}

@Composable
fun PagingScreen() {
    val viewModel: PagingViewModel = viewModel()
    val peopleData = viewModel.people.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(peopleData.itemCount) { index ->
            val people = peopleData[index]
            people?.let {
                PeopleItem(it)
            }
        }
    }
}

@Composable
fun PeopleItem(people: PeopleData) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = people.id.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 16.dp)
        )
        GlideImage(
            imageModel = { people.image?.medium ?: R.drawable.mypage_icon },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp),
        )
        Text(
            text = people.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

