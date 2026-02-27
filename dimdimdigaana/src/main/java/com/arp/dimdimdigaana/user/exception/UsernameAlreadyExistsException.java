package com.arp.dimdimdigaana.user.exception;

import com.arp.dimdimdigaana.exception.AppException;
import com.arp.dimdimdigaana.exception.ErrorCode;

public class UsernameAlreadyExistsException extends AppException {

    public UsernameAlreadyExistsException(String username) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists: " + username);
    }

    public UsernameAlreadyExistsException(String username, Throwable cause) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists: " + username, cause);
    }
}

