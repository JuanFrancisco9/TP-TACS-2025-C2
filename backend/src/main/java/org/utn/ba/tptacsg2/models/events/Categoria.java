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

    public Categoria() {}

    public Categoria(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public Categoria(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
