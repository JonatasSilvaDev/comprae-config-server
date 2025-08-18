
# Script de build Maven para comprae-config-server

# 1. Navegar até o diretório do projeto em que o POM está localizado e Executar o comando Maven para limpar e empacotar o projeto
try {
    Push-Location "..\config-server"
    Write-Host "Navegando para o diretório comprae-config-server/config-server..." -ForegroundColor Blue
} catch {
    Write-Host "❌ Não foi possível navegar para o diretório comprae-config-server/config-server" -ForegroundColor Red
    exit 1
}
mvn clean package

# 3. Verificar se o build terminou
if ($LASTEXITCODE -eq 0) {
	Write-Host "Build Maven concluído com sucesso."
	$buildSuccess = $true
} else {
	Write-Host "Build Maven falhou."
	$buildSuccess = $false
}

# 4. Retornar valor booleano
return $buildSuccess

#imprimir o valor da variavel no console
Write-Host "O resultado do build Maven é: $buildSuccess"