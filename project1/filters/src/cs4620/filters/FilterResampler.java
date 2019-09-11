package cs4620.filters;

/**
 * A resampler that uses an arbitrary separable filter.  Resampling is done using the 2D filter
 *    f(x) f(y)
 * where f is the provided 1D filter.  This class will use appropriately sized filters for both
 * magnification and minification.
 * 
 * @author srm
 * @author rc844
 * @author Riley Niu,
 *
 * Warmup answers:
 *      1. Nearest neighbor:
 *          dst[1,1] = 1 * src[3,4] = 1
 *          dst[1,3] = 1 * src[3,5] = 0
 *      2. Linear interpolation:
 *          dst[0,3] = 0.12 * (0.64 * src[3,4]) =  0.0768
 *          dst[1,1] = 0.88 * (0.08 * src[3,3] + 0.92 * src[3,4]) + 0.12 *(0.08 * src[4,3] + 0.92 * src[4,4] = 1.0
 *          dst[1,2] = 0.6 * (0.12 * src[4,4] + 0.88 * src[3,4]) = 0.6
 *      3. B-spline cubic:
 *          dst[1,2] = 0.653*0.036*src[3,3] + 0.653*0.539*src[3,4] + 0.233*0.036*[4,3] + 0.233*0.539*src[4,4] = 0.5092

 */
public class FilterResampler implements ResampleEngine {

    Filter filter;

    /**
     * A new instance that uses the provided filter
     * @param filter
     */
    FilterResampler(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void resample(SimpleImage src, SimpleImage dst, double left, double bottom, double right, double top) {
        // TODO upsample(interpolate) and downsample image with arbitrary filter

        for (int i = 0; i <dst.getHeight();i++) {
            for (int j = 0; j < dst.getWidth(); j++) {

                // obtain pixel in source img
                double x = left+((float)j+0.5)*(right-left)/dst.getWidth();
                double y = bottom+((float)i+0.5)*(top-bottom)/dst.getHeight();

                if(x > src.getWidth()-1){
                    x = src.getWidth()-1;
                }else if(x<0){
                    x = 0;
                }

                if(y > src.getHeight()-1){
                    y = src.getHeight()-1;
                }else if(y<0){
                    y = 0;
                }

                // handle downsampling
                double radius_x = filter.radius();
                double radius_y = filter.radius();
                if(dst.getWidth()/src.getWidth() > 1){
                    radius_x = radius_x * ((right-left)/dst.getWidth());
                }
                if(dst.getHeight()/src.getHeight()>1){
                    radius_y = radius_y * ((top-bottom)/dst.getHeight());
                }

                double start_x = (x-radius_x) < 0 ? 0: x-radius_x;
                double start_y = (y-radius_y) < 0 ? 0: y-radius_y;
                double end_x = (x+radius_x) > src.getWidth() ? src.getWidth()-1: x+radius_x;
                double end_y = (y+radius_y) > src.getHeight() ? src.getHeight()-1: y+radius_y;

                float[] dst_outputs = new float[3];

                //(m,n) are all pixels fall in support of the filter
                for (int m = (int)start_y; m<=end_y; m++ ){
                    for (int n = (int)start_x; n<=end_x; n++ ){
                        float offset = (float)Math.sqrt(Math.pow(n-x,2)+Math.pow(m-y,2));
                        float value = filter.evaluate(offset);
                        dst_outputs[0] +=value*(src.getPixel(n,m,0) & 0xff);
                        dst_outputs[1] +=value*(src.getPixel(n,m,1) & 0xff);
                        dst_outputs[2] +=value*(src.getPixel(n,m,2) & 0xff);
                    }
                }

                dst.setPixel(j,i,0,(byte)Math.max(0,Math.min(dst_outputs[0],255)));
                dst.setPixel(j,i,1,(byte)Math.max(0,Math.min(dst_outputs[1],255)));
                dst.setPixel(j,i,2,(byte)Math.max(0,Math.min(dst_outputs[2],255)));
            }
        }

    }

}
