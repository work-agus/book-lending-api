package com.demandlane.booklending;

import com.demandlane.booklending.auth.controller.AuthController;
import com.demandlane.booklending.book.controller.BookController;
import com.demandlane.booklending.loan.controller.LoanController;
import com.demandlane.booklending.member.controller.MemberController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.demandlane.booklending.controller.PingController;


@SpringBootApplication
// We use direct @Import instead of @ComponentScan to speed up cold starts
// @ComponentScan(basePackages = "org.example.controller")
@Import({
        PingController.class,
        AuthController.class,
        BookController.class,
        MemberController.class,
        LoanController.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}