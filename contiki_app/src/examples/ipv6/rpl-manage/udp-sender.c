#include "udp-sender.h"
#include "data.h"
#include "geolocation.h"
#include "sender_command.h"

#define MODULE_NB 3

static uint16_t n_node_id;
static uip_ipaddr_t server_ipaddr;

/*---------------------------------------------------------------------------*/
PROCESS(udp_client_process, "UDP client process");
AUTOSTART_PROCESSES(&udp_client_process);

/*---------------------------------------------------------------------------*/
/*static void print_local_addresses(void)
{
  int i;
  uint8_t state;

  PRINTF("Client IPv6 addresses: ");
  for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
    state = uip_ds6_if.addr_list[i].state;
    if(uip_ds6_if.addr_list[i].isused &&
       (state == ADDR_TENTATIVE || state == ADDR_PREFERRED)) {
      PRINT6ADDR(&uip_ds6_if.addr_list[i].ipaddr);
      PRINTF("\n");
      // hack to make address "final"
      if (state == ADDR_TENTATIVE) {
        uip_ds6_if.addr_list[i].state = ADDR_PREFERRED;
      }
    }
  }
}*/
/*---------------------------------------------------------------------------*/
static void set_global_address(void)
{
  uip_ipaddr_t ipaddr;

  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);

  n_node_id = ipaddr.u8[15] + (ipaddr.u8[14]<<8);
  PRINTF("My ID : %u\n",n_node_id);

  /* set server address */
  uip_ip6addr(&server_ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 1);
}
/*---------------------------------------------------------------------------*/
static struct module{
    void (*start)();
    void (*stop)();
    void (*checker)();
    int8_t id;
    char* name;
};

static struct module modules[MODULE_NB];
/*---------------------------------------------------------------------------*/
void sender_start_modules(const char* module_name){
	printf("Starting %s\n",module_name);
    int i;
    for(i=0;i<MODULE_NB;i++){
	if(strncmp(module_name,"ALL",3)==0 || strncmp(modules[i].name,module_name,strlen(modules[i].name))==0){
		(modules[i].start)();
	}
    }
}
/*---------------------------------------------------------------------------*/
void sender_stop_modules(const char* module_name){
	printf("Stopping %s\n",module_name);
    int i;
    for(i=0;i<MODULE_NB;i++){
	if(strncmp(module_name,"ALL",3)==0 || strncmp(modules[i].name,module_name,strlen(modules[i].name))==0){
        	(modules[i].stop)();
	}
    }
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_client_process, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  SENSORS_ACTIVATE(button_sensor);
  set_global_address();

  PRINTF("UDP client process started\n");

  //init command module
  command_sender_init();
  modules[0].start = &sender_command_start;
  modules[0].stop = &sender_command_stop;
  modules[0].checker = &sender_command_check;
  modules[0].id=0;
  modules[0].name="COMMAND";

   //init data module
  data_init(server_ipaddr);
  modules[2].start = &data_start;
  modules[2].stop = &data_stop;
  modules[2].checker = &data_check;
  modules[2].id=1;
  modules[2].name="DATA";

   //init geolocation module
  geoloc_init(server_ipaddr,n_node_id);
  modules[1].start = &geoloc_start;
  modules[1].stop = &geoloc_stop;
  modules[1].checker = &geoloc_check;
  modules[1].id=2;
  modules[1].name="GEOLOC";

  //print_local_addresses();
  int i;
  while(1) {
    PROCESS_YIELD();

    for(i=0;i<MODULE_NB;i++){
        (modules[i].checker)(&udp_client_process,ev,data);
    }

  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
