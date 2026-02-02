#!/usr/bin/env bash
#
# Nexora Deployment Script
#
# This script deploys Nexora artifacts to Aliyun Yunxiao Maven repository.
# Supports snapshot and release deployments with retry logic and rollback capability.
#
# Usage: ./deploy.sh [OPTIONS]
#
# Author: Nexora Team
# Version: 2.0.0

set -euo pipefail

#===========================================
# Color Definitions
#===========================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
GRAY='\033[0;90m'
NC='\033[0m' # No Color

#===========================================
# Configuration
#===========================================
GROUP="com.nexora"
VERSION=$(grep '^version=' gradle.properties | cut -d'=' -f2 | tr -d ' ')
MAX_RETRIES=3
RETRY_DELAY=5
DRY_RUN=false
ROLLBACK_ON_FAILURE=true

# Credential file
GRADLE_PROPERTIES="$HOME/.gradle/gradle.properties"

# Backup directory for rollback
BACKUP_DIR=".deploy-backup"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

#===========================================
# Utility Functions
#===========================================

log_info() {
  echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
  echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
  echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
  if [[ "${DEBUG:-false}" == "true" ]]; then
    echo -e "${GRAY}[DEBUG]${NC} $1"
  fi
}

log_step() {
  echo -e "${CYAN}[STEP]${NC} $1"
}

log_success() {
  echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Progress indicator
show_progress() {
  local pid=$1
  local message="$2"
  local spin='-\|/'

  echo -ne "${BLUE}[${NC}${message}${BLUE}]${NC} "

  while kill -0 $pid 2>/dev/null; do
    for i in $(seq 0 3); do
      if ! kill -0 $pid 2>/dev/null; then
        break
      fi
      echo -ne "\b${spin:$i:1}"
      sleep 0.1
    done
  done
  echo -ne "\b\r"
}

# Dry run banner
show_dry_run_banner() {
  echo -e "${YELLOW}╔══════════════════════════════════════════════════════════╗${NC}"
  echo -e "${YELLOW}║                    DRY RUN MODE                          ║${NC}"
  echo -e "${YELLOW}║           No actual deployment will be performed         ║${NC}"
  echo -e "${YELLOW}╚══════════════════════════════════════════════════════════╝${NC}"
  echo ""
}

# Read property from gradle.properties
read_gradle_property() {
  local property_name="$1"
  local file="$2"

  if [[ -f "$file" ]]; then
    grep "^${property_name}=" "$file" | cut -d'=' -f2 | tr -d ' ' || echo ""
  fi
}

#===========================================
# Retry Logic with Exponential Backoff
#===========================================

execute_with_retry() {
  local command="$1"
  local attempt=1
  local delay=$RETRY_DELAY

  while true; do
    log_debug "Attempt $attempt of $MAX_RETRIES"

    if eval "$command"; then
      return 0
    fi

    if [[ $attempt -ge $MAX_RETRIES ]]; then
      log_error "Command failed after $MAX_RETRIES attempts: $command"
      return 1
    fi

    log_warn "Command failed, retrying in ${delay}s... (attempt $attempt/$MAX_RETRIES)"
    sleep $delay

    # Exponential backoff
    delay=$((delay * 2))
    attempt=$((attempt + 1))
  done
}

#===========================================
# Rollback Functions
#===========================================

create_backup() {
  log_step "Creating backup for potential rollback..."

  mkdir -p "$BACKUP_DIR"
  local backup_file="$BACKUP_DIR/deploy_${TIMESTAMP}.tar.gz"

  tar -czf "$backup_file" \
    build/ \
    gradle.properties \
    .gradle/ 2>/dev/null || true

  log_success "Backup created: $backup_file"
  echo "$backup_file"
}

rollback_deployment() {
  local backup_file="$1"

  if [[ ! -f "$backup_file" ]]; then
    log_error "Backup file not found: $backup_file"
    return 1
  fi

  log_step "Rolling back deployment..."

  if tar -xzf "$backup_file"; then
    log_success "Rollback completed"
    return 0
  else
    log_error "Rollback failed"
    return 1
  fi
}

#===========================================
# Credential Loading
#===========================================

load_credentials() {
  log_step "Loading credentials..."

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

  log_debug "Username: ${YUNXIAO_USERNAME:0:3}***"
  log_debug "Snapshot URL: $YUNXIAO_SNAPSHOT_URL"
  log_debug "Release URL: $YUNXIAO_RELEASE_URL"
}

#===========================================
# Validation Functions
#===========================================

validate_credentials() {
  log_step "Validating credentials..."

  if [[ -z "$YUNXIAO_USERNAME" ]]; then
    log_error "Username not configured."
    echo ""
    echo -e "${YELLOW}Configure credentials in ~/.gradle/gradle.properties:${NC}"
    echo "  yunxiaoUsername=your_username"
    echo "  yunxiaoPassword=your_password"
    echo "  yunxiaoSnapshotRepositoryUrl=https://..."
    echo "  yunxiaoReleaseRepositoryUrl=https://..."
    echo ""
    echo -e "${YELLOW}Or set environment variables:${NC}"
    echo "  export YUNXIAO_USERNAME=your_username"
    echo "  export YUNXIAO_PASSWORD=your_password"
    return 1
  fi

  if [[ -z "$YUNXIAO_PASSWORD" ]]; then
    log_error "Password not configured."
    echo ""
    echo -e "${YELLOW}Configure credentials in ~/.gradle/gradle.properties:${NC}"
    echo "  yunxiaoUsername=your_username"
    echo "  yunxiaoPassword=your_password"
    return 1
  fi

  log_success "Credentials validated"
  return 0
}

validate_environment() {
  log_step "Validating environment..."

  # Check Java version
  if ! command -v java &> /dev/null; then
    log_error "Java not found. Please install JDK 21 or higher."
    return 1
  fi

  local java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
  if [[ $java_version -lt 21 ]]; then
    log_warn "Java version $java_version detected. JDK 21+ is recommended."
  fi

  # Check Gradle wrapper
  if [[ ! -f "./gradlew" ]]; then
    log_error "Gradle wrapper not found. Please run: gradle wrapper"
    return 1
  fi

  # Check if in project root
  if [[ ! -f "build.gradle.kts" ]] && [[ ! -f "build.gradle" ]]; then
    log_error "Not in project root. Please run from project root directory."
    return 1
  fi

  log_success "Environment validated"
  return 0
}

#===========================================
# Deployment Functions
#===========================================

dry_run_deployment() {
  show_dry_run_banner

  log_info "Dry run: Would deploy the following:"
  echo ""
  echo -e "${BLUE}  Configuration:${NC}"
  echo "  Group:    $GROUP"
  echo "  Version:  $FINAL_VERSION"
  echo "  Type:     $REPO_NAME"
  echo "  URL:      $REPO_URL"
  echo ""
  echo -e "${BLUE}  Modules to deploy:${NC}"
  echo "  - nexora-common"
  echo "  - nexora-spring-boot-starter-web"
  echo "  - nexora-spring-boot-starter-webflux"
  echo "  - nexora-spring-boot-starter-data-jpa"
  echo "  - nexora-spring-boot-starter-redis"
  echo "  - nexora-spring-boot-starter-kafka"
  echo "  - nexora-spring-boot-starter-resilience"
  echo "  - nexora-spring-boot-starter-security"
  echo "  - nexora-spring-boot-starter-file-storage"
  echo "  - nexora-spring-boot-starter-audit"
  echo "  - nexora-spring-boot-starter-observability"
  echo ""

  log_info "Dry run completed. No deployment performed."
}

execute_deployment() {
  log_step "Building and publishing..."

  local gradle_cmd="./gradlew publish --no-daemon --scan \
    -PYunxiaoSnapshotRepositoryUrl=\"$YUNXIAO_SNAPSHOT_URL\" \
    -PYunxiaoReleaseRepositoryUrl=\"$YUNXIAO_RELEASE_URL\" \
    -PYUNXIAO_USERNAME=\"$YUNXIAO_USERNAME\" \
    -PYUNXIAO_PASSWORD=\"$YUNXIAO_PASSWORD\" \
    -PprojectVersion=\"$FINAL_VERSION\""

  # Execute with retry
  if execute_with_retry "$gradle_cmd"; then
    return 0
  else
    return 1
  fi
}

#===========================================
# Help Function
#===========================================

show_help() {
  cat << EOF
${CYAN}Nexora Deployment Script${NC}

${YELLOW}Usage:${NC}
  $0 [OPTIONS]

${YELLOW}Options:${NC}
  -t, --type TYPE          Deployment type: snapshot or release (default: snapshot)
  -v, --version VERSION    Override version (default: use gradle.properties)
  -y, --yes                Skip confirmation prompt
  -d, --dry-run            Show what would be deployed without actual deployment
  -r, --retry N            Maximum retry attempts (default: 3)
  --no-rollback            Disable automatic rollback on failure
  --debug                  Enable debug logging
  -h, --help               Show this help message

${YELLOW}Examples:${NC}
  $0                       # Deploy snapshot (with confirmation)
  $0 -t release            # Deploy release version
  $0 -t release -v 1.0.1   # Deploy specific version
  $0 -y                    # Deploy snapshot without confirmation
  $0 -d                    # Dry run - show what would be deployed
  $0 --debug               # Deploy with debug logging

${YELLOW}Credentials Configuration:${NC}
  Add to ~/.gradle/gradle.properties:
    ${GRAY}# Recommended format (matches CI)${NC}
    yunxiaoUsername=your_username
    yunxiaoPassword=your_password
    yunxiaoSnapshotRepositoryUrl=https://packages.aliyun.com/maven/repository/xxx-snapshot/
    yunxiaoReleaseRepositoryUrl=https://packages.aliyun.com/maven/repository/xxx-release/

    ${GRAY}# Legacy format (still supported)${NC}
    codeupUsername=your_username
    codeupPassword=your_password

  Or set environment variables:
    export YUNXIAO_USERNAME=your_username
    export YUNXIAO_PASSWORD=your_password
    export YUNXIAO_SNAPSHOT_URL=https://...
    export YUNXIAO_RELEASE_URL=https://...

${YELLOW}Features:${NC}
  • Retry logic with exponential backoff
  • Automatic rollback on failure
  • Dry-run mode for testing
  • Progress indicators
  • Detailed logging

${YELLOW}Version:${NC} 2.0.0
${YELLOW}Author:${NC} Nexora Team
EOF
}

#===========================================
# Main Deployment Flow
#===========================================

main() {
  local backup_file=""

  # Parse arguments
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
    -d | --dry-run)
      DRY_RUN=true
      shift
      ;;
    -r | --retry)
      MAX_RETRIES="$2"
      shift 2
      ;;
    --no-rollback)
      ROLLBACK_ON_FAILURE=false
      shift
      ;;
    --debug)
      DEBUG=true
      shift
      ;;
    -h | --help)
      show_help
      exit 0
      ;;
    *)
      log_error "Unknown option: $1. Use -h for help."
      exit 1
      ;;
    esac
  done

  # Print banner
  echo -e "${MAGENTA}╔══════════════════════════════════════════════════════════╗${NC}"
  echo -e "${MAGENTA}║           Nexora Deployment Script v2.0.0              ║${NC}"
  echo -e "${MAGENTA}╚══════════════════════════════════════════════════════════╝${NC}"
  echo ""

  # Load and validate credentials
  load_credentials
  validate_credentials || exit 1
  validate_environment || exit 1

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

  # Dry run mode
  if [[ "$DRY_RUN" == true ]]; then
    dry_run_deployment
    exit 0
  fi

  # Show deployment summary
  log_info "Deployment Configuration"
  echo ""
  echo -e "${BLUE}  Project:${NC}  $GROUP"
  echo -e "${BLUE}  Version:${NC}  $FINAL_VERSION"
  echo -e "${BLUE}  Type:${NC}     $REPO_NAME"
  echo -e "${BLUE}  URL:${NC}      $REPO_URL"
  echo -e "${BLUE}  Max Retries:${NC} $MAX_RETRIES"
  echo ""

  # Confirm deployment
  if [[ "${SKIP_CONFIRM:-false}" == false ]]; then
    read -p "Continue with deployment? [y/N] " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
      log_warn "Deployment aborted by user"
      exit 0
    fi
  fi

  # Create backup for rollback
  if [[ "$ROLLBACK_ON_FAILURE" == true ]]; then
    backup_file=$(create_backup)
  fi

  # Execute deployment
  echo ""
  if execute_deployment; then
    echo ""
    log_success "✅ Deployment completed successfully!"
    echo ""
    echo -e "${GREEN}  Version:${NC}     $FINAL_VERSION"
    echo -e "${GREEN}  Repository:${NC}  $REPO_NAME"
    echo -e "${GREEN}  URL:${NC}         $REPO_URL"
    echo ""

    # Clean up old backups (keep last 5)
    if [[ -d "$BACKUP_DIR" ]]; then
      ls -t "$BACKUP_DIR"/deploy_*.tar.gz 2>/dev/null | tail -n +6 | xargs rm -f 2>/dev/null || true
    fi

    exit 0
  else
    echo ""
    log_error "❌ Deployment failed!"

    # Rollback if enabled
    if [[ "$ROLLBACK_ON_FAILURE" == true ]] && [[ -n "$backup_file" ]]; then
      echo ""
      log_step "Attempting rollback..."
      if rollback_deployment "$backup_file"; then
        log_success "Rollback completed"
      else
        log_error "Rollback failed. Manual intervention may be required."
      fi
    fi

    exit 1
  fi
}

# Run main
main "$@"
