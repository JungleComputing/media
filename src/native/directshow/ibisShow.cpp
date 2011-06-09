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
int setupDone = 0;
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
int graphSetup = 0;
CComPtr<IGraphBuilder> graph;
CComPtr<ICaptureGraphBuilder2> capGraph;

//Control filter
CComPtr<IMediaControl> mediaControl;

//Buffer for samples
int *targetBuf;

//booleans for state
int captureRequest = 0;
int captureDone = 0;

HRESULT setup() 
{
	//Administration
	graphSetup = 0;

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
	if (hr == S_OK) 
	{
		hr = deviceEnumerator->CreateClassEnumerator(CLSID_VideoInputDeviceCategory, &enumMonikers, NULL);
		if (hr == S_FALSE) printf("Device Enum returned 0 devices");
		if (FAILED(hr)) printf("Device Enum failed\n");
		if (hr == S_OK) 
		{
			//get devices (max 8)
			numberOfDevices = 0;
			while (enumMonikers->Next(1, &moniker, 0) == S_OK)
			{	
				//Apparantly this is a valid device
				numberOfDevices++;				
			}
			enumMonikers->Reset();

			deviceNames = new char*[numberOfDevices];
			deviceMonikers = new IMoniker*[numberOfDevices];

			int devicenum = 0;
			while (enumMonikers->Next(1, &moniker, 0) == S_OK)
			{
				//Store the moniker
				deviceMonikers[devicenum] = moniker;

				//get properties
				hr = moniker->BindToStorage(0, 0, IID_IPropertyBag, (void**) &propBag);
				if (FAILED(hr)) printf("propbag init failed\n");
				if (SUCCEEDED(hr))
				{
					VariantInit(&deviceName);

					//get the description
					hr = propBag->Read(L"Description", &deviceName, 0);
					if (FAILED(hr)) hr = propBag->Read(L"FriendlyName", &deviceName, 0);
					if (SUCCEEDED(hr))
					{
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

	setupDone = 1;
	return hr;
}

HRESULT setupGraph() 
{
	HRESULT hr;
	graph = NULL;
	capGraph = NULL;
	mediaControl = NULL;	
	sinkFilter = NULL;

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

	// Render the preview pin on the video capture filter
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

	graphSetup = 1;

	return S_OK;    
}

HRESULT stopDevices() {
	//Stop the graph
	if (graphSetup == 1) {
		mediaControl->StopWhenReady();
	}	

	//Free memory
	currentDevice.mWidth = 0;
	currentDevice.mHeight = 0;

	if (currentDevice.mTargetBuf != 0) { 
		free(currentDevice.mTargetBuf);
		currentDevice.mTargetBuf = 0;
	}

	return S_OK;
}

HRESULT closeDevices() {
	stopDevices();	

	setupDone = 0;
		
	//Release all device monikers	
	for(int i=0; i<numberOfDevices; i++) {
		if (deviceMonikers[i] != NULL) deviceMonikers[i]->Release();		
	}

	// Release COM	
    CoUninitialize();

	return S_OK;
}

// ---------------------------- DirectShowDiscovery ---------------------------------

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDiscovery_countDevices(JNIEnv *env, jobject jo) {
	if (setupDone == 0) setup();
	
	return numberOfDevices;
}

JNIEXPORT jstring JNICALL Java_ibis_media_video_devices_directshow_DirectShowDiscovery_getDeviceName(JNIEnv *env, jobject jo, jint deviceNumber) {
	if (setupDone == 0) setup();
	
	if (deviceNumber < 0 || deviceNumber > numberOfDevices) {
		return  env->NewStringUTF("");
	}

	return env->NewStringUTF(deviceNames[deviceNumber]);
}

// ---------------------------- DirectShowDevice ---------------------------------

//Add a callback to the java class
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

int selectDevice(int deviceNumber) {
	if (deviceNumber < 0 || deviceNumber > numberOfDevices) {
		return -1;
	}

	IBaseFilter *filter;
	HRESULT hr = deviceMonikers[deviceNumber]->BindToObject(0,0,IID_IBaseFilter, (void**)&filter);
    if (FAILED(hr)) {
    	printf("bind failed: %X\n", hr);
		return hr;
    }
	
	// Copy the found filter pointer to the output parameter.
	if (SUCCEEDED(hr)) {
	    sourceFilter = filter;
		selectedDevice = deviceNumber;
	} else {
		printf("graph setup failed: %X\n", hr);
		return S_FALSE;
	}

	return 0;
}

HRESULT startCapture(int deviceNumber, SimpleCapParams *params) {
	if (setupDone == 0) setup();

	HRESULT hr = S_OK;

	if (graphSetup == 0) {
		hr = setupGraph();
		if (hr != S_OK)	{			
			printf("graph setup failed: %X\n", hr);
			return hr;
		}		
	}

	if (sourceFilter == NULL) {
		printf("source filter null.\n", hr);
		return S_FALSE;
	}
		
	hr = mediaControl->Run();
	
	return hr;	
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_configureDevice(JNIEnv *env, jobject jo, jint deviceNumber, jint width, jint height) {	
	stopDevices();	
	if (setupDone == 0) setup();

	jobject buffer;

	if (currentDevice.mWidth != 0) { 
		printf("configure did not select a legal device: %d", deviceNumber);
		return S_FALSE;
	} 

	currentDevice.mWidth = width;
	currentDevice.mHeight = height;
	currentDevice.mTargetBuf = (int *)malloc(width * height * sizeof(int));

	HRESULT hr;
	hr = selectDevice(deviceNumber);
	if (FAILED(hr)) {
		printf("select device failed, %X", hr);
		return hr;
	}

	hr = startCapture(deviceNumber, &currentDevice);
	if (FAILED(hr)) {
		printf("start capture failed, %X", hr);
		return hr;
	}

	buffer = env->NewDirectByteBuffer(currentDevice.mTargetBuf, (jlong) (width * height * sizeof(int)));
    
	return callbackAddBuffer(env, jo, buffer);	
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_grab(JNIEnv *env, jobject jo, jint deviceNumber) {
	if (setupDone == 0) return -1;

	captureRequest = 1;
	captureDone = 0;

	return 1;
}

JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_grabDone(JNIEnv *env, jobject jo, jint deviceNumber) {
	return captureDone;
}



JNIEXPORT jint JNICALL Java_ibis_media_video_devices_directshow_DirectShowDevice_closeDevice(JNIEnv *env, jobject jo, jint deviceNumber) {
	if (setupDone == 0) return 0;

	closeDevices();

	return 0;
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
HRESULT CCapRenderer::DoRenderSample( IMediaSample * pSample )
{	
	if (captureRequest == 0) return S_OK;

    int min=0x100, max=0;
    int sx = 0, sy = 0, wx, wy;

	//Get a pointer to the sample's bytebuffer
	BYTE * pBmpBuffer;
    pSample->GetPointer( &pBmpBuffer );

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

	captureRequest = 0;
	captureDone = 1;

    return S_OK;
}
