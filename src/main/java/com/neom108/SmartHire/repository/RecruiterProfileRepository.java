package com.neom108.SmartHire.repository;

import com.neom108.SmartHire.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Integer> {
}
