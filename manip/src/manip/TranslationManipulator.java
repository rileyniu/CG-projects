package manip;

import egl.math.*;
import gl.RenderObject;

public class TranslationManipulator extends Manipulator {
	// hard coded mesh for now


	public TranslationManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public TranslationManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.translation given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.

		// A3 SOLUTION START
		
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
			this.reference.translation.mulAfter(Matrix4.createTranslation(new Vector3(t2-t1,0,0)));
		} else if(this.axis == ManipulatorAxis.Y) {
			this.reference.translation.mulAfter(Matrix4.createTranslation(new Vector3(0,t2-t1,0)));
		} else {
			this.reference.translation.mulAfter(Matrix4.createTranslation(new Vector3(0,0,t2-t1)));
		}
		
		// A3 SOLUTION END
	}

	@Override
	protected String meshPath () {
		return "data/meshes/Translate.obj";
	}

}
