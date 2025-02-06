package com.neom108.SmartHire.repository;

import com.neom108.SmartHire.entity.JobPostActivity;
import com.neom108.SmartHire.entity.JobSeekerProfile;
import com.neom108.SmartHire.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave,Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    public List<JobSeekerSave> findByJob(JobPostActivity job);
}
