package com.arp.dimdimdigaana.user.service;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.dto.UserRequestDto;
import com.arp.dimdimdigaana.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto request);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(Long id, UserRequestDto request);

    void deleteUser(Long id);

    List<UserResponseDto> searchUsers(List<SearchCriteria> criteria);
}

