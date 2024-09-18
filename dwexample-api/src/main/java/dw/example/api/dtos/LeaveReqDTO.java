package dw.example.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeaveReqDTO {
    @JsonProperty("empId")
    @NotNull(message="empId is required")
    private Integer empId;

    @JsonProperty("leaveType")
    @NotEmpty(message="leaveType is required")
    private String leaveType;

    @JsonProperty("reqDays")
    @NotNull(message="reqDays is required")
    private Integer reqDays;

    @JsonProperty("managerId")
    @NotNull(message="managerId is required")
    private Integer managerId;
}
