package com.lvl6.util

object Util {
  def notNullOrEmpty(in:String):Option[String]={
    if(in != null && !in.isEmpty()){
      Some(in)
    }else None
  }
}