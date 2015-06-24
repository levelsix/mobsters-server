package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.Research;
import com.lvl6.info.ResearchProperty;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ResearchForUserPojo;
import com.lvl6.proto.ResearchsProto.AllUserResearchProto;
import com.lvl6.proto.ResearchsProto.ResearchDomain;
import com.lvl6.proto.ResearchsProto.ResearchPropertyProto;
import com.lvl6.proto.ResearchsProto.ResearchProto;
import com.lvl6.proto.ResearchsProto.ResearchType;
import com.lvl6.proto.ResearchsProto.UserResearchProto;
import com.lvl6.proto.StructureProto.ResourceType;
import com.lvl6.retrieveutils.rarechange.ResearchPropertyRetrieveUtils;
import com.lvl6.retrieveutils.rarechange.ResearchRetrieveUtils;

@Component
public class ResearchUtil {

	
	private static final Logger log = LoggerFactory
			.getLogger(ResearchUtil.class);
	
	public List<AllUserResearchProto> createAllUserResearchProto(Map<String, 
			List<ResearchForUserPojo>> mapOfResearches, 
			ResearchRetrieveUtils researchRetrieveUtils,
			ResearchPropertyRetrieveUtils researchPropertyRetrieveUtils) {
		List<AllUserResearchProto> returnList = new ArrayList<AllUserResearchProto>();
		for(String userId : mapOfResearches.keySet()) {
			AllUserResearchProto.Builder b = AllUserResearchProto.newBuilder();
			b.setUserUuid(userId);
			List<ResearchForUserPojo> rfuList = mapOfResearches.get(userId);
			for(ResearchForUserPojo rfu : rfuList) {
				Research r = researchRetrieveUtils.getResearchForId(rfu.getResearchId());
				Map<Integer, ResearchProperty> researchPropertyMap = 
						researchPropertyRetrieveUtils.getResearchPropertiesForResearchId(rfu.getResearchId());
				ResearchProto rp = createResearchProto(r, researchPropertyMap.values());
				b.addUserResearch(rp);
			}
			returnList.add(b.build());
		}
		return returnList;
	}
	
	/** Research.proto ****************************************/
	public ResearchProto createResearchProto(Research r,
			Collection<ResearchProperty> researchProperties) {
		ResearchProto.Builder rpb = ResearchProto.newBuilder();

		rpb.setResearchId(r.getId());
		String rt = r.getResearchType();
		if (null != rt) {
			try {
				ResearchType researchType = ResearchType.valueOf(rt);

				rpb.setResearchType(researchType);
			} catch (Exception e) {
				log.error(String.format(
						"invalid research type. Researchtype=%s", rt), e);
			}

		}

		String rd = r.getResearchDomain();

		if (null != rd) {
			try {
				ResearchDomain researchDomain = ResearchDomain.valueOf(rd);
				rpb.setResearchDomain(researchDomain);
			} catch (Exception e) {
				log.error(String.format(
						"invalid research domain. Researchdomain=%s", rd), e);
			}

		}

		String str;
		str = r.getIconImgName();
		if (null != str && !str.isEmpty()) {
			rpb.setIconImgName(str);
		}

		str = r.getName();
		if (null != str && !str.isEmpty()) {
			rpb.setName(str);
		}

		int predId = r.getPredId();
		if (predId > 0) {
			rpb.setPredId(predId);
		}
		int succId = r.getSuccId();
		if (succId > 0) {
			rpb.setSuccId(succId);
		}

		str = r.getDesc();
		if (null != str) {
			rpb.setDesc(str);
		}

		rpb.setDurationMin(r.getDurationMin());
		rpb.setCostAmt(r.getCostAmt());
		rpb.setLevel(r.getLevel());

		str = r.getCostType();
		if (null != str && !str.isEmpty()) {
			try {
				ResourceType resType = ResourceType.valueOf(str);
				rpb.setCostType(resType);
			} catch (Exception e) {
				log.error(
						String.format("invalid ResourceType. Research=%s", r),
						e);
			}
		}

		if (null != researchProperties) {
			List<ResearchPropertyProto> rppList = createResearchPropertyProto(researchProperties);
			rpb.addAllProperties(rppList);
		}

		rpb.setPriority(r.getPriority());
		rpb.setTier(r.getTier());
		rpb.setStrength(r.getStrength());

		return rpb.build();
	}

	public List<ResearchPropertyProto> createResearchPropertyProto(
			Collection<ResearchProperty> rpCollection) {
		List<ResearchPropertyProto> retVal = new ArrayList<ResearchPropertyProto>();
		for (ResearchProperty bp : rpCollection) {
			ResearchPropertyProto bpp = createResearchPropertyProto(bp);

			retVal.add(bpp);
		}

		return retVal;
	}

	public ResearchPropertyProto createResearchPropertyProto(
			ResearchProperty rp) {
		ResearchPropertyProto.Builder rppb = ResearchPropertyProto.newBuilder();

		rppb.setResearchPropertyId(rp.getId());

		String str = rp.getName();
		if (null != str && !str.isEmpty()) {
			rppb.setName(str);
		}

		rppb.setResearchValue(rp.getValue());
		rppb.setResearchId(rp.getId());

		return rppb.build();
	}

	public Collection<UserResearchProto> createUserResearchProto(
			Collection<ResearchForUserPojo> userResearchs) {
		List<UserResearchProto> urpList = new ArrayList<UserResearchProto>();
		for (ResearchForUserPojo rfu : userResearchs) {
			UserResearchProto urp = createUserResearchProto(rfu);
			urpList.add(urp);
		}
		return urpList;
	}

	public UserResearchProto createUserResearchProto(ResearchForUserPojo rfu) {
		UserResearchProto.Builder urpb = UserResearchProto.newBuilder();
		urpb.setUserResearchUuid(rfu.getId());
		urpb.setResearchId(rfu.getResearchId());
		urpb.setUserUuid(rfu.getUserId());
		urpb.setTimePurchased(rfu.getTimePurchased().getTime());
		urpb.setComplete(rfu.getIsComplete());

		return urpb.build();
	}
	
}