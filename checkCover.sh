#!/usr/bin/env bash

set -e

FILE="swagger-coverage-results.json"

if [ ! -f "$FILE" ]; then
  echo "❌ ERROR: swagger-coverage-results.json not found!"
  exit 1
fi

echo "➡️ Calculating API coverage based on $FILE ..."

# total endpoints
TOTAL=$(jq '.groups | length' "$FILE")

# FULL = 100%
FULL=$(jq '[.groups[] | select(.status == "FULL")] | length' "$FILE")

# PARTY = 50%
PARTY=$(jq '[.groups[] | select(.status == "PARTY")] | length' "$FILE")

# formula: FULL*1 + PARTY*0.5
COVERAGE=$(echo "scale=2; ($FULL + $PARTY * 0.5) / $TOTAL * 100" | bc)

echo "📊 API Coverage: $COVERAGE%"
echo "$COVERAGE" > coverage-percent.txt

# quality gate
LOW=$(echo "$COVERAGE < 50" | bc)

if [ "$LOW" -eq 1 ]; then
  echo "❌ API coverage is below 50% — failing pipeline."
  exit 1
fi

echo "✅ API coverage is OK: $COVERAGE%"