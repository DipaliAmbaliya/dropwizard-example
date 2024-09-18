package dw.example.application.config;

import static dw.example.application.DwExampleApplication.DEFAULT_TEMPDIR;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

@Data
public class DwExampleConfiguration extends Configuration {

	@JsonProperty("tempDir")
	@NotEmpty
	private String tempDirPath = DEFAULT_TEMPDIR;
	/**
	 * A factory used to connect to a relational database management system.
	 * Factories are used by Dropwizard to group together related configuration
	 * parameters such as database connection driver, URI, password etc.
	 */
	@NotNull
	@Valid
	private final DataSourceFactory dataSourceFactory = new DataSourceFactory();

	/**
	 * Jersey client default configuration.
	 */
	@Valid
	@NotNull
	private final JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();

	/**
	 *
	 * @return Jersey Client
	 */
	@JsonProperty("jerseyClient")
	public JerseyClientConfiguration getJerseyClientConfiguration() {
		return jerseyClientConfiguration;
	}

	/**
	 * A getter for the database factory.
	 *
	 * @return An instance of database factory deserialized from the
	 * configuration file passed as a command-line argument to the application.
	 */
	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	@JsonProperty("greetings")
	@NotEmpty(message = "Add at least one greeting pattern")
	@Valid
	private List<Greeting> greetings;

}
