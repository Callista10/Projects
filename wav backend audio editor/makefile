#TODO
CC=gcc
FLAGS=-Wall -Werror -I.
OBJS=sound_seg.o tests/test.o
TARGET=test

all: $(TARGET)

sound_seg.o: sound_seg.c sound_seg.h
	$(CC) -c $(FLAGS) -fPIC sound_seg.c -o sound_seg.o

%.o: %.c
	$(CC) -c $(FLAGS) $< -o $@


$(TARGET): $(OBJS)
	$(CC) $(FLAGS) -o $(TARGET) $(OBJS) -lm

tests: $(TARGET)
	echo "Running tests!"
	chmod u+x tests/run_all_tests.sh
	./tests/run_all_tests.sh

.PHONY:
build: clean all
	echo "Running build!"

.PHONY:
clean:
	rm -f $(OBJS) $(TARGET)
