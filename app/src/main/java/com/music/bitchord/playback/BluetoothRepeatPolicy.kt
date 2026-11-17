package com.music.bitchord.playback

import androidx.media3.common.Player

/**
 * Some AVRCP head units (including the reported BMW E84 CHIC MID) restore
 * their own repeat setting when connecting. Do not let that overwrite the
 * listener's choice in BitChord. Media3 enforces these per-controller commands
 * for both modern and legacy media sessions, before touching the player.
 *
 * Android exposes the phone's Bluetooth service, not the remote car's name.
 * No Bluetooth scan permission or device-name matching is needed. Anonymous
 * legacy controllers are covered too, for Android versions that cannot
 * identify the caller. Play/pause, track keys and metadata remain available;
 * repeat is still controlled by the app, watch and identified Auto clients.
 */
internal object BluetoothRepeatPolicy {
    fun allowsCommand(packageName: String, command: Int): Boolean {
        val bluetooth = packageName == "com.android.bluetooth" ||
            packageName == "com.google.android.bluetooth" ||
            packageName == "android.media.session.MediaController"
        return command != Player.COMMAND_SET_REPEAT_MODE || !bluetooth
    }

    fun availableCommands(packageName: String, defaults: Player.Commands): Player.Commands =
        if (allowsCommand(packageName, Player.COMMAND_SET_REPEAT_MODE)) {
            defaults
        } else {
            defaults.buildUpon().remove(Player.COMMAND_SET_REPEAT_MODE).build()
        }
}
