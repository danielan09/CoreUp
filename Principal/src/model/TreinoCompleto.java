package model;

import java.util.Objects;

public class TreinoCompleto {
    private final TreinoPredefinido parteApp;
    private final TreinoPersonalizado parteUsuario;

    public TreinoCompleto(TreinoPredefinido var1, TreinoPersonalizado var2) {
        this.parteApp = (TreinoPredefinido)Objects.requireNonNull(var1, "parteApp não pode ser nula");
        this.parteUsuario = (TreinoPersonalizado)Objects.requireNonNull(var2, "parteUsuario não pode ser nula");
    }

    public TreinoPredefinido getParteApp() {
        return this.parteApp;
    }

    public TreinoPersonalizado getParteUsuario() {
        return this.parteUsuario;
    }

    public void exibir() {
        System.out.println("\n########## TREINO COMPLETO ##########");
        this.parteApp.exibir();
        System.out.println();
        this.parteUsuario.exibir();
        System.out.println("#####################################");
    }
}
