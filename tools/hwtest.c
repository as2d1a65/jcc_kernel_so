#define _GNU_SOURCE
#include <stdio.h>
#include <unistd.h>
#include <dlfcn.h>
#include <string.h>

typedef size_t (*fn_t)(const char *);
static volatile int sink;
static char bufA[] = "PROBE-A";
static char bufB[] = "PROBE-BB";

__attribute__((noinline)) static long eight(long a, long b, long c, long d,
                                            long e, long f, long g, long h)
{
    return a + b + c + d + e + f + g + h;
}

typedef long (*eight_t)(long, long, long, long, long, long, long, long);

int main(int argc, char **argv)
{
    const char *mode = (argc > 1) ? argv[1] : "strlen";
    long i = 0;

    if (!strcmp(mode, "argtest")) {
        volatile eight_t vf = eight;
        printf("pid=%d target=eight addr=%p\n", getpid(), (void *)eight);
        fflush(stdout);
        for (;;) {
            sink += (int)vf(0x1111111111111111L, 0x2222222222222222L,
                            0x3333333333333333L, 0x4444444444444444L,
                            0x5555555555555555L, 0x6666666666666666L,
                            0x7777777777777777L, 0x8888888888888888L);
            if ((++i & 0x1ffff) == 0) usleep(500);
        }
    }

    {
        fn_t f = (fn_t)dlsym(RTLD_DEFAULT, mode);
        volatile fn_t vf;
        if (!f) { printf("dlsym %s failed\n", mode); return 1; }
        vf = f;
        printf("pid=%d sym=%s addr=%p A=%p B=%p\n", getpid(), mode, (void *)f, (void *)bufA, (void *)bufB);
        fflush(stdout);
        for (;;) {
            const char *p = (i & 1) ? bufA : bufB;
            sink += (int)vf(p);
            if ((++i & 0x1ffff) == 0) usleep(500);
        }
    }
}