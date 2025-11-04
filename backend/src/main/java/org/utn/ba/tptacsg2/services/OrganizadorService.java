package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;

import java.util.List;

@Service
public class OrganizadorService {
    private final EventoRepositoryDB eventoRepository;

    public OrganizadorService(EventoRepositoryDB eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> getEventosDeOrganizadorPorUsuario(String idUsuario) {
        return eventoRepository.findByUsuarioIdDelOrganizador(idUsuario);
    }

    public List<Evento> getEventosDeOrganizador(String idOrganizador) {
        return eventoRepository.findByOrganizador_Id(idOrganizador);
    }
}

