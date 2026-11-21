# Bodo121 BitChord fork

## Bluetooth repeat protection

The media session no longer grants SET_REPEAT_MODE to Android's Bluetooth
controller (`com.android.bluetooth` / `com.google.android.bluetooth`) or the
anonymous legacy controller. This prevents a head unit from restoring its repeat
setting into the app during AVRCP connection. The phone's repeat control still
works. Playback, pause, track keys, shuffle, metadata, volume and Android Auto
remain available. This applies to all Bluetooth devices because the session sees
the phone's Bluetooth service, not a remote device name. Unknown vendor Bluetooth
package names may require another entry after collecting logs.

The reported BMW E84 CHIC MID behavior needs verification in the actual car:
1. Set repeat OFF in BitChord, connect, start playback, and reconnect several times.
2. Check repeat stays OFF and the next song plays normally.
3. Select repeat ONE/ALL in the app and confirm the selection survives reconnects.
4. Check car play/pause, next/previous and metadata; check normal headphones too.
If repeat was already enabled before installing, turn it off once in the app.
The patch preserves the user's selection rather than resetting repeat on launch.

## Build and install

Actions → **Fork Release APK** → **Run workflow** builds `prodRelease`, with
R8 optimization and resource shrinking. It never substitutes a debug APK.
The workflow also runs production release unit tests.

For an installable APK, configure these repository Actions secrets once:
- `KEYSTORE_BASE64`: base64 of your private release JKS.
- `KEYSTORE_PASSWORD`: store password.
- `KEY_ALIAS`: signing alias.
- `KEY_PASSWORD`: key password.

If you do not already have a key, create one locally and back it up securely:

```sh
keytool -genkeypair -keystore bitchord-release.jks -alias bitchord \
  -keyalg RSA -keysize 4096 -validity 10000
base64 -w0 bitchord-release.jks
```

Use the output as KEYSTORE_BASE64. Never commit the keystore or passwords.
Keep the same key for every future release. Without secrets, the workflow still
builds a clearly labeled **UNSIGNED** release artifact for inspection, but does
not publish an unusable update. Partial signing settings fail with an explanation.
A fork-signed build cannot update an official APK signed with the author's key;
back up app data before uninstalling the official build for the first fork install.

Signed builds appear in this fork's Releases, with universal and ABI-specific APKs
and SHA-256 sums. The in-app updater reads **Bodo121/BitChord** only. Each build
uses a numeric version such as `1.9.1.27700000` and a monotonic, time-based fork versionCode, so
subsequent fork releases are detected and install over the prior fork release.
The version changes happen only in the build workspace, leaving upstream's
version lines untouched for easier merging.

## Get official changes and retain the fix

Actions → **Sync upstream and build release** → **Run workflow**.
This fetches the latest stable `kushagrasinghx/BitChord` release tag, performs a three-way merge retaining
this fork's patch commits, checks the fix remains integrated, pushes main, then
explicitly runs the release builder. It works even when upstream is unchanged.
No manual patch copying, resets or force pushes are used. If upstream changes
the same code incompatibly, the merge fails before pushing; resolve the conflict
in a branch, keeping our update URL, repeat policy and workflows, then rerun.
Use this workflow for syncing; GitHub's destructive 'discard commits' option
would remove the fork fixes.

## Side-by-side identity and release sync

As of upstream v1.10, this fork installs as **BitChord Bodo** with Android
application ID `com.bodo121.bitchord` (TV: `com.bodo121.bitchord.tv`).
The bundled watch app uses the phone package and existing fork signing key.
Kotlin namespaces and component class names remain upstream-compatible.
Provider authorities already derive from the application ID.

The official app and this fork can coexist, with separate app data and logins.
The earlier fork used the official package; Android treats this as a new app,
so existing settings are not migrated automatically. Use an exported backup
when moving data. Google Drive authorization may require an OAuth Android
client registered for the fork package and signing certificate.

The sync workflow now fetches the latest stable upstream **release tag**, not
upstream main (v1.10 was published from the v1.9.2 branch). It retains fork
patches and checks the application IDs before pushing.
