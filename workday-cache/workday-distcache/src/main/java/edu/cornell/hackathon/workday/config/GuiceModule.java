package edu.cornell.hackathon.workday.config;

import com.google.inject.Binder;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;

import edu.cornell.hackathon.workday.distcache.DistCache;
import edu.cornell.hackathon.workday.resources.WorkdayResource;

public class GuiceModule extends DropwizardAwareModule<WorkdayConfig> {

	@Override
	public void configure(final Binder binder) {

        DistCache distCache = new DistCache();

        binder.bind(DistCache.class).toInstance(distCache);

		binder.bind(WorkdayResource.class);
	}

}
