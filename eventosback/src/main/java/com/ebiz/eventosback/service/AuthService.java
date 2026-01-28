package com.ebiz.eventosback.service;

import com.ebiz.eventosback.config.JwtUtil;
import com.ebiz.eventosback.dto.AuthResponseDTO;
import com.ebiz.eventosback.dto.LoginRequestDTO;
import com.ebiz.eventosback.dto.RegisterRequestDTO;
import com.ebiz.eventosback.dto.UsuarioDTO;
import com.ebiz.eventosback.exception.BadRequestException;
import com.ebiz.eventosback.model.Usuario;
import com.ebiz.eventosback.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {

        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        
        Usuario usuario = new Usuario();
        usuario.setNombre(requestDTO.getNombre());
        usuario.setEmail(requestDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        usuario.setRol(Usuario.Rol.USER); 

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        String token = jwtUtil.generateToken(
            usuarioGuardado.getEmail(),
            usuarioGuardado.getId(),
            usuarioGuardado.getRol().name()
        );
        
        UsuarioDTO usuarioDTO = convertirADTO(usuarioGuardado);
        

        return new AuthResponseDTO(token, usuarioDTO);
    }
    
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

                if (!passwordEncoder.matches(requestDTO.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }
        
        String token = jwtUtil.generateToken(
            usuario.getEmail(),
            usuario.getId(),
            usuario.getRol().name()
        );
        
        UsuarioDTO usuarioDTO = convertirADTO(usuario);
        
        return new AuthResponseDTO(token, usuarioDTO);
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) { 
        UsuarioDTO dto = new UsuarioDTO(); 
        dto.setId(usuario.getId()); 
        dto.setNombre(usuario.getNombre()); 
        dto.setEmail(usuario.getEmail()); 
        dto.setRol(usuario.getRol().name()); 
        return dto;
    }
}