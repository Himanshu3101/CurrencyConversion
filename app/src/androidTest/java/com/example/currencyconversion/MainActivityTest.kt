package com.example.currencyconversion

import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyconversion.adapter.ConversionListAdapter
import com.example.currencyconversion.di.IoDispatcher
import com.example.currencyconversion.models.EndResult
import com.example.currencyconversion.utils.AnimationUtils
import com.example.currencyconversion.viewModels.DataVModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Mock
    private lateinit var viewModel: DataVModel

    @Inject
    @IoDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)

        viewModel = Mockito.mock(DataVModel::class.java)

        runBlocking {
            Mockito.`when`(viewModel.getDBConversionCurrency()).thenReturn(Unit)
            Mockito.`when`(viewModel.getSelectedCurrencyRate(anyString())).thenReturn(1.0)
        }
        AnimationUtils.disableAnimations()
    }

    @Test
    fun testEditTextAmount() {
        onView(withId(R.id.editTextAmount)).perform(typeText("100"))
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.editTextAmount)).check(matches(withText("100")))
    }

    @Test
    fun testCurrencySelection() {
        val scenario = activityRule.scenario
        scenario.onActivity { activity ->

            val spinner = activity.findViewById<Spinner>(R.id.spinnerCurrency)
            val adapter = ArrayAdapter(
                activity,
                android.R.layout.simple_spinner_item,
                listOf("Select Currency", "USD", "EUR", "GBP")
            )
            spinner.adapter = adapter
        }

        onView(withId(R.id.spinnerCurrency)).perform(click())
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.spinnerCurrency)).check(matches(withSpinnerText(containsString("USD"))))
    }

    @Test
    fun testSpinnerDisplaysDefaultData() {
        val scenario = activityRule.scenario
        scenario.onActivity { activity ->

            val spinner = activity.findViewById<Spinner>(R.id.spinnerCurrency)
            val adapter = ArrayAdapter(
                activity,
                android.R.layout.simple_spinner_item,
                listOf("Select Currency", "USD", "EUR", "GBP")
            )
            spinner.adapter = adapter
        }

        onView(withId(R.id.spinnerCurrency)).perform(click())
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.spinnerCurrency)).check(matches(withSpinnerText(containsString("USD"))))

    }


    @Test
    fun testRecyclerViewDisplaysItemsCorrectly() {
        val resultList = listOf(
            EndResult(100.0, "USD"),
            EndResult(90.0, "EUR")
        )
        val adapter = ConversionListAdapter(resultList)
        activityRule.scenario.onActivity { activity ->
            val recyclerView: RecyclerView = activity.findViewById(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText("USD"))))
            .check(matches(hasDescendant(withText("100.0"))))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        AnimationUtils.enableAnimations()
    }
}





