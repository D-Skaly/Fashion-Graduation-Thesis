@echo off
cd /d %~dp0
echo Running from: %CD%
node node_modules\vitest\dist\cli.js run --root . %*