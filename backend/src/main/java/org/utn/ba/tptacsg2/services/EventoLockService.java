package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EventoLockService {
    // Creo un lock por cada evento registrado en mi sistema.
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    ReentrantLock getLock(String evento){
        return locks.computeIfAbsent(evento, key -> new ReentrantLock());
    }

}
