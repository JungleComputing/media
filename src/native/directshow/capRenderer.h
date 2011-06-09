#include <streams.h>

struct __declspec(uuid("{0c20a766-2bd5-4b68-bc08-0f1d881b009b}")) CLSID_CapRenderer;

struct SimpleCapParams
{
	int * mTargetBuf;
	int mWidth;
	int mHeight;
};

class CCapRenderer : public CBaseVideoRenderer
{
public:
    CCapRenderer(LPUNKNOWN pUnk, HRESULT *phr, int device);
    ~CCapRenderer();

public:
    HRESULT CheckMediaType(const CMediaType *pmt );     // Format acceptable?
    HRESULT SetMediaType(const CMediaType *pmt );       // Video format notification
    HRESULT DoRenderSample(IMediaSample *pMediaSample); // New video sample
    
	int mDevice;
	LONG m_lVidWidth;	// Video width
	LONG m_lVidHeight;	// Video Height
	LONG m_lVidPitch;	// Video Pitch
};