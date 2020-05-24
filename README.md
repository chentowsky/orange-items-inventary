# Orange Items Inventory Application

## Requierements
_(Spanish)_
Gestión de inventario y stock
La compañía planea ofrecer dos nuevos servicios a los clientes. Estos servicios requieren para su correcto funcionamiento la reserva de routers y teléfonos móviles, en adelante, items. Por este motivo la compañía tiene la necesidad de diseñar un nuevo dominio para la gestión del inventario y el stock. El dominio debe ofrecer las siguientes capacidades:

- [X] Añadir stock para cualquiera de estos items.
- [X] Gestionar el stock de los items. Necesitamos conocer cuántos items de cada tipo tenemos disponibles.
- [X] Reservar N items de un mismo tipo permitiéndonos conocer a qué cliente está asociado.
- [X] Devolver N recursos de un mismo tipo al stock una vez el cliente deja de necesitarlo bien porque se ha dado de baja del servicio asociado o bien porque ha cancelado la compra.

La información que debemos almacenar en el stock para cada tipo de item es la siguiente:
* Para los routers solamente almacenamos su identificador que es un alfanumérico.
* Para los números de teléfono móvil guardamos la numeración. Por ejemplo, 687204053

Desde el punto de vista de negocio se valora que a futuro sea sencillo añadir más tipos de items sin tener que realizar cambios en el modelo.

Desde el punto de vista técnico se valorará prioritariamente que:
* Existan como interfaces de entrada REST y mensajería asíncrona(Kafka, Rabbit).
* Los contratos estén correctamente especificados.
* El dominio emita eventos anunciando las acciones que ha realizado para que otros dominios puedan saber qué está pasando.
* La gestión de la reserva en escenarios de concurrencia.
* La estructura de la paquetería esté razonada.
* El testing de la solución.

Se valorará positivamente pero no es necesario que:
* El servicio se pueda desplegar como un contenedor.
* Exista un README de la solución.
* Integración continua.
* Despliegue automático.
* Servicio desplegado y publicado hacia internet.

Como lenguaje de programación es preferible el uso de java o go. Una vez la prueba esté implementada y publicada en el repositorio de código de tu elección necesitamos que nos compartas el enlace para realizar la revisión en dos fases. Una primera fase interna y una segunda fase de revisión conjunta. 

## Modules
### Schema
We use Apache Avro for define the domain schemas, and use the avro maven plugin for Schema generation, for futher information: [Apache Avro documentation](https://avro.apache.org/docs/current/index.html)
To build first time schema just run
`mvn generate-resources` 
### Inventory Manager
Has simple API's for accepting inventory updates (add stock, reservations by item type and releases items by client)
### Inventory Calculator
This is a stream processor service that computes available inventory by item type and reserved items by client.
### Integration test
Separated project for run integration testing on the integrated project. First start docker containers with docker-compose and run `mvn test` on this project.
 
## Docker 
This project can be run with docker-compose for demo proposal. Just, first build the complete project with `mvn clean install` command, and after that use the `docker-compose up --build` command to run the entire project

## Endpoints

* Add items: POST http://localhost:9090/api/inventory/items/add and example body content:
```
{
  "items" : [
    {
      "id" : "684512345", 
      "type":  "movil"
    },
    {
      "id" : "684512555", 
      "type":  "movil"
    },
    {
      "id" : "584512345", 
      "type":  "movil"
    },
    {
      "id" : "884512555", 
      "type":  "movil"
    }
    
     ]
}
```
* Request reserve items: POST http://localhost:9090/api/inventory/items/reserve and example body content:
```
{
  "clientId" : "X9877299Z", 
  "type":  "router",
  "quantity": "20"
}
```

* Get available stock: GET http://192.168.99.100:9091/api/inventory/types/{type}

 
