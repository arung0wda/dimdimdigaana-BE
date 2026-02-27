package com.arp.dimdimdigaana.user.exception;

import com.arp.dimdimdigaana.exception.AppException;
import com.arp.dimdimdigaana.exception.ErrorCode;

public class UserNotFoundException extends AppException {

    public UserNotFoundException(Long id) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id);
    }

    public UserNotFoundException(Long id, Throwable cause) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id, cause);
    }
}

