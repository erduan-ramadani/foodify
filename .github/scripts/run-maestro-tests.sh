#!/usr/bin/env bash

if ! maestro test .maestro; then
    echo "=== Foodify and Firebase errors ==="

    adb logcat -d -v brief \
      | grep -Ei 'App attestation failed|FirebaseAppCheck|Antwort failure|403' \
      | tail -80 || true

    exit 1
fi