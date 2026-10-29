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
            System.out.println("SERVIÇOS DA ACADEMIA");
            System.out.println("1 - Cadastrar novousuário e montar treino");
            System.out.println("");
        }
    }
}
