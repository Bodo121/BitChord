"""Fail closed if an upstream merge dropped or bypassed our integration."""
from pathlib import Path

base = Path('app/src/main/java/com/music/bitchord')
assert 'https://api.github.com/repos/Bodo121/BitChord/releases/latest' in (base / 'data/AppUpdateChecker.kt').read_text()
assert 'BluetoothRepeatPolicy.availableCommands(' in (base / 'playback/PlaybackService.kt').read_text()
assert (base / 'playback/BluetoothRepeatPolicy.kt').is_file()
assert ':app:assembleProdRelease' in Path('.github/workflows/android.yml').read_text()
print('Fork update source, repeat protection and release task are present.')
