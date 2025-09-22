#!/bin/bash

# === COMPRAE CONFIG-SERVER - SCRIPT PRINCIPAL ===

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para mostrar ajuda
show_help() {
    echo -e "${BLUE}=== COMPRAE CONFIG-SERVER ===${NC}"
    echo -e "Uso: $0 [COMANDO]"
    echo ""
    echo -e "${YELLOW}Comandos disponíveis:${NC}"
    echo -e "  ${GREEN}build${NC}       - Compila o projeto (Maven)"
    echo -e "  ${GREEN}docker${NC}      - Constrói imagem Docker"
    echo -e "  ${GREEN}dev${NC}         - Executa em modo desenvolvimento"
    echo -e "  ${GREEN}prod${NC}        - Executa em modo produção (Docker)"
    echo -e "  ${GREEN}infra${NC}       - Gerencia infraestrutura (up/down)"
    echo -e "  ${GREEN}clean${NC}       - Limpa arquivos de build"
    echo -e "  ${GREEN}test${NC}        - Executa testes"
    echo -e "  ${GREEN}help${NC}        - Mostra esta ajuda"
    echo ""
    echo -e "${YELLOW}Exemplos:${NC}"
    echo -e "  $0 build         # Compila o projeto"
    echo -e "  $0 infra up      # Sobe infraestrutura"
    echo -e "  $0 dev           # Executa em desenvolvimento"
}

# Navegar para o diretório do script
cd "$(dirname "$0")/.."

case "${1:-help}" in
    "build")
        echo -e "${BLUE}🔨 Compilando projeto...${NC}"
        cd config-server
        ./mvnw clean package -DskipTests
        echo -e "${GREEN}✅ Build concluído!${NC}"
        ;;
    
    "docker")
        echo -e "${BLUE}🐳 Construindo imagem Docker...${NC}"
        cd config-server
        docker build -t comprae/config-server:latest .
        echo -e "${GREEN}✅ Imagem Docker criada!${NC}"
        ;;
    
    "dev")
        echo -e "${BLUE}🚀 Executando em modo desenvolvimento...${NC}"
        cd config-server
        ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
        ;;
    
    "prod")
        echo -e "${BLUE}🏭 Executando em modo produção...${NC}"
        docker-compose up -d
        echo -e "${GREEN}✅ Serviços iniciados!${NC}"
        ;;
    
    "infra")
        case "${2:-}" in
            "up")
                echo -e "${BLUE}🏗️ Subindo infraestrutura...${NC}"
                docker-compose -f docker-compose.yml -f monitoring/docker-compose.monitoring.yml up -d
                echo -e "${GREEN}✅ Infraestrutura iniciada!${NC}"
                ;;
            
            "down")
                echo -e "${YELLOW}🛑 Parando infraestrutura...${NC}"
                docker-compose -f docker-compose.yml -f monitoring/docker-compose.monitoring.yml down
                echo -e "${GREEN}✅ Infraestrutura parada!${NC}"
                ;;
            
            *)
                echo -e "${RED}❌ Uso: $0 infra [up|down]${NC}"
                exit 1
                ;;
        esac
        ;;
    
    "clean")
        echo -e "${BLUE}🧹 Limpando arquivos de build...${NC}"
        cd config-server
        ./mvnw clean
        echo -e "${GREEN}✅ Limpeza concluída!${NC}"
        ;;
    
    "test")
        echo -e "${BLUE}🧪 Executando testes...${NC}"
        cd config-server
        ./mvnw test
        echo -e "${GREEN}✅ Testes concluídos!${NC}"
        ;;
    
    "help")
        show_help
        ;;
    
    *)
        echo -e "${RED}❌ Comando desconhecido: $1${NC}"
        show_help
        exit 1
        ;;
esac