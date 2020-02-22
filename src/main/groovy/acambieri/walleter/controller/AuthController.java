package acambieri.walleter.controller;

import acambieri.walleter.controller.requests.AuthRequest;
import acambieri.walleter.controller.response.AuthResponse;
import acambieri.walleter.model.User;
import acambieri.walleter.model.VO.VOUser;
import acambieri.walleter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static acambieri.walleter.JwtUtils.generateToken;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${token.validity.seconds}")
    private Long tokenValidity;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = generateToken(userDetails,tokenValidity,jwtSecret);
        User user = userRepository.findByUsernameAndEnabledIsTrue(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponse(token,new VOUser(user)
                ));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }


}
