package manip;

import egl.math.*;
import gl.RenderObject;

public class RotationManipulator extends Manipulator {

	protected String meshPath = "Rotate.obj";

	public RotationManipulator(ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public RotationManipulator(RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	//assume X, Y, Z on stack in that order
	@Override
	protected Matrix4 getReferencedTransform() {
		Matrix4 m = new Matrix4();
		switch (this.axis) {
		case X:
			m.set(reference.rotationX).mulAfter(reference.translation);
			break;
		case Y:
			m.set(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
			break;
		case Z:
			m.set(reference.rotationZ)
			.mulAfter(reference.rotationY)
			.mulAfter(reference.rotationX)
			.mulAfter(reference.translation);
			break;
		}
		return m;
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#Manipulator: Modify this.reference.rotationX, this.reference.rotationY, or this.reference.rotationZ
		//   given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.
		
		Vector3 manipOrigin = getReferencedTransform().mulPos(new Vector3(0,0,0));
		Vector3 manipAxis;
		if(this.axis == ManipulatorAxis.X) {
			manipAxis = getReferencedTransform().mulDir(new Vector3(1,0,0));
		} else if(this.axis == ManipulatorAxis.Y) {
			manipAxis = getReferencedTransform().mulDir(new Vector3(0,1,0));
		} else {
			manipAxis = getReferencedTransform().mulDir(new Vector3(0,0,1));
		}
		
		Vector3 manipPlaneNorm = manipAxis.normalize();
		float d = manipPlaneNorm.dot(manipOrigin);
		
		Vector3 p1 = new Vector3(lastMousePos.x, lastMousePos.y, -1);
		Vector3 p2 = new Vector3(lastMousePos.x, lastMousePos.y, 1);
		Vector3 p3 = new Vector3(curMousePos.x, curMousePos.y, -1);
		Vector3 p4 = new Vector3(curMousePos.x, curMousePos.y, 1);
		Matrix4 mVPI = viewProjection.clone().invert();
		
		//mouse ray in world space
		mVPI.mulPos(p1);
		mVPI.mulPos(p2);
		mVPI.mulPos(p3);
		mVPI.mulPos(p4);
		Vector3 rayOrigin1 = p1;
		Vector3 rayDirection1 = p2.clone().sub(p1).normalize();
		Vector3 rayOrigin2 = p3;
		Vector3 rayDirection2 = p4.clone().sub(p3).normalize();
		
		//  If one or both of the rays is parallel to the plane, don't apply any transformation to the object.
		if ((rayDirection1.clone().dot(manipPlaneNorm)== 0)||(rayDirection2.clone().dot(manipPlaneNorm)==0)) {
			return;
		}
		
		float t1 = (d - manipPlaneNorm.dot(rayOrigin1))/manipPlaneNorm.dot(rayDirection1);
		float t2 = (d - manipPlaneNorm.dot(rayOrigin2))/manipPlaneNorm.dot(rayDirection2);
		Vector3 intersectedPt1 = rayOrigin1.clone().add(rayDirection1.clone().mul(t1));
		Vector3 intersectedPt2 = rayOrigin2.clone().add(rayDirection2.clone().mul(t2));
		Vector3 ray1 = intersectedPt1.clone().sub(manipOrigin).normalize();
		Vector3 ray2 = intersectedPt2.clone().sub(manipOrigin).normalize();
		
		float cos_angle = (ray1.clone().dot(ray2))/(ray1.len()*ray2.len());
		cos_angle = Math.min(1, Math.max(0, cos_angle));
		float angle = (float)Math.acos(cos_angle);
		
		//  use the plane's normal to find out if the angle is positive or negative
		float dir = ray1.clone().cross(ray2).dot(manipPlaneNorm);
		if (dir < 0 ) {
			angle = -angle;
		}

		if(this.axis == ManipulatorAxis.X) {
			this.reference.rotationX.mulAfter(Matrix4.createRotationX(angle));
		} else if(this.axis == ManipulatorAxis.Y) {
			this.reference.rotationY.mulAfter(Matrix4.createRotationY(angle));
		} else {
			this.reference.rotationZ.mulAfter(Matrix4.createRotationZ(angle));
		}

	}


	@Override
	protected String meshPath () {
		return "data/meshes/Rotate.obj";
	}
}
