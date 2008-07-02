#ifndef VIDEO4L_H
#define VIDEO4L_H 

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <signal.h>
#include <errno.h>
#include <time.h>
#include <math.h>
#include <stdarg.h>
#include <linux/types.h>
#include <linux/videodev.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/file.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <pthread.h>


// We define some frequently used data types here. These should match
// the constants in the Java code. We probably missed a few, but we can 
// always extend this set later on....

#define DATA_GREY       0      /* Linear greyscale */

#define DATA_RGB565    10      /* 565 16 bit RGB */
#define DATA_RGB24     11      /* 24bit RGB */
#define DATA_RGB32     12      /* 32bit RGB */
#define DATA_RGB555    13      /* 555 15bit RGB */

#define DATA_YUV422    30      /* YUV capture */
#define DATA_YUYV      31
#define DATA_UYVY      32    
#define DATA_YUV420    33
#define DATA_YUV411    34   

#define PALETTE_JPEG   98      /* JPG */
#define PALETTE_RAW    99      /* any vendor specific format */


// Structure that contains all information related to a specific video 
// device. Note that part of the information is store in the Java object; 
// we only store the c-specific bits here.

/*
struct video_device {    
        int fd;
        struct video_mmap vmmap;
        struct video_capability videocap;
        int mmapsize;
        struct video_mbuf videombuf;
        struct video_picture videopict;
        struct video_window videowin;
        struct video_channel videochan;
        struct video_param videoparam;
        unsigned char *pFramebuffer;
        unsigned char *ptframe[4];
        int framelock[4];
        pthread_mutex_t grabmutex;
};
*/

// Some handy conversion functions

int convert(int source_fmt, unsigned char *src, int width, int height, int dest_fmt, unsigned char *dest);

#endif /* VIDEO4L */
