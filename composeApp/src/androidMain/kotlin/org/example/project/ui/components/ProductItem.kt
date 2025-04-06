package org.example.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.model.entities.Product

@Composable
fun ProductItem(product: Product) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleLarge)
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "$${product.price}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
