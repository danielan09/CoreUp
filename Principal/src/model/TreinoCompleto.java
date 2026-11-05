package model;

public class TreinoCompleto {
    private final TreinoPredefinido parteApp;
    private final TreinoPersonalizado parteUsuario;

    public TreinoCompleto(TreinoPredefinido parteApp, TreinoPersonalizado parteUsuario) {
        this.parteApp = parteApp;
        this.parteUsuario = parteUsuario;
    }

    public TreinoPredefinido getParteApp() { return parteApp; }
    public TreinoPersonalizado getParteUsuario() { return parteUsuario; }

    public void exibir() {
        System.out.println("\n TREINO COMPLETO ");
        parteApp.exibir();
        System.out.println();
        parteUsuario.exibir();
        System.out.println("#####################################");
    }
}
