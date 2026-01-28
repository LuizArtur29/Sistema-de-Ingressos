<img width="1301" height="538" alt="image" src="https://github.com/user-attachments/assets/f68d74cf-7098-4da3-8c1a-06d8988e2f20" />


Minimundo
O sistema foi projetado para gerenciar de forma robusta a venda e transferência de ingressos para eventos, com papéis de usuário bem definidos.

No topo da hierarquia está o Administrador, responsável por cadastrar e gerenciar os eventos na plataforma. Cada Evento, que representa um acontecimento principal como um festival, é criado por um administrador e contém informações gerais como nome, descrição, período de duração (data_inicio, data_fim), local e capacidade.

Para oferecer maior flexibilidade, cada Evento se desdobra em uma ou várias SessaoEvento. Cada sessão representa uma data ou horário específico dentro do evento principal, permitindo um controle granular sobre o acesso, com seu próprio nome, data e hora.

A estrutura de venda é detalhada em dois níveis: TipoIngresso e Ingresso. Cada SessaoEvento pode ter múltiplos tipos de ingresso (ex: "Pista", "VIP", "Meia-Entrada"), cada um cadastrado como um TipoIngresso com atributos específicos como preço, lote, quantidade total e ingressos disponíveis. Quando uma venda é efetivada, são gerados registros individuais na entidade Ingresso, representando cada bilhete único vendido.

O sistema gerencia os Usuarios, que são os clientes finais. O cadastro do Usuario armazena dados pessoais completos, como nome, CPF, data de nascimento, informações de contato e credenciais de acesso.

A transação primária é a Compra. Realizada por um Usuario, uma Compra pode conter um ou vários Ingressos individuais. O registro da Compra armazena detalhes da transação, como a data, a quantidade de ingressos, o valor total, o método de pagamento e o status.

Uma nova funcionalidade central do sistema é a Transferencia de ingressos entre usuários. Um Usuario (vendedor) pode transferir um Ingresso específico para outro Usuario (comprador). Essa transação é registrada na entidade Transferencia, que armazena o valor de revenda negociado, conectando o ingresso aos dois usuários envolvidos na troca.

Este modelo permite um rastreamento completo do ciclo de vida de um ingresso: desde a criação do evento pelo administrador, passando pela definição dos tipos de ingresso, a compra inicial por um usuário, até uma possível revenda ou transferência para outro cliente.
