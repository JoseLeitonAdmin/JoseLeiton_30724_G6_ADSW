package com.aligest.command;

import java.util.Stack;

/**
 * Mantiene la pila de comandos ejecutados para soportar el deshacer (undo).
 */
public class HistorialComandos {
    private final Stack<Command> pilaContenedora = new Stack<>();

    public void ejecutar(Command comando) {
        comando.execute();
        pilaContenedora.push(comando);
    }

    public boolean deshacer() {
        if (pilaContenedora.isEmpty()) {
            return false;
        }
        Command ultimoComando = pilaContenedora.pop();
        ultimoComando.undo();
        return true;
    }

    public void limpiar() {
        pilaContenedora.clear();
    }
}
