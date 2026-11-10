package Service;

import model.*;
import Repository.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.sql.SQLException;

public class AcademiaService {
    private final Repositorio<Usuario> usuarioRepo = new UsuarioJdbcRepositorio();
    private final ExercicioJdbcRepositorio exercicioRepo = new ExercicioJdbcRepositorio();
    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() throws SQLException {
        ConnexionFactory.criarTabelas();
        int opcao;

        do {
            System.out.println("\n===== ACADEMIA =====");
            System.out.println("1 - Cadastrar novo usuário e montar treino");
            System.out.println("2 - Ver usuários salvos e seus treinos");
            System.out.println("0 - Sair");
            opcao = lerInt("Opção: ");

            switch (opcao) {
                case 1 -> novoUsuario();
                case 2 -> verUsuarios();
                case 0 -> System.out.println("Até mais!");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void novoUsuario() {
        Usuario usuario = cadastrarUsuario();
        usuarioRepo.salvar(usuario);
        System.out.println("Salvo no banco com o id " + usuario.getId());

        TreinoPersonalizado personalizado = new TreinoPersonalizado();
        montarTreinoPersonalizado(usuario, personalizado);
        mostrarTreinoCompleto(usuario, personalizado);
    }

    private void verUsuarios() {
        List<Usuario> usuarios = usuarioRepo.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        usuarios.forEach(System.out::println);

        int id = lerInt("Id do usuário para ver o treino (0 volta): ");
        if (id == 0) return;

        Optional<Usuario> usuario = usuarioRepo.buscarPorId(id);
        if (usuario.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        TreinoPersonalizado personalizado = new TreinoPersonalizado();
        exercicioRepo.listarPorUsuario(id).forEach(personalizado::adicionarExercicio);
        mostrarTreinoCompleto(usuario.get(), personalizado);
    }

    private void mostrarTreinoCompleto(Usuario usuario, TreinoPersonalizado personalizado) {

        TreinoPredefinido parteApp = new TreinoPredefinido(usuario.getIdade());
        new TreinoCompleto(parteApp, personalizado).exibir();
    }

    // =========================================================================
    // // [CADASTRO BLINDADO] Método com loops e validações para testar erros
    // =========================================================================
    private Usuario cadastrarUsuario() {
        int id = 0; 

        // // Entrada e validação do Nome (barra vazios e números)
        String nome = "";
        while (true) {
            System.out.print("Seu nome: ");
            nome = scanner.nextLine();
            if (nome != null && !nome.trim().isEmpty() && !nome.matches(".*\\d.*")) {
                break;
            }
            System.out.println("Erro: O nome não pode estar vazio ou conter números.");
        }

        // // Entrada e validação da Idade (barra letras e valores fora do intervalo)
        int idade = 0;
        while (true) {
            try {
                idade = lerInt("Sua idade: ");
                if (idade > 0 && idade <= 120) {
                    break;
                }
                System.out.println("Erro: Idade deve estar entre 1 e 120 anos.");
            } catch (Exception e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }

        // // Entrada e validação do Peso (barra letras e negativos)
        double peso = 0;
        while (true) {
            try {
                System.out.print("Seu peso em kg (ex: 70.5): ");
                peso = Double.parseDouble(scanner.nextLine().replace(",", "."));
                if (peso > 0 && peso <= 500) {
                    break;
                }
                System.out.println("Erro: Peso inválido. Deve ser maior que 0.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um número válido para o peso (use ponto ou vírgula).");
            }
        }

        // // Entrada e validação da Altura em cm (ex: 174)
        double altura = 0;
        while (true) {
            try {
                System.out.print("Sua altura em cm (ex: 174): ");
                altura = Double.parseDouble(scanner.nextLine().replace(",", "."));
                if (altura > 50 && altura <= 300) {
                    break;
                }
                System.out.println("Erro: Altura inválida. Insira em cm (ex: 174).");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um número válido para a altura.");
            }
        }

        // // Entrada do Objetivo via Menu Numerado (1, 2 ou 3)
        String objetivo = "";
        while (true) {
            System.out.println("\nSelecione o Objetivo:");
            System.out.println("1 - EMAGRECER");
            System.out.println("2 - GANHAR_MASSA");
            System.out.println("3 - SAUDE_GERAL");
            int opcaoObj = lerInt("Opção: ");

            if (opcaoObj == 1) {
                objetivo = "EMAGRECER";
                break;
            } else if (opcaoObj == 2) {
                objetivo = "GANHAR_MASSA";
                break;
            } else if (opcaoObj == 3) {
                objetivo = "SAUDE_GERAL";
                break;
            } else {
                System.out.println("Erro: Opção inválida! Digite 1, 2 ou 3.");
            }
        }

        // Retorna o objeto Usuario criado com todos os 6 parâmetros exigidos e validados
        return new Usuario(id, nome, idade, peso, altura, objetivo);
    }

    private void montarTreinoPersonalizado(Usuario usuario, TreinoPersonalizado treino) {
        int opcao;
        do {
            System.out.println("\n--- Monte seu treino ---");
            System.out.println("1 - Adicionar exercício de força");
            System.out.println("2 - Adicionar exercício de cardio");
            System.out.println("3 - Remover exercício");
            System.out.println("4 - Ver meu treino");
            System.out.println("0 - Finalizar");
            opcao = lerInt("Opção: ");

            switch (opcao) {
                case 1 -> adicionarForca(usuario, treino);
                case 2 -> adicionarCardio(usuario, treino);
                case 3 -> removerExercicio(treino);
                case 4 -> treino.exibir();
                case 0 -> System.out.println("Treino finalizado!");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void adicionarForca(Usuario usuario, TreinoPersonalizado treino) {
        System.out.print("Nome do exercício: ");
        String nome = scanner.nextLine();
        System.out.print("Grupo muscular: ");
        String grupo = scanner.nextLine();
        int series = lerInt("Séries: ");
        int reps = lerInt("Repetições: ");

        Exercicio ex = new ExercicioForca(nome, grupo, series, reps);
        exercicioRepo.salvar(ex, usuario.getId());
        treino.adicionarExercicio(ex);
    }

    private void adicionarCardio(Usuario usuario, TreinoPersonalizado treino) {
        System.out.print("Nome do exercício: ");
        String nome = scanner.nextLine();
        int minutos = lerInt("Duração (min): ");

        Exercicio ex = new ExercicioCardio(nome, minutos);
        exercicioRepo.salvar(ex, usuario.getId());
        treino.adicionarExercicio(ex);
    }

    private void removerExercicio(TreinoPersonalizado treino) {
        treino.exibir();
        int indice = lerInt("Número do exercício a remover: ") - 1;

        List<Exercicio> exercicios = treino.getExercicios();
        if (indice < 0 || indice >= exercicios.size()) {
            System.out.println("Número inválido.");
            return;
        }
        exercicioRepo.remover(exercicios.get(indice).getId());
        treino.removerExercicio(indice);
    }

    private int lerInt(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
