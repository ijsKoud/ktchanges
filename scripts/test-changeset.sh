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
echo "---"
cat "$CHANGESET_FILE"
echo "---"
echo ""

# Parse and apply version bumps
echo "🔄 Simulating version bump process..."
echo ""

TEMP_CHANGES="/tmp/version_changes.txt"
> "$TEMP_CHANGES"

while IFS=: read -r project bump_type; do
    project=$(echo "$project" | xargs)  # trim whitespace
    bump_type=$(echo "$bump_type" | xargs)  # trim whitespace

    if [ -z "$project" ] || [ -z "$bump_type" ]; then
        continue
    fi

    # Find gradle.properties for this project
    GRADLE_PROPS=$(find "$ROOT_DIR" -path "*/$project/gradle.properties" -o -path "*${project}/gradle.properties" | head -1)

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

