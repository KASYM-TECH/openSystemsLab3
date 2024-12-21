package tim.labs.labs.service.interfaces;

import tim.labs.labs.database.entity.RoleRequest;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.exception.ForbiddenException;
import tim.labs.labs.exception.UserDoesNotExistException;

import java.util.List;


public interface IUserService {
    User findUserByUserNameAndPassword(String username, String password);
    RoleRequest requestAccess(User user, Role role);
    List<RoleRequest> getUsersByRole(Role role, String token) throws UserDoesNotExistException, ForbiddenException;
    void addUser(User user);
    void fulfillRoleRequest(Long roleRequestId, String token) throws UserDoesNotExistException, ForbiddenException;
}
