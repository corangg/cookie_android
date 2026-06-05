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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.core.navigation.Route
import com.nuecoo.domain.model.EmailCheckResult
import com.nuecoo.domain.model.PwCheckResult
import com.nuecoo.ui.component.NueCooButton
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.ui.theme.MainButton
import com.nuecoo.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpEmailScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    var emailInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val domains = listOf("gmail.com", "naver.com", "daum.net", "outlook.com", "kakao.com")
    var selectedDomain by remember { mutableStateOf(domains.first()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            SignUpTopBar(title = stringResource(R.string.label_signup)) {
                navController.popBackStack()
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.text_input_email),
                color = MainBorder, fontSize = 20.sp
            )
            Spacer(Modifier.height(16.dp))
            EmailInputRow(
                emailInput = emailInput,
                onEmailChange = { emailInput = it; viewModel.setEmail(it) },
                selectedDomain = selectedDomain,
                domains = domains,
                onDomainChange = { selectedDomain = it; viewModel.setDomain(it) }
            )
            Spacer(Modifier.weight(1f))
            if (isLoading) {
                CircularProgressIndicator(
                    color = MainBorder,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                NueCooButton(
                    text = stringResource(R.string.btn_ok),
                    enabled = isEmailValid,
                    onClick = {
                        scope.launch {
                            when (viewModel.checkEmailExists()) {
                                EmailCheckResult.Available -> navController.navigate(Route.SignUp.PW)
                                EmailCheckResult.Duplicated -> snackbarHostState.showSnackbar("이미 사용 중인 이메일입니다")
                                EmailCheckResult.Error -> snackbarHostState.showSnackbar("오류가 발생했습니다")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailInputRow(
    emailInput: String,
    onEmailChange: (String) -> Unit,
    selectedDomain: String,
    domains: List<String>,
    onDomainChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = emailInput,
            onValueChange = onEmailChange,
            placeholder = { Text("이메일", color = MainBorder.copy(0.5f)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(4f).fillMaxWidth()
        )
        Text("@", color = MainBorder, fontSize = 16.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(4f)
        ) {
            OutlinedTextField(
                value = selectedDomain,
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
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                domains.forEach { domain ->
                    DropdownMenuItem(
                        text = { Text(domain) },
                        onClick = { onDomainChange(domain); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpPwScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isPwValid by viewModel.isPwValid.collectAsState()
    val isPwCheckEnabled by viewModel.isPwCheckEnabled.collectAsState()
    var pwInput by remember { mutableStateOf("") }
    var pwCheckInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SignUpTopBar(title = stringResource(R.string.label_signup)) { navController.popBackStack() }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.text_input_pw), color = MainBorder, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pwInput,
                onValueChange = { pwInput = it; viewModel.setPw(it) },
                placeholder = { Text("비밀번호 (8-20자, 영문+숫자+특수문자)", color = MainBorder.copy(0.5f)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                    focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                    focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            if (!isPwValid && pwInput.isNotEmpty()) {
                Text(
                    stringResource(R.string.text_valid_pw),
                    color = Color.Red, fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = pwCheckInput,
                onValueChange = { pwCheckInput = it; viewModel.setPwCheck(it) },
                placeholder = { Text("비밀번호 확인", color = MainBorder.copy(0.5f)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                    focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                    focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            Spacer(Modifier.weight(1f))
            NueCooButton(
                text = stringResource(R.string.btn_ok),
                enabled = isPwCheckEnabled,
                onClick = {
                    when (viewModel.checkPw()) {
                        PwCheckResult.Accordance -> navController.navigate(Route.SignUp.PHONE)
                        PwCheckResult.NotAccordance -> scope.launch {
                            snackbarHostState.showSnackbar("비밀번호가 일치하지 않습니다")
                        }
                        PwCheckResult.NotValid -> scope.launch {
                            snackbarHostState.showSnackbar("비밀번호 형식을 확인해주세요")
                        }
                    }
                }
            )
            Spacer(Modifier.height(20.dp))
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SignUpPhoneScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isCodeSent by viewModel.isCodeSent.collectAsState()
    val isPhoneOkEnabled by viewModel.isPhoneOkEnabled.collectAsState()
    var phoneInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            SignUpTopBar(title = stringResource(R.string.label_signup)) { navController.popBackStack() }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.text_input_phone), color = MainBorder, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it; viewModel.setPhoneNumber(it) },
                placeholder = { Text("01000000000", color = MainBorder.copy(0.5f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                    focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                    focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it; viewModel.setVerificationCode(it) },
                    placeholder = { Text("인증번호 6자리", color = MainBorder.copy(0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MainBorder, unfocusedTextColor = MainBorder,
                        focusedContainerColor = MainBackground, unfocusedContainerColor = MainBackground,
                        focusedBorderColor = MainBorder, unfocusedBorderColor = MainBorder.copy(0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(7f).height(56.dp)
                )
                Button(
                    onClick = {
                        scope.launch {
                            val isResend = isCodeSent
                            val ok = viewModel.sendVerificationCode(isResend)
                            snackbarHostState.showSnackbar(
                                if (ok) if (isResend) "인증번호를 재전송했습니다" else "인증번호를 전송했습니다"
                                else "전송 실패"
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButton, contentColor = MainBorder
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(3f).height(56.dp)
                        .border(2.dp, MainBorder, RoundedCornerShape(8.dp))
                ) {
                    Text(if (isCodeSent) "재전송" else "인증", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (isCodeSent) {
                Text(
                    "인증번호가 전송되었습니다",
                    color = MainBorder, fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            NueCooButton(
                text = stringResource(R.string.btn_ok),
                enabled = isPhoneOkEnabled,
                onClick = {
                    scope.launch {
                        val ok = viewModel.verifySmsCode()
                        if (ok) {
                            navController.navigate(Route.SignUp.BIRTH)
                        } else {
                            snackbarHostState.showSnackbar("인증에 실패했습니다")
                        }
                    }
                }
            )
            Spacer(Modifier.height(20.dp))
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

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
            SignUpTopBar(title = stringResource(R.string.label_signup)) { navController.popBackStack() }
            Spacer(Modifier.height(16.dp))
            Text("성별", color = MainBorder, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderButton(text = "남성", isSelected = isGender, onClick = { viewModel.setGender(true) }, modifier = Modifier.weight(1f))
                GenderButton(text = "여성", isSelected = !isGender, onClick = { viewModel.setGender(false) }, modifier = Modifier.weight(1f))
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
                CircularProgressIndicator(color = MainBorder, modifier = Modifier.align(Alignment.CenterHorizontally))
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
                                navController.navigate(Route.LOGIN) {
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
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
        PickerDropdown("년도", years.map { "${it}년" }, years.indexOf(year), { onYearChange(years[it]) }, Modifier.weight(3f))
        PickerDropdown("월", months.map { "${it}월" }, months.indexOf(month), { onMonthChange(months[it]) }, Modifier.weight(2f))
        PickerDropdown("일", days.map { "${it}일" }, days.indexOf(day), { onDayChange(days[it]) }, Modifier.weight(2f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerDropdown(
    label: String, items: List<String>, selectedIndex: Int,
    onSelect: (Int) -> Unit, modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
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
            modifier = Modifier.menuAnchor().fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
