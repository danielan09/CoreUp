package model.Alimentos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import model.Alimentos.Alimento.TipoRefeicao;

public class DiarioAlimentar {
    private final List<Alimento> refeicoes = new ArrayList<>();
    private final Set<TipoRefeicao> refeicoesConfiguradas = EnumSet.allOf(TipoRefeicao.class);

    public void configurarRefeicoes(List<TipoRefeicao> tipos) {
        Objects.requireNonNull(tipos, "A lista de refeições não pode ser nula");
        if (tipos.isEmpty()) {
            throw new IllegalArgumentException("Configure pelo menos uma refeição");
        }

        Set<TipoRefeicao> configuradas = EnumSet.noneOf(TipoRefeicao.class);
        for (TipoRefeicao tipo : tipos) {
            configuradas.add(Objects.requireNonNull(tipo, "O tipo da refeição não pode ser nulo"));
        }
        refeicoesConfiguradas.clear();
        refeicoesConfiguradas.addAll(configuradas);
    }

    public List<TipoRefeicao> getRefeicoesConfiguradas() {
        return Collections.unmodifiableList(new ArrayList<>(refeicoesConfiguradas));
    }

    public void adicionarRefeicao(Alimento alimento) {
        Objects.requireNonNull(alimento, "A refeição não pode ser nula");
        validarRefeicaoConfigurada(alimento.getRefeicao());
        refeicoes.add(alimento);
    }

    public void registrarRefeicaoPulada(TipoRefeicao refeicao, LocalDate data) {
        validarRefeicaoConfigurada(refeicao);
        adicionarRefeicao(Alimento.refeicaoPulada(refeicao, data));
    }

    public List<Alimento> listarRefeicoes(LocalDate data) {
        Objects.requireNonNull(data, "A data não pode ser nula");

        List<Alimento> refeicoesDaData = new ArrayList<>();
        for (Alimento alimento : refeicoes) {
            if (alimento.getData().equals(data)) {
                refeicoesDaData.add(alimento);
            }
        }
        return refeicoesDaData;
    }

    public List<Alimento> listarRefeicoesDeHoje() {
        return listarRefeicoes(LocalDate.now());
    }

    public int calcularCalorias(LocalDate data) {
        Objects.requireNonNull(data, "A data não pode ser nula");

        int total = 0;
        for (Alimento alimento : listarRefeicoes(data)) {
            if (alimento.getCalorias() != null) {
                total += alimento.getCalorias();
            }
        }
        return total;
    }

    private void validarRefeicaoConfigurada(TipoRefeicao refeicao) {
        Objects.requireNonNull(refeicao, "O tipo da refeição não pode ser nulo");
        if (!refeicoesConfiguradas.contains(refeicao)) {
            throw new IllegalArgumentException("Refeição não configurada: " + refeicao.getNome());
        }
    }
}
