package org.example.project.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import org.example.project.ui.components.ProductItem
import org.example.project.ui.viewmodel.ProductViewModel

@Composable
fun ProductScreen(viewModel: ProductViewModel = hiltViewModel()) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Product List") }) }
    ) {
        LazyColumn(contentPadding = it) {
            items(products) { product ->
                ProductItem(product)
            }
        }
    }
}