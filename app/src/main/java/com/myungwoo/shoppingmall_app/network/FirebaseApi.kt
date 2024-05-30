import com.myungwoo.shoppingmall_app.data.ProductModel
import retrofit2.http.GET
import retrofit2.http.Query

interface FirebaseApi {

    @GET("product.json")
    suspend fun getAllProduct(@Query("auth") idToken: String): Map<String, ProductModel>

    @GET("product.json")
    suspend fun getProductsByCategory(@Query("category") category: String): Map<String, ProductModel>
}
