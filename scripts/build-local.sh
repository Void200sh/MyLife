#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

JAR=tools/android.jar
KS=tools/mylife.keystore
OUT=dist
W=build/local
VER_CODE=2
VER_NAME=1.1

[ -f "$JAR" ] || { echo "android.jar belum ada di tools/"; exit 1; }

rm -rf "$W"
mkdir -p "$W/classes" "$W/dex" "$OUT"

sed 's|<manifest xmlns:android=|<manifest package="com.zyvo.mylife" xmlns:android=|' \
  app/src/main/AndroidManifest.xml > "$W/AndroidManifest.xml"

find app/src/main/java -name '*.java' > "$W/sources.txt"
javac -source 11 -target 11 -Xlint:-options -classpath "$JAR" -d "$W/classes" @"$W/sources.txt"

jar cf "$W/classes.jar" -C "$W/classes" .
d8 --lib "$JAR" --min-api 26 --output "$W/dex" "$W/classes.jar"

rm -f "$W/unsigned.apk"
aapt package -f \
  -M "$W/AndroidManifest.xml" \
  -S app/src/main/res \
  -A app/src/main/assets \
  -I "$JAR" \
  --min-sdk-version 26 --target-sdk-version 34 \
  --version-code "$VER_CODE" --version-name "$VER_NAME" \
  -F "$W/unsigned.apk"

( cd "$W/dex" && aapt add "$ROOT/$W/unsigned.apk" classes.dex >/dev/null )
zipalign -f 4 "$W/unsigned.apk" "$W/aligned.apk"

if [ ! -f "$KS" ]; then
  keytool -genkeypair -keystore "$KS" -storepass android -keypass android \
    -alias mylife -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=MyLife,O=Zyvo,C=ID" >/dev/null 2>&1
fi

apksigner sign --ks "$KS" --ks-pass pass:android --key-pass pass:android \
  --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true \
  --out "$OUT/MyLife-$VER_NAME.apk" "$W/aligned.apk"

apksigner verify --print-certs "$OUT/MyLife-$VER_NAME.apk" | head -4
ls -l "$OUT/MyLife-$VER_NAME.apk"
