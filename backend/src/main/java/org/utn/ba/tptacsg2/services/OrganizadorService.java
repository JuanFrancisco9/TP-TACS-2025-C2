package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import java.util.List;

@Service
public class OrganizadorService {
    private final EventoRepository eventoRepository;

    public OrganizadorService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> getEventosDeOrganizadorPorUsuario(String idUsuario) {
        return eventoRepository.getEventosDeOrganizadorPorUsuario(idUsuario);
    }
}

