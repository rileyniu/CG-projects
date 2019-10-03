package ray1.camera;

import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

public class OrthographicCamera extends Camera {

    //TODO#Ray Task 1: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.
    public Vector3d u = new Vector3d();
    public Vector3d v = new Vector3d();
    public Vector3d w = new Vector3d();
    
    
    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#Ray Task 1:  Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
        // 2) Set up the helper variables if needed
        w.addMultiple(-1, this.getViewDir());
        w.normalize();
        Vector3d viewUpCopy = new Vector3d(this.getViewUp());
        u = viewUpCopy.clone().cross(w).normalize();
        v = w.clone().cross(u).normalize();
    }

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
    public void getRay(Ray outRay, float inU, float inV) {
        // TODO#Ray Task 1: Fill in this function.
        // 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
        //    instead of [0, 1]. Similarly, transform inV so that its range is
        //    [-vieHeight / 2, +viewHeight / 2]
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
        // 3) Set the direction field of outRay for an orthographic camera.
        double newU = - this.getViewWidth()/2+inU*this.getViewWidth();
        double newV = -this.getViewHeight()/2+inV*this.getViewHeight();

        Vector3d origin= new Vector3d(this.getViewPoint());
        origin.addMultiple(newU,u).addMultiple(newV,v);//e+uu+vv
        Vector3d rayDir = new Vector3d();
        rayDir.addMultiple(-1,w);  //-w

        outRay.set(origin,rayDir);
        outRay.makeOffsetRay();

    }

}
