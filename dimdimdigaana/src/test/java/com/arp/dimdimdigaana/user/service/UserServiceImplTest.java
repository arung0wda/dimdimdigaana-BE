package com.arp.dimdimdigaana.user.service;

import com.arp.dimdimdigaana.user.dto.UserRequestDto;
import com.arp.dimdimdigaana.user.dto.UserResponseDto;
import com.arp.dimdimdigaana.user.entity.UserEntity;
import com.arp.dimdimdigaana.user.exception.UserNotFoundException;
import com.arp.dimdimdigaana.user.exception.UsernameAlreadyExistsException;
import com.arp.dimdimdigaana.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * Pure unit tests for {@link UserServiceImpl}.
 * No Spring context is loaded — all dependencies are mocked with Mockito.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // ── Fixtures ──────────────────────────────────────────────────

    private static final Long   USER_ID   = 1L;
    private static final String USERNAME  = "johndoe";
    private static final String FIRST     = "John";
    private static final String LAST      = "Doe";
    private static final LocalDate DOB    = LocalDate.of(1990, 1, 15);

    private UserRequestDto requestDto;
    private UserEntity     savedEntity;

    @BeforeEach
    void setUp() {
        requestDto = UserRequestDto.builder()
                .username(USERNAME)
                .firstName(FIRST)
                .lastName(LAST)
                .dob(DOB)
                .build();

        savedEntity = UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST)
                .lastName(LAST)
                .dob(DOB)
                .build();
    }

    // ══════════════════════════════════════════════════════════════
    // createUser
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("creates and returns user when username is unique")
        void createUser_success() {
            given(userRepository.existsByUsername(USERNAME)).willReturn(false);
            given(userRepository.save(any(UserEntity.class))).willReturn(savedEntity);

            UserResponseDto result = userService.createUser(requestDto);

            assertThat(result.getId()).isEqualTo(USER_ID);
            assertThat(result.getUsername()).isEqualTo(USERNAME);
            assertThat(result.getFirstName()).isEqualTo(FIRST);
            assertThat(result.getLastName()).isEqualTo(LAST);
            assertThat(result.getDob()).isEqualTo(DOB);

            then(userRepository).should().save(any(UserEntity.class));
        }

        @Test
        @DisplayName("throws UsernameAlreadyExistsException when username is taken")
        void createUser_duplicateUsername_throws() {
            given(userRepository.existsByUsername(USERNAME)).willReturn(true);

            assertThatThrownBy(() -> userService.createUser(requestDto))
                    .isInstanceOf(UsernameAlreadyExistsException.class)
                    .hasMessageContaining(USERNAME);

            then(userRepository).should(never()).save(any());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getUserById
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("returns user when found")
        void getUserById_found() {
            given(userRepository.findById(USER_ID)).willReturn(Optional.of(savedEntity));

            UserResponseDto result = userService.getUserById(USER_ID);

            assertThat(result.getId()).isEqualTo(USER_ID);
            assertThat(result.getUsername()).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("throws UserNotFoundException when id does not exist")
        void getUserById_notFound_throws() {
            given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(USER_ID.toString());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // getAllUsers
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("returns all users as DTOs")
        void getAllUsers_returnsAll() {
            UserEntity second = UserEntity.builder()
                    .id(2L).username("jane").firstName("Jane")
                    .lastName("Doe").dob(DOB).build();

            given(userRepository.findAll()).willReturn(List.of(savedEntity, second));

            List<UserResponseDto> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(UserResponseDto::getUsername)
                    .containsExactly(USERNAME, "jane");
        }

        @Test
        @DisplayName("returns empty list when no users exist")
        void getAllUsers_empty() {
            given(userRepository.findAll()).willReturn(List.of());

            assertThat(userService.getAllUsers()).isEmpty();
        }
    }

    // ══════════════════════════════════════════════════════════════
    // updateUser
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("updates user when same username is kept")
        void updateUser_sameUsername_success() {
            given(userRepository.findById(USER_ID)).willReturn(Optional.of(savedEntity));
            given(userRepository.save(any(UserEntity.class))).willReturn(savedEntity);

            UserResponseDto result = userService.updateUser(USER_ID, requestDto);

            assertThat(result.getUsername()).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("updates user when new username is available")
        void updateUser_newUniqueUsername_success() {
            UserRequestDto newRequest = UserRequestDto.builder()
                    .username("newname").firstName(FIRST).lastName(LAST).dob(DOB).build();
            UserEntity updatedEntity = UserEntity.builder()
                    .id(USER_ID).username("newname").firstName(FIRST).lastName(LAST).dob(DOB).build();

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(savedEntity));
            given(userRepository.existsByUsername("newname")).willReturn(false);
            given(userRepository.save(any(UserEntity.class))).willReturn(updatedEntity);

            UserResponseDto result = userService.updateUser(USER_ID, newRequest);

            assertThat(result.getUsername()).isEqualTo("newname");
        }

        @Test
        @DisplayName("throws UserNotFoundException when user does not exist")
        void updateUser_notFound_throws() {
            given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(USER_ID, requestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(USER_ID.toString());
        }

        @Test
        @DisplayName("throws UsernameAlreadyExistsException when new username is taken")
        void updateUser_newUsernameTaken_throws() {
            UserRequestDto newRequest = UserRequestDto.builder()
                    .username("taken").firstName(FIRST).lastName(LAST).dob(DOB).build();

            given(userRepository.findById(USER_ID)).willReturn(Optional.of(savedEntity));
            given(userRepository.existsByUsername("taken")).willReturn(true);

            assertThatThrownBy(() -> userService.updateUser(USER_ID, newRequest))
                    .isInstanceOf(UsernameAlreadyExistsException.class)
                    .hasMessageContaining("taken");

            then(userRepository).should(never()).save(any());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // deleteUser
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("deletes user when found")
        void deleteUser_success() {
            given(userRepository.existsById(USER_ID)).willReturn(true);

            userService.deleteUser(USER_ID);

            then(userRepository).should().deleteById(USER_ID);
        }

        @Test
        @DisplayName("throws UserNotFoundException when id does not exist")
        void deleteUser_notFound_throws() {
            given(userRepository.existsById(USER_ID)).willReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(USER_ID.toString());

            then(userRepository).should(never()).deleteById(any());
        }
    }
}

