#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <signal.h>
#include <stdio.h>

int print_process_state(pid_t pid) {
    char cmdstring[80];
    char *end = cmdstring;
    end += sprintf(end, "%s", "cat /proc/");
    end += sprintf(end, "%ld", (long) pid);
    sprintf(end, "%s", "/status | grep State");

    puts(cmdstring);
    system(cmdstring);
}

/**
 * avoid producing zombie processing by ignoring SIGCHLD signal explicitly
 */
int main(void) {
    pid_t pid;

    signal(SIGCHLD, SIG_IGN);

    if ((pid = fork()) > 0) {
        sleep(1); // let child terminates first
        print_process_state(pid);
    } else if (pid == 0) {
        // this process won't become a zombie process
        printf("child process pid = %ld\n", (long) getpid());
    }
   
    return 0;
}

