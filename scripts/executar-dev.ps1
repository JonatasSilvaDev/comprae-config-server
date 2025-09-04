# 🔧 Script para Desenvolvimento Local (apenas infraestrutura no Docker)
# Usa Docker para infraestrutura e executa a aplicação localmente com Maven

Write-Host "🔧 Compraê Config Server - Modo Desenvolvimento" -ForegroundColor Blue
Write-Host "================================================" -ForegroundColor Blue
Write-Host ""

# Verificar Docker
Write-Host "🔍 Verificando Docker..." -ForegroundColor Cyan
try {
    $null = docker --version
    $null = docker-compose --version
    Write-Host "✅ Docker disponível" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não encontrado. Por favor, instale o Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar Java e Maven
Write-Host "🔍 Verificando Java e Maven..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { 
        if ($_ -match '"1\.(\d+)' -or $_ -match '"(\d+)') { 
            [int]$matches[1] 
        }
    } | Select-Object -First 1
    
    if ($javaVersion -ge 17) {
        Write-Host "✅ Java $javaVersion encontrado" -ForegroundColor Green
    } else {
        Write-Host "❌ Java 17+ necessário, encontrado: $javaVersion" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Java não encontrado" -ForegroundColor Red
    exit 1
}

try {
    $null = mvn --version 2>$null
    Write-Host "✅ Maven encontrado" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven não encontrado" -ForegroundColor Red
    exit 1
}


# Subir infraestrutura usando script auxiliar
Write-Host "🛑 Parando containers existentes..." -ForegroundColor Yellow
& "${PSScriptRoot}\infra.ps1" -Action down

Write-Host "🏗️ Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..." -ForegroundColor Cyan
& "${PSScriptRoot}\infra.ps1" -Action up

Write-Host ""
Write-Host "🚀 Infraestrutura pronta! Agora execute a aplicação:" -ForegroundColor Green
Write-Host ""
Write-Host "cd config-server" -ForegroundColor White
Write-Host ".\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "📊 Será disponível em:" -ForegroundColor Cyan
Write-Host "   • http://localhost:8080/actuator/health" -ForegroundColor Gray
Write-Host "   • http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
Write-Host ""
Write-Host "🛑 Para parar infraestrutura: .\scripts\infra.ps1 -Action down" -ForegroundColor Yellow

# Opcional: executar automaticamente
$choice = Read-Host "Deseja executar a aplicação automaticamente? (s/N)"
if ($choice -eq "s" -or $choice -eq "S") {
    Write-Host ""
    Write-Host "▶️ Executando aplicação..." -ForegroundColor Green
    Set-Location "config-server"
    .\mvnw.cmd spring-boot:run
}
