package com.caiotayota.platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeSnippetNotFoundException extends RuntimeException{
    public CodeSnippetNotFoundException() {
        super("Code snippet not found!");
    }
}
