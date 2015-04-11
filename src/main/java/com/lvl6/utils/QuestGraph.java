package com.lvl6.utils;

import java.util.ArrayList;
import java.util.List;

import com.lvl6.info.Quest;

public class QuestGraph {
	private class Vertex {
		private int nodeId;
		private List<Integer> requiredVertices;

		public Vertex(int id, List<Integer> required) {
			nodeId = id;
			requiredVertices = required;
		}

		public int getNodeId() {
			return nodeId;
		}

		public List<Integer> getRequiredVertices() {
			return requiredVertices;
		}

		@Override
		public String toString() {
			String s = nodeId + ": ";
			for (int x : requiredVertices) {
				s += x + ", ";
			}
			return "{" + s.substring(0, s.length() - 2) + "}";
		}
	}

	private ArrayList<Vertex> questVertices;

	public QuestGraph(List<Quest> quests) {
		questVertices = new ArrayList<Vertex>(quests.size());
		for (Quest quest : quests) {
			questVertices.add(new Vertex(quest.getId(), quest
					.getQuestsRequiredForThis()));
		}
	}

	public List<Integer> getQuestsAvailable(List<Integer> redeemed,
			List<Integer> inProgress) {
		ArrayList<Integer> available = new ArrayList<Integer>();

		for (Vertex v : questVertices) {
			//search for quests the user has not redeemed nor is in
			//the process of completing
			if (redeemed == null || !redeemed.contains(v.getNodeId())) {
				if (inProgress == null || !inProgress.contains(v.getNodeId())) {

					if (v.getRequiredVertices().isEmpty()
							|| (redeemed != null && redeemed.containsAll(v
									.getRequiredVertices()))) {
						//for all stand alone quests (quests with no prerequisites) OR
						//quests that the user has finished all the prerequisites, return these
						available.add(v.getNodeId());
					}
				}
			}
		}

		return available;
	}

	@Override
	public String toString() {
		return questVertices.toString();
	}
}
