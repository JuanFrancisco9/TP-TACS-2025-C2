#!/bin/sh
set -e

CONFIG_TEMPLATE="/usr/share/nginx/html/app-config.js.template"
CONFIG_OUTPUT="/usr/share/nginx/html/app-config.js"

API_BASE_URL_VALUE="${VITE_API_BASE_URL:-${API_BASE_URL:-}}"
R2_IMAGE_BASE_URL_VALUE="${VITE_R2_IMAGE_BASE_URL:-${R2_IMAGE_BASE_URL:-}}"

export API_BASE_URL_VALUE
export R2_IMAGE_BASE_URL_VALUE

if [ ! -f "${CONFIG_TEMPLATE}" ]; then
  cat <<'EOF' > "${CONFIG_OUTPUT}"
window.__APP_CONFIG__ = {};
EOF
  echo "40-generate-app-config.sh: template not found; generated empty runtime config."
  exit 0
fi

envsubst '${API_BASE_URL_VALUE} ${R2_IMAGE_BASE_URL_VALUE}' \
  < "${CONFIG_TEMPLATE}" \
  > "${CONFIG_OUTPUT}"

echo "40-generate-app-config.sh: generated runtime config at ${CONFIG_OUTPUT}"
