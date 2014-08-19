#include "multicast.h"
#define MCAST_NB 1

static struct uip_udp_conn* mcast_conn[MCAST_NB];
static char buf[MAX_PAYLOAD_LEN];
static uip_ipaddr_t maddr[MCAST_NB];
static struct uip_udp_conn* rec_mcast_conn[MCAST_NB];
static uint16_t mcastport[MCAST_NB];
static uint8_t conn_nb=0;
static uint8_t joined=0;
/*---------------------------------------------------------------------------*/
uint8_t
multicast_init(uip_ipaddr_t *ipaddr, uint16_t mport)
{
   mcastport[conn_nb]=mport;
   maddr[conn_nb]=*ipaddr;
  /*
   * IPHC will use stateless multicast compression for this destination
   * (M=1, DAC=0), with 32 inline bits (1E 89 AB CD)
   */

  mcast_conn[conn_nb++] = udp_new(&maddr[conn_nb], UIP_HTONS(mport), NULL);
  return conn_nb-1;
}

/*---------------------------------------------------------------------------*/
void
multicast_send(uint8_t conn_id, const void* data, uint16_t len)
{
	  PRINTF("Send to: ");
	  PRINT6ADDR(&mcast_conn[conn_id]->ripaddr);
	  PRINTF(" Remote Port %u,", uip_ntohs(mcast_conn[conn_id]->rport));
	  PRINTF(" (msg=%s)\n", (char*)data);

	  uip_udp_packet_send(mcast_conn[conn_id], data, len);

}
/*---------------------------------------------------------------------------*/
void
join_mcast_group(int conn_id)
{

  uip_ds6_maddr_t *rv;
  uip_ipaddr_t addr;

  /* First, set our v6 global */
  /*uip_ip6addr(&maddr[conn_id], 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&maddr[conn_id], &uip_lladdr);
  uip_ds6_addr_add(&maddr[conn_id], 0, ADDR_AUTOCONF);*/

  /*
   * IPHC will use stateless multicast compression for this destination
   * (M=1, DAC=0), with 32 inline bits (1E 89 AB CD)
   */

  addr = maddr[conn_id];
  rv = uip_ds6_maddr_add(&addr);

  rec_mcast_conn[conn_id] = udp_new(NULL, UIP_HTONS(0), NULL);
  udp_bind(rec_mcast_conn[conn_id], UIP_HTONS(mcastport[conn_id]));

  if(rv) {
    printf("[MULTICAST] Joined multicast group ");
    PRINT6ADDR(&uip_ds6_maddr_lookup(&addr)->ipaddr);
    printf("\n");
    joined = 1;
  }
}
/*---------------------------------------------------------------------------*/
void setTTL(uint8_t conn_id,uint8_t ttl){
    mcast_conn[conn_id]->ttl=ttl;
}
