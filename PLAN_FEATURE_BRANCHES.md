# Plan de feature/branch y commits convencionales — EcoStore

Ramas por agrupación lógica (capas hexagonales). Cada commit usa **Conventional Commits**: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`.

---

## Convención de commits

| Prefijo    | Uso |
|-----------|-----|
| `feat:`   | Nueva funcionalidad o componente |
| `fix:`    | Corrección de bug o comportamiento incorrecto |
| `docs:`   | Solo documentación (README, entregables, comentarios) |
| `chore:`  | Configuración, dependencias, build, sin lógica de negocio |
| `refactor:` | Cambio de código sin cambiar comportamiento observable |

Formato: `tipo(ámbito opcional): descripción breve`  
Ejemplo: `feat(domain): add Category enum with discount percent`

---

## Rama 1: `feature/domain-model`

**Objetivo:** Modelo de dominio (entidades, VO, regla de descuento).

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 1 | `feat(domain): add Category enum with NORMAL and TEMPORADA_PASADA` | `domain/model/Category.java` — enum + `getDiscountPercent()` (0%, 15%) |
| 2 | `feat(domain): add PriceInfo value object with discount and final price` | `domain/model/PriceInfo.java` — `withoutDiscount`, `withDiscount`, trazabilidad |
| 3 | `feat(domain): add Product entity with create and updateWith` | `domain/model/Product.java` — aplicación de descuento desde Category |
| 4 | `feat(domain): add ProductRepository port (out)` | `domain/port/out/ProductRepository.java` — save, findById, findAll, update, deleteById, exists* |

**Total rama 1:** 4 commits.

---

## Rama 2: `feature/domain-ports-in`

**Objetivo:** Puertos de entrada (casos de uso).

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 5 | `feat(domain): add CreateProductUseCase port` | `domain/port/in/CreateProductUseCase.java` |
| 6 | `feat(domain): add GetProductUseCase port` | `domain/port/in/GetProductUseCase.java` |
| 7 | `feat(domain): add UpdateProductUseCase port` | `domain/port/in/UpdateProductUseCase.java` |
| 8 | `feat(domain): add DeleteProductUseCase port` | `domain/port/in/DeleteProductUseCase.java` |
| 9 | `refactor(domain): extend ProductRepository with existsByName and existsByNameExcludingId` | `domain/port/out/ProductRepository.java` — métodos para validación de nombre |

**Total rama 2:** 5 commits.

---

## Rama 3: `feature/application-service`

**Objetivo:** Capa de aplicación (casos de uso implementados).

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 10 | `feat(application): add DuplicateProductNameException` | `application/exception/DuplicateProductNameException.java` |
| 11 | `feat(application): add ProductApplicationService implementing use cases` | `application/service/ProductApplicationService.java` — create, getById, getAll, update, delete + validaciones |

**Total rama 3:** 2 commits.

---

## Rama 4: `feature/infrastructure-persistence`

**Objetivo:** Adaptador de salida (persistencia).

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 12 | `feat(infra): add InMemoryProductRepository implementing ProductRepository` | `infrastructure/adapter/out/persistence/InMemoryProductRepository.java` |

**Total rama 4:** 1 commit.

---

## Rama 5: `feature/infrastructure-web`

**Objetivo:** Adaptador de entrada (HTTP, DTOs, mapper, excepciones).

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 13 | `feat(infra): add CreateProductDTO and UpdateProductDTO with validation` | `infrastructure/adapter/in/web/dto/CreateProductDTO.java`, `UpdateProductDTO.java` |
| 14 | `feat(infra): add ProductResponse for API output` | `infrastructure/adapter/in/web/dto/ProductResponse.java` |
| 15 | `feat(infra): add ProductMapper (MapStruct) DTO to domain` | `infrastructure/adapter/in/web/mapper/ProductMapper.java` |
| 16 | `feat(infra): add ProductNotFoundException and GlobalExceptionHandler` | `ProductNotFoundException.java`, `GlobalExceptionHandler.java` — 404, 409, 400 |
| 17 | `feat(infra): add ProductController REST CRUD and OpenAPI docs` | `ProductController.java` — POST/GET/PUT/DELETE `/api/products` |

**Total rama 5:** 5 commits.

---

## Rama 6: `chore/config-deps`

**Objetivo:** Configuración y dependencias.

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 18 | `chore: add MapStruct and Lombok to pom.xml` | `pom.xml` — dependencias y plugin MapStruct |
| 19 | `chore: add SpringDoc OpenAPI and application properties` | `pom.xml` (SpringDoc), `application.properties` — SpringDoc, server, etc. |

**Total rama 6:** 2 commits.

---

## Rama 7: `docs/entregables`

**Objetivo:** Documentación de entrega y sustentación.

| # | Commit (conventional) | Archivos / descripción |
|---|------------------------|-------------------------|
| 20 | `docs: add ENTREGABLES_ECOSTORE (RF, diseño hexagonal, patrones)` | `ENTREGABLES_ECOSTORE.md` — secciones 1, 2, 4 |
| 21 | `docs: add API endpoints, cURL examples and exposition guide` | `ENTREGABLES_ECOSTORE.md` — secciones 5 y 6 |
| 22 | `docs: add plan of feature branches and conventional commits` | `PLAN_FEATURE_BRANCHES.md` (este archivo) |

**Total rama 7:** 3 commits.

---

## Resumen por rama

| Rama | Commits | Tipos |
|------|---------|--------|
| `feature/domain-model` | 4 | feat |
| `feature/domain-ports-in` | 5 | feat, refactor |
| `feature/application-service` | 2 | feat |
| `feature/infrastructure-persistence` | 1 | feat |
| `feature/infrastructure-web` | 5 | feat |
| `chore/config-deps` | 2 | chore |
| `docs/entregables` | 3 | docs |
| **Total** | **22** | — |

---

## Orden sugerido de trabajo (flujo)

1. Partir de `main` actualizado.
2. Crear y trabajar ramas en este orden (cada una puede hacerse desde `main` o integrando la anterior):
   - `feature/domain-model` → merge a `main`
   - `feature/domain-ports-in` → merge a `main`
   - `feature/application-service` → merge a `main`
   - `feature/infrastructure-persistence` → merge a `main`
   - `feature/infrastructure-web` → merge a `main`
   - `chore/config-deps` → merge a `main`
   - `docs/entregables` → merge a `main`
3. Cada commit con mensaje tal como en la tabla (copiar/pegar y ajustar si hace falta).

### Ejemplo de comandos por rama

```bash
git checkout main
git pull
git checkout -b feature/domain-model
# ... editar Category.java ...
git add src/main/java/com/itm/eco_store/domain/model/Category.java
git commit -m "feat(domain): add Category enum with NORMAL and TEMPORADA_PASADA"
# ... repetir por cada commit de la tabla ...
git checkout main
git merge feature/domain-model
git checkout -b feature/domain-ports-in
# ...
```

---

*Plan de 22 commits agrupados en 7 ramas con Conventional Commits.*
