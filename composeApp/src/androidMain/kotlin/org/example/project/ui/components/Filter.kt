package org.example.project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.model.Filter
import org.example.project.ui.theme.warna1
import org.example.project.ui.theme.Divider
import org.example.project.utils.shadow


@Composable
fun FilterChip(
    filter: Filter,
    onSelected: (Filter) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(percent = 50)
) {
    val selected by filter.enabled
    val backgroundColor by animateColorAsState(
        if (selected) warna1 else Divider,
        label = "background color"
    )

    val textColor by animateColorAsState(
        if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
        label = "text color"
    )

    Surface(
        modifier = if (selected) {
            modifier.shadow(
                color = Color.Black.copy(alpha = 0.2f),
                borderRadius = 20.dp,
                blurRadius = 15.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
                spread = 0.dp
            )
        } else {
            modifier
        },
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val backgroundPressed =
            if (pressed) {
                Modifier.background(MaterialTheme.colorScheme.secondary)
            } else {
                Modifier.background(Color.Transparent)
            }
        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = { onSelected(filter) },
                    interactionSource = interactionSource,
                    indication = null
                )
                .then(backgroundPressed)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}