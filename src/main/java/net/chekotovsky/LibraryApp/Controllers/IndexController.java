package net.chekotovsky.LibraryApp.Controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController implements ErrorController{

    @GetMapping("/error")
    public String getErrorPath() {
        return "error";
    }


}