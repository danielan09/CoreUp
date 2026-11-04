package model;

public class ExercicioForca extends Exercicio {
    private final int series;
    private final int repeticoes;

    public ExercicioForca(String var1, String var2, int var3, int var4) {
        super(var1, var2);
        if (var3 > 0 && var4 > 0) {
            this.series = var3;
            this.repeticoes = var4;
        } else {
            throw new IllegalArgumentException("Séries e repetições devem ser maiores que zero.");
        }
    }

    public String getDescricao() {
        String var10000 = this.getNome();
        return "Exercicio: " + var10000 + "\nGrupo muscular: " + this.getGrupoMuscular() + "\nSeries: " + this.series + "\nRepeticoes: " + this.repeticoes;
    }
}
