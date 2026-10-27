#define _GNU_SOURCE
#include <stdio.h>
#include <unistd.h>
#include <dlfcn.h>

typedef size_t (*fn_t)(const char *);
static volatile int sink;
static char bufA[] = "PROBE-A";
static char bufB[] = "PROBE-BB";

int main(int argc, char **argv)
{
    const char *nm = (argc > 1) ? argv[1] : "strlen";
    fn_t f = (fn_t)dlsym(RTLD_DEFAULT, nm);
    volatile fn_t vf;
    long i = 0;

    if (!f) { printf("dlsym %s failed\n", nm); return 1; }
    vf = f;
    printf("pid=%d sym=%s addr=%p A=%p B=%p\n", getpid(), nm, (void *)f, (void *)bufA, (void *)bufB);
    fflush(stdout);

    for (;;) {
        const char *p = (i & 1) ? bufA : bufB;
        sink += (int)vf(p);
        if ((++i & 0x1ffff) == 0) usleep(500);
    }
}