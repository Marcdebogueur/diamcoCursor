package com.diamco.v1.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // ignore les champs nulls dans la réponse
public class ResponseApi {
    private int status;      // Code HTTP (200, 400, 500…)
    private String message;  // Message de réponse
    private Object data;     // Optionnel : données à retourner

    // Constructeur
    public ResponseApi(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Méthodes statiques utilitaires
    public static ResponseApi success(int status, String message, Object data) {
        return new ResponseApi(status, message, data);
    }

    public static ResponseApi error(int status, String message) {
        return new ResponseApi(status, message, null);
    }

    // Getters & Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
