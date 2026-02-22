package com.arp.dimdimdigaana.user.service;

import com.arp.dimdimdigaana.user.dto.UserRequestDto;
import com.arp.dimdimdigaana.user.dto.UserResponseDto;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import com.arp.dimdimdigaana.user.exception.UserNotFoundException;
import com.arp.dimdimdigaana.user.exception.UsernameAlreadyExistsException;
import com.arp.dimdimdigaana.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_LOG = "UserEntity not found with id: {}";

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("Attempting to create userEntity with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dob(request.getDob())
                .build();

        UserEntity saved = userRepository.save(userEntity);
        log.info("UserEntity created successfully with id: {}", saved.getId());

        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching userEntity with id: {}", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(USER_NOT_FOUND_LOG, id);
                    return new UserNotFoundException(id);
                });

        return toResponseDto(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all users");

        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();

        log.info("Total users fetched: {}", users.size());
        return users;
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        log.info("Attempting to update userEntity with id: {}", id);

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(USER_NOT_FOUND_LOG, id);
                    return new UserNotFoundException(id);
                });

        // Check uniqueness only if the username is being changed
        if (!userEntity.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            log.warn("Cannot update: username already exists: {}", request.getUsername());
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        userEntity.setUsername(request.getUsername());
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setDob(request.getDob());

        UserEntity updated = userRepository.save(userEntity);
        log.info("UserEntity updated successfully with id: {}", updated.getId());

        return toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn(USER_NOT_FOUND_LOG, id);
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("UserEntity deleted successfully with id: {}", id);
    }

    private UserResponseDto toResponseDto(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .dob(userEntity.getDob())
                .build();
    }
}





