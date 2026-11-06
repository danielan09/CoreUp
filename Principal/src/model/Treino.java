package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Treino {
    private String nome;
    protected List<Exercicio> exercicios = new ArrayList<>();

    public Treino(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }

    public List<Exercicio> getExercicios() {
        return new ArrayList<>(exercicios);
    }

    public abstract String getOrigem();

    public void exibir() {
        System.out.println("=== " + nome + " [" + getOrigem() + "] ===");
        if (exercicios.isEmpty()) {
            System.out.println("  (nenhum exercício)");
            return;
        }
        for (int i = 0; i < exercicios.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + exercicios.get(i));
        }
    }
}
