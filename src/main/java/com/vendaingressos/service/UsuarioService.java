package com.vendaingressos.service;

import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //CRUD básico
    public void cadastrarUsuario() {}

    public void buscarUsuarioPorEmail() {}

}
