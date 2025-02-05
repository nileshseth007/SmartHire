package com.neom108.SmartHire.controller;

import com.neom108.SmartHire.entity.RecruiterProfile;
import com.neom108.SmartHire.entity.Users;
import com.neom108.SmartHire.repository.RecruiterProfileRepository;
import com.neom108.SmartHire.repository.UsersRepository;
import com.neom108.SmartHire.services.RecruiterProfileService;
import com.neom108.SmartHire.services.UsersService;
import com.neom108.SmartHire.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;
    private final UsersService usersService;

    public RecruiterProfileController(RecruiterProfileService recruiterProfileService, UsersService usersService) {
        this.recruiterProfileService = recruiterProfileService;
        this.usersService = usersService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUserName = authentication.getName();

            Users users = usersService.getUserByEmail(currentUserName).orElseThrow(()-> new UsernameNotFoundException("Unable to find user"));

            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());

            if(!recruiterProfile.isEmpty()){
                model.addAttribute("profile", recruiterProfile.get());
            }
        }

        return "recruiter_profile";
    }


    //creates a new recruiter profile (in memory) based on form data
    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersService.getUserByEmail(currentUsername).orElseThrow(()-> new UsernameNotFoundException("Unable to find user"));

            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }

        model.addAttribute("profile",recruiterProfile);

        // processing file upload for recruiter profile image
        String fileName = "";

        if(!multipartFile.getOriginalFilename().equals("")){

            //retrieving the file name of the user
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }

        // save the profile
        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        //setting upload directory to save the profile image
        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId()+"/";

        try{
            //save the image on server in the directory photos/recruiter
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return "redirect:/dashboard/";
    }




}
