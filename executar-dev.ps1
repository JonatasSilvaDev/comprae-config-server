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

# Parar containers existentes
Write-Host "🛑 Parando containers existentes..." -ForegroundColor Yellow
docker-compose down 2>$null

# Iniciar apenas infraestrutura
Write-Host "🏗️ Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..." -ForegroundColor Cyan
docker-compose up -d postgres redis kafka zookeeper

Write-Host "⏳ Aguardando infraestrutura ficar pronta..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Verificar se PostgreSQL está pronto
$attempts = 0
$maxAttempts = 30
do {
    $attempts++
    try {
        $pgReady = docker exec config-server-postgres pg_isready -U configuser -d configdb 2>$null
        if ($pgReady -match "accepting connections") {
            Write-Host "✅ PostgreSQL pronto" -ForegroundColor Green
            break
        }
    } catch { }
    
    if ($attempts -ge $maxAttempts) {
        Write-Host "❌ PostgreSQL não ficou pronto" -ForegroundColor Red
        exit 1
    }
    
    Start-Sleep -Seconds 2
} while ($true)

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
Write-Host "🛑 Para parar infraestrutura: docker-compose stop" -ForegroundColor Yellow

# Opcional: executar automaticamente
$choice = Read-Host "Deseja executar a aplicação automaticamente? (s/N)"
if ($choice -eq "s" -or $choice -eq "S") {
    Write-Host ""
    Write-Host "▶️ Executando aplicação..." -ForegroundColor Green
    Set-Location "config-server"
    .\mvnw.cmd spring-boot:run
}
