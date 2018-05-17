package io.cobla.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoblaRestController {

    @GetMapping
    public String test(){

        return "test";
    }
}
