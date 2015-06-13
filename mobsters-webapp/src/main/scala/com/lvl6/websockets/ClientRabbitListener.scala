package com.lvl6.websockets

import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.Binding
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer

case class ClientRabbitListener(routingKey:String, queue:Queue, binding:Binding, listener:SimpleMessageListenerContainer)