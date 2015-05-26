package com.lvl6.events.response;

import java.nio.ByteBuffer;

import com.google.protobuf.ByteString;
import com.lvl6.events.NormalResponseEvent;
import com.lvl6.proto.EventStructureProto.PurchaseNormStructureResponseProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolResponse;

public class PurchaseNormStructureResponseEvent extends NormalResponseEvent<PurchaseNormStructureResponseProto> {

	private PurchaseNormStructureResponseProto responseProto;

	public PurchaseNormStructureResponseEvent(String playerId) {
		super(playerId);
		eventType = EventProtocolResponse.S_PURCHASE_NORM_STRUCTURE_EVENT;
	}

	@Override
	public int write(ByteBuffer bb) {
		ByteString b =  responseProto.toByteString();
		b.copyTo(bb);
		return b.size();
	}

	public void setPurchaseNormStructureResponseProto(
			PurchaseNormStructureResponseProto responseProto) {
		this.responseProto = responseProto;
	}

	public int eventSize() {
		return purchaseNormStructureResponseProto.getSerializedSize();
	}
}
