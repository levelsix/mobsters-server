package com.lvl6.ui.admin.components;

import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.server.ServerAdmin;
import com.lvl6.spring.AppContext;
import com.lvl6.ui.admin.pages.AdminPage;

public class ReloadClanSearchLink extends Link<String> {

	Logger log = LoggerFactory.getLogger(getClass());

	public ReloadClanSearchLink(String id) {
		super(id);
	}
	
//	@Autowired
//	protected LeaderBoardImpl leaderBoardImpl;

	private static final long serialVersionUID = 1L;

	@Override
	public void onClick() {
		log.info("An Admin requested a reload for leaderboard");
//		leaderBoardImpl.reload();
		
		ServerAdmin sa = AppContext.get().getBean(
				ServerAdmin.class);
		sa.reloadClanSearch();
		setResponsePage(AdminPage.class);
	}

}
