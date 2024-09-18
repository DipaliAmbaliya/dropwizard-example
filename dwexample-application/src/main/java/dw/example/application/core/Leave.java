package dw.example.application.core;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "leaves")
@NamedQueries({
        @NamedQuery(name = "dw.example.application.core.Leave.findAll",
                query = "select l from Leave l")
        ,
        @NamedQuery(name = "dw.example.application.core.Leave.findByType",
                query = "select l from Leave l "
                        + "where l.leaveType like:leaveType ")
})
@Data
public class Leave  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "leave_type")
    private String leaveType;

    @Column(name = "leave_balance")
    private Integer leaveBalance;

}
