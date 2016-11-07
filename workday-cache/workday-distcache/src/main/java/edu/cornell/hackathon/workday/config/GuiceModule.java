package edu.cornell.hackathon.workday.config;

import com.google.inject.Binder;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;

import edu.cornell.hackathon.workday.distcache.DistCache;
import edu.cornell.hackathon.workday.resources.WorkdayResource;

public class GuiceModule extends DropwizardAwareModule<WorkdayConfig> {

	@Override
	public void configure(final Binder binder) {

		binder.bind(DistCache.class).toInstance(new DistCache());

		binder.bind(WorkdayResource.class);

	}

}
