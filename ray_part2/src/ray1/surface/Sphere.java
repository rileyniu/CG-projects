package ray1.surface;

import egl.math.Matrix3;
import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }
  
  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }
  
  protected final double M_2PI = 2 * Math.PI;
  
  public Sphere() { }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#Ray Task 2: fill in this function.
        Vector3d p = new Vector3d(rayIn.origin);
        Vector3d d = new Vector3d(rayIn.direction);
        p.sub(center);
        double tm = -1 * d.clone().dot(p);
	  	double delta = tm * tm - d.clone().lenSq() * (p.clone().lenSq() - radius*radius);
	  	
	 // If there was an intersection, fill out the intersection record
	  	if(delta >= 0){
  		  double t = 0;
		  double t1 = (tm - Math.sqrt(delta))/d.clone().lenSq();
		  double t2 = (tm + Math.sqrt(delta))/d.clone().lenSq();

	      // test if t is withn ray.start and ray.end
	      if (t1 > rayIn.start && t1 < rayIn.end) {
			  t = t1;
		  } else if (t2 > rayIn.start && t2 < rayIn.end) {
			  t = t2;
		  } else {
			  return false;
		  }
	      
	      Vector3d pos = new Vector3d();
	      rayIn.evaluate(pos,t);
	      outRecord.location.set(pos);
	      
	      Vector3d norm = new Vector3d();
	      norm.set(pos).sub(center).normalize();
	      outRecord.normal.set(norm);
	
	      double phi = Math.acos(norm.y/this.radius);
	      double theta = Math.atan2(norm.x, norm.z);
	      double u = (theta+Math.PI)/(Math.PI*2);
	      double v = (Math.PI-phi)/Math.PI;
	      outRecord.texCoords.set(u,v);
	      outRecord.surface = this;
	      outRecord.t = t;
	      return true;
          
        }
	    return false;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}