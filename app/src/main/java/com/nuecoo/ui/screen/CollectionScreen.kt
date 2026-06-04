package com.nuecoo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.domain.model.CookieType
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.SubBackground
import com.nuecoo.viewmodel.CollectionSortType
import com.nuecoo.viewmodel.CollectionViewModel
import getCollectionTypeImages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(viewModel: CollectionViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val showCollectedOnly by viewModel.showCollectedOnly.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    var sortExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("번호순" to CollectionSortType.BY_NO, "날짜순" to CollectionSortType.BY_DATE)
    val selectedSortLabel = sortOptions.first { it.second == sortType }.first

    LaunchedEffect(Unit) { viewModel.loadCollection(CookieType.Cheering.type) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.width(120.dp)
            ) {
                OutlinedTextField(
                    value = selectedSortLabel,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sortExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MainBorder,
                        unfocusedTextColor = MainBorder,
                        focusedContainerColor = SubBackground,
                        unfocusedContainerColor = SubBackground,
                        focusedBorderColor = MainBorder,
                        unfocusedBorderColor = MainBorder
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MainBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .height(44.dp)
                )
                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    sortOptions.forEach { (label, sort) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label,
                                    color = MainBorder,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = { viewModel.setSortType(sort); sortExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showCollectedOnly,
                    onCheckedChange = { viewModel.setShowCollectedOnly(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MainBorder,
                        uncheckedColor = MainBorder,
                        checkmarkColor = MainButton
                    )
                )
                Text(
                    "수집한 쿠키만 보기ㅁ",
                    color = MainBorder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Collection grid
        if (isLoading) {
            Box(Modifier
                .weight(1f)
                .fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainBorder)
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MainBorder.copy(alpha = 0.08f))
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items) { item ->
                        CollectionItemCard(item = item, type = selectedType)
                    }
                }
            }
        }

        // Cookie type selector
        CookieTypeSelector(
            selectedType = selectedType,
            onTypeSelected = { viewModel.loadCollection(it) }
        )
    }
}

@Composable
private fun CollectionItemCard(item: CollectionDisplayItem, type: Int) {
    val imgRes = if (item.isCollected) {
        getCollectionTypeImages(type).getOrElse(5) { R.drawable.img_cookie_deactive }
    } else {
        R.drawable.img_cookie_deactive
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SubBackground)
            .border(2.dp, MainBorder, RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(imgRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "No.${item.no}",
                color = MainBorder,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CookieTypeSelector(selectedType: Int, onTypeSelected: (Int) -> Unit) {
    val types = listOf(
        CookieType.Cheering.type to R.drawable.img_cookie_cheering_1,
        CookieType.Comfort.type to R.drawable.img_cookie_comfort_1,
        CookieType.Passion.type to R.drawable.img_cookie_passion_1,
        CookieType.Sermon.type to R.drawable.img_cookie_sermon_1
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MainButton.copy(alpha = 0.3f))
            .border(width = 4.dp, color = MainBorder, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(types) { (type, imgRes) ->
                val isSelected = type == selectedType
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MainBorder else MainBackground)
                        .border(3.dp, MainBorder, CircleShape)
                        .clickable { onTypeSelected(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(imgRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
