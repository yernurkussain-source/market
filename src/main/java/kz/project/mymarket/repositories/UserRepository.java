package kz.project.mymarket.repositories;

import kz.project.mymarket.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email) ;
}
