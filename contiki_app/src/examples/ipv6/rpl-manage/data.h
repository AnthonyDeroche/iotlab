#ifndef DATA_H_INCLUDED
#define DATA_H_INCLUDED

#include "contiki.h"
#include "contiki-conf.h"
#include "net/linkaddr.h"
#include "contiki-net.h"
#include "net/ip/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/rime/collect-neighbor.h"
#include "net/rime/rime.h"
#include "net/rime/timesynch.h"
#include "net/rime/collect.h"
#include "net/rpl/rpl.h"
#include "net/ip/uip-udp-packet.h"
#include "lib/random.h"
#include "net/netstack.h"
#include "dev/serial-line.h"
#include "dev/leds.h"
#include "dev/uart1.h"

#include "cc2420.h"
#include "dev/leds.h"

#if CONTIKI_TARGET_SKY
#include "dev/light-sensor.h"
#include "dev/battery-sensor.h"
#include "dev/sht11/sht11-sensor.h"
#else
#include "dev/i2cmaster.h"
#include "dev/tmp102.h"
#endif

#define DEBUG DEBUG_PRINT
#include "net/ip/uip-debug.h"

#define UDP_CLIENT_PORT 3000
#define UDP_SERVER_PORT 3001

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

void data_check(struct process *p, process_event_t ev, void *data);
void data_init(uip_ipaddr_t serv_ipaddr);
void set_data_period(uint16_t period);
void data_start();
void data_stop();

void data_recv(const linkaddr_t *originator, uint8_t seqno, uint8_t hops,
                    uint8_t *payload, uint16_t payload_len);



#endif
