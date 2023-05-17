package com.example.dtod.user.service;
import com.example.dtod.exception.BusinessException;
import com.example.dtod.fileUpload.S3Service;
import com.example.dtod.post.dto.request.PostUpdateRequestDto;
import com.example.dtod.post.dto.response.PostUpdateResponseDto;
import com.example.dtod.post.entity.Post;
import com.example.dtod.response.ErrorMessage;
import com.example.dtod.user.dto.request.UserSignInRequestDto;
import com.example.dtod.user.dto.request.UserUpdateRequestDto;
import com.example.dtod.user.dto.response.UserUpdateResponseDto;
import com.example.dtod.user.entity.User;
import com.example.dtod.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Uploader;

    public Long signInUser(UserSignInRequestDto userSignInRequestDto) {
        if(userRepository.existsUserByUserId(userSignInRequestDto.getUserId())){
            return (long) -1;
        }

        User signInUser = User.builder()
                .userId(userSignInRequestDto.getUserId())
                .password(userSignInRequestDto.getPassword())
                .nickname(userSignInRequestDto.getNickname())
                .category(userSignInRequestDto.getCategory())
                .build();

        userRepository.save(signInUser);

        return signInUser.getId();
    }

    public Boolean delete(Long id) {
        if(!userRepository.existsUserById(id))
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);

        userRepository.deleteById(id);
        return true;
    }

    public UserUpdateResponseDto update(UserUpdateRequestDto userUpdateRequestDto,
                                        MultipartFile file) throws IOException {
        User user = userRepository.findById(userUpdateRequestDto.getId()).get();
        String imgUrl = s3Uploader.upload(file);

        user.update(userUpdateRequestDto.getUserId(), userUpdateRequestDto.getPassword(), userUpdateRequestDto.getNickname(),userUpdateRequestDto.getCategory(), imgUrl);

        return new UserUpdateResponseDto(user.getId());
    }
}
