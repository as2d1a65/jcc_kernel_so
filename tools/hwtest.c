#define _GNU_SOURCE
#include <stdio.h>
#include <unistd.h>
#include <dlfcn.h>

typedef size_t (*fn_t)(const char *);
static volatile int sink;
static const char probe[] = "JCC-CAPTURE-X0-PROBE";

int main(int argc, char **argv)
{
    const char *nm = (argc > 1) ? argv[1] : "strlen";
    fn_t f = (fn_t)dlsym(RTLD_DEFAULT, nm);
    volatile fn_t vf;
    long i = 0;

    if (!f) { printf("dlsym %s failed\n", nm); return 1; }
    vf = f;
    printf("pid=%d sym=%s addr=%p probe=%p (%s)\n", getpid(), nm, (void *)f, (void *)probe, probe);
    fflush(stdout);

    for (;;) {
        sink += (int)vf(probe);
        if ((++i & 0x3ffff) == 0) usleep(500);
    }
}