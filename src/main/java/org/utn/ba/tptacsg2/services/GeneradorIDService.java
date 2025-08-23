package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GeneradorIDService {

    public String generarID() {
        return UUID.randomUUID().toString();
    }
}
