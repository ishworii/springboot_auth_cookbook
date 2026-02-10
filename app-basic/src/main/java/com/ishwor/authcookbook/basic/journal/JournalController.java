package com.ishwor.authcookbook.basic.journal;


import com.ishwor.authcookbook.common.journal.Journal;
import com.ishwor.authcookbook.common.journal.JournalRepository;
import com.ishwor.authcookbook.common.journal.dto.JournalCreateRequest;
import com.ishwor.authcookbook.common.journal.dto.JournalUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {
    private final JournalRepository journalRepository;

    public JournalController(JournalRepository journalRepository){
        this.journalRepository = journalRepository;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<Journal> getAll(){
        return journalRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Journal create(@RequestBody @Valid JournalCreateRequest request){
        Journal newJournal = new Journal();
        newJournal.setTitle(request.title());
        newJournal.setContent(request.content());
        return journalRepository.save(newJournal);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public Journal getJournal(@PathVariable Long id){
        return journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public Journal updateJournal(@PathVariable Long id, @RequestBody @Valid JournalUpdateRequest request){
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException(id));
        existingJournal.setTitle(request.title());
        existingJournal.setContent(request.content());
        return journalRepository.save(existingJournal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteJournal(@PathVariable Long id){
        if(journalRepository.findById(id).isEmpty()) throw new JournalNotFoundException(id);
        journalRepository.deleteById(id);
    }
}
