package com.arp.dimdimdigaana.user.service;

import com.arp.dimdimdigaana.user.dto.SearchCriteria;
import com.arp.dimdimdigaana.user.dto.UserRequestDto;
import com.arp.dimdimdigaana.user.dto.UserResponseDto;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import com.arp.dimdimdigaana.user.exception.UserNotFoundException;
import com.arp.dimdimdigaana.user.exception.UsernameAlreadyExistsException;
import com.arp.dimdimdigaana.user.repository.UserRepository;
import com.arp.dimdimdigaana.user.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("Attempting to create userEntity with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
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
                .orElseThrow(() -> new UserNotFoundException(id));

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
                .orElseThrow(() -> new UserNotFoundException(id));

        // Check uniqueness only if the username is being changed
        if (!userEntity.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        userEntity.setUsername(request.getUsername());
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setDob(request.getDob());

        // No explicit save() needed — the entity is managed; Hibernate flushes on commit.
        log.info("UserEntity updated successfully with id: {}", id);

        return toResponseDto(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);

        // findById confirms existence and throws before we touch deleteById,
        // avoiding the TOCTOU race of the original existsById + deleteById pattern.
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.deleteById(id);
        log.info("UserEntity deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(List<SearchCriteria> criteria) {
        log.info("Searching users with {} criteria", criteria == null ? 0 : criteria.size());

        Specification<UserEntity> spec = UserSpecification.buildFromCriteria(criteria);

        List<UserResponseDto> results = userRepository.findAll(spec)
                .stream()
                .map(this::toResponseDto)
                .toList();

        log.info("Search returned {} users", results.size());
        return results;
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





