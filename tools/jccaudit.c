/* ????????????????????????"????" */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>

static int interesting_map(const char *line)
{
    const char *p = strchr(line, ' ');
    if (strstr(line, "memfd")) return 1;
    if (strstr(line, "(deleted)")) return 1;
    if (strstr(line, "/dev/")) return 1;
    if (strstr(line, "jcc") || strstr(line, "libdemo")) return 1;
    if (strstr(line, "xf_game") || strstr(line, "ipcb")) return 1;
    /* ?????????? x ?????? */
    if (p) {
        const char *perm = p + 1;
        if (perm[0] && perm[1] && perm[2] == 'x') {
            const char *path = strchr(perm, '/');
            if (!path) return 1;
        }
    }
    return 0;
}

static void section(const char *title)
{
    printf("\n== %s ==\n", title);
}

int main(int argc, char **argv)
{
    int pid, i;
    char path[128], line[1024];
    FILE *f;

    if (argc < 2) { printf("usage: jccaudit <pid>\n"); return 1; }
    pid = atoi(argv[1]);
    if (pid <= 0) { printf("bad pid\n"); return 1; }

    section("TracerPid / State");
    snprintf(path, sizeof(path), "/proc/%d/status", pid);
    f = fopen(path, "r");
    if (!f) { printf("cannot open %s\n", path); return 2; }
    while (fgets(line, sizeof(line), f)) {
        if (!strncmp(line, "TracerPid:", 10) || !strncmp(line, "Name:", 5) ||
            !strncmp(line, "State:", 6) || !strncmp(line, "Threads:", 8) ||
            !strncmp(line, "Seccomp:", 8))
            printf("  %s", line);
    }
    fclose(f);

    section("?????memfd / deleted / dev / anon-exec / ????");
    snprintf(path, sizeof(path), "/proc/%d/maps", pid);
    f = fopen(path, "r");
    if (f) {
        while (fgets(line, sizeof(line), f))
            if (interesting_map(line)) printf("  %s", line);
        fclose(f);
    }

    section("???????????????");
    snprintf(path, sizeof(path), "/proc/%d/task", pid);
    {
        FILE *p = popen("ls /proc/x", "r");   /* ????? */
        if (p) pclose(p);
    }
    {
        char cmd[128];
        snprintf(cmd, sizeof(cmd), "ls /proc/%d/task 2>/dev/null", pid);
        f = popen(cmd, "r");
        if (f) {
            while (fgets(line, sizeof(line), f)) {
                int tid = atoi(line);
                char cpath[128], comm[64];
                FILE *g;
                if (tid <= 0) continue;
                snprintf(cpath, sizeof(cpath), "/proc/%d/task/%d/comm", pid, tid);
                g = fopen(cpath, "r");
                if (!g) continue;
                if (fgets(comm, sizeof(comm), g)) {
                    comm[strcspn(comm, "\n")] = 0;
                    /* ??????/??????????????? */
                    if (strstr(comm, "pool") || strstr(comm, "jcc") ||
                        strstr(comm, "xf") || strchr(comm, '-'))
                        printf("  tid=%-7d comm=%s\n", tid, comm);
                }
                fclose(g);
            }
            pclose(f);
        }
    }

    section("?????? unix socket");
    {
        /* ?? fd ??? socket inode */
        char cmd[128];
        unsigned long inodes[256];
        int ni = 0, k;
        snprintf(cmd, sizeof(cmd), "ls -l /proc/%d/fd 2>/dev/null", pid);
        f = popen(cmd, "r");
        if (f) {
            while (fgets(line, sizeof(line), f)) {
                char *s = strstr(line, "socket:[");
                if (s) {
                    unsigned long ino = strtoul(s + 8, NULL, 10);
                    if (ni < 256) inodes[ni++] = ino;
                }
            }
            pclose(f);
        }
        printf("  socket fd ? = %d\n", ni);
        f = fopen("/proc/net/unix", "r");
        if (f) {
            while (fgets(line, sizeof(line), f)) {
                unsigned long ino = 0;
                char *s = strrchr(line, ' ');
                if (!s) continue;
                /* ??? inode */
                char *tok = strtok(line, " ");
                int col = 0;
                char *pathstr = NULL;
                ino = 0;
                while (tok) {
                    if (col == 6) ino = strtoul(tok, NULL, 10);
                    if (col == 7) pathstr = tok;
                    tok = strtok(NULL, " ");
                    col++;
                }
                for (k = 0; k < ni; k++) {
                    if (inodes[k] == ino) {
                        printf("  inode=%lu path=%s\n", ino, pathstr ? pathstr : "(abstract/unnamed)");
                        break;
                    }
                }
            }
            fclose(f);
        }
    }

    section("??????????");
    {
        char cmd[256];
        snprintf(cmd, sizeof(cmd),
                 "ls -la /data/data/*/cache/jcc-nb.txt /data/data/*/.xfjcc* "
                 "/data/data/*/.ipcb* /data/data/*/files/pkf473104974 2>/dev/null");
        f = popen(cmd, "r");
        if (f) {
            int n = 0;
            while (fgets(line, sizeof(line), f)) { printf("  %s", line); n++; }
            pclose(f);
            if (!n) printf("  (?)\n");
        }
    }

    section("??/????");
    printf("  /proc/%d/mem ????: ", pid);
    snprintf(path, sizeof(path), "/proc/%d/mem", pid);
    printf("%s\n", access(path, W_OK) == 0 ? "?" : "?");
    (void)i;
    printf("\n== end ==\n");
    return 0;
}