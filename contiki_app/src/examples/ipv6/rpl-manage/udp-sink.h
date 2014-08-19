#ifndef UDP_SINK_H_INCLUDED
    #define UDP_SINK_H_INCLUDED
    #include "contiki-lib.h"
    #include "contiki-net.h"
    #include "net/ip/uip.h"
    #include "net/rpl/rpl.h"
    #include "rpl-private.h"

    #include "net/netstack.h"
    #include "dev/button-sensor.h"
    #include "dev/serial-line.h"
    #if CONTIKI_TARGET_Z1
    #include "dev/uart0.h"
    #else
    #include "dev/uart1.h"
    #endif
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    #include <ctype.h>


    #define UIP_IP_BUF   ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])
    #define DEBUG DEBUG_PRINT
    #include "net/ip/uip-debug.h"

    #include "net/packetbuf.h"

    #define UDP_CLIENT_PORT 3000
    #define UDP_SERVER_PORT 3001

    #include "geolocation.h"
    #include "data.h"

void print_sink_id_dvn(void);

#endif


