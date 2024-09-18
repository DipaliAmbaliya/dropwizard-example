package dw.example.api.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeLogsData {
    private List<Logs> logs;
    private Date date;
    private String totalHours;
    private Integer employeeId;

}
