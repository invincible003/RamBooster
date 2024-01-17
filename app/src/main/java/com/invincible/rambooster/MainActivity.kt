package com.invincible.rambooster

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.AppTheme
import com.google.android.material.color.DynamicColors
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

    }

}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manager = Manager(applicationContext)

        setContent {

            var noRootDialog by rememberSaveable { mutableStateOf(!Manager.IS_ROOT_GRANTED) }
            var logs by remember { mutableStateOf("Logs Will Appear Here") }
            var appDescription by remember { mutableStateOf("New And Powerful Tool Only For Rooted Devices !") }
            var showProgress by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0f) }

            AppTheme {

                if (noRootDialog) {
                    NoRootDialog(
                        onConfirmation = { noRootDialog = false },
                    )
                    appDescription = "Root Not Found\n" +
                            "Only Radio Info Will Work So Don't Complain"
                } else {
                    Shell.cmd("su --mount-master").exec()
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Developed By Invincible",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("With Love 💕")
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            appDescription,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Column {

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent("android.intent.action.MAIN")
                                        intent.setClassName(
                                            "com.android.phone",
                                            "com.android.phone.settings.RadioInfo"
                                        )
                                        startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text("Radio Info Toggle 5G/4G")
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                if (!showProgress) {
                                    FilledTonalButton(
                                        onClick = {
                                            showProgress = true
                                            val list = manager.stopableApps()
                                            var runTimes = 0f
                                            CoroutineScope(Dispatchers.IO).launch {
                                                for (name in (list.size) / 2 until list.size) {
                                                    progress = runTimes/list.size
                                                    runTimes++
                                                    manager.stopPackage(list[name])
                                                    logs = "Killing -- ${list[name]}"
                                                }
                                            }.invokeOnCompletion {
                                                manager.stopPackage(packageName)
                                            }

                                            CoroutineScope(Dispatchers.IO).launch {
                                                for (name in 1..(list.size) / 2) {
                                                    progress = runTimes/list.size
                                                    runTimes++
                                                    manager.stopPackage(list[name])
                                                }
                                            }


                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    ) {
                                        Text("Boost Ram")
                                    }
                                }
                                else{
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                            .height(20.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                        ,
                                        progress = progress,
                                    )
                                }

                            }

                        }
                        Text(
                            "+ $logs",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "Memory ${manager.getRamInfo()}"
                        )
                    }
                }
            }

        }
    }

    @Composable
    fun NoRootDialog(
        onConfirmation: () -> Unit,
        dialogTitle: String = "Root Not Found",
        dialogText: String = "Only Radio Info Will Work To use All Features Please have a Root Access ",
    ) {
        AlertDialog(
            icon = {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Example Icon")
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
            },
            onDismissRequest = {
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Confirm")
                }
            }
        )
    }
}


