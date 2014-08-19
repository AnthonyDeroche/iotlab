#ifndef GEOLOCATION_H_INCLUDED
#define GEOLOCATION_H_INCLUDED

#include "contiki-lib.h"
#include "contiki-net.h"
#include "net/ip/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ip/uip-udp-packet.h"

#include "dev/button-sensor.h"
#include "dev/light-sensor.h"
#include "dev/leds.h"
#include "random.h"

#include "broadcast.h"
//#include "multicast.h"

#include "dev/cc2420/cc2420.h"
#include <string.h>
#define UDP_GEOLOC_PORT 5690

void geoloc_init(uip_ipaddr_t serv_ipaddr,uint16_t node_id);
void geoloc_check(struct process *p, process_event_t ev, void *data);
void reception_callback(struct simple_udp_connection *c, const uip_ipaddr_t *sender_addr,uint16_t sender_port,
         const uip_ipaddr_t *receiver_addr,uint16_t receiver_port,const uint8_t *data,uint16_t datalen);
void geoloc_start();
void geoloc_stop();
#endif // GEOLOCATION_H_INCLUDED
