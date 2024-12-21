package tim.labs.labs.service;

import tim.labs.labs.controller.dto.LoginDTO;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.exception.InvalidUserCredentialsException;
import tim.labs.labs.exception.UserDoesNotExistException;
import tim.labs.labs.exception.UsernameOccupiedException;
import tim.labs.labs.security.jwt.service.IJwtService;
import tim.labs.labs.service.interfaces.IAuthService;
import tim.labs.labs.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {
    private final IUserService userService;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public synchronized LoginDTO registerNewUser(User user, HttpServletResponse response) throws UsernameOccupiedException, UserDoesNotExistException, InvalidUserCredentialsException {
        if (userService.findUserByUserNameAndPassword(user.getUsername(), user.getPassword()) != null) {
            throw new UsernameOccupiedException();
        }
        if(user.getRole() == Role.Admin) {
            user.setRole(Role.User);
            var rq = userService.requestAccess(user, Role.Admin);
            return authUser(rq.getUser(), response);
        }
        userService.addUser(user);
        return authUser(user, response);
    }

    @Override
    @Transactional
    public LoginDTO authUser(User user, HttpServletResponse response) throws UserDoesNotExistException, InvalidUserCredentialsException {
        var foundUser = userService.findUserByUserNameAndPassword(user.getUsername(), user.getPassword());
        if (foundUser == null) {
            throw new UserDoesNotExistException();
        }
        final String token = jwtService.generateAccessToken(foundUser.getUsername(), foundUser.getId(), foundUser.getRole());
        return new LoginDTO(foundUser.getId(), foundUser.getUsername(), token, foundUser.getRole());
    }
}
