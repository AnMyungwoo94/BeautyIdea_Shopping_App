package com.myungwoo.shoppingmall_app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityMainBinding
import com.myungwoo.shoppingmall_app.ui.board.TalkFragment
import com.myungwoo.shoppingmall_app.ui.bookmark.BookmarkFragment
import com.myungwoo.shoppingmall_app.ui.category.CategoryFragment
import com.myungwoo.shoppingmall_app.ui.home.ShopFragment
import com.myungwoo.shoppingmall_app.ui.tip.TipFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarComposeView.setContent {
            TopBar()
        }
        binding.bottomNavComposeView.setContent {
            val navController = rememberNavController()
            MainScreen(navController)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { padding ->
        NavHostContainer(navController, Modifier.padding(padding))
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.mainicon_peach),
                contentDescription = "Main Icon",
                modifier = Modifier
                    .size(width = 80.dp, height = 40.dp)
            )
        },
        actions = {
            AppBarAction(R.drawable.cart_icon) { /* Navigate to cart */ }
            AppBarAction(R.drawable.mypage_icon) { /* Navigate to settings */ }
        },
        backgroundColor = Color.White
    )
}


@Composable
fun AppBarAction(iconRes: Int, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val bottomNavItems = listOf(
        Triple("category", R.drawable.cart_icon, "쇼핑"),
        Triple("community", R.drawable.community_icon, "커뮤니티"),
        Triple("home", R.drawable.store_icon, "홈"),
        Triple("tip", R.drawable.tip_icon, "꿀팁"),
        Triple("bookmark", R.drawable.bookmark_icon, "북마크")
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "navigation icon"
                    )
                },
                label = {
                    Text(label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.LightGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") { FragmentContainer { ShopFragment() } }
        composable("category") { FragmentContainer { CategoryFragment() } }
        composable("community") { FragmentContainer { TalkFragment() } }
        composable("tip") { FragmentContainer { TipFragment() } }
        composable("bookmark") { FragmentContainer { BookmarkFragment() } }
    }
}

@Composable
fun FragmentContainer(fragmentInstance: () -> Fragment) {
    val context = LocalContext.current as? AppCompatActivity ?: return

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            FragmentContainerView(context).apply {
                id = R.id.fragmentContainerView
            }
        },
        update = {
            context.supportFragmentManager.commit {
                replace(it.id, fragmentInstance())
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    MaterialTheme {
        TopBar()
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        MainScreen(navController)
    }
}