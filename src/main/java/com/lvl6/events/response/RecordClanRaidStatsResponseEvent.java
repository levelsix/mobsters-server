package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventClanProto.RecordClanRaidStatsResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class RecordClanRaidStatsResponseEvent extends NormalResponseEvent<RecordClanRaidStatsResponseProto> {

	

	public RecordClanRaidStatsResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_RECORD_CLAN_RAID_STATS_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setRecordClanRaidStatsResponseProto(
			RecordClanRaidStatsResponseProto responseProto) {
		this.responseProto = responseProto;
	}
	
	public int eventSize() {
		return responseProto.getSerializedSize();
	}

}
