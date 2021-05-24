package com.example.ds_rest;

import com.example.ds_rest.model.CharacterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CharacterController {

    private final CharacterService service;

    @Autowired
    public CharacterController(CharacterService service) {
        this.service = service;
    }

    @GetMapping(value = "/getOne", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> get(@RequestParam String charName) {
        CharacterInfo characterInfo;
        try {
            characterInfo = service.getOne(charName);
        } catch (Exception e) {
            return new ResponseEntity<>("<h1>bad input name</h1>", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(characterInfo.toHtml("<h1>", "<h3>"), HttpStatus.OK);
    }

    @GetMapping(value = "/getAvg", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getStats() {
        return new ResponseEntity<>(service.getStats().toHtml(), HttpStatus.OK);
    }
}
