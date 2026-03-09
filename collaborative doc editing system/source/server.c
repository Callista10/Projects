// TODO: server code that manages the document and handles client instructions
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

#define MAX 100
#define BUFSIZE 100
document *doc = NULL;
//keep track of number of clients online
int clients[MAX];
int count = 0;
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;

void broadcast(const char* msg, size_t len){
    pthread_mutex_lock(&client_mutex);

    //go through the clients list
    for(int i = 0; i < count; i++){
        //get the fifo name for each client then open it
        char fifo_s2c[BUFSIZE];
        snprintf(fifo_s2c, sizeof(fifo_s2c), "FIFO_S2C_%d", clients[i]);

        int fd = open(fifo_s2c, O_WRONLY);
        write(fd, msg, len);
        close(fd);

    }
    pthread_mutex_unlock(&client_mutex);
}  

void* client_handler(void* args){
    int cpid = *(int*) args;
    free(args);

    //add the number of client, use mutex to lock (so no race condition)
    pthread_mutex_lock(&client_mutex);
    clients[count++] = cpid;
    pthread_mutex_unlock(&client_mutex);

    //initialise the fifo
    char fifo_c2s[BUFSIZE];
    char fifo_s2c[BUFSIZE];
    snprintf(fifo_c2s, sizeof(fifo_c2s), "FIFO_C2S_%d", cpid);
    snprintf(fifo_s2c, sizeof(fifo_s2c), "FIFO_S2C_%d", cpid);

    //clean fifo if they already exist
    unlink(fifo_c2s);
    unlink(fifo_s2c);

    //create the fifo
    if (mkfifo(fifo_c2s, 0666) == -1 || mkfifo(fifo_s2c, 0666) == -1) {
        perror("mkfifo error");
        
        //decrement the client number whenever exit thread
        pthread_mutex_lock(&client_mutex);
        count--;
        pthread_mutex_unlock(&client_mutex);

        pthread_exit(NULL);

    }
    kill(cpid, SIGRTMIN + 1);

    //open the fifo
    int c2s_fd = open(fifo_c2s, O_RDONLY);
    if (c2s_fd < 0) {
        perror("open for read failed");
        unlink(fifo_c2s);
        unlink(fifo_s2c);

        //decrement the client number whenever exit thread
        pthread_mutex_lock(&client_mutex);
        count--;
        pthread_mutex_unlock(&client_mutex);

        pthread_exit(NULL);;
    }

    int s2c_fd = open(fifo_s2c, O_WRONLY);
    if (s2c_fd < 0) {
        perror("open for write failed");
        close(c2s_fd);
        unlink(fifo_c2s);
        unlink(fifo_s2c);

        //decrement the client number whenever exit thread
        pthread_mutex_lock(&client_mutex);
        count--;
        pthread_mutex_unlock(&client_mutex);

        pthread_exit(NULL);;
    }

    //get the username
    char buffer[BUFSIZE];
    ssize_t bytes_read = read(c2s_fd, buffer, BUFSIZE-1);

    buffer[bytes_read] = '\0';
    //remove trailing newline
    buffer[strcspn(buffer, "\r\n")] = '\0';

    //check roles.txt for the username
    FILE* file = fopen("roles.txt", "r");
    char name[BUFSIZE];
    char role[BUFSIZE];
    int found = 0;

    while(fscanf(file, "%s %s", name, role) == 2){
        //check if the name entered matches a name in role.txt
        if(strcmp(name, buffer) == 0){
            found = 1;
            break;
        }

    }

    //if found then write the role of the client
    if(found == 1){
        printf("Client %s registered.\n", name);
        write(s2c_fd, role, strlen(role));
        //add newline
        write(s2c_fd, "\n", 1);
    } 
    else{
        //if not found then put error message
        char* error_msg = "Reject UNAUTHORISED\n";
        write(s2c_fd, error_msg, strlen(error_msg));

        sleep(1);

        fclose(file);
        close(c2s_fd);
        close(s2c_fd);
        unlink(fifo_c2s);
        unlink(fifo_s2c);

        //decrement the client number whenever exit thread
        pthread_mutex_lock(&client_mutex);
        count--;
        pthread_mutex_unlock(&client_mutex);

        pthread_exit(NULL);
    }

    //now that the client is checked, print the doc & check roles read/write
    char* flatten = markdown_flatten(doc);
    size_t len = strlen(flatten);
    uint64_t ver = doc->version;

    //put the len and ver to s2c_fd
    char vbuf[BUFSIZE]; //version buffer
    char lbuf[BUFSIZE]; //len buffer
    snprintf(vbuf, BUFSIZE, "%lu\n", ver);
    snprintf(lbuf, BUFSIZE, "%lu\n", len);
    write(s2c_fd, vbuf, strlen(vbuf));
    write(s2c_fd, lbuf, strlen(lbuf));

    //write the flatten content to fd
    write(s2c_fd, flatten, len);

    free(flatten);

    //if role is read, then just exit
    if(strcmp(role, "read") ==  0){
        fclose(file);
        close(c2s_fd);
        close(s2c_fd);
        unlink(fifo_c2s);
        unlink(fifo_s2c);

        //decrement the client number whenever exit thread
        pthread_mutex_lock(&client_mutex);
        count--;
        pthread_mutex_unlock(&client_mutex);

        pthread_exit(NULL);
    }

    //this is for write role
    while(1){
        //loop to let client put in the commands (insert, delete, etc)
        char input[BUFSIZE];
        ssize_t bytes_read = read(c2s_fd, input, BUFSIZE-1);

        input[bytes_read] = '\0';
        //remove trailing newline
        input[strcspn(input, "\r\n")] = '\0';

        //parse the client input
        char command[64];
        size_t pos;
        size_t n; //this is len
        int lvl;
        size_t start;
        size_t end;
        char content[BUFSIZE];
        char linkk[BUFSIZE];

        if(sscanf(input, "%s", command) ==  1){
            if(strcmp(command, "INSERT") == 0){
                if(sscanf(input, "%*s %zu %[^\n]", &pos, content) >= 2){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_insert(doc, doc->version, pos, content);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "DEL") == 0){
                if(sscanf(input, "%*s %zu %zu", &pos, &n) == 2){
                    if(pos < 0 || n < 1){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_delete(doc, doc->version, pos, n);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "NEWLINE") == 0){
                if(sscanf(input, "%*s %zu", &pos) == 1){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_newline(doc, doc->version, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "HEADING") == 0){
                if(sscanf(input, "%*s %d %zu", &lvl, &pos) == 2){
                    if(pos < 0 || lvl > 3 || lvl < 1){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_heading(doc, doc->version, lvl, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "BOLD") == 0){
                if(sscanf(input, "%*s %zu %zu", &start, &end) == 2){
                    if(start < 0 || end < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_bold(doc, doc->version, start, end);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "ITALIC") == 0){
                if(sscanf(input, "%*s %zu %zu", &start, &end) == 2){
                    if(start < 0 || end < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_italic(doc, doc->version, start, end);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "BLOCKQUOTE") == 0){
                if(sscanf(input, "%*s %zu", &pos) == 1){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_blockquote(doc, doc->version, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "ORDERED_LIST") == 0){
                if(sscanf(input, "%*s %zu", &pos) == 1){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_ordered_list(doc, doc->version, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "UNORDERED_LIST") == 0){
                if(sscanf(input, "%*s %zu", &pos) == 1){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_unordered_list(doc, doc->version, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "CODE") == 0){
                if(sscanf(input, "%*s %zu %zu", &start, &end) == 2){
                    if(start < 0 || end < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_code(doc, doc->version, start, end);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "HORIZONTAL_RULE") == 0){
                if(sscanf(input, "%*s %zu", &pos) == 1){
                    if(pos < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_horizontal_rule(doc, doc->version, pos);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "LINK") == 0){
                if(sscanf(input, "%*s %zu %zu %[^\n]", &start, &end, linkk) == 3){
                    if(start < 0 || end < 0){
                        char* msg = "Reject INVALID_POSITION\n";
                        broadcast(msg, strlen(msg));
                        continue;
                    }
                    markdown_link(doc, doc->version, start, end, linkk);
                    markdown_increment_version(doc);
                    char* msg = "SUCCESS\n";
                    broadcast(msg, strlen(msg));
                }
            }
            else if(strcmp(command, "DOC?") == 0){
                char* output = markdown_flatten(doc);
                printf("%s\n", output);
                write(s2c_fd, output, strlen(output));
                write(s2c_fd, "\n", 1);
                free(output);
            }
            else if(strcmp(command, "PERM?") == 0){
                printf("%s\n", role);
                write(s2c_fd, role, strlen(role));
                write(s2c_fd, "\n", 1);
            }
            else if(strcmp(command, "DISCONNECT") == 0){
                break;
            }
            else{
                char* msg = "Reject INVALID COMMAND\n";
                broadcast(msg, strlen(msg));
            }

        }
    }

    fclose(file);
    close(c2s_fd);
    close(s2c_fd);
    unlink(fifo_c2s);
    unlink(fifo_s2c);

    //decrement the client number whenever exit thread
    pthread_mutex_lock(&client_mutex);
    count--;
    pthread_mutex_unlock(&client_mutex);

    pthread_exit(NULL);
}

void signal_handler(int signo, siginfo_t* sinfo, void* context){
    //get child pid
    int *cpid = malloc(sizeof(int));
    *cpid = sinfo->si_pid;

    //create thread
    pthread_t client_thread;
    pthread_create(&client_thread, NULL, client_handler, cpid);
    

}

void* quit_handler(void* args){
    //checks the input, if QUIT or not
    char input[BUFSIZE];
    while(fgets(input, BUFSIZE, stdin) != NULL){
        //remove trailing newline
        input[strcspn(input, "\r\n")] = '\0';
        if(strcmp(input, "QUIT") == 0){
            //if QUIT, then lock and check how many active clients
            pthread_mutex_lock(&client_mutex);
            if (count > 0) {
                printf("QUIT rejected, %d clients still connected.\n", count);
                pthread_mutex_unlock(&client_mutex);
                continue;
            }
            pthread_mutex_unlock(&client_mutex);

            //if it is 0, save content to doc.md
            FILE* file = fopen("doc.md", "w");
            char* content = markdown_flatten(doc);
            fwrite(content, sizeof(char), strlen(content), file);
            fclose(file);
            free(content);
            markdown_free(doc);
            exit(0);
        }
    }

}

int main(int argc, char* argv[]){
    //get the time interval
    int interval = atoi(argv[1]);

    printf("Server PID: %d\n", getpid());

     //check if doc is null, if yes then initialise it
    if (doc == NULL){
        doc = markdown_init();
    }

    struct sigaction sa;
    sa.sa_flags = SA_SIGINFO;
    sa.sa_sigaction = signal_handler;
    sigaction(SIGRTMIN, &sa, NULL);

    //make a thread to listen for a QUIT
    pthread_t quit_thread;
    pthread_create(&quit_thread, NULL, quit_handler, NULL);

    while(1){
        pause();
    }


    return 0;
}