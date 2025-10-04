package org.utn.ba.tptacsg2.models.events;


import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;

import java.time.LocalDateTime;
@Document(collection = "estadoeventos")
public class EstadoEvento {
    @Id
    private String id;
    private TipoEstadoEvento tipoEstado;
    private LocalDateTime fechaCambio;

    @JsonBackReference
    @DBRef
    private Evento evento;

    public EstadoEvento() {}

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
    public EstadoEvento(TipoEstadoEvento tipoEstado, LocalDateTime fechaCambio) {
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

