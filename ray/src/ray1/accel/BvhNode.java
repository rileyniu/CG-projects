package ray1.accel;

import ray1.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#Ray Part 2 Task 3: fill in this function.
		// You can find this in the slides.
		
		Vector3d p = ray.origin;
		Vector3d d = ray.direction;
		
		double txmin = (minBound.x - p.x)/d.x;
		double txmax = (maxBound.x - p.x)/d.x;
		double tymin = (minBound.y - p.y)/d.y;
		double tymax = (maxBound.y - p.y)/d.y;
		double tzmin = (minBound.z - p.z)/d.z;
		double tzmax = (maxBound.z - p.z)/d.z;
		double txenter = Math.min(txmin, txmax);
		double txexit = Math.max(txmin, txmax);
		double tyenter = Math.min(tymin, tymax);
		double tyexit = Math.max(tymin, tymax);
		double tzenter = Math.min(tzmin, tzmax);
		double tzexit = Math.max(tzmin, tzmax);
		
		if (txexit>tyenter) {
			double txymax = Math.min(txexit, tyexit);
			if(txymax>tzenter)
			{
				return true;
			}
		}
		return false;
//		double tenter = tyenter > tzenter ? tyenter : tzenter;
//		tenter = tenter > txenter ? tenter : txenter;
//		double texit = tyexit < tzexit ? tyexit : tzexit;
//		texit = texit < txexit ? texit : txexit;
//		return texit >= tenter && tenter <= ray.end && texit >= ray.start;

	}
}
