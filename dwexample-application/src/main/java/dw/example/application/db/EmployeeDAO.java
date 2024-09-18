package dw.example.application.db;

import java.util.Optional;

import dw.example.application.core.Employee;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;
import org.hibernate.SessionFactory;

public class EmployeeDAO extends AbstractDAO<Employee> {

    public EmployeeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Employee> findAll() {
        return list(namedQuery("dw.example.application.core.Employee.findAll"));
    }


    public List<Employee> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("dw.example.application.core.Employee.findByName")
                        .setParameter("name", builder.toString())
        );
    }


    public Optional<Employee> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

}
