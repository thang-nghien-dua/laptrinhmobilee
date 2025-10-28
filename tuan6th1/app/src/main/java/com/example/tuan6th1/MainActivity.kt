package com.example.tuan6th1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tuan6th1.ui.theme.Tuan6th1Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
// removed duplicate padding import
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
// removed unused state-related imports
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.Locale

data class Product(
    @SerializedName("id") val id: String?,
    @SerializedName(value = "name", alternate = ["title"]) val name: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName(value = "description", alternate = ["desc", "content", "des"]) val description: String?,
    @SerializedName(value = "image", alternate = ["imageUrl", "thumbnail", "image_url", "imgURL"]) val image: String?
)

interface ApiService {
    @retrofit2.http.GET("v2/product")
    suspend fun getProduct(): Product
}

object ApiClient {
    private const val BASE_URL = "https://mock.apidog.com/m1/890655-872447-default/"

    val api: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}

class ProductRepository(private val service: ApiService) {
    suspend fun fetchProduct(): Product = service.getProduct()
}

data class ProductUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository(ApiClient.api)
    private val _state = MutableStateFlow(ProductUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val p = repository.fetchProduct()
                _state.value = ProductUiState(product = p)
            } catch (e: Exception) {
                _state.value = ProductUiState(error = e.message ?: "Lỗi không xác định")
            }
        }
    }
}

@Composable
fun ProductDetailScreen(vm: ProductViewModel = viewModel()) {
    val ui by vm.state.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        val product = ui.product
        when {
            ui.isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            ui.error != null -> Text("Lỗi: ${ui.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            product != null -> ProductScreenScaffold(product)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProductScreenScaffold(product: Product) {
    Scaffold(topBar = { TopAppBar(title = { Text("Product detail") }) }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 560.dp)
            ) {
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = product.name ?: "",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                val priceText = product.price?.let { NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(it) } ?: ""
                Text(
                    text = "Giá: $priceText",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                if (!product.description.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = product.description ?: "",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tuan6th1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ProductDetailScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Tuan6th1Theme {
        Greeting("Android")
    }
}