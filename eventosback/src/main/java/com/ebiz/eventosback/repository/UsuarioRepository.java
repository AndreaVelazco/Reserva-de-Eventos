package com.ebiz.eventosback.repository;

import com.ebiz.eventosback.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { 
    
    Optional<Usuario> findByEmail(String email); // Método para buscar usuario por email
    
    Boolean existsByEmail(String email); // Método para verificar si un email ya está registrado
}