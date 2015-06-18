package com.lvl6.scheduledtasks

import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import com.lvl6.websockets.ClientConnections


@Component
class ServerTasks {
  
  @Scheduled(fixedRate=20000)
  def pingConnections= {
    ClientConnections.pingConnections
  }
}