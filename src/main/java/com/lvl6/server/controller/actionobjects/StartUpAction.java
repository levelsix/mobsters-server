package com.lvl6.server.controller.actionobjects;


public interface StartUpAction
{
	/**
	 * 
	 * @return A collection of groups of ids this ControllerAction needs in order to continue processing
	 */
	public void setUp(StartUpResource fillMe);
	
	public void execute(StartUpResource useMe);
}
