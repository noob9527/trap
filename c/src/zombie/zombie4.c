#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>

/**
 * avoid producing zombie processing by calling fork twice
 */
int main(void) {
    pid_t pid;

    if ((pid = fork()) == 0) {
        /* first child */
        if (fork() > 0) {
            /* parent from second fork == first child */
            exit(0);
        }

        /*
         * We're the second child; our parent becomes init as soon
         * as our real parent calls exit() in the statement above.
         * Here's where we'd continue executing, knowing that when
         * we're done, init will reap our status.
         */
        sleep(2);
        printf("second child, parent pid = %ld\n", (long) getppid());
        exit(0);
    }

    waitpid(pid, NULL, 0);    /* wait for first child */

    /*
     * We're the parent (the original process); we continue executing,
     * knowing that we're not the parent of the second child.
     */
    return 0;
}

