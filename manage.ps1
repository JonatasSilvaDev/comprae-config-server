# === COMPRAE CONFIG-SERVER - SCRIPT PRINCIPAL ===

param(
    [Parameter(Position=0)]
    [string]$Command = "help",
    
    [Parameter(Position=1)]
    [string]$Subcommand = ""
)

# Cores para output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"

function Show-Help {
    Write-Host "=== COMPRAE CONFIG-SERVER ===" -ForegroundColor Blue
    Write-Host "Uso: .\manage.ps1 [COMANDO]"
    Write-Host ""
    Write-Host "Comandos disponíveis:" -ForegroundColor Yellow
    Write-Host "  build       - Compila o projeto (Maven)" -ForegroundColor Green
    Write-Host "  docker      - Constrói imagem Docker" -ForegroundColor Green
    Write-Host "  dev         - Executa em modo desenvolvimento" -ForegroundColor Green
    Write-Host "  prod        - Executa em modo produção (Docker)" -ForegroundColor Green
    Write-Host "  infra       - Gerencia infraestrutura (up/down)" -ForegroundColor Green
    Write-Host "  clean       - Limpa arquivos de build" -ForegroundColor Green
    Write-Host "  test        - Executa testes" -ForegroundColor Green
    Write-Host "  help        - Mostra esta ajuda" -ForegroundColor Green
    Write-Host ""
    Write-Host "Exemplos:" -ForegroundColor Yellow
    Write-Host "  .\manage.ps1 build         # Compila o projeto"
    Write-Host "  .\manage.ps1 infra up      # Sobe infraestrutura"
    Write-Host "  .\manage.ps1 dev           # Executa em desenvolvimento"
}

# Navegar para o diretório do script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -Path $scriptDir

switch ($Command.ToLower()) {
    "build" {
        Write-Host "🔨 Compilando projeto..." -ForegroundColor Blue
        Set-Location -Path "config-server"
        & .\mvnw.cmd clean package -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Build concluído!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erro no build!" -ForegroundColor Red
            exit 1
        }
    }
    
    "docker" {
        Write-Host "🐳 Construindo imagem Docker..." -ForegroundColor Blue
        Set-Location -Path "config-server"
        docker build -t comprae/config-server:latest .
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Imagem Docker criada!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erro ao criar imagem Docker!" -ForegroundColor Red
            exit 1
        }
    }
    
    "dev" {
        Write-Host "🚀 Executando em modo desenvolvimento..." -ForegroundColor Blue
        Set-Location -Path "config-server"
        & .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
    }
    
    "prod" {
        Write-Host "🏭 Executando em modo produção..." -ForegroundColor Blue
        docker-compose up -d
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Serviços iniciados!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erro ao iniciar serviços!" -ForegroundColor Red
            exit 1
        }
    }
    
    "infra" {
        switch ($Subcommand.ToLower()) {
            "up" {
                Write-Host "🏗️ Subindo infraestrutura..." -ForegroundColor Blue
                docker-compose -f docker-compose.yml -f monitoring/docker-compose.monitoring.yml up -d
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "✅ Infraestrutura iniciada!" -ForegroundColor Green
                } else {
                    Write-Host "❌ Erro ao iniciar infraestrutura!" -ForegroundColor Red
                    exit 1
                }
            }
            
            "down" {
                Write-Host "🛑 Parando infraestrutura..." -ForegroundColor Yellow
                docker-compose -f docker-compose.yml -f monitoring/docker-compose.monitoring.yml down
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "✅ Infraestrutura parada!" -ForegroundColor Green
                } else {
                    Write-Host "❌ Erro ao parar infraestrutura!" -ForegroundColor Red
                    exit 1
                }
            }
            
            default {
                Write-Host "❌ Uso: .\manage.ps1 infra [up|down]" -ForegroundColor Red
                exit 1
            }
        }
    }
    
    "clean" {
        Write-Host "🧹 Limpando arquivos de build..." -ForegroundColor Blue
        Set-Location -Path "config-server"
        & .\mvnw.cmd clean
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Limpeza concluída!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erro na limpeza!" -ForegroundColor Red
            exit 1
        }
    }
    
    "test" {
        Write-Host "🧪 Executando testes..." -ForegroundColor Blue
        Set-Location -Path "config-server"
        & .\mvnw.cmd test
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Testes concluídos!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erro nos testes!" -ForegroundColor Red
            exit 1
        }
    }
    
    "help" {
        Show-Help
    }
    
    default {
        Write-Host "❌ Comando desconhecido: $Command" -ForegroundColor Red
        Show-Help
        exit 1
    }
}