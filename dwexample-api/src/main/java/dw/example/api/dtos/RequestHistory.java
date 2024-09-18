package dw.example.api.dtos;

import lombok.Data;

@Data
public class RequestHistory {
    private String EmpName;
    private String leaveType;
    private String manager;
    private Integer reqDays;
    private String status;

}
