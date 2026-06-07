package com.nuecoo.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.core.ui.model.CommonDropDownItem
import com.nuecoo.ui.theme.MainText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CommonDropDown(
    selectedValue: T,
    items: List<CommonDropDownItem<T>>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,

    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,

    itemHeight: Dp = Dp.Unspecified,

    cornerRadius: Dp = 30.dp,
    itemCornerRadius: Dp = 24.dp,

    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,

    itemBackgroundColor: Color = Color.Transparent,
    itemBorderColor: Color = Color.Transparent,
    itemBorderShape: Dp = 0.dp,
    itemBorderWidth: Dp = 0.dp,

    itemSelectedBackgroundColor: Color = Color.Transparent,
    itemUnselectedBackgroundColor: Color = Color.Transparent,

    textColor: Color = MainText,
    menuTextColor: Color = MainText,
    iconColor: Color = MainText,

    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight = FontWeight.Light,

    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 12.dp,

    itemHorizontalPadding: Dp = 8.dp,
    itemVerticalPadding: Dp = 0.dp,

    dropDownIcon: ImageVector = Icons.Default.ArrowDropDown,
    dropDownIconSize: Dp = 20.dp,

    itemLeadingContent: @Composable ((CommonDropDownItem<T>) -> Unit)? = null,
    selectedTrailingContent: @Composable (() -> Unit)? = null,
    selectedLeadingContent: @Composable ((T) -> Unit)? = null,

    menuAnchorType: MenuAnchorType = MenuAnchorType.PrimaryNotEditable,
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
                .menuAnchor(
                    type = menuAnchorType,
                    enabled = true
                )
                .then(
                    if (width != Dp.Unspecified) {
                        Modifier.width(width)
                    } else {
                        Modifier.wrapContentWidth()
                    }
                )
                .then(
                    if (height != Dp.Unspecified) {
                        Modifier.height(height)
                    } else {
                        Modifier.wrapContentHeight()
                    }
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedLeadingContent?.invoke(selectedValue)

            Text(
                text = selectedLabel,
                color = textColor,
                fontSize = fontSize,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            Icon(
                imageVector = dropDownIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(dropDownIconSize)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(width),
            shape = RoundedCornerShape(itemCornerRadius),
            offset = DpOffset(x = 0.dp, y = 8.dp),
            containerColor = itemBackgroundColor,
            shadowElevation = 2.dp
        ) {
            items.forEach { item ->
                val isSelected = item.value == selectedValue

                DropdownMenuItem(
                    modifier = Modifier
                        .padding(vertical = itemVerticalPadding, horizontal = itemHorizontalPadding)
                        .fillMaxWidth()
                        .border(
                            itemBorderWidth,
                            itemBorderColor,
                            RoundedCornerShape(itemBorderShape)
                        )
                        .then(
                            if (itemHeight != Dp.Unspecified) {
                                Modifier.height(itemHeight)
                            } else {
                                Modifier.wrapContentHeight()
                            }
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) itemSelectedBackgroundColor
                            else itemUnselectedBackgroundColor
                        ),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemLeadingContent?.let {
                                it(item)
                                Spacer(Modifier.width(12.dp))
                            }

                            Text(
                                text = item.label,
                                color = menuTextColor,
                                fontSize = fontSize,
                                fontFamily = fontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else fontWeight,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected) {
                                selectedTrailingContent?.invoke()
                            }
                        }
                    },
                    onClick = {
                        onItemSelected(item.value)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = menuTextColor
                    )
                )
            }
        }
    }
}