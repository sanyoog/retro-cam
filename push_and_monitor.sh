#!/bin/bash
set -e

cd /workspaces/retro-cam

echo "=== Checking git status ==="
git status --short

echo -e "\n=== Last commit ==="
git log --oneline -1

echo -e "\n=== Staging changes ==="
git add gradle.properties local.properties 2>/dev/null || git add gradle.properties

echo -e "\n=== Committing ==="
git diff --cached --quiet && echo "No changes to commit" || git commit -m "fix: Remove hardcoded Java path for CI compatibility"

echo -e "\n=== Pushing to main ==="
git push origin main

echo -e "\n=== Creating and pushing tag ==="
git tag -d v1.5.0-translucency-fixed 2>/dev/null || true
git tag v1.5.0-translucency-fixed
git push origin v1.5.0-translucency-fixed --force

echo -e "\n=== Waiting for build to start ==="
sleep 10

echo -e "\n=== Checking latest runs ==="
gh run list --limit 3 --json conclusion,name,headBranch,workflowName,createdAt,displayTitle --jq '.[] | "\(.conclusion // "IN_PROGRESS") - \(.displayTitle) [\(.headBranch)]"'

echo -e "\n=== Monitoring build ==="
gh run list --limit 1 --json databaseId --jq '.[0].databaseId' | xargs -I {} gh api "repos/sanyoog/retro-cam/actions/runs/{}" --jq '.status + " - " + .conclusion // "running"'
