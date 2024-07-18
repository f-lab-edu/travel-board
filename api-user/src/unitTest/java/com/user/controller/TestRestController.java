package com.user.controller;

import com.user.utils.error.CommonException;
import com.user.utils.error.ErrorType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRestController {

    @GetMapping("/common-exception")
    public void throwCommonException() {
        throw new CommonException(ErrorType.DUPLICATED_EMAIL);
    }

    @GetMapping("/runtime-exception")
    public void throwRuntimeException() {
        throw new RuntimeException("Unexpected error");
    }
}
