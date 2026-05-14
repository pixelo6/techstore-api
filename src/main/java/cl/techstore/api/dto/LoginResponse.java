package cl.techstore.api.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private String expiracion = "3600";

    public LoginResponse(String token) {
        this.token = token;
    }
}