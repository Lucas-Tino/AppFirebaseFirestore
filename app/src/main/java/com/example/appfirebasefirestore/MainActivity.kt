package com.example.appfirebasefirestore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import com.example.appfirebasefirestore.ui.theme.AppFirebaseFirestoreTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.appfirebasefirestore.UserFunctions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFirebaseFirestoreTheme {
                val act = this
                val navController = rememberNavController()
                val authViewModel : AuthViewModel by viewModels()

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
                                navController,
                                authViewModel
                            )
                        }
                        composable("login") {
                            LoginScreen(Modifier,
                                act,
                                navController,
                                authViewModel)
                        }
                        composable("register") {
                            RegisterScreen(
                                Modifier,
                                act,
                                navController,
                                authViewModel
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
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
    val userFunctions = UserFunctions();

    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navHostController.navigate("login")
            else -> Unit
        }
    }

    val uid = FirebaseAuth.getInstance().currentUser?.uid;
    var userData by remember(uid) {
        mutableStateOf(User())
    }

    LaunchedEffect(uid) {
        if (uid != null) {
            val userRef = db.collection("users").document(uid)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject<User>()
                user?.let {
                    userData = it
                }
            }
        }
    }

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
            Text(text = "Seja bem vindo, " +userData.name, color = Color(254, 102, 0))
            TextButton(onClick = {
                authViewModel.signout()
            }) {
                Text(text = "Sair da conta", color = Color(254, 102, 0))
            }
            LazyColumn {
                // todo
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember {
        mutableStateOf("")
    }
    var senha by remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navHostController.navigate("home")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(40, 40, 40)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Página de Login", fontSize = 32.sp, color = Color(254, 102, 0))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
            },
            label = {
                Text(text = "Password")
            },

            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.login(email, senha)
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0)),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Login", color = Color.White)
        }


        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navHostController.navigate("signup")
        }) {
            Text(text = "Não possui uma conta? Registre-se", color = Color(254, 102, 0))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
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

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                val user = hashMapOf(
                    "nome" to name,
                    "email" to email,
                    "telefone" to telefone,
                    "mensagem" to mensagem,
                    "senha" to senha
                )

                FirebaseAuth.getInstance().currentUser?.let { UserFunctions().addUser(user, it.uid) }
                navHostController.navigate("home")
            }
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(40, 40, 40)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Página de Registro", fontSize = 32.sp, color = Color(254, 102, 0))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text(text = "Nome")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefone,
            onValueChange = {
                telefone = it
            },
            label = {
                Text(text = "Telefone")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mensagem,
            onValueChange = {
                mensagem = it
            },
            label = {
                Text(text = "Mensagem")
            },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = {
                senha = it
            },
            label = {
                Text(text = "Senha")
            },

            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(254, 102, 0),
                unfocusedLabelColor = Color(254, 102, 0),
                unfocusedContainerColor = Color(40, 40, 40),
                unfocusedTextColor = Color.White,

                focusedIndicatorColor = Color(254, 102, 0),
                focusedLabelColor = Color(254, 102, 0),
                focusedContainerColor = Color(40, 40, 40),
                focusedTextColor = Color.White,

                cursorColor = Color(254, 102, 0)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.signup(email, senha)

            val user = hashMapOf(
                "nome" to name,
                "email" to email,
                "telefone" to telefone,
                "mensagem" to mensagem,
                "senha" to senha
            )

            FirebaseAuth.getInstance().currentUser?.let { UserFunctions().addUser(user, it.uid) }
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0)),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Criar conta", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navHostController.navigate("login")
        }) {
            Text(text = "Já possui uma conta? Faça Login", color = Color(254, 102, 0))
        }
    }
}