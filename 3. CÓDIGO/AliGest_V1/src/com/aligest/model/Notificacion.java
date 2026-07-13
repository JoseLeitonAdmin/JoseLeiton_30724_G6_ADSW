package com.aligest.model;

/**
 * Representa una notificación o evento en el log de comunicaciones (ej: WhatsApp API).
 */
public class Notificacion {
    private String type; // "success", "warning", "info"
    private String msg;
    private String time;

    public Notificacion(String type, String msg, String time) {
        this.type = type;
        this.msg = msg;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
