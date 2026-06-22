package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.ui.component.BackButton
import com.nuecoo.core.ui.component.DefaultItemBox
import com.nuecoo.feature.main.presentation.main.component.MainTitleItem
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MainText
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun AppRateScreen(onBack: () -> Unit, onMoveStore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(modifier = Modifier, onClick = onBack)
            MainTitleItem(
                modifier = Modifier.padding(start = 8.dp),
                subTitle = stringResource(R.string.text_app_rate_sub_title),
                mainTitle = stringResource(R.string.text_app_info_rate_title)
            )
        }

        DefaultItemBox(modifier = Modifier.padding(top = 24.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.text_app_rate_desc),
                    color = SubText,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MainButton)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    text = stringResource(R.string.text_app_rate_btn),
                    color = White,
                    fontFamily = FontFamily(Font(R.font.title_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
