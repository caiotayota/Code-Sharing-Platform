package com.caiotayota.platform.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caiotayota.platform.entity.CodeSnippet;
import com.caiotayota.platform.service.CodeService;

import java.util.List;

@Controller
@RequestMapping(path = "/code", produces = MediaType.TEXT_HTML_VALUE)
public class CodeWebController {
    
    private final CodeService codeService;
    
    @Autowired
    public CodeWebController(CodeService codeService) {
        this.codeService = codeService;
    }
    
    @GetMapping("/new")
    public String newCode() {
        return "create-code";
    }
    
    @GetMapping("/{id}")
    public String getCode(Model model, @PathVariable String id) {
        CodeSnippet codeSnippet = codeService.getCode(id);
        model.addAttribute("codeSnippet", codeSnippet);
        return "show-code";
    }
    
    @GetMapping("/latest")
    public String getLatest(Model model) {
        List<CodeSnippet> codeSnippets = codeService.getLatestCodeSnippets();
        
        model.addAttribute("codeSnippets", codeSnippets);
        return "latest-code";
    }
    
}
