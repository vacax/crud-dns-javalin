#!/usr/bin/env bash

# Variable de ambientes.
export URL_MONGO=""
export DB_NOMBRE=""
export CRUD_DNS_JAVALIN_DOMINIO=""

# Levantando la aplicacion
./gradlew shadowjar && java -jar build/libs/crud-dns.jar > salida.txt 2> error.txt