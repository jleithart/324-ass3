//modeler

/*  Model.java
  * Programming assignment #2
  * CS 324 Bruce Bolden
  * Due February 14, 2014
  */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Model extends JComponent
{
    static int FRAME_HEIGHT = 500;
    static int FRAME_WIDTH = 500;

    public enum TRANSFORM_CODE { X_TRANS, Y_TRANS, Z_TRANS, Y_ROT, X_ROT, Z_ROT, PERSPECTIVE};

    static double [][]CAMERA = { {1, 0, 0, 0},
    						{0, 1, 0, 0},
    						{0, 0, 1, 0},
    						{0, 0, 0, 1},
						  };

	static double [][]IDENTITY = { 	{1, 0, 0, 0},
									{0, 1, 0, 0},
									{0, 0, 1, 0},
									{0, 0, 0, 1}
								 };

    private static class Object{
        public String name;
        public double [][]aT;

        //list of vertices to draw
        public Point3D []vertices;

        private Object(Point3D [] points, double [][]transform, String name){
            this.vertices = points;

            this.aT = transform;
            this.name = name;
        }

        // Draw Square object
        public void DrawObject(Graphics g, double [][] transform){
            double [][]M = MultiplyTransforms(this.aT, transform);
            Move3D(vertices[0], M, CAMERA);
            for(int i = 1; i < this.vertices.length; i++){
                Draw3D(g, vertices[i], M, CAMERA);
            }
            Draw3D(g, vertices[0], M, CAMERA);

        }
    }

    private static class Assembly{
        public String name;
        public double [][]aT;
        public Assembly []assemblies;
        public Object drawnObject;

        private Assembly(Assembly []in_assem, double [][]transform, Object inputObject, String name){
            this.name = name;
            this.aT = transform;
            this.assemblies = in_assem;
            this.drawnObject = inputObject;

        }

        public void Assemble(Graphics g, double [][]transform){
            //if nil call list of assemblies
            if(drawnObject != null){
                double [][]M = MultiplyTransforms(this.aT, transform);
                //drawnObject.aT = transform;
                drawnObject.DrawObject(g, M);
            }
            for(int i = 0; i < this.assemblies.length; i++){
                double [][]M = MultiplyTransforms(this.aT, transform);
                //PrintMatrix(M);
                //assemblies[i].aT = M;
                //assemblies[i].aT = this.aT;
                assemblies[i].Assemble(g, M);
            }
        }
    }


    // A viewport struct/class
    private static class ViewPort
    {
        public double LeftX;
        public double RightX;
        public double TopY;
        public double BotY;

        private ViewPort(double LX, double RX, double TY, double BT){
            this.LeftX = LX;
            this.RightX = RX;
            this.TopY = TY;
            this.BotY = BT;
        }

    }

    // the Window Struct/Class
    private static class Window
    {
        public double LeftX;
        public double RightX;
        public double TopY;
        public double BotY;

        private Window(double LX, double RX, double TY, double BY){
            this.LeftX = LX;
            this.RightX = RX;
            this.TopY = TY;
            this.BotY = BY;
        }
    }


    // This is the class/struct for the Frame Point
    // This must be in integers to correlate to the pixels
    private static class drawPoint
    {
        public int x;
        public int y;
        private drawPoint(int iX, int iY){
            this.x = iX;
            this.y = iY;
        }

        public void SetCoords(int iX, int iY){
            this.x = iX;
            this.y = iY;
        }
    }

    //My point Class/Struct
    private static class Point2D
    {
        public double x;
        public double y;
        private Point2D(double iX, double iY){
            this.x = iX;
            this.y = iY;
        }

        public void SetCoords(double iX, double iY){
            this.x = iX;
            this.y = iY;
        }
    }

    private static class Point3D
    {
    	public double x;
    	public double y;
    	public double z;
    	private Point3D(double iX, double iY, double iZ){
    		this.x = iX;
    		this.y = iY;
    		this.z = iZ;
    	}

        public void SetCoords(double iX, double iY, double iZ){
            this.x = iX;
            this.y = iY;
            this.z = iZ;
        }
    }

    // The current position of the "pen"
    // This is in relation to the window
    static Point2D curPos = new Point2D(0, 0);

    static ViewPort curVP = new ViewPort(0, 0, 0, 0);
    static Window curWin = new Window(0, 0, 0, 0);



     public static void main( String[] args )
     {
        JFrame f = new JFrame( "Model" );
        InitGraphics(f);

        //  Exit application when the window is closed
         f.addWindowListener( new WindowAdapter() {
             public void windowClosing( WindowEvent e )
             {  System.exit(0); }
             }
         );

     }

     public void paintComponent( Graphics g )
     {
        //allow resizing over everything drawn
        FRAME_HEIGHT = getHeight();
        FRAME_WIDTH = getWidth();

        ShowViewport(g);

        //PlotGraph(g);

        //DrawRubiksCube(g, 0.75);
        //DrawRubiksCube(g, 0);
        //DrawCube(g);
        DrawHallway(g);
        //DrawName(g);
     }

     /*
      * Initialization for the graphics engine
      * Creates a window into which all drawing operations are made.
      * Initializes default values
     */
     public static void InitGraphics(JFrame f){
        f.setSize( FRAME_HEIGHT, FRAME_WIDTH);
        f.getContentPane().add( new Model() );
        f.setVisible( true );

        DefineViewport(0, 0, 1, 1);

        //the focal point for the graph
        //window for the Z graph
        //DefineWindow(-3, -3, 3, 3);
        //Point3D focalPoint = new Point3D(0, 0, 0);
        //DefineCameraTransform(focalPoint, 45, 30, 0, 10);

        // focal point for rubik's cube
        //window for the rubik's cube
        //DefineWindow(-15, -15, 15, 15);
        //Point3D focalPoint = new Point3D(0, 0, 0);
        //DefineCameraTransform(focalPoint, 30, 45, 0, 10);

        // the camera tranform for the hallway
        //window for the hallway
        DefineWindow(-120, 0, 100, 150);
        Point3D focalPoint = new Point3D(0, 0, 0);
        DefineCameraTransform(focalPoint, 0, 30, 0, 200);

        //the camera transform for the lettering
        //window for the lettering
        //DefineWindow(-10, -10, 10, 10);
        //Point3D focalPoint = new Point3D(1, 0, 1);
        //DefineCameraTransform(focalPoint, 10, 30, 0, 10);



        // set the values of each viewport
        //SetViewport();
        //SetWindow();

     }

     public static double [][] MultiplyTransforms(double[][] M, double[][] N){
		assert(M[0].length == N.length);
		int mColumns = M[0].length;
		int mRows = M.length;
		int nColumns = N[0].length;
		int nRows = N.length;

		double [][]retval = new double[mRows][nColumns];
		for(int i = 0; i < mRows; i++){
			for(int j = 0; j < nColumns; j++){
				retval[i][j] = 0.0;
			}
		}
		double sum;

		for(int col = 0; col < mRows; col++){
			for(int row = 0; row < nColumns; row++){
				sum = 0.0;
				for(int index = 0; index < mColumns; index++){
					sum += M[col][index]*N[index][row];
				}
				retval[col][row] = sum;
			}
		}

		return retval;
	}

    public static void PrintMatrix(double [][] M){
        for(int i = 0; i < M.length; i++){
            System.out.print("|");
            for(int j = 0; j < M[0].length; j++){
                System.out.print(" " + M[i][j] + " ");
            }
            System.out.println("|");

        }
        System.out.println();
    }

     public static void DefineWindow(double LX, double BY, double RX, double TY){
     	curWin.LeftX = LX;
     	curWin.BotY = BY;
     	curWin.RightX = RX;
     	curWin.TopY = TY;
     }

     public static void DefineViewport(double LX, double BY, double RX, double TY){
     	curVP.LeftX = LX;
     	curVP.BotY = BY;
     	curVP.RightX = RX;
     	curVP.TopY = TY;
     }

     public static Point2D WindowToViewPort(Point3D point){
     	Point2D tmpCoordinate = new Point2D(0, 0);

     	tmpCoordinate.x = LinearInterpolation(curVP.LeftX, curVP.RightX, curWin.LeftX, curWin.RightX, point.x);
        if(tmpCoordinate.x < curVP.LeftX){
            tmpCoordinate.x = curVP.LeftX;
        }
        else if(tmpCoordinate.x > curVP.RightX){
            tmpCoordinate.x = curVP.RightX;
        }
     	tmpCoordinate.y = LinearInterpolation(curVP.BotY, curVP.TopY, curWin.BotY, curWin.TopY, point.y);
        if(tmpCoordinate.y < curVP.BotY){
            tmpCoordinate.y = curVP.BotY;
        }
        else if(tmpCoordinate.y > curVP.TopY){
            tmpCoordinate.y = curVP.TopY;
        }

     	return tmpCoordinate;
     }

     public static void Move3D(Point3D inputPoint, double[][] activeTransform, double[][] cameraTransform){
     	Point3D activePoint = ApplyTransform(inputPoint, activeTransform);
     	Point3D cameraPoint = ApplyTransform(activePoint, cameraTransform);

     	Point2D pointToMove = WindowToViewPort(cameraPoint);

     	MoveTo2D(pointToMove);
     }

     //I think for this aassignment activeTransform needs to be identity matrix

     public static void Draw3D(Graphics g, Point3D inputPoint, double [][]activeTransform, double [][] cameraTransform){
     	Point3D activePoint = ApplyTransform(inputPoint, activeTransform);
     	Point3D cameraPoint = ApplyTransform(activePoint, cameraTransform);

     	Point2D pointToDraw = WindowToViewPort(cameraPoint);

     	DrawTo2D(g, pointToDraw);
     	Move3D(inputPoint, activeTransform, cameraTransform);

     }

     public static double[][] DefineElementaryTransform(double [][] inputMatrix, TRANSFORM_CODE transformCode, 
     											double transformValue)
     {
     	double [][]transMatrix = { 	{1, 0, 0, 0},
     						  		{0, 1, 0, 0},
     						  		{0, 0, 1, 0},
     						  		{0, 0, 0, 1}
     							};
     	double [][]retMatrix = new double[4][4];
     	double radianVal = 3.14159*transformValue/180;

     	switch(transformCode){
     		case X_TRANS:
     			transMatrix[3][0] = transformValue;
     			break;
     		case Y_TRANS:
     			transMatrix[3][1] = transformValue;
     			break;
     		case Z_TRANS:
     			transMatrix[3][2] = transformValue;
     			break;
     		case Y_ROT:
     			transMatrix[0][0] = Math.cos(radianVal);
     			transMatrix[0][2] = -Math.sin(radianVal);
     			transMatrix[2][0] = Math.sin(radianVal);
     			transMatrix[2][2] = Math.cos(radianVal);
     			break;
     		case X_ROT:
     			transMatrix[1][1] = Math.cos(radianVal);
     			transMatrix[1][2] = Math.sin(radianVal);
     			transMatrix[2][1] = -Math.sin(radianVal);
     			transMatrix[2][2] = Math.cos(radianVal);
     			break;
     		case Z_ROT:
     			transMatrix[0][0] = Math.cos(radianVal);
     			transMatrix[0][1] = Math.sin(radianVal);
     			transMatrix[1][0] = -Math.sin(radianVal);
     			transMatrix[1][1] = Math.cos(radianVal);
     			break;
     		case PERSPECTIVE:
     			assert(transformValue != 0);
     			transMatrix[2][3] = -1/transformValue;
     			break;
     		default:
     			System.out.println("Error: invalid code passed into function DefineElementaryTransform");
     			break;
     	}

     	retMatrix = MultiplyTransforms(inputMatrix, transMatrix);
     	return retMatrix;
     }

     public static double[][] BuildElementaryTransform(double [][]transformMatrix,
     											TRANSFORM_CODE transformCode,
     											double transformValue)
     {
     	double [][]M = new double[4][4];
     	//double M2 = new double[4][4];

     	//I want deine elementary transform to return the matrix
     	M = DefineElementaryTransform(transformMatrix, transformCode, transformValue);
     	//M2 = MultiplyTransforms(transformMatrix, M);

     	return M;

     }

     public static void DefineCameraTransform(Point3D focalPoint, 
     											double theta, double phi, double alpha, 
     											double r)
     {
     	CAMERA = DefineElementaryTransform(CAMERA, TRANSFORM_CODE.X_TRANS, -focalPoint.x);
     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.Y_TRANS, -focalPoint.y);
     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.Z_TRANS, -focalPoint.z);

     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.Y_ROT, -theta);
     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.X_ROT, phi);
     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.Z_ROT, -alpha);

     	CAMERA = BuildElementaryTransform(CAMERA, TRANSFORM_CODE.PERSPECTIVE, r);

     }

     public static Point3D ApplyTransform(Point3D point, double[][] activeTransform){
     	double [][]vector = { {point.x, point.y, point.z, 1} };
     	double [][]retVector = new double[1][4];
     	retVector = MultiplyTransforms(vector, activeTransform);

     	Point3D retPoint = new Point3D(retVector[0][0]/retVector[0][3], retVector[0][1]/retVector[0][3], retVector[0][2]/retVector[0][3]);

     	return retPoint;
     }

     /*
     * Moves the starting position to the specified (x,y) location.
     * This is a window point
     */
     public static void MoveTo2D(Point2D inputPoint){
        curPos = inputPoint;
     }

     /*
     * Draws from the last position to a specified (x,y) location.
     * Utilizes Java's draw line method
     * Last position is changed by MoveTo2D or DrawTo2D
     */
     public static void DrawTo2D(Graphics g, Point2D inputPoint){

        Point2D firstPoint = curPos;	//this might need to go through the window to viewport transition
        Point2D secondPoint = inputPoint;

        //now draw it
        drawPoint drawFirstPoint = ViewPortToFrameWindow(firstPoint);
        drawPoint drawSecondPoint = ViewPortToFrameWindow(secondPoint);
        g.drawLine(drawFirstPoint.x, drawFirstPoint.y, drawSecondPoint.x, drawSecondPoint.y);

        MoveTo2D(inputPoint);
     }

     public static double LinearInterpolation(double min1, double max1, double min2, double max2, double known){
     	return (min1 + (max1 - min1)*((known - min2)/(max2-min2)));
     }

     /*
      * Converts the viewport coordinates to actual display frame coordinates
     */

     public static drawPoint ViewPortToFrameWindow(Point2D coordinate){

        drawPoint tmpCoordinate = new drawPoint(0, 0);

        // given a viewport coordinate, find the pixel placement
        //user linear interpolation:
        // x = x(0) + x1 - x0(y-y0/y1-y0)
        tmpCoordinate.x = (int) LinearInterpolation(0 + 5, FRAME_WIDTH - 5, curVP.LeftX, curVP.RightX, coordinate.x);
        tmpCoordinate.y = (int) LinearInterpolation(FRAME_HEIGHT - 5, 0 + 5, curVP.BotY, curVP.TopY, coordinate.y);
        

        
        return tmpCoordinate;
     }

     /*
     * Draws a rectangle around each viewport
     */

     // TODO REWORK
     public static void ShowViewport(Graphics g){
        //draws the viewports
        drawPoint drawFirstPoint;
        drawPoint drawSecondPoint;

        Point2D firstPoint = new Point2D(0, 0);
        Point2D secondPoint = new Point2D(1, 1);

        drawFirstPoint = ViewPortToFrameWindow(firstPoint);
        drawSecondPoint = ViewPortToFrameWindow(secondPoint);

        g.drawLine(drawFirstPoint.x, drawFirstPoint.y, drawSecondPoint.x, drawFirstPoint.y);
        g.drawLine(drawSecondPoint.x, drawFirstPoint.y, drawSecondPoint.x, drawSecondPoint.y);
        g.drawLine(drawSecondPoint.x, drawSecondPoint.y, drawFirstPoint.x, drawSecondPoint.y);
        g.drawLine(drawFirstPoint.x, drawSecondPoint.y, drawFirstPoint.x, drawFirstPoint.y);
     }

     public static void DrawAxes(Graphics g){
        //X axis
        Point3D PointToDraw = new Point3D(2.5, 0, 0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(-2.5, 0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //y-axis
        PointToDraw.SetCoords(0, 2.5, 0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, -2.5, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //z-axis
        PointToDraw.SetCoords(0, 0, 10);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 0, -10);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

     }

     /*
     * Plots the given equation
     */
     public static void PlotGraph(Graphics g){
        //start at the low point
        DrawAxes(g);
        Point3D firstPoint = new Point3D(-1.25, -1.25, 0);
        Point3D secondPoint = new Point3D(-1.25, -1.25, 0);
        double inc = 0.02;
        g.setColor(Color.red);
        for(double y = -1.25; y <= 1.25; y += inc){
            firstPoint.SetCoords(y, -1.25, PlotFunction(y, -1.25));
            //Move3D(firstPoint, IDENTITY, CAMERA);
            for(double x = -1.25; x <= 1.25; x+=inc){
                //draw the small square
                firstPoint.SetCoords(x, y, 0);
                secondPoint.SetCoords(x+inc, y+inc, 0);
                DrawSquare(g, firstPoint, secondPoint);
            }
        }
        Branding(g, "z = (x^2) + (y^2) - (x^3) - 8*x*(y^4)");


     }

     /*
     *
     */
     public static void DrawSquare(Graphics g, Point3D leftPoint, Point3D rightPoint){
        Point3D PointToDraw = new Point3D(leftPoint.x, leftPoint.y, PlotFunction(leftPoint.x, leftPoint.y));
        Move3D(PointToDraw, IDENTITY, CAMERA);

        //leftX -> rightX
        PointToDraw.SetCoords(rightPoint.x, leftPoint.y, PlotFunction(rightPoint.x, leftPoint.y));
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //botY -> topY
        PointToDraw.SetCoords(rightPoint.x, rightPoint.y, PlotFunction(rightPoint.x, rightPoint.y));
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //rightX -> leftX
        PointToDraw.SetCoords(leftPoint.x, rightPoint.y, PlotFunction(leftPoint.x, rightPoint.y));
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //topY -> botY
        PointToDraw.SetCoords(leftPoint.x, leftPoint.y, PlotFunction(leftPoint.x, leftPoint.y));
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);
     }

     /*
     * Given an x & y value return the z value
     */
     public static double PlotFunction(double x, double y){
        double z;
        z = x*x + y*y - (x*x*x) - 8*x*y*y*y*y;
        return z;
     }

     /*
     *
     */
     public static void DrawCube(Graphics g){
        Point3D PointToDraw = new Point3D(1.0, 0, 1.0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 1.0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 1.0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //RIGHT SQUARE
        PointToDraw.SetCoords(1.0, 0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 1.0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 1.0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        //LEFT SQUARE
        PointToDraw.SetCoords(0, 0, 1.0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 1.0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 1.0, 1.0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        // BACK SQUARE
        PointToDraw.SetCoords(0, 0, 0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(1.0, 1.0, 0);
        Move3D(PointToDraw, IDENTITY, CAMERA);

        PointToDraw.SetCoords(0, 1.0, 0);
        Draw3D(g, PointToDraw, IDENTITY, CAMERA);


     }

     /*
     * Draws my name, class, date, and assignment #
     * Drawn in Quadrant 1, Top Left corner
     */
     public static void Branding(Graphics g, String str){
        g.setColor(Color.black);
        g.drawString("Jordan Leithart", FRAME_WIDTH/2, 20);
        g.drawString("CS 324", FRAME_WIDTH/2, 35);
        g.drawString("February 14, 2014", FRAME_WIDTH/2, 50);
        g.drawString("Assignment 4", FRAME_WIDTH/2, 65);
        g.drawString(str, FRAME_WIDTH/2, 80);
     }

     public static Assembly [] AppendToArray(Assembly []input, Assembly addition){
        Assembly []newArray = new Assembly[input.length + 1];
        for(int i = 0; i < input.length; i++){
            newArray[i] = input[i];
        }
        newArray[newArray.length - 1] = addition;
        return newArray;
     }

     public static void DrawRubiksCube(Graphics g, double space){
        double spacing = 2.0 + space;   //size of cube + the space we want
        //draw a square
        Point3D [] tmpPoints = new Point3D [4];
        tmpPoints[0] = new Point3D(-1, -1, 1);
        tmpPoints[1] = new Point3D(-1, 1, 1);
        tmpPoints[2] = new Point3D(1, 1, 1);
        tmpPoints[3] = new Point3D(1, -1, 1);

        double [][]ActiveTransform = new double[4][4];

        Object square = new Object(tmpPoints, IDENTITY, "Square");
        Assembly []EMPTY = new Assembly[0];
        Assembly []cube_array = new Assembly[0];
        Assembly []row_array = new Assembly[0];
        Assembly []side_array = new Assembly[0];
        Assembly []rubik_array = new Assembly[0];

//        square.DrawObject(g);
        Assembly front = new Assembly(EMPTY, IDENTITY, square, "Front");
        cube_array = AppendToArray(cube_array, front);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_ROT, 90);
        Assembly bot = new Assembly(EMPTY, ActiveTransform, square, "Bottom");
        cube_array = AppendToArray(cube_array, bot);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_ROT, 180);
        Assembly back = new Assembly(EMPTY, ActiveTransform, square, "Back");
        cube_array = AppendToArray(cube_array, back);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_ROT, -90);
        Assembly top = new Assembly(EMPTY, ActiveTransform, square, "Top");
        cube_array = AppendToArray(cube_array, top);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        Assembly left = new Assembly(EMPTY, ActiveTransform, square, "Left");
        cube_array = AppendToArray(cube_array, left);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        Assembly right = new Assembly(EMPTY, ActiveTransform, square, "Right");
        cube_array = AppendToArray(cube_array, right);

        Assembly cube = new Assembly(cube_array, IDENTITY, null, "Cube 1");
        row_array = AppendToArray(row_array, cube);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, spacing);
        Assembly cube2 = new Assembly(cube_array, ActiveTransform, null, "Cube 2");
        row_array = AppendToArray(row_array, cube2);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, -spacing);
        Assembly cube3 = new Assembly(cube_array, ActiveTransform, null, "Cube 3");
        row_array = AppendToArray(row_array, cube3);

        Assembly row1 = new Assembly(row_array, IDENTITY, null, "Row 1");
        side_array = AppendToArray(side_array, row1);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_TRANS, spacing);
        Assembly row2 = new Assembly(row_array, ActiveTransform, null, "Row 2");
        side_array = AppendToArray(side_array, row2);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_TRANS, -spacing);
        Assembly row3 = new Assembly(row_array, ActiveTransform, null, "Row 3");
        side_array = AppendToArray(side_array, row3);

        Assembly side1 = new Assembly(side_array, IDENTITY, null, "Side 1");
        rubik_array = AppendToArray(rubik_array, side1);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Z_TRANS, spacing);
        Assembly side2 = new Assembly(side_array, ActiveTransform, null, "Side 2");
        rubik_array = AppendToArray(rubik_array, side2);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Z_TRANS, -spacing);
        Assembly side3 = new Assembly(side_array, ActiveTransform, null, "Side 3");
        rubik_array = AppendToArray(rubik_array, side3);

        g.setColor(Color.red);
        side1.Assemble(g, IDENTITY);
        g.setColor(Color.blue);
        side2.Assemble(g, IDENTITY);
        g.setColor(Color.green);
        side3.Assemble(g, IDENTITY);
        //Assembly rubik = new Assembly(rubik_array, IDENTITY, null, "Rubik's Cube");


        //cube.Assemble(g, IDENTITY);
        //cube2.Assemble(g, IDENTITY);
        //cube3.Assemble(g, IDENTITY);

        //row1.Assemble(g, IDENTITY);
        //row2.Assemble(g, IDENTITY);
        //row3.Assemble(g, IDENTITY);
        //side.Assemble(g, IDENTITY);

        //rubik.Assemble(g, IDENTITY);

        //copy and tranform into a row

        //copy and transform row into a side


        //copy and transform side into cube
     }

     public static void DrawHallway(Graphics g){

        double [][]ActiveTransform = new double[4][4];
        Assembly []EMPTY = new Assembly[0];
        Assembly []ceilingAssembly = new Assembly[0];
        Assembly []doorAssembly = new Assembly[0];

        //origin
        Point3D [] floorPoints = new Point3D [9];
        floorPoints[0] = new Point3D(-70.75, 0, 0);
        floorPoints[1] = new Point3D(70.75, 0, 0);
        floorPoints[2] = new Point3D(70.75, 0, -727.125);
        floorPoints[3] = new Point3D(-70.75, 0, -727.125);
        floorPoints[4] = new Point3D(-70.75, 0, -232.5);
        floorPoints[5] = new Point3D(-156.5, 0, -232.5);
        floorPoints[6] = new Point3D(-156.5, 0, -151.5);
        floorPoints[7] = new Point3D(-70.75, 0, -151.5);
        floorPoints[8] = new Point3D(-70.75, 0, 0);

        Object floor = new Object(floorPoints, IDENTITY, "Floor");
        floor.DrawObject(g, IDENTITY);
        ConnectWalls(g, floor);

        //Assembly rubik = new Assembly(rubik_array, IDENTITY, null, "Rubik's Cube");
        // HEIGHT IS 132
        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_TRANS, 132);
        Assembly ceiling = new Assembly(EMPTY, ActiveTransform, floor, "Ceiling");

        ceiling.Assemble(g, IDENTITY);

        Point3D [] pillarPoints = new Point3D[4];
        pillarPoints[0] = new Point3D(-70.75, 0, -182.5);
        pillarPoints[1] = new Point3D(-70.75, 0, -201.5);
        pillarPoints[2] = new Point3D(-88.75, 0, -201.5);
        pillarPoints[3] = new Point3D(-88.75, 0, -182.5);

        Object pillar = new Object(pillarPoints, IDENTITY, "Pillar");
        pillar.DrawObject(g, IDENTITY);

        Assembly pillarAssembly = new Assembly(EMPTY, ActiveTransform, pillar, "Top of Pillar");
        pillarAssembly.Assemble(g, IDENTITY);
        ConnectWalls(g, pillar);

        Point3D[] doorPoints = new Point3D[4];
        doorPoints[0] = new Point3D(0, 0, 0);
        doorPoints[1] = new Point3D(35.75, 0, 0);
        doorPoints[2] = new Point3D(35.75, 87, 0);
        doorPoints[3] = new Point3D(0, 87, 0);

        Object door = new Object(doorPoints, IDENTITY, "Door");
        //door.DrawObject(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -53.75);
        Assembly door1 = new Assembly(EMPTY, ActiveTransform, door, "Alves Foss");

        door1.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -251.375);
        Assembly door2 = new Assembly(EMPTY, ActiveTransform, door, "Terrence Soule");

        door2.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -369.375);
        Assembly door3 = new Assembly(EMPTY, ActiveTransform, door, "Daniel Conte De Leon");

        door3.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -568.125);
        Assembly door4 = new Assembly(EMPTY, ActiveTransform, door, "Greg Donahoe");

        door4.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -644.875);
        Assembly door5 = new Assembly(EMPTY, ActiveTransform, door, "Robert Rinker");

        door5.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -180);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 62.25);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -727.125);
        Assembly door6 = new Assembly(EMPTY, ActiveTransform, door, "East Office Door");

        door6.Assemble(g, IDENTITY);

        Point3D []windowPoints = new Point3D[4];
        windowPoints[0] = new Point3D(-51.75, 38, -727.125);
        windowPoints[1] = new Point3D(16.25, 38, -727.125);
        windowPoints[2] = new Point3D(16.25, 86, -727.125);
        windowPoints[3] = new Point3D(-51.75, 86, -727.125);
        Object window = new Object(windowPoints, IDENTITY, "Window");

        window.DrawObject(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -691.375);
        Assembly door7 = new Assembly(EMPTY, ActiveTransform, door, "North Entrance");

        door7.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -655.625);
        Assembly door8 = new Assembly(EMPTY, ActiveTransform, door, "North Entrance");

        door8.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -474.875);
        Assembly door9 = new Assembly(EMPTY, ActiveTransform, door, "CS Office");

        door9.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -156.5);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -151.5);
        Assembly door10 = new Assembly(EMPTY, ActiveTransform, door, "Clinton Jeffery");

        door10.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, -123.5);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -151.5);
        Assembly door11 = new Assembly(EMPTY, ActiveTransform, door, "Clinton Jeffery Side Entrance");

        door11.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, 90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -156.5);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -196.75);
        Assembly door12 = new Assembly(EMPTY, ActiveTransform, door, "Bruce Bolden");

        door12.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -180);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, -87.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -232.5);
        Assembly door13 = new Assembly(EMPTY, ActiveTransform, door, "Bruce Bolden Side Entrance");

        door13.Assemble(g, IDENTITY);

        // boards on the wall
        Point3D[] boardPoints = new Point3D[4];
        boardPoints[0] = new Point3D(0, 0, 0);
        boardPoints[1] = new Point3D(36, 0, 0);
        boardPoints[2] = new Point3D(36, 24, 0);
        boardPoints[3] = new Point3D(0, 24, 0);

        Object board = new Object(boardPoints, IDENTITY, "Board");

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, 48);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -107.75);
        Assembly board1 = new Assembly(EMPTY, ActiveTransform, board, "Alves Foss Board");

        board1.Assemble(g, IDENTITY);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.Y_ROT, -90);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.X_TRANS, 70.75);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, 48);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -107.75);
        Assembly board2 = new Assembly(EMPTY, ActiveTransform, board, "Alves Foss Board");

        board1.Assemble(g, IDENTITY);

        Branding(g, "Hallway Model");


     }

     public static void ConnectWalls(Graphics g, Object floorObject){
        Point3D topPoint = new Point3D(0, 0, 0);
        for(int i = 0; i < floorObject.vertices.length; i++){
            Move3D(floorObject.vertices[i], floorObject.aT, CAMERA);
            topPoint.SetCoords(floorObject.vertices[i].x, 132, floorObject.vertices[i].z);
            Draw3D(g, topPoint, floorObject.aT, CAMERA);
        }
     }

     public static void DrawName(Graphics g){

        double [][]ActiveTransform = IDENTITY;

        double startSpot = -12;
        //J
        Point3D []jpoints = new Point3D[10];
        jpoints[0] = new Point3D(0, 0, 0);
        jpoints[1] = new Point3D(3, 0, 0);
        jpoints[2] = new Point3D(3, -1, 0);
        jpoints[3] = new Point3D(2, -1, 0);
        jpoints[4] = new Point3D(2, -5, 0);
        jpoints[5] = new Point3D(0, -5, 0);
        jpoints[6] = new Point3D(0, -4, 0);
        jpoints[7] = new Point3D(1, -4, 0);
        jpoints[8] = new Point3D(1, -1, 0);
        jpoints[9] = new Point3D(0, -1, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot);
        Object J = new Object(jpoints, ActiveTransform, "J letter");
        
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object J_Back = new Object(jpoints, ActiveTransform, "J back");
        J.DrawObject(g, IDENTITY);
        J_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, J);

        //O
        Point3D []opoints_outer = new Point3D[4];
        opoints_outer[0] = new Point3D(0, 0, 0);
        opoints_outer[1] = new Point3D(3, 0, 0);
        opoints_outer[2] = new Point3D(3, -3, 0);
        opoints_outer[3] = new Point3D(0, -3, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot + 4);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, -2);
        Object O1 = new Object(opoints_outer, ActiveTransform, "O Letter");

        Point3D []opoints_inner = new Point3D[4];
        opoints_inner[0] = new Point3D(1, -1, 0);
        opoints_inner[1] = new Point3D(2, -1, 0);
        opoints_inner[2] = new Point3D(2, -2, 0);
        opoints_inner[3] = new Point3D(1, -2, 0);

        Object O2 = new Object(opoints_inner, ActiveTransform, "inside o letter");

        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object O1_Back = new Object(opoints_outer, ActiveTransform, "O letter back");
        Object O2_Back = new Object(opoints_inner, ActiveTransform, "Inside o back");

        O1.DrawObject(g, IDENTITY);
        O2.DrawObject(g, IDENTITY);
        O1_Back.DrawObject(g, IDENTITY);
        O2_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, O1);
        ConnectLetters(g, O2);

        //R
        Point3D []rpoints_outer = new Point3D[9];
        rpoints_outer[0] = new Point3D(0, 0, 0);
        rpoints_outer[1] = new Point3D(3, 0, 0);
        rpoints_outer[2] = new Point3D(3, -1, 0);
        rpoints_outer[3] = new Point3D(2, -1, 0);
        rpoints_outer[4] = new Point3D(3, -2.5, 0);
        rpoints_outer[5] = new Point3D(2.5, -3, 0);
        rpoints_outer[6] = new Point3D(1, -1.5, 0);
        rpoints_outer[7] = new Point3D(1, -3, 0);
        rpoints_outer[8] = new Point3D(0, -3, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot + 8);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, -2);
        Object R1 = new Object(rpoints_outer, ActiveTransform, "R outside");

        Point3D []rpoints_inner = new Point3D[4];
        rpoints_inner[0] = new Point3D(1, -0.5, 0);
        rpoints_inner[1] = new Point3D(2, -0.5, 0);
        rpoints_inner[2] = new Point3D(2, -0.75, 0);
        rpoints_inner[3] = new Point3D(1, -0.75, 0);

        Object R2 = new Object(rpoints_inner, ActiveTransform, "R inside");

        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object R1_Back = new Object(rpoints_outer, ActiveTransform, "R back outside");
        Object R2_Back = new Object(rpoints_inner, ActiveTransform, "R back inside");

        R1.DrawObject(g, IDENTITY);
        R2.DrawObject(g, IDENTITY);
        R1_Back.DrawObject(g, IDENTITY);
        R2_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, R1);
        ConnectLetters(g, R2);


        //D
        Point3D []dpoints_outer = new Point3D[5];
        dpoints_outer[0] = new Point3D(0, 0, 0);
        dpoints_outer[1] = new Point3D(2, 0, 0);
        dpoints_outer[2] = new Point3D(3, -1.5, 0);
        dpoints_outer[3] = new Point3D(2, -3, 0);
        dpoints_outer[4] = new Point3D(0, -3, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot + 12);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, -2);
        Object D1 = new Object(dpoints_outer, ActiveTransform, "D Outside");

        Point3D []dpoints_inner = new Point3D[4];
        dpoints_inner[0] = new Point3D(0.75, -1, 0);
        dpoints_inner[1] = new Point3D(1.5, -1, 0);
        dpoints_inner[2] = new Point3D(1.5, -2, 0);
        dpoints_inner[3] = new Point3D(0.75, -2, 0);

        Object D2 = new Object(dpoints_inner, ActiveTransform, "D inside");

        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object D1_Back = new Object(dpoints_outer, ActiveTransform, "D Outside Back");
        Object D2_Back = new Object(dpoints_inner, ActiveTransform, "D Inside Back");

        D1.DrawObject(g, IDENTITY);
        D2.DrawObject(g, IDENTITY);
        D1_Back.DrawObject(g, IDENTITY);
        D2_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, D1);
        ConnectLetters(g, D2);

        //A
        Point3D []apoints_outer = new Point3D[8];
        apoints_outer[0] = new Point3D(1, 0, 0);
        apoints_outer[1] = new Point3D(2, 0, 0);
        apoints_outer[2] = new Point3D(3, -3, 0);
        apoints_outer[3] = new Point3D(2, -3, 0);
        apoints_outer[4] = new Point3D(1.75, -2, 0);
        apoints_outer[5] = new Point3D(1.25, -2, 0);
        apoints_outer[6] = new Point3D(1, -3, 0);
        apoints_outer[7] = new Point3D(0, -3, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot + 15);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, -2);
        Object A1 = new Object(apoints_outer, ActiveTransform, "A outside");

        Point3D []apoints_inner = new Point3D[3];
        apoints_inner[0] = new Point3D(1.5, -1, 0);
        apoints_inner[1] = new Point3D(1.75, -1.5, 0);
        apoints_inner[2] = new Point3D(1.25, -1.5, 0);
        Object A2 = new Object(apoints_inner, ActiveTransform, "A inside");

        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object A1_Back = new Object(apoints_outer, ActiveTransform, "A outside back");
        Object A2_Back = new Object(apoints_inner, ActiveTransform, "A inside back");

        A1.DrawObject(g, IDENTITY);
        A2.DrawObject(g, IDENTITY);
        A1_Back.DrawObject(g, IDENTITY);
        A2_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, A1);
        ConnectLetters(g, A2);

        //N
        Point3D []npoints = new Point3D[10];
        npoints[0] = new Point3D(0, 0, 0);
        npoints[1] = new Point3D(1, 0, 0);
        npoints[2] = new Point3D(2, -2, 0);
        npoints[3] = new Point3D(2, 0, 0);
        npoints[4] = new Point3D(3, 0, 0);
        npoints[5] = new Point3D(3, -3, 0);
        npoints[6] = new Point3D(2, -3, 0);
        npoints[7] = new Point3D(1, -1, 0);
        npoints[8] = new Point3D(1, -3, 0);
        npoints[9] = new Point3D(0, -3, 0);

        ActiveTransform = BuildElementaryTransform(IDENTITY, TRANSFORM_CODE.X_TRANS, startSpot + 18.5);
        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Y_TRANS, -2);
        Object N = new Object(npoints, ActiveTransform, "N Letter");

        ActiveTransform = BuildElementaryTransform(ActiveTransform, TRANSFORM_CODE.Z_TRANS, -1);
        Object N_Back = new Object(npoints, ActiveTransform, "N Letter Back");

        N.DrawObject(g, IDENTITY);
        N_Back.DrawObject(g, IDENTITY);
        ConnectLetters(g, N);

        Branding(g, "3D Letters");

     }

     public static void ConnectLetters(Graphics g, Object letter){
        Point3D topPoint = new Point3D(0, 0, 0);
        for(int i = 0; i < letter.vertices.length; i++){
            Move3D(letter.vertices[i], letter.aT, CAMERA);
            topPoint.SetCoords(letter.vertices[i].x, letter.vertices[i].y, -1);
            Draw3D(g, topPoint, letter.aT, CAMERA);
        }
     }

}
