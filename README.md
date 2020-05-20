# Orange Items Inventory Application

## Requierements
_(Spanish)_
Gestión de inventario y stock
La compañía planea ofrecer dos nuevos servicios a los clientes. Estos servicios requieren para su correcto funcionamiento la reserva de routers y teléfonos móviles, en adelante, items. Por este motivo la compañía tiene la necesidad de diseñar un nuevo dominio para la gestión del inventario y el stock. El dominio debe ofrecer las siguientes capacidades:

1. Añadir stock para cualquiera de estos items.
2. Gestionar el stock de los items. Necesitamos conocer cuántos items de cada tipo tenemos disponibles.
3. Reservar N items de un mismo tipo permitiéndonos conocer a qué cliente está asociado.
4. Devolver N recursos de un mismo tipo al stock una vez el cliente deja de necesitarlo bien porque se ha dado de baja del servicio asociado o bien porque ha cancelado la compra.

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