package cs4620.filters;

/**
 * A resampler that uses bilinear interpolation.
 * 
 * @author srm
 * @author rc844
 * @author Riley Niu (hn263), Adrian Zheng (yz388)
 */

public class LinearResampler implements ResampleEngine {

    @Override
    public void resample(SimpleImage src, SimpleImage dst, double left, double bottom, double right, double top) {
    	// TODO implement linear interpolation

        double delta_x = (right - left)/dst.getWidth();
        double delta_y = (top - bottom)/dst.getHeight();
        double offset_x = delta_x/2;
        double offset_y = delta_y/2;
        for (int i = 0; i <dst.getHeight();i++){
            for (int j = 0;j<dst.getWidth();j++){
                // rounded value

                double x_lb = left+offset_x+j*delta_x;
                double y_lb = bottom+offset_y+i*delta_y;


                if(x_lb+1 > src.getWidth()-1){
                    x_lb = src.getWidth()-2;
                }else if(x_lb<0){
                    x_lb = 0;
                }

                if(y_lb +1 > src.getHeight()-1){
                    y_lb = src.getHeight()-2;
                }else if(y_lb<0){
                    y_lb = 0;
                }

                byte[] outputs = new byte[3];

                outputs = bilinearInterpolation(src, x_lb, y_lb, (int)x_lb, (int)y_lb); // cast to int == math.floor()
                dst.setPixel(j,i,0,outputs[0]);
                dst.setPixel(j,i,1,outputs[1]);
                dst.setPixel(j,i,2,outputs[2]);
            }
        }

    }

    /**
     * perform a bilinear interpolation
     *
     * @param x_lb  left bottom pixel x-coordinate
     * @param y_lb  left bottom pixel y-coordinate
     * @param x  output pixel x-coordinate
     * @param y  output pixel y-coordinate
     */
    private byte[] bilinearInterpolation(SimpleImage src, double x, double y, int x_lb, int y_lb)
    {
        byte[] outputs = new byte[3];
        double lamda_x = x-x_lb;
        double lamda_y = y-y_lb;

        for (int c = 0;c<3;c++){

            int pixel_lb = src.getPixel(x_lb,y_lb,c)& 0xff;
            int pixel_rb = src.getPixel(x_lb+1,y_lb,c)& 0xff;
            int pixel_lt = src.getPixel(x_lb,y_lb+1,c)& 0xff;
            int pixel_rt = src.getPixel(x_lb+1,y_lb+1,c)& 0xff;
            int midpoint_b = (int)Math.round((1-lamda_x) * pixel_lb + lamda_x * pixel_rb);
            int midpoint_t = (int)Math.round((1-lamda_x) * pixel_lt + lamda_x * pixel_rt);
            outputs[c] = (byte)(int)((1-lamda_y) * midpoint_b + lamda_y * midpoint_t);
        }
        return outputs;

    }
}
