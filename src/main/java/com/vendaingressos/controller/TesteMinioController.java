package com.vendaingressos.controller;

import com.vendaingressos.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/teste-minio")
public class TesteMinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            String nomeArquivo = minioService.uploadArquivo(arquivo);
            return ResponseEntity.ok("✅ Sucesso! Arquivo salvo como: " + nomeArquivo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Erro: " + e.getMessage());
        }
    }

    @GetMapping("/url/{nomeArquivo}")
    public ResponseEntity<String> pegarUrl(@PathVariable String nomeArquivo) {
        try {
            String url = minioService.getUrlArquivo(nomeArquivo);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Erro: " + e.getMessage());
        }
    }
}