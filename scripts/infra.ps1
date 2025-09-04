# Script auxiliar para infraestrutura do Compraê Config Server
# Centraliza validações e subida/parada da infraestrutura Docker

param(
    [ValidateSet("up", "down", "status")][string]$Action = "up"
)

function Test-Docker {
    try {
        $dockerVersion = docker --version 2>$null
        $composeVersion = docker-compose --version 2>$null
        if ($dockerVersion -and $composeVersion) {
            Write-Host "[SUCCESS] Docker e Docker Compose encontrados" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[ERROR] Docker ou Docker Compose não encontrados" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "[ERROR] Erro ao verificar Docker: $_" -ForegroundColor Red
        return $false
    }
}

function Test-JavaMaven {
    try {
        $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object {
            if ($_ -match '"1\.(\d+)' -or $_ -match '"(\d+)') {
                [int]$matches[1]
            }
        } | Select-Object -First 1
        if ($javaVersion -ge 17) {
            Write-Host "[SUCCESS] Java $javaVersion encontrado" -ForegroundColor Green
        } else {
            Write-Host "[ERROR] Java 17+ necessário, encontrado: $javaVersion" -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "[ERROR] Java não encontrado" -ForegroundColor Red
        exit 1
    }
    try {
        $null = mvn --version 2>$null
        Write-Host "[SUCCESS] Maven encontrado" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Maven não encontrado" -ForegroundColor Red
        exit 1
    }
}

function Infra-Up {
    Write-Host "[INFO] Subindo infraestrutura (PostgreSQL, Redis, Kafka, Zookeeper)..." -ForegroundColor Cyan
    docker-compose -f ../docker-compose.yml up -d postgres redis kafka zookeeper
    Write-Host "[INFO] Aguardando infraestrutura ficar pronta..." -ForegroundColor Yellow
    Start-Sleep -Seconds 15
    # Health check PostgreSQL
    $attempts = 0
    $maxAttempts = 30
    do {
        $attempts++
        try {
            $pgReady = docker exec config-server-postgres pg_isready -U configuser -d configdb 2>$null
            if ($pgReady -match "accepting connections") {
                Write-Host "[SUCCESS] PostgreSQL pronto" -ForegroundColor Green
                break
            }
        } catch {}
        if ($attempts -ge $maxAttempts) {
            Write-Host "[ERROR] PostgreSQL não ficou pronto" -ForegroundColor Red
            exit 1
        }
        Start-Sleep -Seconds 2
    } while ($true)
    Write-Host "[SUCCESS] Infraestrutura pronta!" -ForegroundColor Green
}

function Infra-Down {
    Write-Host "[INFO] Parando containers de infraestrutura..." -ForegroundColor Yellow
    docker-compose -f ../docker-compose.yml down 2>$null
    Write-Host "[SUCCESS] Infraestrutura parada." -ForegroundColor Green
}

function Infra-Status {
    Write-Host "[INFO] Status da infraestrutura:" -ForegroundColor Cyan
    docker-compose -f ../docker-compose.yml ps
}

# Execução principal
if (-not (Test-Docker)) { exit 1 }

switch ($Action) {
    "up"    { Infra-Up }
    "down"  { Infra-Down }
    "status"{ Infra-Status }
    default  { Write-Host "Ação inválida. Use: up, down ou status." -ForegroundColor Red; exit 1 }
}
