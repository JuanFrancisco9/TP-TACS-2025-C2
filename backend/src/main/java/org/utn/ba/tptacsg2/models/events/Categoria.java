package org.utn.ba.tptacsg2.models.events;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("categorias")
public class Categoria {
    @Id
    private String id;
    private String tipo;
    private String icono;

    public Categoria() {}

    public Categoria(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public Categoria(String id, String tipo, String icono) {
        this.id = id;
        this.tipo = tipo;
        this.icono = icono;
    }

    public Categoria(String tipo, String icono) {
        this.tipo = tipo;
        this.icono = icono;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}
