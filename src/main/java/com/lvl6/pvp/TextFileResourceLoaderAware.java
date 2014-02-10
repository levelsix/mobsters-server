package com.lvl6.pvp;

import java.io.Serializable;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class TextFileResourceLoaderAware implements ResourceLoaderAware, Serializable {

	private static final long serialVersionUID = 7320635945876211445L;
	private ResourceLoader resourceLoader;

	
	public TextFileResourceLoaderAware() {
		super();
	}

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