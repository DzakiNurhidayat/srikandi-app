package org.example.project.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.utils.shadow
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    shadowColor: Color = Color.Black.copy(alpha = 0.2f),
    borderRadius: Int = 10,
    blurRadius: Int = 15,
    offsetX: Int = 2,
    offsetY: Int = 8
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                color = shadowColor,
                borderRadius = borderRadius.dp,
                blurRadius = blurRadius.dp,
                offsetX = offsetX.dp,
                offsetY = offsetY.dp
            )
            .hoverable(remember { MutableInteractionSource() }),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(borderRadius.dp)
    ) {
        Text(text = text)
    }
}
