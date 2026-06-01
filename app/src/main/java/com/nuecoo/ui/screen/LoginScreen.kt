package com.nuecoo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nuecoo.R
import com.nuecoo.domain.model.LoginResult
import com.nuecoo.ui.component.NueCooButton
import com.nuecoo.ui.component.NueCooTextField
import com.nuecoo.ui.navigation.Route
import com.nuecoo.ui.theme.MainBackground
import com.nuecoo.ui.theme.MainBorder
import com.nuecoo.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(MainBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(80.dp))

            NueCooTextField(
                value = emailInput,
                onValueChange = { emailInput = it; viewModel.setEmail(it) },
                hint = stringResource(R.string.hint_email)
            )
            Spacer(Modifier.height(12.dp))
            NueCooTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; viewModel.setPassword(it) },
                hint = stringResource(R.string.hint_pw),
                isPassword = true
            )
            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = MainBorder)
            } else {
                NueCooButton(
                    text = stringResource(R.string.btn_login),
                    onClick = {
                        scope.launch {
                            when (viewModel.login()) {
                                LoginResult.Success -> navController.navigate(Route.MAIN) {
                                    popUpTo(Route.LOGIN) { inclusive = true }
                                }
                                LoginResult.Empty -> snackbarHostState.showSnackbar("이메일과 비밀번호를 입력해주세요")
                                LoginResult.Failed -> snackbarHostState.showSnackbar("로그인에 실패했습니다")
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.btn_signup),
                color = MainBorder.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { navController.navigate(Route.SignUp.GRAPH) }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
