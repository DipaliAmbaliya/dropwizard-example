package dw.example.application.resources;

import dw.example.api.dtos.EmployeeLogsData;
import dw.example.api.dtos.Logs;
import dw.example.application.core.Employee;
import dw.example.application.core.LogSystem;
import dw.example.application.db.EmployeeDAO;
import dw.example.application.db.LogSystemDAO;
import dw.example.api.dtos.LogsDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.var;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/logs")
@Consumes({MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public class LogSystemResource {

    private final EmployeeDAO employeeDAO;

    private final LogSystemDAO logSystemDAO;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public LogSystemResource(EmployeeDAO employeeDAO, LogSystemDAO logSystemDAO) {
        this.employeeDAO = employeeDAO;
        this.logSystemDAO = logSystemDAO;
    }

    @POST
    @Path("/log_in")
    @UnitOfWork
    @PermitAll
    public Response logsIn(@Valid LogsDTO logsDTO) throws URISyntaxException {

        Optional<Employee> employee = employeeDAO.findById(logsDTO.getEmpId());

        if (employee.isPresent()) {
            List<LogSystem> logs = logSystemDAO.checkOpenTask(employee.get().getId());
            if (logs.isEmpty()) {

                LogSystem logSystem = new LogSystem();
                logSystem.setDate(new Date());
                logSystem.setEmployee(employee.get());
                logSystem.setLogin(LocalDateTime.now());
                logSystem.setTaskName(logsDTO.getTask());
                logSystemDAO.insert(logSystem);
                return Response.ok(logSystem).build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/log_out")
    @UnitOfWork
    @PermitAll
    public Response LogsOut(LogsDTO logsDTO) {

        Optional<Employee> employee = employeeDAO.findById(logsDTO.getEmpId());

        if (employee.isPresent()) {
            Optional<LogSystem> logs = logSystemDAO.checkCloseTask(employee.get().getId(),logsDTO.getTask());
            if (logs.isPresent()) {
                LogSystem logSystem = logs.get();
                logSystem.setDate(new Date());
                logSystem.setEmployee(employee.get());
                logSystem.setLogout(LocalDateTime.now());
                logSystemDAO.update(logSystem);
                return Response.ok(logSystem).build();
            } else {
                throw new RuntimeException(Response.Status.BAD_REQUEST.getReasonPhrase());
            }
        } else {
            throw new RuntimeException("Employee is not found");
        }
    }

    @POST
    @Path("/update")
    @UnitOfWork
    @PermitAll
    public Response updateLogs(LogsDTO logsDTO) throws URISyntaxException {

        Optional<Employee> employee = employeeDAO.findById(logsDTO.getEmpId());

        if (employee.isPresent()) {
            Optional<LogSystem> log = logSystemDAO.findById(logsDTO.getLogId());
            if (log.isPresent() && (log.get().getLogin()!=null && log.get().getLogout()!=null)) {
                List<LogSystem> availableLogs = logSystemDAO.checkToday(employee.get().getId(),log.get().getDate());
                List<Integer> ids = availableLogs.stream().map(l -> l.getId()).sorted().collect(Collectors.toList());
                int index = ids.indexOf(logsDTO.getLogId());
                Optional<LogSystem> preLog = Optional.empty();
                Optional<LogSystem> nextLog = Optional.empty();
                if (index != 0)
                    preLog = logSystemDAO.findById(ids.get(index-1));
               if (index < ids.size() -1)
                   nextLog = logSystemDAO.findById(ids.get(index+1));

                if(!checkInputTime(preLog,LocalDateTime.parse(logsDTO.getIn(),formatter))) {
                    throw new RuntimeException("not able to update in logs");
                }
                if(!checkOutputTime(nextLog,LocalDateTime.parse(logsDTO.getOut(),formatter))) {
                    throw new RuntimeException("not able to update out logs");
                }

                LogSystem logSystem = log.get();
                logSystem.setEmployee(employee.get());
                logSystem.setLogin(LocalDateTime.parse(logsDTO.getIn(),formatter));
                logSystem.setLogout(LocalDateTime.parse(logsDTO.getOut(),formatter));
                logSystemDAO.update(logSystem);
            }
            return Response.ok(logsDTO).build();
        } else {
            throw new RuntimeException("Employee is not found");
        }
    }

    @GET
    @Path("/getEmployeeLogs/{id}/{date}")
    @UnitOfWork
    @PermitAll
    public Response getEmployeeLogs(@PathParam("id") Integer id,@PathParam("date") String date) throws Exception{

        Optional<Employee> employee = employeeDAO.findById(id);

        if (employee.isPresent()) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date1 = format.parse(date);
            List<LogSystem> availableLogs = logSystemDAO.checkToday(employee.get().getId(),date1);
            List<Logs> logsList = availableLogs.stream().map(lr->{
                Logs logs = new Logs();
                logs.setLogin(lr.getLogin());
                logs.setLogout(lr.getLogout());
                logs.setTaskName(lr.getTaskName());
                long minute = lr.getLogin().until(lr.getLogout(), ChronoUnit.MINUTES);
                int Hours = (int)minute/60;
                int Minutes = (int)minute%60;
                logs.setHours(Hours+":"+Minutes);
                return logs;
            }).collect(Collectors.toList());

            EmployeeLogsData employeeLogsData = new EmployeeLogsData();
            employeeLogsData.setLogs(logsList);
            employeeLogsData.setEmployeeId(id);
            employeeLogsData.setDate(date1);
            long total = 0;
            for (var logs : logsList) {
                long minutes = (Integer.parseInt(logs.getHours().split(":")[0]) * 60) + Integer.parseInt(logs.getHours().split(":")[1]);
                total = total + minutes;
            }
            int Hours = (int)total/60;
            int Minutes = (int)total%60;
            employeeLogsData.setTotalHours(Hours+":"+Minutes);

            return Response.ok(employeeLogsData).build();
        } else {
            throw new RuntimeException("Employee is not associated for given id");
        }
    }

    private boolean checkOutputTime(Optional<LogSystem> next, LocalDateTime log) {

        if (next.isPresent()) {
            return next.get().getLogin().isAfter(log);
        } else {
            return true;
        }
    }

    private boolean checkInputTime(Optional<LogSystem> pre, LocalDateTime log) {

        if (pre.isPresent()) {
            return pre.get().getLogout().isBefore(log);
        } else {
            return true;
        }
    }
}
