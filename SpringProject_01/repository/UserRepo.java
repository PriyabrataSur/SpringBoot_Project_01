package in.sigma.SpringProject_01.repository;

import in.sigma.SpringProject_01.entity.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Integer> {
    public User findByEmail(String email);
}
