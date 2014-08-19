#define MCAST_UDP_PORT 3003

void command_sender_init();
void sender_command_check(struct process *p, process_event_t ev, void *data);
void sender_command_start();
void sender_command_stop();
