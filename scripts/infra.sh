#!/bin/bash
# Script auxiliar para infraestrutura do Compraê Config Server (Linux/Mac)
# Centraliza validações e subida/parada da infraestrutura Docker

ACTION="${1:-up}"

function check_docker() {
    if ! command -v docker &>/dev/null || ! command -v docker-compose &>/dev/null; then
        echo "[ERROR] Docker ou Docker Compose não encontrados"; exit 1
    fi
    echo "[SUCCESS] Docker e Docker Compose encontrados"
}

function check_java_maven() {
    if command -v java &>/dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
        if [ "$JAVA_VERSION" -ge 17 ]; then
            echo "[SUCCESS] Java $JAVA_VERSION encontrado"
        else
            echo "[ERROR] Java 17+ necessário, encontrado: $JAVA_VERSION"; exit 1
        fi
    else
        echo "[ERROR] Java não encontrado"; exit 1
    fi
    if command -v mvn &>/dev/null; then
        echo "[SUCCESS] Maven encontrado"
    else
        echo "[ERROR] Maven não encontrado"; exit 1
    fi
}

function infra_up() {
    echo "[INFO] Subindo infraestrutura (PostgreSQL, Redis, Kafka, Zookeeper)..."
    docker-compose -f ../docker-compose.yml up -d postgres redis kafka zookeeper
    echo "[INFO] Aguardando infraestrutura ficar pronta..."
    sleep 15
    # Health check PostgreSQL
    for i in {1..30}; do
        if docker exec config-server-postgres pg_isready -U configuser -d configdb 2>/dev/null | grep 'accepting connections' >/dev/null; then
            echo "[SUCCESS] PostgreSQL pronto"; break
        fi
        if [ $i -eq 30 ]; then
            echo "[ERROR] PostgreSQL não ficou pronto"; exit 1
        fi
        sleep 2
    done
    echo "[SUCCESS] Infraestrutura pronta!"
}

function infra_down() {
    echo "[INFO] Parando containers de infraestrutura..."
    docker-compose -f ../docker-compose.yml down
    echo "[SUCCESS] Infraestrutura parada."
}

function infra_status() {
    echo "[INFO] Status da infraestrutura:"
    docker-compose -f ../docker-compose.yml ps
}

check_docker

case "$ACTION" in
    up) infra_up ;;
    down) infra_down ;;
    status) infra_status ;;
    *) echo "Ação inválida. Use: up, down ou status."; exit 1 ;;
esac
