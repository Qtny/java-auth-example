package com.auth_example.challenge_service.mfa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mfa")
@RequiredArgsConstructor
@Slf4j
public class MfaController {

    @GetMapping
    public String hello() {
        log.info("hello world");
        return "world";
    }
}
