package com.diamco.v1.services;

import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private  final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur user = utilisateurRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }
        log.info("Connexion utilisateur : {}", user.getEmail());

        return new User(
                user.getEmail(), // ✅ toujours l'email comme username
                user.getMdpHash(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())) // ✅ rôle formaté
        );
    }

}
