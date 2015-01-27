#include "multicast.h"
#include "udp-sender.h"
#include "sender_command.h"
#include "dev/cc2420/cc2420.h"
#include "data.h"

#define MAX_REPEAT 3  // the number of times the message is repeated by a sender

static uip_ipaddr_t maddr;


static int8_t start_counter=0;
static int8_t stop_counter=0;
static int8_t data_period_counter=0;
static int8_t reset_dvn_counter=0;
static int8_t txpower_counter=0;
static uint16_t ccvn=0; //current command version number
static uint8_t mid;
/*---------------------------------------------------------------------------*/
void
command_sender_init(){
	uip_ip6addr(&maddr, 0xFF1E,0,0,0,0,0,0x89,0xABCD);
	mid = multicast_init(&maddr,MCAST_UDP_PORT);
	join_mcast_group(mid);

	 //printf("port : %u\n",uip_ntohs(sender_mcast_conn->lport));

}
/*---------------------------------------------------------------------------*/
void
sender_command_check(struct process *p, process_event_t ev, void *data){

  if(ev == tcpip_event){
	if(uip_newdata() &&  UIP_HTONS(UIP_IP_BUF->destport)==MCAST_UDP_PORT){
		char *appdata, *appdata_backup;
        	appdata = (char *)uip_appdata;
		appdata_backup = strdup(appdata); //strtok function modifies the input string

		char *command;
		command = strtok(appdata," ");
		uint16_t cvn = atoi(command); //recovery of the command version number
		//printf("ccvn : %d, cvn : %d\n",ccvn, cvn);

		if(ccvn <= cvn){

			uint8_t forward=0;

			if(ccvn < cvn){
				//If it is a new cvn, we reset all the counters
				start_counter=0;
				stop_counter=0;
				data_period_counter=0;
				if(cvn<65535){
                    			reset_dvn_counter=0;
				}
				txpower_counter=0;
				ccvn=cvn; //we save the new cvn
				}

			command = strtok(NULL," ");

		 	if(strncmp("START",command,5)==0){
				char *module_name;
				module_name = strtok(NULL," ");
				if(module_name==NULL){
					//printf("The module name is missing\n");
					return;
				}
		   		sender_start_modules(module_name);
				start_counter++;
				//printf("start_counter : %d\n", start_counter);
				if(start_counter<MAX_REPEAT){
					forward=1;
				}
		 	}
			else if(strncmp("STOP",command,4)==0){
				char *module_name;
				module_name = strtok(NULL," ");
				if(module_name==NULL){
					//printf("The module name is missing\n");
					return;
				}
		    		sender_stop_modules(module_name);
				stop_counter++;
				//printf("stop_counter : %d\n", stop_counter);
				if(stop_counter<MAX_REPEAT){
					forward=1;
				}
			}
			else if(strncmp("DATA_PERIOD",command,11)==0){
				command = strtok(NULL," ");
				if(command==NULL){
					//printf("The value of the data period is missing\n");
					return;
				}
				uint16_t period = atoi(command); //recovery of the data period
				//printf("period : %d\n",period);
                		set_data_period(period); // we change the data period
				data_period_counter++;
				//printf("data_period_counter : %d\n", data_period_counter);
				if(data_period_counter<MAX_REPEAT){
					forward=1;
				}
			}
			else if(strncmp(command, "TXPOWER", 7) == 0) {
				command = strtok(NULL," ");
				if(command==NULL){
					//printf("The value of the txpower is missing\n");
					return;
				}
				uint8_t power = atoi(command); //recovery of the txpower
				//printf("power : %d\n",power);
				cc2420_set_txpower(power);
				txpower_counter++;
				//printf("txpower_counter : %d\n", txpower_counter);
				if(txpower_counter<MAX_REPEAT){
					forward=1;
				}
			}
			else if(strncmp("RESET_CVN",command,9)==0){
				ccvn=0; //we reset the command version number
				reset_dvn_counter++;
				//printf("reset cnt = %d\n",reset_dvn_counter);
				if(reset_dvn_counter<MAX_REPEAT){
					forward=1;
				}
			}

			if(forward){
				multicast_send(mid,appdata_backup,strlen(appdata_backup)); //sender forwards the command
			}
		
		}
		else {
			//printf("Obsolete command number\n");
		}

		free(appdata_backup);
	}
  }
}
/*---------------------------------------------------------------------------*/
void sender_command_start(){
}
/*---------------------------------------------------------------------------*/
void sender_command_stop(){
}
/*---------------------------------------------------------------------------*/
