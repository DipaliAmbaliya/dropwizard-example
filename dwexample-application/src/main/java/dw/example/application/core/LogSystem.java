package dw.example.application.core;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "log_system")
@Data
@NamedQueries({
        @NamedQuery(name = "dw.example.application.core.LogSystem.checkCloseTask",
                query = "select ls from LogSystem ls "
                        + "where ls.employee.id = :empId and ls.taskName like :taskName" +
                        " and ls.login is not null and ls.logout is null")
        ,
        @NamedQuery(name = "dw.example.application.core.LogSystem.checkOpenTask",
                query = "select ls from LogSystem ls "
                        + "where ls.employee.id = :empId and " +
                        "ls.logout is null")
        ,
        @NamedQuery(name = "dw.example.application.core.LogSystem.checkToday",
                query = "select ls from LogSystem ls "
                        + "where ls.employee.id = :empId and " +
                        "ls.date = :date and ls.login is not null and ls.logout is not null ")
})
public class LogSystem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "login")
    private LocalDateTime login;

    @Column(name = "logout")
    private LocalDateTime logout;

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
