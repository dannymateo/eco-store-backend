package com.itm.eco_store.infrastructure.adapter.in.web;

import com.itm.eco_store.domain.model.Product;
import com.itm.eco_store.application.port.in.CreateProductUseCase;
import com.itm.eco_store.application.port.in.DeleteProductUseCase;
import com.itm.eco_store.application.port.in.GetProductUseCase;
import com.itm.eco_store.application.port.in.UpdateProductUseCase;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.CreateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.ProductResponse;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.UpdateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.web.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Catálogo",
        description = "CRUD de productos. El descuento por categoría: NORMAL=0%, TEMPORADA_PASADA=15% (precio final y trazabilidad en BD)."
)
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductMapper productMapper;

    @Operation(
            summary = "Crear producto",
            description = "Registra un producto en el catálogo. Si categoría es TEMPORADA_PASADA se aplica 15% de descuento (definido en dominio)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (validación)"),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese nombre"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Parameter(description = "Datos del producto a crear", required = true)
            @Valid @RequestBody CreateProductDTO dto
    ) {
        Product domain = productMapper.toDomain(dto);
        Product created = createProductUseCase.create(domain);
        return productMapper.toResponse(created);
    }

    @Operation(summary = "Obtener producto por ID", description = "Devuelve un producto por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getById(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id
    ) {
        Product product = getProductUseCase.getById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponse(product);
    }

    @Operation(summary = "Listar productos", description = "Devuelve todos los productos del catálogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> getAll() {
        return getProductUseCase.getAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza un producto existente. Si categoría es TEMPORADA_PASADA se aplica 15% de descuento (definido en dominio)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro producto con ese nombre"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse update(
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del producto", required = true)
            @Valid @RequestBody UpdateProductDTO dto
    ) {
        Product domain = productMapper.toDomain(dto);
        Product updated = updateProductUseCase.update(id, domain);
        return productMapper.toResponse(updated);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo por ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado (existsById false)"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1")
            @PathVariable Long id
    ) {
        deleteProductUseCase.delete(id);
    }
}
