#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <linux/videodev.h>
#include <linux/videodev2.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <fcntl.h>

#include "video4j.h"
#include "ibis_video4j_devices_video4linux_Video4LinuxDevice.h"
#include "ibis_video4j_devices_video4linux_Video4LinuxDiscovery.h"

#define DEBUG 0

#define DISCRETE   0
#define CONTINUOUS 1
#define STEPWISE   2

#define MAX_DEVICES 64
#define MAX_BUFFERS 16 // 16 Buffers should be enough for everyone ;-)

struct vdevice {    
        int filedescriptor;

	int width;
	int height;
	int palette;

	int configured;	

	int available_buffers;
	int next_buffer;

	int v4l; 

        unsigned char *buffers[MAX_BUFFERS];	
	int buffer_length[MAX_BUFFERS];
	int total_length;
};

static struct vdevice *video_devices[MAX_DEVICES]; 

#define MAX_PALETTE 16

static int v4l1_palette_options[] = { 
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

static int v4l1_palette_depth [] = {
 8, 
 8, 
 16, 
 24, 
 24, // 32,
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

// ================================================================================
// Shared functions
// ================================================================================

int callbackDeviceName(JNIEnv *env, jobject this, jstring name)
{
        jmethodID mid;
        jclass clazz;

	// Prepare the info needed to do a Java callback
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return -12;
        }

        mid = (*env)->GetMethodID(env, clazz, "deviceName", "(Ljava/lang/String;)V");

        if (mid == NULL) {
            return -12;
        }

        // Inform Java of the device name
        (*env)->CallVoidMethod(env, this, mid, name);

	return 0;
}

int callbackBufferCount(JNIEnv *env, jobject this, jint count)
{
        jmethodID mid;
        jclass clazz;

        // Prepare the info needed to do a Java callback
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return -12;
        }

        mid = (*env)->GetMethodID(env, clazz, "bufferCount", "(I)V");

        if (mid == NULL) {
            return -12;
        }

        // Inform Java of the device name
        (*env)->CallVoidMethod(env, this, mid, count);

        return 0;
}

int callbackCapability(JNIEnv *env, jobject this, jint type, jint palette, jint minWidth, jint minHeight, 
	jint maxWidth, jint maxHeight, jint stepWidth, jint stepHeight, jint numerator, jint denominator)
{
        jmethodID mid;
        jclass clazz;

        // Prepare the info needed to do a Java callback
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return -12;
        }

        mid = (*env)->GetMethodID(env, clazz, "capability", "(IIIIIIIIII)V");

        if (mid == NULL) {
            return -13;
        }
	
	// Inform Java of the device name
        (*env)->CallVoidMethod(env, this, mid, type, palette, minWidth, minHeight, maxWidth, maxHeight, stepWidth, stepHeight, numerator, denominator);

	return 0;
}

int callbackAddBuffer(JNIEnv *env, jobject this, jobject buffer, jint index) 
{ 
	jmethodID mid;
	jclass clazz;

	clazz = (*env)->GetObjectClass(env, this); 

        if (clazz == NULL) { 
            return -12;
        }

	mid = (*env)->GetMethodID(env, clazz, "addBuffer", "(ILjava/nio/ByteBuffer;)V");

	if (mid == NULL) { 
	    return -12;
	}	

	(*env)->CallVoidMethod(env, this, mid, index, buffer); 

        return 0;
}

int freeDevice(struct vdevice *dev)
{
	int i;

	if (dev->v4l == 1) { 
		if (dev->buffers[0] != 0) { 
			munmap(dev->buffers[0], dev->total_length);		
		}
	} 

	if (dev->v4l == 2) { 
		for (i=0;i<dev->available_buffers;i++) { 
			if (dev->buffer_length[i] > 0) { 
				munmap(dev->buffers[i], dev->buffer_length[i]);
			}
		}
	}

	if (dev->filedescriptor != 0) { 
		close(dev->filedescriptor);		
	}
	
	if (dev != 0) { 
		free(dev);
	}
}


// ================================================================================
// V4L1 Part
// ================================================================================

int scan_palette(JNIEnv *env, jobject this, struct vdevice *dev, struct video_capability *capabilities) 
{ 
        int i;
	int result = -1;
        struct video_picture pict;

        /* initialize the internal struct */
        if (ioctl (dev->filedescriptor, VIDIOCGPICT, &pict) < 0)
	{
            	return -1;
        }
   
        /* try each palette */
        for (i=0;i<MAX_PALETTE;i++)
        {
	        pict.palette = v4l1_palette_options[i];
	        pict.depth = v4l1_palette_depth[i];
	
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

	        if (pict.palette == v4l1_palette_options[i])
                {
			callbackCapability(env, this, CONTINUOUS, pict.palette, 
				capabilities->minwidth, capabilities->minheight, 
               			capabilities->maxwidth, capabilities->maxheight, 
				0, 0, 1, 15);
	
			result = 0;		
		
			if (DEBUG) { 
	                	printf("Available palette %d \n", v4l1_palette_options[i]);
                	}
		}
        }

	return result;
}

jint v4l_initDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber)
{
	int i, size;

	const jbyte *str;
	jobject buffer;
	
	struct vdevice *dev;	

        struct video_picture picture;
        struct video_mbuf       mbuf;
        struct video_capability capabilities;

        memset(&picture, 0, sizeof(struct video_picture));
        memset(&mbuf, 0, sizeof(struct video_mbuf));
        memset(&capabilities, 0, sizeof(struct video_capability));

	str = (*env)->GetStringUTFChars(env, device, NULL);

     	if (str == NULL) {
       	    	return -1; /* OutOfMemoryError already thrown */
     	}

	if (deviceNumber < 0 || deviceNumber >= MAX_DEVICES) {
	    	return -2;	
        }

//	if (video_devices[deviceNumber] != null) { 
//	    // TODO: should throuw exception here!	 
//	    return JNI_FALSE;	
//      }

	dev = malloc(sizeof(struct vdevice));

	if (dev == NULL) { 
	    	return -1;		
	}

	memset(dev, 0, sizeof(struct vdevice));

	dev->v4l = 1;

	if (DEBUG) { 
		printf("Opening v4l1 webcam %s\n", str);
	}

	if ((dev->filedescriptor = open(str, O_RDWR | O_NONBLOCK)) == -1 ) {
            	(*env)->ReleaseStringUTFChars(env, device, str);
            	freeDevice(dev);
	    	return -3;
        }

        (*env)->ReleaseStringUTFChars(env, device, str);

        if (ioctl(dev->filedescriptor, VIDIOCGCAP, &capabilities) == -1) {
	    	freeDevice(dev);	
            	return -4;
        }

	if (DEBUG) { 
            	printf("CAP:\n");
            	printf("name: \"%s\"\n", capabilities.name);
            	printf("type: %d\n", capabilities.type);
            	printf("channels: %d\n", capabilities.channels);
            	printf("audios: %d\n", capabilities.audios);
            	printf("maxwidth: %d\n", capabilities.maxwidth);
            	printf("maxheight: %d\n", capabilities.maxheight);
            	printf("minwidth: %d\n", capabilities.minwidth);
            	printf("minheight: %d\n", capabilities.minheight);
            	printf("\n");
	}

	callbackDeviceName(env, this, (*env)->NewStringUTF(env, capabilities.name));

	if (ioctl(dev->filedescriptor, VIDIOCGPICT, &(picture)) == -1) {
		freeDevice(dev);
		return -4;
        }

	if (DEBUG) { 
		printf("PIC\n");
        	printf("current picture settings:\n");
       		printf("brightness: %d\n", picture.brightness);
        	printf("hue: %d\n", picture.hue);
        	printf("colour: %d\n", picture.colour);
        	printf("contrast: %d\n", picture.contrast);
        	printf("whiteness: %d\n", picture.whiteness);
        	printf("depth: %d\n",  picture.depth);
        	printf("palette: %d\n", picture.palette);
        	printf("\n");
	}

        if (scan_palette(env, this, dev, &capabilities) == -1) { 
		// No useful palette options found!
		freeDevice(dev);
		return -6;			
	} 

        if (ioctl(dev->filedescriptor, VIDIOCGMBUF, &(mbuf)) == -1) {
		freeDevice(dev);
		return -4;		
        }

	dev->available_buffers = mbuf.frames;

	callbackBufferCount(env, this, mbuf.frames);

	if (DEBUG) { 
        	printf("buffer settings:\n");
        	printf("total size: %d\n", mbuf.size);
        	printf("frames: %d\n", mbuf.frames);
		printf("offset[%d]: %d\n", i, mbuf.offsets[i]);
	}	

	dev->buffers[0] = mmap(0, mbuf.size, PROT_READ | PROT_WRITE, MAP_SHARED, dev->filedescriptor, 0);
	dev->buffer_length[0] = mbuf.size;

	if (dev->buffers[0] == NULL) {
		freeDevice(dev);
		return -1;
        }

	// Wrap each image buffer in a DirectByteBuffer, and pass this on to Java. 
        for (i=0;i<mbuf.frames;i++) {

		if (i < mbuf.frames-1) { 
			dev->buffer_length[i] = mbuf.offsets[i+1] - mbuf.offsets[i]; 
		} else { 
			dev->buffer_length[i] = mbuf.size - mbuf.offsets[i];
		}

		buffer = (*env)->NewDirectByteBuffer(env, dev->buffers[0] + mbuf.offsets[i], (jlong) dev->buffer_length[i]); 

		callbackAddBuffer(env, this, buffer, i);
        }

	dev->total_length = mbuf.size;

	video_devices[(int) deviceNumber] = dev;

	return 0;
}

jint v4l_configureDevice(JNIEnv *env, jobject this, struct vdevice *dev, jint width, jint height, jint palette) 
{
        struct video_picture picture;

        memset(&picture, 0, sizeof(struct video_picture));

fprintf(stderr, "Setting resolution to %dx%d (palette %d depth %d)\n", width, height, palette, v4l1_palette_depth[palette-1]); 

	dev->width = width;
	dev->height = height;
	dev->palette = palette;

	// Init values for hue, brightnes, etc...
        if (ioctl(dev->filedescriptor, VIDIOCGPICT, &picture) == -1) 
        {
                return -5;
        }


                fprintf(stderr,"PIC PRE\n");
                fprintf(stderr,"current picture settings:\n");
                fprintf(stderr,"brightness: %d\n", picture.brightness);
                fprintf(stderr,"hue: %d\n", picture.hue);
                fprintf(stderr,"colour: %d\n", picture.colour);
                fprintf(stderr,"contrast: %d\n", picture.contrast);
                fprintf(stderr,"whiteness: %d\n", picture.whiteness);
                fprintf(stderr,"depth: %d\n",  picture.depth);
                fprintf(stderr,"palette: %d\n", picture.palette);
                fprintf(stderr,"\n");


	picture.palette = palette;
	picture.depth = v4l1_palette_depth[palette-1];

                fprintf(stderr,"PIC SET\n");
                fprintf(stderr,"current picture settings:\n");
                fprintf(stderr,"brightness: %d\n", picture.brightness);
                fprintf(stderr,"hue: %d\n", picture.hue);
                fprintf(stderr,"colour: %d\n", picture.colour);
                fprintf(stderr,"contrast: %d\n", picture.contrast);
                fprintf(stderr,"whiteness: %d\n", picture.whiteness);
                fprintf(stderr,"depth: %d\n",  picture.depth);
                fprintf(stderr,"palette: %d\n", picture.palette);
                fprintf(stderr,"\n");
 

//	if (DEBUG) { 
		printf("Setting resolution to %dx%d (palette %d depth)\n", width, height, palette, v4l1_palette_depth[palette-1]); 
//	}

        if (ioctl(dev->filedescriptor, VIDIOCSPICT, &picture) == -1) 
        {
                return -5;
        }

                fprintf(stderr,"PIC POST\n");
                fprintf(stderr,"current picture settings:\n");
                fprintf(stderr,"brightness: %d\n", picture.brightness);
                fprintf(stderr,"hue: %d\n", picture.hue);
                fprintf(stderr,"colour: %d\n", picture.colour);
                fprintf(stderr,"contrast: %d\n", picture.contrast);
                fprintf(stderr,"whiteness: %d\n", picture.whiteness);
                fprintf(stderr,"depth: %d\n",  picture.depth);
                fprintf(stderr,"palette: %d\n", picture.palette);
                fprintf(stderr,"\n");



	return 0;
}

jint v4l_grab(JNIEnv *env, jobject this, struct vdevice *dev) 
{
	int current, next;

	jmethodID mid;
        jclass clazz;
        jboolean more;

	struct video_mmap mmap;

        memset(&mmap, 0, sizeof(struct video_mmap));

	// Prepare the callback 
  	clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            return -12;
        }

        mid = (*env)->GetMethodID(env, clazz, "gotImage", "(II)Z");

        if (mid == NULL) {
            return -12;
        }

	// Grab the first frame
	next = 0;

	mmap.frame = 0;
	mmap.format = dev->palette;
        mmap.width = dev->width;
        mmap.height = dev->height;

	if (ioctl(dev->filedescriptor, VIDIOCMCAPTURE, &mmap) == -1) 
	{
                return -5;
        }

	more = JNI_TRUE;

	while (more == JNI_TRUE) { 

	        current = next;

	      	// Start grabbing next frame immediately
       	 	next = (current + 1) % dev->available_buffers;
       		mmap.frame = next;

	     	if (ioctl(dev->filedescriptor, VIDIOCMCAPTURE, &mmap) < 0) {	
               		return -13;
       		} 

        	mmap.frame = current;

        	// Wait until current frame is ready
        	if (ioctl(dev->filedescriptor, VIDIOCSYNC, &mmap.frame) < 0) {
                	return -13;
        	} 

		// Forward the buffer index and size of the new frame to Java
                more = (*env)->CallBooleanMethod(env, this, mid,
                                (jint) current,
                                (jint) dev->buffer_length[current]);
	} 

	return 0;
}



// ================================================================================
// V4L2 Part
// ================================================================================

void forward_frame_intervals(JNIEnv *env, jobject this, struct vdevice *dev, __u32 format,  __u32 width, __u32 height) 
{
        int ret;
        struct v4l2_frmivalenum interval;

        memset(&interval, 0, sizeof(struct v4l2_frmivalenum));
      
	interval.index = 0;
        interval.pixel_format = format;
        interval.width = width;
        interval.height = height;
        
        while ((ret = ioctl(dev->filedescriptor, VIDIOC_ENUM_FRAMEINTERVALS, &interval)) == 0) {
                if (interval.type == V4L2_FRMIVAL_TYPE_DISCRETE) {
			callbackCapability(env, this, DISCRETE, format, width, height, 0, 0, 0, 0, 
                                interval.discrete.numerator, interval.discrete.denominator);
                } else if (interval.type == V4L2_FRMIVAL_TYPE_CONTINUOUS) {
			// TODO: implement
 	         	printf("Unsupported interval: continuous\n");
                        break;
                } else if (interval.type == V4L2_FRMIVAL_TYPE_STEPWISE) {
			// TODO: implement
 	         	printf("Unsupported interval: stepwise\n");
		}
                interval.index++;
        }
}

void forward_frame_sizes(JNIEnv *env, jobject this, struct vdevice *dev, __u32 format) 
{
	int ret;
        struct v4l2_frmsizeenum size;

        memset(&size, 0, sizeof(struct v4l2_frmsizeenum));
        
	size.index = 0;
        size.pixel_format = format;
        
	while ((ret = ioctl(dev->filedescriptor, VIDIOC_ENUM_FRAMESIZES, &size)) == 0) {
                if (size.type == V4L2_FRMSIZE_TYPE_DISCRETE) {
           		forward_frame_intervals(env, this, dev, format, size.discrete.width, size.discrete.height);
                } else if (size.type == V4L2_FRMSIZE_TYPE_CONTINUOUS) {
			// TODO: implement
 	         	printf("Unsupported format: continuous\n");
                        break;
                } else if (size.type == V4L2_FRMSIZE_TYPE_STEPWISE) {
 	         	// TODO: implement
			printf("Unsupported format: stepwise\n");
                        break;
                }
                size.index++;
        }
}

int forward_formats(JNIEnv *env, jobject this, struct vdevice *dev) 
{
        int ret;
        struct v4l2_fmtdesc format;

        memset(&format, 0, sizeof(struct v4l2_fmtdesc));

        format.index = 0;
        format.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

        while ((ret = ioctl(dev->filedescriptor, VIDIOC_ENUM_FMT, &format)) == 0) {
                format.index++;
		forward_frame_sizes(env, this, dev, format.pixelformat);
        }

        if (errno != EINVAL) {
                return errno;
        }

        return 0;
}

#if DEBUG

int enum_frame_intervals(int dev, __u32 pixfmt, __u32 width, __u32 height)
{
        int ret;
        struct v4l2_frmivalenum fival;

        memset(&fival, 0, sizeof(fival));
        fival.index = 0;
        fival.pixel_format = pixfmt;
        fival.width = width;
        fival.height = height;
        printf("\tTime interval between frame: ");
        while ((ret = ioctl(dev, VIDIOC_ENUM_FRAMEINTERVALS, &fival)) == 0) {
                if (fival.type == V4L2_FRMIVAL_TYPE_DISCRETE) {
                                printf("%u/%u, ",
                                                fival.discrete.numerator, fival.discrete.denominator);
                } else if (fival.type == V4L2_FRMIVAL_TYPE_CONTINUOUS) {
                                printf("{min { %u/%u } .. max { %u/%u } }, ",
                                                fival.stepwise.min.numerator, fival.stepwise.min.numerator,
                                                fival.stepwise.max.denominator, fival.stepwise.max.denominator);
                                break;
                } else if (fival.type == V4L2_FRMIVAL_TYPE_STEPWISE) {
                                printf("{min { %u/%u } .. max { %u/%u } / "
                                                "stepsize { %u/%u } }, ",
                                                fival.stepwise.min.numerator, fival.stepwise.min.denominator,
                                                fival.stepwise.max.numerator, fival.stepwise.max.denominator,
                                                fival.stepwise.step.numerator, fival.stepwise.step.denominator);
                                break;
                }
                fival.index++;
        }
        printf("\n");
        if (ret != 0 && errno != EINVAL) {
                printf("ERROR enumerating frame intervals: %d\n", errno);
                return errno;
        }

        return 0;
}

int enum_frame_sizes(int dev, __u32 pixfmt)
{
        int ret;
        struct v4l2_frmsizeenum fsize;

        memset(&fsize, 0, sizeof(fsize));
        fsize.index = 0;
        fsize.pixel_format = pixfmt;
        while ((ret = ioctl(dev, VIDIOC_ENUM_FRAMESIZES, &fsize)) == 0) {
                if (fsize.type == V4L2_FRMSIZE_TYPE_DISCRETE) {
                        printf("{ discrete: width = %u, height = %u }\n",
                                        fsize.discrete.width, fsize.discrete.height);
                        ret = enum_frame_intervals(dev, pixfmt,
                                        fsize.discrete.width, fsize.discrete.height);
                        if (ret != 0)
                                printf("  Unable to enumerate frame sizes.\n");
                } else if (fsize.type == V4L2_FRMSIZE_TYPE_CONTINUOUS) {
                        printf("{ continuous: min { width = %u, height = %u } .. "
                                        "max { width = %u, height = %u } }\n",
                                        fsize.stepwise.min_width, fsize.stepwise.min_height,
                                        fsize.stepwise.max_width, fsize.stepwise.max_height);
                        printf("  Refusing to enumerate frame intervals.\n");
                        break;
                } else if (fsize.type == V4L2_FRMSIZE_TYPE_STEPWISE) {
                        printf("{ stepwise: min { width = %u, height = %u } .. "
                                        "max { width = %u, height = %u } / "
                                        "stepsize { width = %u, height = %u } }\n",
                                        fsize.stepwise.min_width, fsize.stepwise.min_height,
                                        fsize.stepwise.max_width, fsize.stepwise.max_height,
                                        fsize.stepwise.step_width, fsize.stepwise.step_height);
                        printf("  Refusing to enumerate frame intervals.\n");
                        break;
                }
                fsize.index++;
        }
        if (ret != 0 && errno != EINVAL) {
                printf("ERROR enumerating frame sizes: %d\n", errno);
                return errno;
        }

        return 0;
}

int enum_frame_formats(int dev)
{
        int ret;
        struct v4l2_fmtdesc fmt;

        memset(&fmt, 0, sizeof(fmt));

        fmt.index = 0;
        fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        while ((ret = ioctl(dev, VIDIOC_ENUM_FMT, &fmt)) == 0) {
                fmt.index++;
                printf("{ pixelformat = '%c%c%c%c', description = '%s' }\n",
                                fmt.pixelformat & 0xFF, (fmt.pixelformat >> 8) & 0xFF,
                                (fmt.pixelformat >> 16) & 0xFF, (fmt.pixelformat >> 24) & 0xFF,
                                fmt.description);
                ret = enum_frame_sizes(dev, fmt.pixelformat);
                if (ret != 0)
                        printf("  Unable to enumerate frame sizes.\n");
        }
        if (errno != EINVAL) {
                printf("ERROR enumerating frame formats: %d\n", errno);
                return errno;
        }

        return 0;
}

#endif // DEBUG


jint v4l2_initDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber)
{
        int ret, i;

	jstring name;

	const jbyte *str;
	struct vdevice *dev;	

        struct v4l2_capability capabilities;	

	memset(&capabilities, 0, sizeof(struct v4l2_capability));

	str = (*env)->GetStringUTFChars(env, device, NULL);

     	if (str == NULL) {
       		return -1;
     	}

	if (deviceNumber < 0 || deviceNumber >= MAX_DEVICES) {
	    	return -2;	
        }

//	if (video_devices[deviceNumber] != null) { 
//	    // TODO: should throuw exception here!	 
//	    return JNI_FALSE;	
//      }

	dev = malloc(sizeof(struct vdevice));

	if (dev == NULL) { 
	    	return -1;		
	}

	memset(dev, 0, sizeof(struct vdevice));

	dev->v4l = 2;

printf("Opening V4L2 webcam %s\n", str);

	if ((dev->filedescriptor = open(str, O_RDWR)) == -1 ) {
            	(*env)->ReleaseStringUTFChars(env, device, str);
            	freeDevice(dev);
	    	return -3;
        }

        (*env)->ReleaseStringUTFChars(env, device, str);

        // Perform a V4L2 capabilities query
	if (ioctl(dev->filedescriptor, VIDIOC_QUERYCAP, &capabilities) < 0) {
            	freeDevice(dev);
	    	return -4;
	}
	
        if ((capabilities.capabilities & V4L2_CAP_VIDEO_CAPTURE) == 0) {
            	freeDevice(dev);
	    	return -7;	    
        }

	if (DEBUG) { 
		// TODO: Use these ??
		if ((capabilities.capabilities & V4L2_CAP_STREAMING) == 0) {
        	    	printf("%s does not support streaming i/o\n", str);
        	}

        	if ((capabilities.capabilities & V4L2_CAP_READWRITE) == 0) {
            		printf("%s does not support read i/o\n", str);
        	}
	}	

	// Inform Java of the device name 
	callbackDeviceName(env, this, (*env)->NewStringUTF(env, capabilities.card)); 

	// Inform Java of the supported video formats 
        forward_formats(env, this, dev);

	// NOTE: we stop the configuration here. The device has not yet been 
        // configured to use a specific resolution or palette, nor have any 
        // buffers been allocated. We pospone this, since we do not yet know 
        // which resolution/palette the user prefers. In addition, some webcam 
        // drivers do not seem to like it when an oversized buffer is used. 

	// Store the device info
	video_devices[(int) deviceNumber] = dev;

	return 0;
}

jint v4l2_configureDevice(JNIEnv *env, jobject this, struct vdevice *dev, jint width, jint height, jint palette, jint fps, jint quality, jint bufferCount) 
{
	int ret, i;

	jobject bytebuffer;

	struct v4l2_format format;
	struct v4l2_streamparm stream;
        struct v4l2_jpegcompression compression;
	struct v4l2_requestbuffers buffers;
        struct v4l2_buffer buffer;

	memset(&format, 0, sizeof(struct v4l2_format));
	memset(&stream, 0, sizeof(struct v4l2_streamparm));
        memset(&buffers, 0, sizeof(struct v4l2_requestbuffers));

	// NOTE: if the device is already configured, we need to unmap old buffers before mapping new ones!
fprintf(stderr, "FIXME: configure device incomplete!\n");
	
	//if (DEBUG) { 
		fprintf(stderr, "Configure V4L2 device to use %dx%d @ %d fps, palette %d\n", width, height, fps, palette);
	//}

	// Set the resolution and palette
	format.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    	format.fmt.pix.width = width;
    	format.fmt.pix.height = height;
    	format.fmt.pix.pixelformat = palette;
    	format.fmt.pix.field = V4L2_FIELD_ANY;
   
	ret = ioctl(dev->filedescriptor, VIDIOC_S_FMT, &format);

fprintf(stderr, "V4L2 device returned a resolution of %dx%d bytes per image: %d\n", format.fmt.pix.width, format.fmt.pix.height, format.fmt.pix.sizeimage);

	if (ret < 0) {
fprintf(stderr, "S_FMT failed\n");
	   	return -5;
	}

        // Set the framerate
	stream.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    	stream.parm.capture.timeperframe.numerator = 1;
    	stream.parm.capture.timeperframe.denominator = fps;
    
	ret = ioctl(dev->filedescriptor, VIDIOC_S_PARM, &stream); 

fprintf(stderr, "V4L2 device returned a framerate of %d/%d, number of buffers %d\n", stream.parm.capture.timeperframe.numerator, stream.parm.capture.timeperframe.denominator, stream.parm.capture.readbuffers);

	if (ret < 0) {
fprintf(stderr, "S_PARM failed\n");
	   	return 0;
	}

	if (quality >= 0 && quality <= 65535) { 
		compression.quality = quality;		 
		compression.APPn = 0;
		compression.APP_len = 0;
		compression.COM_len = 0;
	/*
		ret = ioctl(dev->filedescriptor, VIDIOC_S_JPEGCOMP, &compression); 

		if (ret < 0) {
			fprintf(stderr, "S_COMP failed\n");
		}
*/
	}

	// Allocate the buffers
        if (bufferCount > MAX_BUFFERS) { 
		bufferCount = MAX_BUFFERS;
	}

	buffers.count = bufferCount;
    	buffers.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    	buffers.memory = V4L2_MEMORY_MMAP;

    	ret = ioctl(dev->filedescriptor, VIDIOC_REQBUFS, &buffers);
    
	if (ret < 0) {
fprintf(stderr, "VIDIOC_REQBUFS failed\n");
	   	return -8;
	}

	// Note that we may get less buffers that we asked for (this depends on the driver),
	// so make sure we use the right number in the following loops 
	dev->available_buffers = buffers.count;
	dev->next_buffer = 0;

	callbackBufferCount(env, this, buffers.count);

	// mmap the buffers 
    	for (i=0;i<buffers.count;i++) {
            	memset(&buffer, 0, sizeof(struct v4l2_buffer));
        	
	    	buffer.index = i;
            	buffer.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
            	buffer.memory = V4L2_MEMORY_MMAP;
        
            	ret = ioctl(dev->filedescriptor, VIDIOC_QUERYBUF, &buffer);
        
            	if (ret < 0) {
fprintf(stderr, "VIDIOC_QUERYBUF failed\n");
			return -1;
            	}
     
            	dev->buffer_length[i] = buffer.length;

printf("Buffer %d length %d\n", i, buffer.length);

	    	dev->buffers[i] = mmap(0, buffer.length, PROT_READ | PROT_WRITE, MAP_SHARED, dev->filedescriptor, buffer.m.offset);

	    	if (dev->buffers[i] == MAP_FAILED) {
			if (DEBUG) { 	        
				printf("Unable to map buffer (%d)\n", errno);
			}
                	dev->buffer_length[i] = 0;
			return -5;
     	    	}

            	// Wrap each image buffer in a DirectByteBuffer, and pass this on to Java.
	    	bytebuffer = (*env)->NewDirectByteBuffer(env, dev->buffers[i], (jlong) buffer.length);
	    	callbackAddBuffer(env, this, bytebuffer, i);
        }
    
        // Queue the buffers.
        for (i=0;i<buffers.count;++i) {
            	memset(&buffer, 0, sizeof(struct v4l2_buffer));
            
            	buffer.index = i;
            	buffer.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
            	buffer.memory = V4L2_MEMORY_MMAP;
            
            	ret = ioctl(dev->filedescriptor, VIDIOC_QBUF, &buffer);
       
		if (DEBUG) { 
	            	if (ret < 0) {
        	        	printf("Unable to queue buffer (%d).\n", errno);
			} else { 
				printf("Queued buffer %d\n", i);
	    		}
		}
	
		if (ret < 0) { 
			return -9;
		}
	}

	dev->configured = 1;
        return 0;
}

jint v4l2_grab(JNIEnv *env, jobject this, struct vdevice *dev) 
{
        int opcode, ret;

	jmethodID mid;
        jclass clazz;
	jboolean more;

        struct v4l2_buffer buffer;

printf("v4l2 grab\n");

	more = JNI_TRUE;

	// Prepare the callback
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            	return -12;
        }

        mid = (*env)->GetMethodID(env, clazz, "gotImage", "(II)Z");

        if (mid == NULL) {
            	return -12;
        }

	// Start the video stream
	opcode = V4L2_BUF_TYPE_VIDEO_CAPTURE;

printf("Doning streamon\n");

        ret = ioctl(dev->filedescriptor, VIDIOC_STREAMON, &opcode);
  
        if (ret < 0) {
    	    	printf("Unable to %s capture: %d.\n", "start", errno);
            	return -5;
    	}

//	if (DEBUG) { 
		printf("START GRABBING!!!\n");
//  	}

	// Keep grabbing frames until we are told to stop!
	while (more == JNI_TRUE) { 

		// Dequeue the next buffer
		memset(&buffer, 0, sizeof(struct v4l2_buffer));
        	buffer.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        	buffer.memory = V4L2_MEMORY_MMAP;
		buffer.index = dev->next_buffer;

		dev->next_buffer = (dev->next_buffer + 1) % dev->available_buffers;
  
//printf("DQBUF!!!\n");
  
		ret = ioctl(dev->filedescriptor, VIDIOC_DQBUF, &buffer);


   
		if (ret < 0) {
//			if (DEBUG) {
       	   			printf("Unable to dequeue buffer (%d %d %d %d %d).\n", errno, EAGAIN, EINVAL, ENOMEM, EIO);
//     			}
	 	} else {

			if (DEBUG) { 
				printf("GOT IMAGE size %d!\n", buffer.bytesused);
			}

			// Forward the buffer index and size of the new frame to Java
      			more = (*env)->CallBooleanMethod(env, this, mid, 
                        	(jint) buffer.index,
                       		(jint) buffer.bytesused);

			// Return the buffer to the queue
			ret = ioctl(dev->filedescriptor, VIDIOC_QBUF, &buffer);
   
			if (ret < 0) {
//				if (DEBUG) { 
	       				printf("Unable to requeue buffer (%d).\n", errno);
//				}
			}
		}
	}

//	if (DEBUG) {
		printf("STOP GRABBING!!!\n");
//	}

	// Stop the video stream
	opcode = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	ret = ioctl(dev->filedescriptor, VIDIOC_STREAMOFF, &opcode);
   
	if (ret < 0) {
//		if (DEBUG) { 
        		printf("Unable to %s capture: %d.\n", "stop", errno);
//        	}
		return -5;
    	}
	
	return 0;
}


// ===========================================================================================
// JNI Functions
// ===========================================================================================



jint Java_ibis_video4j_devices_video4linux_Video4LinuxDevice_initDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber, jint api)
{
	if (api == 1) { 
		return v4l_initDevice(env, this, device, deviceNumber);
	} else if (api == 2){ 
		return v4l2_initDevice(env, this, device, deviceNumber);
	}

	return -10;
}

jint Java_ibis_video4j_devices_video4linux_Video4LinuxDevice_configureDevice(JNIEnv *env, jobject this, jint deviceNumber, jint width, jint height, jint palette, jint fps, jint quality, jint bufferCount)
{
	struct vdevice *dev;	

	// TODO: is this correct ? Maybe the array isn't inited ?
	dev = video_devices[(int) deviceNumber];

	if (dev == NULL) { 
		return -11;
	}

	if (dev->v4l == 1) { 
		return v4l_configureDevice(env, this, dev, width, height, palette);
	} else if (dev->v4l == 2) { 
		return v4l2_configureDevice(env, this, dev, width, height, palette, fps, quality, bufferCount);
	}

fprintf(stderr, "####### configuring device FAILED %d\n", dev->v4l); 

	return -10;
} 


jint Java_ibis_video4j_devices_video4linux_Video4LinuxDevice_grab(JNIEnv *env, jobject this, jint deviceNumber) 
{
	struct vdevice *dev;	

	// TODO: is this correct ? Maybe the array isn't inited ?
	dev = video_devices[(int) deviceNumber];

	if (dev == NULL) { 
		return -11;
	}

	// Check if the device has already been configured (i.e., resolution, palette and buffers are set). 
	if (dev->configured == 0) { 
		return -12;
	}

	if (dev->v4l == 1) { 
		return v4l_grab(env, this, dev);
	} else if (dev->v4l == 2) { 
		return v4l2_grab(env, this, dev);
	}

	return -10;
}

jint Java_ibis_video4j_devices_video4linux_Video4LinuxDevice_closeDevice(JNIEnv *env, jobject this, jint deviceNumber) 
{
	struct vdevice *dev;	
	
	// TODO: is this correct ? Maybe the array isn't inited ?
	dev = video_devices[(int) deviceNumber];

	if (dev == NULL) { 
		// TODO: throw exception ?
		return -11;
	}

	freeDevice(dev);

	video_devices[(int) deviceNumber] = NULL;
	
	return 0;
}






jboolean Java_ibis_video4j_devices_video4linux_Video4LinuxDiscovery_testDevice(JNIEnv *env, jobject this, jstring device, jint deviceNumber) 
{
        const jbyte *str;
        int filedescriptor;
        jmethodID mid;
        jclass clazz;
        jobject buffer;
        jboolean v4l1;
        jboolean v4l2;
	jstring name;

        struct v4l2_capability capabilities;
        struct video_capability capabilities_v4l1;
	
	memset(&capabilities, 0, sizeof(struct v4l2_capability));
        memset(&capabilities_v4l1, 0, sizeof(struct video_capability));

	v4l1 = JNI_FALSE;
	v4l2 = JNI_FALSE;

        // Get the device name
        str = (*env)->GetStringUTFChars(env, device, NULL);

        if (str == NULL) {
        	return JNI_FALSE; /* OutOfMemoryError already thrown */
        }

        if (deviceNumber < 0 || deviceNumber >= MAX_DEVICES) {
            	// TODO: should throw exception here ?
            	return JNI_FALSE;
        }
 
        // Prepare the info needed to do a Java callback
        clazz = (*env)->GetObjectClass(env, this);

        if (clazz == NULL) {
            	return JNI_FALSE;
        }

        mid = (*env)->GetMethodID(env, clazz, "available", "(Ljava/lang/String;Ljava/lang/String;IZZ)V");

        if (mid == NULL) {
            	return JNI_FALSE;
        }

        // Open the device
        if ((filedescriptor = open(str, O_RDWR | O_NONBLOCK)) < 0) {
            	// TODO: should throw exception here ?
            	(*env)->ReleaseStringUTFChars(env, device, str);
            	return JNI_FALSE;
        }

        // Attempt a V4L2 capabilities query
	if (ioctl(filedescriptor, VIDIOC_QUERYCAP, &capabilities) >= 0) { 
	
             	if ((capabilities.capabilities & V4L2_CAP_VIDEO_CAPTURE) == 0) {
       	        	if (DEBUG) {  
				printf("Error opening device %s: video capture not supported.\n", str);
             		}

			return JNI_FALSE;
	     	}

	     	if (DEBUG) {

 	     		if ((capabilities.capabilities & V4L2_CAP_STREAMING) == 0) {
       	          		printf("%s does not support streaming i/o\n", str);
	     		}

   	     		if ((capabilities.capabilities & V4L2_CAP_READWRITE) == 0) {
                 		printf("%s does not support read i/o\n", str);
    	     		}

	             	enum_frame_formats(filedescriptor);
		}

		v4l2 = JNI_TRUE;
	
	     	name = (*env)->NewStringUTF(env, capabilities.card); 
	}	 

	(*env)->ReleaseStringUTFChars(env, device, str);

        // Attempt a V4L1 capabilities query
        if (ioctl(filedescriptor, VIDIOCGCAP, &capabilities_v4l1) >= 0) {
		v4l1 = JNI_TRUE;

             	if (v4l2 == JNI_FALSE) { 
	        	name = (*env)->NewStringUTF(env, capabilities_v4l1.name); 
	     	}
        }

        // Check if we were able to do any initialization at all
        if (v4l1 == JNI_FALSE && v4l2 == JNI_FALSE) { 
	     	close(filedescriptor);
	     	return JNI_FALSE;
        }

	// Inform Java of the device information we found
        (*env)->CallVoidMethod(env, this, mid, device, name, deviceNumber, v4l1, v4l2);
 
	// Close the device and free all resources
	close(filedescriptor);

	return JNI_TRUE;
}


