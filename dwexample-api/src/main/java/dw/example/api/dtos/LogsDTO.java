package dw.example.api.dtos;

import io.dropwizard.validation.Validated;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class LogsDTO {
    @NotEmpty(message="task is required")
    private String task;
    @NotNull(message="empId is required")
    private Integer empId;
    private String in;
    private String out;
    private Integer logId;
}
