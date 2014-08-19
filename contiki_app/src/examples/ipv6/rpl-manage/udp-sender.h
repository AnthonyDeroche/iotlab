#ifndef UDP_SENDER_H_INCLUDED
    #define UDP_SENDER_H_INCLUDED
    #include "contiki-lib.h"
    #include "contiki-net.h"
    #include "net/ip/uip.h"
    #include "net/ipv6/uip-ds6.h"
    #include "net/ip/uip-udp-packet.h"

    #include "dev/serial-line.h"


    #include <stdio.h>
    #include <string.h>

    #define UIP_IP_BUF ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])
    #define DEBUG DEBUG_NONE
    #include "net/ip/uip-debug.h"

    void sender_start_modules(const char* module_name);
    void sender_stop_modules(const char* module_name);
#endif
