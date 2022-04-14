package com.turboparser.turbo.aop;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyAopController {

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/hello"
    )
    public ResponseEntity<?> getHello(
            @RequestParam("name") String username
    ) {
        return ResponseEntity.ok("Hello, " + username);
    }

}
