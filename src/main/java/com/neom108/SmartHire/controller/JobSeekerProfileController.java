package com.neom108.SmartHire.controller;

import com.neom108.SmartHire.entity.JobSeekerProfile;
import com.neom108.SmartHire.entity.Skills;
import com.neom108.SmartHire.entity.Users;
import com.neom108.SmartHire.services.JobSeekerProfileService;
import com.neom108.SmartHire.services.UsersService;
import com.neom108.SmartHire.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;

    private final UsersService usersService;


    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersService usersService) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersService = usersService;
    }

    @GetMapping("/")
    public String JobSeekerProfile(Model model){

        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users user = usersService.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());

            if(seekerProfile.isPresent()){
                jobSeekerProfile = seekerProfile.get();
                if(jobSeekerProfile.getSkills().isEmpty()){
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }

            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);


        }
        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile,
                         @RequestParam("image")MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //check is user is not anonymous
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            //find the current user
            Users user = usersService.findByEmail(authentication.getName()).orElseThrow(()->
                    new UsernameNotFoundException("User not found"));

            //map user to profile
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());

        }

        List<Skills> skillsList = new ArrayList<>();
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);

        //map skills to the job seeker profile
        for(Skills skills: jobSeekerProfile.getSkills()){
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName ="";
        String resumeName = "";

        // get the file name of image and set profile photo of that profile
        if(!Objects.equals(image.getOriginalFilename(),"")){
            imageName = StringUtils.cleanPath((Objects.requireNonNull(image.getOriginalFilename())));
            jobSeekerProfile.setProfilePhoto(imageName);

        }

        // get the file name of pdf and set resume of that profile
        if(!Objects.equals(pdf.getOriginalFilename(),"")){
            resumeName = StringUtils.cleanPath((Objects.requireNonNull(pdf.getOriginalFilename())));
            jobSeekerProfile.setResume(resumeName);

        }

        // save the profile in the database
        JobSeekerProfile seekerProfile = jobSeekerProfileService.addNew(jobSeekerProfile);

        try{
            //define directory to store profile photo and resume ac to account it
            String uploadDir = "photos/candidate/"+jobSeekerProfile.getUserAccountId();
            //save the image
            if (!Objects.equals(image.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,imageName,image);
            }

            if (!Objects.equals(pdf.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,resumeName,pdf);
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return "redirect:/dashboard/";
    }
}
