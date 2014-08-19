#ifndef BROADCAST_H_INCLUDED
#define BROADCAST_H_INCLUDED

#include "contiki.h"
#include "net/ip/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "simple-udp.h"

#include "net/rime/rimestats.h"
#include "dev/leds.h"

#include <stdio.h>
#include <stdlib.h>

#define BROADCAST_PORT 3002

void broadcast_init();
void broadcast_doSend(const void* data, uint16_t len);

#endif




