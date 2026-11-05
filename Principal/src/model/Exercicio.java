package model;

import java.util.Objects;

public abstract class Exercicio {
    private final String nome;
    private final String grupoMuscular;

    public Exercicio(String nome, String grupoMuscular) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }
    public int getId(); { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome;}
    public String getGrupoMuscular() { return grupoMuscular; }

    public abstract String getDescricao();

    @Override
    public String toString() {
        return this.getDescricao();
    }
}
