# EcoStore — Plantilla de Trabajo y Entregables

---

## 1. Requisitos Funcionales (RF) y Atributos de Calidad

### Requisitos Funcionales implementados

| ID  | Descripción |
|-----|-------------|
| RF1 | **CRUD de catálogo**: Crear, leer (por ID y listado), actualizar y eliminar productos. |
| RF2 | **Regla de negocio TEMPORADA_PASADA**: Si la categoría es `TEMPORADA_PASADA` se aplica 15% de descuento; si es `NORMAL`, 0%. El porcentaje está definido en el dominio (`Category`), no lo envía el cliente. |
| RF3 | **Trazabilidad de precios**: Se persisten precio original, porcentaje de descuento aplicado y precio final calculado. |
| RF4 | **Validación de unicidad de nombre**: No se permite crear ni actualizar un producto con un nombre que ya exista en el catálogo (case-insensitive). |
| RF5 | **Validación de existencia**: En actualización y eliminación se valida que el producto exista (`existsById`) antes de operar. |
| RF6 | **API REST documentada**: Endpoints documentados con OpenAPI/Swagger y códigos HTTP (201, 400, 404, 409, 500). |

### Atributos de calidad priorizados

| Atributo        | Prioridad | Cómo se aborda en el proyecto |
|-----------------|-----------|-------------------------------|
| **Modularidad** | Alta      | Separación estricta dominio / aplicación / infraestructura (hexagonal). |
| **Mantenibilidad** | Alta  | Puertos (interfaces) en aplicación; casos de uso en aplicación; adaptadores en infraestructura. |
| **Testabilidad** | Media    | Dominio sin dependencias de framework; puertos inyectables (mocks en tests). |
| **Usabilidad**  | Media    | API REST con validación (Bean Validation), mensajes claros y documentación Swagger. |
| **Confiabilidad** | Media  | Validaciones de negocio (nombre duplicado, existencia por id) y manejo de excepciones (404, 409). |

---

## 2. Diseño Hexagonal (Mapping)

| Capa            | Componente              | Identificación en el proyecto |
|-----------------|-------------------------|--------------------------------|
| **DOMINIO**     | Entidades / VO          | `domain.model.Product` (entidad)<br>`domain.model.PriceInfo` (value object)<br>`domain.model.Category` (enum) |
| **APLICACIÓN**  | Puertos (interfaces)    | **Entrada:** `application.port.in.CreateProductUseCase`, `application.port.in.GetProductUseCase`, `application.port.in.UpdateProductUseCase`, `application.port.in.DeleteProductUseCase`<br>**Salida:** `application.port.out.ProductRepository`<br>**Salida:** `application.port.out.DiscountRepository` |
| **APLICACIÓN**  | Casos de uso            | `application.service.ProductApplicationService` (implementa los 4 use cases)<br>`application.exception.DuplicateProductNameException` |
| **INFRAESTRUCTURA** | Adaptadores entrada | `infrastructure.adapter.in.web.ProductController`<br>`infrastructure.adapter.in.web.dto.CreateProductDTO`, `UpdateProductDTO`, `ProductResponse`<br>`infrastructure.adapter.in.web.mapper.ProductMapper`<br>`infrastructure.adapter.in.web.GlobalExceptionHandler`<br>`infrastructure.adapter.in.web.ProductNotFoundException` |
| **INFRAESTRUCTURA** | Adaptadores salida  | `infrastructure.adapter.out.persistence.InMemoryProductRepository` (implementa `ProductRepository`)<br>`infrastructure.adapter.out.persistence.InMemoryDiscountRepository` (implementa `DiscountRepository`) |

### Resumen por paquete

```
com.itm.eco_store
├── domain
│   └── model              → Entidades / VO (Product, PriceInfo, Category)
├── application
│   ├── port
│   │   ├── in             → Puertos de entrada (CreateProductUseCase, GetProductUseCase, UpdateProductUseCase, DeleteProductUseCase)
│   │   └── out            → Puerto de salida (ProductRepository, DiscountRepository)
│   ├── service            → Casos de uso (ProductApplicationService)
│   └── exception          → Excepciones de aplicación (DuplicateProductNameException)
└── infrastructure
    └── adapter
        ├── in.web         → Adaptadores de entrada (Controller, DTOs, Mapper, manejo de excepciones)
        └── out.persistence→ Adaptadores de salida (InMemoryProductRepository, InMemoryDiscountRepository)
```

---

## 3. Estructura de Carpetas

```
src/main/java/com/itm/eco_store/
├── EcoStoreApplication.java                 # Punto de entrada Spring Boot
├── domain/                                  # CAPA DE DOMINIO
│   └── model/
│       ├── Category.java                    # Enum de categorías con descuento
│       ├── DiscountRule.java                 # NUEVO: Value Object para reglas de descuento
│       ├── PriceInfo.java                   # Value Object para precios
│       └── Product.java                     # Entidad Producto
├── application/                             # CAPA DE APLICACIÓN
│   ├── exception/
│   │   └── DuplicateProductNameException.java
│   ├── port/
│   │   ├── in/                              # Puertos de entrada
│   │   │   ├── CreateProductUseCase.java
│   │   │   ├── DeleteProductUseCase.java
│   │   │   ├── GetProductUseCase.java
│   │   │   └── UpdateProductUseCase.java
│   │   └── out/                             # Puertos de salida
│   │       ├── DiscountRepository.java       # NUEVO: Puerto para obtener reglas de descuento
│   │       └── ProductRepository.java
│   └── service/
│       └── ProductApplicationService.java   # Implementación de casos de uso
└── infrastructure/                          # CAPA DE INFRAESTRUCTURA
    └── adapter/
        ├── in/                              # Adaptadores de entrada
        │   └── web/
        │       ├── GlobalExceptionHandler.java
        │       ├── ProductController.java
        │       ├── ProductNotFoundException.java
        │       ├── dto/
        │       │   ├── CreateProductDTO.java
        │       │   ├── ProductResponse.java
        │       │   └── UpdateProductDTO.java
        │       └── mapper/
        │           └── ProductMapper.java
        └── out/                             # Adaptadores de salida
            └── persistence/
                ├── InMemoryDiscountRepository.java  # NUEVO
                └── InMemoryProductRepository.java
```

---

## 4. Patrones de Diseño Aplicados

| Patrón | Justificación técnica en el código |
|--------|-----------------------------------|
| **Puertos y Adaptadores (Hexagonal)** | La aplicación expone *puertos* (`application.port.in.*UseCase`, `application.port.out.*Repository`) como interfaces. El servicio implementa los casos de uso y la infraestructura implementa los adaptadores (`ProductController` llama a los use cases; `InMemoryProductRepository` implementa `ProductRepository`). Así el núcleo no depende de HTTP ni de la base de datos. |
| **Inversión de Dependencias (DIP)** | `ProductApplicationService` depende de la interfaz `ProductRepository` (puerto de salida), no de una clase concreta. Spring inyecta `InMemoryProductRepository` en tiempo de ejecución. El dominio y la aplicación no conocen detalles de persistencia. |
| **Repository** | `ProductRepository` abstrae el almacenamiento de productos. La aplicación usa `save`, `findById`, `findAll`, `update`, `deleteById`, `existsById`, `existsByName`, etc. El adaptador `InMemoryProductRepository` puede sustituirse por uno con JPA sin cambiar dominio ni casos de uso. |
| **Use Case (Application Service)** | Cada operación del catálogo se expone como un puerto de entrada (`CreateProductUseCase`, `GetProductUseCase`, `UpdateProductUseCase`, `DeleteProductUseCase`). `ProductApplicationService` implementa estos interfaces y orquesta validaciones (nombre duplicado, existencia por ID) y delegación al repositorio. |
| **DTO (Data Transfer Object)** | Los datos que entran/salen por la API están en `CreateProductDTO`, `UpdateProductDTO` y `ProductResponse`. Evitan acoplar el contrato REST al modelo de dominio y permiten validación con Bean Validation y documentación con `@Schema`. |
| **Mapper** | `ProductMapper` (MapStruct) convierte DTOs a entidades de dominio y dominio a respuestas. El controlador solo hace: DTO → mapper → dominio → use case → dominio → mapper → Response. La transformación queda centralizada y el dominio no tiene anotaciones de infraestructura. |
| **Value Object** | `PriceInfo` encapsula precio original, porcentaje de descuento y precio final con invariantes (escala, rangos). No tiene identidad; se usa dentro de `Product`. `Category` es un enum que además define el porcentaje por categoría (`getDiscountPercent()`). `DiscountRule` encapsula la relación entre categoría y porcentaje de descuento. |
| **Factory Method / Creación Rica** | `Product.create(name, description, category, originalPrice, discountPercent)` y `Product.updateWith(...)` aplican la regla de descuento dentro del dominio: obtienen el porcentaje de `Category` y construyen `PriceInfo` con `withDiscount` o `withoutDiscount`. La creación del agregado incluye la lógica de negocio. |

### Resumen de Cambios

| Aspecto | Antes | Ahora |
|--------|---------|------|
| **Category** | Enum con porcentaje hardcodeado | Enum simple, sin porcentaje |
| **Product** | Recibía descuento como parámetro externo | Recibe `discountPercent` como parámetro externo |
| **ProductApplicationService** | No accedía a Category.getDiscountPercent() | Usa `DiscountRepository` para obtener el descuento |
| **DiscountRepository** | No existía | NUEVO: Puerto en `application.port.out` |

---

## 5. Documentación de API (Endpoints y cURL)

Base URL de ejemplo: `http://localhost:8080` (ajustar si el puerto es otro).

| Endpoint | Descripción |
|----------|-------------|
| **POST** `/api/products` | Crear producto. Si categoría es `TEMPORADA_PASADA` se aplica 15% de descuento. |
| **GET** `/api/products` | Listar todos los productos. |
| **GET** `/api/products/{id}` | Obtener producto por ID. |
| **PUT** `/api/products/{id}` | Actualizar producto. Descuento según categoría. |
| **DELETE** `/api/products/{id}` | Eliminar producto por ID. |

### cURL de prueba

> **Nota:** En Windows PowerShell, usar `curl.exe` en lugar de `curl` para evitar conflictos con el alias de `Invoke-WebRequest`.

**1. Crear producto NORMAL (sin descuento)**
```bash
curl.exe -s -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\":\"Camiseta Verde\",\"description\":\"Algodon organico\",\"category\":\"NORMAL\",\"originalPrice\":29.99}"
```
*Respuesta esperada:*
```json
{"id":1,"name":"Camiseta Verde","description":"Algodon organico","category":"NORMAL","originalPrice":29.99,"discountPercent":0.00,"finalPrice":29.99}
```

**2. Crear producto TEMPORADA_PASADA (regla de negocio: 15% descuento)**
```bash
curl.exe -s -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\":\"Abrigo Invierno\",\"description\":\"Temporada pasada\",\"category\":\"TEMPORADA_PASADA\",\"originalPrice\":100.00}"
```
*Respuesta esperada:*
```json
{"id":2,"name":"Abrigo Invierno","description":"Temporada pasada","category":"TEMPORADA_PASADA","originalPrice":100.00,"discountPercent":15.00,"finalPrice":85.00}
```

**3. Listar todos los productos**
```bash
curl.exe -s http://localhost:8080/api/products
```
*Respuesta esperada:*
```json
[{"id":1,"name":"Camiseta Verde","description":"Algodon organico","category":"NORMAL","originalPrice":29.99,"discountPercent":0.00,"finalPrice":29.99},{"id":2,"name":"Abrigo Invierno","description":"Temporada pasada","category":"TEMPORADA_PASADA","originalPrice":100.00,"discountPercent":15.00,"finalPrice":85.00}]
```

**4. Obtener producto por ID (usar el `id` devuelto en el POST)**
```bash
curl.exe -s http://localhost:8080/api/products/1
```
*Respuesta esperada:*
```json
{"id":1,"name":"Camiseta Verde","description":"Algodon organico","category":"NORMAL","originalPrice":29.99,"discountPercent":0.00,"finalPrice":29.99}
```

**5. Actualizar producto (cambiar a TEMPORADA_PASADA para ver recálculo de descuento)**
```bash
curl.exe -s -X PUT http://localhost:8080/api/products/1 -H "Content-Type: application/json" -d "{\"name\":\"Camiseta Verde\",\"description\":\"Algodon organico\",\"category\":\"TEMPORADA_PASADA\",\"originalPrice\":29.99}"
```
*Respuesta esperada:*
```json
{"id":1,"name":"Camiseta Verde","description":"Algodon organico","category":"TEMPORADA_PASADA","originalPrice":29.99,"discountPercent":15.00,"finalPrice":25.49}
```

**6. Eliminar producto**
```bash
curl.exe -s -o NUL -w "%{http_code}" -X DELETE http://localhost:8080/api/products/1
```
*Se espera: `204` (No Content)*

---

## 5.1. Persistencia de Datos (H2 en Archivo)

### Configuración

La aplicación usa **H2 Database** con persistencia en archivo. Los datos se guardan en `./data/ecostore.mv.db` y persisten entre reinicios.

**Configuración en `application.properties`:**
```properties
# H2 Database - Persistencia en archivo
spring.datasource.url=jdbc:h2:file:./data/ecostore
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### URLs de interés

| Servicio | URL |
|----------|-----|
| **API Products** | `http://localhost:8080/api/products` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **H2 Console** | `http://localhost:8080/h2-console` |

### Acceso a H2 Console

1. Abrir `http://localhost:8080/h2-console` en el navegador
2. Configurar conexión:
   - **JDBC URL:** `jdbc:h2:file:./data/ecostore`
   - **User Name:** `sa`
   - **Password:** (dejar vacío)
3. Click en "Connect"

### Comandos útiles H2

```sql
-- Ver todas las tablas
SHOW TABLES;

-- Ver productos guardados
SELECT * FROM PRODUCT;

-- Ver estructura de tabla
DESCRIBE PRODUCT;
```

---

## 6. Guía de Exposición

### Cómo se aplicó la inversión de dependencia

- **Dominio**: Contiene solo las entidades y value objects con lógica de negocio pura (`Product`, `PriceInfo`, `Category`, `DiscountRule`). No conoce Spring, HTTP ni bases de datos.
- **Aplicación**: Define los puertos (interfaces) que necesita para recibir peticiones (`*UseCase`) y para salir al exterior (`ProductRepository`, yDiscountRepository`). `ProductApplicationService` depende solo de interfaces, no de clases concretas.
- **Infraestructura**: Implementa el puerto de salida (`InMemoryProductRepository implements ProductRepository`, `InMemoryDiscountRepository implements DiscountRepository`) y los puertos de entrada son *usados* por el adaptador de entrada: el `ProductController` recibe las interfaces `CreateProductUseCase`, `GetProductUseCase`, etc., inyectadas por Spring.

Así, las dependencias apuntan hacia el dominio: la aplicación y la infraestructura dependen de las abstracciones, no al revés.

### En qué parte del código reside la lógica de negocio (Regla de Oro)

- **En el dominio**:
  - `Category.getDiscountPercent()`: ~~ELIMINADO~~ (movido a DiscountRepository)
  - `Product.create(...)` y `Product.updateWith(...)`: Ahora reciben `discountPercent` como parámetro externo (desde `DiscountRepository`)
  - `PriceInfo.withDiscount` / `withoutDiscount`: Calculan el precio final y garantizan la trazabilidad (original, %, final). La creación del agregado incluye la lógica de negocio.

- **En la aplicación (orquestación y reglas de aplicación)**:
  - `ProductApplicationService`: valida nombre duplicado (`existsByName`, `existsByNameExcludingId`) y existencia por ID (`existsById`) antes de crear, actualizar o eliminar. No calcula precios; delega la creación/actualización al dominio.

La *regla de oro* se cumple: la regla de negocio del descuento por categoría y el cálculo de precios viven en el dominio; la capa de aplicación solo orquesta bien valida condiciones de uso.

### Cómo se visualizan los puertos y adaptadores en la ejecución

1. **Petición HTTP** → llega al **adaptador de entrada** `ProductController` (infraestructura).
2. El controlador convierte el DTO a dominio con `ProductMapper` y llama al **puerto de entrada** (por ejemplo `CreateProductUseCase.create(product)`).
3. La **implementación del puerto** es `ProductApplicationService`: valida y llama al **puerto de salida** `ProductRepository` (por ejemplo `repository.save(product)`).
4. El **adaptador de salida** `InMemoryProductRepository` implementa `ProductRepository` y persiste en memoria (o en el futuro podría ser un `JpaProductRepository`).
5. La respuesta vuelve: dominio → mapper → DTO → HTTP.

En la sustentación se puede mostrar: las interfaces en `application.port.in` y `application.port.out`, la inyección en `ProductController` y `ProductApplicationService`, y el flujo de una petición POST hasta que el dominio aplica el descuento y el adaptador de salida guarda el producto.

---

*Documento de entregables — EcoStore (Arquitectura Hexagonal / Clean Architecture).*