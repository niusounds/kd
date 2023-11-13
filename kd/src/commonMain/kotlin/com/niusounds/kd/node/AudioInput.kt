package com.niusounds.kd.node

import com.niusounds.kd.Node

enum class Source {
    Default, VoiceCommunication
}

expect class AudioInput(source: Source = Source.Default) : Node