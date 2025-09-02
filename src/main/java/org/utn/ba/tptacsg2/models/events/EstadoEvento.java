package org.utn.ba.tptacsg2.models.events;


import jdk.jfr.Event;

import java.time.LocalDateTime;

public class EstadoEvento {
    private String id;
    private TipoEstadoEvento tipoEstado;
    private LocalDateTime fechaCambio;
    private Evento evento;

    public EstadoEvento(String id, TipoEstadoEvento tipoEstado, LocalDateTime fechaCambio, Evento evento) {
        this.id = id;
        this.tipoEstado = tipoEstado;
        this.fechaCambio = fechaCambio;
        this.evento = evento;
    }

    public EstadoEvento(String id, TipoEstadoEvento tipoEstado, LocalDateTime fechaCambio) {
        this.id = id;
        this.tipoEstado = tipoEstado;
        this.fechaCambio = fechaCambio;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getId() {
        return id;
    }

    public TipoEstadoEvento getTipoEstado() {
        return tipoEstado;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}

