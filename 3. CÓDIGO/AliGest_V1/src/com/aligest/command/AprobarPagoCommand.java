package com.aligest.command;

import com.aligest.model.PagoPendiente;
import java.util.List;

/**
 * Comando concreto para aprobar un pago. Soporta ejecución y reversión (deshacer).
 */
public class AprobarPagoCommand implements Command {
    private final long idPago;
    private final List<PagoPendiente> pagosPendientes;
    private PagoPendiente pagoRespaldado;
    private int posicionOriginal = -1;
    private final Runnable updateUI;

    public AprobarPagoCommand(long idPago, List<PagoPendiente> pagosPendientes, Runnable updateUI) {
        this.idPago = idPago;
        this.pagosPendientes = pagosPendientes;
        this.updateUI = updateUI;
    }

    @Override
    public void execute() {
        posicionOriginal = -1;
        for (int i = 0; i < pagosPendientes.size(); i++) {
            if (pagosPendientes.get(i).getId() == idPago) {
                posicionOriginal = i;
                break;
            }
        }
        if (posicionOriginal != -1) {
            pagoRespaldado = pagosPendientes.remove(posicionOriginal);
            if (updateUI != null) {
                updateUI.run();
            }
        }
    }

    @Override
    public void undo() {
        if (pagoRespaldado != null && posicionOriginal != -1) {
            pagosPendientes.add(posicionOriginal, pagoRespaldado);
            if (updateUI != null) {
                updateUI.run();
            }
        }
    }

    public PagoPendiente getPagoRespaldado() {
        return pagoRespaldado;
    }
}
