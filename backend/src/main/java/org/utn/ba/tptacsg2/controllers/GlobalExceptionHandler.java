package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.exceptions.EventoSinConfirmarException;
import org.utn.ba.tptacsg2.exceptions.InscripcionNoEncontradaException;
import org.utn.ba.tptacsg2.exceptions.InscripcionDuplicadaException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventoNoEncontradoException.class)
    public ResponseEntity<String> handleEventoNoEncontradoException(EventoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InscripcionNoEncontradaException.class)
    public ResponseEntity<String> handleEventoNoEncontradaException(InscripcionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InscripcionDuplicadaException.class)
    public ResponseEntity<String> handleInscripcionDuplicadaException(InscripcionDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EventoSinConfirmarException.class)
    public ResponseEntity<String> handleEventoSinConfirmarException(EventoSinConfirmarException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
