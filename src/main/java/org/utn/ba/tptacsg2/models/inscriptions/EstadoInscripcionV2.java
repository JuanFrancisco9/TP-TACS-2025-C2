package org.utn.ba.tptacsg2.models.inscriptions;

import java.time.LocalDateTime;

public class EstadoInscripcionV2 {

    private final String id;
    private TipoEstadoInscripcion tipoEstado;
    private Inscripcion inscripcion;
    private LocalDateTime fechaDeCambio;

    public EstadoInscripcionV2(String id, TipoEstadoInscripcion tipo, LocalDateTime fechaDeCambio) {
        this.id = id;
        this.tipoEstado = tipo;
        this.fechaDeCambio = fechaDeCambio;
    }

    public EstadoInscripcionV2(String id, TipoEstadoInscripcion tipoEstado, Inscripcion inscripcion, LocalDateTime fechaDeCambio) {
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

}
