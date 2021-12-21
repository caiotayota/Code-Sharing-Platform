package com.caiotayota.platform.service;

import com.caiotayota.platform.entity.CodeSnippet;
import com.caiotayota.platform.exceptions.CodeSnippetNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.caiotayota.platform.repository.CodeRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodeService {
    
    private final CodeRepository codeRepo;
    
    @Autowired
    public CodeService(CodeRepository codeRepo) {
        this.codeRepo = codeRepo;
    }
    
    public void saveCode(CodeSnippet codeSnippet) {
        codeSnippet.setDate(LocalDateTime.now());
        codeSnippet.setTimeRestricted(codeSnippet.getTime() != 0);
        codeSnippet.setViewRestricted(codeSnippet.getViews() != 0);
        codeRepo.save(codeSnippet);
    }
    
    public CodeSnippet getCode(String id) {
        CodeSnippet codeSnippet = codeRepo.findById(id)
                .orElseThrow(CodeSnippetNotFoundException::new);
        
        if (codeSnippet.isTimeRestricted() || codeSnippet.isViewRestricted()) {
            updateTimeAndViews(codeSnippet);
        }
        
        return codeSnippet;
    }
    
    public List<CodeSnippet> getLatestCodeSnippets() {
        List<CodeSnippet> codeSnippetList =
                codeRepo.findAllByTimeRestrictedAndViewRestrictedOrderByDateDesc(false, false);
        return codeSnippetList.size() > 10
                ? codeSnippetList.subList(0, 10)
                : codeSnippetList;
    }
    
    public List<CodeSnippet> findLastCodeSnippets(int count) {
        List<CodeSnippet> lastSnippets = codeRepo.findLastSnippets(PageRequest.of(0, count));
        return lastSnippets.stream()
                .map(snippet -> {
                    snippet.setViews(0);
                    snippet.setTime(0L);
                    return snippet;
                })
                .collect(Collectors.toList());
    }
    
    
    
    public void updateTimeAndViews(CodeSnippet codeSnippet) {
        if (codeSnippet.isViewRestricted()) {
            if (codeSnippet.getViews() == 0) {
                codeRepo.deleteById(codeSnippet.getId());
                throw new CodeSnippetNotFoundException();
            }
            
            codeSnippet.setViews(codeSnippet.getViews() - 1);
            codeRepo.save(codeSnippet);
        }
        
        if (codeSnippet.isTimeRestricted()) {
            long difference = LocalDateTime.now()
                    .until(codeSnippet.getDate().plusSeconds(codeSnippet.getTime()), ChronoUnit.SECONDS);
            if (difference <= 0) {
                codeRepo.deleteById(codeSnippet.getId());
                throw new CodeSnippetNotFoundException();
            }
    
            codeSnippet.setTime(difference);
        }
        
    }
}
