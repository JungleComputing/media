#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <linux/videodev.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <fcntl.h>

#include "video4j.h"
#include "ibis_video4j_devices_video4linux1_Video4Linux1Device.h"
#include "ibis_video4j_devices_video4linux1_Video4Linux1Discovery.h"

#define MAX_DEVICES 64

struct vdevice {    
        int filedescriptor;
	int mode;
	int frame_size;
	int next_image;

	int palette_available;
	int current_palette;
	int convert_to_palette;
	
	int width;
	int height;

        struct video_picture    picture;
        struct video_mbuf       mbuf;
	struct video_mmap       mmap;
        struct video_capability capabilities;

	unsigned char *buffers;
};

static struct vdevice *video_devices[MAX_DEVICES]; 

#define MAX_PALETTE 16

static int palette_options[] = { 
 VIDEO_PALETTE_GREY, 
 VIDEO_PALETTE_HI240, 
 VIDEO_PALETTE_RGB565, 
 VIDEO_PALETTE_RGB24, 
 VIDEO_PALETTE_RGB32,
 VIDEO_PALETTE_RGB555,
 VIDEO_PALETTE_YUV422,
 VIDEO_PALETTE_YUYV,
 VIDEO_PALETTE_UYVY,
 VIDEO_PALETTE_YUV420,
 VIDEO_PALETTE_YUV411,
 VIDEO_PALETTE_RAW,
 VIDEO_PALETTE_YUV422P,
 VIDEO_PALETTE_YUV411P,
 VIDEO_PALETTE_YUV420P, 
 VIDEO_PALETTE_YUV410P 
};

static int palette_depth [] = {
 8, 
 8, 
 16, 
 24, 
 32,
 15,
 16,
 16,
 16,
 12,
 12,
 8,
 16,
 12,
 12, 
 10 /* unsure! */
};

void print_capability(struct vdevice *dev) 
{
        printf("name: \"%s\"\n", dev->capabilities.name);
        printf("type: %d\n", dev->capabilities.type);
        printf("channels: %d\n", dev->capabilities.channels);
        printf("audios: %d\n", dev->capabilities.audios);
        printf("maxwidth: %d\n", dev->capabilities.maxwidth);
        printf("maxheight: %d\n", dev->capabilities.maxheight);
        printf("minwidth: %d\n", dev->capabilities.minwidth);
        printf("minheight: %d\n", dev->capabilities.minheight);
        printf("\n");
}

void print_picture(struct vdevice *dev)
{
        printf("current picture settings:\n");
        printf("brightness: %d\n", dev->picture.brightness);
        printf("hue: %d\n", dev->picture.hue);
        printf("colour: %d\n", dev->picture.colour);
        printf("contrast: %d\n", dev->picture.contrast);
        printf("whiteness: %d\n", dev->picture.whiteness);
        printf("depth: %d\n",  dev->picture.depth);
        printf("palette: %d\n", dev->picture.palette);
        printf("\n");
}

void print_buffer(struct vdevice *dev)
{
	int i;

        printf("buffer settings:\n");
        printf("total size: %d\n", dev->mbuf.size);
        printf("frames: %d\n", dev->mbuf.frames);

	for (i=0;i<dev->mbuf.frames;i++) {
	        printf("offset[%d]: %d\n", i, dev->mbuf.offsets[i]);
	} 	
}

int scan_palette(struct vdevice *dev) 
{ 
        struct video_picture pict;
        int mask = 0x1;
        int i;

	dev->palette_available = 0;

        /* initialize the internal struct */
        if (ioctl (dev->filedescriptor, VIDIOCGPICT, &pict) < 0)
	{
            perror ("Couldnt get videopict params with VIDIOCGPICT\n");
            return -1;
        }
   
        /* try each palette */
        for (i=0;i<MAX_PALETTE;i++)
        {
	        pict.palette = palette_options[i];
	        pict.depth = palette_depth[i];
	
		// Try to set it 
		if (ioctl (dev->filedescriptor, VIDIOCSPICT, &pict) < 0)
                {
		        // failed -- ignore
                }

		// Try to get it
	        if (ioctl (dev->filedescriptor, VIDIOCGPICT, &pict) < 0)
                {
        		// failed -- ignore
                }

		// Check result
	        if (pict.palette == palette_options[i])
                {
                	dev->palette_available |= mask ;
	                printf("Available palette %d \n", palette_options[i]);
                }

	        mask = mask << 1;
        }

	return 1;
}


int callback(JNIEnv *env, jobject this, struct vdevice *dev) 
{ 
        int i;
	int size;
	jmethodID mid;
	jmethodID mid2;
	jstring name; 
	jclass clazz;
	jobject buffer;

	clazz = (*env)->GetObjectClass(env, this); 

        if (clazz == NULL) { 
            return -1;
        }

	mid = (*env)->GetMethodID(env, clazz, "availableFeatures", "(IIIIIILjava/lang/String;)V");

	if (mid == NULL) { 
	    return -1;
	}	

	mid2 = (*env)->GetMethodID(env, clazz, "addBuffer", "(ILjava/nio/ByteBuffer;)V");

	if (mid2 == NULL) { 
	    return -1;
	}	

	// Inform Java of the device parameters 
        (*env)->CallObjectMethod(env, this, mid, 
		dev->capabilities.maxwidth, 
		dev->capabilities.maxheight, 
		dev->capabilities.minwidth, 
		dev->capabilities.minheight, 
		dev->palette_available, 
                dev->mbuf.frames,
	        (*env)->NewStringUTF(env, dev->capabilities.name));

	// Wrap each image buffer in a DirectByteBuffer, and pass this on to Java. 
        for (i=0;i<dev->mbuf.frames;i++) {

		if (i < dev->mbuf.frames-1) { 
			size = dev->mbuf.offsets[i+1] - dev->mbuf.offsets[i]; 
		} else { 
			size = dev->mbuf.size - dev->mbuf.offsets[i];
		}

		buffer = (*env)->NewDirectByteBuffer(env, dev->buffers + dev->mbuf.offsets[i], (jlong) size); 

	        (*env)->CallObjectMethod(env, this, mid2, i, buffer); 
        }

        return 0;
}

int freeDevice(struct vdevice *dev)
{
	if (dev->buffers != 0) { 
		munmap(dev->buffers, dev->mbuf.size);		
	}

	if (dev->filedescriptor != 0) { 
		close(dev->filedescriptor);		
	}
	
	if (dev != 0) { 
		free(dev);
	}
}

jboolean Java_ibis_video4j_devices_video4linux1_Video4Linux1Device_initDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber)
{
	const jbyte *str;
	struct vdevice *dev;	

	str = (*env)->GetStringUTFChars(env, device, NULL);

     	if (str == NULL) {
       	    return JNI_FALSE; /* OutOfMemoryError already thrown */
     	}

	if (deviceNumber < 0 || deviceNumber >= MAX_DEVICES) {
	    // TODO: should throuw exception here!	 
	    return JNI_FALSE;	
        }

//	if (video_devices[deviceNumber] != null) { 
//	    // TODO: should throuw exception here!	 
//	    return JNI_FALSE;	
//      }

	dev = malloc(sizeof(struct vdevice));

	if (dev == NULL) { 
	    // TODO: should throuw exception here!	 
	    return JNI_FALSE;		
	}

	memset(dev, 0, sizeof(struct vdevice));

printf("Opening webcam %s\n", str);

	if ((dev->filedescriptor = open(str, O_RDWR | O_NONBLOCK)) == -1 ) {
            perror("open");
            (*env)->ReleaseStringUTFChars(env, device, str);
            freeDevice(dev);
	    return JNI_FALSE;
        }

        (*env)->ReleaseStringUTFChars(env, device, str);

printf("CAP\n");

        if (ioctl(dev->filedescriptor, VIDIOCGCAP, &(dev->capabilities)) == -1) {
            perror("VIDIOCGCAP");
	    freeDevice(dev);	
            return JNI_FALSE;
        }

printf("CAP\n");

        print_capability(dev);

        if (ioctl(dev->filedescriptor, VIDIOCGPICT, &(dev->picture)) == -1) {
                perror("VIDIOCGPICT");
		freeDevice(dev);
		return JNI_FALSE;
        }

printf("PIC\n");

        print_picture(dev);

	if (scan_palette(dev) == -1) { 
		freeDevice(dev);
		return JNI_FALSE;			
	} 

        if (ioctl(dev->filedescriptor, VIDIOCGMBUF, &(dev->mbuf)) == -1) {
                perror("VIDIOCGMBUF");
		freeDevice(dev);
		return JNI_FALSE;		
        }

printf("BUF\n");

	print_buffer(dev);

	dev->buffers = mmap(0, dev->mbuf.size, PROT_READ | PROT_WRITE, MAP_SHARED, dev->filedescriptor, 0);

	if (dev->buffers == NULL) {
		freeDevice(dev);
		return JNI_FALSE;
        }

	// Now do some callback to Java to inform the object of our parameters.
	callback(env, this, dev); 

	dev->mmap.frame = 0;

	video_devices[(int) deviceNumber] = dev;

	return JNI_TRUE;
}

jboolean Java_ibis_video4j_devices_video4linux1_Video4Linux1Device_configureDevice(JNIEnv *env, jobject this, jint deviceNumber, jint width, jint height, jint palette_grab, jint palette_convert) 
{
	struct vdevice *dev;	
	
	// TODO: is this correct ? Maybe the array isn't inited ?
	dev = video_devices[(int) deviceNumber];

	if (dev == NULL) { 
		// TODO: throw exception ?
		return JNI_FALSE;
	}

	dev->current_palette = palette_grab;
	dev->convert_to_palette = palette_convert;
	dev->width = width;
	dev->height = height;

	dev->mmap.format = palette_grab;
	dev->mmap.width = width;
	dev->mmap.height = height;


	// Init values for hue, brightnes, etc...
        if (ioctl(dev->filedescriptor, VIDIOCGPICT, &(dev->picture)) == -1) 
        {
                perror("VIDIOCGPICT");
                return JNI_FALSE;
        }

	dev->picture.palette = palette_grab;
	dev->picture.depth = palette_depth[palette_grab-1];
/*
	dev->picture.brightness = 32768;
	dev->picture.hue = 65535;
	dev->picture.colour = 65535;
	dev->picture.contrast = 32768;
	dev->picture.whiteness = 32768;
*/

	printf("Setting resolution to %dx%d (palette %d -> %d)\n", width, height, palette_grab, palette_convert); 


        if (ioctl(dev->filedescriptor, VIDIOCSPICT, &(dev->picture)) == -1) 
        {
                perror("VIDIOCSPICT");
                return JNI_FALSE;
        }

	return JNI_TRUE;
}

jboolean Java_ibis_video4j_devices_video4linux1_Video4Linux1Device_closeDevice(JNIEnv *env, jobject this, jint deviceNumber) 
{
	struct vdevice *dev;	
	
	// TODO: is this correct ? Maybe the array isn't inited ?
	dev = video_devices[(int) deviceNumber];

	if (dev == NULL) { 
		// TODO: throw exception ?
		return JNI_FALSE;
	}

	freeDevice(dev);

	video_devices[(int) deviceNumber] = NULL;
	
	return JNI_TRUE;
}

jint Java_ibis_video4j_devices_video4linux1_Video4Linux1Device_grab(JNIEnv *env, jobject this, jint deviceNumber, jint frames) 
{
	jmethodID mid;
        jclass clazz;
	int count;
	int current_frame;
	struct vdevice *dev;

        // TODO: is this correct ? Maybe the array isn't inited ?
        dev = video_devices[(int) deviceNumber];

        if (dev == NULL) {
                // TODO: throw exception ?
                return -1;
        }

	// Prepare the callback 
  	clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return -1;
        }

        mid = (*env)->GetMethodID(env, clazz, "gotImage", "(I)V");

        if (mid == NULL) {
            return -1;
        }

	// Grab the first frame
	dev->next_image = 0;
	dev->mmap.frame = 0;

//	dev->mmap.format = dev->current_palette;
//	dev->mmap.width = dev->width;
//	dev->mmap.height = dev->height;

	if (ioctl(dev->filedescriptor, VIDIOCMCAPTURE, &(dev->mmap)) == -1) 
	{
                perror("VIDIOCMCAPTURE");
                return -2;
        }

	count = 0;

	while (frames == 0 || count != frames) { 

	        current_frame = dev->next_image;

		if (frames > 0 && count != frames-1) { 		
	        	// Start grabbing next frame immediately
       		 	dev->next_image = (current_frame + 1) % dev->mbuf.frames;
       			dev->mmap.frame = dev->next_image;

//			dev->mmap.format = dev->current_palette;	
//			dev->mmap.width = dev->width;
//			dev->mmap.height = dev->height;

        		if (ioctl(dev->filedescriptor, VIDIOCMCAPTURE, &(dev->mmap)) < 0) {	
                		perror("VIDIOCMCAPTURE");
                		return -2;
        		} 
		}

        	dev->mmap.frame = current_frame;
//		dev->mmap.format = dev->current_palette;	
//		dev->mmap.width = dev->width;
//		dev->mmap.height = dev->height;

        	// Wait until current frame is ready
        	if (ioctl(dev->filedescriptor, VIDIOCSYNC, &(dev->mmap.frame)) < 0) {
                	perror("VIDIOCSYNC");
                	return -3;
        	} 

		// Forward the index of the new frame to Java
                (*env)->CallObjectMethod(env, this, mid, current_frame);

		count++; 
	} 

	return count;
}


jboolean Java_ibis_video4j_devices_video4linux1_Video4Linux1Discovery_testDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber) 
{
        const jbyte *str;
        int filedescriptor;
        jmethodID mid;
        jclass clazz;
        jobject buffer;

        struct video_capability capabilities;
	
	memset(&capabilities, 0, sizeof(struct video_capability));

        str = (*env)->GetStringUTFChars(env, device, NULL);

        if (str == NULL) {
            return JNI_FALSE; /* OutOfMemoryError already thrown */
        }

        if (deviceNumber < 0 || deviceNumber >= MAX_DEVICES) {
            // TODO: should throw exception here ?
            return JNI_FALSE;
        }

        if ((filedescriptor = open(str, O_RDWR | O_NONBLOCK)) == -1 ) {
            // TODO: should throw exception here ?
            (*env)->ReleaseStringUTFChars(env, device, str);
            return JNI_FALSE;
        }

        (*env)->ReleaseStringUTFChars(env, device, str);

        if (ioctl(filedescriptor, VIDIOCGCAP, &capabilities) == -1) {
            perror("VIDIOCGCAP");
	    close(filedescriptor);
            return JNI_FALSE;
        }
	
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return JNI_FALSE;
        }

        mid = (*env)->GetMethodID(env, clazz, "available", "(Ljava/lang/String;Ljava/lang/String;I)V");

        if (mid == NULL) {
            return JNI_FALSE;
        }

        // Inform Java of the device parameters
        (*env)->CallObjectMethod(env, this, mid, 
		device, 
                (*env)->NewStringUTF(env, capabilities.name), 
		deviceNumber);

	// Close the device and free all resources
	close(filedescriptor);

	return JNI_TRUE;
}

