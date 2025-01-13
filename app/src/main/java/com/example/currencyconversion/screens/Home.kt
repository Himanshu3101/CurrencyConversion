package com.example.currencyconversion.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconversion.models.EndResult
import com.example.currencyconversion.viewModels.DataVModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun Home() {
    val viewModel: DataVModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getDBConversionCurrency()
    }

    val listOfCurrencyDb by viewModel.dBConversionCurrency.collectAsState()

    var selectedIndex by remember { mutableStateOf(0) }
    var inputAmt by remember { mutableStateOf("") }
    var selectedCurr by remember { mutableStateOf("") }

    val currencyList = listOf("Select Currency") + listOfCurrencyDb

    Log.d("HomeLog", "List _ $currencyList")

/*    @Inject
    @IoDispatcher
    lateinit var dispatcher: CoroutineDispatcher*/

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {

        Column {
            EditTextExample(
                listOfCurrency = currencyList,
                selectedIndex = selectedIndex,
                selectedCurr = selectedCurr,
                onIndexChanged = { selectedIndex = it },
                onCurrencyChanged = { selectedCurr = it },
                inputAmt = inputAmt,
                onTextChanged = { inputAmt = it }
            )
            if (selectedIndex != 0 && inputAmt.isNotEmpty()) {
                GridFrame(selectedCurr, inputAmt = inputAmt, currencyList/*, dispatcher*/)
            }
        }
    }
}

@Composable
fun EditTextExample(
    listOfCurrency: List<String?>,
    selectedIndex: Int,
    selectedCurr: String,
    onIndexChanged: (Int) -> Unit,
    onCurrencyChanged: (String) -> Unit,
    inputAmt: String,
    onTextChanged: (String) -> Unit
) {
    Log.d("HomeLog", "EditTextExample")
    var expanded by remember { mutableStateOf(false) }
    TextField(
        value = inputAmt,
        onValueChange = { newText ->
            onTextChanged(newText)
        },
        singleLine = true,
        placeholder = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Enter Amount",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(0.5.dp, Color.Gray),

        textStyle = TextStyle(
            textAlign = TextAlign.End,
            textDirection = TextDirection.ContentOrLtr
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.TopEnd
    ) {

        Column(horizontalAlignment = Alignment.End) {

            Button(onClick = { expanded = true }) {
                listOfCurrency[selectedIndex]?.let { Text(text = it) }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .padding(3.dp)
            ) {
                listOfCurrency.forEachIndexed { index, item ->
                    DropdownMenuItem(onClick = {
                        onIndexChanged(index)
                        onCurrencyChanged(listOfCurrency[index].toString())
                        expanded = false
                    }) {
                        if (item != null) {
                            Text(text = item)
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun GridFrame(
    selectedCurr: String,
    inputAmt: String,
    currencyList: List<String?>,
//    dispatcher: CoroutineDispatcher
) {

    val dataVModel: DataVModel = hiltViewModel()
    var baseRate by remember { mutableStateOf<Double?>(null) }
    var conversionResults by remember { mutableStateOf<List<EndResult>>(emptyList()) }

    LaunchedEffect(selectedCurr, inputAmt) {

        baseRate = dataVModel.getSelectedCurrencyRate(selectedCurr)
        if (baseRate == null) {
            Log.e(
                "currencyCalculation",
                "Base rate for $selectedCurr is null or invalid"
            )
        } else {
            val results = withContext(Dispatchers.IO) {
                currencyList.mapNotNull { currency ->
                    val targetRate = dataVModel.getSelectedCurrencyRate(currency)?.toDouble()

                    val result = targetRate?.let {
                        baseRate?.let { baseRate ->
                            convertCurrency(inputAmt.toDouble(), baseRate, it)
                        }
                    }
                    if (result != null) {
                        EndResult(result, currency)
                    } else {
                        null
                    }.also {
                        Log.d("HomeLog", "Result $result and Currency_$currency")
                    }
                }
            }
            conversionResults = results
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        items(conversionResults) { conversionResult ->
            DisplayGrid(conversionResult)
        }
    }
}


fun convertCurrency(amount: Double, sourceRate: Double, targetRate: Double): Double {
    return (amount / sourceRate) * targetRate
}

@Composable
fun DisplayGrid(conversionResult: EndResult) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        conversionResult.currency?.let { Text(text = it, style = MaterialTheme.typography.h6) }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = conversionResult.result.toString(), style = MaterialTheme.typography.body1)
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home(/*listOf("Select Currency","AAH","AED")*/)
}