#include "multicast.h"
#include "udp-sink.h"
#include "sink_command.h"
#include "dev/cc2420/cc2420.h"

static uip_ipaddr_t maddr;
static uint16_t ccvn=0; //current command version number
static uint8_t mid;
/*---------------------------------------------------------------------------*/
void command_sink_init(){

	uip_ip6addr(&maddr, 0xFF1E,0,0,0,0,0,0x89,0xABCD);
	mid=multicast_init(&maddr,MCAST_UDP_PORT);

    //SERIAL LINE INIT
	#if CONTIKI_TARGET_Z1
      uart0_set_input(serial_line_input_byte);
    #else
      uart1_set_input(serial_line_input_byte);
    #endif
      serial_line_init();
}

/*---------------------------------------------------------------------------*/
void
command_sink_check(struct process *p, process_event_t ev, void *data){

  if(ev == serial_line_event_message) {
	char *line, *line_backup;
	line = (char *)data;
	line_backup=strdup(line); //strtok function modifies the input string

	//printf("Received line : %s\n",line);

	char *command;
	command = strtok(line," ");
	uint16_t cvn = atoi(command); //recovery of the command version number
	if(cvn==0){
		printf("Command must have a version number\n");
		return;
	}
	//printf("ccvn : %d, cvn : %d\n",ccvn, cvn);

	if(ccvn < cvn){

		ccvn=cvn; //we save the new cvn

		command = strtok(NULL," ");

		if(strncmp(command, "START", 5) == 0) {
			printf("Received command : %s\n",command);
			multicast_send(mid,line_backup,strlen(line_backup));
		}
		else if(strncmp(command, "STOP", 4) == 0) {
			printf("Received command : %s\n",command);
			multicast_send(mid,line_backup,strlen(line_backup));
		}
		else if(strncmp(command, "DATA_PERIOD", 11) == 0) {
			printf("Received command : %s\n",command);
			multicast_send(mid,line_backup,strlen(line_backup));
		}
		else if(strncmp(command, "SINK_ID_DVN", 11) == 0) {
			print_sink_id_dvn();
		}
		else if(strncmp(command, "GLOBAL_REPAIR", 13) == 0) {
			printf("Initiating global repair\n");
			rpl_repair_root(RPL_DEFAULT_INSTANCE);
		}
		else if(strncmp(command, "TXPOWER", 7) == 0) {
			printf("Received command : %s\n",command);
			command = strtok(NULL," ");
			if(command==NULL){
				printf("The value of the txpower is missing\n");
				return;
			}
			uint8_t power = atoi(command); //recovery of the txpower
			cc2420_set_txpower(power);
			multicast_send(mid,line_backup,strlen(line_backup));
		}
		else if(strncmp(command, "RESET_CVN", 9) == 0) {
			printf("Reset command version number\n");
			ccvn=0; //we reset the command version number
			multicast_send(mid,line_backup,strlen(line_backup)); //and forward the command to the senders
		}
		else {
			printf("Unhandled command: %s\n", command);
		     }
	}
	else {
		printf("Obsolete command version number\n");
	}

	free(line_backup);
  }
}
/*---------------------------------------------------------------------------*/
