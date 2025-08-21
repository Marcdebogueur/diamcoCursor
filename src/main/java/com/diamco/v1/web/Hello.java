package com.diamco.v1.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/classes")
@AllArgsConstructor
public class Hello {
    @GetMapping
    public String helloWord( ) {

        return ("Hello");
    }


}
