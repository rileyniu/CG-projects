/**
 * @author Jimmy, Andrew 
 */

package splines;
import java.util.ArrayList;

import egl.math.Matrix4;
import egl.math.Vector2;

public class CatmullRom extends SplineCurve {

	public CatmullRom(ArrayList<Vector2> controlPoints, boolean isClosed,
			float epsilon) throws IllegalArgumentException {
		super(controlPoints, isClosed, epsilon);
	}

	@Override
	public CubicBezier toBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3,
			float eps) {
		//TODO A6
		Matrix4 catRomBasis= new Matrix4( 
				-1/2f,  3/2f, -3/2f,  1/2f,
		           1, -5/2f,    2f, -1/2f,
		       -1/2f,     0,  1/2f,     0,
		           0,    1f,     0,     0);

		Matrix4 bezBasis = new Matrix4 (
					-1,  3, -3, 1,
					 3, -6,  3, 0,
					-3,  3,  0, 0, 
					 1,  0,  0, 0);
		
		Matrix4 catRomPoints = new Matrix4(
					p0.x, p0.y, 0, 0,
				    p1.x, p1.y, 0, 0,
				    p2.x, p2.y, 0, 0,
				    p3.x, p3.y, 0, 0);
		
		
		Matrix4 bezPoints = bezBasis.invert().mulBefore(catRomBasis.mulBefore(catRomPoints));
		
		Vector2 bezp0 = new Vector2(bezPoints.m[0], bezPoints.m[4]);
		Vector2 bezp1 = new Vector2(bezPoints.m[1], bezPoints.m[5]);
		Vector2 bezp2 = new Vector2(bezPoints.m[2], bezPoints.m[6]);
		Vector2 bezp3 = new Vector2(bezPoints.m[3], bezPoints.m[7]);
		
		return new CubicBezier(bezp0, bezp1, bezp2, bezp3, eps);
		//return new CubicBezier(new Vector2(), new Vector2(), new Vector2(), new Vector2(), eps);
		//END SOLUTION
	}
}
