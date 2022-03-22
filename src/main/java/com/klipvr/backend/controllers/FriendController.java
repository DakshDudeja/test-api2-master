package com.klipvr.backend.controllers;

import com.klipvr.backend.models.User;
import com.klipvr.backend.payload.request.AddFriend;
import com.klipvr.backend.payload.request.UserName;
import com.klipvr.backend.repository.UserRepository;
import com.klipvr.backend.security.services.FriendService;
import com.klipvr.backend.security.services.ScoreService;
import com.klipvr.backend.security.services.SecurityService;
import com.klipvr.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test/user")
public class FriendController
{
    @Autowired
    FriendService friendService;
    @Autowired
    SecurityService securityService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScoreService scoreService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody AddFriend addFriend)
    {
        UserDetailsImpl currentUser =  securityService.getUser();
        //System.out.println(currentUser.getEmail());
        friendService.saveFriend(currentUser, addFriend.getId());
        Long id = addFriend.getId();
        //  return currentUser.getId();
        return ResponseEntity.ok("Friend added successfully");
    }
    @GetMapping("/task")
    public ResponseEntity<String> level()
    {
        UserDetailsImpl currentUser = securityService.getUser();
        Boolean levelup = scoreService.levelup(currentUser);
        if (levelup)
        {
            return new ResponseEntity<String>("Congrats Level Increased",HttpStatus.OK);
        }
        return new ResponseEntity<String>("Complete More Tasks to increase your level",HttpStatus.OK);
    }
    @PostMapping("/add1")
    public ResponseEntity<?> addUser1(@Valid @RequestBody UserName  userName)
    {
        UserDetailsImpl currentUser =  securityService.getUser();
        //  System.out.println(currentUser.getEmail());
        Optional<User> user1=  userRepository.findByUsername(userName.getUsername());
        User user = user1.get();
        friendService.saveFriend1(currentUser,user);
        return ResponseEntity.ok("Friend added successfully");
    }
    @GetMapping("/friends")
    public ResponseEntity<String> isUserFriend(@Valid @RequestBody UserName  userName)
    {
        UserDetailsImpl currentUser =  securityService.getUser();
        //  System.out.println(currentUser.getEmail());
        Optional<User> user1=  userRepository.findByUsername(userName.getUsername());
        User user = user1.get();
        if(friendService.areFriend(currentUser,user))
        {
            return new ResponseEntity<String>("Yes, they're friends", HttpStatus.OK);
        }else
        {
            //return (ResponseEntity<String>) ResponseEntity.badRequest();
            return new ResponseEntity<String>("He hasn't added back", HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("listFriends")
    public ResponseEntity<List<AddFriend>> getFriends()
    {
        List<AddFriend> myFriends = friendService.getFriends();
        return new ResponseEntity<List<AddFriend>>(myFriends, HttpStatus.OK);
    }

}
