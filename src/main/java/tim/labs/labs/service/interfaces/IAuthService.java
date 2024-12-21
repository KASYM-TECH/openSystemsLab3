package tim.labs.labs.service.interfaces;

import tim.labs.labs.controller.dto.LoginDTO;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.exception.InvalidUserCredentialsException;
import tim.labs.labs.exception.UserDoesNotExistException;
import tim.labs.labs.exception.UsernameOccupiedException;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    LoginDTO registerNewUser(User user, HttpServletResponse response) throws UsernameOccupiedException, UserDoesNotExistException, InvalidUserCredentialsException;
    LoginDTO authUser(User user, HttpServletResponse response) throws UserDoesNotExistException, InvalidUserCredentialsException;
}
