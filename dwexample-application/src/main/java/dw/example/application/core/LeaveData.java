package dw.example.application.core;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "leave_data")
@NamedQueries({
        @NamedQuery(name = "dw.example.application.core.Leave.findByTypeAndEmpId",
                query = "select ld from LeaveData ld "
                        + "where ld.leave.id = :leaveId" +
                        " and ld.employee.id = :employeeId ")
})
@Data
public class LeaveData  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_id")
    private Integer mapId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_id")
    private Leave leave;

    @Column(name = "leave_taken")
    private Integer leaveTaken;

}

