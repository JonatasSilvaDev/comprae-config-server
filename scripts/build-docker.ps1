# Script para build da imagem Docker do comprae-config-server

try {
    Push-Location "..\\config-server"
    Write-Host "Navegando para o diretório config-server..." -ForegroundColor Blue
} catch {
    Write-Host "❌ Não foi possível navegar para o diretório config-server" -ForegroundColor Red
    exit 1
}
docker build -t comprae/config-server:latest .

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Imagem comprae/config-server:latest criada com sucesso." -ForegroundColor Green
    $buildSuccess = $true
} else {
    Write-Host "❌ Falha ao criar a imagem comprae/config-server:latest." -ForegroundColor Red
    $buildSuccess = $false
}

# tambem deve retornar uma variavel $buildSuccess informando se o build foi bem-sucedido
return $buildSuccess

#imprimir o valor da variavel no console
Write-Host "O resultado do build Docker é: $buildSuccess"