package io.magikwytch.movienights.repository;

import io.magikwytch.movienights.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {



}
