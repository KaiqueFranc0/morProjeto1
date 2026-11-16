package view;

import controller.DenunciaController;
import controller.InstituicaoController;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.Denuncia;
import model.Instituicao;
import service.EmailService;

/** Menus, entrada de dados e apresentação. Não acessa JPA diretamente. */
public class DenunciaView {
    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Scanner scanner;
    private final DenunciaController denunciaController;
    private final InstituicaoController instituicaoController;
    private final EmailService emailService;

    public DenunciaView(Scanner scanner, DenunciaController denunciaController,
                       InstituicaoController instituicaoController, EmailService emailService) {
        this.scanner = scanner;
        this.denunciaController = denunciaController;
        this.instituicaoController = instituicaoController;
        this.emailService = emailService;
    }

    public void executar() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n=== DENÚNCIAS - DIREITOS DOS POVOS INDÍGENAS ===");
            System.out.println("1 - Cadastrar denúncia");
            System.out.println("2 - Consultar denúncia por ID (find)");
            System.out.println("3 - Consultar instituição e suas denúncias por ID");
            System.out.println("4 - Atualizar denúncia");
            System.out.println("5 - Remover denúncia");
            System.out.println("6 - Consultar instituição por ID");
            System.out.println("7 - Cadastrar instituição");
            System.out.println("8 - Enviar denúncia por e-mail");
            System.out.println("0 - Sair");
            try {
                int opcao = lerInteiro(scanner, "Escolha: ", 0);
                switch (opcao) {
                    case 1: cadastrar(); break;
                    case 2: consultar(); break;
                    case 3: consultarInstituicao(true); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 6: consultarInstituicao(false); break;
                    case 7: cadastrarInstituicao(); break;
                    case 8: enviarDenuncia(); break;
                    case 0: executando = false; break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                // EOF também encerra corretamente uma execução com entrada redirecionada.
                executando = false;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Falha na operação. Alterações não confirmadas foram desfeitas.");
                System.out.println("Detalhe: " + e.getMessage());
            }
        }
        System.out.println("Sistema encerrado.");
    }

    private void cadastrar() {
        // Coleta antes de abrir a transação, conforme a orientação sobre duração.
        String titulo = lerTexto("Título: ");
        String descricao = lerTexto("Descrição (uma linha): ");
        String local = lerTexto("Local do fato: ");
        System.out.println("Use o ID informado no cadastro da instituição (consulta na opção 6).");
        int idInstituicao = lerInteiro(scanner, "ID da instituição destinatária: ", 1);

        Integer id = denunciaController.cadastrar(titulo, descricao, local, idInstituicao);
        System.out.println("Denúncia cadastrada. ID: " + id);
    }

    private void consultar() {
        int id = lerInteiro(scanner, "ID da denúncia: ", 1);
        exibirExistente(id);
    }

    private void atualizar() {
        int id = lerInteiro(scanner, "ID da denúncia a atualizar: ", 1);
        // Exibe a versão atual antes da coleta, sem manter conexão aberta no terminal.
        if (!exibirExistente(id)) {
            return;
        }
        System.out.println("Informe os novos valores para todos os campos editáveis.");
        String titulo = lerTexto("Novo título: ");
        String descricao = lerTexto("Nova descrição (uma linha): ");
        String local = lerTexto("Novo local: ");
        System.out.println("Informe o ID atual ou o de outra instituição cadastrada.");
        int idInstituicao = lerInteiro(scanner, "ID da instituição: ", 1);

        denunciaController.atualizar(id, titulo, descricao, local, idInstituicao);
        System.out.println("Denúncia atualizada.");
    }

    private void remover() {
        int id = lerInteiro(scanner, "ID da denúncia a remover: ", 1);
        if (!exibirExistente(id)) {
            return;
        }
        System.out.print("Confirma exclusão? Digite S para confirmar: ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Exclusão cancelada.");
            return;
        }
        denunciaController.remover(id);
        System.out.println("Denúncia removida. A instituição foi preservada.");
    }

    private void enviarDenuncia() {
        int id = lerInteiro(scanner, "ID da denúncia a enviar: ", 1);
        Denuncia denuncia = denunciaController.consultar(id);
        if (denuncia == null) {
            System.out.println("Denúncia não encontrada.");
            return;
        }

        Instituicao instituicao = denuncia.getInstituicao();
        if (instituicao == null || instituicao.getEmail() == null
                || instituicao.getEmail().isBlank()) {
            System.out.println("A denúncia precisa de uma instituição com e-mail cadastrado.");
            return;
        }

        String destinatario = instituicao.getEmail().trim();
        String assunto = "Denúncia — " + denuncia.getTitulo();
        String corpo = "À instituição " + instituicao.getNome() + ",\n\n"
                + "Título: " + denuncia.getTitulo() + "\n"
                + "Local: " + denuncia.getLocal() + "\n\n"
                + "Descrição:\n" + denuncia.getDescricao();

        System.out.println("\nPara: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println("\n" + corpo);
        System.out.print("\nConfirma o envio? Digite S para confirmar: ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Envio cancelado.");
            return;
        }

        try {
            emailService.enviar(destinatario, assunto, corpo);
            System.out.println("Mensagem aceita pelo servidor de e-mail.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (MessagingException e) {
            System.out.println("Não foi possível confirmar o envio. Detalhe: " + e.getMessage());
        }
    }

    private void cadastrarInstituicao() {
        String nome = lerTexto("Nome da instituição: ");
        String email = lerTexto("E-mail da instituição: ");
        Integer id = instituicaoController.cadastrar(nome, email);
        System.out.println("Instituição cadastrada. ID: " + id);
    }

    private void consultarInstituicao(boolean mostrarDenuncias) {
        int id = lerInteiro(scanner, "ID da instituição: ", 1);
        Instituicao instituicao = instituicaoController.consultar(id);
        if (instituicao == null) {
            System.out.println("Instituição não encontrada.");
            return;
        }
        System.out.println(instituicao);
        if (mostrarDenuncias) {
            if (instituicao.getDenuncias().isEmpty()) {
                System.out.println("Nenhuma denúncia associada a esta instituição.");
            }
            for (Denuncia denuncia : instituicao.getDenuncias()) {
                exibirDenuncia(denuncia);
            }
        }
    }

    private boolean exibirExistente(int id) {
        Denuncia denuncia = denunciaController.consultar(id);
        if (denuncia == null) {
            System.out.println("Denúncia não encontrada.");
            return false;
        }
        exibirDenuncia(denuncia);
        return true;
    }

    private void exibirDenuncia(Denuncia denuncia) {
        System.out.println("\n----------------------------------------");
        System.out.println("ID: " + denuncia.getId());
        System.out.println("Título: " + denuncia.getTitulo());
        System.out.println("Descrição: " + denuncia.getDescricao());
        System.out.println("Local: " + denuncia.getLocal());
        LocalDateTime dataCriacao = denuncia.getDataCriacao();
        System.out.println("Cadastro: "
                + (dataCriacao == null ? "Não informada" : dataCriacao.format(DATA)));
        // Navegação no relacionamento: evidencia os dados da outra tabela.
        System.out.println("Instituição: " + denuncia.getInstituicao().getNome());
        System.out.println("E-mail destinatário: " + denuncia.getInstituicao().getEmail());
    }

    private int lerInteiro(Scanner scanner, String mensagem, int minimo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // Repete a pergunta sem encerrar o menu.
            }
            System.out.println("Informe um número inteiro a partir de " + minimo + ".");
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static void exibirFalhaInicializacao(RuntimeException e) {
        System.err.println("Não foi possível iniciar o sistema: " + e.getMessage());
        System.err.println("Verifique o MySQL, o script SQL e as credenciais no persistence.xml.");
    }
}
