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

static void sig_chld_handler(int signo) {
    if (signo == SIGCHLD) {
        pid_t pid = wait(NULL);
        if (pid > 0) printf("received SIGCHLD from child process pid = %d\n", pid);
    }
}

/**
 * avoid producing zombie processing by calling one of the wait functions
 */
int main(void) {
    pid_t pid;

    signal(SIGCHLD, sig_chld_handler);

    if ((pid = fork()) > 0) {
        sleep(2); // let child terminates first
        print_process_state(pid);
    } else if (pid == 0) {
        // this process won't become a zombie process
        printf("child process pid = %ld\n", (long) getpid());
    }

    return 0;
}

