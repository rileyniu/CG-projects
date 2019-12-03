package anim;

import java.util.TreeSet;

import common.SceneObject;
import egl.math.Matrix3;
import egl.math.Matrix4;
import egl.math.Quat;
import egl.math.Vector3;

/**
 * A timeline for a particular object in the scene.  The timeline holds
 * a sequence of keyframes and a reference to the object that they
 * pertain to.  Via linear interpolation between keyframes, the timeline
 * can compute the object's transformation at any point in time.
 * 
 * @author Cristian
 */
public class AnimTimeline {

    /**
     * A sorted set of keyframes.  Invariant: there is at least one keyframe.
     */
    public final TreeSet<AnimKeyframe> frames = new TreeSet<>(AnimKeyframe.COMPARATOR);

    /**
     * The object that this timeline animates
     */
    public final SceneObject object;

    /**
     * Create a new timeline for an object.  The new timeline initially has the object
     * stationary, with the same transformation it currently has at all times.  This is
     * achieve by createing a timeline with a single keyframe at time zero.
     * @param o Object
     */
    public AnimTimeline(SceneObject o) {
        object = o;

        // Create A Default Keyframe
        AnimKeyframe f = new AnimKeyframe(0);
        f.transformation.set(o.transformation);
        frames.add(f);
    }

    /**
     * Add A keyframe to the timeline.
     * @param frame Frame number
     * @param t Transformation
     */
    public void addKeyFrame(int frame, Matrix4 t) {
        // TODO#A7: Add an AnimKeyframe to frames and set its transformation
    	AnimKeyframe f = new AnimKeyframe(frame);
        AnimKeyframe ff = frames.floor(f);
        f.transformation.set(t);
		if(!frames.add(f))
			frames.floor(f).transformation.set(t);
       
    }

    /**
     * Checks if there is a keyframe at a given frame
     */
    public boolean hasKeyFrame(int frame) {
        AnimKeyframe f = new AnimKeyframe(frame);
        AnimKeyframe fTree = frames.floor(f);
        return fTree != null && fTree.frame == f.frame;
    }

    /**
     * Remove a keyframe from the timeline.  If the timeline is empty,
     * maintain the invariant by adding a single keyframe with the given
     * transformation.
     * @param frame Frame number
     * @param t Transformation
     */
    public void removeKeyFrame(int frame, Matrix4 t) {
        // TODO#A7: Delete a frame, you might want to use Treeset.remove
        // If there is no frame after deletion, add back this frame.
//    	AnimKeyframe f = new AnimKeyframe(frame);
//    	frames.remove(f);
//    	if(frames.size() ==0) {
//    		f.transformation.set(t);
//    		frames.add(f);
//    	}
    	if(frames.size() != 0) {
			AnimKeyframe tmp = new AnimKeyframe(frame);
			tmp.transformation.set(t);
			frames.remove(tmp);
		}
		if(frames.size() == 0) {
			AnimKeyframe tmp = new AnimKeyframe(frame);
			tmp.transformation.set(t);
			frames.add(tmp);
		}
    }

    /**
     * Update the transformation for the object connected to this timeline to the current frame
     * @curFrame Current frame number
     */
    public void updateTransformation(int curFrame) {
        //TODO#A7: You need to get pair of surrounding frames,
        // calculate interpolation ratio,
        // calculate Translation, Scale and Rotation Interpolation,
        // and combine them.
        // Argument curFrame is current frame number
    	
    	AnimKeyframe f = new AnimKeyframe(curFrame);
    	AnimKeyframe left = frames.floor(f);
    	AnimKeyframe right = frames.ceiling(f);
    	if( left == null && right == null) {
    		return;
    	}else if (left == null) {
    		object.transformation.set(right.transformation);
    		return;
    	}else if (right == null || left.frame == curFrame) {
    		object.transformation.set(left.transformation);
    		return;
    	}else {
    		Matrix4 l = left.transformation.clone();
    		Matrix4 r = right.transformation.clone();
    		float t = (float)((curFrame-left.frame)*1.0/(right.frame - left.frame));
    		// translation
    		Vector3 l_T = l.getTrans();
    		Vector3 r_T = r.getTrans();
    		Vector3 f_T = l_T.clone().mul(1f-t).add(r_T.clone().mul(t));
    		Matrix4 T = Matrix4.createTranslation(f_T);
    		
    		// matrix decomposition
    		Matrix3 l_RS = new Matrix3(l);
    		Matrix3 r_RS = new Matrix3(r);
    		Matrix3 l_R = new Matrix3();
    		Matrix3 l_S = new Matrix3();
    		Matrix3 r_R = new Matrix3();
    		Matrix3 r_S = new Matrix3();
    		l_RS.polar_decomp(l_R, l_S);
    		r_RS.polar_decomp(r_R, r_S);
    		
    		// scale
    		Matrix3 f_S = new Matrix3();
    		f_S.interpolate(l_S, r_S, t);
    		Matrix4 S = new Matrix4(f_S);
    		
    		// rotation
    		Quat l_quat = new Quat(l_R);
    		Quat r_quat = new Quat(r_R);
    		Quat f_quat = Quat.slerp(l_quat, r_quat, t);
    		Matrix4 R = new Matrix4();
    		f_quat.toRotationMatrix(R);
    		
    		Matrix4 trans = T.mulBefore(R.mulBefore(S));
    		object.transformation.set(trans);
    		
    	}
    }
}
