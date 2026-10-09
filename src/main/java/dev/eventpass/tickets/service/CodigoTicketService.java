package dev.eventpass.tickets.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class CodigoTicketService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LARGO_CODIGO = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generar() {
        StringBuilder codigo = new StringBuilder("EVP-");
        for (int i = 0; i < LARGO_CODIGO; i++) {
            codigo.append(ALFABETO.charAt(secureRandom.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }
}
