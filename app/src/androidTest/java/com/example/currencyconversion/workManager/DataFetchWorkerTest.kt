package com.example.currencyconversion.workManager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.currencyconversion.BuildConfig
import com.example.currencyconversion.models.ResponseExchangeList
import com.example.currencyconversion.network.database.CurrencyDataBase
import com.example.currencyconversion.network.server.API
import com.example.currencyconversion.network.server.NetworkResult
import com.example.currencyconversion.repository.ServerRepository
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import com.example.currencyconversion.utils.NetworkConnectivity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.time.delay

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.IOException
import java.time.Duration


@RunWith(AndroidJUnit4::class)
class DataFetchWorkerTest {

    private lateinit var context: Context

    @Mock
    lateinit var api: API

    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testDoWork_Success() = runBlocking {
        val request = OneTimeWorkRequestBuilder<DataFetchWorker>().build()
        workManager.enqueue(request).result.get()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        testDriver?.setAllConstraintsMet(request.id)


        var workInfo = workManager.getWorkInfoById(request.id).get()

        while (workInfo.state !in listOf(
                WorkInfo.State.SUCCEEDED,
                WorkInfo.State.FAILED,
                WorkInfo.State.CANCELLED
            )
        ) {
            delay(Duration.ofMillis(100))
            workInfo = workManager.getWorkInfoById(request.id)
                .get()
        }
        assertThat(workInfo.state).isEqualTo(WorkInfo.State.SUCCEEDED)
    }
}
