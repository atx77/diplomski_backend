package hr.tvz.diplomski.webshop.service.impl;

import hr.tvz.diplomski.webshop.domain.Cart;
import hr.tvz.diplomski.webshop.domain.User;
import hr.tvz.diplomski.webshop.dto.UserDto;
import hr.tvz.diplomski.webshop.enumeration.UserType;
import hr.tvz.diplomski.webshop.repository.CartRepository;
import hr.tvz.diplomski.webshop.repository.UserRepository;
import hr.tvz.diplomski.webshop.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private ConversionService conversionService;

    @Resource
    private CartRepository cartRepository;

    @Override
    public UserDto getLoggedUser() {
        return conversionService.convert(userRepository.findByEmailEquals(getLoggedUserUsername()).orElse(null), UserDto.class);
    }

    @Override
    public void registerNewCustomer(String email, String password, String firstName, String lastName) {
        if (userRepository.findByEmailEquals(email).isPresent()) {
            throw new IllegalArgumentException("User already exists!");
        }
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
        user.setType(UserType.CUSTOMER);
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setCreationDate(new Date());
        cart.setUser(user);
        cartRepository.save(cart);
    }

    private String getLoggedUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails)authentication.getPrincipal()).getUsername();
        }
        return null;
    }

    @Override
    public void updateUserPersonalInformation(String firstName, String lastName, String password) {
        User user = getLoggedUserModel();
        if (StringUtils.isNotBlank(firstName)) {
            user.setFirstName(firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            user.setLastName(lastName);
        }
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
        }
        userRepository.save(user);
    }

    @Override
    public User getLoggedUserModel() {
        return userRepository.findByEmailEquals(getLoggedUserUsername()).orElse(null);
    }
}
