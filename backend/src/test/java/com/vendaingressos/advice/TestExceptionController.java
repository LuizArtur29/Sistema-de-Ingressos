package com.vendaingressos.advice;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ForbiddenException;
import com.vendaingressos.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestExceptionController {

    @PostMapping("/validate")
    ResponseEntity<Void> validate(@Valid @RequestBody TestRequest request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/not-found")
    void notFound() {
        throw new ResourceNotFoundException("Recurso não encontrado");
    }

    @GetMapping("/bad-request")
    void badRequest() {
        throw new BadRequestException("Requisição inválida");
    }

    @GetMapping("/forbidden")
    void forbidden() {
        throw new ForbiddenException("Acesso negado");
    }

    @GetMapping("/unauthorized")
    void unauthorized() {
        throw new BadCredentialsException("Credenciais inválidas");
    }

    @GetMapping("/runtime")
    void runtime() {
        throw new RuntimeException("Detalhe interno que não deve vazar");
    }

    static class TestRequest {
        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}