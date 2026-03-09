//TODO: client code that can send instructions to server.
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>
#include "../libs/markdown.h"

#define BUFSIZE 256
volatile int signal_received = 0;

void signal_handler(int signo){
    if(signo == SIGRTMIN + 1){
        signal_received = 1;
    }

}

int main(int argc, char* argv[]){
    //server pid
    int spid = atoi(argv[1]);
    char* name = argv[2];

    //register the sighandler
    signal(SIGRTMIN + 1, signal_handler);

    char fifo_c2s[BUFSIZE];
    char fifo_s2c[BUFSIZE];
    snprintf(fifo_c2s, sizeof(fifo_c2s), "FIFO_C2S_%d", getpid());
    snprintf(fifo_s2c, sizeof(fifo_s2c), "FIFO_S2C_%d", getpid());

    unlink(fifo_c2s);
    unlink(fifo_s2c);

    //create the fifo
    if (mkfifo(fifo_c2s, 0666) == -1) {
        perror("mkfifo error");
        return 3;
    }

    if (mkfifo(fifo_s2c, 0666) == -1) {
        perror("mkfifo error");
        unlink(fifo_c2s);
        return 4;
    }
    
    kill(spid, SIGRTMIN);

    //wait for SIGRTMIN + 1 from server
    while(signal_received == 0){
        pause();
    }

    int c2s_fd = open(fifo_c2s, O_WRONLY);
    if (c2s_fd < 0) {
        perror("open for write failed");
        unlink(fifo_c2s);
        unlink(fifo_s2c);
        return 1;
    }

    int s2c_fd = open(fifo_s2c, O_RDONLY);
    if (s2c_fd < 0) {
        perror("open for read failed");
        close(c2s_fd);
        unlink(fifo_c2s);
        unlink(fifo_s2c);
        return 2;
    }

    //send the name to server
    char buf[BUFSIZE];
    snprintf(buf, BUFSIZE, "%s\n", name);
    write(c2s_fd, buf, strlen(buf));

    //check what server sent, if unauthorised error then print error
    char buffer[BUFSIZE];
    ssize_t bytes_read = read(s2c_fd, buffer, BUFSIZE-1);
    buffer[bytes_read] = '\0';

    if(strcmp(buffer, "Reject UNAUTHORISED\n") == 0) {
        printf("Reject UNAUTHORISED\n");
        close(c2s_fd);
        close(s2c_fd);
        unlink(fifo_c2s);
        unlink(fifo_s2c);
    }
    else{
        //print the doc content
        printf("%s", buffer);
    }

    //loop to let client edit
    while(1){
        char input[BUFSIZE];
        fgets(input, BUFSIZE, stdin);
        write(c2s_fd, input, strlen(input));

        //remove trailing newline
        input[strcspn(input, "\r\n")] = '\0';

        if(strcmp(input, "DISCONNECT") == 0){
            break;
        }

        //read response from server
        char res[BUFSIZE];
        ssize_t bytes_read = read(s2c_fd, res, BUFSIZE-1);
        res[bytes_read] = '\0';
        printf("%s\n", res);
    }


    close(c2s_fd);
    close(s2c_fd);
    unlink(fifo_c2s);
    unlink(fifo_s2c);
    return 0;
}