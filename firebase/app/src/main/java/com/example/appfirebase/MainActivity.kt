package com.example.appfirebase

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirebase.ui.theme.AppFirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(db)
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    AppFirebaseTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Header()
                InputFields(nome, telefone) { newNome, newTelefone ->
                    nome = newNome
                    telefone = newTelefone
                }
                ActionButton(db, nome, telefone)
                FetchClientData(db)
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "App Firebase - Cadastrar Clientes",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun InputFields(nome: String, telefone: String, onValueChange: (String, String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        InputField(label = "Nome", value = nome, onValueChange = { newNome -> onValueChange(newNome, telefone) })
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Telefone", value = telefone, onValueChange = { newTelefone -> onValueChange(nome, newTelefone) })
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ActionButton(db: FirebaseFirestore, nome: String, telefone: String) {
    Button(
        onClick = {
            val client = hashMapOf(
                "nome" to nome,
                "telefone" to telefone
            )
            db.collection("Clientes").add(client)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)), // Cor roxa
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(48.dp) // Tamanho menor
    ) {
        Text(text = "Cadastrar", fontSize = 16.sp)
    }
}

@Composable
fun FetchClientData(db: FirebaseFirestore) {
    // Placeholder function for fetching client data
    LaunchedEffect(Unit) {
        db.collection("Clientes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val lista = hashMapOf(
                        "nome" to "${document.data["nome"]}",
                        "telefone" to "${document.data["telefone"]}"
                    )
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
