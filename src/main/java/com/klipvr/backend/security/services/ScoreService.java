package com.klipvr.backend.security.services;

import com.klipvr.backend.models.User;
import com.klipvr.backend.models.UserInfo;
import com.klipvr.backend.repository.UserRepository;
import com.klipvr.backend.repository.UserinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ScoreService
{

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserinfoRepository userinfoRepository;
    public Boolean levelup(UserDetailsImpl userDto1) throws NullPointerException
    {
         User currentUser = userRepository.findByEmail(userDto1.getEmail());
         Long id = currentUser.getId();
         Optional<UserInfo> userInfo = userinfoRepository.findById(id);
         UserInfo userInfo1 = userInfo.get();
         Long score = userInfo1.getTask_done();
         score++;
         userInfo1.setTask_done(score);
         Long lev = userInfo1.getLevel();
         Long score_needed = ((2*5*(lev+1))/2 + ((lev+1)*lev*5)/2);
         // used arithmetic progression here
         // we need score=50 for level 4
         //  50 = 2*[ 10 + 15 ]
         //  Long cur_lev = userInfo1.getLevel();
         if(Objects.equals(score_needed, score))
         {
             userInfo1.setLevel(lev+1);
             userinfoRepository.save(userInfo1);
             // userinfoRepository.
             return true;
         }else
         {
             userinfoRepository.save(userInfo1);
             return false;
         }
        //return false;
    }
}
