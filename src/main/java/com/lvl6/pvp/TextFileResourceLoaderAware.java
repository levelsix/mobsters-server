package com.lvl6.pvp;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TextFileResourceLoaderAware implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	public TextFileResourceLoaderAware(ResourceLoader resourceLoader) {
		super();
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// TODO Auto-generated method stub
		this.resourceLoader = resourceLoader;
	}

	public Resource getResource(String location) {
		return resourceLoader.getResource(location);
	}

}