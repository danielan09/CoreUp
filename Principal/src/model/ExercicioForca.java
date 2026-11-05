package model;

public class ExercicioForca extends Exercicio {
    private int series;
    private int repeticoes;

    public ExercicioForca(String nome, String grupoMuscular, int series, int repeticoes) {
        super(nome, grupoMuscular);
        this.series = series;
        this.repeticoes = repeticoes;
    }

    public int getSeries() { return series; }
    public int getRepeticoes() { return repeticoes; }

    @Override
    public String getDescricao() {
        return getNome() + "("+getGrupoMuscular()+")-"
                +series+ "x" + repeticoes;
    }
}