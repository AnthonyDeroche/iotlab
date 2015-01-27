#include "broadcast.h"

static struct simple_udp_connection broadcast_connection;
static uip_ipaddr_t addr;

void broadcast_init(void* callback){
    simple_udp_register(&broadcast_connection, BROADCAST_PORT,NULL, BROADCAST_PORT,callback);
    uip_create_linklocal_allnodes_mcast(&addr);
}

void broadcast_doSend(const void* data, uint16_t len){
    simple_udp_sendto(&broadcast_connection, data, len, &addr);
}
