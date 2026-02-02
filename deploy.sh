#!/usr/bin/env bash

set -euo pipefail

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Config
GROUP="com.nexora"
VERSION=$(grep '^version=' gradle.properties | cut -d'=' -f2 | tr -d ' ')

# Read credentials from global gradle.properties
GRADLE_PROPERTIES="$HOME/.gradle/gradle.properties"

# Functions
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() {
  echo -e "${RED}[ERROR]${NC} $1"
  exit 1
}

# Helper function to read property from gradle.properties
read_gradle_property() {
  local property_name="$1"
  local file="$2"

  if [[ -f "$file" ]]; then
    grep "^${property_name}=" "$file" | cut -d'=' -f2 | tr -d ' ' || echo ""
  fi
}

# Load credentials from global gradle.properties
# Support multiple property name formats for compatibility
if [[ -f "$GRADLE_PROPERTIES" ]]; then
  # Try new format (YUNXIAO_*) first, then fall back to legacy formats
  YUNXIAO_USERNAME="${YUNXIAO_USERNAME:-$(read_gradle_property "yunxiaoUsername" "$GRADLE_PROPERTIES")}"
  YUNXIAO_USERNAME="${YUNXIAO_USERNAME:-$(read_gradle_property "codeupUsername" "$GRADLE_PROPERTIES")}"
  YUNXIAO_PASSWORD="${YUNXIAO_PASSWORD:-$(read_gradle_property "yunxiaoPassword" "$GRADLE_PROPERTIES")}"
  YUNXIAO_PASSWORD="${YUNXIAO_PASSWORD:-$(read_gradle_property "codeupPassword" "$GRADLE_PROPERTIES")}"
  YUNXIAO_SNAPSHOT_URL="${YUNXIAO_SNAPSHOT_URL:-$(read_gradle_property "yunxiaoSnapshotRepositoryUrl" "$GRADLE_PROPERTIES")}"
  YUNXIAO_SNAPSHOT_URL="${YUNXIAO_SNAPSHOT_URL:-$(read_gradle_property "codeupSnapshotUrl" "$GRADLE_PROPERTIES")}"
  YUNXIAO_RELEASE_URL="${YUNXIAO_RELEASE_URL:-$(read_gradle_property "yunxiaoReleaseRepositoryUrl" "$GRADLE_PROPERTIES")}"
  YUNXIAO_RELEASE_URL="${YUNXIAO_RELEASE_URL:-$(read_gradle_property "codeupReleaseUrl" "$GRADLE_PROPERTIES")}"
fi

# Env vars (fallback to defaults)
YUNXIAO_USERNAME="${YUNXIAO_USERNAME:-}"
YUNXIAO_PASSWORD="${YUNXIAO_PASSWORD:-}"
YUNXIAO_SNAPSHOT_URL="${YUNXIAO_SNAPSHOT_URL:-https://packages.aliyun.com/maven/repository/snapshot}"
YUNXIAO_RELEASE_URL="${YUNXIAO_RELEASE_URL:-https://packages.aliyun.com/maven/repository/release}"

# Parse args
TYPE="snapshot"
VERSION_OVERRIDE=""
SKIP_CONFIRM=false

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
  -y | --yes)
    SKIP_CONFIRM=true
    shift
    ;;
  -h | --help)
    echo "Usage: $0 [-t|--type snapshot|release] [-v|--version VERSION] [-y|--yes]"
    echo ""
    echo "Options:"
    echo "  -t, --type TYPE      Deployment type: snapshot or release (default: snapshot)"
    echo "  -v, --version VER    Override version (default: use gradle.properties)"
    echo "  -y, --yes            Skip confirmation prompt"
    echo "  -h, --help           Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                  # Deploy snapshot (with confirmation)"
    echo "  $0 -t release       # Deploy release version"
    echo "  $0 -t release -v 1.0.1  # Deploy specific version"
    echo "  $0 -y               # Deploy snapshot without confirmation"
    echo ""
    echo "Credentials configuration (add to ~/.gradle/gradle.properties):"
    echo "  # Recommended format (matches CI)"
    echo "  yunxiaoUsername=your_username"
    echo "  yunxiaoPassword=your_password"
    echo "  yunxiaoSnapshotRepositoryUrl=https://packages.aliyun.com/maven/repository/xxx-snapshot/"
    echo "  yunxiaoReleaseRepositoryUrl=https://packages.aliyun.com/maven/repository/xxx-release/"
    echo ""
    echo "  # Legacy format (still supported)"
    echo "  codeupUsername=your_username"
    echo "  codeupPassword=your_password"
    echo ""
    echo "Or set environment variables:"
    echo "  export YUNXIAO_USERNAME=your_username"
    echo "  export YUNXIAO_PASSWORD=your_password"
    echo "  export YUNXIAO_SNAPSHOT_URL=https://..."
    echo "  export YUNXIAO_RELEASE_URL=https://..."
    exit 0
    ;;
  *)
    log_error "Unknown option: $1. Use -h for help."
    ;;
  esac
done

# Validate credentials
if [[ -z "$YUNXIAO_USERNAME" ]]; then
  log_error "Username not set."
  echo ""
  echo "Add to ~/.gradle/gradle.properties:"
  echo "  yunxiaoUsername=your_username"
  echo "  yunxiaoPassword=your_password"
  echo ""
  echo "Or set environment variables:"
  echo "  export YUNXIAO_USERNAME=your_username"
  echo "  export YUNXIAO_PASSWORD=your_password"
fi

if [[ -z "$YUNXIAO_PASSWORD" ]]; then
  log_error "Password not set."
  echo ""
  echo "Add to ~/.gradle/gradle.properties:"
  echo "  yunxiaoUsername=your_username"
  echo "  yunxiaoPassword=your_password"
  echo ""
  echo "Or set environment variables:"
  echo "  export YUNXIAO_USERNAME=your_username"
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

# Determine URL and repository name
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
echo -e "${BLUE}  Configuration:${NC}"
echo "  Group:    $GROUP"
echo "  Version:  $FINAL_VERSION"
echo "  Type:     $REPO_NAME"
echo "  URL:      $REPO_URL"
echo ""

# Confirm
if [[ "$SKIP_CONFIRM" == false ]]; then
  read -p "Continue? [y/N] " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    log_warn "Aborted"
    exit 0
  fi
fi

# Publish
log_info "Building and publishing..."

# Use Gradle wrapper for consistent builds
./gradlew publish --no-daemon --scan \
  -PYunxiaoSnapshotRepositoryUrl="$YUNXIAO_SNAPSHOT_URL" \
  -PYunxiaoReleaseRepositoryUrl="$YUNXIAO_RELEASE_URL" \
  -PYUNXIAO_USERNAME="$YUNXIAO_USERNAME" \
  -PYUNXIAO_PASSWORD="$YUNXIAO_PASSWORD" \
  -PprojectVersion="$FINAL_VERSION"

log_info "✅ Deployed successfully!"
log_info "Version: $FINAL_VERSION"
log_info "Repository: $REPO_NAME"
