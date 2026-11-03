package model;

public class ResumoDiario {
    private String data;
    private String treinoDoDia;
    private double horasDeSono;
    private int aguaConsumidaMl;
    private String cafeDaManha;
    private String almoco;
    private String jantar;

    public ResumoDiario(String data, String treinoDoDia, double horasDeSono,
                        int aguaConsumidaMl, String cafeDaManha, String almoco, String jantar) {
        this.data = data;
        this.treinoDoDia = treinoDoDia;
        this.horasDeSono = horasDeSono;
        this.aguaConsumidaMl = aguaConsumidaMl;
        this.cafeDaManha = cafeDaManha;
        this.almoco = almoco;
        this.jantar = jantar;
    }

    @Override
    public String toString() {
        return "Resumo de " + data
                + " | Treino: " + treinoDoDia
                + " | Sono: " + horasDeSono + " horas"
                + " | Água: " + aguaConsumidaMl + " ml"
                + " | Café da manhã: " + cafeDaManha
                + " | Almoço: " + almoco
                + " | Jantar: " + jantar;
    }
}
//resumo diário