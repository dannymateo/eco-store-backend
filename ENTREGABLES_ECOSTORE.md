# EcoStore — Plantilla de Trabajo y Entregables

---

## 1. Requisitos Funcionales (RF) y Atributos de Calidad

### Requisitos Funcionales implementados

| ID  | Descripción |
|-----|-------------|
| RF1 | **CRUD de catálogo**: Crear, leer (por ID y listado), actualizar y eliminar productos. |
| RF2 | **Regla de negocio TEMPORADA_PASADA**: Si la categoría es `TEMPORADA_PASADA` se aplica 15% de descuento; si es `NORMAL`, 0%. El porcentaje está definido en el dominio (`Category`), no lo envía el cliente. |
| RF3 | **Trazabilidad de precios**: Se persisten precio original, porcentaje de descuento aplicado y precio final. |
| RF4 | **Validación de unicidad de nombre**: No se permite crear ni actualizar un producto con un nombre que ya exista en el catálogo (case-insensitive). |
| RF5 | **Validación de existencia**: En actualización y eliminación se valida que el producto exista (`existsById`) antes de operar. |
| RF6 | **API REST documentada**: Endpoints documentados con OpenAPI/Swagger y códigos HTTP (201, 400, 404, 409, 500). |

### Atributos de calidad priorizados

| Atributo        | Prioridad | Cómo se aborda en el proyecto |
|-----------------|-----------|-------------------------------|
| **Modularidad** | Alta      | Separación estricta dominio / aplicación / infraestructura (hexagonal). |
| **Mantenibilidad** | Alta  | Puertos (interfaces) en el dominio; casos de uso en aplicación; adaptadores en infraestructura. |
| **Testabilidad** | Media    | Dominio sin dependencias de framework; puertos inyectables (mocks en tests). |
| **Usabilidad**  | Media    | API REST con validación (Bean Validation), mensajes claros y documentación Swagger. |
| **Confiabilidad** | Media  | Validaciones de negocio (nombre duplicado, existencia por id) y manejo de excepciones (404, 409). |

---

## 2. Diseño Hexagonal (Mapping)

| Capa            | Componente              | Identificación en el proyecto |
|-----------------|-------------------------|--------------------------------|
| **DOMINIO**     | Entidades / VO          | `domain.model.Product` (entidad)<br>`domain.model.PriceInfo` (value object)<br>`domain.model.Category` (enum) |
| **DOMINIO**     | Puertos (interfaces)    | **Salida:** `domain.port.out.ProductRepository`<br>**Entrada:** `domain.port.in.CreateProductUseCase`, `domain.port.in.GetProductUseCase`, `domain.port.in.UpdateProductUseCase`, `domain.port.in.DeleteProductUseCase` |
| **APLICACIÓN**  | Casos de uso            | `application.service.ProductApplicationService` (implementa los 4 use cases)<br>`application.exception.DuplicateProductNameException` |
| **INFRAESTRUCTURA** | Adaptadores entrada | `infrastructure.adapter.in.web.ProductController`<br>`infrastructure.adapter.in.web.dto.CreateProductDTO`, `UpdateProductDTO`, `ProductResponse`<br>`infrastructure.adapter.in.web.mapper.ProductMapper`<br>`infrastructure.adapter.in.web.GlobalExceptionHandler`<br>`infrastructure.adapter.in.web.ProductNotFoundException` |
| **INFRAESTRUCTURA** | Adaptadores salida  | `infrastructure.adapter.out.persistence.InMemoryProductRepository` (implementa `ProductRepository`) |

### Resumen por paquete

```
com.itm.eco_store
├── domain
│   ├── model          → Entidades / VO (Product, PriceInfo, Category)
│   └── port
│       ├── in         → Puertos de entrada (CreateProductUseCase, GetProductUseCase, UpdateProductUseCase, DeleteProductUseCase)
│       └── out        → Puerto de salida (ProductRepository)
├── application
│   ├── service        → Casos de uso (ProductApplicationService)
│   └── exception     → Excepciones de aplicación (DuplicateProductNameException)
└── infrastructure
    └── adapter
        ├── in.web     → Adaptadores de entrada (Controller, DTOs, Mapper, manejo de excepciones)
        └── out.persistence → Adaptadores de salida (InMemoryProductRepository)
```

---

## 4. Patrones de Diseño Aplicados

| Patrón | Justificación técnica en el código |
|--------|-----------------------------------|
| **Puertos y Adaptadores (Hexagonal)** | El dominio expone *puertos* (`domain.port.in.*UseCase`, `domain.port.out.ProductRepository`) como interfaces. La aplicación implementa los casos de uso y la infraestructura implementa los adaptadores (`ProductController` llama a los use cases; `InMemoryProductRepository` implementa `ProductRepository`). Así el núcleo no depende de HTTP ni de la base de datos. |
| **Inversión de dependencias** | `ProductApplicationService` depende de la interfaz `ProductRepository` (puerto de salida), no de una clase concreta. Spring inyecta `InMemoryProductRepository` en tiempo de ejecución. El dominio y la aplicación no conocen detalles de persistencia. |
| **Repository** | `ProductRepository` abstrae el almacenamiento de productos. La aplicación usa `save`, `findById`, `findAll`, `update`, `deleteById`, `existsById`, `existsByName`, etc. El adaptador `InMemoryProductRepository` puede sustituirse por uno con JPA sin cambiar dominio ni casos de uso. |
| **Use Case (Application Service)** | Cada operación del catálogo se expone como un puerto de entrada (`CreateProductUseCase`, `GetProductUseCase`, `UpdateProductUseCase`, `DeleteProductUseCase`). `ProductApplicationService` implementa estos interfaces y orquesta validaciones (nombre duplicado, existencia por ID) y delegación al repositorio. |
| **DTO (Data Transfer Object)** | Los datos que entran/salen por la API están en `CreateProductDTO`, `UpdateProductDTO` y `ProductResponse`. Evitan acoplar el contrato REST al modelo de dominio y permiten validación con Bean Validation y documentación con `@Schema`. |
| **Mapper** | `ProductMapper` (MapStruct) convierte DTOs a entidades de dominio y dominio a respuestas. El controlador solo hace: DTO → mapper → dominio → use case → dominio → mapper → Response. La transformación queda centralizada y el dominio no tiene anotaciones de infraestructura. |
| **Value Object** | `PriceInfo` encapsula precio original, porcentaje de descuento y precio final con invariantes (escala, rangos). No tiene identidad; se usa dentro de `Product`. `Category` es un enum que además define el porcentaje por categoría (`getDiscountPercent()`). |
| **Factory Method / Creación rica** | `Product.create(name, description, category, originalPrice)` y `Product.updateWith(...)` aplican la regla de descuento dentro del dominio: obtienen el porcentaje de `Category` y construyen `PriceInfo` con `withDiscount` o `withoutDiscount`. La creación del agregado incluye la lógica de negocio. |

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

**1. Crear producto NORMAL (sin descuento)**  
```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Camiseta Verde","description":"Algodón orgánico","category":"NORMAL","originalPrice":29.99}'
```
*Se espera: `originalPrice`: 29.99, `discountPercent`: 0, `finalPrice`: 29.99*

**2. Crear producto TEMPORADA_PASADA (regla de negocio: 15% descuento)**  
```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Abrigo Invierno","description":"Temporada pasada","category":"TEMPORADA_PASADA","originalPrice":100.00}'
```
*Se espera: `originalPrice`: 100.00, `discountPercent`: 15.00, `finalPrice`: 85.00*

**3. Listar todos los productos**  
```bash
curl -s http://localhost:8080/api/products
```

**4. Obtener producto por ID (usar el `id` devuelto en el POST)**  
```bash
curl -s http://localhost:8080/api/products/1
```

**5. Actualizar producto (cambiar a TEMPORADA_PASADA para ver recálculo de descuento)**  
```bash
curl -s -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Camiseta Verde","description":"Algodón orgánico","category":"TEMPORADA_PASADA","originalPrice":29.99}'
```
*Se espera: mismo precio original, `discountPercent`: 15, `finalPrice` recalculado.*

**6. Eliminar producto**  
```bash
curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/api/products/1
```
*Se espera: 204 No Content.*

---

## 6. Guía de Exposición

### Cómo se aplicó la inversión de dependencia

- **Dominio**: Define las interfaces (puertos) que necesita para salir al exterior: `ProductRepository`. No conoce Spring, HTTP ni bases de datos.
- **Aplicación**: `ProductApplicationService` depende solo de `ProductRepository` (interfaz). No instancia ni conoce `InMemoryProductRepository`; quien inyecta la implementación es el contenedor (Spring) vía inyección por constructor.
- **Infraestructura**: Implementa el puerto de salida (`InMemoryProductRepository implements ProductRepository`) y los puertos de entrada son *usados* por el adaptador de entrada: el `ProductController` recibe las interfaces `CreateProductUseCase`, `GetProductUseCase`, etc., inyectadas por Spring.

Así, las dependencias apuntan hacia el dominio: la aplicación y la infraestructura dependen de las abstracciones del dominio, no al revés.

### En qué parte del código reside la lógica de negocio (Regla de Oro)

- **En el dominio**:  
  - `Category.getDiscountPercent()`: define 0% para NORMAL y 15% para TEMPORADA_PASADA.  
  - `Product.create(...)` y `Product.updateWith(...)`: obtienen el porcentaje de la categoría y construyen `PriceInfo` con `PriceInfo.withDiscount(originalPrice, discountPct)` o `PriceInfo.withoutDiscount(originalPrice)`.  
  - `PriceInfo.withDiscount` / `withoutDiscount`: calculan el precio final y garantizan la trazabilidad (original, %, final).

- **En la aplicación (orquestación y reglas de aplicación)**:  
  - `ProductApplicationService`: valida nombre duplicado (`existsByName`, `existsByNameExcludingId`) y existencia por ID (`existsById`) antes de crear, actualizar o eliminar. No calcula precios; delega la creación/actualización al dominio.

La *regla de oro* se cumple: la regla de negocio del descuento por categoría y el cálculo de precios viven en el dominio; la capa de aplicación solo orquesta y valida condiciones de uso.

### Cómo se visualizan los puertos y adaptadores en la ejecución

1. **Petición HTTP** → llega al **adaptador de entrada** `ProductController` (infraestructura).
2. El controlador convierte el DTO a dominio con `ProductMapper` y llama al **puerto de entrada** (por ejemplo `CreateProductUseCase.create(product)`).
3. La **implementación del puerto** es `ProductApplicationService`: valida y llama al **puerto de salida** `ProductRepository` (por ejemplo `repository.save(product)`).
4. El **adaptador de salida** `InMemoryProductRepository` implementa `ProductRepository` y persiste en memoria (o en el futuro podría ser un `JpaProductRepository`).
5. La respuesta vuelve: dominio → mapper → DTO → HTTP.

En la sustentación se puede mostrar: las interfaces en `domain.port.in` y `domain.port.out`, la inyección en `ProductController` y `ProductApplicationService`, y el flujo de una petición POST hasta que el dominio aplica el descuento y el adaptador de salida guarda el producto.

---

*Documento de entregables — EcoStore (Arquitectura Hexagonal).*
