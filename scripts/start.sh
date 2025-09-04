#!/bin/bash

# Script de inicialização do Compraê Config Server
# Este script facilita a configuração e execução do ambiente de desenvolvimento

set -e

echo "🚀 Inicializando Compraê Config Server..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se Docker está instalado
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker não está instalado. Por favor, instale o Docker primeiro."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
        exit 1
    fi
    
    print_success "Docker e Docker Compose estão instalados"
}

# Verificar se Java e Maven estão instalados (para desenvolvimento local)
check_java_maven() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_success "Java $JAVA_VERSION encontrado"
        else
            print_warning "Java 17+ recomendado, encontrado: Java $JAVA_VERSION"
        fi
    else
        print_warning "Java não encontrado. Necessário para desenvolvimento local."
    fi
    
    if command -v mvn &> /dev/null; then
        print_success "Maven encontrado"
    else
        print_warning "Maven não encontrado. Necessário para desenvolvimento local."
    fi
}

# Parar serviços existentes
stop_services() {
    print_status "Parando serviços existentes..."
    docker-compose down --remove-orphans
    print_success "Serviços parados"
}

# Construir a aplicação
build_application() {
    print_status "Construindo aplicação..."
    cd config-server
    
    if command -v mvn &> /dev/null; then
        ./mvnw clean package -DskipTests
        print_success "Aplicação construída com sucesso"
    else
        print_warning "Maven não disponível, usando build do Docker"
    fi
    
    cd ..
}

# Iniciar infraestrutura
start_infrastructure() {
    print_status "Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..."
    docker-compose up -d postgres redis zookeeper kafka
    
    print_status "Aguardando serviços ficarem prontos..."
    sleep 30
    
    # Verificar se PostgreSQL está pronto
    for i in {1..30}; do
        if docker-compose exec -T postgres pg_isready -U configuser -d configdb &> /dev/null; then
            print_success "PostgreSQL está pronto"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "PostgreSQL não ficou pronto a tempo"
            exit 1
        fi
        sleep 2
    done
    
    # Verificar se Redis está pronto
    if docker-compose exec -T redis redis-cli ping &> /dev/null; then
        print_success "Redis está pronto"
    else
        print_warning "Redis pode não estar completamente pronto"
    fi
    
    print_success "Infraestrutura inicializada"
}

# Iniciar aplicação
start_application() {
    print_status "Iniciando Config Server..."
    docker-compose up -d config-server
    
    print_status "Aguardando aplicação ficar pronta..."
    for i in {1..60}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "Config Server está rodando!"
            print_success "Health Check: http://localhost:8080/actuator/health"
            print_success "API Docs: http://localhost:8080/swagger-ui.html"
            break
        fi
        if [ $i -eq 60 ]; then
            print_error "Config Server não ficou pronto a tempo"
            print_status "Verificando logs..."
            docker-compose logs config-server
            exit 1
        fi
        sleep 2
    done
}

# Iniciar monitoramento (opcional)
start_monitoring() {
    print_status "Iniciando monitoramento (Prometheus + Grafana)..."
    docker-compose --profile monitoring up -d prometheus grafana
    
    sleep 10
    print_success "Monitoramento disponível:"
    print_success "  - Prometheus: http://localhost:9090"
    print_success "  - Grafana: http://localhost:3000 (admin/admin123)"
}

# Mostrar status dos serviços
show_status() {
    print_status "Status dos serviços:"
    docker-compose ps
    
    echo ""
    print_status "URLs úteis:"
    echo "  🔗 Config Server: http://localhost:8080"
    echo "  📚 API Documentation: http://localhost:8080/swagger-ui.html"
    echo "  ❤️  Health Check: http://localhost:8080/actuator/health"
    echo "  📊 Metrics: http://localhost:8080/actuator/metrics"
    echo "  🗄️  PostgreSQL: localhost:5432 (configuser/configpass)"
    echo "  🚀 Redis: localhost:6379"
    echo "  📨 Kafka: localhost:9092"
    
    if docker-compose ps | grep prometheus > /dev/null; then
        echo "  📈 Prometheus: http://localhost:9090"
    fi
    
    if docker-compose ps | grep grafana > /dev/null; then
        echo "  📊 Grafana: http://localhost:3000 (admin/admin123)"
    fi
}

# Executar testes
run_tests() {
    print_status "Executando testes..."
    cd config-server
    
    if command -v mvn &> /dev/null; then
        ./mvnw test
        print_success "Testes executados com sucesso"
    else
        print_error "Maven não disponível para executar testes"
    fi
    
    cd ..
}

# Limpar ambiente
cleanup() {
    print_status "Limpando ambiente..."
    docker-compose down --volumes --remove-orphans
    docker system prune -f
    print_success "Ambiente limpo"
}

# Menu principal
show_menu() {
    echo ""
    echo "==================================="
    echo "🛠️  Compraê Config Server"
    echo "==================================="
    echo "1. 🚀 Iniciar ambiente completo"
    echo "2. 🏗️  Iniciar apenas infraestrutura"
    echo "3. 🖥️  Iniciar apenas aplicação"
    echo "4. 📊 Iniciar com monitoramento"
    echo "5. 🧪 Executar testes"
    echo "6. 📋 Mostrar status"
    echo "7. 🛑 Parar serviços"
    echo "8. 🧹 Limpar ambiente"
    echo "9. ❌ Sair"
    echo "==================================="
}

# Loop principal
main() {
    check_docker
    check_java_maven
    
    while true; do
        show_menu
        read -p "Escolha uma opção [1-9]: " option
        
        case $option in
            1)
                stop_services
                build_application
                start_infrastructure
                start_application
                show_status
                ;;
            2)
                start_infrastructure
                show_status
                ;;
            3)
                build_application
                start_application
                show_status
                ;;
            4)
                stop_services
                build_application
                start_infrastructure
                start_application
                start_monitoring
                show_status
                ;;
            5)
                run_tests
                ;;
            6)
                show_status
                ;;
            7)
                stop_services
                ;;
            8)
                cleanup
                ;;
            9)
                print_success "Saindo..."
                exit 0
                ;;
            *)
                print_error "Opção inválida!"
                ;;
        esac
        
        echo ""
        read -p "Pressione Enter para continuar..."
    done
}

# Executar se for chamado diretamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
