package com.example.appfirebasefirestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import com.example.appfirebasefirestore.ui.theme.AppFirebaseFirestoreTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfirebasefirestore.User
import com.example.appfirebasefirestore.UserFunctions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFirebaseFirestoreTheme {
                val act = this
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "register",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") {
                            HomeScreen(
                                Modifier,
                                act,
                                navController
                            )
                        }
                        composable("login") {
                            LoginScreen()
                        }
                        composable("register") {
                            RegisterScreen(
                                Modifier,
                                act,
                                navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController
) {
    val context = LocalContext.current

    Column(
        Modifier
            .background(Color(38, 38, 38))
            .fillMaxHeight()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Homepage",
                    color = Color(254, 102, 0),
                    fontSize = 30.sp
                )
            },
            actions = {
                IconButton(onClick = {/* todo */}) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Novo",
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
            },
            colors = TopAppBarColors(
                containerColor = Color(25, 25, 25),
                scrolledContainerColor = Color(25, 25, 25),
                navigationIconContentColor = Color(254, 102, 0),
                titleContentColor = Color(254, 102, 0),
                actionIconContentColor = Color(254, 102, 0)
            ),
            modifier = modifier
        )

        Row (
            modifier.padding(3.dp)
        ){

        }
        Column (
            Modifier
                .padding(24.dp)
        ){
            LazyColumn {
                // todo
            }
        }
    }
}

@Composable
fun LoginScreen() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController
) {
    var uid by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var telefone by remember {
        mutableStateOf("")
    }
    var mensagem by remember {
        mutableStateOf("")
    }
    var senha by remember {
        mutableStateOf("")
    }
    val user = User(
        uid,
        name,
        email,
        telefone,
        mensagem,
        senha
    )

    Column(
        Modifier
            .background(Color(38, 38, 38))
            .fillMaxHeight()
    ) {
        CenterAlignedTopAppBar(
            title = {Text(
                text = "Adicionar Livro",
                color = Color(254, 102, 0),
                fontSize = 30.sp
            ) },
            navigationIcon = {
                IconButton(onClick = {navHostController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            },
            colors = TopAppBarColors(
                containerColor = Color(25, 25, 25),
                scrolledContainerColor = Color(25, 25, 25),
                navigationIconContentColor = Color(254, 102, 0),
                titleContentColor = Color(254, 102, 0),
                actionIconContentColor = Color(254, 102, 0)
            ),
            modifier = modifier
        )

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color(254, 102, 0),
                    unfocusedLabelColor = Color.White,
                    unfocusedContainerColor = Color(86, 86, 86),
                    unfocusedTextColor = Color.White,

                    focusedIndicatorColor = Color(254, 102, 0),
                    focusedLabelColor = Color(254, 102, 0),
                    focusedContainerColor = Color(86, 86, 86),
                    focusedTextColor = Color.White,

                    cursorColor = Color(254, 102, 0)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color(254, 102, 0),
                    unfocusedLabelColor = Color.White,
                    unfocusedContainerColor = Color(86, 86, 86),
                    unfocusedTextColor = Color.White,

                    focusedIndicatorColor = Color(254, 102, 0),
                    focusedLabelColor = Color(254, 102, 0),
                    focusedContainerColor = Color(86, 86, 86),
                    focusedTextColor = Color.White,

                    cursorColor = Color(254, 102, 0)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            TextField(
                value = telefone,
                onValueChange = { telefone = it },
                label = { Text("Telefone") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color(254, 102, 0),
                    unfocusedLabelColor = Color.White,
                    unfocusedContainerColor = Color(86, 86, 86),
                    unfocusedTextColor = Color.White,

                    focusedIndicatorColor = Color(254, 102, 0),
                    focusedLabelColor = Color(254, 102, 0),
                    focusedContainerColor = Color(86, 86, 86),
                    focusedTextColor = Color.White,

                    cursorColor = Color(254, 102, 0)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            TextField(
                value = mensagem,
                onValueChange = { mensagem = it },
                label = { Text("Mensagem") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color(254, 102, 0),
                    unfocusedLabelColor = Color.White,
                    unfocusedContainerColor = Color(86, 86, 86),
                    unfocusedTextColor = Color.White,

                    focusedIndicatorColor = Color(254, 102, 0),
                    focusedLabelColor = Color(254, 102, 0),
                    focusedContainerColor = Color(86, 86, 86),
                    focusedTextColor = Color.White,

                    cursorColor = Color(254, 102, 0)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color(254, 102, 0),
                    unfocusedLabelColor = Color.White,
                    unfocusedContainerColor = Color(86, 86, 86),
                    unfocusedTextColor = Color.White,

                    focusedIndicatorColor = Color(254, 102, 0),
                    focusedLabelColor = Color(254, 102, 0),
                    focusedContainerColor = Color(86, 86, 86),
                    focusedTextColor = Color.White,

                    cursorColor = Color(254, 102, 0)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )
        }

        Row(
            Modifier
                .padding(15.dp)
        ) {

        }

        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center
        ) {
            val botaoSalvar = Button(
                onClick = {
                    // todo
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0)),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Registrar",
                    color = Color.White
                )
            }
        }
    }
}