package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.dto.UserDto;
import hr.tvz.diplomski.webshop.dto.request.RegisterCustomerRequest;
import hr.tvz.diplomski.webshop.security.JWTToken;
import hr.tvz.diplomski.webshop.security.LoginDto;
import hr.tvz.diplomski.webshop.security.TokenProvider;
import hr.tvz.diplomski.webshop.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private TokenProvider tokenProvider;

    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<JWTToken> authenticate(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getLoggedUser() {
        return userService.getLoggedUser();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody final RegisterCustomerRequest request) {
        userService.registerNewCustomer(request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName());
    }
}
