# Script para executar o Compraê Config Server
Write-Host "🚀 Iniciando Compraê Config Server..." -ForegroundColor Green

# Aguardar containers ficarem prontos
Write-Host "⏳ Aguardando infraestrutura ficar pronta..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Executar aplicação
Write-Host "▶️ Executando Config Server..." -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=docker

Write-Host "✅ Config Server finalizado!" -ForegroundColor Green
