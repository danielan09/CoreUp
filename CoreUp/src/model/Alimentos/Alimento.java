package model.Alimentos;

import java.time.LocalDate;
import java.util.Objects;

public class Alimento {
    public enum TipoRefeicao {
        CAFE_DA_MANHA("Café da manhã"),
        ALMOCO("Almoço"),
        LANCHE("Lanche"),
        JANTAR("Jantar");

        private final String nome;

        TipoRefeicao(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }

    private final TipoRefeicao refeicao;
    private final String descricao;
    private final Integer calorias;
    private final LocalDate data;
    private final boolean pulada;

    public Alimento(TipoRefeicao refeicao, String descricao, Integer calorias, LocalDate data) {
        this(refeicao, descricao, calorias, data, false);
    }

    private Alimento(
            TipoRefeicao refeicao, String descricao, Integer calorias, LocalDate data, boolean pulada) {
        this.refeicao = Objects.requireNonNull(refeicao, "O tipo da refeição não pode ser nulo");
        this.data = Objects.requireNonNull(data, "A data da refeição não pode ser nula");
        if (pulada) {
            this.descricao = null;
            this.calorias = null;
        } else {
            if (descricao == null || descricao.isBlank()) {
                throw new IllegalArgumentException("A descrição não pode ser nula ou vazia");
            }
            if (calorias != null && calorias < 0) {
                throw new IllegalArgumentException("As calorias não podem ser negativas");
            }
            this.descricao = descricao;
            this.calorias = calorias;
        }
        this.pulada = pulada;
    }

    public static Alimento refeicaoPulada(TipoRefeicao refeicao, LocalDate data) {
        return new Alimento(refeicao, null, null, data, true);
    }

    public TipoRefeicao getRefeicao() { return refeicao; }
    public String getDescricao() { return descricao; }
    public Integer getCalorias() { return calorias; }
    public LocalDate getData() { return data; }
    public boolean isPulada() { return pulada; }

    @Override
    public String toString() {
        if (pulada) {
            return refeicao.getNome() + " - refeição pulada - " + data;
        }
        return refeicao.getNome() + " - " + descricao
                + (calorias != null ? " (" + calorias + " kcal)" : "") + " - " + data;
    }
}
