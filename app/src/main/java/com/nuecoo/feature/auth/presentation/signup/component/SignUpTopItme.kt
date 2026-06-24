package com.nuecoo.feature.auth.presentation.signup.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuecoo.R
import com.nuecoo.core.ui.component.BackButton
import com.nuecoo.core.ui.component.MainTitleItem

@Composable
fun SignUpTopItem(modifier: Modifier, onBack: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier, onClick = onBack)

        MainTitleItem(
            modifier = Modifier.padding(start = 8.dp),
            subTitle = stringResource(R.string.signup_sub_title),
            mainTitle = stringResource(R.string.signup_main_title)
        )
    }
}