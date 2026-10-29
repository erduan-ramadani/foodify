
import os
import subprocess
import time
import xml.etree.ElementTree as ET

PACKAGE = "com.eddiapps.foodify"
KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
PREFIX = "com.google.firebase.appcheck.debug.store."

token = os.environ.get("APP_CHECK_DEBUG_TOKEN", "")
if not token:
    raise SystemExit("APP_CHECK_DEBUG_TOKEN is missing")


def adb(*args, data=None):
    return subprocess.run(
        ["adb", *args],
        input=data,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        check=True,
    ).stdout


# Start the debug app once to initialize Firebase preferences.
adb("shell", "monkey", "-p", PACKAGE, "1")

filename = None

for _ in range(30):
    result = subprocess.run(
        ["adb", "shell", "run-as", PACKAGE, "ls", "shared_prefs"],
        capture_output=True,
        text=True,
    )

    matches = [
        name.strip()
        for name in result.stdout.splitlines()
        if name.strip().startswith(PREFIX)
        and name.strip().endswith(".xml")
    ]

    if len(matches) == 1:
        filename = matches[0]
        break

    time.sleep(1)

if filename is None:
    raise SystemExit("Firebase debug preferences not found")

# Stop the app before modifying its preferences.
adb("shell", "am", "force-stop", PACKAGE)

path = f"shared_prefs/{filename}"
xml_data = adb("shell", "run-as", PACKAGE, "cat", path)

root = ET.fromstring(xml_data)
entry = root.find(f"./string[@name='{KEY}']")

if entry is None:
    raise SystemExit("Firebase debug secret key not found")

entry.text = token
updated = ET.tostring(root, encoding="utf-8", xml_declaration=True)

# Write through stdin; do not print or pass the secret as a command argument.
adb(
    "shell", "-T", "run-as", PACKAGE,
    "sh", "-c", f"'cat > {path}'",
    data=updated,
)

# Verify that the stored value matches without printing it.
saved = adb("shell", "run-as", PACKAGE, "cat", path)
saved_root = ET.fromstring(saved)
saved_entry = saved_root.find(f"./string[@name='{KEY}']")

if saved_entry is None or saved_entry.text != token:
    raise SystemExit("Firebase debug token verification failed")

print("Firebase App Check CI token configured")
