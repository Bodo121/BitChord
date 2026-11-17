package com.music.bitchord

import androidx.media3.common.Player
import com.music.bitchord.playback.BluetoothRepeatPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothRepeatPolicyTest {
    // Keep these tests independent of Android's SparseBooleanArray backing
    // Player.Commands, since ordinary JVM tests use a stub android.jar.
    private val transportCommands = listOf(
        Player.COMMAND_PLAY_PAUSE, Player.COMMAND_SEEK_TO_NEXT,
        Player.COMMAND_SEEK_TO_PREVIOUS, Player.COMMAND_GET_METADATA,
        Player.COMMAND_SET_SHUFFLE_MODE, Player.COMMAND_SET_VOLUME,
    )

    @Test
    fun `car connection cannot set repeat but retains transport and metadata`() {
        listOf("com.android.bluetooth", "com.google.android.bluetooth",
            "android.media.session.MediaController").forEach { caller ->
            assertFalse(BluetoothRepeatPolicy.allowsCommand(caller, Player.COMMAND_SET_REPEAT_MODE))
            transportCommands.forEach { command ->
                assertTrue(BluetoothRepeatPolicy.allowsCommand(caller, command))
            }
        }
    }

    @Test
    fun `phone watch and Android Auto can still choose repeat`() {
        listOf("com.music.bitchord", "com.dev.bitchord", "com.music.bitchord.wear",
            "com.google.android.projection.gearhead", "com.android.systemui").forEach { caller ->
            assertTrue(BluetoothRepeatPolicy.allowsCommand(caller, Player.COMMAND_SET_REPEAT_MODE))
        }
    }
}
