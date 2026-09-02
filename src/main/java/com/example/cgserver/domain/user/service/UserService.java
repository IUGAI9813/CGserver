package com.example.cgserver.domain.user.service;


import com.example.cgserver.domain.user.dto.UserResponse;
import com.example.cgserver.domain.user.entity.UserEntity;
import com.example.cgserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;


     public List<UserResponse> getAllUsers(){
         return  userRepository.findAll().stream().map(UserResponse::fromEntity).toList();
     }

     public UserResponse getUser(String email){
         UserEntity user =  userRepository.findByEmail(email)
                 .orElseThrow(() -> new RuntimeException("User not found : " + email));
         return  UserResponse.fromEntity(user);
     }

}
