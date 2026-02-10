package com.ishwor.authcookbook.none.common;


import com.ishwor.authcookbook.none.journal.JournalNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExeptionHandler {

    @ExceptionHandler(JournalNotFoundException.class)
    public ProblemDetail notFound(JournalNotFoundException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Not found");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }
}
