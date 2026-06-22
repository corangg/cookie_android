package com.nuecoo.feature.main.presentation.menu.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuecoo.R
import com.nuecoo.core.ui.component.BackButton
import com.nuecoo.core.ui.component.DefaultItemBox
import com.nuecoo.feature.main.presentation.main.component.MainTitleItem
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.SubText

@Composable
fun AppPrivacyScreen(onBack: () -> Unit) {
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
                subTitle = stringResource(R.string.text_app_privacy_sub_title),
                mainTitle = stringResource(R.string.text_app_info_privacy_title)
            )
        }

        DefaultItemBox(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)) {
            Text(
                text = stringResource(R.string.text_app_privacy_content),
                color = SubText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
