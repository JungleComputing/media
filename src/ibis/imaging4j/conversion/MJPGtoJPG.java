package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class MJPGtoJPG extends Convertor {

    private static final int COST = 10; 
    
    private static final byte [] DHT_DATA = {
        (byte) 0xff, (byte) 0xc4, (byte) 0x01, (byte) 0xa2, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
        (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02,
        (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x01, (byte) 0x00, (byte) 0x03,
        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09,
        (byte) 0x0a, (byte) 0x0b, (byte) 0x10, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x03, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x05,
        (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x7d, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x04,
        (byte) 0x11, (byte) 0x05, (byte) 0x12, (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x06, (byte) 0x13, (byte) 0x51, (byte) 0x61, (byte) 0x07, (byte) 0x22,
        (byte) 0x71, (byte) 0x14, (byte) 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xa1, (byte) 0x08, (byte) 0x23, (byte) 0x42, (byte) 0xb1, (byte) 0xc1, (byte) 0x15,
        (byte) 0x52, (byte) 0xd1, (byte) 0xf0, (byte) 0x24, (byte) 0x33, (byte) 0x62, (byte) 0x72, (byte) 0x82, (byte) 0x09, (byte) 0x0a, (byte) 0x16, (byte) 0x17,
        (byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2a, (byte) 0x34, (byte) 0x35, (byte) 0x36,
        (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a,
        (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66,
        (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6a, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7a,
        (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95,
        (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xa6, (byte) 0xa7, (byte) 0xa8,
        (byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xc2,
        (byte) 0xc3, (byte) 0xc4, (byte) 0xc5, (byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4, (byte) 0xd5,
        (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe1, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7,
        (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8, (byte) 0xf9,
        (byte) 0xfa, (byte) 0x11, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x04, (byte) 0x03, (byte) 0x04, (byte) 0x07, (byte) 0x05,
        (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x77, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x11, (byte) 0x04,
        (byte) 0x05, (byte) 0x21, (byte) 0x31, (byte) 0x06, (byte) 0x12, (byte) 0x41, (byte) 0x51, (byte) 0x07, (byte) 0x61, (byte) 0x71, (byte) 0x13, (byte) 0x22,
        (byte) 0x32, (byte) 0x81, (byte) 0x08, (byte) 0x14, (byte) 0x42, (byte) 0x91, (byte) 0xa1, (byte) 0xb1, (byte) 0xc1, (byte) 0x09, (byte) 0x23, (byte) 0x33,
        (byte) 0x52, (byte) 0xf0, (byte) 0x15, (byte) 0x62, (byte) 0x72, (byte) 0xd1, (byte) 0x0a, (byte) 0x16, (byte) 0x24, (byte) 0x34, (byte) 0xe1, (byte) 0x25,
        (byte) 0xf1, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x26, (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2a, (byte) 0x35, (byte) 0x36,
        (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a,
        (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66,
        (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6a, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7a,
        (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94,
        (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xa6, (byte) 0xa7,
        (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba,
        (byte) 0xc2, (byte) 0xc3, (byte) 0xc4, (byte) 0xc5, (byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4,
        (byte) 0xd5, (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7,
        (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8, (byte) 0xf9, (byte) 0xfa
    };
    
    public MJPGtoJPG() {
        super(COST);
    }
    
    private boolean isHuffmann(ByteBuffer buf) {

        int i = 0;
        int index = 0;

        int tmp = (((0xff & buf.get(index)) << 8) | (0xff & buf.get(index+1)));

        while (tmp != 0xffda) {
            if (i++ > 2048) {
                return false;
            }

            if (tmp == 0xffc4) {
                return true;
            }

            index++;

            tmp = (((0xff & buf.get(index)) << 8) | (0xff & buf.get(index+1)));
        }

        return false;
    }
    
    @Override
    public Image convert(Image in, Image out) throws ConversionException {
    
        ByteBuffer tmp = in.getData();
        int size = tmp.limit();

        try { 
            if (!isHuffmann(tmp)) { 
                // We need to insert the hufmann table

    System.out.println("!ishufmann");
                
                ByteBuffer dst = null;

                if (out == null) { 

                    dst = ByteBuffer.allocate(tmp.limit() + DHT_DATA.length);
                    out = new Image(Format.JPG, in.getWidth(), in.getHeight(), dst);

                } else { 

                    dst = out.getData();

                    if (dst.capacity() < (size + DHT_DATA.length)) { 
                        throw new ConversionException("Destination image too small!");
                    }
                }

                dst.position(0);
                dst.limit(dst.capacity());
                
                // Find the place to insert the DHT

                int index = 0;

                int value = ((0xff & tmp.get(index)) << 8) | (0xff & tmp.get(index+1));

                while (value != 0xffc0) {
                    if (++index > size-1) { 
                        throw new ConversionException("Failed to insert DHT into MJPEG");
                    }

                    value = ((0xff & tmp.get(index)) << 8) | (0xff & tmp.get(index+1));
                }

                System.out.println("Index = " + index);
                
                tmp.position(0);
                tmp.limit(index);

                dst.put(tmp);
                dst.put(DHT_DATA);

                tmp.limit(size);
                tmp.position(index);
                
                dst.put(tmp);

                dst.position(0);
                dst.limit(size + DHT_DATA.length);
                
            } else { 
                // We can directly copy the data since it already contains a jpg file.

    System.out.println("ishufmann");
                
                ByteBuffer dst = null;

                if (out == null) { 

                    dst = ByteBuffer.allocate(tmp.limit());
                    out = new Image(Format.JPG, in.getWidth(), in.getHeight(), dst);

                } else { 

                    dst = out.getData();

                    if (dst.capacity() < size) { 
                        throw new ConversionException("Destination image too small!");
                    }

                    dst.position(0);
                    dst.limit(dst.capacity());
                }

                dst.put(tmp);
            
                dst.position(0);
                dst.limit(size);
            }
            
        } finally { 
            // reset the original image
            tmp.position(0);
            tmp.limit(size);
        }
        
        return out;
    }
    
    
    /*
     int is_huffman(unsigned char *buf)
{
  unsigned char *ptbuf;
  int i = 0;
  ptbuf = buf;
  while (((ptbuf[0] << 8) | ptbuf[1]) != 0xffda) {
    if (i++ > 2048)
      return 0;
    if (((ptbuf[0] << 8) | ptbuf[1]) == 0xffc4)
      return 1;
    ptbuf++;
  }
  return 0;
}

int memcpy_picture(unsigned char *out, unsigned char *buf, int size)
{
  unsigned char *ptdeb, *ptlimit, *ptcur = buf;
  int sizein, pos=0;

  if (!is_huffman(buf)) {

printf("!ishuffman\n");

    ptdeb = ptcur = buf;
    ptlimit = buf + size;

    while ((((ptcur[0] << 8) | ptcur[1]) != 0xffc0) && (ptcur < ptlimit)) {
      ptcur++;
    }

    if (ptcur >= ptlimit) {
        return pos;
    }

    sizein = ptcur - ptdeb;

    memcpy(out+pos, buf, sizein);
    pos += sizein;

    memcpy(out+pos, dht_data, sizeof(dht_data));
    pos += sizeof(dht_data);

    memcpy(out+pos, ptcur, size - sizein);
    pos += size-sizein;
  } else {
printf("ishuffman\n");

    memcpy(out+pos, ptcur, size);
    pos += size;
  }
  return pos;
}
*/

}
