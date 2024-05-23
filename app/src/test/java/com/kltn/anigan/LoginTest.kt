package com.kltn.anigan

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import com.kltn.anigan.api.LoginApi
import com.kltn.anigan.domain.DocsViewModel
import com.kltn.anigan.domain.request.LoginRequestBody
import com.kltn.anigan.domain.response.LoginResponse
import com.kltn.anigan.ui.login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import androidx.compose.runtime.mutableStateOf

@OptIn(ExperimentalCoroutinesApi::class)
class LoginTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var viewModel: DocsViewModel

    @Mock
    private lateinit var navController: NavController

    @Mock
    private lateinit var loginApi: LoginApi

    @Mock
    private lateinit var call: Call<LoginResponse>

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        Mockito.`when`(viewModel.username).thenReturn(mutableStateOf("test_user"))
    }

    @Test
    fun login_success() = runTest {
        val loginResponse =
            LoginResponse(access_token = "test_access_token", refresh_token = "test_refresh_token")
        val response = Response.success(loginResponse)

        Mockito.`when`(loginApi.login(Mockito.any(LoginRequestBody::class.java))).thenReturn(call)
        Mockito.doAnswer { invocation ->
            val callback: Callback<LoginResponse> = invocation.getArgument(0)
            callback.onResponse(call, response)
            null
        }.`when`(call).enqueue(Mockito.any())

        val latch = CountDownLatch(1)

        login(context, viewModel, "test_password", navController)

        advanceUntilIdle()

        Mockito.verify(context, Mockito.times(1)).let {
            Toast.makeText(it, "Successfully!", Toast.LENGTH_SHORT).show()
        }

        assert(viewModel.accessToken.value == "test_access_token")
        assert(viewModel.refreshToken.value == "test_refresh_token")

        latch.countDown()
        latch.await()
    }

    @Test
    fun login_failure() = runTest {
        val errorResponse = Response.error<LoginResponse>(
            400,
            ResponseBody.create(null, "{\"message\": \"Invalid credentials\"}")
        )

        Mockito.`when`(loginApi.login(Mockito.any(LoginRequestBody::class.java))).thenReturn(call)
        Mockito.doAnswer { invocation ->
            val callback: Callback<LoginResponse> = invocation.getArgument(0)
            callback.onResponse(call, errorResponse)
            null
        }.`when`(call).enqueue(Mockito.any())

        val latch = CountDownLatch(1)

        login(context, viewModel, "test_password", navController)

        advanceUntilIdle()

        Mockito.verify(context, Mockito.times(1)).let {
            Toast.makeText(it, "Error: Invalid credentials", Toast.LENGTH_LONG).show()
        }

        latch.countDown()
        latch.await()
    }

    @Test
    fun login_networkFailure() = runTest {
        Mockito.`when`(loginApi.login(Mockito.any(LoginRequestBody::class.java))).thenReturn(call)
        Mockito.doAnswer { invocation ->
            val callback: Callback<LoginResponse> = invocation.getArgument(0)
            callback.onFailure(call, Throwable("Network error"))
            null
        }.`when`(call).enqueue(Mockito.any())

        val latch = CountDownLatch(1)

        login(context, viewModel, "test_password", navController)

        advanceUntilIdle()

        Mockito.verify(context, Mockito.times(1)).let {
            Toast.makeText(it, "Fail by Network error!", Toast.LENGTH_LONG).show()
        }

        latch.countDown()
        latch.await()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
