package com.vendaingressos.model; // Certifique-se de que o pacote está correto

import jakarta.persistence.*; // Importa as anotações JPA
import lombok.Data; // Importa a anotação @Data do Lombok
import java.time.LocalDateTime; // Para lidar com data e hora

@Entity // Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no BD
@Table(name = "eventos") // Define o nome da tabela no banco de dados (opcional, mas boa prática)
@Data // Anotação do Lombok para gerar getters, setters, toString, equals e hashCode automaticamente
public class Evento {

    @Id // Indica que este campo é a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia para geração automática do ID (auto-incremento)
    private Long id;

    @Column(nullable = false, length = 255) // Mapeia para uma coluna, não pode ser nula, tamanho máximo 255
    private String nome;

    @Column(columnDefinition = "TEXT") // Mapeia para uma coluna de texto longo
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHora; // Usa LocalDateTime para data e hora

    @Column(nullable = false, length = 255)
    private String local;

    @Column(nullable = false)
    private Integer capacidadeTotal;

    @Column(nullable = false)
    private String status; // Ex: "ATIVO", "CANCELADO", "FINALIZADO"

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