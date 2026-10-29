package model;

public abstract class Exercicio {
    private String nome;
    private String grupoMuscular;

    public Exercicio(String nome, String grupoMuscular) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }

    public String getNome() { return nome; }
    public String getGrupoMuscular() { return grupoMuscular; }

    public abstract String getDescricao();

    @Override
    public String toString() {
        return getDescricao();
    }
}
