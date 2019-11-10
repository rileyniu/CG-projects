package manip;

import egl.math.*;
import gl.RenderObject;

public class ScaleManipulator extends Manipulator {

	public ScaleManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public ScaleManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.scale)
				.mulAfter(reference.rotationZ)
				.mulAfter(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.scale given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.

		// A3 SOLUTION BEGIN
		
		// calculate manip plane in world space
		Vector3 manipOrigin = getReferencedTransform().mulPos(new Vector3(0,0,0));
		Vector3 manipAxis;
		if(this.axis == ManipulatorAxis.X) {
			manipAxis = getReferencedTransform().mulDir(new Vector3(1,0,0));
		} else if(this.axis == ManipulatorAxis.Y) {
			manipAxis = getReferencedTransform().mulDir(new Vector3(0,1,0));
		} else {
			manipAxis = getReferencedTransform().mulDir(new Vector3(0,0,1));
		}
		
		// calculate mouse ray and t value
		float t1 = getAxisT(manipOrigin, manipAxis, viewProjection,lastMousePos);
		float t2 = getAxisT(manipOrigin, manipAxis, viewProjection,curMousePos);
		if(this.axis == ManipulatorAxis.X) {
			this.reference.scale.mulAfter(Matrix4.createScale(t2/t1,1.0f,1.0f));
		} else if(this.axis == ManipulatorAxis.Y) {
			this.reference.scale.mulAfter(Matrix4.createScale(1.0f,t2/t1,1.0f));
		} else {
			this.reference.scale.mulAfter(Matrix4.createScale(1.0f,1.0f,t2/t1));;
		}

		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Scale.obj";
	}

}
