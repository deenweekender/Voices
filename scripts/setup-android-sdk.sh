#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK_DIR_DEFAULT="${HOME}/Android/Sdk"
SDK_DIR="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$SDK_DIR_DEFAULT}}"
CMDLINE_TOOLS_DIR="${SDK_DIR}/cmdline-tools"
LATEST_DIR="${CMDLINE_TOOLS_DIR}/latest"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required but not installed."
  exit 1
fi

if ! command -v unzip >/dev/null 2>&1; then
  echo "unzip is required but not installed."
  exit 1
fi

mkdir -p "${SDK_DIR}" "${CMDLINE_TOOLS_DIR}"

if [[ ! -x "${LATEST_DIR}/bin/sdkmanager" ]]; then
  TMP_DIR="$(mktemp -d)"
  ZIP_PATH="${TMP_DIR}/cmdline-tools.zip"

  echo "Downloading Android command-line tools..."
  curl -fsSL "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" -o "${ZIP_PATH}"

  rm -rf "${LATEST_DIR}"
  mkdir -p "${LATEST_DIR}"
  unzip -q "${ZIP_PATH}" -d "${TMP_DIR}"

  if [[ -d "${TMP_DIR}/cmdline-tools" ]]; then
    cp -R "${TMP_DIR}/cmdline-tools/." "${LATEST_DIR}/"
  else
    cp -R "${TMP_DIR}/." "${LATEST_DIR}/"
  fi

  rm -rf "${TMP_DIR}"
fi

export ANDROID_SDK_ROOT="${SDK_DIR}"
export ANDROID_HOME="${SDK_DIR}"
export PATH="${LATEST_DIR}/bin:${SDK_DIR}/platform-tools:${PATH}"

if [[ ! -x "${LATEST_DIR}/bin/sdkmanager" ]]; then
  echo "sdkmanager was not found after setup."
  exit 1
fi

echo "Accepting Android SDK licenses..."
yes | sdkmanager --sdk_root="${SDK_DIR}" --licenses >/dev/null

echo "Installing required Android SDK packages..."
sdkmanager --sdk_root="${SDK_DIR}" \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0"

LOCAL_PROPERTIES="${PROJECT_ROOT}/local.properties"

if [[ -f "${LOCAL_PROPERTIES}" ]] && grep -q '^sdk\.dir=' "${LOCAL_PROPERTIES}"; then
  sed -i "s#^sdk\\.dir=.*#sdk.dir=${SDK_DIR}#" "${LOCAL_PROPERTIES}"
else
  {
    [[ -f "${LOCAL_PROPERTIES}" ]] && cat "${LOCAL_PROPERTIES}"
    echo "sdk.dir=${SDK_DIR}"
  } > "${LOCAL_PROPERTIES}.tmp"
  mv "${LOCAL_PROPERTIES}.tmp" "${LOCAL_PROPERTIES}"
fi

echo ""
echo "Android SDK is configured."
echo "sdk.dir set in: ${LOCAL_PROPERTIES}"
echo "SDK path: ${SDK_DIR}"
echo ""
echo "Next: ./gradlew :app:assembleDebug"