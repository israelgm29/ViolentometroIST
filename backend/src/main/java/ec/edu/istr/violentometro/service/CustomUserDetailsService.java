package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserRepository sysUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        SysUser user = sysUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        System.out.println("Rol del usuario: " + user.getIdRole().getName());

        System.out.println("Usuario encontrado: " + user.getEmail());
        System.out.println("Password en DB: " + user.getPassword());
        System.out.println("Roles del usuario: " + user.getAuthorities());

        return user;
    }
}