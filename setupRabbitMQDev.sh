#!/bin/bash

#create vhosts

echo creating vhost devmobsters
rabbitmqctl add_vhost devmobsters


#create users

rabbitmqctl add_user lvl6client devclient
rabbitmqctl add_user lvl6server devserver

#set permissions

rabbitmqctl set_permissions -p devmobsters lvl6client ".*" ".*" ".*"
rabbitmqctl set_permissions -p devmobsters lvl6server ".*" ".*" ".*"
