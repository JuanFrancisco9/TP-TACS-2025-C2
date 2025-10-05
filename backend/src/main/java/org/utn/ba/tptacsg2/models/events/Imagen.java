package org.utn.ba.tptacsg2.models.events;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "imagenes")
public record Imagen (
    @Id
    @Field("_id")
    String id,
    String bucket,
    String key,
    String contentType,
    Long sizeBytes,
    String originalName,
    String etag,
    Long ownerUserId
){}
