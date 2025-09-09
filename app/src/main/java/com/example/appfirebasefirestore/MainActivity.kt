package com.example.appfirebasefirestore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.example.appfirebasefirestore.ui.theme.AppFirebaseFirestoreTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues),
                        enterTransition = {
                            EnterTransition.None
                        },
                        exitTransition = {
                            ExitTransition.None
                        }
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
                        composable("updateown") {
                            UpdateOwnAccountScreen(
                                Modifier,
                                act,
                                navController,
                                authViewModel
                            )
                        }
                        composable("create") {
                            CreateUpdateScreen(
                                Modifier,
                                act,
                                navController,
                                authViewModel,
                                "create",
                                "a"
                            )
                        }
                        composable("update") {
                            CreateUpdateScreen(
                                Modifier,
                                act,
                                navController,
                                authViewModel,
                                "update",
                                "a"
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

    var userData: User by remember {
        mutableStateOf(User())
    }

    // verificação se o usuário está logado
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navHostController.navigate("login")
            else -> Unit
        }
    }

    val firebaseUser = FirebaseAuth.getInstance().currentUser;

    var userList by remember { mutableStateOf<List<User>>(emptyList()) }

    // listar usuários
    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userList = documents.map { document ->
                    User(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        email = document.getString("email") ?: "",
                        telefone = document.getString("telefone") ?: "",
                        mensagem = document.getString("mensagem") ?: "",
                        senha = document.getString("senha") ?: ""
                    )
                } as MutableList<User>
            }
    }

    var dropdown by remember { mutableStateOf(false) }
    var deleteOwnAccountDialog by remember { mutableStateOf(false) }

    if (deleteOwnAccountDialog == true) {
        AlertDialog(
            title = {
                Text(text = "Deseja mesmo excluir sua própria conta?", color = Color(254, 102, 0))
            },
            text = {
                Text(text = "Esta ação é irreversível.", color = Color(254, 102, 0))
            },
            onDismissRequest = {
                deleteOwnAccountDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.delete()
                    }
                ) {
                    Text("Sim", color = Color(254, 102, 0))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteOwnAccountDialog = false
                    }
                ) {
                    Text("Não", color = Color(254, 102, 0))
                }
            },
            containerColor = Color(20, 20, 20)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(40, 40, 40)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Seja bem vindo, administrador.",
                    color = Color(254, 102, 0),
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = {dropdown = true}) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Novo",
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
                DropdownMenu(
                    expanded = dropdown,
                    onDismissRequest = { dropdown = false },
                    containerColor = Color(20, 20, 20)
                ) {
                    DropdownMenuItem(
                        text = { Text("Atualizar conta", color = Color(254, 102, 0)) },
                        onClick = { navHostController.navigate("updateown") }
                    )
                    DropdownMenuItem(
                        text = { Text("Sair da conta", color = Color(254, 102, 0)) },
                        onClick = { authViewModel.signout() }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir conta", color = Color(254, 102, 0)) },
                        onClick = {
                            deleteOwnAccountDialog = true
                        }
                    )
                }
            },
            colors = TopAppBarColors(
                containerColor = Color(40, 40, 40),
                scrolledContainerColor = Color(40, 40, 40),
                navigationIconContentColor = Color(254, 102, 0),
                titleContentColor = Color(254, 102, 0),
                actionIconContentColor = Color(254, 102, 0)
            ),
            modifier = modifier,
        )

        TextButton(onClick = {
            navHostController.navigate("create")
        }) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Adicionar Usuário",
                    tint = Color(254, 102, 0),
                )
                Text(
                    text = "Adicionar um novo usuário",
                    color = Color(254, 102, 0),
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        /* Talvez usar depois
        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(
                text = "Sair da conta",
                color = Color(254, 102, 0)
            )
        }
         */
        LazyColumn {
            items(userList) { user ->
                Row(
                    Modifier
                        .fillMaxWidth(1f)
                        .padding(vertical = 8.dp),
                    Arrangement.Center
                ) {

                    // popup para confirmar deleção de usuários
                    val deleteDialog = remember { mutableStateOf(false) }
                    if (deleteDialog.value == true) {
                        AlertDialog(
                            title = {
                                Text(text = "Deseja mesmo excluir esse usuário?", color = Color(254, 102, 0))
                            },
                            text = {
                                Text(text = "Esta ação é irreversível.", color = Color(254, 102, 0))
                            },
                            onDismissRequest = {
                                deleteDialog.value = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        userFunctions.deleteUser(user.id)
                                    }
                                ) {
                                    Text("Sim", color = Color(254, 102, 0))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        deleteDialog.value = false
                                    }
                                ) {
                                    Text("Não", color = Color(254, 102, 0))
                                }
                            },
                            containerColor = Color(20, 20, 20)
                        )
                    }
                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        colors = CardColors(
                            containerColor = Color(86, 86, 86),
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.DarkGray
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Nome: " +user.nome,
                                    color = Color(254, 102, 0),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Editar Usuário",
                                    Modifier
                                        .clickable {
                                            /* todo */
                                        },
                                    tint = Color(254, 102, 0)
                                )
                                Spacer(Modifier.padding(horizontal = 6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Deletar Usuário",
                                    Modifier
                                        .clickable {
                                            deleteDialog.value = true
                                           // userFunctions.deleteUser(user.id)
                                        },
                                    tint = Color(254, 102, 0)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Email: " + user.email,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Telefone: " + user.telefone,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Mensagem: " + user.mensagem,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Senha: " + user.senha,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
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
            authViewModel.login(email, senha)
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0)),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Login", color = Color.White)
        }


        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navHostController.navigate("register")
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
    var email by remember {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateOwnAccountScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember {
        mutableStateOf(FirebaseAuth.getInstance().currentUser!!.email)
    }
    var senha by remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

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
            value = email.toString(),
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
            authViewModel.update(email, senha)
            navHostController.popBackStack()
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0)),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Atualizar", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateScreen(
    modifier: Modifier = Modifier,
    mainActivity: MainActivity,
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
    createOrUpdate: String,
    uid: String
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(40, 40, 40)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text =
                if(createOrUpdate == "create") {
                    "Adicionar usuário"
                } else {
                    "Atualizar usuário"
                },
            fontSize = 32.sp, color = Color(254, 102, 0)
        )

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
            if(createOrUpdate == "create") {
                val user = hashMapOf(
                    "nome" to name,
                    "email" to email,
                    "telefone" to telefone,
                    "mensagem" to mensagem,
                    "senha" to senha
                )

                UserFunctions().addUser(user)

                navHostController.popBackStack()
            } else {
                val user = hashMapOf(
                    "nome" to name,
                    "email" to email,
                    "telefone" to telefone,
                    "mensagem" to mensagem,
                    "senha" to senha
                )

                UserFunctions().updateUser(user, uid)

                navHostController.popBackStack()
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(254, 102, 0))
        ) {
            Text(
                text =
                    if(createOrUpdate == "create") {
                        "Adicionar usuário"
                    } else {
                        "Atualizar usuário"
                    },
                color = Color.White
            )
        }
    }
}