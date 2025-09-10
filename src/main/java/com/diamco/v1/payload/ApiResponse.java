package com.diamco.v1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String status;   // success ou error
    private int code;        // code HTTP
    private String message;  // message clair
    private Object data;     // données optionnelles

    // Méthode statique pour succès
    public static ApiResponse success(int code, String message, Object data) {
        return new ApiResponse("success", code, message, data);
    }

    // Méthode statique pour erreur
    public static ApiResponse error(int code, String message) {
        return new ApiResponse("error", code, message, null);
    }
}
