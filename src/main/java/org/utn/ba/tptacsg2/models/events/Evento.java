package org.utn.ba.tptacsg2.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;
    private LocalDateTime horaInicio;
    private Float duracion;
    private Ubicacion ubicacion;
    private Integer cupoMaximo;
    private Integer cuposLibres;
    private Precio precio;
    private Organizador organizador;
    private EstadoEvento estado;

}
