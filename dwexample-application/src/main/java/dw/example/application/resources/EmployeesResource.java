/*
 * The MIT License
 *
 * Copyright 2015 Dmitry Noranovich.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dw.example.application.resources;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

import dw.example.api.CustomResponse;
import dw.example.api.CustomResponseStatus;
import dw.example.application.auth.User;
import dw.example.application.core.Leave;
import dw.example.application.core.LeaveData;
import dw.example.application.core.LeaveRequest;
import dw.example.application.db.EmployeeDAO;
import dw.example.application.core.Employee;
import dw.example.application.db.LeaveDAO;
import dw.example.application.db.LeaveDataDAO;
import dw.example.application.db.LeaveRequestDAO;
import dw.example.api.dtos.LeaveReqDTO;
import dw.example.api.dtos.RequestHistory;
import dw.example.api.dtos.RespondReqDTO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource class which exposes employee data via REST API.
 *
 * @author Dmitry Noranovich
 */
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeesResource  implements Serializable {

    /**
     * The DAO object to manipulate employees.
     */
    private final EmployeeDAO employeeDAO;

    private final LeaveDAO leaveDAO;
    private final LeaveDataDAO leaveDataDAO;
    private final LeaveRequestDAO leaveRequestDAO;

    /**
     * Constructor.
     *
     * @param employeeDAO DAO object to manipulate employees.
     */
    public EmployeesResource(EmployeeDAO employeeDAO, LeaveDAO leaveDAO, LeaveDataDAO leaveDataDAO, LeaveRequestDAO leaveRequestDAO) {
        this.employeeDAO = employeeDAO;
        this.leaveDAO = leaveDAO;
        this.leaveRequestDAO = leaveRequestDAO;
        this.leaveDataDAO = leaveDataDAO;
    }

    /**
     * Looks for employees whose first or last name contains the passed
     * parameter as a substring. If name argument was not passed, returns all
     * employees stored in the database.
     *
     * @param name query parameter
     * @return list of employees whose first or last name contains the passed
     * parameter as a substring or list of all employees stored in the database.
     */
    @GET
    @UnitOfWork
    @PermitAll
    public List<Employee> findByName(
            @QueryParam("name") Optional<String> name
    ) {
        if (name.isPresent()) {
            return employeeDAO.findByName(name.get());
        } else {
            return employeeDAO.findAll();
        }
    }

    /**
     * Method looks for an employee by her id.
     *
     * @param id the id of an employee we are looking for.
     * @return Optional containing the found employee or an empty Optional
     * otherwise.
     */
    @GET
    @Path("/{id}")
    @UnitOfWork
    @PermitAll
    public Optional<Employee> findById(@PathParam("id") Integer id) {
        return employeeDAO.findById(id);
    }

    @POST
    @Path("/addRequest")
    @UnitOfWork
    @PermitAll
    public CustomResponse createRequest(@Valid LeaveReqDTO leaveReqDTO, @Auth User user) throws Exception {

        Optional<Employee> employee = employeeDAO.findById(leaveReqDTO.getEmpId());

        if (employee.isPresent()) {
            Optional<Leave> leave = leaveDAO.findByLeaveType(leaveReqDTO.getLeaveType());
            if (checkBalance(employee,leave,leaveReqDTO.getReqDays())) {
                Optional<Employee> manager = employeeDAO.findById(leaveReqDTO.getManagerId());
                if (employee.get().getAssigned_manager() == manager.get().getId()) {
                    LeaveRequest leaveRequest = new LeaveRequest();
                    leaveRequest.setLeave(leave.get());
                    leaveRequest.setEmployee(employee.get());
                    leaveRequest.setApproved_by(manager.get());
                    leaveRequest.setStatus("Pending");
                    leaveRequest.setNumDays(leaveReqDTO.getReqDays());
                    leaveRequestDAO.insert(leaveRequest);
                    return new CustomResponse(new CustomResponseStatus(Response.Status.CREATED.getStatusCode(),"leaves Requested successfully", LocalDateTime.now()));
                } else {
                    throw new Exception("Employee not able to req leave for given data");
                }
            } else {
                throw new Exception("Employee not able to req leave for given data");
            }
        } else {
            throw new RuntimeException("Employee is not associated for given id");
        }

    }

    @GET
    @Path("/getRequestHistory/{id}")
    @UnitOfWork
    @PermitAll
    public Response getRequestHistory(@PathParam("id") Integer id) throws Exception{

        Optional<Employee> employee = employeeDAO.findById(id);

        if (employee.isPresent()) {
            List<LeaveRequest> leaveRequests = leaveRequestDAO.findByEmpId(employee.get().getId());
            List<RequestHistory> requestHistories = leaveRequests.stream().map(lr->{
                RequestHistory requestHistory = new RequestHistory();
                requestHistory.setManager(lr.getApproved_by().getFirstName() + " " + lr.getApproved_by().getLastName());
                requestHistory.setLeaveType(lr.getLeave().getLeaveType());
                requestHistory.setEmpName(lr.getEmployee().getFirstName() + " " + lr.getEmployee().getFirstName());
                requestHistory.setStatus(lr.getStatus());
                requestHistory.setReqDays(lr.getNumDays());
                return requestHistory;
            }).collect(Collectors.toList());
            return Response.ok(requestHistories).build();
        } else {
            throw new RuntimeException("Employee is not associated for given id");
        }
    }

    @POST
    @Path("/respondRequest")
    @UnitOfWork
    @RolesAllowed({"MANAGER"})
    public Response respondRequest(RespondReqDTO respondReqDTO) throws URISyntaxException {

        Optional<Employee> manager = employeeDAO.findById(respondReqDTO.getManagerId());

        if (manager.isPresent() && manager.get().is_manager()) {

            Optional<LeaveRequest> leaveReq = leaveRequestDAO.findById(respondReqDTO.getReqId());

            if (leaveReq.isPresent() && leaveReq.get().getEmployee().getAssigned_manager() == manager.get().getId()) {
                LeaveRequest leaveRequest = leaveReq.get();
                    if (respondReqDTO.getStatus().equalsIgnoreCase("Approved") && leaveReq.get().getStatus().equalsIgnoreCase("Pending")) {
                        Optional<LeaveData> leaveData = leaveDataDAO.findByLeaveTypeAndEmpId(leaveRequest.getLeave().getId(),leaveRequest.getEmployee().getId());
                        if (leaveData.isPresent()) {
                            Integer total = leaveData.get().getLeaveTaken() + leaveRequest.getNumDays();
                            LeaveData data = leaveData.get();
                            data.setLeaveTaken(total);
                            leaveRequest.setApproved_by(manager.get());
                            leaveRequest.setStatus(respondReqDTO.getStatus());
                            leaveRequestDAO.update(leaveRequest);
                            leaveDataDAO.update(data);
                        }
                    } else if (respondReqDTO.getStatus().equalsIgnoreCase("Rejected") && leaveReq.get().getStatus().equalsIgnoreCase("Pending")) {
                        Optional<LeaveData> leaveData = leaveDataDAO.findByLeaveTypeAndEmpId(leaveRequest.getLeave().getId(),leaveRequest.getEmployee().getId());
                        if (leaveData.isPresent()) {
                            Integer total = leaveData.get().getLeaveTaken() - leaveRequest.getNumDays();
                            LeaveData data = leaveData.get();
                            data.setLeaveTaken(total);

                            leaveRequest.setApproved_by(manager.get());
                            leaveRequest.setStatus(respondReqDTO.getStatus());
                            leaveRequestDAO.update(leaveRequest);
                            leaveDataDAO.update(data);
                        }
                    }
            }
            return Response.ok().build();
        } else {
            throw new RuntimeException("Manager is not associated for given id");
        }
    }


    private boolean checkBalance(Optional<Employee> employee, Optional<Leave> leave, Integer reqDays) {
        if (leave.isPresent()) {
            Optional<LeaveData> leaveData = leaveDataDAO.findByLeaveTypeAndEmpId(leave.get().getId(),employee.get().getId());
            if (leaveData.isPresent()) {


                Integer availableLeaves = leave.get().getLeaveBalance() - leaveData.get().getLeaveTaken();

                if (availableLeaves-reqDays>=0) {
                    Integer total = leaveData.get().getLeaveTaken() +reqDays;
                    LeaveData data = leaveData.get();
                    data.setLeaveTaken(total);
                    leaveDataDAO.update(data);
                    return true;
                } else {
                    return false;
                }
            } else {
                LeaveData data = new LeaveData();
                data.setLeaveTaken(reqDays);
                data.setLeave(leave.get());
                data.setEmployee(employee.get());
                leaveDataDAO.insert(data);
                return true;
            }
        }
           return false;

    }
}
