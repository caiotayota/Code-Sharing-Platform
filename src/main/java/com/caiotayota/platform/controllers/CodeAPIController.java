package com.caiotayota.platform.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.caiotayota.platform.entity.CodeSnippet;
import com.caiotayota.platform.service.CodeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/code", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeAPIController {
    
    private final CodeService codeService;
    
    @Autowired
    public CodeAPIController(CodeService codeService) {
        this.codeService = codeService;
    }
    
    @GetMapping("/{id}")
    public CodeSnippet getCode(@PathVariable String id) {
        return codeService.getCode(id);
    }
    
    @GetMapping("/latest")
    public List<CodeSnippet> getLatest() {
        return codeService.getLatestCodeSnippets();
    }
    
    @PostMapping(value = "/new", consumes = "application/json")
    public Map<String, String> newCode(@RequestBody CodeSnippet codeSnippet) {
        codeService.saveCode(codeSnippet);
        return Map.of("id", codeSnippet.getId());
    }
    
}
