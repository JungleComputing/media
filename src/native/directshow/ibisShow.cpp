#include "capRenderer.h"
#include <windows.h>
#include <jni.h>
#include <atlbase.h>
#include <dshow.h>
#include <comutil.h>

#include "ibis_media_video_devices_directshow_DirectShowDiscovery.h"
#include "ibis_media_video_devices_directshow_DirectShowDevice.h"

#pragma comment(lib, "kernel32")
#pragma comment(lib, "user32")

#pragma comment(lib, "winmm")
#pragma comment(lib, "strmbase")
#pragma comment(lib, "strmiids")
//#pragma comment(lib, "quartz")

#pragma comment(lib, "ole32")
#pragma comment(lib, "oleaut32")
#pragma comment(lib, "comsuppw")
#pragma comment(lib, "odbc32")
#pragma comment(lib, "odbccp32")
#pragma comment(lib, "gdi32")
#pragma comment(lib, "winspool")
#pragma comment(lib, "comdlg32")
#pragma comment(lib, "advapi32")
#pragma comment(lib, "shell32")

//Device enumerator stuff
int numberOfDevices;
char **deviceNames;
IMoniker **deviceMonikers;
struct SimpleCapParams currentDevice;

//Selected Source filter
CComPtr<IBaseFilter> sourceFilter;
int selectedDevice = 0;

//Destination filter
CComPtr<IBaseFilter> sinkFilter;

//Capture graph stuff
CComPtr<IGraphBuilder> graph;
CComPtr<ICaptureGraphBuilder2> capGraph;

//Control filter
CComPtr<IMediaControl> mediaControl;

//Buffer for samples
int *targetBuf;

//state
enum states { OFF, UNINITIALIZED, STOPPED, STARTED };
enum bufferstates { NONE, GRAB_REQUESTED, GRAB_DONE };
states state = OFF;
bufferstates bufferState = NONE;

//Grab callback
static JavaVM *jvm = NULL;
JNIEnv *callingEnv;
jobject callingObj;


HRESULT setup() 
{
	//Administration
	if (state == UNINITIALIZED) return S_OK;
	else if (state != OFF) {
		printf("setup called in the wrong state\n");
		return -1;
	}

	//initialize COM
	CoInitialize(NULL);
	
	HRESULT hr = S_OK;
	VARIANT deviceName;
	LONGLONG start=0, stop=MAXLONGLONG;
	
	CComPtr<ICreateDevEnum>		deviceEnumerator	= NULL;
	CComPtr<IEnumMoniker>		enumMonikers		= NULL;
	CComPtr<IMoniker>			moniker				= NULL;
	CComPtr<IPropertyBag>		propBag				= NULL;
	
	//create an enumerator for video input devices
	hr = CoCreateInstance(CLSID_SystemDeviceEnum, NULL, CLSCTX_INPROC_SERVER, IID_ICreateDevEnum, (void**) &deviceEnumerator);
	if (FAILED(hr)) 
	{
		printf("CoCreate failed with hr = %X\n", hr);
	}
	if (hr == S_OK) {
		hr = deviceEnumerator->CreateClassEnumerator(CLSID_VideoInputDeviceCategory, &enumMonikers, NULL);
		if (hr == S_FALSE) printf("Device Enum returned 0 devices");
		if (FAILED(hr)) printf("Device Enum failed\n");
		if (hr == S_OK) {
			//get devices (max 8)
			numberOfDevices = 0;
			while (enumMonikers->Next(1, &moniker, 0) == S_OK) {
				//Apparantly this is a valid device
				numberOfDevices++;				
			}
			enumMonikers->Reset();

			deviceNames = new char*[numberOfDevices];
			deviceMonikers = new IMoniker*[numberOfDevices];

			int devicenum = 0;
			while (enumMonikers->Next(1, &moniker, 0) == S_OK) {
				//Store the moniker
				deviceMonikers[devicenum] = moniker;

				//get properties
				hr = moniker->BindToStorage(0, 0, IID_IPropertyBag, (void**) &propBag);
				if (FAILED(hr)) printf("propbag init failed\n");
				if (SUCCEEDED(hr)) {
					VariantInit(&deviceName);

					//get the description
					hr = propBag->Read(L"Description", &deviceName, 0);
					if (FAILED(hr)) hr = propBag->Read(L"FriendlyName", &deviceName, 0);
					if (SUCCEEDED(hr)) {
						BSTR ptr = deviceName.bstrVal;
						char* resultString = _com_util::ConvertBSTRToString(ptr);
						deviceNames[devicenum] = new char[40];
						strncpy(deviceNames[devicenum], resultString, 39);
					}
				}
				devicenum++;
			}
		}
	}	

	state = UNINITIALIZED;
	return hr;
}

HRESULT setupGraph() {
	if (state == STOPPED) return S_OK;
	else if (state != UNINITIALIZED) {
		printf("setupGraph called in the wrong state\n");
		return -1;
	}

	HRESULT hr;
	graph = NULL;
	capGraph = NULL;
	mediaControl = NULL;	
	sinkFilter = NULL;

	//Setup the source filter
	IBaseFilter *filter;
	hr = deviceMonikers[selectedDevice]->BindToObject(0,0,IID_IBaseFilter, (void**)&filter);
	if (FAILED(hr)) {
		printf("bind failed: %X\n", hr);
		return hr;
	}

	// Copy the found filter pointer.
	if (SUCCEEDED(hr)) {
		sourceFilter = filter;
	} else {
		printf("graph setup failed: %X\n", hr);
		return S_FALSE;
	}

	// Create the filter graph
    hr = CoCreateInstance (CLSID_FilterGraph, NULL, CLSCTX_INPROC, IID_IGraphBuilder, (void **) &graph);
    if (FAILED(hr)) { 
		 printf("setupGraph failed at create: %X\n", hr); 
		 return hr;
	}

	// Create the Cap Renderer object
    CCapRenderer *renderer = new CCapRenderer(NULL, &hr, selectedDevice);
    if (FAILED(hr)) { 
		printf("setupGraph could not create Cap renderer object!  hr=0x%x\n", hr); 
		return E_FAIL;
	}

	sinkFilter = renderer;
    // Get a pointer to the IBaseFilter on the CapRenderer, add it to graph	
    if (FAILED(hr = graph->AddFilter(sinkFilter, L"CAPRENDERER"))) { 
		 printf("setupGraph could not add renderer filter to graph!  hr=0x%x\n", hr); 
		 return hr;
	}

    // Create the capture graph builder
    hr = CoCreateInstance (CLSID_CaptureGraphBuilder2 , NULL, CLSCTX_INPROC, IID_ICaptureGraphBuilder2, (void **) &capGraph);
    if (FAILED(hr)) { 
		 printf("setupGraph failed at create capture: %X\n", hr); 
		 return hr;
	}

	//Attach capture graph to filter graph
	hr = capGraph->SetFiltergraph(graph);
    if (FAILED(hr)) { 
		 printf("setupGraph failed at set: %X\n", hr); 
		 return hr;
	}

	// Add Capture filter to our graph.
	hr = graph->AddFilter(sourceFilter, L"Video Capture");
    if (FAILED(hr)) { 
		 printf("setupGraph failed at add: %X\n", hr); 
		 return hr;
	}

	// Render the capture pin on the video capture filter
	hr = capGraph->RenderStream (&PIN_CATEGORY_CAPTURE, &MEDIATYPE_Video, sourceFilter, NULL, sinkFilter);
    if (FAILED(hr)) { 
		 printf("setupGraph failed at render: %X\n", hr);
		 return hr;
	}

	// Obtain interface for media control
    hr = graph->QueryInterface(IID_IMediaControl,(LPVOID *) &mediaControl);
    if (FAILED(hr)) { 
		 printf("setupGraph failed at query: %X\n", hr); 
		 return hr;
	}

    state = STOPPED;

	return S_OK;    
}

HRESULT stopDevices() {
	if (state == STOPPED) return S_OK;
	else if (state != STARTED) {
		printf("stopDevices called in the wrong state\n");
		return -1;
	}

	mediaControl->StopWhenReady();

	//unbind the source filter
	sourceFilter = NULL;

	//Free memory
	currentDevice.mWidth = 0;
	currentDevice.mHeight = 0;

	if (currentDevice.mTargetBuf != 0) { 
		free(currentDevice.mTargetBuf);
		currentDevice.mTargetBuf = 0;
	}

	state = STOPPED;

	return S_OK;
}

HRESULT closeDevices() {
	HRESULT hr = S_OK;

	if (state == OFF) return S_OK;
	else if (state == STARTED) {
		hr = stopDevices();
		if (FAILED(hr)) return hr;
	}
	else if (state != STOPPED) {
		printf("closeDevices called in the wrong state\n");
		return -1;
	}
		
	//Release all device monikers	
	for(int i=0; i<numberOfDevices; i++) {
		if (deviceMonikers[i] != NULL) deviceMonikers[i]->Release();		
	}

	// Release COM	
    CoUninitialize();

    state = OFF;

	return S_OK;
}

// ---------------------------- DirectShowDiscovery ---------------------------------

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDiscovery_countDevices(JNIEnv *env, jobject jo) {
	HRESULT hr = setup();
	if (FAILED(hr)) return hr;
	
	return numberOfDevices;
}

JNIEXPORT jstring JNICALL Java_ibis_media_video_devices_directshow_DirectShowDiscovery_getDeviceName(JNIEnv *env, jobject jo, jint deviceNumber) {
	HRESULT hr = setup();
	if (FAILED(hr)) return env->NewStringUTF("");
	
	if (deviceNumber < 0 || deviceNumber > numberOfDevices) {
		return  env->NewStringUTF("");
	}

	return env->NewStringUTF(deviceNames[deviceNumber]);
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_getDeviceCapabilities(JNIEnv *env, jobject jo, jint deviceNumber) {
	if (state != STOPPED) {
		printf("getDeviceCapabilities called in the wrong state\n");
		return false;
	}

	CComPtr<IAMStreamConfig> config = NULL;
	HRESULT hr = capGraph->FindInterface(
	    &PIN_CATEGORY_PREVIEW, 	// Preview pin.
	    &MEDIATYPE_Video,    	// Only video types
	    sourceFilter,			// Pointer to the capture filter.
	    IID_IAMStreamConfig,
	    (void**)&config);
	if (FAILED(hr)) {
		printf("FindInterface failed\n");
		return -1;
	}

	int count = 0;
	int size = 0;
	hr = config->GetNumberOfCapabilities(&count, &size);
	if (FAILED(hr)) {
		printf("GetNumberOfCapabilities failed\n");
		return -1;
	} else {
		// Check the size to make sure we pass in the correct structure.
		if (size == sizeof(VIDEO_STREAM_CONFIG_CAPS)) {
		    // Use the video capabilities structure.
		    for (int format = 0; format < count; format++)
		    {
		        VIDEO_STREAM_CONFIG_CAPS scc;
		        AM_MEDIA_TYPE *mediatype;
		        hr = config->GetStreamCaps(format, &mediatype, (BYTE*)&scc);
		        if (SUCCEEDED(hr))
		        {
		        	if (mediatype->majortype == MEDIATYPE_Video) {
		        		if (mediatype->subtype == MEDIASUBTYPE_RGB24) {
		        			printf("RGB24 available \n");
		        	    } else if (mediatype->subtype == MEDIASUBTYPE_RGB32) {
		        	    	printf("RGB32 available \n");
		        	    } else if (mediatype->subtype == MEDIASUBTYPE_ARGB32) {
		        	    	printf("ARGB32 available \n");
		        	    } else if (mediatype->subtype == MEDIASUBTYPE_AYUV) {
		        	    	printf("AYUV available \n");
		        	    }
		        	}
		            /* Examine the format, and possibly use it. */


		            // Delete the media type when you are done.
		            DeleteMediaType(mediatype);
		        } else {
		        	printf("GetStreamCaps failed\n");
		        	return -1;
		        }
		    }
		}
	}

}


// ---------------------------- DirectShowDevice ---------------------------------

//Add a callback to the java class telling  the location of
HRESULT callbackAddBuffer(JNIEnv *env, jobject jo, jobject buffer) {
        jmethodID mid;
        jclass clazz;

        clazz = env->GetObjectClass(jo); 

        if (clazz == NULL) { 
            return -2;
        }

        mid = env->GetMethodID(clazz, "addBuffer", "(Ljava/nio/ByteBuffer;)V");

        if (mid == NULL) { 
            return -3;
        }       

        env->CallVoidMethod(jo, mid, buffer); 

        return S_OK;
}

HRESULT changeSz(int width, int height) {
	HRESULT hr;

	//Get the configuration interface
	CComPtr<IAMStreamConfig> config;
	hr = capGraph->FindInterface(&PIN_CATEGORY_CAPTURE,
										 &MEDIATYPE_Video, sourceFilter,
										 IID_IAMStreamConfig, (void **)&config);
	if (FAILED(hr)) {
		printf("FindInterface failed, %X", hr);
		return hr;
	}

	//Get the current media format
	AM_MEDIA_TYPE *type;
	config->GetFormat(&type);
	if (FAILED(hr)) {
		printf("GetFormat failed, %X", hr);
		return hr;
	}

	//Leave everything but the size alone
	if(type->formattype == FORMAT_VideoInfo) {
		VIDEOINFOHEADER *pvi = (VIDEOINFOHEADER *)type->pbFormat;
		pvi->bmiHeader.biWidth = width;
		pvi->bmiHeader.biHeight = height;
		pvi->bmiHeader.biSizeImage = DIBSIZE(pvi->bmiHeader);

		hr = config->SetFormat(type);
		if (FAILED(hr)) {
			printf("SetFormat failed, %X", hr);
			return hr;
		}
	}

	return S_OK;
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_configureDevice(JNIEnv *env, jobject jo, jint deviceNumber, jint width, jint height) {	
	HRESULT hr;

	if (deviceNumber < 0 || deviceNumber > numberOfDevices) {
		printf("illegal device selected\n");
		return -1;
	}

	if (state == STOPPED) return S_OK;
	if (state == OFF) {
		hr = setup();
		if (FAILED(hr)) return hr;
	}
	if (state != UNINITIALIZED) {
		printf("configureDevice called in the wrong state\n");
		return false;
	}

	selectedDevice = deviceNumber;
	hr = setupGraph();
	if (FAILED(hr)) return hr;

	jobject buffer;

	currentDevice.mWidth = width;
	currentDevice.mHeight = height;
	currentDevice.mTargetBuf = (int *)malloc(width * height * sizeof(int));

	changeSz(width, height);
	if (FAILED(hr)) {
		printf("changeSz failed, %X", hr);
		return hr;
	}

	buffer = env->NewDirectByteBuffer(currentDevice.mTargetBuf, (jlong) (width * height * sizeof(int)));

	state = STOPPED;

	return callbackAddBuffer(env, jo, buffer);
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_startDevice(JNIEnv *env, jobject jo, jint deviceNumber) {
	if (deviceNumber < 0 || deviceNumber > numberOfDevices) {
			printf("illegal device selected\n");
			return -1;
		}
	if (state == STARTED && selectedDevice == deviceNumber) return S_OK;
	if (state == STARTED && selectedDevice != deviceNumber) {
		stopDevices();
	}
	if (state != STOPPED) {
		printf("startDevice called in the wrong state\n");
		return false;
	}

	HRESULT hr = mediaControl->Run();
	if (FAILED(hr)) {
		printf("run failed, %X", hr);
		return hr;
	}

	state = STARTED;
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_changeSize(JNIEnv *env, jobject jo, jint width, jint height) {
	return changeSz(width, height);
}

JNIEXPORT jboolean JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_grabBuffer(JNIEnv *env, jobject jo) {
	if (state != STARTED) return false;

	if (jvm == NULL) env->GetJavaVM(&jvm);

	callingObj = jo;
	bufferState = GRAB_REQUESTED;
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_closeDevice(JNIEnv *env, jobject jo) {
	if (state == OFF) return S_OK;

	HRESULT hr = closeDevices();
	if (FAILED(hr)) return hr;

	return S_OK;
}

//-----------------------------------------------------------------------------
// CCapRenderer constructor
//-----------------------------------------------------------------------------
CCapRenderer::CCapRenderer( LPUNKNOWN pUnk, HRESULT *phr, int device ) : CBaseVideoRenderer(__uuidof(CLSID_CapRenderer), _T("Cap Renderer"), pUnk, (long *)phr)
{    
	mDevice = device;
    *phr = S_OK;
}

//-----------------------------------------------------------------------------
// CCapRenderer destructor
//-----------------------------------------------------------------------------
CCapRenderer::~CCapRenderer()
{
    // Do nothing
}


//-----------------------------------------------------------------------------
// CheckMediaType: This method forces the graph to give us an R8G8B8 video
// type, making our copy to textfx4 trivial.
//-----------------------------------------------------------------------------
HRESULT CCapRenderer::CheckMediaType(const CMediaType *pmt)
{
    HRESULT   hr = E_FAIL;
    VIDEOINFO *pvi;
    
    // Reject the connection if this is not a video type
    if( *pmt->FormatType() != FORMAT_VideoInfo ) {
        return E_INVALIDARG;
    }
    
    // Only accept RGB24
    pvi = (VIDEOINFO *)pmt->Format();
    if(IsEqualGUID( *pmt->Type(), MEDIATYPE_Video) && IsEqualGUID( *pmt->Subtype(), MEDIASUBTYPE_RGB24) )
    {
        hr = S_OK;
    }
    
    return hr;
}


//-----------------------------------------------------------------------------
// SetMediaType: Graph connection has been made. 
//-----------------------------------------------------------------------------
HRESULT CCapRenderer::SetMediaType(const CMediaType *pmt)
{
	// Get the bitmap info header
    VIDEOINFO *pviBmp;                      
    pviBmp = (VIDEOINFO *)pmt->Format();

	// Retrieve the size of this media type
    m_lVidWidth  = pviBmp->bmiHeader.biWidth;
    m_lVidHeight = abs(pviBmp->bmiHeader.biHeight);

	//Allocate the target buffer
	currentDevice.mTargetBuf = new int[m_lVidWidth*m_lVidHeight];

	// We are forcing RGB24
    m_lVidPitch = (m_lVidWidth * 3 + 3) & ~(3);
    
	return S_OK;
}


//-----------------------------------------------------------------------------
// DoRenderSample: A sample has been delivered. Copy it to the buffer.
//-----------------------------------------------------------------------------

//callback to the java class telling it when the grab is finished
//HRESULT callbackGrabDone(JNIEnv *jenv, jobject buffer) {
HRESULT callbackGrabDone(JNIEnv *jenv) {
	//Retrieve the object from the JVM and tell it we are done.
	jmethodID mid;
	jclass clazz;

	clazz = jenv->GetObjectClass(callingObj);
	if (clazz == NULL) {
		printf("GetObjectClass failed.\n");
		return -1;
	}

	//mid = jenv->GetMethodID(clazz, "grabDone", "(Ljava/nio/ByteBuffer;)V");
	mid = jenv->GetMethodID(clazz, "grabDone", "()V");
	if (mid == NULL) {
		printf("GetMethodID failed.\n");
		return -2;
	}

	//jenv->CallVoidMethod(callingObj, mid, buffer);
	jenv->CallVoidMethod(callingObj, mid);

	return S_OK;
}

HRESULT CCapRenderer::DoRenderSample( IMediaSample * pSample )
{	
	//Attach the current thread (the directshow one) to the JVM if it hasn't been done yet
	JNIEnv* jenv;
	int res = jvm->AttachCurrentThread((void **)&jenv, NULL);
	if (res < 0) {
		fprintf(stderr, "Attach failed\n");
		return false;
	}

	if (bufferState == GRAB_REQUESTED) {
		int min=0x100, max=0;
		int sx = 0, sy = 0, wx, wy;

		//Get a pointer to the sample's bytebuffer
		BYTE * pBmpBuffer;
		pSample->GetPointer( &pBmpBuffer );
		//int size = pSample->GetSize();

		//Attach the current buffer to the java object
		//jobject buffer;
		//buffer = jenv->NewDirectByteBuffer(currentDevice.mTargetBuf, (jlong) size);
		//callbackAddBuffer(jenv, callingObj, buffer);


		//Use the width and height given
		wx = currentDevice.mWidth;
		wy = currentDevice.mHeight;

		for (int i=0,c=0;i<wy;i++)
		{
			for (int j=0;j<wx;j++,c++)
			{
				int cb=pBmpBuffer[(((wy-i-1)*m_lVidHeight/wy)*m_lVidPitch+((j*m_lVidWidth/wx)*3))+0]&0xff;
				int cg=pBmpBuffer[(((wy-i-1)*m_lVidHeight/wy)*m_lVidPitch+((j*m_lVidWidth/wx)*3))+1]&0xff;
				int cr=pBmpBuffer[(((wy-i-1)*m_lVidHeight/wy)*m_lVidPitch+((j*m_lVidWidth/wx)*3))+2]&0xff;

				currentDevice.mTargetBuf[c] =	((cr)<<16)+
												((cg)<<8) +
												((cb)) | 0xff000000;
			}
		}


		HRESULT hr = callbackGrabDone(jenv);
		if (hr != S_OK) {
			printf("callback error %X\n", hr);
			return S_FALSE;
		}


		bufferState = GRAB_DONE;
	}

    return S_OK;
}
