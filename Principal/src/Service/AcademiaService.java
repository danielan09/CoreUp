package Service;

import model.*;
import Repository.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AcademiaService {
    private final Repositorio<Usuario> usuarioRepo = new UsuarioJdbcRepositorio();
    private final ExercicioJdbcRepositorio exercicioRepo = new ExercicioJdbcRepositorio();
    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
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

    private Usuario cadastrarUsuario() {
        System.out.print("Seu nome: ");
        String nome = scanner.nextLine();

        while (true) {
            try {
                int idade = lerInt("Sua idade: ");
                double peso = lerDoublePositivo("Seu peso (kg): ");
                double altura = lerDoublePositivo("Sua altura (cm): ");
                String objetivo = lerObjetivo();
                return new Usuario(0, nome, idade, peso, altura, objetivo);
            } catch (IllegalArgumentException e) {
                System.out.println("Dados inválidos. Confira a idade (1 a 120), peso e altura.");
            }
        }
    }

    private double lerDoublePositivo(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                double valor = Double.parseDouble(scanner.nextLine().replace(',', '.'));
                if (Double.isFinite(valor) && valor > 0) {
                    return valor;
                }
                System.out.println("Digite um valor maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private String lerObjetivo() {
        while (true) {
            System.out.println("Objetivo: 1 - Emagrecer | 2 - Ganhar massa | 3 - Saúde geral");
            switch (lerInt("Opção: ")) {
                case 1 -> { return "EMAGRECER"; }
                case 2 -> { return "GANHAR_MASSA"; }
                case 3 -> { return "SAUDE_GERAL"; }
                default -> System.out.println("Opção inválida.");
            }
        }
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
