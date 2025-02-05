package com.neom108.SmartHire.services;

import com.neom108.SmartHire.entity.*;
import com.neom108.SmartHire.repository.JobPostActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity){
        return jobPostActivityRepository.save(jobPostActivity);
    }

    //DTO - data transfer object - holder for data
    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter){

        List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);

        List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();

        //converting list of data received to dtos
        for (IRecruiterJobs rec: recruiterJobsDtos){
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
            recruiterJobsDtoList.add(new RecruiterJobsDto(rec.getTotalCandidates(),rec.getJob_post_id(),rec.getJob_title(),loc,comp));
        }

        return recruiterJobsDtoList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()-> new RuntimeException("Job not found"));
    }
}
