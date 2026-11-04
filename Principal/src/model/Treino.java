package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Treino {
    private final String nome;
    protected final List<Exercicio> exercicios = new ArrayList();

    protected Treino(String var1) {
        this.nome = (String)Objects.requireNonNull(var1, "nome não pode ser nulo");
        if (var1.isBlank()) {
            throw new IllegalArgumentException("O nome do treino não pode estar vazio.");
        }
    }

    public String getNome() {
        return this.nome;
    }

    public List<Exercicio> getExercicios() {
        return new ArrayList(this.exercicios);
    }

    public void adicionarExercicio(Exercicio var1) {
        this.exercicios.add((Exercicio)Objects.requireNonNull(var1, "exercicio não pode ser nulo"));
    }

    public abstract String getOrigem();

    public void exibir() {
        String var10001 = this.nome;
        System.out.println("=== " + var10001 + " [" + this.getOrigem() + "] ===");
        if (this.exercicios.isEmpty()) {
            System.out.println("  (nenhum exercício)");
        } else {
            for(int var1 = 0; var1 < this.exercicios.size(); ++var1) {
                System.out.println("  " + (var1 + 1) + ". " + ((Exercicio)this.exercicios.get(var1)).getDescricao());
            }

        }
    }
}
