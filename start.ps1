# Script de inicialização do Compraê Config Server para Windows
# Este script facilita a configuração e execução do ambiente de desenvolvimento

param(
    [string]$Action = "menu"
)

# Configuração de cores
$Host.UI.RawUI.WindowTitle = "Compraê Config Server"

function Write-ColorText {
    param(
        [string]$Text,
        [ConsoleColor]$Color = [ConsoleColor]::White
    )
    Write-Host $Text -ForegroundColor $Color
}

function Write-Status {
    param([string]$Message)
    Write-ColorText "[INFO] $Message" -Color Cyan
}

function Write-Success {
    param([string]$Message)
    Write-ColorText "[SUCCESS] $Message" -Color Green
}

function Write-Warning {
    param([string]$Message)
    Write-ColorText "[WARNING] $Message" -Color Yellow
}

function Write-Error {
    param([string]$Message)
    Write-ColorText "[ERROR] $Message" -Color Red
}

# Verificar se Docker está instalado
function Test-Docker {
    try {
        $dockerVersion = docker --version 2>$null
        $composeVersion = docker-compose --version 2>$null
        
        if ($dockerVersion -and $composeVersion) {
            Write-Success "Docker e Docker Compose estão instalados"
            return $true
        } else {
            Write-Error "Docker ou Docker Compose não estão instalados"
            return $false
        }
    } catch {
        Write-Error "Erro ao verificar Docker: $_"
        return $false
    }
}

# Verificar se Java e Maven estão instalados
function Test-JavaMaven {
    try {
        $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { 
            if ($_ -match '"(\d+)') { 
                [int]$matches[1] 
            }
        }
        
        if ($javaVersion -ge 17) {
            Write-Success "Java $javaVersion encontrado"
        } else {
            Write-Warning "Java 17+ recomendado, encontrado: Java $javaVersion"
        }
    } catch {
        Write-Warning "Java não encontrado. Necessário para desenvolvimento local."
    }
    
    try {
        $mvnVersion = mvn --version 2>$null
        if ($mvnVersion) {
            Write-Success "Maven encontrado"
        }
    } catch {
        Write-Warning "Maven não encontrado. Necessário para desenvolvimento local."
    }
}

# Parar serviços existentes
function Stop-Services {
    Write-Status "Parando serviços existentes..."
    try {
        docker-compose down --remove-orphans
        Write-Success "Serviços parados"
    } catch {
        Write-Error "Erro ao parar serviços: $_"
    }
}

# Construir a aplicação
function Build-Application {
    Write-Status "Construindo aplicação..."
    try {
        Set-Location "config-server"
        
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            & ".\mvnw.cmd" clean package -DskipTests
            Write-Success "Aplicação construída com sucesso"
        } else {
            Write-Warning "Maven não disponível, usando build do Docker"
        }
        
        Set-Location ".."
    } catch {
        Write-Error "Erro ao construir aplicação: $_"
        Set-Location ".."
    }
}

# Iniciar infraestrutura
function Start-Infrastructure {
    Write-Status "Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..."
    try {
        docker-compose up -d postgres redis zookeeper kafka
        
        Write-Status "Aguardando serviços ficarem prontos..."
        Start-Sleep -Seconds 30
        
        # Verificar PostgreSQL
        $maxAttempts = 30
        for ($i = 1; $i -le $maxAttempts; $i++) {
            try {
                $result = docker-compose exec -T postgres pg_isready -U configuser -d configdb 2>$null
                if ($LASTEXITCODE -eq 0) {
                    Write-Success "PostgreSQL está pronto"
                    break
                }
            } catch { }
            
            if ($i -eq $maxAttempts) {
                Write-Error "PostgreSQL não ficou pronto a tempo"
                return $false
            }
            Start-Sleep -Seconds 2
        }
        
        # Verificar Redis
        try {
            $redisResult = docker-compose exec -T redis redis-cli ping 2>$null
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Redis está pronto"
            }
        } catch {
            Write-Warning "Redis pode não estar completamente pronto"
        }
        
        Write-Success "Infraestrutura inicializada"
        return $true
    } catch {
        Write-Error "Erro ao iniciar infraestrutura: $_"
        return $false
    }
}

# Iniciar aplicação
function Start-Application {
    Write-Status "Iniciando Config Server..."
    try {
        docker-compose up -d config-server
        
        Write-Status "Aguardando aplicação ficar pronta..."
        $maxAttempts = 60
        for ($i = 1; $i -le $maxAttempts; $i++) {
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 2>$null
                if ($response.StatusCode -eq 200) {
                    Write-Success "Config Server está rodando!"
                    Write-Success "Health Check: http://localhost:8080/actuator/health"
                    Write-Success "API Docs: http://localhost:8080/swagger-ui.html"
                    return $true
                }
            } catch { }
            
            if ($i -eq $maxAttempts) {
                Write-Error "Config Server não ficou pronto a tempo"
                Write-Status "Verificando logs..."
                docker-compose logs config-server
                return $false
            }
            Start-Sleep -Seconds 2
        }
    } catch {
        Write-Error "Erro ao iniciar aplicação: $_"
        return $false
    }
}

# Iniciar monitoramento
function Start-Monitoring {
    Write-Status "Iniciando monitoramento (Prometheus + Grafana)..."
    try {
        docker-compose --profile monitoring up -d prometheus grafana
        Start-Sleep -Seconds 10
        
        Write-Success "Monitoramento disponível:"
        Write-Success "  - Prometheus: http://localhost:9090"
        Write-Success "  - Grafana: http://localhost:3000 (admin/admin123)"
    } catch {
        Write-Error "Erro ao iniciar monitoramento: $_"
    }
}

# Mostrar status dos serviços
function Show-Status {
    Write-Status "Status dos serviços:"
    docker-compose ps
    
    Write-Host ""
    Write-Status "URLs úteis:"
    Write-Host "  🔗 Config Server: http://localhost:8080" -ForegroundColor White
    Write-Host "  📚 API Documentation: http://localhost:8080/swagger-ui.html" -ForegroundColor White
    Write-Host "  ❤️  Health Check: http://localhost:8080/actuator/health" -ForegroundColor White
    Write-Host "  📊 Metrics: http://localhost:8080/actuator/metrics" -ForegroundColor White
    Write-Host "  🗄️  PostgreSQL: localhost:5432 (configuser/configpass)" -ForegroundColor White
    Write-Host "  🚀 Redis: localhost:6379" -ForegroundColor White
    Write-Host "  📨 Kafka: localhost:9092" -ForegroundColor White
    
    $psOutput = docker-compose ps
    if ($psOutput -match "prometheus") {
        Write-Host "  📈 Prometheus: http://localhost:9090" -ForegroundColor White
    }
    
    if ($psOutput -match "grafana") {
        Write-Host "  📊 Grafana: http://localhost:3000 (admin/admin123)" -ForegroundColor White
    }
}

# Executar testes
function Invoke-Tests {
    Write-Status "Executando testes..."
    try {
        Set-Location "config-server"
        
        if (Get-Command mvn -ErrorAction SilentlyContinue) {
            & ".\mvnw.cmd" test
            Write-Success "Testes executados com sucesso"
        } else {
            Write-Error "Maven não disponível para executar testes"
        }
        
        Set-Location ".."
    } catch {
        Write-Error "Erro ao executar testes: $_"
        Set-Location ".."
    }
}

# Limpar ambiente
function Clear-Environment {
    Write-Status "Limpando ambiente..."
    try {
        docker-compose down --volumes --remove-orphans
        docker system prune -f
        Write-Success "Ambiente limpo"
    } catch {
        Write-Error "Erro ao limpar ambiente: $_"
    }
}

# Menu principal
function Show-Menu {
    Clear-Host
    Write-Host ""
    Write-ColorText "===================================" -Color Magenta
    Write-ColorText "🛠️  Compraê Config Server" -Color Magenta
    Write-ColorText "===================================" -Color Magenta
    Write-Host "1. 🚀 Iniciar ambiente completo"
    Write-Host "2. 🏗️  Iniciar apenas infraestrutura"
    Write-Host "3. 🖥️  Iniciar apenas aplicação"
    Write-Host "4. 📊 Iniciar com monitoramento"
    Write-Host "5. 🧪 Executar testes"
    Write-Host "6. 📋 Mostrar status"
    Write-Host "7. 🛑 Parar serviços"
    Write-Host "8. 🧹 Limpar ambiente"
    Write-Host "9. ❌ Sair"
    Write-ColorText "===================================" -Color Magenta
}

# Loop principal
function Start-MainLoop {
    if (-not (Test-Docker)) {
        return
    }
    
    Test-JavaMaven
    
    while ($true) {
        Show-Menu
        $option = Read-Host "Escolha uma opção [1-9]"
        
        switch ($option) {
            "1" {
                Stop-Services
                Build-Application
                if (Start-Infrastructure) {
                    Start-Application
                }
                Show-Status
            }
            "2" {
                Start-Infrastructure
                Show-Status
            }
            "3" {
                Build-Application
                Start-Application
                Show-Status
            }
            "4" {
                Stop-Services
                Build-Application
                if (Start-Infrastructure) {
                    if (Start-Application) {
                        Start-Monitoring
                    }
                }
                Show-Status
            }
            "5" {
                Invoke-Tests
            }
            "6" {
                Show-Status
            }
            "7" {
                Stop-Services
            }
            "8" {
                Clear-Environment
            }
            "9" {
                Write-Success "Saindo..."
                return
            }
            default {
                Write-Error "Opção inválida!"
            }
        }
        
        Write-Host ""
        Read-Host "Pressione Enter para continuar..."
    }
}

# Executar ação baseada no parâmetro
switch ($Action.ToLower()) {
    "menu" { Start-MainLoop }
    "start" { 
        if (Test-Docker) {
            Stop-Services
            Build-Application
            if (Start-Infrastructure) {
                Start-Application
            }
            Show-Status
        }
    }
    "stop" { Stop-Services }
    "status" { Show-Status }
    "test" { Invoke-Tests }
    "clean" { Clear-Environment }
    default { 
        Write-Host "Uso: .\start.ps1 [-Action <menu|start|stop|status|test|clean>]"
        Write-Host "Exemplo: .\start.ps1 -Action start"
        Start-MainLoop
    }
}
