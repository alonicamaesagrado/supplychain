package repository;

import enums.StatusEnum;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author nica
 */
public interface UserRepository extends JpaRepository<User, Integer>{
    
    
    public User findByUserCodeAndStatus(String userCode, StatusEnum status);
}
