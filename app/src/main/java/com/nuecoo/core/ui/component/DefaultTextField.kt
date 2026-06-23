package com.nuecoo.core.ui.component

import android.R.attr.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.ui.theme.TextFieldBackground
import com.nuecoo.ui.theme.TextFieldBorder
import com.nuecoo.ui.theme.TextFieldHint
import com.nuecoo.ui.theme.TextFieldText

@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(20.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(
            color = TextFieldText,
            fontSize = 14.sp
        ),
        cursorBrush = SolidColor(TextFieldBorder),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
            },
        decorationBox = { innerTextField ->

            val borderModifier = if (isFocused) {
                Modifier.border(
                    width = 1.dp,
                    color = TextFieldBorder,
                    shape = shape
                )
            } else {
                Modifier
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = TextFieldBackground,
                        shape = shape
                    )
                    .then(borderModifier)
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = TextFieldHint,
                            fontSize = 14.sp
                        )
                    }

                    innerTextField()
                }

                // 비밀번호 보기 버튼
                if (isPassword && value.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))

                    Icon(
                        painter = if (passwordVisible) {
                            painterResource(R.drawable.ic_visibility_on)
                        } else {
                            painterResource(R.drawable.ic_visibility_off)
                        },
                        contentDescription = "비밀번호 보기",
                        tint = TextFieldHint,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                passwordVisible = !passwordVisible
                            }
                    )
                }

                // 입력값 삭제 버튼 (포커스 있을 때만 표시)
                if (value.isNotEmpty() && isFocused) {
                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "입력 내용 삭제",
                        tint = TextFieldHint,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onValueChange("")
                            }
                    )
                }
            }
        }
    )
}