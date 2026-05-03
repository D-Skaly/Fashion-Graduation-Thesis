# Install MCP Servers Globally for Fashion E-Commerce Project
Write-Host "=== Bat dau cai dat MCP Servers toan cuc ===" -ForegroundColor Green

# Kiem tra dieu kien tien quyet
Write-Host "`n[1/6] Kiem tra Node.js..." -ForegroundColor Yellow
node --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "LOI: Node.js chua duoc cai dat!" -ForegroundColor Red
    exit 1
}

Write-Host "`n[2/6] Kiem tra Docker..." -ForegroundColor Yellow
docker --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "CANH BAO: Docker chua san sang" -ForegroundColor Yellow
}

# Cai dat npm global packages
Write-Host "`n[3/6] Cai dat PostgreSQL MCP Server..." -ForegroundColor Cyan
npm install -g @modelcontextprotocol/server-postgres

Write-Host "`n[4/6] Cai dat Redis MCP Server..." -ForegroundColor Cyan
npm install -g @modelcontextprotocol/server-redis

Write-Host "`n[5/6] Cai dat GitHub MCP Server..." -ForegroundColor Cyan
npm install -g @modelcontextprotocol/server-github

Write-Host "`n[6/6] Cai dat Docker MCP Server (community)..." -ForegroundColor Cyan
npm install -g mcp-docker-server

Write-Host "`n[7/6] Cai dat Filesystem MCP Server..." -ForegroundColor Cyan
npm install -g @modelcontextprotocol/server-filesystem

Write-Host "`n[8/6] Luu y: Fetch MCP Server can Python/pip..." -ForegroundColor Yellow
Write-Host "Chay: pip install mcp-server-fetch" -ForegroundColor Yellow

# Xac nhan cai dat
Write-Host "`n=== Xac nhan cai dat ===" -ForegroundColor Green
npm list -g --depth=0 | Select-String "modelcontextprotocol|mcp-docker"

Write-Host "`n=== Hoan tat cai dat MCP Servers ===" -ForegroundColor Green
Write-Host "`nCau hinh da duoc cap nhat tai: C:\Users\DELL\.config\kilo\kilo.json" -ForegroundColor Cyan
