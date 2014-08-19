#include "contiki.h"
#include "contiki-lib.h"
#include "contiki-net.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ipv6/multicast/uip-mcast6.h"
#include <string.h>

#define DEBUG DEBUG_NONE
#include "net/ip/uip-debug.h"
#include "net/rpl/rpl.h"

//#define UIP_IP_BUF ((struct uip_ip_hdr *)&uip_buf[UIP_LLH_LEN])

#define MAX_PAYLOAD_LEN

uint8_t multicast_init(uip_ipaddr_t *ipaddr, uint16_t mport);
void multicast_send(uint8_t conn_id, const void* data, uint16_t len);
void join_mcast_group(int conn_id);
void setTTL(uint8_t conn_id,uint8_t ttl);
