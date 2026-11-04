package model;

import java.util.Objects;

public abstract class Exercicio {
    private final String nome;
    private final String grupoMuscular;

    public Exercicio(String nome, String grupoMuscular) {
        this.nome = Objects.requireNonNull(nome, "nome não pode ser nulo");
        this.grupoMuscular = Objects.requireNonNull(grupoMuscular, "grupo muscular não pode ser nulo");
        if (nome.isBlank() || grupoMuscular.isBlank()) {
            throw new IllegalArgumentException("Nome e grupo muscular não podem estar vazios.");
        }
    }

    public String getNome() { return nome; }
    public String getGrupoMuscular() { return grupoMuscular; }

    public abstract String getDescricao();

    @Override
    public String toString() {
        return getDescricao();
    }
}
