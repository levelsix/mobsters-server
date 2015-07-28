package com.lvl6.ui.admin.components;

import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.server.ServerAdmin;
import com.lvl6.spring.AppContext;
import com.lvl6.ui.admin.pages.AdminPage;

public class ReloadPvpUsersLink extends Link<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3433143309462013846L;
	
	Logger log = LoggerFactory.getLogger(getClass());

	public ReloadPvpUsersLink(String id) {
		super(id);
	}

	@Override
	public void onClick() {
		log.info("An Admin requested a reload of all pvp users data");
		ServerAdmin sa = AppContext.get().getBean(
				ServerAdmin.class);
		sa.reloadPvpUsers();
		setResponsePage(AdminPage.class);
	}

}
