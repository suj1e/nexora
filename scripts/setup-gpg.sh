#!/bin/bash
#
# GPG Key Generation Script for Maven Central Publishing
#
# Usage: ./scripts/setup-gpg.sh
#

set -e

echo "=== GPG Key Generation for Maven Central ==="
echo ""

# Check if GPG is installed
if ! command -v gpg &> /dev/null; then
    echo "Error: GPG is not installed. Please install it first."
    echo "  macOS: brew install gnupg"
    echo "  Ubuntu: sudo apt-get install gnupg"
    exit 1
fi

echo "This script will:"
echo "1. Generate a GPG key pair"
echo "2. Export the private key for GitHub Secrets"
echo "3. Upload the public key to key servers"
echo ""

read -p "Continue? [y/N] " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
fi

# Get user info
echo ""
echo "Please provide the following information:"
read -p "Your Name (e.g., SuJie): " USER_NAME
read -p "Your Email (e.g., your@email.com): " USER_EMAIL
read -s -p "GPG Passphrase (remember this!): " PASSPHRASE
echo ""
read -s -p "Confirm Passphrase: " PASSPHRASE_CONFIRM
echo ""

if [[ "$PASSPHRASE" != "$PASSPHRASE_CONFIRM" ]]; then
    echo "Error: Passphrases do not match!"
    exit 1
fi

# Generate GPG key
echo ""
echo "Generating GPG key..."

cat > /tmp/gpg-batch << EOF
%echo Generating GPG key
Key-Type: RSA
Key-Length: 4096
Subkey-Type: RSA
Subkey-Length: 4096
Name-Real: $USER_NAME
Name-Email: $USER_EMAIL
Expire-Date: 0
Passphrase: $PASSPHRASE
%commit
%echo Done
EOF

gpg --batch --gen-key /tmp/gpg-batch
rm /tmp/gpg-batch

# Get key ID
KEY_ID=$(gpg --list-keys --keyid-format SHORT "$USER_EMAIL" | grep "rsa4096" | awk '{print $3}')
echo ""
echo "Generated key ID: $KEY_ID"

# Export private key
echo ""
echo "Exporting private key..."
PRIVATE_KEY_FILE="private-key.asc"
gpg --armor --export-secret-keys "$KEY_ID" > "$PRIVATE_KEY_FILE"
echo "Private key exported to: $PRIVATE_KEY_FILE"

# Upload to key servers
echo ""
echo "Uploading public key to key servers..."
gpg --keyserver keys.openpgp.org --send-keys "$KEY_ID" || true
gpg --keyserver keyserver.ubuntu.com --send-keys "$KEY_ID" || true
gpg --keyserver pgp.mit.edu --send-keys "$KEY_ID" || true

echo ""
echo "=== Setup Complete ==="
echo ""
echo "Next steps:"
echo ""
echo "1. Register at https://central.sonatype.com/"
echo "   - Create namespace: io.github.suj1e"
echo "   - Verify your GitHub account"
echo ""
echo "2. Add GitHub Secrets to your repository:"
echo "   - OSSRH_USERNAME: Your Sonatype username"
echo "   - OSSRH_TOKEN: Your Sonatype token (generate in account settings)"
echo "   - GPG_PRIVATE_KEY: Content of $PRIVATE_KEY_FILE"
echo "   - GPG_PASSPHRASE: The passphrase you just set"
echo ""
echo "3. Create a release on GitHub to trigger publishing"
echo ""
echo "Keep your private key ($PRIVATE_KEY_FILE) secure!"
