#include "data.h"


#ifndef PERIOD
#define PERIOD 30
#endif
#define RANDWAIT (PERIOD)


static uint16_t data_period = PERIOD;
static uint16_t randwait = PERIOD;

static unsigned long time_offset;
static struct uip_udp_conn *client_conn;
static struct etimer period_timer, wait_timer;
static uip_ipaddr_t server_ipaddr;

static struct data_msg {
  uint16_t len;
  uint16_t clock;
  uint16_t timesynch_time;
  uint16_t cpu;
  uint16_t lpm;
  uint16_t transmit;
  uint16_t listen;
  uint16_t parent;
  uint16_t parent_etx;
  uint16_t current_rtmetric;
  uint16_t num_neighbors;
  uint16_t beacon_interval;

  uint16_t sensors[10];
};

static void data_construct_message(struct data_msg *msg,
                                    const linkaddr_t *parent,
                                    uint16_t etx_to_parent,
                                    uint16_t current_rtmetric,
                                    uint16_t num_neighbors,
                                    uint16_t beacon_interval);

static void data_send(void);

static void data_arch_read_sensors(struct data_msg *msg);
enum {
  BATTERY_VOLTAGE_SENSOR,
  BATTERY_INDICATOR,
  LIGHT1_SENSOR,
  LIGHT2_SENSOR,
  TEMP_SENSOR,
  HUMIDITY_SENSOR,
  RSSI_SENSOR,
  ETX1_SENSOR,
  ETX2_SENSOR,
  ETX3_SENSOR,
  ETX4_SENSOR,
};


/*---------------------------------------------------------------------------*/
static int8_t started=1;
static int8_t isStarted(){
    return started;
}

void data_start(){
    started=1;
}

void data_stop(){
    started=0;
}

/*---------------------------------------------------------------------------*/
void set_data_period(uint16_t period)
{
  data_period=period;
  randwait=period;
  etimer_set(&period_timer, CLOCK_SECOND * data_period);
  etimer_set(&wait_timer, CLOCK_SECOND * randwait);
}
/*---------------------------------------------------------------------------*/
void data_init(uip_ipaddr_t serv_ipaddr)
{
  server_ipaddr=serv_ipaddr;

  /* Send a packet every 60-62 seconds. */
  etimer_set(&period_timer, CLOCK_SECOND * data_period);


  /************** DATA UDP FLOW ****************/
  client_conn = udp_new(NULL, UIP_HTONS(UDP_SERVER_PORT), NULL);
  udp_bind(client_conn, UIP_HTONS(UDP_CLIENT_PORT));

  PRINTF("Created a connection with the server ");
  PRINT6ADDR(&client_conn->ripaddr);
  PRINTF(" local/remote port %u/%u\n",
  UIP_HTONS(client_conn->lport), UIP_HTONS(client_conn->rport));
}

void data_check(struct process *p, process_event_t ev, void *data){

     if(ev == PROCESS_EVENT_TIMER) {
      if(data == &period_timer) {
        etimer_reset(&period_timer);
        etimer_set(&wait_timer, random_rand() % (CLOCK_SECOND * randwait));
      } else if(data == &wait_timer) {
          // Time to send the data
          if(isStarted()==1){
            data_send();
          }
      }
    }
}


/*---------------------------------------------------------------------------*/
static unsigned long get_time(void)
{
  return clock_seconds() + time_offset;
}

/*---------------------------------------------------------------------------*/
void data_recv(const linkaddr_t *originator, uint8_t seqno, uint8_t hops,
                    uint8_t *payload, uint16_t payload_len)
{
  unsigned long time;
  uint16_t data;
  int i;

  printf("%u", 8 + payload_len / 2);
  /* Timestamp. Ignore time synch for now. */
  time = get_time();
  printf(" %lu %lu 0", ((time >> 16) & 0xffff), time & 0xffff);
  /* Ignore latency for now */
  printf(" %u %u %u %u",
         originator->u8[0] + (originator->u8[1] << 8), seqno, hops, 0);
  for(i = 0; i < payload_len / 2; i++) {
    memcpy(&data, payload, sizeof(data));
    payload += sizeof(data);
    printf(" %u", data);
  }
  printf("\n");
  leds_blink();
}

/*---------------------------------------------------------------------------*/
static void data_send(void)
{
  static uint8_t seqno;
  struct {
    uint8_t seqno;
    uint8_t for_alignment;
    struct data_msg msg;
  } msg;
  /* struct collect_neighbor *n; */
  uint16_t parent_etx;
  uint16_t rtmetric;
  uint16_t num_neighbors;
  uint16_t beacon_interval;
  rpl_parent_t *preferred_parent;
  linkaddr_t parent;
  rpl_dag_t *dag;

  if(client_conn == NULL) {
    /* Not setup yet */
    return;
  }
  memset(&msg, 0, sizeof(msg));
  seqno++;
  if(seqno == 0) {
    /* Wrap to 128 to identify restarts */
    seqno = 128;
  }
  msg.seqno = seqno;

  linkaddr_copy(&parent, &linkaddr_null);
  parent_etx = 0;

  /* Let's suppose we have only one instance */
  dag = rpl_get_any_dag();
  if(dag != NULL) {
    preferred_parent = dag->preferred_parent;
    if(preferred_parent != NULL) {
      uip_ds6_nbr_t *nbr;
      nbr = uip_ds6_nbr_lookup(rpl_get_parent_ipaddr(preferred_parent));
      if(nbr != NULL) {
        /* Use parts of the IPv6 address as the parent address, in reversed byte order. */
        parent.u8[LINKADDR_SIZE - 1] = nbr->ipaddr.u8[sizeof(uip_ipaddr_t) - 2];
        parent.u8[LINKADDR_SIZE - 2] = nbr->ipaddr.u8[sizeof(uip_ipaddr_t) - 1];
        parent_etx = rpl_get_parent_rank((linkaddr_t *) uip_ds6_nbr_get_ll(nbr)) / 2;
      }
    }
    rtmetric = dag->rank;
    beacon_interval = (uint16_t) ((2L << dag->instance->dio_intcurrent) / 1000);
    num_neighbors = uip_ds6_nbr_num();

     //printf("num_neighors=%d\n",num_neighbors);

  } else {
    rtmetric = 0;
    beacon_interval = 0;
    num_neighbors = 0;
  }

  data_construct_message(&msg.msg, &parent,
                                 parent_etx, rtmetric,
                                 num_neighbors, beacon_interval);

  uip_udp_packet_sendto(client_conn, &msg, sizeof(msg),
                        &server_ipaddr, UIP_HTONS(UDP_SERVER_PORT));
  //printf("Sent data message\n");
}
/*---------------------------------------------------------------------------*/
static void data_construct_message(struct data_msg *msg,
                               const linkaddr_t *parent,
                               uint16_t parent_etx,
                               uint16_t current_rtmetric,
                               uint16_t num_neighbors,
                               uint16_t beacon_interval)
{
  static unsigned long last_cpu, last_lpm, last_transmit, last_listen;
  unsigned long cpu, lpm, transmit, listen;


  msg->len = sizeof(struct data_msg) / sizeof(uint16_t);
  msg->clock = clock_time();
#if TIMESYNCH_CONF_ENABLED
  msg->timesynch_time = timesynch_time();
#else /* TIMESYNCH_CONF_ENABLED */
  msg->timesynch_time = 0;
#endif /* TIMESYNCH_CONF_ENABLED */

  energest_flush();

  cpu = energest_type_time(ENERGEST_TYPE_CPU) - last_cpu;
  lpm = energest_type_time(ENERGEST_TYPE_LPM) - last_lpm;
  transmit = energest_type_time(ENERGEST_TYPE_TRANSMIT) - last_transmit;
  listen = energest_type_time(ENERGEST_TYPE_LISTEN) - last_listen;

  /* Make sure that the values are within 16 bits. If they are larger,
     we scale them down to fit into 16 bits. */
  while(cpu >= 65536ul || lpm >= 65536ul ||
	transmit >= 65536ul || listen >= 65536ul) {
    cpu /= 2;
    lpm /= 2;
    transmit /= 2;
    listen /= 2;
  }

  msg->cpu = cpu;
  msg->lpm = lpm;
  msg->transmit = transmit;
  msg->listen = listen;

  last_cpu = energest_type_time(ENERGEST_TYPE_CPU);
  last_lpm = energest_type_time(ENERGEST_TYPE_LPM);
  last_transmit = energest_type_time(ENERGEST_TYPE_TRANSMIT);
  last_listen = energest_type_time(ENERGEST_TYPE_LISTEN);

  memcpy(&msg->parent, &parent->u8[LINKADDR_SIZE - 2], 2);
  msg->parent_etx = parent_etx;
  msg->current_rtmetric = current_rtmetric;
  msg->num_neighbors = num_neighbors;
  msg->beacon_interval = beacon_interval;

  memset(msg->sensors, 0, sizeof(msg->sensors));
  data_arch_read_sensors(msg);
}


#if CONTIKI_TARGET_SKY
static void data_arch_read_sensors(struct data_msg *msg)
{

  SENSORS_ACTIVATE(light_sensor);
  SENSORS_ACTIVATE(battery_sensor);
  SENSORS_ACTIVATE(sht11_sensor);

  msg->sensors[BATTERY_VOLTAGE_SENSOR] = battery_sensor.value(0);
  msg->sensors[BATTERY_INDICATOR] = sht11_sensor.value(SHT11_SENSOR_BATTERY_INDICATOR);
  msg->sensors[LIGHT1_SENSOR] = light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC);
  msg->sensors[LIGHT2_SENSOR] = light_sensor.value(LIGHT_SENSOR_TOTAL_SOLAR);
  msg->sensors[TEMP_SENSOR] = sht11_sensor.value(SHT11_SENSOR_TEMP);
  msg->sensors[HUMIDITY_SENSOR] = sht11_sensor.value(SHT11_SENSOR_HUMIDITY);


  SENSORS_DEACTIVATE(light_sensor);
  SENSORS_DEACTIVATE(battery_sensor);
  SENSORS_DEACTIVATE(sht11_sensor);
}
#else
/*---------------------------------------------------------------------------*/
static uint16_t get_temp()
{
  /* XXX Fix me: check /examples/z1/test-tmp102.c for correct conversion */
  return (uint16_t)tmp102_read_temp_raw();
}
/*---------------------------------------------------------------------------*/
static void data_arch_read_sensors(struct collect_view_data_msg *msg)
{
  static int initialized = 0;

  if(!initialized) {
    tmp102_init();
    initialized = 1;
  }

  msg->sensors[BATTERY_VOLTAGE_SENSOR] = 0;
  msg->sensors[BATTERY_INDICATOR] = 0;
  msg->sensors[LIGHT1_SENSOR] = 0;
  msg->sensors[LIGHT2_SENSOR] = 0;
  msg->sensors[TEMP_SENSOR] = get_temp();
  msg->sensors[HUMIDITY_SENSOR] = 0;
}
/*---------------------------------------------------------------------------*/
#endif
