package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.myapplication.model.Operation
import com.example.myapplication.model.Valute
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.room.OperationDao
import com.example.myapplication.room.OperationDatabase
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.OperationViewModel
import com.example.myapplication.viewmodel.Repository
import com.example.myapplication.viewmodel.ValutesViewModel
import java.io.File

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            OperationDatabase::class.java,
            "operations.db"
        ).build()
    }

    private val viewModel by viewModels<OperationViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OperationViewModel(Repository(database)) as T
                }
            }
        }
    )

    private val valutesViewModel: ValutesViewModel by viewModels()

    private var selOperation:Operation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main_page"){
                    composable("main_page"){
                        FinancesApp(navController)
                    }
                    composable("add_operation_page"){
                        AddOperationPage(navController)
                    }
                    composable("edit_operation_page",
                    ) {
                        EditOperationPage(navController = navController)
                    }
                }
                }
            }
    }


    @Composable
    fun FinancesApp(navController: NavHostController){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,

            ) {
            Scaffold(topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Financial app", style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

            },
                floatingActionButton = {
                    FloatingActionButton(onClick = { navController.navigate("add_operation_page") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }


            ) { paddingValues ->

                Column(modifier = Modifier.padding(paddingValues)) {
                    Spacer(modifier = Modifier.height(20.dp))
                    PeriodSelectionMenu(mutableStateOf("last week"))
                    Spacer(modifier = Modifier.height(20.dp))
                    // TotalExpensesMenu(selectedPeriod = mutableStateOf("last week"))
                    IncomeExpensePanels()
                    Spacer(Modifier.height(10.dp))
                    ValutesPanel()
                    Spacer(Modifier.height(10.dp))
                    TransactionsList(navController)
                }
            }
        }
    }

    @Composable
    fun ValutesPanel(){
        // val valutesDatabase by valutesViewModel.valutes.observeAsState()

        val valutesDatabase by valutesViewModel.valutes
        if (valutesDatabase != null) {
            val valutes = valutesDatabase?.valute ?: emptyMap()
            Text(
                text = String.format(
                    "USD %.2f • EUR %.2f • GBP %.2f",
                    valutes["USD"]!!.value,
                    valutes["EUR"]!!.value,
                    valutes["GBP"]!!.value
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        } else {
            Text(
                text = "Couldn't load currencies",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                color = Color.Red
            )
        }
    }


    @Composable
    fun PeriodSelectionMenu(selectedPeriod: MutableState<String>){
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("last week", "last month", "all time").forEach { period ->
                Button(
                    onClick = { selectedPeriod.value = period
                              viewModel.selectTimePeriod(period)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (period == selectedPeriod.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(period)
                }
            }
        }
    }

    @Composable
    fun IncomeExpensePanels() {
        var income by remember{
            mutableStateOf(0)
        }
        viewModel.getIncome().observe(this){
            if (it != null) {
                income = it
            }
        }

        var expenses by remember{
            mutableStateOf(0)
        }
        viewModel.getExpenses().observe(this){
            if (it != null) {
                expenses = -it
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Income Panel
            Panel(title = "Income", amount = income, color = Color.Green)
            Spacer(modifier = Modifier.width(10.dp))
            // Expenses Panel
            Panel(title = "Expenses", amount = expenses, color = Color.Red)
        }
    }

    @Composable
    fun RowScope.Panel(title: String, amount: Int, color: Color) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(0.dp)
                .background(colorResource(id = R.color.panel))
                .clip(RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = amount.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
        }
    }

    @Composable
    fun TransactionsList(navController: NavHostController){
        var operationList by remember {
            mutableStateOf(listOf<Operation>())
        }

        viewModel.getOperations().observe(this) {
            operationList = it
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(operationList) { operation ->
                OperationItem(
                    operation, viewModel, navController
                )
            }
        }
    }

    @Composable
    fun AddOperationPage(navController: NavHostController) {
        var transactionType by remember { mutableStateOf("Income") }
        var title by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Transaction Type Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { transactionType = "Income" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Income")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = { transactionType = "Expense" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "Expense") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Expense")
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.weight(1f))

            // Add Button
            Button(onClick = {
                val amountInt = if (transactionType == "Income") amount.toInt() else -amount.toInt()
                viewModel.upsert(Operation(title, amountInt))
                navController.navigate("main_page")
                             },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Add", color = Color.White)
            }
        }
    }
    
    @Composable
    fun EditOperationPage(navController: NavHostController){
        var transactionType by remember { mutableStateOf(if (selOperation!!.sum >= 0)  "Income" else "Expense") }
        var title by remember { mutableStateOf(selOperation!!.title) }
        var amount by remember { mutableStateOf(if (selOperation!!.sum >= 0) selOperation!!.sum.toString() else selOperation!!.sum.toString()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Transaction Type Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { transactionType = "Income" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Income")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = { transactionType = "Expense" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (transactionType == "Expense") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Expense")
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.weight(1f))

            // Add Button
            Button(onClick = {
                val amountInt = if (transactionType == "Income") amount.toInt() else -amount.toInt()
                viewModel.upsert(Operation(title, amountInt, date = selOperation!!.date, id = selOperation!!.id))
                navController.navigate("main_page")
            },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "Update", color = Color.White)
            }

            Button(onClick = {
                viewModel.delete(selOperation!!)
                navController.navigate("main_page")
            },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.Transparent)
            ) {
                Text(text = "Delete", color = Color.Red)
            }
        }
    }

    @Composable
    fun OperationItem(
        operation: Operation,
        viewModel: OperationViewModel,
        navController: NavHostController
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
                .clickable {
                    selOperation = operation
                    navController.navigate("edit_operation_page")
                }
        ) {
            Box(
                Modifier
                    .background(
                        if (operation.sum > 0) Color.Green else Color.Red
                    )
                    .size(35.dp)){
                Text(text = if (operation.sum > 0) "+" else "-",
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center)
            }
            Text(
                text = operation.title, style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 25.dp, end = 25.dp)
                    .weight(1f),
                textAlign = TextAlign.Left
            )
            Text (
                text = if (operation.sum > 0) operation.sum.toString() else operation.sum.toString(),
                color = if (operation.sum > 0) Color.Green else Color.Red,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 25.dp, end = 25.dp)
                    .width(100.dp),
                textAlign = TextAlign.Right
            )
        }
    }
}


