package model;

import java.util.Objects;

public abstract class Exercicio {
    private final String nome;
    private final String grupoMuscular;

    protected Exercicio(String var1, String var2) {
        this.nome = (String)Objects.requireNonNull(var1, "nome não pode ser nulo");
        this.grupoMuscular = (String)Objects.requireNonNull(var2, "grupo muscular não pode ser nulo");
        if (var1.isBlank() || var2.isBlank()) {
            throw new IllegalArgumentException("Nome e grupo muscular não podem estar vazios.");
        }
    }

    public String getNome() {
        return this.nome;
    }

    public String getGrupoMuscular() {
        return this.grupoMuscular;
    }

    public abstract String getDescricao();

    public String toString() {
        return this.getDescricao();
    }
}
