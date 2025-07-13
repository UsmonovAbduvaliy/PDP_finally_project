package com.example.pdp_project.controller;

import com.example.pdp_project.repo.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final UserFileRepository userFileRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        return userFileRepository.findById(id)
                .map(file -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(file.getContentType()));
                    headers.setContentDisposition(ContentDisposition.inline().filename(file.getOriginalName()).build());
                    return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
