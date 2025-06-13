package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.model.FilterTab
import org.example.project.R

@Composable
fun UserFilterTabs(
    selectedFilter: MutableState<String>,
    modifier: Modifier = Modifier,
    chipWidth: Dp = 164.dp,
    chipHeight: Dp = 44.dp
) {
    val filters = listOf(
        FilterTab("Laporan", R.drawable.ic_laporan),
        FilterTab("Undangan", R.drawable.ic_undangan)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter.value == filter.name
                FilterTabItem(
                    filter = filter,
                    isSelected = isSelected,
                    onClick = { selectedFilter.value = filter.name },
                    modifier = Modifier
                        .width(chipWidth)
                        .height(chipHeight)
                )
            }
        }
    }
}

@Composable
fun FilterTabItem(
    filter: FilterTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF1E88E5) else Color(0xFFFFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = filter.iconRes),
            contentDescription = filter.name,
            tint = if (isSelected) Color.White else Color(0xFF666666),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = filter.name,
            color = if (isSelected) Color.White else Color(0xFF333333),
            fontSize = 14.sp
        )
    }
}