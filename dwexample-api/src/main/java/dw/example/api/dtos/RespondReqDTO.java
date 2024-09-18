package dw.example.api.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class RespondReqDTO {
    @NotNull(message="managerId is required")
    private Integer managerId;

    @NotEmpty(message="status is required")
    private String status;

    @NotNull(message="reqId is required")
    private Integer reqId;
}
