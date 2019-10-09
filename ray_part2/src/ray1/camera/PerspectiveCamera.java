package ray1.camera;

import egl.math.Matrix3;
import egl.math.Vector3;
import ray1.Ray;
import egl.math.Vector3d;

/**
 * Represents a camera with perspective view. For this camera, the view window
 * corresponds to a rectangle on a plane perpendicular to viewDir but at
 * distance projDistance from viewPoint in the direction of viewDir. A ray with
 * its origin at viewPoint going in the direction of viewDir should intersect
 * the center of the image plane. Given u and v, you should compute a point on
 * the rectangle corresponding to (u,v), and create a ray from viewPoint that
 * passes through the computed point.
 */
public class PerspectiveCamera extends Camera {

    protected float projDistance = 1.0f;
    public float getProjDistance() { return projDistance; }
    public void setprojDistance(float projDistance) {
        this.projDistance = projDistance;
    }


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
        // TODO#Ray Task 1: Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis,
        // based on viewDir and viewUp
        // 2) Set up the helper variables if needed
        //  set w parallel to v.p. normal, facing away from d
        //  – set u perpendicular to w and perpendicular to up-vector
        //  – set v perpendicular to w and u to form a right-handed ONB
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
        // 2) Set the origin field of outRay for a perspective camera.
        // 3) Set the direction field of outRay for an perspective camera. This
        //    should depend on your transformed inU and inV and your basis vectors,
        //    as well as the projection distance.
        double newU = - this.getViewWidth()/2+inU*this.getViewWidth();
        double newV = -this.getViewHeight()/2+inV*this.getViewHeight();

        Vector3d origin = new Vector3d(this.getViewPoint());
        Vector3d rayDir = new Vector3d();
        rayDir.addMultiple(-this.getProjDistance(),w).addMultiple(newU,u).addMultiple(newV,v);// –d w + u u + v v

        outRay.set(origin,rayDir);
        outRay.makeOffsetRay(); // avoid self intersection
    }
}