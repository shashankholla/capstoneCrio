#!/usr/bin/env bash

CRIO_WORK_TREE="$(pwd)"
echo "$CRIO_WORK_TREE" >> /home/crio-user/.crio_user_dir.log

echo "Workspace setup complete."