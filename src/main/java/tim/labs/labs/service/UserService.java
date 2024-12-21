package tim.labs.labs.service;

import tim.labs.labs.database.entity.RoleRequest;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.database.repository.RoleRequestRepository;
import tim.labs.labs.database.repository.UserRepository;
import tim.labs.labs.exception.ForbiddenException;
import tim.labs.labs.exception.UserDoesNotExistException;
import tim.labs.labs.security.jwt.service.IJwtService;
import tim.labs.labs.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private UserRepository repository;
    private RoleRequestRepository roleRequestRepository;
    private IJwtService jwtService;

    @Override
    public User findUserByUserNameAndPassword(String username, String password) {
        return repository.findByNamePassword(username, password);
    }

    @Override
    public void addUser(User user) {
        repository.save(user);
    }

    @Override
    public RoleRequest requestAccess(User user, Role role) {
        var rq = new RoleRequest();
        rq.setUser(user);
        rq.setRole(role);
        roleRequestRepository.save(rq);
        return rq;
    }

    @Override
    public List<RoleRequest> getUsersByRole(Role role, String token) throws UserDoesNotExistException, ForbiddenException {
        var userId = jwtService.getUserIdFromToken(token);
        var user = repository.findById(userId);
        if(user.isEmpty()) throw new UserDoesNotExistException();

        if(user.get().getRole() != Role.Admin) {
            throw new ForbiddenException();
        }

        return roleRequestRepository.getByRole(role);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional
    @Override
    public void fulfillRoleRequest(Long roleRequestId, String token) throws UserDoesNotExistException, ForbiddenException {
        var userId = jwtService.getUserIdFromToken(token);
        var user = repository.findById(userId);
        if(user.isEmpty()) throw new UserDoesNotExistException();

        var fetchedRoleRequest = roleRequestRepository.findById(roleRequestId).orElseThrow();

        if(user.get().getRole() != Role.Admin) {
            throw new ForbiddenException();
        }

        var role = roleRequestRepository.fulfill(fetchedRoleRequest.getId());
        userRepository.changeStatus(fetchedRoleRequest.getUser().getId(), role.toString());

//        if(fetchedRoleRequest.getRole() == Role.Admin){
//            webSocketController.update(String.valueOf(fetchedRoleRequest.getUser().getId()));
//        }
    }

    @Transactional
    public void insertUsers(List<User> userList) {
        for (User user : userList) {
            if (!user.Validate()) {
                throw new IllegalArgumentException("Validation failed for user: " + user);
            }
        }
        userRepository.saveAll(userList);
    }
}
