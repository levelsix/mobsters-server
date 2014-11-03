package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IList;
import com.lvl6.info.User;
import com.lvl6.proto.ChatProto.GroupChatMessageProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;

public class SetGlobalChatMessageAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final GroupChatComparator comparator = new GroupChatComparator();
	
	private static final class GroupChatComparator implements Comparator<GroupChatMessageProto> {
		@Override
		public int compare(GroupChatMessageProto o1, GroupChatMessageProto o2) {
			if (o1.getTimeOfChat() < o2.getTimeOfChat()) {
				return -1;
			} else if (o1.getTimeOfChat() > o2.getTimeOfChat()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private final StartupResponseProto.Builder resBuilder;
	private final IList<GroupChatMessageProto> chatMessages;
	
	public SetGlobalChatMessageAction(
		StartupResponseProto.Builder resBuilder, User user,
		IList<GroupChatMessageProto> chatMessages
		)
	{
		this.resBuilder = resBuilder;
		this.chatMessages = chatMessages;
	}
	
	//Extracted from Startup
	public void execute()
	{
		Iterator<GroupChatMessageProto> it = chatMessages.iterator();
		List<GroupChatMessageProto> globalChats = new ArrayList<GroupChatMessageProto>();
		while (it.hasNext()) {
			globalChats.add(it.next());
		}
		
		Collections.sort(globalChats, comparator);
		// Need to add them in reverse order
		for (int i = 0; i < globalChats.size(); i++) {
			resBuilder.addGlobalChats(globalChats.get(i));
		}
	}

}
