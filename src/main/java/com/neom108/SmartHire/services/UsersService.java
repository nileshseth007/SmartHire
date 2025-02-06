package com.neom108.SmartHire.services;

import com.neom108.SmartHire.entity.JobSeekerProfile;
import com.neom108.SmartHire.entity.RecruiterProfile;
import com.neom108.SmartHire.entity.Users;
import com.neom108.SmartHire.repository.JobSeekerProfileRepository;
import com.neom108.SmartHire.repository.RecruiterProfileRepository;
import com.neom108.SmartHire.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository,
                        RecruiterProfileRepository recruiterProfileRepository,
                        JobSeekerProfileRepository jobSeekerProfileRepository,
                        PasswordEncoder passwordEncoder) { // Password Encoder from Config file will be injected
        this.usersRepository = usersRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users getCurrentUser() {
        // SecurityContextHolder.getContext() - Retrieves the SecurityContext, which holds security-related information, including authentication details.
        // getAuthentication() - This object represents the logged-in user and contains details such as username, roles, credentials, and authentication status.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){ //determine if the currently authenticated user is an anonymous user (i.e., not logged in) in Spring Security.
            String username =  authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("Could not find user"));

            return users;
        }
        // if user is anonymous
        return null;
    }

    public Users addNew(Users users){
        users.setActive(true);
        users.setregistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users savedUser = usersRepository.save(users);
        int userTypeId = savedUser.getUserTypeId().getUserTypeId();
        if(userTypeId == 1){
            recruiterProfileRepository.save(new RecruiterProfile(savedUser));
        }
        else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
        }

        return savedUser;
    }

    public Optional<Users> findByEmail(String email){
        return usersRepository.findByEmail(email);
    }


    public Object getCurrentUserProfile() {

        // SecurityContextHolder.getContext() - Retrieves the SecurityContext, which holds security-related information, including authentication details.
        // getAuthentication() - This object represents the logged-in user and contains details such as username, roles, credentials, and authentication status.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){ //determine if the currently authenticated user is an anonymous user (i.e., not logged in) in Spring Security.
            String username =  authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("Could not find user"));

            int userId = users.getUserId();
            // returns if user is a recruiter
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
                return recruiterProfile;
            }
            //returns if user is a job seeker
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Job Seeker"))){
                JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                return jobSeekerProfile;
            }
        }
        // if user is anonymous
        return null;
    }
}
