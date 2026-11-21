"""Fail closed if an upstream merge dropped or bypassed our integration."""
from pathlib import Path

base = Path('app/src/main/java/com/music/bitchord')
assert 'https://api.github.com/repos/Bodo121/BitChord/releases/latest' in (base / 'data/AppUpdateChecker.kt').read_text()
assert 'BluetoothRepeatPolicy.availableCommands(' in (base / 'playback/PlaybackService.kt').read_text()
assert (base / 'playback/BluetoothRepeatPolicy.kt').is_file()
assert ':app:assembleProdRelease' in Path('.github/workflows/android.yml').read_text()
print('Fork update source, repeat protection and release task are present.')

# Source namespaces stay upstream; installed application IDs must be distinct.
for module, app_id in [('app', 'com.bodo121.bitchord'), ('wearos', 'com.bodo121.bitchord'), ('tv', 'com.bodo121.bitchord.tv')]:
    source = Path(module, 'build.gradle.kts').read_text()
    assert f'applicationId = "{app_id}"' in source, f'{module}: fork package identity lost'
print('Distinct fork application IDs are present.')
