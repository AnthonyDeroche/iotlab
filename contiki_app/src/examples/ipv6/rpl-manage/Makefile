CONTIKI = ../../..
CONTIKI_PROJECT = udp-sender udp-sink
PROJECT_SOURCEFILES += data.c geolocation.c multicast.c sink_command.c sender_command.c

UIP_CONF_IPV6=1

CFLAGS+= -DUIP_CONF_IPV6_RPL

ifdef PERIOD
CFLAGS=-DPERIOD=$(PERIOD)
endif

all: $(CONTIKI_PROJECT)

include $(CONTIKI)/Makefile.include

