package com.nuecoo.feature.auth.presentation.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.theme.MainText
import com.nuecoo.core.theme.MenuSubBoxBackground
import com.nuecoo.core.theme.SubText
import kotlin.math.abs

private val ITEM_HEIGHT = 52.dp
private const val VISIBLE_ITEMS = 5
private const val HALF_VISIBLE = VISIBLE_ITEMS / 2
private const val CIRCULAR_MULTIPLIER = 10_000

private val YEAR_COLUMN_WIDTH = 96.dp
private val MONTH_DAY_COLUMN_WIDTH = 60.dp

@Composable
fun BirthDateSpinnerItem(
    year: Int,
    month: Int,
    day: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = remember { (1900..2026).toList() }
    val months = remember { (1..12).toList() }

    val maxDay = remember(year, month) { daysInMonth(year, month) }
    val days = remember(maxDay) { (1..maxDay).toList() }
    val clampedDay = day.coerceAtMost(maxDay)

    LaunchedEffect(maxDay) {
        if (day > maxDay) onDayChanged(maxDay)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT * VISIBLE_ITEMS),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                .padding(horizontal = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MenuSubBoxBackground)
        )

        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpinnerColumn(
                items = years,
                selectedValue = year,
                onValueChanged = onYearChanged,
                circular = false,
                modifier = Modifier.width(YEAR_COLUMN_WIDTH)
            )
            SpinnerUnitLabel(stringResource(R.string.year))
            SpinnerColumn(
                items = months,
                selectedValue = month,
                onValueChanged = onMonthChanged,
                circular = true,
                modifier = Modifier.width(MONTH_DAY_COLUMN_WIDTH)
            )
            SpinnerUnitLabel(stringResource(R.string.month))
            key(maxDay) {
                SpinnerColumn(
                    items = days,
                    selectedValue = clampedDay,
                    onValueChanged = onDayChanged,
                    circular = true,
                    modifier = Modifier.width(MONTH_DAY_COLUMN_WIDTH)
                )
            }
            SpinnerUnitLabel(stringResource(R.string.day), modifier = Modifier.padding(end = 20.dp))
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 31
}

@Composable
private fun SpinnerColumn(
    items: List<Int>,
    selectedValue: Int,
    onValueChanged: (Int) -> Unit,
    circular: Boolean,
    modifier: Modifier = Modifier
) {
    if (circular) {
        CircularSpinnerColumn(
            items = items,
            selectedValue = selectedValue,
            onValueChanged = onValueChanged,
            modifier = modifier
        )
    } else {
        LinearSpinnerColumn(
            items = items,
            selectedValue = selectedValue,
            onValueChanged = onValueChanged,
            modifier = modifier
        )
    }
}

@Composable
private fun LinearSpinnerColumn(
    items: List<Int>,
    selectedValue: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val paddedItems: List<Int?> = remember(items) {
        List(HALF_VISIBLE) { null } + items + List(HALF_VISIBLE) { null }
    }

    val initialIndex = items.indexOf(selectedValue).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val density = LocalDensity.current
    val itemHeightPx = remember(density) { with(density) { ITEM_HEIGHT.toPx() } }

    val centerIndex by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            (firstVisible + if (offset > itemHeightPx / 2) 1 else 0)
                .coerceIn(0, items.lastIndex)
        }
    }

    LaunchedEffect(centerIndex) {
        onValueChanged(items[centerIndex])
    }

    Box(
        modifier = modifier.height(ITEM_HEIGHT * VISIBLE_ITEMS),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(paddedItems) { index, value ->
                val firstVisible = listState.firstVisibleItemIndex
                val offset = listState.firstVisibleItemScrollOffset
                val adjustedFirst =
                    if (offset > itemHeightPx / 2) firstVisible + 1 else firstVisible
                val distance = abs(index - (adjustedFirst + HALF_VISIBLE))

                SpinnerItemBox(value = value, distance = distance)
            }
        }
    }
}

@Composable
private fun CircularSpinnerColumn(
    items: List<Int>,
    selectedValue: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = CIRCULAR_MULTIPLIER * items.size
    val initialIndex = items.indexOf(selectedValue).coerceAtLeast(0)
    val initialFirstVisible = (CIRCULAR_MULTIPLIER / 2) * items.size + initialIndex - HALF_VISIBLE

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirstVisible)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val density = LocalDensity.current
    val itemHeightPx = remember(density) { with(density) { ITEM_HEIGHT.toPx() } }

    val centerIndex by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val adjustedFirst = firstVisible + if (offset > itemHeightPx / 2) 1 else 0
            (adjustedFirst + HALF_VISIBLE) % items.size
        }
    }

    LaunchedEffect(centerIndex) {
        onValueChanged(items[centerIndex])
    }

    Box(
        modifier = modifier.height(ITEM_HEIGHT * VISIBLE_ITEMS),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(totalCount) { index ->
                val value = items[index % items.size]
                val firstVisible = listState.firstVisibleItemIndex
                val offset = listState.firstVisibleItemScrollOffset
                val adjustedFirst = firstVisible + if (offset > itemHeightPx / 2) 1 else 0
                val distance = abs(index - (adjustedFirst + HALF_VISIBLE))

                SpinnerItemBox(value = value, distance = distance)
            }
        }
    }
}

@Composable
private fun SpinnerItemBox(value: Int?, distance: Int) {
    val alpha = when {
        distance == 0 -> 1f
        distance == 1 -> 0.55f
        else -> 0.2f
    }
    val fontSize = when {
        distance == 0 -> 20.sp
        distance == 1 -> 16.sp
        else -> 12.sp
    }

    Box(
        modifier = Modifier
            .height(ITEM_HEIGHT)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (value != null) {
            Text(
                text = value.toString(),
                fontSize = fontSize,
                fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Normal,
                color = (if (distance == 0) MainText else SubText).copy(alpha = alpha)
            )
        }
    }
}

@Composable
private fun SpinnerUnitLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MainText,
        modifier = modifier.padding(horizontal = 4.dp)
    )
}
