package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;

import java.util.ArrayList;

import egl.math.Vector2;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** The face that contains this triangle */
  OBJFace face = null;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

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
	  double g = rayIn.direction.x;
	  double h = rayIn.direction.y;
	  double i = rayIn.direction.z;
	  Vector3 v0 = owner.getMesh().getPosition(face,0);
	  double j = v0.x - rayIn.origin.x;
	  double k = v0.y - rayIn.origin.y;
	  double l = v0.z - rayIn.origin.z;
	  
	  // in-triangle test by cramer rule
	  double detA = a*(e*i-f*h) - b*(d*i - f*g) + c*(d*h - e*g);
	  double t = -(f*(a*k -j*b) + e*(j*c - a*l) + d*(b*l - k*c))/detA;
	  if ((t<rayIn.start) || (t> rayIn.end)) {
		  return false;
	  }
	  double gamma = (i*(a*k - j*b) + h*(j*c - a*l) + g*(b*l - k*c))/detA;
	  if ((gamma<0) || (gamma> 1)) {
		  return false;
	  }
	  double beta = (j*(e*i - h*f) + k*(g*f - d*i) + l*(d*h - e*g))/detA;
	  if ((beta<0) || (beta> 1-gamma)) {
		  return false;
	  }
	    
	  // If there was an intersection, fill out the intersection record
	  
	  // set location
	  Vector3d pos = new Vector3d();
      rayIn.evaluate(pos,t);
      outRecord.location.set(pos);
      
      // set normals
      if(this.face.hasNormals()) {
    	  Vector3 na = this.owner.getMesh().getNormal(this.face, 0);
    	  Vector3 nb = this.owner.getMesh().getNormal(this.face, 1);
    	  Vector3 nc = this.owner.getMesh().getNormal(this.face, 2);
    	  double alpha = 1-gamma - beta;
    	  double nx =alpha * na.x + beta * nb.x +gamma* nc.x;
    	  double ny =alpha * na.y + beta * nb.y +gamma* nc.y;
    	  double nz =alpha * na.z + beta * nb.z +gamma* nc.z;
    	  outRecord.normal.set(new Vector3d(nx,ny,nz).normalize());
    	  
      }else {
    	  outRecord.normal.set(this.norm);
      }
      
      // set uvs
      if (face.hasUVs()) {
		  Vector2 uv0 = owner.getMesh().getUV(face, 0);
		  Vector2 uv1 = owner.getMesh().getUV(face, 1);
		  Vector2 uv2 = owner.getMesh().getUV(face, 2);
		  Vector2d uv = new Vector2d();
		  uv.addMultiple((1 - beta - gamma), uv0).addMultiple(beta, uv1).addMultiple(gamma, uv2);
		  outRecord.texCoords.set(uv);
	  }
	  outRecord.t = t;
	  outRecord.surface = this;
	  return true;
	  
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}