package com.niusounds.myapplication

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.niusounds.kd.Kd
import com.niusounds.kd.node.AudioInput
import com.niusounds.kd.node.AudioOutput
import com.niusounds.kd.node.OpusOutput
import com.niusounds.kd.node.SimplePitchShifterNode
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val opusOutput = OpusOutput()
                lifecycleScope.launch {
                    Kd {
                        add(AudioInput())
                        add(SimplePitchShifterNode().apply { pitch = 2.0 })
                        add(AudioOutput())
                        add(opusOutput)
                    }.launch()
                }
                lifecycleScope.launch {
                    opusOutput.flow.collect { res ->
                        Log.d("OpusEncode", "${res.size}")
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "kd")
                        })
                },
            ) { padding ->
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = ::onClick) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }

    private fun onClick() {
        permission.launch(Manifest.permission.RECORD_AUDIO)
    }
}
