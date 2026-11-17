package com.music.bitchord

import androidx.media3.common.Player
import com.music.bitchord.playback.BluetoothRepeatPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothRepeatPolicyTest {
    private val commands = Player.Commands.Builder().addAllCommands().build()

    @Test
    fun `car connection cannot set repeat but retains every other command`() {
        listOf("com.android.bluetooth", "com.google.android.bluetooth",
            "android.media.session.MediaController").forEach { caller ->
            val allowed = BluetoothRepeatPolicy.availableCommands(caller, commands)
            assertFalse(allowed.contains(Player.COMMAND_SET_REPEAT_MODE))
            for (i in 0 until commands.size()) {
                val command = commands[i]
                if (command != Player.COMMAND_SET_REPEAT_MODE) assertTrue(allowed.contains(command))
            }
            // The defaults must not be mutated for the next app connection.
            assertTrue(commands.contains(Player.COMMAND_SET_REPEAT_MODE))
        }
    }

    @Test
    fun `phone watch and Android Auto can still choose repeat`() {
        listOf("com.music.bitchord", "com.dev.bitchord", "com.music.bitchord.wear",
            "com.google.android.projection.gearhead", "com.android.systemui").forEach { caller ->
            assertEquals(commands, BluetoothRepeatPolicy.availableCommands(caller, commands))
        }
    }
}
