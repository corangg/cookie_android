package com.nuecoo.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.ui.model.CommonDropDownItem
import com.nuecoo.ui.theme.DropDownBackground
import com.nuecoo.ui.theme.MainText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CollectionDropDown(
    selectedValue: T,
    items: List<CommonDropDownItem<T>>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,

    width: Dp = 140.dp,
    height: Dp = 44.dp,
    cornerRadius: Dp = 30.dp,

    backgroundColor: Color = DropDownBackground,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,

    textColor: Color = MainText,
    menuTextColor: Color = MainText,
    iconColor: Color = MainText,

    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = FontFamily(Font(R.font.cookie_run_regular)),
    fontWeight: FontWeight = FontWeight.Light,

    horizontalPadding: Dp = 16.dp
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = items
        .firstOrNull { it.value == selectedValue }
        ?.label
        ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .menuAnchor()
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedLabel,
                color = textColor,
                fontSize = fontSize,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            color = menuTextColor,
                            fontSize = fontSize,
                            fontFamily = fontFamily,
                            fontWeight = fontWeight
                        )
                    },
                    onClick = {
                        onItemSelected(item.value)
                        expanded = false
                    }
                )
            }
        }
    }
}