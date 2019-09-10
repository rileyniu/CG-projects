package cs4620.filters;

/**
 * A resampler that uses nearest-neighbor interpolation.
 * 
 * @author srm
 * @author Riley Niu,
 */
public class NearestNeighborResampler implements ResampleEngine {

    @Override
    public void resample(SimpleImage src, SimpleImage dst, double left, double bottom, double right, double top) {
        // TODO nearest-neighbor sampling

        double delta_x = (right - left)/dst.getWidth();
        double delta_y = (top - bottom)/dst.getHeight();
        double offset_x = delta_x/2;
        double offset_y = delta_y/2;
        for (int i = 0; i <dst.getHeight();i++){
            for (int j = 0;j<dst.getWidth();j++){
                // rounded value

                int x = (int) Math.round(left+offset_x+j*delta_x);
                int y = (int) Math.round(bottom+offset_y+i*delta_y);

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
                dst.setPixel(j,i,0,src.getPixel(x,y,0));
                dst.setPixel(j,i,1,src.getPixel(x,y,1));
                dst.setPixel(j,i,2,src.getPixel(x,y,2));
            }
        }
        
    }

}
