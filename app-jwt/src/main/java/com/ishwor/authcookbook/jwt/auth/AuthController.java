package com.ishwor.authcookbook.jwt.auth;


import com.ishwor.authcookbook.common.auth.AppUser;
import com.ishwor.authcookbook.common.auth.UserRepository;
import com.ishwor.authcookbook.common.auth.UserRole;
import com.ishwor.authcookbook.jwt.auth.dto.LoginRequest;
import com.ishwor.authcookbook.jwt.auth.dto.RegisterRequest;
import com.ishwor.authcookbook.jwt.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthController(UserRepository users,PasswordEncoder encoder,TokenService tokenService){
        this.encoder = encoder;
        this.users = users;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequest request){
        if(users.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered.");
        }
        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole(UserRole.USER);
        users.save(user);

    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request){
        AppUser user = users.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));
        if(!encoder.matches(request.password(),user.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
        }

        return TokenResponse.bearer(tokenService.issueAccessToken(user));
    }


}
