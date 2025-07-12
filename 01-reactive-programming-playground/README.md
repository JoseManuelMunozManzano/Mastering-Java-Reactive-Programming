# Reactive Programming Playground

Vemos:

- Mono
- Flux

# Mono

## Creación de proyecto

![alt Create Project](images/01-CreateProject.png).

Ver `pom.xml` y la configuración de logs `src/main/resources/logback.xml`.

## Implementación Publisher/Subscriber

En `src/java/com/jmunoz` creamos el package `sec01` y dentro, por temas de organización, los packages `publisher` y `subscriber`.

Dentro del package `subscriber` creamos la clase `SubscriberImpl`.

Dentro del package `publisher` creamos las clases `PublisherImpl` y `SubscriptionImpl`.

En la clase `Demo` tenemos una prueba.

Vamos a crear:

- Publisher
  - Da direcciones de email de clientes.
- Subscription
- Subscriber

Los Reactive Streams ya proveen un conjunto de interfaces y nosotros vamos a crear nuestra propia implementación.

Esto no se tiene que hacer en proyectos reales, ya que usaremos `Project Reactor`, que es una implementación de Reative Streams.

El objetivo es obtener una comprensión básica de como se hace, ver como se conectan, como se hacen peticiones, como trabajan, como se cancela...

### Testing

Ejecutar la clase `Demo`.

## Mono/Flux - Introducción

Comenzamos con `Project Reactor`.

Recordar que `Reactive Stream` es una especificación y `Project Reactor` es una librería que lo implementa (parecido a Hibernate, que es una implementación de la especificación JPA)

Sabemos que `Publisher` es una interfaz del flujo reactivo. La librería `Reactor` provee dos implementaciones diferentes, `Mono` y `Flux`.

![alt Mono-Flux](./images/02-Mono-Flux.png)

**Mono**

- Emite 0 o 1 item.
- Le sigue una señal onComplete / onError

Es decir, si el Publisher puede emitir 1 item, entonces es un caso de éxito, pero si no tiene data que entregar al subscriber obtendremos `onComplete` directamente sin ningún item (recordar que no es obligatorio que el publisher emite un item, puede llamar a onComplete)

Solo en caso de excepción, pasará el `throwable` al subscriber via el método `onError`.

**Flux**

- Emite 0, 1, ... N items.
- Le sigue una señal onComplete / onError

Flux puede ser un flujo infinito que nunca acabe, por ejemplo el precio de Bitcoin puede cambiar cada segundo, así que no se para de emitir su nuevo precio.

El publisher obtiene la data de fuentes como bases de datos, y se la pasa al subscriber cuando este se la pide.

El flujo (stream) puede completarse via método `onComplete` una vez que se haya emitido toda la data, o el subscriber puede cancelar cuando ya no necesite más data.

El publisher puede llamar al método `onError` si encuentra alguna excepción, cancelando la emisión de data.

## ¿Por qué necesitamos Mono?

Si tenemos `Flux`, ¿por qué necesitamos `Mono`?

La respuesta más sencilla es que `Mono` es muy conveniente cuando sabemos seguro que vamos a obtener solo 1 item del publisher.

Ejemplo usando `JpaRepository`:

```java
interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFirstname(String firstname);
    Optional<Customer> findById(Long id);
}
```

En esta petición `Optional<Customer> findById(long id);` sabemos que, si encuentra el id, obtendremos un registro y, si no lo encuentra, 0 registros. Usaremos `Mono`.

Sin embargo, en esta petición `List<Customer> findByFirstname(String firstname);` sabemos que vamos a obtener entre 0 y N registros, por lo que usaremos `Flux`.

El mismo ejemplo usando `ReactiveCrudRepository` queda, por tanto:

```java
interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Flux<Customer> findByFirstname(String firstname);
    Mono<Customer> findById(Long id);
}
```

Tanto `Mono` como `Flux` pueden manejar data de una forma no bloqueante, asíncrona. Las diferencias son:

- Flux
  - Flujo de mensajes.
  - Backpressure (Producer emite mucha data que el consumer no puede manejar)
  - Muchos métodos adicionales específicos para manejar el procesamiento del flujo.
- Mono
  - Sin flujos (no stream!)
  - Sin Backpressure.
  - Un publisher ligero.
  - Modelo de comunicación Request -> Response

## Stream Lazy Behaviour

En `src/java/com/jmunoz` creamos el package `sec02` y dentro creamos la clase:

- `Lec01LazyStream`
  - El objetivo de esta clase es entender que un flujo (stream) por defecto es lazy.
  - Hasta que el flujo no se conecta a una salida no se ejecuta ni se obtiene el resultado.
  - La programación reactiva es igual.
  - Hasta que no nos conectemos a un subscriber no se ejecuta nada ni obtendremos ningún resultado.

## Mono Just

Cómo crear un publisher Mono usando métodos Factory para soportar la base de código existente en el proyecto.

![alt Mono-Factory Methods](./images/03-Mono-FactoryMethods.png)

**¿Por qué se creó este método just()?**

En la programación reactiva, cualquier cosa puede ser un publisher o un subscriber.

Por ejemplo, si hacemos una petición a la BD, la BD es el publisher y nosotros somos el subscriber.

Si vamos a guardar un registro en BD, entonces nosotros somos el publisher y la BD será el subscriber.

Imaginemos este código:

```java
private static void save(Publisher<String> publisher) {
    
}
```

A veces necesitamos crear rápidamente un publisher para poder consumir ese método. De ahí la necesidad de que exista `just()`.

```java
save(Mono.just("José"));
```

Por tanto, si tenemos un valor en memoria y por cualquier razón necesitamos crear un publisher usando ese valor, sencillamente usaremos el método `just()` para convertirlo en un publisher.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec02MonoJust`
  - Nuestra primera clase 100% reactiva.
  - Just es un Factory Method.

## Mono Subscribe - Overloaded Methods

Como se ha comentado, el método `subscribe()` está bastante sobrecargado. En esta clase vamos a explorarlos.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec03MonoSubscribe`

## Creating Default Subscriber

Para incrementar nuestra productividad y no tener que estar creando subscriber (el consumer del ejemplo anterior) cada dos por tres, vamos a crear un subscriber genérico que utilizaremos en los ejemplos.

En `src/java/com/jmunoz` creamos el paquete `common` y dentro las clases:

- `DefaultSubscriberImpl`
  - Creación de un Subscriber genérico.
- `Util`
  - Contiene la creación de nuestro Subscriber por defecto y una demo del funcionamiento.

## Mono - Empty / Error

Vemos como crear un publisher que no va a dar data y que pasa un mensaje de error.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec04MonoEmptyError`

## On Error Dropped - Problem

Volver a ver la clase `Lec04MonoEmptyError`.

## Mono - From Supplier

En la programación reactiva, tenemos que ser lo más perezoso (lazy) posibles.

A veces, deberemos retrasar la ejecución y hacer el trabajo solo cuando se requiere. En caso contrario, si no se requiere, no se hace nada.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec05MonoFromSupplier`

## Mono - From Callable

Project Reactor soporta el tipo Supplier, pero también el tipo Callable, que también demora la ejecución (lazy).

Tanto Supplier como Callable son interfaces funcionales en Java, pero son dos interfaces diferentes.

Supplier no lanza excepciones, no tiene la excepción como parte de su firma de método. Puede lanzar Runtime Exception pero no Checked Exception.

Callable puede lanzar excepciones como parte de su firma de método

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec06MonoFromCallable`

## Mono - From Runnable

El caso de uso es el siguiente. Si necesitamos invocar un método y después enviar empty, entonces usaremos fromRunnable.

Igual que tenemos la diferencia entre Mono.just y Mono.fromSupplier, tenemos Mono.empty y Mono.fromRunnable.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec07MonoFromRunnable`

## Mono - From Future

Vamos a ver como convertir un CompletableFuture en un publisher Mono.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec08MonoFromFuture`

## Publisher - Create vs Execute

Esta es una clase para clarificar conceptos, en concreto, que no es lo mismo crear un publisher que ejecutarlo.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec09PublisherCreateVsExecution`

## Mono - Defer

Usaremos defer() para retrasar la creación de un publisher.

Aunque normalmente la creación de un objeto publisher es un proceso muy ligero, si en nuestro caso debe retrasarse incluso la creación del mismo, puede hacerse usando defer().

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec10MonoDefer`

## Data from Remote Service

Hasta ahora hemos discutido sobre varios métodos Factory que Mono provee para crear rápidamente un tipo Publisher.

Estas opciones serán muy útiles cuando queramos usar una librería, algún framework con métodos como estos, que aceptan un tipo Publisher:

![alt Métddos que aceptan un tipo Publisher](./images/04-Methods-AcceptsPublisherType.png)

En este ejemplo, nosotros seríamos los Publisher y el framework sería el subscriber.

Pero, ¿qué pasa si necesitamos data de un servicio remoto o una base de datos? Un ejemplo parecido al de la imagen de arriba no va a ser posible. Para operaciones de entrada/salida no podemos usarlo.

Para poder usar esto, necesitamos drivers adecuados o clientes adecuados para enviar la request.

Por ejemplo, si hablamos de HTTP, usaremos Spring WebFlux, que nos permitirá desarrollar microservicios reactivos más robustos.

O si hablamos de BD relacionales, usaremos R2DBC.

El caso es que para poder usar Spring WebFlux o R2DBC, tenemos que saber trabajar muy bien con la programación reactiva.

Para hacer los ejemplos más interesantes, haremos peticiones reales HTTP en vez de usar Faker o métodos privados estáticos.

Y, para ello, usaremos la dependencia Project Reactor Netty, que es capaz de mandar peticiones HTTP no bloqueantes.

## External Services

Usaremos un fichero jar para demostrar HTTP no bloqueante.

**How To Run**

- Ensure that you have Java 17 or anything above installed
- Use the jar `external-services.jar`
- Open terminal/command line and navigate to the path where you have the jar
- Run this command: `java -jar external-services.jar`
- It uses port 7070 by default: `http://localhost:7070`

En esta sección vamos a probar el endpoint `demo01`, donde tenemos que imaginar que es un microservicio Product Service, donde pasamos un productId y nos devolverá el nombre del producto.

Está hecho adrede para que la respuesta tarde un segundo en devolverse, más o menos parecido a como si fuera una respuesta HTTP real.

La idea es centrarnos en cómo enviar peticiones HTTP a la manera reactiva, no bloqueante.

Ya en el siguiente punto, vamos a usar Reactor Netty para enviar peticiones HTTP para un productId y vamos a obtener la respuesta.

## Non-Bloking IO Client

Ahora si, vamos a usar Project Reactor Netty para enviar peticiones HTTP al Product Service para obtener la información del producto.

No olvidar arrancar el proyecto `external-services.jar`.

Añadimos a nuestro pom la dependencia:

```xml
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty-core</artifactId>
</dependency>
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty-http</artifactId>
</dependency>
```

Estas dependencias son las mismas que usa Spring WebFlux por debajo, pero WebFlux simplifica mucho la serialización, deserialización..., mientras que Reactor Netty es de bajo nivel, lo que puede ser un poco confuso al principio.

Pero dejar claro que, en la vida real, usaremos Spring WebFlux, aunque en este curso nos centramos en aprender el core de la programación reactiva.

Creamos una clase abstracta usando Netty para enviar peticiones HTTP. Con esto facilitamos el curso, ya que va a haber muchas demos y podemos extender esa clase abstracta rápidamente para enviar una petición o recibir una respuesta.

En el package `common` crearemos la clase `AbstractHttpClient`.

En `src/java/com/jmunoz/sec02` creamos el package `client`, el cliente para HTTP, y dentro la clase:

- `ExternalServiceClient` que extiende `AbstractHttpClient`.

## Non-Bloking IO Demo

Este punto es continuación del anterior. Usando el cliente ya construido, vamos a enviar una petición HTTP, en concreto, pasaremos un productId y obtendremos el nombre del producto.

No olvidar arrancar el proyecto `external-services.jar`.

En `src/java/com/jmunoz/sec02` creamos la clase:

- `Lec11NonBlockingIO`

## Como funciona Event Loop

Vamos a ver como funciona IO no bloqueante (o event loop).

Como parte del event loop, tendremos 1 thread y una cola de salida. Cuando hacemos las peticiones a Reactor Netty, se añaden las tareas a una cola.

El event loop thread continuamente mira las tareas en esta cola. Si hay tareas que realizar, las haces, si no, espera ocioso.

Cuando hay tareas en la cola, coge la primera tarea y la envia al servicio remoto. Sabemos que el servicio remoto tarda 1sg en ejecutarse.

Pero el event loop no está ocioso esperando la respuesta (no espera 1sg), sino que cogerá la siguiente tarea y enviará la petición de nuevo al servicio remoto.

Así hasta completar la cantidad de tareas que tenga en la cola.

¿Qué respuesta nos llega antes? No lo sabemos, la que la red (no la programación reactiva ni Java, sino la red), por los motivos que sea, nos entregue primero. Es decir, no hay orden en las respuestas.

El Sistema Operativo notificará al thread que nos ha llegado una respuesta y esta respuesta va a una cola de entrada. Seguirán llegando respuestas que irán a esta cola de entrada y de ahí a nosotros.

Un solo thread es suficiente para enviar cientos de peticiones concurrentes.

![alt Non Blocking IO](./images/05-NonBlockingIO.png)

## ¿Por qué no deberíamos usar Block?

Vamos a ver en esta clase por qué no deberíamos usar el método `block()` en programación reactiva.

En este ejemplo que se puede añadir a `Lec11NonBlockingIO` vemos:

```java
for (int i = 1; i <= 5; i++) {
    var name = client.getProductName(i)
            .block();
}
```

`block()` nos devuelve el valor, es decir, en vez de obtener un Mono<String> nos devuelve directamente el String.

Para eso, lo que hace por detrás es subscribirse automáticamente al publisher.

Pero el problema es que bloquea el thread porque no continua hasta que no obtiene el valor. Es decir, las peticiones se vuelven secuenciales.

¿Cuándo podemos usar block()? En tests unitarios, porque ese código no va a producción.

También, algunos dicen que usando Virtual Threads es correcto usar block. Esto no es totalmente correcto, solo cuando no tratamos con Flux, ya que entonces perderíamos los beneficios de la programación reactiva.

## Ejercicio

En `src/java/com/jmunoz/sec02/assignment` creamos la interface `FileService` que cree el contrato:

- Leer fichero y devolver contenido.
- Crear fichero y escribir contenido.
- Borrar fichero.

Y su implementación `FileServiceImpl`.

También creamos la clase de prueba `Assignment`.

Suposiciones:

- Los ficheros son de tamaño muy pequeño (no obtendremos errores del tipo OutOfMemory).
- Los ficheros deben estar en `src/main/resources/sec02`.

Expectativas:

- Los métodos de FileService deberán hacer el trabajo solo cuando los subscribers se subscriban.
- Comunicar el error al subscriber en caso de problemas.
- No hay necesidad de ninguna librería especial.

## Resumen Mono

- La programación reactiva es un paradigma de programación para manejar operaciones IO más eficientemente.
- Reactive Streams es una especificación.
  - Reactor es una implementación.
- Mono & Flux
  - Mono - emite 0/1 item
    - Request -> Response
  - Flux - emite 0 ... infinito

# Flux

- Flux puede emitir 0, 1 ... N items.
- Seguido de onComplete / onError.

Puede ser un flujo de mensajes interminable, y, en este caso, nunca obtendremos un onComplete/onError.

Flux, al igual que Mono, provee factory methods para poder crearlo rápidamente a partir de data existente, como, por ejemplo:

- just
  - Muy útil cuando tenemos data en memoria.
- fromIterable
  - Si tenemos una lista o array, o un Java stream.
- fromArray
- fromStream

![alt Flux Factory Methods](./images/08-Flux-FactoryMethods.png)

Esta no es una lista exhaustiva, ya que hay más métodos avanzados para crear un Flux, y los veremos más adelante, pero estos son los más comunes.

## Flux - Just

Usaremos el factory method `Flux.just()` cuando tengamos data en memoria.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec01FluxJust`

## Flux - Multiple Subscribers

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec02MultipleSubscribers`
  - Vemos que un publisher puede tener varios subscribers y aplicamos filtros y map.

## Flux - From Array / List

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec03FluxFromIterableOrArray`
  - Vamos a ver como crear un publisher Flux a partir de un array o un iterable.

## Flux - From Stream

Esta clase es muy importante, ya que hay algo complicado que es la conversión de un Java Stream a un Flux.

Esto es que cuando el stream ya se ha consumido, no se puede volver a usar.

Para evitar el problema y poder tener más de un subscriber, debemos proveer el supplier, que nos va a dar el stream cada vez que se necesite.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec04FluxFromStream`
  - Vamos a ver como crear un publisher Flux a partir de un Java Stream.

## Flux - Range

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec05FluxRange`
  - Vamos a crear un publisher Flux a partir de un rango de números.
  - Podemos usarlo como un bucle for en programación reactiva.

## Log Operator

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec06Log`
  - El operador log nos permite ver lo que está pasando en el publisher, es decir, ver los items que se están emitiendo.
  - Es muy útil para debugging.

## Flux vs List

Vamos a implementar un requerimiento de negocio, generar nombres, de dos formas distintas, una usando Flux y otra usando List.

Este es el comportamiento:

![alt List vs Flux](./images/06-ListVsFlux.png)

- List: Genera todos los nombres y los devuelve en una lista. Si cada nombre tarda 1 segundo en generarse, tardará 10 segundos en devolver la lista completa.
- Flux: Genera los nombres uno a uno, de forma que cada nombre se devuelve en 1 segundo, pero el siguiente nombre se genera mientras el subscriber está procesando el anterior. Por tanto, al cabo de 10 segundos, ya tenemos todos los nombres generados.
  - El subscriber puede procesar los nombres a su ritmo, sin necesidad de esperar a que se generen todos y puede cancelar la emisión en cualquier momento.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec07FluxVsList`

Y en `src/java/com/jmunoz/sec03/helper` creamos la clase:

- `NameGenerator`
  - Genera nombres aleatorios.

## ¿Son Mono & Flux estructuras de datos?

- List<T>, Set<T>...
  - Son una representación de data en la memoria.
  - Almacenan data (finita).
- Flux<T>, Mono<T>
  - Representan un tunel/pipe a través del cual la data puede transferirse de un lugar a otro.
  - No almacenan data, la transmiten, por lo que puede ser infinita.

Es decir, Mono y Flux no son estructuras de datos, sino que son una representación de un flujo de data.

![alt Stream Of Data](./images/07-StreamOfData.png)

Donde App1 es el publisher y App2 es el subscriber.

## Flux - Non-Blocking IO Stream - Demo

Vamos a ver como podemos consumir el flujo de mensajes usando HTTP Reactor Netty.

- Arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
  - Usaremos el endpoint `demo02/name/stream` que genera nombres aleatorios cada 500ms.
    - Pulsamos Execute y esperamos. Veremos que tarda pero devuelve un 200 y una lista de nombres.
    - Veremos la siguiente Request URL: `http://localhost:7070/demo02/name/stream` y la copiamos y pegamos en el navegador.
    - Veremos que van saliendo los nombres (flujo de mensajes) uno a uno, cada 500ms.

¿Cómo puedo desarrollar una aplicación exponiendo una API con un flujo de mensajes? Esto lo veremos en el curso Spring WebFlux. Ahí se ve como desarrollar microservicios reactivos.

Ahora vamos a ver como consumir este flujo de mensajes usando Reactor Netty.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec08NonBlockingIOStreamingMessages`

En `src/java/com/jmunoz/sec03/client` creamos la clase:

- `ExternalServiceClient`
  - Enviamos una petición HTTP.

## Flux - Interval

La idea de Interval es que, en vez de tener una data y emitirla, lo que tenemos es un requerimiento de emitir data cada cierto tiempo, de forma no bloqueante.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec09FluxInterval`

## Flux - Empty / Error

Flux no tiene por qué estar emitiendo data todo el tiempo. Si no tiene nada que emitir, puede enviar empty o la señal onComplete directamente.

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec10FluxEmptyError`
  - Vamos a ver como crear un publisher que no va a dar data y que pasa un mensaje de error.

## Flux - Defer

Hasta ahora no hemos visto como retrasar la ejecución de un publisher Flux.

Usaremos el método `defer()` que acepta un Supplier que nos va a dar el publisher cada vez que se necesite.

Ejemplo:

```java
Flux.defer(() -> Flux.fromIterable(createList()));
```

Con esto terminamos de ver como crear un Flux cuando tenemos la data en memoria.

En otras secciones de este curso veremos como crear un Flux de formas más avanzadas. 

## Mono/Flux Conversion

En `src/java/com/jmunoz/sec03` creamos la clase:

- `Lec11FluxMono`
  - Vamos a ver como convertir un Mono en un Flux y viceversa.
  - Usaremos el método Flux.from() donde le pasamos un Mono y nos devuelve un Flux.
  - Usaremos el método Mono.from() donde le pasamos un Flux y nos devuelve un Mono, o el método next() para obtener el primer item del Flux.

## Ejercicio

- Arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
  - Usaremos el endpoint `demo02/stock/stream` que genera cambios de precios.
    - Pulsamos Execute y esperamos 20sg hasta que termine. Veremos que devuelve un 200 y una lista de números.
    - Veremos la siguiente Request URL: `http://localhost:7070/demo02/stock/stream` y la copiamos y pegamos en el navegador.
    - Veremos que van saliendo precios (flujo de mensajes) uno a uno, cada 500ms.

La tarea es la siguiente:

- Crear un subscriber con un balance inicial de $1000.
- Cuando el precio cae por debajo de 90, comprar una acción.
- Cuando el precio sube por encima de 110:
  - Vender todo.
  - Cancelar la suscripción.
  - Imprimir la ganancia obtenida.

En `src/java/com/jmunoz/sec03/assignment` creamos la clase:

- `Assignment`

En `src/java/com/jmunoz/sec03/client` creamos un método en la clase:

- `ExternalServiceClient`

## Resumen Flux

- Flux emite 0, 1, ..., N items.
- Seguido de onComplete / onError.
- Hemos visto Factory Methods para crear un Flux, pero no ha sido una lista exhaustiva.

# Flux - Emitir items programáticamente

En la sección anterior hemos visto como crear un Flux a partir de data existente, pero no hemos visto como emitir items programáticamente, por ejemplo emitir data hasta que se cumpla una condición.

## Flux Create

Para esto, Reactor provee algunas opciones que son las que vamos a ver en esta sección.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec01FluxCreate`
  - Vemos el método `Fkux.create()`

## Flux Create - Refactor

En la clase anterior vimos como añadir toda la lógica dentro de la expresión lambda del método `Flux.create()`.

Pero si hay mucha lógica, es mejor refactorizarla a un método/clase aparte.

En `src/java/com/jmunoz/sec04` creamos el package `helper` y dentro la clase:

- `NameGenerator`
  - Implementamos un consumer.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec02FluxCreateRefactor`
  - Vemos como refactorizar `Lec01FluxCreate` para que sea más limpio.

## Flux Sink - Thread Safety

Flux Sink es thread safe, es decir, podemos emitir items desde diferentes threads sin problemas.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec03FluxSinkThreadSafety`
  - Vemos como usar Flux Sink para emitir items desde diferentes threads.

## Flux Create - Comportamiento por defecto

Vamos a ver el comportamiento por defecto de `Flux.create()`, que puede no gustarnos, ya que no es lo que esperamos.

El producer produce los items por adelantado, sin esperar a que el subscriber haga un request.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec04FluxCreateDownstreamDemand`

## Flux Create - Emit on Demand

Vamos a cambiar el comportamiento por defecto de `Flux.create()` para que emita los items bajo demanda, es decir, cuando el subscriber haga un request.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec04bFluxCreateDownstreamDemand`

## Flux Sink - Casos de uso

Flux.create -> FluxSink:

- Diseñado para usarse cuando tenemos un solo subscriber.
- FluxSink es thread safe. Podemos compartirlo entre varios threads.
- Podemos mantener la emisión de items al sink sin tener que preocuparnos de una baja demanda.
- FluxSink entregará todo al subscriber de forma segura, secuencialmente, de uno en uno.
  - No hay problemas de race condition ya que se sincroniza internamente.

## Take Operators

Vamos a hablar del operador `take()`, que nos permite limitar el número de items que se emiten. Es muy parecido a `limit()` en Java Streams.

También hablaremos del operador `takeWhile()` que nos permite emitir items mientras se cumpla una condición.

También hablaremos del operador `takeUntil()`, que nos permite emitir items hasta que se cumpla una condición.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec05TakeOperator`
  - Vemos como usar el operador `take()` para limitar el número de items que se emiten.
  - Vemos como usar el operador `takeWhile()` para emitir items mientras se cumpla una condición.
  - Vemos como usar el operador `takeUntil()` para emitir items hasta que se cumpla una condición.

## Flux Generate

Mientras que `Flux.create()` acepta un Consumer de `FluxSync`, el método `Flux.generate()` acepta un Consumen de `SynchronousSink`.

La diferencia es que con `Flux.generate()` podemos emitir solo un item y que con `Flux.create()` obteníamos la request y nos permitía controlar el bucle para emitir items. Es decir es de más bajo nivel.

Con `Flux.generate()` tenemos que pensar que internamente existe ese bucle que obtiene el request de forma automática. Es de más alto nivel.

¿Cuál es la diferencia con un `Mono`? Un `Mono` permite emitir solo un item y se acabó, pero con `Flux.generate()` podemos usar un `SynchronousSink` para emitir un valor una y otra vez, mientras no se ejecute `complete()` o `error()`.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec06FluxGenerate`
  - Vemos en este ejemplo que `Flux.generate()` solo permite emitir un item, pero las veces que queramos, bajo demanda.

## Flux Generate - Emit Until

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec07FluxGenerateUntil`
  - Vemos como usar `Flux.generate()` para emitir items hasta que se cumpla una condición.

## Flux Generate - State problem

Vamos a hablar de un posible problema que puede surgir al usar `Flux.generate()` cuando tenemos un estado mutable.

`Flux.generate()` es sin estado.

En `src/java/com/jmunoz/sec04` creamos la clase:

- `Lec08GenerateWithState`
  - Vemos los problemas que pueden surgir al usar `Flux.generate()` con un estado mutable.

## Flux Generate - State Supplier

Para evitar el problema del estado mutable, podemos proveer el estado como un valor inicial (un objeto, una conexión a BD, lo que queramos) que se invoca una sola vez, al método `Flux.generate()`.

Ese objeto se pasa también junto con `synchronousSink` y se puede usar para emitir items.

Se devuelve el estado actualizado al final de la ejecución.

Si el estado inicial consiste en abrir una conexión a BD, ¿cuándo se cierra? Se acepta un tercer parámetro para ello (optativo).

```java
Flux.generate(
        () -> someObject,  // Invocado solo una vez
        (someObject, synchronousSink) -> {
            // Aquí podemos usar someObject para emitir items
            // ...
            return someObject;  // Devolvemos el estado actualizado
        },
        someObject -> close   // Invocado solo una vez. Sirve también para onComlete, onError, onCancel...
)
```

- `Lec08bGenerateWithState`
  - Vemos la solución al problema del estado mutable usando un estado inicial.

## Ejercicio

Vamos a implementar la siguiente interface:

```java
public interface FileReaderService {
    Flux<String> read(Path path);
}
```

En `src/java/com/jmunoz/sec04/assignment` creamos la interface `FileReaderService` que cree el contrato:

- Leer fichero y devolver contenido.
- Crear fichero y escribir contenido.
- Borrar fichero.

Y su implementación `FileReaderServiceImpl`.

También creamos la clase de prueba `Assignment`.

## Resumen

![alt Flux Create vs Flux Generate](./images/09-FluxCreateVsGenerate.png)

En `Flux.generate()`, como solo se emite un item, no tiene sentido compartirlo con varios threads, así que no aplica.

**Flux.create()**

- Diseñado para usarse cuando tenemos un solo subscriber.
- FluxSink es thread safe. Podemos compartirlo entre varios threads.
- Podemos mantener la emisión de items al sink sin tener que preocuparnos de una baja demanda.
- FluxSink entregará todo al subscriber de forma segura, secuencialmente, de uno en uno.
- Items pendientes
  - ¿Qué pasa si quiero tener varios subscribers?
  - Gestión de backpressure, ya que en algún momento la cola se va a llenar.

**Flux.generate()**

- Permite crear una utilidad genérica.

# Operators

## Introducción

![alt Operators](./images/10-OperatorsAreDecorators.png)

Este ejemplo quiere decir:

- Tenemos un expresso.
- Si le añadimos agua, tenemos un americano.
- Pero si le añadimos leche al vapor, tenemos un flat white.
- ...

Los operadores son como decoradores o ingredientes que añadimos para decorar nuestro publisher con comportamientos adicionales, basado en los requerimientos de negocio.

Siempre que se añade un comportamiento adicional via operator, se devuelve una nueva instancia, un nuevo objeto Java publisher. Este nuevo objeto es al que nos tenemos que subscribir.

Se llama pipeline a la cadena de operadores que se aplican a un publisher. La data fluye a través de este pipeline y finalmente llega al subscriber.

Estos operators ya los hemos visto antes:

- filter
- map
- log
- take
- takeWhile
- takeUntil

## Operator - Handle

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec01Handle`
  - Handle se comporta como un filter + map.

## Operator - HandleUntil

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec02HandleUntil`
  - Ejemplo de uso de handle para filtrar hasta que se encuentre un elemento específico.

## Do Hooks/Callbacks

Hay una gran cantidad de operators do que se pueden usar, los más comunes son:

- doOnNext
- doOnSubscribe
- doOnComplete
- doOnError
- doOnCancel
- doFinally
- doOnRequest
- doFirst

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec03DoCallbacks`
  - Vemos ejemplos de como usar los callbacks doOnNext, doOnSubscribe, doOnComplete, doOnError, doOnCancel, doFinally, doOnRequest...

## Operator - doOnNext - Clarificación

En programación reactiva, el operador `doOnNext()` es algo que podemos usar dependiendo de nuestro caso de uso.

Esto es porque muta la data, y en principio, no es lo mejor, pero todo depende del contexto. En concreto:

- La inmutabilidad es buena, pero esto no significa que la mutación sea mala.
- La programación funcional prefiere funciones puras (sin efectos secundarios).
  - ¡Preferir funciones puras!

Pero tener en cuenta que los objetos Entity son objetos mutables. Los arquitectos Java dicen que la mutación tiene sus ventajas si se sabe como usarla.

```java
interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Flux<Customer> findByFirstname(String firstname);
    Mono<Customer> findById(Long id);
}

// Con programación clásica
// Si la interface devuelve un Mono, esto no tiene mucho sentido porque Mono no es bloqueante, no sabemos cuando vamos a obtener Customer.
var customer = this.repository.findById(123).get();
customer.setAge(10);
this.repository.save(customer);

// Con programación reactiva
this.repository.findById(123)
    .doOnNext(customer -> customer.setAge(10))
    .flatMap(this.repository::save);
```

Indicar que `findById()` devuelve un `Mono<Customer>`, no un Optional. Este Mono es un publisher, y ya sabemos que es no bloqueante.

Esto significa que no sabemos cuando vamos a obtener el objeto Customer. En este caso el método `doOnNext()` es muy útil. Cuando obtenemos el valor, lo mutamos y luego lo guardamos.

Project Reactor garantiza que internamente se sincroniza y la mutación es segura, hecha por un thread y no vendrá otro thread a mutar el mismo objeto al mismo tiempo.

## Operator - Delay Elements

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec04Delay`
  - Vemos como retrasar la emisión de items usando el operador `delayElements()`.

## Subscribe

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec05Subscribe`
  - Vemos los operadores doOnNext(), doOnComplete() y doOnError(), que se pueden usar en vez de indicar el subscriber en .subscribe().

## Error Handling

Esta es una clase muy importante.

Vemos como manejar los errores en un pipeline reactivo, tanto Flux como Mono.

En programación tradicional, tenemos el bloque try-catch para manejar excepciones. ¿Qué pasa en programación reactiva?

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec06ErrorHandling`
  - Vemos las formas de manejar errores en un flujo reactivo, valiendo igual para Mono que para Flux.
  - `onErrorReturn`.
  - `onErrorResume`.
  - `onErrorComplete`.
  - `onErrorContinue`.

## Operator - Default If Empty

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec07DefaultIfEmpty`
  - Vemos como usar el operador `defaultIfEmpty()` para devolver un valor por defecto si el publisher no emite ningún item.

## Operator - Switch If Empty

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec08SwitchIfEmpty`
  - Vemos como usar el operador `switchIfEmpty()` para cambiar a otro publisher si el publisher original no emite ningún item.

## Operator - Timeout

Vamos a hablar del operator `timeout`.

En casos en que hacemos peticiones a servicios remotos, es importante saber que si no obtenemos una respuesta en un tiempo determinado, debemos manejar el timeout.

Por ejemplo, esperamos 1sg para obtener la respuesta de un servicio remoto, pero si no obtenemos la respuesta en ese tiempo, vamos a asumir un valor por defecto.

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec09Timeout`
  - Vemos como usar el operador `timeout()` para manejar el timeout en un flujo reactivo.

## Operator - Transform

Este método nos ayuda a escribir código reutilizable en el pipeline reactivo.

Igual que en POO consideramos todo un objeto, en programación reactiva todo el flujo de trabajo empresarial que construyamos será un publisher y un subscriber, donde la data fluirá de uno a otro.

Podemos tener muchos requerimientos de flujos de trabajo empresariales, y muchas veces veremos que hay pasos redundantes en otros pipelines.

Necesitamos una forma de poder reutilizar esos pasos entre todos los pipelines reactivos que construyamos. Usaremos el operador `transform()` para ello.

El método `transform()` nos ayuda a construir estos pasos de forma separada.

![alt Operator Transform](./images/11-OperatorTransform.png)

Es muy parecido a decir que construimos un operator personalizado.

En `src/java/com/jmunoz/sec05` creamos la clase:

- `Lec10Transform`
  - Vemos como usar el operador `transform()` para construir pasos reutilizables en un pipeline reactivo.

## Ejercicio

- Para este ejercicio, arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
- Usaremos los tres endpoints que existen en `demo03/`.

En `src/java/com/jmunoz/sec05/assignment` creamos la clase:

- `Assignment`

En `src/java/com/jmunoz/sec05/client` creamos un método en la clase:

- `ExternalServiceClient`

# Hot & Cold Publishers

Vamos a hablar de dos publishers, `Hot` y `Cold`.

En el concepto publisher/subscriber, se dijo que nada ocurre hasta que el subscriber se subscribe al publisher.

Si dos subscriber se subscriben al mismo publisher, el publisher se comportará como dos publishers diferentes. Un Flux dará dos flujos de data independientes, uno para cada subscriber.

Cada subscriber obtendrá su propia data. No existe conflicto entre ellos. Cada uno puede cancelar su suscripción sin afectar al otro.

A esto se le llama `Cold publisher` y es lo más usado.

Un ejemplo sería Netflix, donde tenemos flujos de video independientes para cada usuario.

Sin embargo, en un `Hot publisher`, solo tenemos un publisher que produce la data para todos los subscribers. En algunos casos, ni siquiera necesitamos un subscriber para que el publisher emita la data.

Se dijo que nada pasaba hasta que había un subscriber, pero en un `Hot publisher`, esta regla no aplica.

Se usa para broadcasting.

Un ejemplo sería un canal de televisión (o un cine), donde el canal emite la señal y cualquier usuario que se conecte al canal recibirá la señal. Si nadie se conecta da igual, el canal emite la señal igualmente.

## Flux Sink - Issue Discussion

En `src/java/com/jmunoz/sec06` creamos la clase:

- `Lec01ColdPublisher`
  - Vemos como crear un `Cold Publisher` usando `FluxSink`.

## Hot Publisher

En `src/java/com/jmunoz/sec06` creamos la clase:

- `Lec02HotPublisher`
  - Vemos como crear un `Hot Publisher` usando `share()`.
  - Vemos `publish().refCount()` y cómo funciona.
  - Vemos re-suscribirse a un `Hot Publisher` y cómo afecta a los subscribers.

## Hot Publisher - Auto Connect

En `src/java/com/jmunoz/sec06` creamos la clase:

- `Lec03HotPublisherAutoConnect`
  - Vemos como usar `autoConnect()`.

## Hot Publisher - Replay / Cache

En `src/java/com/jmunoz/sec06` creamos la clase:

- `Lec04HotPublisherCache`
  - Vemos como usar `replay()` para almacenar los items emitidos por un `Hot Publisher`.
  - Con este se corrige un problema de `publish().autoConnect(0)`.

## Flux Sink - Multiple Subscribers

Conociendo la diferencia entre `Cold` y `Hot` publishers, ya sabemos por qué `Lec02FluxCreateRefactor` de `sec04` no funcionaba con varios subscribers.

```java
    public static void main(String[] args) {
        var generator = new NameGenerator();
        var flux = Flux.create(generator);
        flux.subscribe(Util.subscriber());

        for (int i = 0; i < 10; i++) {
            generator.generate();
        }
    }
```

En `src/java/com/jmunoz/sec06` creamos la clase:

- `Lec05FluxCreateIssueFix`
  - Vemos como solucionar el problema de `Flux.create()` con varios subscribers.

## Resumen

Los métodos que usa Cold Publisher ya los hemos visto.

![alt Métodos de Hot Publisher](./images/12-HotPublisherMethods.png)

## Ejercicio

![alt Ejercicio](./images/13-AssignmentSec06.png)

- Order-Service provee un stream de pedidos. En la vida real, esto podría ser un topic Kafka o un stream Kafka, no importa. Para nosotros es un servicio externo que expone un endpoint para que podamos consumir ese stream de pedidos.
  - Vemos que necesita mínimo 2 subscribers.
  - El mensaje (cada pedido) tendrá el formato que aparece a su derecha, con item, category, price y quantity y será un texto plano que tendremos que parsear para extraer su información.
- Tenemos dos servicios diferentes. En la vida real, esto serían dos aplicaciones diferentes, pero para nuestro ejercicio serán dos clases diferentes.
  - Revenue-service está interesado en consumir el stream de pedidos, y por cada categoría obtendremos la ganancia obtenida, cuyo valor actualizará un Map (en la vida real sería una BD).
    - Este servicio, además, expondrá su propio flujo de data para que lo puedan consumir otros servicios. Para nuestro ejercicio, cada 2sg se emitirá data y la imprimiremos por consola, indicando la categoría y su ganancia.
  - Inventory-service está interesado en consumir el stream de pedidos, y cuando hay un pedido, reducirá la cantidad por categoría. La cantidad inicial la suponemos de 500 elementos por cada categoría. De nuevo, se actualiza un Java Map (en la vida real sería una BD).
    - Este servicio, además, expondrá su propio flujo de data para que lo puedan consumir otros servicios. Para nuestro ejercicio, cada 2sg se emitirá data y la imprimiremos por consola, indicando la categoría y su cantidad restante.

Cosas a tener en cuenta:

- El campo item no lo usaremos, ya que no nos interesa.
- El campo price se refiere al precio total, es decir, no hay que multiplicarlo por quantity.
- El formato del mensaje del flujo de pedidos es: `"item:category:price:quantity"`, por ejemplo: `"item1:electronics:100.0:2"`.
- Como inventario original supondremos 500 elementos por categoría. Hay que deducir la cantidad por categoría cada vez que se recibe un pedido.

- Para este ejercicio, arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
- Usaremos el endpoint `demo04/orders/stream`.
- Podemos probar a obtener la data desde el navegador como ejemplo: `http://localhost:7070/demo04/orders/stream`.

En `src/java/com/jmunoz/sec06/assignment` creamos la clase:

- `Assignment`
- `Order`
- `OrderProcessor` (interface)
- `RevenueService`
- `InventoryService`
 
En `src/java/com/jmunoz/sec06/client` creamos un método en la clase:

- `ExternalServiceClient`
