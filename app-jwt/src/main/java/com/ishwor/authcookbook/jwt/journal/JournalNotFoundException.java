package com.ishwor.authcookbook.jwt.journal;

public class JournalNotFoundException extends RuntimeException{
    public JournalNotFoundException(Long id){
        super("Journal not found: " + id);
    }
}
