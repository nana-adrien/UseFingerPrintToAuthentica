package com.example.usefingerprinttoauthentica

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.usefingerprinttoauthentica.ui.theme.UseFingerPrintToAuthenticaTheme
import java.util.concurrent.Executor

class MainActivity : ComponentActivity() {
                                        private lateinit var executor: Executor
   override fun onCreate(savedInstanceState : Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent {
         UseFingerPrintToAuthenticaTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
               Greeting(
                  name = "Android" ,
                  modifier = Modifier.padding(innerPadding)
               )
            }
         }
      }
   }
}

@Composable
fun Greeting(name : String , modifier : Modifier = Modifier) {

   val context= LocalContext.current as Activity

   val biometricManager = BiometricManager.from(context)
   when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
      BiometricManager.BIOMETRIC_SUCCESS -> {
         BiometricAuthenticationScreen()
      }
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
         Toast.makeText(context,"biometrie pas disponible",Toast.LENGTH_LONG).show()
      }
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

         Toast.makeText(context," Le matériel biométrique n'est pas disponible actuellement",Toast.LENGTH_LONG).show()
         // Le matériel biométrique n'est pas disponible actuellement
      }
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

         Toast.makeText(context,"L'utilisateur n'a pas enregistré de données biométriques",Toast.LENGTH_LONG).show()
         // L'utilisateur n'a pas enregistré de données biométriques
      }
   }

  /* LaunchedEffect(key1 = Unit) {
      biometricPrompt.authenticate(promptInfo)
   }*/
   Text(
      text = "Hello $name!" ,
      modifier = modifier
   )




}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
   UseFingerPrintToAuthenticaTheme {
      //Greeting("Android")
   }
}

fun authenticateWithBiometrics(
        context: Context ,
        onAuthenticationSuccess: () -> Unit ,
        onAuthenticationError: (String) -> Unit
) {
   val executor = ContextCompat.getMainExecutor(context)
   val biometricPrompt = BiometricPrompt(
      context as androidx.fragment.app.FragmentActivity,
      executor,
      object : BiometricPrompt.AuthenticationCallback() {
         override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthenticationSuccess()
         }

         override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onAuthenticationError(errString.toString())
         }
      }
   )

   val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Authentification Biométrique")
      .setSubtitle("Connectez-vous avec vos empreintes digitales ou un scan facial")
      .setNegativeButtonText("Annuler")
      .build()

   biometricPrompt.authenticate(promptInfo)
}
@Composable
fun BiometricAuthenticationScreen() {
   val context = LocalContext.current
   var authenticationStatus by remember { mutableStateOf("") }

   Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
   ) {
      Text(text = "Statut : $authenticationStatus")

      Spacer(modifier = Modifier.height(20.dp))

      Button(onClick = {
         authenticateWithBiometrics(
            context = context,
            onAuthenticationSuccess = {
               authenticationStatus = "Authentification réussie !"
            },
            onAuthenticationError = { error ->
               authenticationStatus = "Erreur : $error"
            }
         )
      }) {
         Text("S'authentifier")
      }
   }
}