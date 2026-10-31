obj-m := jcck.o jcck_hook.o
jcck-y := jcck_main.o
jcck_hook-y := jcck_hook.o

ccflags-y += -Wno-unused-function -Wno-unused-variable
ccflags-y += -fno-stack-protector -fno-asynchronous-unwind-tables
ccflags-y += -fno-builtin -fno-builtin-strlen -fno-builtin-memcmp -fno-builtin-memset