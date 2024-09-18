package dw.example.application.db;

import dw.example.application.core.LeaveRequest;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class LeaveRequestDAO extends AbstractDAO<LeaveRequest> {
    public LeaveRequestDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<LeaveRequest> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }


    public LeaveRequest insert(LeaveRequest leaveRequest) {
        return persist(leaveRequest);
    }

    public void update(LeaveRequest leaveRequest) {
        currentSession().saveOrUpdate(leaveRequest);
    }

    public List<LeaveRequest> findByEmpId(Integer id) {
        return list(
                namedQuery("dw.example.application.core.LeaveRequest.findByEmpId")
                        .setParameter("empId", id)
        );
    }
}
