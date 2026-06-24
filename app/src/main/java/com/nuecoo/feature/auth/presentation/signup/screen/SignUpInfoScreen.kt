package com.nuecoo.feature.auth.presentation.signup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.core.ui.component.DefaultAuthButton
import com.nuecoo.core.ui.component.LoadingOverlay
import com.nuecoo.feature.auth.presentation.component.AuthScreenWrapper
import com.nuecoo.feature.auth.presentation.signup.component.BirthDateSpinnerItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpMainTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpRateItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpSubTextItem
import com.nuecoo.feature.auth.presentation.signup.component.SignUpTopItem
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.ui.theme.MenuSubBoxBackground
import com.nuecoo.ui.theme.SubText
import com.nuecoo.ui.theme.White

@Composable
fun SignUpInfoScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val step by viewModel.signUpStep.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val year by viewModel.year.collectAsStateWithLifecycle()
    val month by viewModel.month.collectAsStateWithLifecycle()
    val day by viewModel.day.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateSignUpStep(5)
    }

    SignUpInfoScreenContent(
        isLoading = isLoading,
        step = step,
        year = year,
        month = month,
        day = day,
        gender = gender,
        onGenderChanged = viewModel::setGender,
        onYearChanged = viewModel::setYear,
        onMonthChanged = viewModel::setMonth,
        onDayChanged = viewModel::setDay,
        onBack = { navController.popBackStack() },
        onNext = { navController.navigate(Route.SignUp.COMPLETE) },
        onCancelLoading = viewModel::cancelCurrentWork
    )
}

@Composable
private fun SignUpInfoScreenContent(
    isLoading: Boolean,
    step: Int,
    year: Int,
    month: Int,
    day: Int,
    gender: Boolean?,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    onGenderChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCancelLoading: () -> Unit
) {
    AuthScreenWrapper {
        val bottomPadding = imeBottomPadding

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp)
        ) {
            SignUpTopItem(modifier = Modifier.padding(top = 16.dp), onBack = onBack)//상단 타이틀
            SignUpRateItem(modifier = Modifier.padding(top = 20.dp), step = step)//진행 단계

            SignUpMainTextItem(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_info_main)
            )//메인 텍스트

            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_info_gender)
            )//성별 텍스트

            SelectGenderItem(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 6.dp),
                onSelect = onGenderChanged,
                gender = gender
            )

            SignUpSubTextItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(start = 10.dp),
                text = stringResource(R.string.signup_info_birth)
            )//생년월일 텍스트

            BirthDateSpinnerItem(
                year = year,
                month = month,
                day = day,
                onYearChanged = onYearChanged,
                onMonthChanged = onMonthChanged,
                onDayChanged = onDayChanged,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(Modifier.weight(1f))

            DefaultAuthButton(
                modifier = Modifier.padding(bottom = bottomPadding),
                title = stringResource(R.string.signup),
                background = MainButton,
                titleColor = White,
                enabled = gender != null,
                onClick = onNext
            )//하단 버튼
        }
        LoadingOverlay(isLoading = isLoading, onCancel = onCancelLoading)
    }
}

@Composable
private fun SelectGenderItem(modifier: Modifier, onSelect: (Boolean) -> Unit, gender: Boolean?) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        GenderBox(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            onClick = { onSelect(true) },
            isSelected = gender == true,
            text = stringResource(R.string.signup_info_male)
        )

        Spacer(modifier = Modifier.size(20.dp))

        GenderBox(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp),
            isSelected = gender == false,
            text = stringResource(R.string.signup_info_female),
            onClick = { onSelect(false) }
        )
    }
}

@Composable
private fun GenderBox(modifier: Modifier, isSelected: Boolean?, text: String, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected == true) MainButton else MenuSubBoxBackground)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected == true) White else SubText,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.title_font)),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            letterSpacing = 2.sp
        )
    }
}