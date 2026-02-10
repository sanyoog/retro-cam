#!/usr/bin/env python3
import subprocess
import sys
import time
import json

def run_cmd(cmd, check=True):
    """Run command and return output"""
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True, cwd='/workspaces/retro-cam')
    if check and result.returncode != 0:
        print(f"ERROR: {cmd}")
        print(f"STDERR: {result.stderr}")
        return None
    return result.stdout.strip()

print("🔍 Checking git status...")
status = run_cmd('git status --short')
print(status if status else "Working tree clean")

print("\n📝 Last commit:")
last_commit = run_cmd('git log --oneline -1')
print(last_commit)

print("\n➕ Staging changes...")
run_cmd('git add gradle.properties', check=False)
run_cmd('git add local.properties', check=False)

print("\n💾 Committing changes...")
commit_result = run_cmd('git commit -m "fix: Remove hardcoded Java path for CI compatibility"', check=False)
if commit_result:
    print(commit_result)
else:
    print("No changes to commit")

print("\n🚀 Pushing to main...")
push_result = run_cmd('git push origin main')
print(push_result if push_result else "Pushed successfully")

print("\n🏷️  Creating tag...")
run_cmd('git tag -d v1.5.0-translucency-fixed', check=False)
run_cmd('git tag v1.5.0-translucency-fixed')

print("\n📤 Pushing tag...")
tag_push = run_cmd('git push origin v1.5.0-translucency-fixed --force')
print(tag_push if tag_push else "Tag pushed successfully")

print("\n⏳ Waiting for GitHub Actions to start...")
for i in range(3):
    time.sleep(3)
    print(f"   Waiting... {(i+1)*3}s")

print("\n📊 Checking recent workflow runs...")
runs_json = run_cmd('gh run list --limit 5 --json conclusion,name,headBranch,workflowName,createdAt,displayTitle,databaseId,status')
if runs_json:
    runs = json.loads(runs_json)
    for run in runs:
        status = run.get('status', 'unknown')
        conclusion = run.get('conclusion', 'in_progress')
        branch = run.get('headBranch', 'unknown')
        title = run.get('displayTitle', 'unknown')
        run_id = run.get('databaseId', '')
        
        status_icon = "⏳" if status == "in_progress" else "✅" if conclusion == "success" else "❌"
        print(f"{status_icon} {title} [{branch}] - {status}")
        
        if status == "in_progress" and branch == "v1.5.0-translucency-fixed":
            print(f"\n🔍 Monitoring build {run_id}...")
            print("=" * 60)
            
            # Monitor this run
            max_checks = 30
            for check in range(max_checks):
                time.sleep(10)
                run_details = run_cmd(f'gh api repos/sanyoog/retro-cam/actions/runs/{run_id} --jq ".status,.conclusion"')
                if run_details:
                    lines = run_details.split('\n')
                    if len(lines) >= 2:
                        current_status = lines[0]
                        current_conclusion = lines[1]
                        
                        elapsed = (check + 1) * 10
                        print(f"[{elapsed}s] Status: {current_status} | Conclusion: {current_conclusion}")
                        
                        if current_status == "completed":
                            if current_conclusion == "success":
                                print("\n✅ BUILD SUCCESSFUL!")
                                sys.exit(0)
                            else:
                                print(f"\n❌ BUILD FAILED with conclusion: {current_conclusion}")
                                print("\n📋 Fetching error logs...")
                                logs = run_cmd(f'gh run view {run_id} --log-failed', check=False)
                                if logs:
                                    print(logs[:2000])  # First 2000 chars
                                sys.exit(1)
            
            print("\n⏰ Timeout reached. Check manually at:")
            print(f"   https://github.com/sanyoog/retro-cam/actions/runs/{run_id}")
            break

print("\n✅ Done! Check status at: https://github.com/sanyoog/retro-cam/actions")
