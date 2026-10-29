package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Treino {
    private final String nome;
    protected final List<Exercicio> exercicios = new ArrayList<>();

    public Treino(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }

    public List<Exercicio> getExercicios() {
        return new ArrayList<>(exercicios); // Cópia: protege o encapsulamento
    }

    public abstract String getOrigem();

    public void exibir() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(nome).append(" [").append(getOrigem()).append("] ===");

        if (exercicios.isEmpty()) {
            sb.append("\n  (nenhum exercício)");
        } else {
            for (int i = 0; i < exercicios.size(); i++) {
                sb.append("\n  ").append(i + 1).append(". ").append(exercicios.get(i));
            }
        }
        System.out.println(sb.toString());
    }
}

