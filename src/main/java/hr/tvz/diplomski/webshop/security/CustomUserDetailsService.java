package hr.tvz.diplomski.webshop.security;

import hr.tvz.diplomski.webshop.domain.User;
import hr.tvz.diplomski.webshop.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    public CustomUserDetailsService() {
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {

        return userRepository
                .findByEmailEquals(username)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " does not exist"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        List<GrantedAuthority> grantedAuthorities =
                new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getType().getRole())));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}

