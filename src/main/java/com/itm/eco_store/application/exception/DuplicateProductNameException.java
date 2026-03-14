package com.itm.eco_store.application.exception;

/**
 * Se lanza cuando se intenta crear o actualizar un producto con un nombre que ya existe en el catálogo.
 */
public class DuplicateProductNameException extends RuntimeException {

    public DuplicateProductNameException(String name) {
        super("Ya existe un producto con el nombre: " + name);
    }
}
