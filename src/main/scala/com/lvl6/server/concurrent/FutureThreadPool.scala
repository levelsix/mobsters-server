package com.lvl6.server.concurrent

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

object FutureThreadPool {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(100))
}