package com.lvl6.server.dynamodb

import java.util.Base64

object Converter {
  implicit def bytesToBase64(bytes:Array[Byte]):String={
    return Base64.getEncoder.encodeToString(bytes)
  }
  
  implicit def stringToBase64(bytes:String):Array[Byte]={
    return Base64.getDecoder.decode(bytes)
  }
  
  def caseClassToMap(cc:AnyRef)= 
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
    f.setAccessible(true)
    a + (f.getName -> f.get(cc))
  }
}