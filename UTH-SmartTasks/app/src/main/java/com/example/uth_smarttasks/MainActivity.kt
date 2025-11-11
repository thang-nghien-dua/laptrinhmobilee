package com.example.uth_smarttasks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uth_smarttasks.ui.theme.UTHSmartTasksTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        firebaseAuth = FirebaseAuth.getInstance()

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // ignore for brevity; could show a snackbar
            }
        }

        setContent {
            UTHSmartTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var statusText by remember { mutableStateOf("") }
                    var currentUserState by remember { mutableStateOf(firebaseAuth.currentUser) }

                    // Lắng nghe thay đổi đăng nhập/đăng xuất và cập nhật UI
                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { auth ->
                            currentUserState = auth.currentUser
                        }
                        firebaseAuth.addAuthStateListener(listener)
                        onDispose { firebaseAuth.removeAuthStateListener(listener) }
                    }

                    if (currentUserState == null) {
                        LoginScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            status = statusText,
                            onSignInClick = {
                                statusText = ""
                                signInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        )
                    } else {
                        HomeScreen(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            email = currentUserState?.email ?: "",
                            onSignOutClick = {
                                firebaseAuth.signOut()
                                googleSignInClient.signOut()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken.isNullOrEmpty()) return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user ?: return@addOnSuccessListener
                val userDoc = mapOf(
                    "uid" to user.uid,
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: "")
                )
                FirebaseFirestore.getInstance().collection("users")
                    .document(user.uid)
                    .set(userDoc, SetOptions.merge())
            }
            .addOnFailureListener {
                // ignore for brevity; could log
            }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    status: String,
    onSignInClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UTH SmartTasks",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Quản lý công việc thông minh",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Tiếp tục với Google",
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (status.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    email: String,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Xin chào!",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onSignOutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Đăng xuất",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    UTHSmartTasksTheme {
        LoginScreen(status = "", onSignInClick = {})
    }
}