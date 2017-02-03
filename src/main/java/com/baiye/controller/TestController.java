package com.baiye.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Baiye on 01/02/2017.
 */

@Controller
public class TestController {

    @RequestMapping("/healthCheck")
    @ResponseBody
    public String home(){
        return "";
    }
}