package dw.example.application;

import com.fasterxml.jackson.databind.SerializationFeature;
import dw.example.application.auth.AppAuthorizer;
import dw.example.application.auth.AppBasicAuthenticator;
import dw.example.application.auth.User;
import dw.example.application.core.LeaveData;
import dw.example.application.core.LeaveRequest;
import dw.example.application.core.LogSystem;
import dw.example.application.db.EmployeeDAO;
import dw.example.application.db.LeaveDAO;
import dw.example.application.db.LeaveDataDAO;
import dw.example.application.db.LeaveRequestDAO;
import dw.example.application.db.LogSystemDAO;
import dw.example.application.resources.EmployeesResource;
import dw.example.application.config.DwExampleConfiguration;
import dw.example.application.core.Employee;
import dw.example.application.core.Leave;
import dw.example.application.health.BuildInfoHealthCheck;
import dw.example.application.health.FileSystemHealthCheck;
import dw.example.application.resources.HelloService;
import dw.example.application.resources.LogSystemResource;
import dw.example.application.resources.MultithreadedExecutorService;
import dw.example.application.exception.ConstraintViolationExceptionMapper;
import dw.example.application.exception.ExceptionMapper;
import dw.example.application.exception.JsonProcessingExceptionMapper;
import dw.example.application.exception.WebApplicationExceptionMapper;
import dw.example.application.task.ExecutionThreadServiceManager;
import dw.example.application.task.ScheduledTask;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DwExampleApplication extends Application<DwExampleConfiguration> {

	public static String DEFAULT_TEMPDIR = "/tmp";

	private final HibernateBundle<DwExampleConfiguration> hibernateBundle = new HibernateBundle<DwExampleConfiguration>(
			Employee.class, Leave.class, LeaveData.class, LogSystem.class, LeaveRequest.class
	) {

		@Override
		public DataSourceFactory getDataSourceFactory(DwExampleConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}

	};

	public static void main(String[] args) throws Exception {
		new DwExampleApplication().run(args);
	}


	@Override
	public String getName() {
		return "dwexample";
	}

	@Override
	public void initialize(final Bootstrap<DwExampleConfiguration> bootstrap) {
		//Serve files at /src/main/resources/assets at url /[applicationContext]/static
		//The asset servlet mapping path must be different than for Jersey resources (which "/")
		//The third parameter is the default file to serve
		bootstrap.addBundle(new AssetsBundle("/assets", "/static", "index.html"));
		bootstrap.addBundle(hibernateBundle);

	}

	@Override
	public void run(DwExampleConfiguration config, Environment environment) throws Exception {

		final EmployeeDAO employeeDAO
				= new EmployeeDAO(hibernateBundle.getSessionFactory());
		final LeaveDAO leaveDAO
				= new LeaveDAO(hibernateBundle.getSessionFactory());
		final LeaveRequestDAO leaveRequestDAO
				= new LeaveRequestDAO(hibernateBundle.getSessionFactory());
		final LogSystemDAO logSystemDAO
				= new LogSystemDAO(hibernateBundle.getSessionFactory());
		final LeaveDataDAO leaveDataDAO
				= new LeaveDataDAO(hibernateBundle.getSessionFactory());

		//Add health checks
		environment.healthChecks().register("tempDir", new FileSystemHealthCheck(config.getTempDirPath()));
		environment.healthChecks().register("buildInfo", new BuildInfoHealthCheck());

		//Modify Jackson object mapper with features.
		//A Dropwizard style objectmapper can be created using  io.dropwizard.jackson.Jackson.newObjectMapper();
//    	environment.getObjectMapper().enable(JsonParser.Feature.IGNORE_UNDEFINED); //Ignore unknown fields
//		environment.getObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE); //To marshal with  @JsonRootName
//		environment.getObjectMapper().enable(DeserializationFeature.UNWRAP_ROOT_VALUE); //To unmarshal with @JsonRootName
    	environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //Required for nice Date marshaling
//    	environment.getObjectMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE); //To keep the timzone for ZonedDateTime
//    	environment.getObjectMapper().setSerializationInclusion(Include.NON_NULL); //Skip nulls

		//Add exception mappers
		environment.jersey().register(new JsonProcessingExceptionMapper());
		environment.jersey().register(new ConstraintViolationExceptionMapper());
		environment.jersey().register(new WebApplicationExceptionMapper());
		environment.jersey().register(new ExceptionMapper());


		environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
				.setAuthenticator(new AppBasicAuthenticator())
				.setAuthorizer(new AppAuthorizer())
				.setRealm("BASIC-AUTH-REALM")
				.buildAuthFilter()));
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));


		//Add a new servlet to admin() or servlet()- for example the directory listing default servlet - http://localhost:8081/dir/
		environment.admin().setInitParameter("org.eclipse.jetty.servlet.Default.resourceBase", config.getTempDirPath());
		environment.admin().setInitParameter("org.eclipse.jetty.servlet.Default.pathInfoOnly", "true");
		environment.admin().addServlet("listTempDirServlet", new DefaultServlet()).addMapping("/dir/*");

		//Add resources
		environment.jersey().register(new HelloService(config.getGreetings()));
		environment.jersey().register(new MultithreadedExecutorService(3));
		environment.jersey().register(new EmployeesResource(employeeDAO, leaveDAO, leaveDataDAO, leaveRequestDAO));
		environment.jersey().register(new LogSystemResource(employeeDAO, logSystemDAO));
		//Add background tasks
		environment.lifecycle().manage(new ScheduledTask(2000));
		environment.lifecycle().manage(new ExecutionThreadServiceManager(2, 2000));
	}


}
