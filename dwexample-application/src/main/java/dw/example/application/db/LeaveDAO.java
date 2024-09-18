package dw.example.application.db;

import dw.example.application.core.Employee;
import dw.example.application.core.Leave;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class LeaveDAO extends AbstractDAO<Leave> {
    public LeaveDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Leave> findByLeaveType(String leaveType) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(leaveType).append("%");
        return
                namedQuery("dw.example.application.core.Leave.findByType")
                        .setParameter("leaveType", builder.toString()).uniqueResultOptional();
    }
}
