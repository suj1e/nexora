#!/usr/bin/env bash

set -euo pipefail

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Config
GROUP="com.nexora"
VERSION=$(grep '^version=' gradle.properties | cut -d'=' -f2 | tr -d ' ')

# Env vars (fallback to defaults)
YUNXIAO_USERNAME="${YUNXIAO_USERNAME:-}"
YUNXIAO_PASSWORD="${YUNXIAO_PASSWORD:-}"
YUNXIAO_SNAPSHOT_URL="${YUNXIAO_SNAPSHOT_URL:-https://packages.aliyun.com/maven/repository/snapshot}"
YUNXIAO_RELEASE_URL="${YUNXIAO_RELEASE_URL:-https://packages.aliyun.com/maven/repository/release}"

# Functions
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() {
  echo -e "${RED}[ERROR]${NC} $1"
  exit 1
}

# Parse args
TYPE="snapshot"
VERSION_OVERRIDE=""

while [[ $# -gt 0 ]]; do
  case $1 in
  -t | --type)
    TYPE="$2"
    shift 2
    ;;
  -v | --version)
    VERSION_OVERRIDE="$2"
    shift 2
    ;;
  -h | --help)
    echo "Usage: $0 [-t|--type snapshot|release] [-v|--version VERSION]"
    echo ""
    echo "Examples:"
    echo "  $0                  # Deploy snapshot"
    echo "  $0 -t release -v 1.0.1"
    exit 0
    ;;
  *)
    log_error "Unknown option: $1"
    ;;
  esac
done

# Validate
if [[ -z "$YUNXIAO_USERNAME" ]]; then
  log_error "YUNXIAO_USERNAME not set. Export it first:"
  echo "  export YUNXIAO_USERNAME=your_username"
fi

if [[ -z "$YUNXIAO_PASSWORD" ]]; then
  log_error "YUNXIAO_PASSWORD not set. Export it first:"
  echo "  export YUNXIAO_PASSWORD=your_password"
fi

# Determine version
if [[ -n "$VERSION_OVERRIDE" ]]; then
  FINAL_VERSION="$VERSION_OVERRIDE"
elif [[ "$TYPE" == "snapshot" ]]; then
  BASE="${VERSION%-SNAPSHOT}"
  FINAL_VERSION="${BASE}-SNAPSHOT"
else
  FINAL_VERSION="$VERSION"
fi

# Determine URL
if [[ "$TYPE" == "snapshot" ]]; then
  REPO_URL="$YUNXIAO_SNAPSHOT_URL"
  REPO_NAME="Snapshot"
else
  REPO_URL="$YUNXIAO_RELEASE_URL"
  REPO_NAME="Release"
fi

# Summary
log_info "Deploying to Aliyun Yunxiao"
echo ""
echo "  Group:    $GROUP"
echo "  Version:  $FINAL_VERSION"
echo "  Type:     $REPO_NAME"
echo "  URL:      $REPO_URL"
echo ""

# Confirm
read -p "Continue? [y/N] " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  log_warn "Aborted"
  exit 0
fi

# Publish
log_info "Building and publishing..."

gradle publish --no-daemon \
  -PrepositoryUrl="$REPO_URL" \
  -PrepositoryUsername="$YUNXIAO_USERNAME" \
  -PrepositoryPassword="$YUNXIAO_PASSWORD" \
  -PprojectVersion="$FINAL_VERSION"

log_info "✅ Deployed successfully!"
