package com.nuecoo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuecoo.R
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.mapper.toUiItem
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.TimerBackground
import com.nuecoo.viewmodel.OvenViewModel

@Composable
fun OvenScreen(viewModel: OvenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val dailyCookieData by viewModel.dailyCookieData.collectAsState()
    val remainTime by viewModel.remainTime.collectAsState()
    val selectedCookie by viewModel.selectedCookie.collectAsState()

    val cookieNameMap = remember {
        mapOf(
            CookieType.Cheering.type to context.resources.getStringArray(R.array.cookie_type_cheering).toList(),
            CookieType.Consolation.type to context.resources.getStringArray(R.array.cookie_type_consolation).toList(),
            CookieType.Passion.type to context.resources.getStringArray(R.array.cookie_type_passion).toList(),
            CookieType.Determination.type to context.resources.getStringArray(R.array.cookie_type_determination).toList(),
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initCookieData(cookieNameMap)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer
            Text(
                text = remainTime,
                color = MainBorder,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TimerBackground)
                    .padding(horizontal = 32.dp, vertical = 10.dp)
            )

            // Tray with cookies
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_oven_tray),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                )
                val cookieList = dailyCookieData?.list ?: emptyList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 60.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cookieList, key = { it.type }) { item ->
                        CookieItem(
                            data = item.toUiItem(),
                            onClick = {
                                if (item.isOpened == null) {
                                    // All collected toast
                                } else {
                                    viewModel.selectCookie(item.toUiItem())
                                }
                            }
                        )
                    }
                }
            }
        }

        // Cookie open overlay
        selectedCookie?.let { cookie ->
            CookieOpenScreen(
                cookieData = cookie,
                cookieMessages = cookieNameMap,
                onClose = { viewModel.clearSelectedCookie() },
                onCookieOpened = { type -> viewModel.updateOpenCookieData(type) }
            )
        }
    }
}

@Composable
fun CookieItem(data: CookieUIItemData, onClick: () -> Unit) {
    Image(
        painter = painterResource(data.imgRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
    )
}
