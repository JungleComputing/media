#include <streams.h>

struct __declspec(uuid("{0c20a766-2bd5-4b68-bc08-0f1d881b009b}")) CLSID_CapRenderer;

struct NamedGuid
{
    const GUID *guid;
    const TCHAR *name;
};

const NamedGuid rgng[] =
{
    {&MEDIASUBTYPE_AIFF, TEXT("MEDIASUBTYPE_AIFF\0")},
    {&MEDIASUBTYPE_AU, TEXT("MEDIASUBTYPE_AU\0")},
    {&MEDIASUBTYPE_AnalogVideo_NTSC_M, TEXT("MEDIASUBTYPE_AnalogVideo_NTSC_M\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_B, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_B\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_D, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_D\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_G, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_G\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_H, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_H\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_I, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_I\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_M, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_M\0")},
    {&MEDIASUBTYPE_AnalogVideo_PAL_N, TEXT("MEDIASUBTYPE_AnalogVideo_PAL_N\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_B, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_B\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_D, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_D\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_G, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_G\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_H, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_H\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_K, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_K\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_K1, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_K1\0")},
    {&MEDIASUBTYPE_AnalogVideo_SECAM_L, TEXT("MEDIASUBTYPE_AnalogVideo_SECAM_L\0")},

    {&MEDIASUBTYPE_ARGB1555, TEXT("MEDIASUBTYPE_ARGB1555\0")},
    {&MEDIASUBTYPE_ARGB4444, TEXT("MEDIASUBTYPE_ARGB4444\0")},
    {&MEDIASUBTYPE_ARGB32, TEXT("MEDIASUBTYPE_ARGB32\0")},
    {&MEDIASUBTYPE_A2R10G10B10, TEXT("MEDIASUBTYPE_A2R10G10B10\0")},
    {&MEDIASUBTYPE_A2B10G10R10, TEXT("MEDIASUBTYPE_A2B10G10R10\0")},

    {&MEDIASUBTYPE_AYUV, TEXT("MEDIASUBTYPE_AYUV\0")},
    {&MEDIASUBTYPE_AI44, TEXT("MEDIASUBTYPE_AI44\0")},
    {&MEDIASUBTYPE_IA44, TEXT("MEDIASUBTYPE_IA44\0")},
    {&MEDIASUBTYPE_NV12, TEXT("MEDIASUBTYPE_NV12\0")},
    {&MEDIASUBTYPE_IMC1, TEXT("MEDIASUBTYPE_IMC1\0")},
    {&MEDIASUBTYPE_IMC2, TEXT("MEDIASUBTYPE_IMC2\0")},
    {&MEDIASUBTYPE_IMC3, TEXT("MEDIASUBTYPE_IMC3\0")},
    {&MEDIASUBTYPE_IMC4, TEXT("MEDIASUBTYPE_IMC4\0")},

    {&MEDIASUBTYPE_Asf, TEXT("MEDIASUBTYPE_Asf\0")},
    {&MEDIASUBTYPE_Avi, TEXT("MEDIASUBTYPE_Avi\0")},
    {&MEDIASUBTYPE_CFCC, TEXT("MEDIASUBTYPE_CFCC\0")},
    {&MEDIASUBTYPE_CLJR, TEXT("MEDIASUBTYPE_CLJR\0")},
    {&MEDIASUBTYPE_CPLA, TEXT("MEDIASUBTYPE_CPLA\0")},
    {&MEDIASUBTYPE_CLPL, TEXT("MEDIASUBTYPE_CLPL\0")},
    {&MEDIASUBTYPE_DOLBY_AC3, TEXT("MEDIASUBTYPE_DOLBY_AC3\0")},
    {&MEDIASUBTYPE_DOLBY_AC3_SPDIF, TEXT("MEDIASUBTYPE_DOLBY_AC3_SPDIF\0")},
    {&MEDIASUBTYPE_DVCS, TEXT("MEDIASUBTYPE_DVCS\0")},
    {&MEDIASUBTYPE_DVD_LPCM_AUDIO, TEXT("MEDIASUBTYPE_DVD_LPCM_AUDIO\0")},
    {&MEDIASUBTYPE_DVD_NAVIGATION_DSI, TEXT("MEDIASUBTYPE_DVD_NAVIGATION_DSI\0")},
    {&MEDIASUBTYPE_DVD_NAVIGATION_PCI, TEXT("MEDIASUBTYPE_DVD_NAVIGATION_PCI\0")},
    {&MEDIASUBTYPE_DVD_NAVIGATION_PROVIDER, TEXT("MEDIASUBTYPE_DVD_NAVIGATION_PROVIDER\0")},
    {&MEDIASUBTYPE_DVD_SUBPICTURE, TEXT("MEDIASUBTYPE_DVD_SUBPICTURE\0")},
    {&MEDIASUBTYPE_DVSD, TEXT("MEDIASUBTYPE_DVSD\0")},
    {&MEDIASUBTYPE_DRM_Audio, TEXT("MEDIASUBTYPE_DRM_Audio\0")},
    {&MEDIASUBTYPE_DssAudio, TEXT("MEDIASUBTYPE_DssAudio\0")},
    {&MEDIASUBTYPE_DssVideo, TEXT("MEDIASUBTYPE_DssVideo\0")},
    {&MEDIASUBTYPE_IF09, TEXT("MEDIASUBTYPE_IF09\0")},
    {&MEDIASUBTYPE_IEEE_FLOAT, TEXT("MEDIASUBTYPE_IEEE_FLOAT\0")},
    {&MEDIASUBTYPE_IJPG, TEXT("MEDIASUBTYPE_IJPG\0")},
    {&MEDIASUBTYPE_IYUV, TEXT("MEDIASUBTYPE_IYUV\0")},
    {&MEDIASUBTYPE_Line21_BytePair, TEXT("MEDIASUBTYPE_Line21_BytePair\0")},
    {&MEDIASUBTYPE_Line21_GOPPacket, TEXT("MEDIASUBTYPE_Line21_GOPPacket\0")},
    {&MEDIASUBTYPE_Line21_VBIRawData, TEXT("MEDIASUBTYPE_Line21_VBIRawData\0")},
    {&MEDIASUBTYPE_MDVF, TEXT("MEDIASUBTYPE_MDVF\0")},
    {&MEDIASUBTYPE_MJPG, TEXT("MEDIASUBTYPE_MJPG\0")},
    {&MEDIASUBTYPE_MPEG1Audio, TEXT("MEDIASUBTYPE_MPEG1Audio\0")},
    {&MEDIASUBTYPE_MPEG1AudioPayload, TEXT("MEDIASUBTYPE_MPEG1AudioPayload\0")},
    {&MEDIASUBTYPE_MPEG1Packet, TEXT("MEDIASUBTYPE_MPEG1Packet\0")},
    {&MEDIASUBTYPE_MPEG1Payload, TEXT("MEDIASUBTYPE_MPEG1Payload\0")},
    {&MEDIASUBTYPE_MPEG1System, TEXT("MEDIASUBTYPE_MPEG1System\0")},
    {&MEDIASUBTYPE_MPEG1Video, TEXT("MEDIASUBTYPE_MPEG1Video\0")},
    {&MEDIASUBTYPE_MPEG1VideoCD, TEXT("MEDIASUBTYPE_MPEG1VideoCD\0")},
    {&MEDIASUBTYPE_MPEG2_AUDIO, TEXT("MEDIASUBTYPE_MPEG2_AUDIO\0")},
    {&MEDIASUBTYPE_MPEG2_PROGRAM, TEXT("MEDIASUBTYPE_MPEG2_PROGRAM\0")},
    {&MEDIASUBTYPE_MPEG2_TRANSPORT, TEXT("MEDIASUBTYPE_MPEG2_TRANSPORT\0")},
    {&MEDIASUBTYPE_MPEG2_VIDEO, TEXT("MEDIASUBTYPE_MPEG2_VIDEO\0")},
    {&MEDIASUBTYPE_None, TEXT("MEDIASUBTYPE_None\0")},
    {&MEDIASUBTYPE_Overlay, TEXT("MEDIASUBTYPE_Overlay\0")},
    {&MEDIASUBTYPE_PCM, TEXT("MEDIASUBTYPE_PCM\0")},
    {&MEDIASUBTYPE_PCMAudio_Obsolete, TEXT("MEDIASUBTYPE_PCMAudio_Obsolete\0")},
    {&MEDIASUBTYPE_Plum, TEXT("MEDIASUBTYPE_Plum\0")},
    {&MEDIASUBTYPE_QTJpeg, TEXT("MEDIASUBTYPE_QTJpeg\0")},
    {&MEDIASUBTYPE_QTMovie, TEXT("MEDIASUBTYPE_QTMovie\0")},
    {&MEDIASUBTYPE_QTRle, TEXT("MEDIASUBTYPE_QTRle\0")},
    {&MEDIASUBTYPE_QTRpza, TEXT("MEDIASUBTYPE_QTRpza\0")},
    {&MEDIASUBTYPE_QTSmc, TEXT("MEDIASUBTYPE_QTSmc\0")},
    {&MEDIASUBTYPE_RAW_SPORT, TEXT("MEDIASUBTYPE_RAW_SPORT\0")},
    {&MEDIASUBTYPE_RGB1, TEXT("MEDIASUBTYPE_RGB1\0")},
    {&MEDIASUBTYPE_RGB24, TEXT("MEDIASUBTYPE_RGB24\0")},
    {&MEDIASUBTYPE_RGB32, TEXT("MEDIASUBTYPE_RGB32\0")},
    {&MEDIASUBTYPE_RGB4, TEXT("MEDIASUBTYPE_RGB4\0")},
    {&MEDIASUBTYPE_RGB555, TEXT("MEDIASUBTYPE_RGB555\0")},
    {&MEDIASUBTYPE_RGB565, TEXT("MEDIASUBTYPE_RGB565\0")},
    {&MEDIASUBTYPE_RGB8, TEXT("MEDIASUBTYPE_RGB8\0")},
    {&MEDIASUBTYPE_SPDIF_TAG_241h, TEXT("MEDIASUBTYPE_SPDIF_TAG_241h\0")},
    {&MEDIASUBTYPE_TELETEXT, TEXT("MEDIASUBTYPE_TELETEXT\0")},
    {&MEDIASUBTYPE_TVMJ, TEXT("MEDIASUBTYPE_TVMJ\0")},
    {&MEDIASUBTYPE_UYVY, TEXT("MEDIASUBTYPE_UYVY\0")},
    {&MEDIASUBTYPE_VPVBI, TEXT("MEDIASUBTYPE_VPVBI\0")},
    {&MEDIASUBTYPE_VPVideo, TEXT("MEDIASUBTYPE_VPVideo\0")},
    {&MEDIASUBTYPE_WAKE, TEXT("MEDIASUBTYPE_WAKE\0")},
    {&MEDIASUBTYPE_WAVE, TEXT("MEDIASUBTYPE_WAVE\0")},
    {&MEDIASUBTYPE_Y211, TEXT("MEDIASUBTYPE_Y211\0")},
    {&MEDIASUBTYPE_Y411, TEXT("MEDIASUBTYPE_Y411\0")},
    {&MEDIASUBTYPE_Y41P, TEXT("MEDIASUBTYPE_Y41P\0")},
    {&MEDIASUBTYPE_YUY2, TEXT("MEDIASUBTYPE_YUY2\0")},
    {&MEDIASUBTYPE_YV12, TEXT("MEDIASUBTYPE_YV12\0")},
    {&MEDIASUBTYPE_YVU9, TEXT("MEDIASUBTYPE_YVU9\0")},
    {&MEDIASUBTYPE_YVYU, TEXT("MEDIASUBTYPE_YVYU\0")},
    {&MEDIASUBTYPE_YUYV, TEXT("MEDIASUBTYPE_YUYV\0")},
    {&MEDIASUBTYPE_dvhd, TEXT("MEDIASUBTYPE_dvhd\0")},
    {&MEDIASUBTYPE_dvsd, TEXT("MEDIASUBTYPE_dvsd\0")},
    {&MEDIASUBTYPE_dvsl, TEXT("MEDIASUBTYPE_dvsl\0")},
    {0, 0},
};

struct SupportedSubtype {
	AM_MEDIA_TYPE *type;
	char *name;
	int width;
	int height;
};

struct SimpleCapParams {
	BYTE * mTargetBuf;
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
