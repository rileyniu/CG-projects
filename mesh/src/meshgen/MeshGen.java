package meshgen;

import java.io.IOException;
import java.util.ArrayList;

import math.Vector2;
import math.Vector3;
/**
 * A command-line tool for generating, analyzing, and processing meshes stored in the OBJ format.
 * Its three main features are:
 *  - Generation of meshes for several basic geometric shapes
 *  - Testing meshes for validity and comparing meshes
 *  - Generating vertex normals for existing mesh geometry
 *  
 * @author ers273, xd93, srm
 * @author Riley Niu(hn263), Adrian Zheng(yz388)
 *
 *
 */

class MeshGen {
	
	/**
	 * Generate the mesh for a cylinder of radius 1 and height 2, centered at the origin.  The 
	 * cylinder axis is the y axis.  The mesh is generated with <tt>divisions</tt> edges around
	 * the top and bottom rims of the cylinder.  The mesh has vertex normals to make the side
	 * of the cylinder smooth and the ends flat.  The (u,v) coordinates map the bottom part of
	 * the texture to the side of the cylinder and the top part to the ends.  See the assignment 
	 * writeup for complete details.
	 * 
	 * @param divisions The number of vertices to use to approximate the circular top and bottom rims
	 * @return A newly created OBJMesh containing the generated geometry
	 */
	public static OBJMesh cylinder(int divisions) {
		OBJMesh outputMesh = new OBJMesh();

		// Task1: Generate Cylinder (20pt)
		Vector3[] vecList = new Vector3[divisions];
		double delta = 2* Math.PI/(double)divisions;
		double radius = 1;

		// side vertices & normals
		for (int i= 0;i<divisions;i++){
			double i_rotated = (double)i-3*(double)divisions/4; // factor to determine the starting point of uv on the rim
			double x = radius * Math.cos((i_rotated * delta));
			double z = radius * Math.sin((-i_rotated * delta));
			outputMesh.positions.add((new Vector3((float)x, 1f, (float)z)));
			outputMesh.normals.add(new Vector3((float)x, 0f, (float)z));
			vecList[i] = new Vector3((float)x, -1f, (float)z);
		}

		for (int i= 0;i<divisions;i++){
			outputMesh.positions.add(vecList[i]);
		}

		// cap vertices & normals
		outputMesh.positions.add((new Vector3(0,1f,0)));
		outputMesh.positions.add((new Vector3(0,-1f,0)));
		outputMesh.normals.add((new Vector3(0,1f,0)));
		outputMesh.normals.add((new Vector3(0,-1f,0)));

		// side uvs
		for (int j = 0; j<=divisions;j++){
			outputMesh.uvs.add((new Vector2((float)j/divisions, 0.5f)));
		}

		for (int j = 0; j<=divisions;j++){
			outputMesh.uvs.add((new Vector2((float)j/divisions, 0.0f)));
		}

		// cap uvs
		for(int i = divisions; i >0 ; --i) {
			double i_rotated = (double)i-3*(double)divisions/4; // factor to determine the starting point of uv on the rim
			outputMesh.uvs.add(new Vector2((float)(0.25+0.25*Math.cos(delta*i_rotated)),
					(float)(0.75+0.25*Math.sin(-delta*i_rotated))));
		}
		for(int i = divisions; i >0 ; --i) {
			double i_rotated = (double)i-(double)divisions/4; // factor to determine the starting point of uv on the rim
			outputMesh.uvs.add(new Vector2((float)(0.75+0.25*Math.cos(delta*i_rotated)),
					(float)(0.75+0.25*Math.sin(-delta*i_rotated))));
		}
		outputMesh.uvs.add(new Vector2((float)0.75,(float)0.75));
		outputMesh.uvs.add(new Vector2((float)0.25,(float)0.75));


		// Calculate indices in faces by looping through n divisions;
		// for each division, calculate the two side faces, one top face and one bottom face
		int n = divisions;
		for(int i = 0; i <n-1; i++){
			OBJFace bot = new OBJFace(3, true, true);
			OBJFace top = new OBJFace(3, true, true);
			OBJFace side1 = new OBJFace(3, true, true);
			OBJFace side2 = new OBJFace(3, true, true);

			bot.setVertex(0, i+n, 2*n+2+i, n+1);
			bot.setVertex(1, 2*n+1, 4*n+3, n+1);
			bot.setVertex(2, i+n+1, 2*n+i+3, n+1);
			outputMesh.faces.add(bot);

			top.setVertex(0, i, 3*n+2+i, n);
			top.setVertex(1, i+1, 3*n+3+i, n);
			top.setVertex(2, 2*n, 4*n+2, n);
			outputMesh.faces.add(top);

			side1.setVertex(0, i, i, i);
			side1.setVertex(1, i+n, i+n+1, i);
			side1.setVertex(2, i+1, i+1, i+1);
			outputMesh.faces.add(side1);

			side2.setVertex(0, i+1, i+1, i+1);
			side2.setVertex(1, i+n, i+n+1, i);
			side2.setVertex(2, i+n+1, i+n+2, i+1);
			outputMesh.faces.add(side2);

		}
		// calculate the last face seperately to wrap texture around seamlessly

		OBJFace lastBottom = new OBJFace(3, true, true);
		lastBottom.setVertex(0, n, 2*n+2, n+1); //3*n+1
		lastBottom.setVertex(1, 2*n-1, 3*n+1, n+1);
		lastBottom.setVertex(2, 2*n+1, 4*n+3, n+1);
		outputMesh.faces.add(lastBottom);

		OBJFace lastTop = new OBJFace(3, true, true);
		lastTop.setVertex(0, n-1, 4*n+1, n);
		lastTop.setVertex(1, 0, 3*n+2, n); //4*n+1
		lastTop.setVertex(2, 2*n, 4*n+2, n);
		outputMesh.faces.add(lastTop);

		OBJFace lastSide1 = new OBJFace(3, true, true);
		lastSide1.setVertex(0, n-1, n-1, n-1);
		lastSide1.setVertex(1, 2*n-1, 2*n, n-1);
		lastSide1.setVertex(2, 0, n, 0);
		outputMesh.faces.add(lastSide1);

		OBJFace lastSide2 = new OBJFace(3, true, true);
		lastSide2.setVertex(0, 0, n, 0);
		lastSide2.setVertex(1, 2*n-1, 2*n, n-1);
		lastSide2.setVertex(2, n, 2*n+1, 0);
		outputMesh.faces.add(lastSide2);



		return outputMesh;


	}
	
	/**
	 * Generate the mesh for a sphere of radius 1, centered at the origin.  The sphere is triangulated
	 * in a longitude-latitude grid, with <tt>divisionsU</tt> divisions around the equator and 
	 * <tt>divisionsV</tt> rows of triangles from the south to the north pole.  The mesh has the exact
	 * surface normals of the geometric sphere, and the (u,v) coordinates are proportional to 
	 * longitude and latitude.  See the assignment writeup for complete details.
	 * 
	 * @param divisionsU The number of divisions around the equator
	 * @param divisionsV The number of divisions from pole to pole
	 * @return A newly created OBJMesh containing the generated geometry
	 */
	public static OBJMesh sphere(int divisionsU, int divisionsV) {
		OBJMesh outputMesh = new OBJMesh();

		// Task1: Generate Sphere (20pt)
		// TODO:
		// Calculate Vertices (positions, uvs, and normals )
		// Calculate indices in faces (use OBJFace class)
		for (int m = 0; m <= divisionsV; m++) {
			if (m == 0) {
				outputMesh.positions.add((new Vector3(0f, 1f, 0f)));
				outputMesh.normals.add((new Vector3(0f,1f,0f)));
				for (int n = 0; n < divisionsU; n++) {
					double u = (double)n/(double)divisionsU;
					outputMesh.uvs.add((new Vector2((float)u, 1f)));
				}
			}else if (m == divisionsV) {
				outputMesh.positions.add((new Vector3(0f, -1f, 0f)));
				outputMesh.normals.add((new Vector3(0f,-1f,0f)));
				for (int n = 0; n < divisionsU; n++) {
					double u = (double)n/(double)divisionsU;
					outputMesh.uvs.add((new Vector2((float)u, 0f)));
				}
			} else {
				for (int n = 0; n < divisionsU; n++) {
					double angle1 = ((double)m)/((double)divisionsV) * Math.PI;
					double angle2 = ((double)n)/((double)divisionsU) * 2 * Math.PI + Math.PI;
					double x = Math.sin(angle1) * Math.sin(angle2);
					double z = Math.sin(angle1) * Math.cos(angle2);
					double y = Math.cos(angle1);
					outputMesh.positions.add((new Vector3((float)x, (float)y, (float)z)));

					outputMesh.normals.add((new Vector3((float)x, (float)y, (float)z)));

					double u = (double)n/(double)divisionsU;
					double v = 1f - (double)m/(double)divisionsV;
					outputMesh.uvs.add((new Vector2((float)u, (float)v)));

					if (n == divisionsU - 1) {
						outputMesh.uvs.add((new Vector2(1f, (float)v)));
					}

					OBJFace triangle1 = new OBJFace(3, true, true);
					triangle1.setVertex(0, (m-1) * divisionsU + n + 1, m * (divisionsU + 1) + n - 1, (m-1) * divisionsU + n + 1);
					if (n != divisionsU - 1) {
						triangle1.setVertex(1, (m-1) * divisionsU + n + 2, m * (divisionsU + 1) + n, (m-1) * divisionsU + n + 2);
					} else {
						triangle1.setVertex(1, (m-1) * divisionsU + 1, m * (divisionsU + 1) + n, (m-1) * divisionsU + 1);
					}

					if (m == 1) {
						triangle1.setVertex(2, Math.max((m-2) * divisionsU + n + 1, 0), (m - 1) * (divisionsU + 1) + n, Math.max((m-2) * divisionsU + n + 1, 0));
					} else {
						triangle1.setVertex(2, Math.max((m-2) * divisionsU + n + 1, 0), (m - 1) * (divisionsU + 1) + n - 1, Math.max((m-2) * divisionsU + n + 1, 0));
					}
					outputMesh.faces.add(triangle1);

					OBJFace triangle2 = new OBJFace(3, true, true);
					triangle2.setVertex(0, (m-1) * divisionsU + n + 1, m * (divisionsU + 1) + n - 1, (m-1) * divisionsU + n + 1);
					int pos2;
					int pos1;
					int tex1;
					int tex2;
					if (n != divisionsU - 1) {
						pos2 = (m-1) * divisionsU + n + 2;
						pos1 = Math.min(m * divisionsU + n + 2, (divisionsV - 1) * divisionsU + 1);
					} else {
						pos2 = (m-1) * divisionsU + 1;
						pos1 = Math.min(m * divisionsU + 1, (divisionsV - 1) * divisionsU + 1);
					}
					tex2 = m * (divisionsU + 1) + n;
					if ((m != divisionsV - 1)) {
						tex1 = (m+1) * (divisionsU + 1) + n;
					} else {
						tex1 = (m+1) * (divisionsU + 1) + n - 1;
					}
					triangle2.setVertex(1, pos1, tex1, pos1);
					triangle2.setVertex(2, pos2, tex2, pos2);
					outputMesh.faces.add(triangle2);
				}
			}
		}

		return outputMesh;
	}
	
	
	/**
	 * Create vertex normals for an existing mesh.  The triangles, positions, and uvs are copied
	 * from the input mesh, and new vertex normals are computed by averaging the normals of the 
	 * faces that share each vertex.
	 * 
	 * @param inputMesh The input mesh, whose triangles and vertex positions define the normals
	 * @return A newly created OBJMesh that is a copy of the input mesh but with new normals
	 */
	public static OBJMesh createNormals(OBJMesh inputMesh) {
		OBJMesh outputMesh = inputMesh;

		// Task2: Compute Normals (35pt)
		// Copy position data
		// Copy UV data

		ArrayList<Vector3> positions = inputMesh.positions;
		ArrayList<OBJFace> faces = inputMesh.faces;
		outputMesh.normals.clear();
		// Each vertex gets a unique normal
		for (int i =0;i<inputMesh.positions.size();i++){
			outputMesh.normals.add(i, new Vector3((float)0.0, (float)0.0, (float)0.0));
		}
		// Initialize output faces
		for (int i =0;i<inputMesh.faces.size();i++){
			outputMesh.faces.get(i).normals = outputMesh.faces.get(i).positions;
		}

		// Calculate face normals, distribute to adjacent vertices
		for (int i = 0;i<faces.size();i++){
			Vector3 pos1 = positions.get(faces.get(i).positions[0]);
			Vector3 pos2 = positions.get(faces.get(i).positions[1]);
			Vector3 pos3 = positions.get(faces.get(i).positions[2]);
			Vector3 triNormal = calculateSurNorm(pos1, pos2, pos3);
			outputMesh.normals.get(faces.get(i).positions[0]).add(triNormal);
			outputMesh.normals.get(faces.get(i).positions[1]).add(triNormal);
			outputMesh.normals.get(faces.get(i).positions[2]).add(triNormal);
		}

		// Normalize new normals
		for(int i = 0; i < outputMesh.normals.size(); i++){
			outputMesh.normals.get(i).normalize();
		}

		return outputMesh;
	}

	// Calculate the normalized surface normal of a triangle given the position of three vertices
	private static Vector3 calculateSurNorm(Vector3 p1, Vector3 p2, Vector3 p3){
		Vector3 d1 = p1.clone().sub(p2);
		Vector3 d2 = p3.clone().sub(p2);
		Vector3 norm = d1.cross(d2).normalize().negate();
		return norm;
	}
	
	
	//
	// The following are extra credits, it is not required, do as you are interested in it.
	//

	/**
	 * Generate the mesh for a torus of major radius 1 and minor radius <tt>minorRadius</tt>.
	 * The symmetry axis is the y axis.  The torus is triangulated in a grid with 
	 * <tt>divisionsU</tt> divisions along the tube (major direction) and <tt>divisionsV</tt>
	 * divisions around the tube (minor direction).  The vertex normals are the exact normals
	 * to the geometric torus, and the (u,v) coordinates follow the triangulation grid.
	 * See the assignment writeup for complete details.
	 * 
	 * @param divisionsU The number of divisions in the major direction
	 * @param divisionsV The number of divisions in the minor direction
	 * @param minorRadius The minor radius (radius of the tube)
	 * @return A newly created OBJMesh containing the generated geometry
	 */
	public static OBJMesh torus(int divisionsU, int divisionsV, float minorRadius) {
		OBJMesh outputMesh = new OBJMesh();
		double majorR = 1;
		double minorR = minorRadius;
		int majorN = divisionsU;
		int minorN = divisionsV;
		// Extra Credit: Generate Turos (10pt)
		// TODO:
		// Calculate vertices: positions, uvs and normals
		// Calculate indices on faces (use OBJFace class)
		double majorDelta = 2* Math.PI/(double)majorN;
		double minorDelta = 2* Math.PI/(double)minorN;
		for (int i= majorN;i>=0;i--){
			for(int j = minorN;j>=0;j--){
				double theta = i* majorDelta;
				double phi = j * minorDelta;
				double x = (majorR - minorR * Math.cos(theta))*(float)Math.cos(phi);
				double y = minorR * Math.sin(theta);
				double z = (majorR- minorR * Math.cos(theta))*Math.sin(phi);
				outputMesh.positions.add((new Vector3((float)x, (float)y, (float)z)));

				//normal vector
				double nx = (majorR - (1.5*minorR * Math.cos(theta)))*(float)Math.cos(phi)-x;
				double ny = 1.5* minorR * Math.sin(theta)-minorR * Math.sin(theta);
				double nz = (majorR- (1.5*minorR * Math.cos(theta)))*Math.sin(phi)-z;
				double l = Math.sqrt(nx*nx+ny*ny+nz*nz);
				outputMesh.normals.add(new Vector3((float)nx/(float)l, (float)ny/(float)l, (float)nz/(float)l));
			}
		}

		for (int i= 0;i<majorN+1;i++) {
			for (int j = 0; j < minorN+1; j++) {
				outputMesh.uvs.add(new Vector2((float)j/(float)minorN, 1-(float)i/(float)majorN));
			}
		}

		for (int i = 0;i<majorN;++i) {
			for (int j = 0; j < minorN; ++j) {
				OBJFace f1 =  new OBJFace(3, true, true);
				OBJFace f2 =  new OBJFace(3, true, true);
				f1.setVertex(0,i*(minorN+1)+j,i*(minorN+1)+j,i*(minorN+1)+j);
				f1.setVertex(1,i*(minorN+1)+j+1,i*(minorN+1)+j+1,i*(minorN+1)+j+1);
				f1.setVertex(2,(i+1)*(minorN+1)+j,(i+1)*(minorN+1)+j,(i+1)*(minorN+1)+j);

				f2.setVertex(1,(i+1)*(minorN+1)+j,(i+1)*(minorN+1)+j,(i+1)*(minorN+1)+j);
				f2.setVertex(0,(i+1)*(minorN+1)+j+1,(i+1)*(minorN+1)+j+1, (i+1)*(minorN+1)+j+1);
				f2.setVertex(2, i*(minorN+1)+j+1,i*(minorN+1)+j+1,i*(minorN+1)+j+1);

				outputMesh.faces.add(f1);
				outputMesh.faces.add(f2);
			}
		}

		return outputMesh;
	}

	
	public static OBJMesh geodesicSphere(int divisionU, int divisionV) {
		OBJMesh outputMesh = new OBJMesh();

		// Extra Credit: Geodesic Sphere (10pt)
		// TODO:
		// Calculate vertices: positions, uvs and normals 
		// Calculate indices on faces (use OBJFace class)

		return outputMesh;
	}

	public void verify(OBJMesh m1){


	}
	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.err.println("Error: not enough input arguments.");
			printUsage();
			System.exit(1);
		}

		if (args[0].equals("-g")) { // Generate mesh
			int divisionsU = 32;
			int divisionsV = 16;
			float minorRadius = 0.25f;
			String outputFilename = null;

			for (int i=2; i<args.length; i += 2) {
				if (i+1 == args.length) {
					System.err.println("Error: expected argument after \"" + args[i] + "\" flag.");
					printUsage();
					System.exit(1);
				}
				if (args[i].equals("-n")) { // Divisions latitude
					divisionsU = Integer.parseInt(args[i+1]);
				} else if (args[i].equals("-m")) { // Divisions longitude
					divisionsV = Integer.parseInt(args[i+1]);
				} else if (args[i].equals("-r")) { // Inner radius
					minorRadius = Float.parseFloat(args[i+1]);
				} else if (args[i].equals("-o")) { // Output filename
					outputFilename = args[i+1];
				} else {
					System.err.println("Error: Unknown option \"" + args[i] + "\"");
					printUsage();
					System.exit(1);
				}
			}

			if (outputFilename == null) {
				System.err.println("Error: expected -o argument.");
				printUsage();
				System.exit(1);
			}

			OBJMesh outputMesh = null;
			if (args[1].equals("cylinder")) {
				outputMesh = cylinder(divisionsU);
			} else if (args[1].equals("sphere")) {
				outputMesh = sphere(divisionsU, divisionsV);
			} else if (args[1].equals("torus")) {
				outputMesh = torus(divisionsU, divisionsV, minorRadius);
			} else {
				System.err.println("Error: expected geometry type.");
				printUsage();
				System.exit(1);
			}

			System.out.println("Output mesh is valid: " + outputMesh.isValid(true));

			try {
				outputMesh.writeOBJ(outputFilename);
			} catch (IOException e) {
				System.err.println("Error: could not write file " + outputFilename);
				System.exit(1);
			}

		} else if (args[0].equals("-i")) { // Assign normals
			if (!args[2].equals("-o")) {
				System.err.println("Error: expected -o argument.");
				printUsage();
				System.exit(1);
			}
			OBJMesh inputMesh = null;
			try {
				inputMesh = new OBJMesh(args[1]);
			} catch (OBJMesh.OBJFileFormatException e) {
				System.err.println("Error: Malformed input OBJ file: " + args[1]);
				System.err.println(e);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Error: could not read file " + args[1]);
				System.err.println(e);
				System.exit(1);
			}
			OBJMesh outputMesh = createNormals(inputMesh);
//
//			System.out.println("Output mesh is valid: " + outputMesh.isValid(true));
//			OBJMesh m2 = new OBJMesh();
//			try {
//				m2.parseOBJ("bunny-norms-reference.obj");
//				System.out.println("SS"+OBJMesh.compare(outputMesh,m2,true,1.0E-5f));
//				System.out.println("Output ref mesh is valid: " + m2.isValid(true));
//
//			} catch (IOException e) {
//				System.err.println("Error: could not write file ");
//				System.exit(1);
//			}
			try {
				outputMesh.writeOBJ(args[3]);
			} catch (IOException e) {
				System.err.println("Error: could not write file " + args[3]);
				System.exit(1);
			}

		} else if (args[0].equals("-v")) { // Verify an OBJ file
			if (args.length != 2) {
				System.err.println("Error: expected an input file argument.");
				printUsage();
				System.exit(1);
			}
			OBJMesh mesh = null;
			try {
				mesh = new OBJMesh(args[1]);
			} catch (OBJMesh.OBJFileFormatException e) {
				System.err.println("Error: Malformed input OBJ file: " + args[1]);
				System.err.println(e);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Error: could not read file " + args[1]);
				System.err.println(e);
				System.exit(1);
			}
			System.out.println("Input mesh is valid OBJ syntax: " + mesh.isValid(true));

		} else if (args[0].equals("-c")) { // Compare two OBJ files
			float eps = 1e-5f;
			if (args.length != 3 && args.length != 5) {
				System.err.println("Error: expected 2 input file arguments and optional epsilon.");
				printUsage();
				System.exit(1);
			}
			if (args.length == 5) {
				if (!args[1].equals("-e")) {
					System.err.println("Error: expected -e flag after -c.");
					printUsage();
					System.exit(1);
				}
				eps = Float.parseFloat(args[2]);
			}
			OBJMesh m1 = null, m2 = null;
			int m1arg = (args.length == 3) ? 1 : 3;
			int m2arg = (args.length == 3) ? 2 : 4;
			try {
				m1 = new OBJMesh(args[m1arg]);
			} catch (OBJMesh.OBJFileFormatException e) {
				System.err.println("Error: Malformed input OBJ file: " + args[m1arg]);
				System.err.println(e);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Error: could not read file " + args[m1arg]);
				System.err.println(e);
				System.exit(1);
			}
			try {
				m2 = new OBJMesh(args[m2arg]);
			} catch (OBJMesh.OBJFileFormatException e) {
				System.err.println("Error: Malformed input OBJ file: " + args[m2arg]);
				System.err.println(e);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Error: could not read file " + args[m2arg]);
				System.err.println(e);
				System.exit(1);
			}
			System.out.println("Meshes are equivalent: " + OBJMesh.compare(m1, m2, true, eps));

		} else {
			System.err.println("Error: Unknown option \"" + args[0] + "\".");
			printUsage();
			System.exit(1);
		}
	}

	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("(1) MeshGen -g <cylinder|sphere|torus> [-n <divisionsLatitude>] [-m <divisionsLongitude>] [-r <innerRadius>] -o <output.obj>");
		System.out.println("(2) MeshGen -i <input.obj> -o <output.obj>");
		System.out.println("(3) MeshGen -v <input.obj>");
		System.out.println("(4) MeshGen -c [-e <epsilon>] <m1.obj> <m2.obj>");
		System.out.println();
		System.out.println("(1) creates an OBJ mesh of a cylinder, sphere, or torus.");
		System.out.println("Cylinder ignores -n and -r flags, sphere ignores the -r flag.");
		System.out.println("(2) takes in an OBJ mesh, strips it of normals (if any), and assigns new normals based on the normals of its faces.");
		System.out.println("(3) verifies that an input OBJ mesh file conforms to the OBJ standard.");
		System.out.println("(4) compares two input OBJ files and checks if they are equivalent up to an optional epsilon parameter (by default, epsilon=1e-5).");
	}

}
