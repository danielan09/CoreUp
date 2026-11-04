package model;

import java.util.Objects;

public class Usuario {
    private static int proximoId = 1;
    private final int id;
    private final String nome;
    private final int idade;
    private TreinoCompleto treino;

    public Usuario(String var1, int var2) {
        this.nome = (String)Objects.requireNonNull(var1, "nome não pode ser nulo");
        if (var1.isBlank()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        } else if (var2 > 0 && var2 <= 120) {
            this.id = proximoId++;
            this.idade = var2;
        } else {
            throw new IllegalArgumentException("Idade inválida: " + var2);
        }
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public int getIdade() {
        return this.idade;
    }

    public TreinoCompleto getTreino() {
        return this.treino;
    }

    public void setTreino(TreinoCompleto var1) {
        this.treino = (TreinoCompleto)Objects.requireNonNull(var1, "treino não pode ser nulo");
    }

    public String toString() {
        return "Usuário " + this.id + " - " + this.nome + ", " + this.idade + " anos";
    }
}
