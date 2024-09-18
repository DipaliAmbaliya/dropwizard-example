package dw.example.application.health;

import com.codahale.metrics.health.HealthCheck;
import dw.example.application.db.EmployeeDAO;

import java.io.File;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

/**
 * Return some information about the given path in health check
 */
public class FileSystemHealthCheck extends HealthCheck {
	String path;

	public FileSystemHealthCheck(String path) {
		this.path = path;
	}

	@Override
	protected Result check() throws Exception {
		File file = new File(path);

		return HealthCheck.Result.builder().withMessage(path)
				.withDetail("totalSpace", byteCountToDisplaySize(file.getTotalSpace()))
				.withDetail("freeSpace", byteCountToDisplaySize(file.getFreeSpace()))
				.healthy()
				.build();
	}


}
