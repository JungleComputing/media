package ibis.media.imaging.decompression;

public class MJPEGDecompressor {

    private static final int RTjpeg_ZZ [] = {
    0,
    8, 1,
    2, 9, 16,
    24, 17, 10, 3,
    4, 11, 18, 25, 32,
    40, 33, 26, 19, 12, 5,
    6, 13, 20, 27, 34, 41, 48,
    56, 49, 42, 35, 28, 21, 14, 7,
    15, 22, 29, 36, 43, 50, 57,
    58, 51, 44, 37, 30, 23,
    31, 38, 45, 52, 59,
    60, 53, 46, 39,
    47, 54, 61,
    62, 55,
    63 };
    
    private static final long RTjpeg_aan_tab[] = {
    4294967296L, 5957222912L, 5611718144L, 5050464768L, 4294967296L, 3374581504L, 2324432128L, 1184891264L, 
    5957222912L, 8263040512L, 7783580160L, 7005009920L, 5957222912L, 4680582144L, 3224107520L, 1643641088L, 
    5611718144L, 7783580160L, 7331904512L, 6598688768L, 5611718144L, 4408998912L, 3036936960L, 1548224000L, 
    5050464768L, 7005009920L, 6598688768L, 5938608128L, 5050464768L, 3968072960L, 2733115392L, 1393296000L, 
    4294967296L, 5957222912L, 5611718144L, 5050464768L, 4294967296L, 3374581504L, 2324432128L, 1184891264L, 
    3374581504L, 4680582144L, 4408998912L, 3968072960L, 3374581504L, 2651326208L, 1826357504L, 931136000L, 
    2324432128L, 3224107520L, 3036936960L, 2733115392L, 2324432128L, 1826357504L, 1258030336L, 641204288L, 
    1184891264L, 1643641088L, 1548224000L, 1393296000L, 1184891264L, 931136000L, 641204288L, 326894240L, 
    };

    private static final int FIX_0_382683433 = 98;               /* FIX(0.382683433) */
    private static final int FIX_0_541196100 = 139;               /* FIX(0.541196100) */
    private static final int FIX_0_707106781 = 181;               /* FIX(0.707106781) */
    private static final int FIX_1_306562965 = 334;               /* FIX(1.306562965) */
    
    private static final int FIX_1_082392200 = 277;               /* FIX(1.082392200) */
    private static final int FIX_1_414213562 = 362;               /* FIX(1.414213562) */
    private static final int FIX_1_847759065 = 473;               /* FIX(1.847759065) */
    private static final int FIX_2_613125930 = 669;               /* FIX(2.613125930) */
    
    private static final int RTjpeg_ws[] = new int[64+31];
    
   // private byte [] RTjpeg_alldata = new byte[2*64+4*64+4*64+4*64+4*64+32];
    
    private short [] RTjpeg_block = new short[64];
    private int [] RTjpeg_lqt = new int[64];
    private int [] RTjpeg_cqt = new int[64];
    private long [] RTjpeg_liqt = new long[64];  // should be u32 ?
    private long [] RTjpeg_ciqt = new long[64];

    byte RTjpeg_lb8;
    byte RTjpeg_cb8;

   /*
    s16 *RTjpeg_block;
    s32 *RTjpeg_lqt;
    s32 *RTjpeg_cqt;
    u32 *RTjpeg_liqt;
    u32 *RTjpeg_ciqt;

    unsigned char RTjpeg_lb8;
    unsigned char RTjpeg_cb8;
*/

    
    
    private int RTjpeg_width, RTjpeg_height;

   //  s16 *RTjpeg_old=NULL;

    private int RTjpeg_lmask;
    private int RTjpeg_cmask;
    
    private int RTjpeg_mtest=0;

    private static final int RTjpeg_lum_quant_tbl[] = {
        16,  11,  10,  16,  24,  40,  51,  61,
        12,  12,  14,  19,  26,  58,  60,  55,
        14,  13,  16,  24,  40,  57,  69,  56,
        14,  17,  22,  29,  51,  87,  80,  62,
        18,  22,  37,  56,  68, 109, 103,  77,
        24,  35,  55,  64,  81, 104, 113,  92,
        49,  64,  78,  87, 103, 121, 120, 101,
        72,  92,  95,  98, 112, 100, 103,  99
     };

    private static final int RTjpeg_chrom_quant_tbl[] = {
        17,  18,  24,  47,  99,  99,  99,  99,
        18,  21,  26,  66,  99,  99,  99,  99,
        24,  26,  56,  99,  99,  99,  99,  99,
        47,  66,  99,  99,  99,  99,  99,  99,
        99,  99,  99,  99,  99,  99,  99,  99,
        99,  99,  99,  99,  99,  99,  99,  99,
        99,  99,  99,  99,  99,  99,  99,  99,
        99,  99,  99,  99,  99,  99,  99,  99
     };
     
 
    
    // private int RTjpeg_s2b(s16 *data, s8 *strm, u8 bt8, u32 *qtbl) { 
    private int RTjpeg_s2b(short [] data, byte [] strm, final int index, byte bt8, long [] qtbl) {

        int co;
        int ci=1, tmp;
       
        int i = RTjpeg_ZZ[0];

        data[i] = (short) (strm[index]*qtbl[i]);

        for(co=1; co<=bt8; co++) {
            i = RTjpeg_ZZ[co];
            data[i] = (short) (strm[index+(ci++)]*qtbl[i]);
        }

        for(; co<64; co++) {

            if (strm[ci]>63) {
                tmp = co+strm[index+ci]-63;

                for(; co<tmp; co++) { 
                    data[RTjpeg_ZZ[co]]=0;
                }
                co--;
            } else {
                i = RTjpeg_ZZ[co];
                data[i] = (short) (strm[index+ci]*qtbl[i]);
            }
            ci++;
        }
        return (int) ci;
    }
    
   
   
   

   
    
    //  #define DESCALE(x) (s16)( ((x)+4) >> 3)
    private static final int DESCALE(int x) { 
        return (x + 4) >> 3;
    }

    /* clip yuv to 16..235 (should be 16..240 for cr/cb but ... */
    // #define RL(x) ((x)>235) ? 235 : (((x)<16) ? 16 : (x))
    private static final int RL(int x) { 
        return ((x)>235) ? 235 : (((x)<16) ? 16 : (x));
    }

    // #define MULTIPLY(var,const)  (((s32) ((var) * (const)) + 128)>>8)
    private static final int MULTIPLY(int var, int cons) { 
        return ((var * cons) + 128) >> 8;
    }
    

    /*

       Main Routines

       This file contains most of the initialisation and control functions

       (C) Justin Schoeman 1998

     */

    /*

       Private function

       Initialise all the cache-aliged data blocks

     */

  
    /*

       External Function

       Re-set quality factor

       Input: buf -> pointer to 128 ints for quant values store to pass back to
       init_decompress.
       Q -> quality factor (192=best, 32=worst)
     */
/*
    void
    RTjpeg_init_Q (u8 Q)
    {
      int i;
      u64 qual;

      qual = (u64) Q << (32 - 7);       // 32 bit FP, 255=2, 0=0 

      for (i = 0; i < 64; i++)
        {
          RTjpeg_lqt[i] = (s32) ((qual / ((u64) RTjpeg_lum_quant_tbl[i] << 16)) >> 3);
          if (RTjpeg_lqt[i] == 0)
        RTjpeg_lqt[i] = 1;
          RTjpeg_cqt[i] = (s32) ((qual / ((u64) RTjpeg_chrom_quant_tbl[i] << 16)) >> 3);
          if (RTjpeg_cqt[i] == 0)
        RTjpeg_cqt[i] = 1;
          RTjpeg_liqt[i] = (1 << 16) / (RTjpeg_lqt[i] << 3);
          RTjpeg_ciqt[i] = (1 << 16) / (RTjpeg_cqt[i] << 3);
          RTjpeg_lqt[i] = ((1 << 16) / RTjpeg_liqt[i]) >> 3;
          RTjpeg_cqt[i] = ((1 << 16) / RTjpeg_ciqt[i]) >> 3;
        }

      RTjpeg_lb8 = 0;
      while (RTjpeg_liqt[RTjpeg_ZZ[++RTjpeg_lb8]] <= 8);
      RTjpeg_lb8--;
      RTjpeg_cb8 = 0;
      while (RTjpeg_ciqt[RTjpeg_ZZ[++RTjpeg_cb8]] <= 8);
      RTjpeg_cb8--;

      RTjpeg_dct_init ();
      RTjpeg_idct_init ();
      RTjpeg_quant_init ();
    }
*/
    /*

       External Function

       Initialise compression.

       Input: buf -> pointer to 128 ints for quant values store to pass back to 
       init_decompress.
       width -> width of image
       height -> height of image
       Q -> quality factor (192=best, 32=worst)

     */

  
    
    private void RTjpeg_init_data() {
     
   
      RTjpeg_block = new short[64];
      RTjpeg_lqt = new int[64];
      RTjpeg_cqt = new int[64];
      RTjpeg_liqt = new long[64];  // should be u32 ?
      RTjpeg_ciqt = new long[64];  // should be u32 ?
      
/*   
      unsigned long dptr;

      dptr = (unsigned long) &(RTjpeg_alldata[0]);
     
      dptr += 32;
      
      dptr = dptr >> 5;
      dptr = dptr << 5;         // cache align data 
      
      RTjpeg_block = (s16 *) dptr;
      dptr += sizeof (s16) * 64;
      
      RTjpeg_lqt = (s32 *) dptr;
      dptr += sizeof (s32) * 64;
      
      RTjpeg_cqt = (s32 *) dptr;
      dptr += sizeof (s32) * 64;
      
      RTjpeg_liqt = (u32 *) dptr;
      dptr += sizeof (u32) * 64;
      RTjpeg_ciqt = (u32 *) dptr;
      */
    }
    
    private void RTjpeg_color_init() {
    }  
    
    private void RTjpeg_idct_init() {

        for(int i=0; i<64; i++) {
            RTjpeg_liqt[i]= ((long) RTjpeg_liqt[i]*RTjpeg_aan_tab[i])>>32;
            RTjpeg_ciqt[i]= ((long) RTjpeg_ciqt[i]*RTjpeg_aan_tab[i])>>32;
        }
    }

    public void RTjpeg_init_decompress(byte [] buf, int width, int height) {
   
      RTjpeg_init_data ();

      RTjpeg_width = width;
      RTjpeg_height = height;

      for(int i=0;i<64; i++) {
          RTjpeg_liqt[i] = buf[i];
          RTjpeg_ciqt[i] = buf[i + 64];
      }

      RTjpeg_lb8 = 0;
      
      while (RTjpeg_liqt[RTjpeg_ZZ[++RTjpeg_lb8]] <= 8);
      
      RTjpeg_lb8--;
      RTjpeg_cb8 = 0;
 
      while (RTjpeg_ciqt[RTjpeg_ZZ[++RTjpeg_cb8]] <= 8);
      RTjpeg_cb8--;

      RTjpeg_idct_init();
      RTjpeg_color_init ();
    }

   // void RTjpeg_idct(u8 *odata, s16 *data, int rskip) {

    private void RTjpeg_idct(byte [] odata, final int index, short [] data, int rskip) {
    
        int tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
        int tmp10, tmp11, tmp12, tmp13;
        int z5, z10, z11, z12, z13;

        /*
      s16 *inptr;
      s32 *wsptr;
      u8 *outptr;
         */

        int dcval;
        int [] workspace = new int[64];

        /*
      inptr = data;
      wsptr = workspace;
         */

        int inptr = 0;
        int wsptr = 0;
        int outptr = index;

        for (int ctr = 8; ctr > 0; ctr--) {


            //if ((inptr[8] | inptr[16] | inptr[24] | inptr[32] | inptr[40] | inptr[48] | inptr[56]) == 0) {

            if ((data[inptr+8] | data[inptr+16] | data[inptr+24] | data[inptr+32] | data[inptr+40] | data[inptr+48] | data[inptr+56]) == 0) {

                // dcval = inptr[0];
                dcval = data[inptr];

                /*
              wsptr[0] = dcval;
              wsptr[8] = dcval;
              wsptr[16] = dcval;
              wsptr[24] = dcval;
              wsptr[32] = dcval;
              wsptr[40] = dcval;
              wsptr[48] = dcval;
              wsptr[56] = dcval;
                 */
                workspace[wsptr+0] = dcval;
                workspace[wsptr+8] = dcval;
                workspace[wsptr+16] = dcval;
                workspace[wsptr+24] = dcval;
                workspace[wsptr+32] = dcval;
                workspace[wsptr+40] = dcval;
                workspace[wsptr+48] = dcval;
                workspace[wsptr+56] = dcval;

                inptr++;      
                wsptr++;
                continue;
            } 

            /*
          tmp0 = inptr[0];
          tmp1 = inptr[16];
          tmp2 = inptr[32];
          tmp3 = inptr[48];
             */

            tmp0 = data[inptr+0];
            tmp1 = data[inptr+16];
            tmp2 = data[inptr+32];
            tmp3 = data[inptr+48];

            tmp10 = tmp0 + tmp2;
            tmp11 = tmp0 - tmp2;

            tmp13 = tmp1 + tmp3;
            tmp12 = MULTIPLY(tmp1 - tmp3, FIX_1_414213562) - tmp13;

            tmp0 = tmp10 + tmp13;
            tmp3 = tmp10 - tmp13;
            tmp1 = tmp11 + tmp12;
            tmp2 = tmp11 - tmp12;

            /*
        tmp4 = inptr[8];
        tmp5 = inptr[24];
        tmp6 = inptr[40];
        tmp7 = inptr[56];
             */

            tmp4 = data[inptr+8];
            tmp5 = data[inptr+24];
            tmp6 = data[inptr+40];
            tmp7 = data[inptr+56];

            z13 = tmp6 + tmp5;
            z10 = tmp6 - tmp5;
            z11 = tmp4 + tmp7;
            z12 = tmp4 - tmp7;

            tmp7 = z11 + z13;
            tmp11 = MULTIPLY(z11 - z13, FIX_1_414213562);

            z5 = MULTIPLY(z10 + z12, FIX_1_847759065);
            tmp10 = MULTIPLY(z12, FIX_1_082392200) - z5;
            tmp12 = MULTIPLY(z10, - FIX_2_613125930) + z5;

            tmp6 = tmp12 - tmp7;
            tmp5 = tmp11 - tmp6;
            tmp4 = tmp10 + tmp5;

            /*
        wsptr[0] = (s32) (tmp0 + tmp7);
        wsptr[56] = (s32) (tmp0 - tmp7);
        wsptr[8] = (s32) (tmp1 + tmp6);
        wsptr[48] = (s32) (tmp1 - tmp6);
        wsptr[16] = (s32) (tmp2 + tmp5);
        wsptr[40] = (s32) (tmp2 - tmp5);
        wsptr[32] = (s32) (tmp3 + tmp4);
        wsptr[24] = (s32) (tmp3 - tmp4);
             */

            workspace[wsptr+0]  = (tmp0 + tmp7);
            workspace[wsptr+56] = (tmp0 - tmp7);
            workspace[wsptr+8]  = (tmp1 + tmp6);
            workspace[wsptr+48] = (tmp1 - tmp6);
            workspace[wsptr+16] = (tmp2 + tmp5);
            workspace[wsptr+40] = (tmp2 - tmp5);
            workspace[wsptr+32] = (tmp3 + tmp4);
            workspace[wsptr+24] = (tmp3 - tmp4);

            inptr++;
            wsptr++;
        }

        //wsptr = workspace;
        wsptr = 0;
        
        for (int ctr = 0; ctr < 8; ctr++) {

            // outptr = &(odata[ctr*rskip]);
            
            outptr = ctr*rskip;

            /*
            tmp10 = wsptr[0] + wsptr[4];
            tmp11 = wsptr[0] - wsptr[4];
            tmp13 = wsptr[2] + wsptr[6];
            
            tmp12 = MULTIPLY(wsptr[2] - wsptr[6], FIX_1_414213562) - tmp13;
            */
            
            tmp10 = workspace[wsptr+0] + workspace[wsptr+4];
            tmp11 = workspace[wsptr+0] - workspace[wsptr+4];
            tmp13 = workspace[wsptr+2] + workspace[wsptr+6];

            tmp12 = MULTIPLY(workspace[wsptr+2] - workspace[wsptr+6], FIX_1_414213562) - tmp13;

            tmp0 = tmp10 + tmp13;
            tmp3 = tmp10 - tmp13;
            tmp1 = tmp11 + tmp12;
            tmp2 = tmp11 - tmp12;

            z13 = workspace[wsptr+5] + workspace[wsptr+3];
            z10 = workspace[wsptr+5] - workspace[wsptr+3];
            z11 = workspace[wsptr+1] + workspace[wsptr+7];
            z12 = workspace[wsptr+1] - workspace[wsptr+7];

            tmp7 = z11 + z13;
            tmp11 = MULTIPLY(z11 - z13, FIX_1_414213562);

            z5 = MULTIPLY(z10 + z12, FIX_1_847759065);
            tmp10 = MULTIPLY(z12, FIX_1_082392200) - z5;
            tmp12 = MULTIPLY(z10, - FIX_2_613125930) + z5;

            tmp6 = tmp12 - tmp7;
            tmp5 = tmp11 - tmp6;
            tmp4 = tmp10 + tmp5;

            odata[outptr+0] = (byte) RL(DESCALE(tmp0 + tmp7));
            odata[outptr+7] = (byte) RL(DESCALE(tmp0 - tmp7));
            odata[outptr+1] = (byte) RL(DESCALE(tmp1 + tmp6));
            odata[outptr+6] = (byte) RL(DESCALE(tmp1 - tmp6));
            odata[outptr+2] = (byte) RL(DESCALE(tmp2 + tmp5));
            odata[outptr+5] = (byte) RL(DESCALE(tmp2 - tmp5));
            odata[outptr+4] = (byte) RL(DESCALE(tmp3 + tmp4));
            odata[outptr+3] = (byte) RL(DESCALE(tmp3 - tmp4));

            wsptr += 8;
        }
    }

    
    
    public void RTjpeg_decompress (byte [] sp, byte [] bp) {
      
        int spIndex = 0;
        int bpIndex = 0;
        
        /* Y */
        for (int i=0; i<RTjpeg_height; i+= 8) {
          for (int j=0; j<RTjpeg_width; j+= 8) {
              if (sp[spIndex] == -1) {
                  spIndex++;
              } else {
                  spIndex += RTjpeg_s2b(RTjpeg_block, sp, spIndex, RTjpeg_lb8, RTjpeg_liqt);
                  RTjpeg_idct(bp, bpIndex + j, RTjpeg_block, RTjpeg_width);
              }
          }
          bpIndex += RTjpeg_width << 3;
        }
    
        /* Cr */
        for (int i=0; i < (RTjpeg_height >> 1); i += 8) {
            for (int j=0; j < (RTjpeg_width >> 1); j += 8) {
                if (sp[spIndex] == -1) {
                    spIndex++;
                } else {
                    spIndex += RTjpeg_s2b (RTjpeg_block, sp, spIndex, RTjpeg_cb8, RTjpeg_ciqt);
                    RTjpeg_idct (bp, bpIndex + j, RTjpeg_block, RTjpeg_width >> 1);
                }
            }
            bpIndex += RTjpeg_width << 2;
        }
    
        /* Cb */
        for (int i=0; i<(RTjpeg_height >> 1); i+= 8) {
            for (int j=0; j<(RTjpeg_width >> 1); j+= 8) {
                if (sp[spIndex] == -1) {
                    spIndex++;
                } else
                {
                    spIndex += RTjpeg_s2b (RTjpeg_block, sp, spIndex, RTjpeg_cb8, RTjpeg_ciqt);
                    RTjpeg_idct (bp, bpIndex + j, RTjpeg_block, RTjpeg_width >> 1);
                }
            }
            bpIndex += RTjpeg_width << 2;
        }
    }
   

}
