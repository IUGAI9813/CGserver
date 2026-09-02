package com.example.cgserver.domain.user.service;


import com.example.cgserver.domain.user.dto.UserResponse;
import com.example.cgserver.domain.user.entity.UserEntity;
import com.example.cgserver.domain.user.repository.UserRepository;
import com.example.cgserver.global.error.BusinessException;
import com.example.cgserver.global.error.ErrorCode;
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
                 .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다 : " + email));

                 
         return  UserResponse.fromEntity(user);
     }

}
