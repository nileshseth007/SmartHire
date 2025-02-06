package com.neom108.SmartHire.repository;

import com.neom108.SmartHire.entity.JobPostActivity;
import com.neom108.SmartHire.entity.JobSeekerApply;
import com.neom108.SmartHire.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
