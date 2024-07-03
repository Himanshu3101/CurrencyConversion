package com.example.currencyconversion.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.currencyconversion.viewModels.DataVModel


@Composable
fun Home() {
    val viewModel: DataVModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getDBConversionCurrency()
    }

    val listOfCurrencyDb by viewModel.dBConversionCurrency.collectAsState()

    var selectedIndex by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }

    val currencyList = listOf("Select Currency") + listOfCurrencyDb

    Log.d("HomeLog", "List _ $currencyList")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {

        Column {
            EditTextExample(
                listOfCurrency = currencyList,
                selectedIndex = selectedIndex,
                onIndexChanged = { selectedIndex = it },
                text = text,
                onTextChanged = { text = it }
            )
            if (selectedIndex != 0) {
                GridFrame(selectedIndex = selectedIndex, text = text)
            }
        }
    }
}

@Composable
fun EditTextExample(
    listOfCurrency: List<String?>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit,
    text: String,
    onTextChanged: (String) -> Unit)
{
    Log.d("HomeLog", "EditTextExample")
    var expanded by remember { mutableStateOf(false) }
    TextField(
        value = text,
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
fun GridFrame(selectedIndex: Int, text: String) {

    val dataVModel: DataVModel = hiltViewModel()
    val currencyList = dataVModel.dBConversionCurrency.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        items(currencyList.value){
//            displayGrid(it)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home(/*listOf("Select Currency","AAH","AED")*/)
}