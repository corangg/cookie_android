package com.nuecoo.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton

@Composable
fun NueCooTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = MainBorder.copy(alpha = 0.5f)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MainBorder,
            unfocusedTextColor = MainBorder,
            focusedContainerColor = MainBackground,
            unfocusedContainerColor = MainBackground,
            focusedBorderColor = MainBorder,
            unfocusedBorderColor = MainBorder.copy(alpha = 0.6f),
            cursorColor = MainBorder
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun NueCooButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MainButton else Color.Gray.copy(alpha = 0.4f),
            contentColor = MainBorder,
            disabledContainerColor = Color.Gray.copy(alpha = 0.4f),
            disabledContentColor = MainBorder.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(width = 3.dp, color = MainBorder, shape = RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MainBorder
        )
    }
}
