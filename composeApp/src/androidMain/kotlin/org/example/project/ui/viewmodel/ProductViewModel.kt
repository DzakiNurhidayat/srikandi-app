package org.example.project.ui.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repositories.ProductRepository
import org.example.project.model.entities.Product
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {
    val products = MutableStateFlow<List<Product>>(emptyList())
    init {
        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                if (response.status) {
                    products.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products: ${e.message}")
            }
        }
    }
}
