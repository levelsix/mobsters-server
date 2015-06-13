package com.lvl6.utils;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.lvl6.events.ResponseEvent;
import com.lvl6.properties.Globals;
import com.lvl6.proto.ProtocolsProto.EventProto;

/**
 * NIOUtils.java
 *
 * Misc utility functions to simplify dealing w/ NIO channels and buffers.
 */
public class NIOUtils {

	private static final Logger log = LoggerFactory.getLogger(NIOUtils.class);

	/**
	 * first, writes the header, then the event into the given ByteBuffer in
	 * preparation for the channel write
	 */
	public static ByteBuffer prepBuffer(ResponseEvent event) {

		ByteBuffer writeBuffer = ByteBuffer
				.allocateDirect(event.eventSize()+Attachment.HEADER_SIZE);
		
		// write header
		writeBuffer.clear();
		
		// Write the event and then put it an EventProto
		event.write(writeBuffer);
		
		writeBuffer.flip();
		
		EventProto.Builder ep = EventProto.newBuilder();
		ep.setEventType(event.getEventType().getNumber());
		ep.setTagNum(event.getTag());
		ep.setEventBytes(ByteString.copyFrom(writeBuffer));
		
		writeBuffer.clear();
		
		ByteString byteString = ep.build().toByteString();
		int size = byteString.size();
		writeBuffer.put((byte) (size & 0xFF));
		writeBuffer.put((byte) ((size & 0xFF00) >> 8));
		writeBuffer.put((byte) ((size & 0xFF0000) >> 16));
		writeBuffer.put((byte) ((size & 0xFF000000) >> 24));
		
		byteString.copyTo(writeBuffer);

		// prepare for a channel.write
		writeBuffer.flip();
		
		return writeBuffer;
	}

	/**
	 * write the contents of a ByteBuffer to the given SocketChannel
	 */
	public static void channelWrite(SocketChannel channel,
			ByteBuffer writeBuffer, int playerId) {
		long nbytes = 0;
		long toWrite = writeBuffer.remaining();

		// loop on the channel.write() call since it will not necessarily
		// write all bytes in one shot
		try {
			while (nbytes != toWrite) {
				nbytes += channel.write(writeBuffer);

				try {
					Thread.sleep(Globals.CHANNEL_WRITE_SLEEP);
				} catch (InterruptedException e) {
					log.error("error in writing to channel " + channel
							+ " for player " + playerId, e);
				}
			}
		} catch (ClosedChannelException cce) {
			log.info("tried to write back to a closed channel " + channel
					+ " for player " + playerId, cce);
		} catch (Exception e) {
			log.error("error in writing to channel " + channel + " for player "
					+ playerId, e);
		}

		// get ready for another write if needed
		writeBuffer.rewind();
	}

	/**
	 * write a String to a ByteBuffer, prepended with a short integer
	 * representing the length of the String
	 */
	public static void putStr(ByteBuffer buff, String str) {
		if (str == null) {
			buff.put((byte) 0);
		} else {
			buff.put((byte) str.length());
			buff.put(str.getBytes());
		}
	}

	/**
	 * read a String from a ByteBuffer that was written w/the putStr method
	 */
	public static String getStr(ByteBuffer buff) {
		byte len = buff.get();
		if (len == 0) {
			return null;
		} else {
			byte[] b = new byte[len];
			buff.get(b);
			return new String(b);
		}
	}

}
