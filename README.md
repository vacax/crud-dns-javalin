# CRUD DNS Javalin

Registro de DNS en DigitalOcean para solventar el problema de asignación de DNS 
para la clase ICC-354.

## Versiones:

- JDK 21.
- Javalin 6.1.0
- Gradle 8.5
- HTMX
- Bootstrap

## Arranque:

Es necesario instalar el cliente de DigitalOcean, 
pueden ver la documentación de su sistema operativa en el siguiente enlace: https://github.com/digitalocean/doctl.

Para el caso de Ubuntu, utilizar el siguiente comando:

```
sudo snap install doctl
```

Una vez instalado debe aplicar el comando para autenticar 
indicado su api-key generado en Digital Ocean:

```
doctl auth init
```

## Configuración:

Es necesario configurar las siguientes variables de ambientes para inicializar la aplicación:

- URL_MONGO -> Represent URL de conexion a Mongodb
- DB_NOMBRE -> Nombre de la base de datos en MongoDb
- CRUD_DNS_JAVALIN_DOMINIO -> Representa el dominio que estará gestionado