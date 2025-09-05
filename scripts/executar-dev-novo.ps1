# Script para Desenvolvimento Local (apenas infraestrutura no Docker)
# Usa Docker para infraestrutura e executa a aplicacao localmente com Maven

Write-Host "Comprae Config Server - Modo Desenvolvimento" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue
Write-Host ""

# Verificar Docker
Write-Host "Verificando Docker..." -ForegroundColor Cyan
try {
    $null = docker --version
    $null = docker-compose --version
    Write-Host "Docker disponivel" -ForegroundColor Green
} catch {
    Write-Host "Docker nao encontrado. Por favor, instale o Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar Java e Maven
Write-Host "Verificando Java e Maven..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { 
        if ($_ -match '"1\.(\d+)' -or $_ -match '"(\d+)') { 
            [int]$matches[1] 
        }
    } | Select-Object -First 1
    
    if ($javaVersion -ge 17) {
        Write-Host "Java $javaVersion encontrado" -ForegroundColor Green
    } else {
        Write-Host "Java 17+ necessario, encontrado: $javaVersion" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Java nao encontrado" -ForegroundColor Red
    exit 1
}

try {
    $null = mvn --version 2>$null
    Write-Host "Maven encontrado" -ForegroundColor Green
} catch {
    Write-Host "Maven nao encontrado" -ForegroundColor Red
    exit 1
}

# Subir infraestrutura usando script auxiliar
Write-Host "Parando containers existentes..." -ForegroundColor Yellow
& "${PSScriptRoot}\infra.ps1" -Action down

Write-Host "Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..." -ForegroundColor Cyan
& "${PSScriptRoot}\infra.ps1" -Action up

Write-Host ""
Write-Host "Infraestrutura pronta! Agora execute a aplicacao:" -ForegroundColor Green
Write-Host ""
Write-Host "cd config-server" -ForegroundColor White
Write-Host ".\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "Sera disponivel em:" -ForegroundColor Cyan
Write-Host "   http://localhost:8080/actuator/health" -ForegroundColor Gray
Write-Host "   http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
Write-Host ""
Write-Host "Para parar infraestrutura: .\scripts\infra.ps1 -Action down" -ForegroundColor Yellow

# Opcional: executar automaticamente
$choice = Read-Host "Deseja executar a aplicacao automaticamente? (s/N)"
if ($choice -eq "s" -or $choice -eq "S") {
    Write-Host ""
    Write-Host "Executando aplicacao..." -ForegroundColor Green
    
    # Determinar o caminho correto para config-server
    $scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
    $configServerPath = Join-Path (Split-Path -Parent $scriptPath) "config-server"
    
    Write-Host "Navegando para: $configServerPath" -ForegroundColor Gray
    
    if (Test-Path $configServerPath) {
        Set-Location $configServerPath
        if (Test-Path ".\mvnw.cmd") {
            .\mvnw.cmd spring-boot:run
        } else {
            Write-Host "Erro: mvnw.cmd nao encontrado" -ForegroundColor Red
        }
    } else {
        Write-Host "Erro: pasta config-server nao encontrada" -ForegroundColor Red
    }
}
