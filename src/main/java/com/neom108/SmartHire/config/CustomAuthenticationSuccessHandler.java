package com.neom108.SmartHire.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // handle to the logged-in user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Username: "+username+" is logged in.");

        //check if their role is job seeker
        Boolean hasJobSeekerRole = authentication.getAuthorities().stream().anyMatch((r-> r.getAuthority().equals("Job Seeker")));

        //check if their role is recruiter
        boolean hasRecruiterRole = authentication.getAuthorities().stream().anyMatch((r-> r.getAuthority().equals("Recruiter")));

        if (hasJobSeekerRole || hasRecruiterRole) response.sendRedirect("/dashboard/");

    }
}
