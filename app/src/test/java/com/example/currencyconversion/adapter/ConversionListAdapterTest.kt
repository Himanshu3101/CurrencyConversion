package com.example.currencyconversion.adapter

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconversion.models.EndResult
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = TestCurrencyConversion::class)
@RunWith(RobolectricTestRunner::class)
class ConversionListAdapterTest{

    @Test
    fun getItemCount_returns_correct_size() {
        val resultList = listOf(
            EndResult(100.0, "USD"),
            EndResult(90.0, "EUR")
        )
        val adapter = ConversionListAdapter(resultList)
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun binds_data_correctly_to_ViewHolder () {
        val resultList = listOf(EndResult(100.0, "USD"))
        val adapter = ConversionListAdapter(resultList)
        val shadowActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        val parent = FrameLayout(shadowActivity)
        val holder = adapter.onCreateViewHolder(parent, 0)

        adapter.onBindViewHolder(holder, 0)

        assertEquals("USD", holder.currencyTextView.text.toString())
        assertEquals("100.0", holder.resultTextView.text.toString())
    }

}