# TODO: make sure the rules for server client and markdown filled!

CC := gcc
CFLAGS := -Wall -Wextra -I.
OBJS= markdown.o

all: server client

%.o: %.c
	$(CC) -c $(FLAGS) $< -o $@

server: $(OBJS) source/server.o
	$(CC) $(FLAGS) -o server $(OBJS) source/server.o

client: $(OBJS) source/client.o
	$(CC) $(FLAGS) -o client $(OBJS) source/client.o

markdown.o: libs/markdown.c libs/markdown.h
	$(CC) -c $(CFLAGS) libs/markdown.c -o markdown.o

clean:
	rm -f $(OBJS) source/client.o source/server.o server client
	rm -f FIFO_C2S_* FIFO_S2C_*
