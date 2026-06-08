package com.nuecoo.feature.main.presentation.collection

import android.R.attr.fontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.core.ui.component.CommonDropDown
import com.nuecoo.core.ui.model.CommonDropDownItem
import com.nuecoo.core.util.toDisplayDate
import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CollectionSortType
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.ui.theme.DropDownBackground
import com.nuecoo.ui.theme.DropDownSelectBackground
import com.nuecoo.ui.theme.ItemCardBackground
import com.nuecoo.ui.theme.ItemCardUnOpenedBackground
import com.nuecoo.ui.theme.ItemCardUnOpenedBorder
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.MainTitle
import com.nuecoo.ui.theme.SubTitle
import com.nuecoo.ui.theme.UnCollectedText
import com.nuecoo.ui.theme.White
import com.nuecoo.viewmodel.CollectionViewModel
import getCollectionTypeImages
import getCookieTypeColor
import getCookieTypeList
import getCookieTypeMainTextRes


@Composable
fun CollectionScreen(viewModel: CollectionViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedType by viewModel.selectedCookieType.collectAsState()
    val showCollectedOnly by viewModel.showCollectedOnly.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    CollectionScreenContent(
        items = items,
        isLoading = isLoading,
        selectedType = selectedType,
        showCollectedOnly = showCollectedOnly,
        sortType = sortType,
        onSortTypeChange = viewModel::setSortType,
        onShowCollectedOnlyChange = viewModel::setShowCollectedOnly,
        onTypeSelected = viewModel::setSelectedCookieType,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreenContent(
    items: List<CollectionDisplayItem>,
    isLoading: Boolean,
    selectedType: CookieType?,
    showCollectedOnly: Boolean,
    sortType: CollectionSortType,
    onSortTypeChange: (CollectionSortType) -> Unit,
    onShowCollectedOnlyChange: (Boolean) -> Unit,
    onTypeSelected: (CookieType?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            CollectionTitle(modifier = Modifier.weight(1f))//타이틀

            CollectionCheckBox(
                showCollectedOnly = showCollectedOnly,
                onShowCollectedOnlyChange = onShowCollectedOnlyChange
            )//체크박스
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 10.dp)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CollectionCookieTypeDropDown(
                selectedType = selectedType,
                onTypeSelected = onTypeSelected,
            )//타입 드랍다운
            Spacer(modifier = Modifier.weight(1f))
            CollectionSortDropDown(
                sortType = sortType,
                onSortTypeChange = onSortTypeChange,
                modifier = Modifier.padding(start = 28.dp)
            )//정렬 드랍다운
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MainBorder)
            }
        } else {
            TextCookieCount(items.size)//쿠키 갯수 표시
            CollectionItemView(items)//쿠키 콜랙션 표시
        }

        // Cookie type selector
        /*CookieTypeSelector(
            selectedType = selectedType,
            onTypeSelected = onTypeSelected
        )*/
    }
}

@Preview(
    name = "CollectionContent Preview",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun CollectionContentPreview() {
    CollectionScreenContent(
        items = listOf(
            CollectionDisplayItem(no = 1, isCollected = true, type = 1),
            CollectionDisplayItem(no = 2, isCollected = false, type = 1),
            CollectionDisplayItem(no = 3, isCollected = true, type = 1),
            CollectionDisplayItem(no = 4, isCollected = false, type = 1)
        ),
        isLoading = false,
        selectedType = CookieType.Cheering,
        showCollectedOnly = false,
        sortType = CollectionSortType.BY_NO,
        onSortTypeChange = {},
        onShowCollectedOnlyChange = {},
        onTypeSelected = {}
    )
}

@Composable
private fun CollectionTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.text_collection_sub_title),
            color = SubTitle,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.text_collection_title),
            color = MainTitle,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
private fun CollectionCheckBox(
    showCollectedOnly: Boolean,
    onShowCollectedOnlyChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.offset(y = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = showCollectedOnly,
            onCheckedChange = onShowCollectedOnlyChange,
            modifier = Modifier.scale(0.7f),
            colors = CheckboxDefaults.colors(
                checkedColor = MainBorder,
                uncheckedColor = MainBorder,
                checkmarkColor = White
            )
        )

        Text(
            text = stringResource(R.string.text_collection_check_text),
            modifier = Modifier.offset(x = (-10).dp),
            color = MainText,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.cookie_run_regular)),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CollectionCookieTypeDropDown(
    selectedType: CookieType?,
    onTypeSelected: (CookieType?) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 160.dp
) {

    val cookieTypeItems: List<CommonDropDownItem<CookieType?>> =
        listOf(
            CommonDropDownItem<CookieType?>(
                label = stringResource(R.string.all),
                value = null
            )
        ) + getCookieTypeList().map {
            CommonDropDownItem(
                label = stringResource(it.nameRes),
                value = it.type
            )
        }

    CommonDropDown(
        selectedValue = selectedType,
        items = cookieTypeItems,
        onItemSelected = onTypeSelected,
        modifier = modifier,
        width = width,
        verticalPadding = 6.dp,
        backgroundColor = DropDownBackground,
        borderColor = Color.Transparent,
        borderWidth = 0.dp,
        textColor = MainText,
        menuTextColor = MainText,
        horizontalPadding = 8.dp,
        iconColor = MainText,
        itemHeight = 36.dp,
        selectedLeadingContent = { cookieType ->
            when (cookieType) {
                null -> {
                    Image(
                        painter = painterResource(R.drawable.ic_drop_down_all),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = getCookieTypeColor(cookieType.type),
                                shape = CircleShape
                            )
                    )
                }
            }
        },
        itemLeadingContent = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = getCookieTypeColor(it.value?.type),
                        shape = CircleShape
                    )
            )
        },
        selectedTrailingContent = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MainText,
                modifier = Modifier.size(16.dp)
            )
        },
        itemBackgroundColor = DropDownBackground,
        itemSelectedBackgroundColor = DropDownSelectBackground,
        fontWeight = FontWeight.Medium

    )
}

@Composable
private fun CollectionSortDropDown(
    sortType: CollectionSortType,
    onSortTypeChange: (CollectionSortType) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 110.dp
) {

    val sortItems = CollectionSortType.entries.map {
        CommonDropDownItem(
            label = stringResource(it.nameRes),
            value = it
        )
    }

    CommonDropDown(
        selectedValue = sortType,
        items = sortItems,
        onItemSelected = onSortTypeChange,
        modifier = modifier,
        width = width,
        verticalPadding = 4.dp,
        backgroundColor = DropDownBackground,
        borderColor = Color.Transparent,
        borderWidth = 0.dp,
        textColor = MainText,
        menuTextColor = MainText,
        iconColor = MainText,
        horizontalPadding = 8.dp,
        itemHeight = 30.dp,
        selectedLeadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_sort),
                contentDescription = null,
                tint = MainText,
                modifier = Modifier.size(14.dp)
            )
        },
        selectedTrailingContent = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MainText,
                modifier = Modifier.size(16.dp)
            )
        },
        itemBackgroundColor = DropDownBackground,
        itemSelectedBackgroundColor = DropDownSelectBackground,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun TextCookieCount(size: Int) {
    Text(
        modifier = Modifier.padding(start = 28.dp),
        color = SubTitle,
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        text = "${stringResource(R.string.text_collection_all)} ${size}${stringResource(R.string.text_collection_cookie_unit)}"
    )
}

@Composable
private fun CollectionItemView(collectionItems: List<CollectionDisplayItem>) {
    val groupedItems =
        collectionItems
            .groupBy { it.type }
            .toSortedMap()
            .values
            .toList()

    LazyColumn( modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .offset(y = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)) {
        groupedItems.forEach { typeItems ->

            item {
                CookieTypeSubTitle(
                    type = typeItems.first().type,
                    items = typeItems
                )
            }

            items(typeItems.chunked(2)) { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            CollectionItemCard(item)
                        }
                    }

                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CookieTypeSubTitle(type: Int, items: List<CollectionDisplayItem>) {
    val openCount = items.count { it.isCollected }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = getCookieTypeColor(type),
                    shape = CircleShape
                )
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            color = MainText,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            text = stringResource(getCookieTypeMainTextRes(type))
        )

        Text(
            modifier = Modifier.padding(start = 6.dp),
            color = SubTitle,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
            fontWeight = FontWeight.Medium,
            lineHeight = 12.sp,
            text = "${openCount}/${items.size}"
        )
    }
}

@Composable
private fun CollectionItemCard(item: CollectionDisplayItem) {
    val imgRes = if (item.isCollected) {
        getCollectionTypeImages(item.type)
            .getOrElse(0) { R.drawable.img_cookie_deactive }
    } else {
        R.drawable.img_cookie_deactive
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (item.isCollected) ItemCardBackground else ItemCardUnOpenedBackground)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (item.isCollected) ItemCardBackground else ItemCardUnOpenedBorder
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp),
                textAlign = TextAlign.Start,
                color = SubTitle,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp,
                text = "${stringResource(R.string.no)}.${item.no}"
            )

            Spacer(Modifier.height(4.dp))

            Image(
                painter = painterResource(imgRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .alpha(if (item.isCollected) 1f else 0.45f)
            )

            Spacer(Modifier.height(4.dp))

            if (item.isCollected) {
                Text(
                    text = item.date?.toDisplayDate()?:"0000.00.00",
                    color = MainText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(0.45f)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = stringResource(R.string.text_collection_un_collected),
                        color = UnCollectedText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}