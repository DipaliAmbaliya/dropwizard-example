package dw.example.application.core;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "leave_request")
@Data
@NamedQueries({
        @NamedQuery(name = "dw.example.application.core.LeaveRequest.findAll",
                query = "select e from LeaveRequest e")
        ,
        @NamedQuery(name = "dw.example.application.core.LeaveRequest.findByEmpId",
                query = "select lr from LeaveRequest lr "
                        + "where lr.employee.id = :empId ")
})
public class LeaveRequest  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_id")
    private Leave leave;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approved_by;

    @Column(name = "num_days")
    private Integer numDays;

}
