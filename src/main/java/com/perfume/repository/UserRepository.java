package com.perfume.repository;

import com.perfume.entity.User;
import com.perfume.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    User findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndPassword(String username,String password);
}
