package dw.example.application.db;

import dw.example.application.core.LeaveRequest;
import dw.example.application.core.LogSystem;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class LogSystemDAO extends AbstractDAO<LogSystem> {
    public LogSystemDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<LogSystem> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }


    public LogSystem insert(LogSystem logSystem) {
        return persist(logSystem);
    }

    public void update(LogSystem logSystem) {
        currentSession().saveOrUpdate(logSystem);
    }

    public List<LogSystem> checkOpenTask(Integer id) {

        return list(
                namedQuery("dw.example.application.core.LogSystem.checkOpenTask")
                        .setParameter("empId", id)
        );
    }

    public List<LogSystem> checkToday(Integer id,Date date) {

        return list(
                namedQuery("dw.example.application.core.LogSystem.checkToday")
                        .setParameter("empId",id).setParameter("date", date)
        );
    }

    public Optional<LogSystem> checkCloseTask(Integer id,String taskName) {
        return
                namedQuery("dw.example.application.core.LogSystem.checkCloseTask")
                        .setParameter("empId", id).setParameter("taskName",taskName).uniqueResultOptional();
    }



}
