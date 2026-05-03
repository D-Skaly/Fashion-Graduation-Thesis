@echo off
cd /d %~dp0
echo Running from: %CD%
node run-vitest.cjs run --root . %*