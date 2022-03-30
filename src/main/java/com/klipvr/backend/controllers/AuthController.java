package com.klipvr.backend.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.klipvr.backend.models.*;
import com.klipvr.backend.payload.request.PasswordDto;
import com.klipvr.backend.payload.request.TokenRefreshRequest;
import com.klipvr.backend.payload.response.TokenRefreshResponse;
import com.klipvr.backend.repository.UserinfoRepository;
import com.klipvr.backend.security.exception.TokenRefreshException;
import com.klipvr.backend.security.services.ISecurityUserService;
import com.klipvr.backend.security.services.RefreshTokenService;
import com.klipvr.backend.security.services.UserService;
//import jdk.jshell.spi.ExecutionControl;
import com.klipvr.backend.util.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.klipvr.backend.payload.request.LoginRequest;
import com.klipvr.backend.payload.request.SignupRequest;
import com.klipvr.backend.payload.response.JwtResponse;
import com.klipvr.backend.payload.response.MessageResponse;
import com.klipvr.backend.repository.RoleRepository;
import com.klipvr.backend.repository.UserRepository;
import com.klipvr.backend.security.jwt.JwtUtils;
import com.klipvr.backend.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());


  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;
  @Autowired
  UserService userService;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  UserinfoRepository userinfoRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  JwtUtils jwtUtils;
  @Autowired
  private JavaMailSender mailSender;
  @Autowired
  private ISecurityUserService securityUserService;

  @Autowired
  private MessageSource messages;
  @Autowired
  private Environment env;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
            .collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            })
            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role ->
      {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            //   System.out.println(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);
    UserInfo userInfo = new UserInfo(0L, 0L, user.getUsername(), user.getId(), 0L, 0L, 0L);
    userinfoRepository.save(userInfo);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/resetPassword")
  public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail)
  {
    final User user = userService.findUserByEmail(userEmail);
    if (user != null) {
      final String token = UUID.randomUUID().toString();
      userService.createPasswordResetTokenForUser(user, token);
      mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
    }
    //messages.
    return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
  }

  @PostMapping("/savePassword")
  public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

    final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

    if(result != null) {
      return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
    }

    Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
    if(user.isPresent())
    {
      userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
      return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    } else
    {
      return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
    }
  }



  // NON API'S
  private String getAppUrl(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
  }
  private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
    final String url = contextPath + "/changePassword?token=" + token;
    final String message = messages.getMessage("message.resetPassword", null, locale);
    return constructEmail("Reset Password", message + " \r\n" + url, user);
  }
  private SimpleMailMessage constructEmail(String subject, String body, User user) {
    final SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(user.getEmail());
    email.setFrom(env.getProperty("support.email"));
    return email;
  }




/*
  private String getAppUrl(HttpServletRequest request) {
    return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
  }

  private String getClientIP(HttpServletRequest request) {
    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
  */



}
