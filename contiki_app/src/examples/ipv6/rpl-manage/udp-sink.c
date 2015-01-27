#include "udp-sink.h"
#include "sink_command.h"

static struct uip_udp_conn *data_conn;
static struct uip_udp_conn *geoloc_conn;

PROCESS(udp_server_process, "UDP server process");
AUTOSTART_PROCESSES(&udp_server_process);



/*---------------------------------------------------------------------------*/
static void tcpip_handler(void)
{
  linkaddr_t sender;
  uint8_t hops;

  if(uip_newdata()) {

        sender.u8[0] = UIP_IP_BUF->srcipaddr.u8[15];
        sender.u8[1] = UIP_IP_BUF->srcipaddr.u8[14];
        hops = uip_ds6_if.cur_hop_limit - UIP_IP_BUF->ttl + 1;

    if(UIP_HTONS(UIP_IP_BUF->destport)==UDP_SERVER_PORT){ //DATA
        uint8_t *appdata;
        appdata = (uint8_t *)uip_appdata;
        uint8_t seqno;
        seqno = *appdata;

        /*PRINTF("RSSI %d, src port %u, dest port %u\n",(signed int16_t)packetbuf_attr(PACKETBUF_ATTR_RSSI) + RSSI_OFFSET
          ,UIP_HTONS(UIP_IP_BUF->srcport),UIP_HTONS(UIP_IP_BUF->destport));*/
        printf("0 ");
        data_recv(&sender, seqno, hops,
                            appdata + 2, uip_datalen() - 2);

    }else if(UIP_HTONS(UIP_IP_BUF->destport)==UDP_GEOLOC_PORT){ //GEOLOCATION
        uint8_t *appdata;
        appdata = (uint16_t *)uip_appdata;
        uint16_t n_node_id;
        int16_t nb;
        memcpy(&n_node_id,appdata,2);
        memcpy(&nb,appdata+2,2);

       // printf("#Received %d geolocation data from %u : \n",nb,n_node_id);

        printf("10 %u %d",n_node_id,nb);
        uint8_t* payload = appdata+4;
        uint16_t data;
        int i;
        for(i = 0; i < 2*nb; i++) {
            memcpy(&data, payload, sizeof(data));
            payload += sizeof(data);
            if(i%2==0)
                printf(" %u", data);
            else
                 printf(" %d", data);
          }
          printf("\n");
          memset(appdata,0x0,4+2*nb*sizeof(data));
    }
  }
}
/*---------------------------------------------------------------------------*/
static void print_local_addresses(void)
{
  int i;
  uint8_t state;
  printf("I am sink!\n");
  PRINTF("Server IPv6 addresses: ");
  for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
    state = uip_ds6_if.addr_list[i].state;
    if(state == ADDR_TENTATIVE || state == ADDR_PREFERRED) {
      PRINT6ADDR(&uip_ds6_if.addr_list[i].ipaddr);
      PRINTF("\n");
      /* hack to make address "final" */
      if (state == ADDR_TENTATIVE) {
        uip_ds6_if.addr_list[i].state = ADDR_PREFERRED;
      }
    }
  }
}
/*---------------------------------------------------------------------------*/
void
print_sink_id_dvn(void)
{
  uip_ipaddr_t sink_addr;
  uip_ip6addr(&sink_addr, 0xaaaa, 0, 0, 0, 0, 0, 0, 1);
  uip_ds6_set_addr_iid(&sink_addr, &uip_lladdr);
  printf("1 %u %d\n",sink_addr.u8[15] + (sink_addr.u8[14]<<8),instance_table[0].dag_table[0].version);
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_server_process, ev, data)
{
  uip_ipaddr_t ipaddr;
  struct uip_ds6_addr *root_if;

  PROCESS_BEGIN();

  PROCESS_PAUSE();

  SENSORS_ACTIVATE(button_sensor);

  PRINTF("UDP server started\n");

#if UIP_CONF_ROUTER
  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 1);
  //uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);


  root_if = uip_ds6_addr_lookup(&ipaddr);
  if(root_if != NULL) {
    rpl_dag_t *dag;
    dag = rpl_set_root(RPL_DEFAULT_INSTANCE,(uip_ip6addr_t *)&ipaddr);
    //uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
    rpl_set_prefix(dag, &ipaddr, 64);
    PRINTF("created a new RPL dag\n");
  } else {
    PRINTF("failed to create a new RPL DAG\n");
  }
#endif /* UIP_CONF_ROUTER */

  print_local_addresses();

  /* The data sink runs with a 100% duty cycle in order to ensure high
     packet reception rates. */
  NETSTACK_RDC.off(1);

  data_conn = udp_new(NULL, UIP_HTONS(UDP_CLIENT_PORT), NULL);
  udp_bind(data_conn, UIP_HTONS(UDP_SERVER_PORT));

  PRINTF("Created a server connection with remote address ");
  PRINT6ADDR(&data_conn->ripaddr);
  PRINTF(" local/remote port %u/%u\n", UIP_HTONS(data_conn->lport),
         UIP_HTONS(data_conn->rport));


  geoloc_conn = udp_new(NULL, UIP_HTONS(0), NULL);
  udp_bind(geoloc_conn, UIP_HTONS(UDP_GEOLOC_PORT));

  PRINTF("Created a server connection with remote address ");
  PRINT6ADDR(&geoloc_conn->ripaddr);
  PRINTF(" local/remote port %u/%u\n", UIP_HTONS(geoloc_conn->lport),
         UIP_HTONS(geoloc_conn->rport));

  //command sink
  command_sink_init();

  while(1) {
    PROCESS_YIELD();

    command_sink_check(&udp_server_process, ev, data);

    if(ev == tcpip_event) {
      tcpip_handler();
    } else if (ev == sensors_event && data == &button_sensor) {
      PRINTF("Initiating global repair\n");
      rpl_repair_root(RPL_DEFAULT_INSTANCE);
    }
  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
