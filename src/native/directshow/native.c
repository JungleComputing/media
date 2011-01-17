
#include <windows.h>
#include <jni.h>

#include "escapi.h"

#include "ibis_video4j_devices_directshow_DirectShowDiscovery.h"
#include "ibis_video4j_devices_directshow_DirectShowDevice.h"

#define MAX_DEVICES 8

countCaptureDevicesProc countCaptureDevices;
initCaptureProc initCapture;
deinitCaptureProc deinitCapture;
doCaptureProc doCapture;
isCaptureDoneProc isCaptureDone;
getCaptureDeviceNameProc getCaptureDeviceName;
ESCAPIDLLVersionProc ESCAPIDLLVersion;

/* Internal: initialize COM */
typedef void (*initCOMProc)();
initCOMProc initCOM;

int initialized = 0;
struct SimpleCapParams devices[MAX_DEVICES];

int setupESCAPI()
{
  /* Load DLL dynamically */
  HMODULE capdll = LoadLibrary("escapi.dll");
  if (capdll == NULL) {
  	printf("Could not load escapi.dll, error = %ul", GetLastError());
    return 0;
  }

  /* Fetch function entry points */
  countCaptureDevices = (countCaptureDevicesProc)GetProcAddress(capdll, "countCaptureDevices");
  initCapture = (initCaptureProc)GetProcAddress(capdll, "initCapture");
  deinitCapture = (deinitCaptureProc)GetProcAddress(capdll, "deinitCapture");
  doCapture = (doCaptureProc)GetProcAddress(capdll, "doCapture");
  isCaptureDone = (isCaptureDoneProc)GetProcAddress(capdll, "isCaptureDone");
  initCOM = (initCOMProc)GetProcAddress(capdll, "initCOM");
  getCaptureDeviceName = (getCaptureDeviceNameProc)GetProcAddress(capdll, "getCaptureDeviceName");
  ESCAPIDLLVersion = (ESCAPIDLLVersionProc)GetProcAddress(capdll, "ESCAPIDLLVersion");

  /* Check that we got all the entry points */
  if (initCOM == NULL ||
      ESCAPIDLLVersion == NULL ||
      getCaptureDeviceName == NULL ||
      countCaptureDevices == NULL ||
      initCapture == NULL ||
      deinitCapture == NULL ||
      doCapture == NULL ||
      isCaptureDone == NULL)
      return 0;

  /* Verify DLL version */
  if (ESCAPIDLLVersion() != 0x200)
    return 0;

  /* Initialize COM.. */
  initCOM();

  /* and return the number of capture devices found. */
  return countCaptureDevices();
}

JNIEXPORT jint JNICALL Java_ibis_video4j_devices_directshow_DirectShowDiscovery_countDevices
 (JNIEnv *env, jobject this)
{
	if (initialized == 0) { 
		initialized = 1;
		memset(devices, 0, MAX_DEVICES*sizeof(struct SimpleCapParams));
		return setupESCAPI();
	}

	return countCaptureDevices();
}

JNIEXPORT jstring JNICALL Java_ibis_video4j_devices_directshow_DirectShowDiscovery_getDeviceName
 (JNIEnv *env, jobject this, jint device)
{
	char buffer[1024];

	buffer[0] = 0;

	getCaptureDeviceName(device, buffer, 1024);

	if (buffer[0] == 0) { 
		return NULL;
	} else { 
		return (*env)->NewStringUTF(env, buffer);
	}
}

int callbackAddBuffer(JNIEnv *env, jobject this, jobject buffer) 
{ 
        jmethodID mid;
        jclass clazz;

        clazz = (*env)->GetObjectClass(env, this); 

        if (clazz == NULL) { 
            return -2;
        }

        mid = (*env)->GetMethodID(env, clazz, "addBuffer", "(Ljava/nio/ByteBuffer;)V");

        if (mid == NULL) { 
            return -3;
        }       

        (*env)->CallVoidMethod(env, this, mid, buffer); 

        return 1;
}

JNIEXPORT jint JNICALL Java_ibis_video4j_devices_directshow_DirectShowDevice_configureDevice
 (JNIEnv *env, jobject this, jint device, jint width, jint height)
{
	int res; 
	jobject buffer;

	if (devices[device].mWidth != 0) { 
		return -1;
	} 

	devices[device].mWidth = width;
	devices[device].mHeight = height;
	devices[device].mTargetBuf = malloc(width * height * sizeof(int));

	res = initCapture(device, &devices[device]);

	if (res == 1) {
		buffer = (*env)->NewDirectByteBuffer(env, devices[device].mTargetBuf, (jlong) (width * height * sizeof(int)));
                return callbackAddBuffer(env, this, buffer);
	} else { 
		return -4;
	}
}

JNIEXPORT jint JNICALL Java_ibis_video4j_devices_directshow_DirectShowDevice_grab
 (JNIEnv *env, jobject this, jint device)
{
	if (devices[device].mWidth == 0) { 
		return 0;
	} 

	doCapture(device);
	return 1;
}	

JNIEXPORT jint JNICALL Java_ibis_video4j_devices_directshow_DirectShowDevice_grabDone
 (JNIEnv *env, jobject this, jint device)
{
	if (devices[device].mWidth == 0) { 
		return -1;
	} 

	return isCaptureDone(device);
}	

JNIEXPORT jint JNICALL Java_ibis_video4j_devices_directshow_DirectShowDevice_closeDevice
 (JNIEnv *env, jobject this, jint device)
{
	if (devices[device].mWidth == 0) { 
		return 0;
	} 

	deinitCapture(device);

	devices[device].mWidth = 0;
	devices[device].mHeight = 0;

	if (devices[device].mTargetBuf != 0) { 
		free(devices[device].mTargetBuf);
		devices[device].mTargetBuf = 0;
	}

	return 1;
}
