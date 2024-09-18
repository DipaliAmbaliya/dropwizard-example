package dw.example.api.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Logs {
    private String taskName;
    private LocalDateTime login;
    private LocalDateTime logout;
    private String hours;
}
