package dev.eventpass.tickets.security;

import dev.eventpass.tickets.model.Rol;

public record UsuarioAutenticado(Long id, String email, Rol rol) {
}
