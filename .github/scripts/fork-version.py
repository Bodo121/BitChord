"""Give fork releases a monotonic install code and numeric updater version."""
import os
import re
import time
from pathlib import Path

# Shared time-based sequence across manual release and sync workflow runs.
run = int(time.time()) - 1_767_225_600
if not 0 < run < 1_000_000_000:
    raise ValueError('Timestamp exceeds the fork version range')
p = Path('build.gradle.kts')
s = p.read_text()
base = re.search(r'extra\["bitchordVersionName"\] = "([0-9.]+)"', s).group(1)
version = f'{base}.{run}'
for key, value in [('bitchordVersionCode', str(1_000_000_000 + run)),
                   ('bitchordVersionName', f'"{version}"'), ('bitchordBetaSuffix', '""')]:
    s, count = re.subn(r'extra\["' + key + r'"\] = [^\n]+',
                       f'extra["{key}"] = {value}', s)
    assert count == 1, f'Upstream changed {key}; review versioning before building'
p.write_text(s)
with open(os.environ['GITHUB_OUTPUT'], 'a') as out:
    out.write(f'version={version}\n')
