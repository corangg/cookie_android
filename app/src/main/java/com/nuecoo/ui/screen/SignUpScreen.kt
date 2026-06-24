package com.nuecoo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.ui.component.NueCooButton
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.feature.auth.presentation.signup.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch





@Composable
fun SignUpBirthScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isGender by viewModel.isGender.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedYear by remember { mutableStateOf(1995) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedDay by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            SignUpTopBar(title = stringResource(R.string.signup_main_title)) { navController.popBackStack() }
            Spacer(Modifier.height(16.dp))
            Text("성별", color = MainBorder, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderButton(
                    text = "남성",
                    isSelected = isGender,
                    onClick = { viewModel.setGender(true) },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    text = "여성",
                    isSelected = !isGender,
                    onClick = { viewModel.setGender(false) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(32.dp))
            Text("생년월일", color = MainBorder, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            BirthPicker(
                year = selectedYear,
                month = selectedMonth,
                day = selectedDay,
                onYearChange = { selectedYear = it },
                onMonthChange = { selectedMonth = it },
                onDayChange = { selectedDay = it }
            )
            Spacer(Modifier.weight(1f))
            if (isLoading) {
                CircularProgressIndicator(
                    color = MainBorder,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                NueCooButton(
                    text = "가입 완료",
                    onClick = {
                        val birth = "%04d%02d%02d".format(selectedYear, selectedMonth, selectedDay)
                        viewModel.setBirthDate(birth)
                        scope.launch {
                            val ok = viewModel.trySignUp()
                            if (ok) {
                                snackbarHostState.showSnackbar("회원가입이 완료되었습니다")
                                navController.navigate(Route.Login) {
                                    popUpTo(Route.SignUp.GRAPH) { inclusive = true }
                                }
                            } else {
                                snackbarHostState.showSnackbar("회원가입에 실패했습니다")
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun GenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MainButton else Color.Gray.copy(0.4f),
            contentColor = MainBorder
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.border(3.dp, MainBorder, RoundedCornerShape(8.dp))
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = MainBorder)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthPicker(
    year: Int, month: Int, day: Int,
    onYearChange: (Int) -> Unit, onMonthChange: (Int) -> Unit, onDayChange: (Int) -> Unit
) {
    val years = (1950..2010).toList()
    val months = (1..12).toList()
    val days = (1..31).toList()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PickerDropdown(
            "년도",
            years.map { "${it}년" },
            years.indexOf(year),
            { onYearChange(years[it]) },
            Modifier.weight(3f)
        )
        PickerDropdown(
            "월",
            months.map { "${it}월" },
            months.indexOf(month),
            { onMonthChange(months[it]) },
            Modifier.weight(2f)
        )
        PickerDropdown(
            "일",
            days.map { "${it}일" },
            days.indexOf(day),
            { onDayChange(days[it]) },
            Modifier.weight(2f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerDropdown(
    label: String, items: List<String>, selectedIndex: Int,
    onSelect: (Int) -> Unit, modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (selectedIndex >= 0) items[selectedIndex] else label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEachIndexed { i, item ->
                DropdownMenuItem(text = { Text(item) }, onClick = { onSelect(i); expanded = false })
            }
        }
    }
}

@Composable
fun SignUpTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.IconButton(onClick = onBack) {
            Text("←", color = MainBorder, fontSize = 24.sp)
        }
        Text(
            title,
            color = MainBorder,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
