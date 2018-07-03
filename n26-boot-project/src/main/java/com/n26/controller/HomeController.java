package com.n26.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class HomeController.This redirects to
 * Swagger UI Page at port configured in properties file 
 * 
 */
@Controller
@ApiIgnore
public class HomeController
{

    /**
     * Home.
     *
     * @return the string
     */
    @RequestMapping("/")
    public String home()
    {
        return "redirect:swagger-ui.html";
    }

}
