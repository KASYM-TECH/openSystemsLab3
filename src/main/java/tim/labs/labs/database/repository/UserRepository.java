package tim.labs.labs.database.repository;

import tim.labs.labs.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users WHERE password=:password AND username=:name LIMIT 1", nativeQuery = true)
    User findByNamePassword(@Param("name") String name, @Param("password") String password);

    @Modifying
    @Query(value = "UPDATE users SET role=:role WHERE id=:user_id", nativeQuery = true)
    void changeStatus(@Param("user_id") Long userId, @Param("role") String role);

    User findByUsername(String username);
}
