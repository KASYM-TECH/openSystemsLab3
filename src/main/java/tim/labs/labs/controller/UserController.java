package tim.labs.labs.controller;

import tim.labs.labs.controller.dto.LoginDTO;
import tim.labs.labs.controller.dto.UserDTO;
import tim.labs.labs.database.entity.RoleRequest;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.exception.InvalidUserCredentialsException;
import tim.labs.labs.exception.UserDoesNotExistException;
import tim.labs.labs.exception.UsernameOccupiedException;
import tim.labs.labs.security.Hasher;
import tim.labs.labs.service.interfaces.IAuthService;
import tim.labs.labs.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final IAuthService authService;
    private final IUserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginDTO> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            userDTO.setPassword(Hasher.hashWithMD5(userDTO.getPassword()));
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRole(userDTO.getRole());
            user.setPassword(userDTO.getPassword());
            return ResponseEntity.ok(authService.authUser(user, response));
        } catch (UserDoesNotExistException e) {
            return ResponseEntity.status(HttpCodes.UNKNOWN_USER.getCode()).build();
        } catch (InvalidUserCredentialsException e) {
            return ResponseEntity.status(HttpCodes.INVALID_USER_CREDENTIALS.getCode()).build();
        }
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<LoginDTO> signup(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            userDTO.setPassword(Hasher.hashWithMD5(userDTO.getPassword()));
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRole(userDTO.getRole());
            user.setPassword(userDTO.getPassword());
            LoginDTO jwtDTO = authService.registerNewUser(user, response);
            return ResponseEntity.ok(jwtDTO);
        } catch (UsernameOccupiedException ex) {
            return ResponseEntity.status(HttpCodes.USERNAME_ALREADY_USED.getCode()).build();
        } catch (UserDoesNotExistException ex) {
            return ResponseEntity.status(HttpCodes.UNKNOWN_USER.getCode()).build();
        } catch (InvalidUserCredentialsException e) {
            return ResponseEntity.status(HttpCodes.INVALID_USER_CREDENTIALS.getCode()).build();
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<RoleRequest>> getRoleRequests(@RequestParam(value = "role") Role role, HttpServletRequest request) {
        try {
            var res = userService.getUsersByRole(role, request.getHeader("Authorization"));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpCodes.FORBIDDEN.getCode()).build();
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> fulfill(@RequestParam(value = "roleRequestId") Long roleRequestId, HttpServletRequest request) {
        try {
            userService.fulfillRoleRequest(roleRequestId, request.getHeader("Authorization"));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpCodes.FORBIDDEN.getCode()).build();
        }
    }
}
