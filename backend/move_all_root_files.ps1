# Script to move all root-level files to correct 4-tier structure
$base = 'd:\CodeFile\Fashion-Graduation-Thesis\backend\src\main\java\com\skaly\fashion_backend'

# Get all modules (directories with package-info.java at root)
$modules = Get-ChildItem $base -Directory | Where-Object { 
    Test-Path "$($_.FullName)\package-info.java" -PathType Leaf 
}

Write-Host "Found $($modules.Count) modules"

foreach ($module in $modules) {
    $modulePath = $module.FullName
    $moduleName = $module.Name
    
    # Get root-level Java files (not in domain/, application/, etc.)
    $rootFiles = Get-ChildItem $modulePath -File -Filter *.java | Where-Object { 
        $_.DirectoryName -eq $modulePath 
    }
    
    if ($rootFiles.Count -eq 0) { continue }
    
    Write-Host "`nProcessing $moduleName ($($rootFiles.Count) root files)"
    
    foreach ($file in $rootFiles) {
        $fileName = $file.Name
        
        # Determine target directory based on naming conventions
        if ($fileName -match '^(.*Controller|.*Dto)\.java$' -or $fileName -match '^package-info\.java$') {
            $targetDir = "$modulePath\interfaces"
        }
        elseif ($fileName -match '^(.*Repository|.*UseCase|.*Service|.*Gateway|.*Request|.*Response)\.java$' -and $fileName -notmatch '^ProductInventoryGatewayAdapter') {
            $targetDir = "$modulePath\application"
        }
        elseif ($fileName -match '^(.*Entity|.*Domain.*|.*Exception|.*Status|.*Method|.*Type|ChatMessage|ChatSession|OutboxEvent|Saga.*|CircuitBreaker|SoftDelete|.*Properties)\.java$' -or $fileName -match '^SizeRecommendationService\.java$') {
            $targetDir = "$modulePath\domain"
        }
        elseif ($fileName -match '^(.*Adapter|.*Config|.*Security|.*Interceptor|.*Jpa|.*Persistence|.*Auth.*)\.java$' -or $fileName -match '^FashionAssistantService\.java$' -or $fileName -match '^AiChatRateLimitInterceptor\.java$') {
            $targetDir = "$modulePath\infrastructure"
        }
        else {
            # Default: check if it's a common file
            if ($moduleName -eq 'common' -or $moduleName -eq 'events' -or $moduleName -eq 'audit' -or $moduleName -eq 'outbox' -or $moduleName -eq 'saga' -or $moduleName -eq 'resilience' -or $moduleName -eq 'storage' -or $moduleName -eq 'util') {
                $targetDir = "$modulePath\domain"
            } else {
                $targetDir = "$modulePath\application"
            }
        }
        
        # Create target directory if not exists
        if (-not (Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
            Write-Host "  Created: $targetDir"
        }
        
        # Move file
        try {
            Move-Item $file.FullName $targetDir -Force
            Write-Host "  Moved: $fileName -> $(Split-Path $targetDir -Leaf)"
        }
        catch {
            Write-Host "  ERROR moving $fileName : $_"
        }
    }
}

Write-Host "`n=== DONE: All root files moved ==="