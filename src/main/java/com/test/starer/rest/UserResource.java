package com.test.starer.rest;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserResource {

    @GetMapping("/greeting")
    public String greeting(){
        return "Hello World";
    }

    @PostMapping("/greeting")
    public String makeGreeting(@RequestParam String name, @RequestBody Profile profile){
        return "Hello World" + name + profile.gender;
    }


    @PutMapping ("/greeting/{name}")
    public String putGreeting(@PathVariable String name){
        return "Hello World" + name;
    }

    /**
     * 定义一个静态内部类
     */
    @Data
    static class  Profile{
        private String gender;
        private String idNo;
    }
}
