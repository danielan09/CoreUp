package model;

public class ExercicioModelo {
    private final int id;
    private final String tipo;
    private final String grupoMuscular;
    private final String nome;

    public ExercicioModelo(int id, String tipo, String grupoMuscular, String nome) {
        this.id = id;
        this.tipo = tipo;
        this.grupoMuscular = grupoMuscular;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public String getNome() { return nome; }
    public boolean isCardio() { return "CARDIO".equals(tipo); }
}
