package edu.cornell.hackathon.workday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hubspot.dropwizard.guicier.GuiceBundle;

import edu.cornell.hackathon.workday.config.GuiceModule;
import edu.cornell.hackathon.workday.config.WorkdayConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WorkdayServer extends Application<WorkdayConfig> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkdayServer.class);

	public WorkdayServer() {
	}

	public static void main(final String[] args) {
		try {
			new WorkdayServer().run(args);
		} catch (final Exception e) {           
			LOGGER.error("Workday Server failed on startup: ", e);
		}
	}


	@Override
	public void initialize(final Bootstrap<WorkdayConfig> bootstrap) {
		bootstrap.addBundle(
				GuiceBundle.defaultBuilder(WorkdayConfig.class).modules(new GuiceModule()).build());
	}

	@Override
	public void run(final WorkdayConfig configuration, final Environment environment) throws Exception {
		// TODO Auto-generated method stub

	}


}
