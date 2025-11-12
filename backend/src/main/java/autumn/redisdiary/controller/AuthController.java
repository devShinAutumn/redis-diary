
package autumn.redisdiary.controller;

import autumn.redisdiary.entity.User;
import autumn.redisdiary.security.JwtUtil;
import autumn.redisdiary.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        var user = authService.signup(req.username(), req.email(), req.password());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var user = authService.authenticate(req.username(), req.password());
        if (user == null) return ResponseEntity.status(401).body("invalid credentials");
        var token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    record SignupRequest(String username, String email, String password) {}
    record LoginRequest(String username, String password) {}
    record LoginResponse(String token) {}
}
