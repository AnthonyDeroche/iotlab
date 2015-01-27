#include "geolocation.h"

/* Start sending messages START_DELAY secs after we start so that routing can
 * converge */
#define START_DELAY 10*CLOCK_SECOND
#define MAX_PAYLOAD_LEN 120

#define SEND_INTERVAL CLOCK_SECOND*1

#define RSSI_OFFSET -45 //We use the CC2420 rssi offset ~ -45 with our motes

#define GEOLOC_DATA_LENGTH 10
#define G_START_DELAY 2*START_DELAY
#define G_SEND_INTERVAL 30*CLOCK_SECOND
#define ACCEPT_RESP_DELAY 5*CLOCK_SECOND

#define G_SIMPLE_SEND 0
#define G_REQUEST_RESPONSE 1
#define G_RESPONSE 2

#define G_MCAST_PORT 4444
#define UIP_IP_BUF ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])

struct geoloc_data{
  uint16_t src_id;
  int16_t rssi;
};

static void g_periodic_send();
static void g_send_req_resp();
static void g_resp();

static void geoloc_handler(struct geoloc_data* data,int* cnt,uint16_t src_id,int16_t rssi);
static void geoloc_data_send(int16_t nb,struct geoloc_data* data);
static void handle_reception(const uip_ipaddr_t *sender_addr,const uint8_t *data);

struct geoloc_data gdata[GEOLOC_DATA_LENGTH];
struct geoloc_data neighbors_resp[GEOLOC_DATA_LENGTH];

static uint16_t n_node_id;
static uip_ipaddr_t server_ipaddr;

static struct uip_udp_conn *client_g_conn;

static struct etimer periodic_messages_timer;
static struct etimer gtim;
static struct etimer gReqTim;

static int16_t neigh_resp_cnt=0;
static int16_t geoloc_cnt=0;
static int16_t waiting_resp=0;

struct geoloc_msg{
    uint16_t node_id;
    int16_t nb;
    struct geoloc_data data[GEOLOC_DATA_LENGTH];
};

static char buf[MAX_PAYLOAD_LEN];
static uint16_t type;


static int8_t started=0;
static int8_t isStarted(){
    return started;
}

static uint8_t mid;

static uip_ipaddr_t maddr;

void geoloc_init(uip_ipaddr_t serv_ipaddr,uint16_t node_id){
    n_node_id=node_id;
    server_ipaddr=serv_ipaddr;

    broadcast_init(&reception_callback); //init broadcast module

    /*uip_ip6addr(&maddr, 0xFF1E,0,0,0,0,0,0x89,0xABCD);
	mid = multicast_init(&maddr,G_MCAST_PORT);
	setTTL(mid,1);
	join_mcast_group(mid);*/


    client_g_conn = udp_new(NULL, UIP_HTONS(UDP_GEOLOC_PORT), NULL); //create udp flow to the sink
    etimer_set(&periodic_messages_timer, START_DELAY); //set the timer of periodic messages between peers
    etimer_set(&gtim, G_START_DELAY); //set the timer to send regularly couples (node_id,rssi)
}

void geoloc_check(struct process *p, process_event_t ev, void *data){

    if(isStarted()==0){
        return;
    }

    if(neigh_resp_cnt>0 && etimer_expired(&gReqTim)){ //if neigh_resp_cnt==0 then data are sent (because neigh_resp_cnt==GEO0LOC_DATA_LENGTH)
        //data are not already sent.
        //timeout we suppose all neighbors have responded
        //PRINTF("Requested geolocation by user : Sent %d data from neighbors\ǹ",neigh_resp_cnt);
        geoloc_data_send(neigh_resp_cnt,neighbors_resp);
        etimer_stop(&gReqTim);
        neigh_resp_cnt=0;
        waiting_resp=0;
   }

    if(etimer_expired(&periodic_messages_timer)) { //if the timer of periodic messages has expired
        g_periodic_send();
        etimer_set(&periodic_messages_timer, SEND_INTERVAL + random_rand() % (CLOCK_SECOND));
    }

    if(etimer_expired(&gtim)) { //if the timer of geoloc data messages (couples) has expired
        if(geoloc_cnt>0){
            geoloc_data_send(geoloc_cnt,gdata);
            geoloc_cnt=0;
        }
        etimer_set(&gtim, G_SEND_INTERVAL);
    }

    if(ev == tcpip_event) {
        if(UIP_HTONS(UIP_IP_BUF->destport)==G_MCAST_PORT){
            handle_reception((uip_ipaddr_t*)&(UIP_IP_BUF->srcipaddr),(uint8_t *)uip_appdata);
        }
    } else if (ev == sensors_event && data == &button_sensor && !waiting_resp){
      //button pressed
      etimer_set(&gReqTim,ACCEPT_RESP_DELAY);
      waiting_resp=1;
      g_send_req_resp();
    }

}

void geoloc_start(){
    started=1;
    //printf("Starting geolocation module\n");
}

void geoloc_stop(){
    started=0;
    //printf("Stopping geolocation module\n");
}

static void g_periodic_send()
{
  //printf("Sent bcast\n");
  type = uip_htons(type);
  memset(buf, 0, MAX_PAYLOAD_LEN);
  memcpy(buf, &type, sizeof(type));
  //multicast_send(mid,buf,sizeof(buf));
  broadcast_doSend(buf,sizeof(buf));
  type = G_SIMPLE_SEND;
}

static void g_send_req_resp()
{
    type = G_REQUEST_RESPONSE;
    g_periodic_send();
}

static void g_resp()
{
    type = G_RESPONSE;
    g_periodic_send();
}

void reception_callback(struct simple_udp_connection *c, const uip_ipaddr_t *sender_addr,uint16_t sender_port,
         const uip_ipaddr_t *receiver_addr,uint16_t receiver_port,const uint8_t *data,uint16_t datalen){
    handle_reception(sender_addr,data);
}

static void handle_reception(const uip_ipaddr_t *sender_addr,const uint8_t *data)
{
    //printf("Received bcast\n");
    if(isStarted()==0){
        return;
    }

   uint16_t type;

   //printf("Port sender=%u / Port destination=%u\n",sender_port,receiver_port);

    /*PRINTF("From %u : [0x%08lx], RSSI %d\n",sender.u8[0] + (sender.u8[1] << 8),
        uip_ntohl((unsigned long) *((uint32_t *)(uip_appdata))),
        (int16_t)packetbuf_attr(PACKETBUF_ATTR_RSSI) + RSSI_OFFSET);*/

   type = uip_ntohs(*((uint16_t *)data));

   // printf("TYPE=%u addr=%u\n",type,sender_addr->u8[15] + (sender_addr->u8[14] << 8));

    if(type==G_SIMPLE_SEND || (type==G_RESPONSE && !waiting_resp)){ //periodic sending or multicast response not expected by the current mote but still handled
        geoloc_handler(gdata,&geoloc_cnt,sender_addr->u8[15] + (sender_addr->u8[14] << 8),(int16_t)packetbuf_attr(PACKETBUF_ATTR_RSSI) + RSSI_OFFSET);
        if(geoloc_cnt==0){ //data have been sent, so reset the timer
             etimer_reset(&gtim);
        }
    }else if(type==G_REQUEST_RESPONSE){ //respond to a specific mote which have requested a geolocation (we are using multicast)
        g_resp();
    }else if(type==G_RESPONSE && waiting_resp){ //handle responses sent by neighbors after a geolocation request sent by the current mote
        geoloc_handler(neighbors_resp,&neigh_resp_cnt,sender_addr->u8[15] + (sender_addr->u8[14] << 8),(int16_t)packetbuf_attr(PACKETBUF_ATTR_RSSI) + RSSI_OFFSET);
    }

  return;
}

static void geoloc_handler(struct geoloc_data* data,int* cnt,uint16_t src_id,int16_t rssi){
  data[*cnt].src_id=src_id;
  data[*cnt].rssi=rssi;

  if((++(*cnt)) >= GEOLOC_DATA_LENGTH){
    geoloc_data_send(GEOLOC_DATA_LENGTH,data);
    *cnt=0;
  }
}

static void geoloc_data_send(int16_t nb,struct geoloc_data* data){
  //printf("Sent GEO DATA\n");
  struct geoloc_msg msg;

  msg.node_id = n_node_id;
  msg.nb = nb;
  memcpy(&msg.data,data,nb*sizeof(struct geoloc_data));

  uip_udp_packet_sendto(client_g_conn, &msg, 4 + sizeof(msg.data),&server_ipaddr, UIP_HTONS(UDP_GEOLOC_PORT));

  memset(data,0x0,GEOLOC_DATA_LENGTH*sizeof(struct geoloc_data));
}
