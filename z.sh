#!/bin/bash
# Zellij session manager - create or attach session based on directory name

SESSION_NAME=$(basename "$(pwd)")

if zellij list-sessions 2>/dev/null | grep -q "^${SESSION_NAME}$"; then
    zellij attach "${SESSION_NAME}"
else
    zellij attach "${SESSION_NAME}" || zellij --session "${SESSION_NAME}"
fi
