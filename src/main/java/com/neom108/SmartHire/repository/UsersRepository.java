package com.neom108.SmartHire.repository;

import com.neom108.SmartHire.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Integer> {

    Optional<Users> findByEmail(String email);
}
