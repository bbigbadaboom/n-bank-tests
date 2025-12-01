#!/usr/bin/env bash
set -e

FILE="swagger-coverage-results.json"

echo "Reading swagger coverage results..."

TOTAL=$(jq '[.operations[].allConditionCount] | add' "$FILE")
COVERED=$(jq '[.operations[].coveredConditionCount] | add' "$FILE")

if [ -z "$TOTAL" ] || [ "$TOTAL" = "null" ]; then
  echo "Cannot read allConditionCount – wrong file format"
  exit 1
fi

if [ "$TOTAL" -eq 0 ]; then
  echo "total condition count = 0 — cannot compute coverage"
  exit 1
fi

COVERAGE=$(echo "scale=2; $COVERED / $TOTAL * 100" | bc)

echo "📊 API Coverage:"
echo "   Covered: $COVERED"
echo "   Total:   $TOTAL"
echo "   Percent: $COVERAGE%"

echo "$COVERAGE" > coverage-percent.txt

# quality gate threshold
THRESHOLD=50

LOW=$(echo "$COVERAGE < $THRESHOLD" | bc)

if [ "$LOW" -eq 1 ]; then
  echo "API coverage < $THRESHOLD% — failing pipeline"
  exit 1
fi

echo "✅ API coverage is OK"
exit 0
