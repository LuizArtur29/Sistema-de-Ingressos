package com.vendaingressos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data; // Importa a anotação @Data do Lombok
import lombok.NonNull;

import java.time.LocalDateTime; // Para lidar com data e hora
import java.util.ArrayList;
import java.util.List;

@Entity // Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no BD
@Table(name = "eventos") // Define o nome da tabela no banco de dados (opcional, mas boa prática)
@Data // Anotação do Lombok para gerar getters, setters, toString, equals e hashCode automaticamente
public class  Evento {

    @Id // Indica que este campo é a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia para geração automática do ID (auto-incremento)
    private Long id;

    @NotBlank(message = "O nome do evento não pode estar em branco")
    @Column(nullable = false, length = 255) // Mapeia para uma coluna, não pode ser nula, tamanho máximo 255
    private String nome;

    @Column(columnDefinition = "TEXT") // Mapeia para uma coluna de texto longo
    private String descricao;

    @Column(nullable = false)
    @NotNull(message = "A data e hora do evento não pode ser nula")
    @FutureOrPresent(message = "A data e hora do evento não pode ser no passado")
    private LocalDateTime dataHora; // Usa LocalDateTime para data e hora

    @NotBlank(message = "O local do evento não pode estar em branco")
    @Column(nullable = false, length = 255)
    private String local;

    @NotNull(message = "A capacidade total do evento não pode ser nula")
    @Min(value = 1, message = "A capacidade total deve ser no mínimo 1")
    @Column(nullable = false)
    private Integer capacidadeTotal;

    @NotBlank(message = "O status do evento não pode estar em branco")
    @Column(nullable = false)
    private String status; // Ex: "ATIVO", "CANCELADO", "FINALIZADO"

    @OneToMany
    private List<Ingresso> listaIngressos = new ArrayList<>();

    // Construtor padrão (necessário para JPA, Lombok @Data geralmente cria um)
    public Evento() {
    }

    // Construtor com todos os campos (útil para criar objetos mais facilmente)
    public Evento(String nome, String descricao, LocalDateTime dataHora, String local, Integer capacidadeTotal, String status) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.local = local;
        this.capacidadeTotal = capacidadeTotal;
        this.status = status;
    }
}