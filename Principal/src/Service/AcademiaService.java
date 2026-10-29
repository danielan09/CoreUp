package Service;

import model.*;
import Repositorio.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AcademiaService {
    private final Repositorio<Usuario>usuarioRepo = new UsuariojdbcRepositorio();
    private final ExerciciojdbcRepositorio exerciciojdbcRepo = new exercicioRepositorio();
    private final Scanner scanner = new Scanner(System.in);

    public void inciar() {
        ConexaoFactory.criarTabelas();;
        int opcao;

        do {
            System.out.println("─── SERVIÇOS DA ACADEMIA ───");
            System.out.println("1 - Cadastrar novousuário e montar treino");
            System.out.println("2 - Ver Usuários salvos e trinos");
            System.out.println("0 - Sair");

            switch (opcao) {
                case 1 -> novoUsuario();
                case 2 -> verUsuario();
                case 0 -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida");
            }
        } while(opcao!=0);
    }

    private void novoUsuario() {
        Usuario usuario = cadastrarUsuario();
        usuarioRepo.salvar(usuario);
        System.out.println("Salvo no banco com o id" + usuario.getId());

        TreinoPersonalizado personalizado = new TreinoPersonalizado();
        montarTreinoPersonalizado(usuario, personalizado);
        mostrarTreinoCompleto(usuario, personalizado);
    }

    private void verUsuario() {
        List<Usuario> usuario = usuarioRepo.listarTodos();
        if(usuario.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        usuarios.forEach(System.out::println);

        int id = lerInt("Id do usuário para ver o treino (0 volta): ");
        if(id == 0 return);

        Optional<Usuario> usuario = usuarioRepo.buscarPorId(id);
        if(Usuario.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        TreinoPersonalizado personalizado = new TreinoPersonalizado();
        exercicioRepo.listarPorUsuario(id).forEarch(personalizado::adicionarExercicio);
        mostrarTreinoCompleto(usuario.get(), personalizado);
    }

    private void mostrarTreinoCompleto(Usuario usuario, TreinoPersonalizado personalizado) {
        int opcao;
        do {
            System.out.println("─── MONTE SEU TREINO ───");
            System.out.println("1 - Adicionar exercicio de força");
            System.out.println("2 - Adicionar exercicio de cardio");
            System.out.println("3 - Remover exercicio");
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
        } while (opcao !=0);
    }

    private void adicionarForca(Usuario usuario, TreinoPersonalizado treino) {
        System.out.println('Nome do exercicio: ');
        String nome = scanner.nextLine();
        System.out.println("Grupo muscular: ");
        int series = lerInt("Séries: ");
        int reps = lerInt('Repetições: ');

        Exercicio ex = new ExercicioForca(nome, grupo, series, reps);
        exercicioRepo.salvar(ex, usuario.getId());
        treino.adicionarExercicio(ex);
    }

    private void removerExercicio(TreinoPersonalizado treino) {
        treino.exibir();
        int.adicionarExercicio(ex);

        List<Exercicio> exercicios = treino.getExercicios();
        if(indice <0 || indice >= exercicios.size()) {
            System.out.println('Número inválido.');
            return;
        }
        exerciciosRepo.remover(exercicios.get(indice).gerId());
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
