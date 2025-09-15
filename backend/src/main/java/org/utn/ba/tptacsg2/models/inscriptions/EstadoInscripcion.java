package org.utn.ba.tptacsg2.models.inscriptions;

import java.time.LocalDateTime;

public class EstadoInscripcion {

    private final String id;
    private TipoEstadoInscripcion tipoEstado;
    private Inscripcion inscripcion; //TODO hacer un DTO sin la inscripcion para evitar referencias circulares en JSON
    private LocalDateTime fechaDeCambio;

    public EstadoInscripcion(String id, TipoEstadoInscripcion tipo, LocalDateTime fechaDeCambio) {
        this.id = id;
        this.tipoEstado = tipo;
        this.fechaDeCambio = fechaDeCambio;
    }

    public EstadoInscripcion(String id, TipoEstadoInscripcion tipoEstado, Inscripcion inscripcion, LocalDateTime fechaDeCambio) {
        this.id = id;
        this.tipoEstado = tipoEstado;
        this.inscripcion = inscripcion;
        this.fechaDeCambio = fechaDeCambio;
    }

    public String getId() {
        return id;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public TipoEstadoInscripcion getTipoEstado() {
        return tipoEstado;
    }

    public LocalDateTime getFechaDeCambio() {
        return fechaDeCambio;
    }

    public void setFechaDeCambio(LocalDateTime fechaDeCambio) {
        this.fechaDeCambio = fechaDeCambio;
    }
}
