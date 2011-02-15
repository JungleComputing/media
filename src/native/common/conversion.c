#include "ibis_media.h"

#define KcrR 76284
#define KcrG 53281
#define KcbG 25625
#define KcbB 132252
#define Ky 76284

// Borrowed from 
// http://search.cpan.org/src/MLEHMANN/Video-Capture-V4l-0.901/RTjpeg/codec/modules/RTcolor_int.c

int convert(int source_fmt, unsigned char *src, int width, int height, int dest_fmt, unsigned char *dest) 
{
	if (source_fmt == DATA_YUV420 && dest_fmt == DATA_RGB32) {
		yuv2rgb32(src, dest, width, height);
		return 1;
 	} else if (source_fmt == DATA_YUV420 && dest_fmt == DATA_RGB24) {
		yuv2rgb24(src, dest, width, height);
		return 1;
	} else {
		// Unsupported conversion
	 	return -1;
	}
}

void yuvrgb32(unsigned char *buf, unsigned char *rgb, int width, int height)
{
	int tmp;
	int i, j;
	int y, crR, crG, cbG, cbB;
	unsigned char *bufcr, *bufcb, *bufy, *bufoute, *bufouto;
	int oskip, yskip;
 
	oskip = width*4;
	yskip = width;
 
	bufcb = &buf[width*height];
	bufcr = &buf[width*height+(width*height)/4];
	bufy = &buf[0];

	bufoute = rgb;
	bufouto = rgb+oskip;
 
	for(i=0; i<(height>>1); i++)
	{
		for(j=0; j<width; j+=2)
		{
			crR = (*bufcr-128)*KcrR;
			crG = (*(bufcr++)-128)*KcrG;
			cbG = (*bufcb-128)*KcbG;
			cbB = (*(bufcb++)-128)*KcbB;
  
			y = (bufy[j]-16)*Ky;
   
			tmp = (y+cbB) >> 16;
			*(bufoute++) = (tmp>255)?255:((tmp<0)?0:tmp);
			tmp = (y-crG-cbG) >> 16;
			*(bufoute++) = (tmp>255)?255:((tmp<0)?0:tmp);
			tmp = (y+crR) >> 16;
			*(bufoute++) = (tmp>255)?255:((tmp<0)?0:tmp);
			
			// set alpha to 255 (opaque)
			*(bufoute++) = 255;

			y=(bufy[j+1]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			*(bufoute++) = 255;

			y=(bufy[j+yskip]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			*(bufouto++) = 255;

			y=(bufy[j+1+yskip]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			*(bufouto++) = 255;
		}
		bufoute+=oskip;
		bufouto+=oskip;
		bufy+=yskip<<1;
	}
}


void yuvrgb24(unsigned char *buf, unsigned char *rgb, int width, int height)
{
	int tmp;
	int i, j; 
	int y, crR, crG, cbG, cbB;
	unsigned char *bufcr, *bufcb, *bufy, *bufoute, *bufouto;
	int oskip, yskip;
 
	oskip=width*3;
	yskip=width;
 
	bufcb=&buf[width*height];
	bufcr=&buf[width*height+(width*height)/4];
	bufy=&buf[0];
	bufoute=rgb;
	bufouto=rgb+oskip;
 
	for(i=0; i<(height>>1); i++)
	{
		for(j=0; j<width; j+=2)
		{
			crR=(*bufcr-128)*KcrR;
			crG=(*(bufcr++)-128)*KcrG;
			cbG=(*bufcb-128)*KcbG;
			cbB=(*(bufcb++)-128)*KcbB;
  
			y=(bufy[j]-16)*Ky;
   
			tmp=(y+cbB)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);

			y=(bufy[j+1]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufoute++)=(tmp>255)?255:((tmp<0)?0:tmp);

			y=(bufy[j+yskip]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);

			y=(bufy[j+1+yskip]-16)*Ky;

			tmp=(y+cbB)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y-crG-cbG)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
			tmp=(y+crR)>>16;
			*(bufouto++)=(tmp>255)?255:((tmp<0)?0:tmp);
   
		}
		bufoute+=oskip;
		bufouto+=oskip;
		bufy+=yskip<<1;
	}
}

