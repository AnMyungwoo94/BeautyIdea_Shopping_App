import com.google.firebase.auth.FirebaseAuth
import com.myungwoo.shoppingmall_app.data.ProductModel
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val firebaseApi = RetrofitInstance.create().create(FirebaseApi::class.java)

    suspend fun getIdToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.getIdToken(true)?.await()?.token
    }

    suspend fun getProduct(idToken: String): Map<String, ProductModel> {
        return firebaseApi.getAllProduct(idToken)
    }

    suspend fun getProductsByCategory(category: String): Map<String, ProductModel>{
        return firebaseApi.getProductsByCategory(category)
    }
}
