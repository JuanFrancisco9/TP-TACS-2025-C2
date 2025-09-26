package org.utn.ba.tptacsg2.exceptions;

public class InscripcionNoEncontradaException extends RuntimeException {
    public InscripcionNoEncontradaException(String message) {
        super(message);
    }

    public InscripcionNoEncontradaException(Throwable cause) {
        super(cause);
    }

    public InscripcionNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
