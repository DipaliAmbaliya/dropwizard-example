package dw.example.application.db;

import dw.example.application.core.Leave;
import dw.example.application.core.LeaveData;
import dw.example.application.core.LeaveRequest;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class LeaveDataDAO extends AbstractDAO<LeaveData> {
    public LeaveDataDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<LeaveData> findByLeaveTypeAndEmpId(Integer leaveId,Integer empId) {

        return
                namedQuery("dw.example.application.core.Leave.findByTypeAndEmpId")
                        .setParameter("leaveId", leaveId).setParameter("employeeId", empId).uniqueResultOptional();
    }

    public void update(LeaveData leaveData) {
        currentSession().saveOrUpdate(leaveData);
    }

    public LeaveData insert(LeaveData leaveData) {
        return persist(leaveData);
    }
}
