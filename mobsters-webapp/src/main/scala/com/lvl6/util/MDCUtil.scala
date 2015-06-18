package com.lvl6.util

import com.lvl6.properties.MDCKeys
import org.slf4j.MDC

object MDCUtil {
  def purgeMDCProperties= {
    MDC.remove(MDCKeys.UDID);
    MDC.remove(MDCKeys.PLAYER_ID);
    MDC.remove(MDCKeys.IP);
  }

  def setMDCProperties(udid:String, playerId:String, ip:String) {
    purgeMDCProperties;
    if (udid != null)
      MDC.put(MDCKeys.UDID, udid);
    if (ip != null)
      MDC.put(MDCKeys.IP, ip);
    if (null != playerId && !playerId.isEmpty())
      MDC.put(MDCKeys.PLAYER_ID, playerId);
  }
}