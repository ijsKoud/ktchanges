#!/bin/bash

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$SCRIPT_DIR/.."

echo "KtChanges Version Bump Test Script"
echo "==================================="
echo ""

# Find latest changeset file
CHANGESET_FILE=$(ls -t "$ROOT_DIR/.ktchanges"/*-changeset.yaml 2>/dev/null | head -1)

if [ -z "$CHANGESET_FILE" ]; then
    echo "❌ No changeset file found in .ktchanges directory"
    echo ""
    echo "First, run: ./gradlew ktchanges"
    exit 1
fi

echo "✅ Found changeset file: $CHANGESET_FILE"
echo ""
echo "📋 Changeset contents:"
cat "$CHANGESET_FILE"
echo ""

# Parse and apply version bumps
echo "🔄 Simulating version bump process..."
echo ""

TEMP_CHANGES="/tmp/version_changes.txt"
> "$TEMP_CHANGES"

while IFS=: read -r project bump_type; do
    project=$(echo "$project" | xargs)  # trim whitespace
    bump_type=$(echo "$bump_type" | xargs)  # trim whitespace

    # Skip empty lines or lines that don't have both project and bump_type
    if [ -z "$project" ] || [ -z "$bump_type" ]; then
        continue
    fi

    # Validate bump_type
    if [[ ! "$bump_type" =~ ^(major|minor|patch)$ ]]; then
        echo "⚠️  Invalid bump type '$bump_type' for project '$project'. Skipping."
        continue
    fi

    # Find gradle.properties for this project
    # Try multiple search strategies
    GRADLE_PROPS=""

    # Strategy 1: Direct path match
    if [ -f "$ROOT_DIR/$project/gradle.properties" ]; then
        GRADLE_PROPS="$ROOT_DIR/$project/gradle.properties"
    fi

    # Strategy 2: Find in subdirectories
    if [ -z "$GRADLE_PROPS" ]; then
        GRADLE_PROPS=$(find "$ROOT_DIR" -type f -name "gradle.properties" | grep "$project" | head -1)
    fi

    # Strategy 3: Find all gradle.properties and match project name
    if [ -z "$GRADLE_PROPS" ]; then
        # Extract project name from path (last component)
        PROJECT_NAME=$(basename "$project")
        GRADLE_PROPS=$(find "$ROOT_DIR" -type f -name "gradle.properties" | xargs grep -l "version=" | grep "$PROJECT_NAME" | head -1)
    fi

    if [ ! -f "$GRADLE_PROPS" ]; then
        echo "⚠️  gradle.properties not found for: $project"
        continue
    fi

    # Read current version
    CURRENT_VERSION=$(grep "^version=" "$GRADLE_PROPS" | cut -d'=' -f2)

    # Parse semantic version
    IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"
    PATCH=${PATCH%%-*}  # remove pre-release suffix if any

    # Bump version based on type
    case "$bump_type" in
        major)
            NEW_VERSION="$((MAJOR + 1)).0.0"
            ;;
        minor)
            NEW_VERSION="$MAJOR.$((MINOR + 1)).0"
            ;;
        patch)
            NEW_VERSION="$MAJOR.$MINOR.$((PATCH + 1))"
            ;;
        *)
            echo "❌ Unknown bump type: $bump_type"
            exit 1
            ;;
    esac

    echo "📝 $project"
    echo "   Current version: $CURRENT_VERSION"
    echo "   Bump type: $bump_type"
    echo "   New version: $NEW_VERSION"
    echo ""

    echo "$project: $CURRENT_VERSION -> $NEW_VERSION ($bump_type)" >> "$TEMP_CHANGES"
done < "$CHANGESET_FILE"

echo "==================================="
echo "✅ Version bump simulation complete!"
echo ""
echo "Changes that would be applied:"
echo "---"
cat "$TEMP_CHANGES" || echo "No changes to apply"
echo "---"
