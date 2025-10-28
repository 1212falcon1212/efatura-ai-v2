package com.efaturaai.api.security;

import com.efaturaai.core.domain.Permission;
import com.efaturaai.core.domain.Role;
import com.efaturaai.core.domain.User;
import com.efaturaai.core.repository.UserRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UUID tenantId =
        TenantContext.getTenantId()
            .orElseThrow(() -> new UsernameNotFoundException("Tenant missing"));
    User user =
        userRepository
            .findByUsernameAndTenantId(username, tenantId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    Set<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(Role::getPermissions)
            .flatMap(Set::stream)
            .map(Permission::getCode)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPasswordHash())
        .authorities(authorities)
        .accountLocked(!user.isEnabled())
        .disabled(!user.isEnabled())
        .build();
  }
}
