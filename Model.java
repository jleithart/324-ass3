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
    static int FRAME_HEIGHT = 800;
    static int FRAME_WIDTH = 800;

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
        DrawAxis(g);
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
        DefineWindow(-5, -5, 5, 5);

        Point3D focalPoint = new Point3D(1, 0, 1);
        DefineCameraTransform(focalPoint, 30, 45, 0, 20);

        // set the values of each viewport
        //SetViewport();
        //SetWindow();

     }

     public static double [][] MatrixMultiply(double[][] M, double[][] N){
		assert(M[0].length == N.length);
		int mColumns = M.length;
		int mRows = M[0].length;
		int nColumns = N.length;
		int nRows = N[0].length;

		System.out.println("rows::" + mRows + " cols::" + nColumns);

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
     	tmpCoordinate.y = LinearInterpolation(curVP.BotY, curVP.TopY, curWin.BotY, curWin.TopY, point.y);

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
     	double radianVal = 3.14*transformValue/180;

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

     	retMatrix = MatrixMultiply(inputMatrix, transMatrix);
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
     	//M2 = MatrixMultiply(transformMatrix, M);

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

     public static void MultiplyTransforms(){

     }

     public static Point3D ApplyTransform(Point3D point, double[][] activeTransform){
     	double [][]vector = { {point.x, point.y, point.z, 1} };
     	double [][]retVector = new double[1][4];
     	retVector = MatrixMultiply(vector, activeTransform);

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

     // TODO! REWORK FOR SINGLE VIEWPORT
     public static drawPoint ViewPortToFrameWindow(Point2D coordinate){

        drawPoint tmpCoordinate = new drawPoint(0, 0);

        // given a viewport coordinate, find the pixel placement
        //user linear interpolation:
        // x = x(0) + x1 - x0(y-y0/y1-y0)
        tmpCoordinate.x = (int) LinearInterpolation(0, FRAME_WIDTH, curVP.LeftX, curVP.RightX, coordinate.x);
        tmpCoordinate.y = (int) LinearInterpolation(FRAME_HEIGHT, 0, curVP.BotY, curVP.TopY, coordinate.y);
        

        
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

        g.drawLine(drawFirstPoint.x + 5, drawFirstPoint.y - 5, drawSecondPoint.x - 5, drawFirstPoint.y - 5);
        g.drawLine(drawSecondPoint.x - 5, drawFirstPoint.y - 5, drawSecondPoint.x - 5, drawSecondPoint.y + 5);
        g.drawLine(drawSecondPoint.x - 5, drawSecondPoint.y + 5, drawFirstPoint.x + 5, drawSecondPoint.y + 5);
        g.drawLine(drawFirstPoint.x + 5, drawSecondPoint.y + 5, drawFirstPoint.x + 5, drawFirstPoint.y - 5);
     }

     /*
     * Draws the axis for the window passed in
     */

     //TODO REWORK FOR 3D
     public static void DrawAxis(Graphics g)
     {

        drawPoint drawFirstPoint;
        drawPoint drawSecondPoint;

        //x-axis
        Point3D lowAxisPoint = new Point3D(-5, 0, 0);
        Point3D hiAxisPoint = new Point3D(5, 0, 0);

        Move3D(lowAxisPoint, IDENTITY, CAMERA);
        Draw3D(g, hiAxisPoint, IDENTITY, CAMERA);

        //x-axis
        /*firstAxisPoint.SetCoords(WindowList[window].LeftX, (WindowList[window].BotY + WindowList[window].TopY)/2);
        secondAxisPoint.SetCoords(WindowList[window].RightX, (WindowList[window].BotY + WindowList[window].TopY)/2);

        firstViewPoint = WindowToViewPort(firstAxisPoint, window);
        secondViewPoint = WindowToViewPort(secondAxisPoint, window);


        drawFirstPoint = ViewPortToFrameWindow(firstViewPoint, WindowList[window].Quadrant);
        drawSecondPoint = ViewPortToFrameWindow(secondViewPoint, WindowList[window].Quadrant);
        g.drawLine(drawFirstPoint.x, drawFirstPoint.y, drawSecondPoint.x, drawSecondPoint.y);
        //end x-axis
        */

        //y-axis
        // the 1st quadrant has no negative axis
        /*
        if(window != 0){
            firstAxisPoint.SetCoords((WindowList[window].LeftX + WindowList[window].RightX)/2, WindowList[window].BotY);
            secondAxisPoint.SetCoords((WindowList[window].LeftX + WindowList[window].RightX)/2, WindowList[window].TopY);
        }
        else{
            firstAxisPoint.SetCoords(WindowList[window].LeftX, WindowList[window].BotY);
            secondAxisPoint.SetCoords(WindowList[window].LeftX, WindowList[window].TopY);
        }

        firstViewPoint = WindowToViewPort(firstAxisPoint, window);
        secondViewPoint = WindowToViewPort(secondAxisPoint, window);


        drawFirstPoint = ViewPortToFrameWindow(firstViewPoint, WindowList[window].Quadrant);
        drawSecondPoint = ViewPortToFrameWindow(secondViewPoint, WindowList[window].Quadrant);
        g.drawLine(drawFirstPoint.x, drawFirstPoint.y, drawSecondPoint.x, drawSecondPoint.y);
        //end y axis
        */
     }

     /*
     * Draws my name, class, date, and assignment #
     * Drawn in Quadrant 1, Top Left corner
     */
     public static void Branding(Graphics g){
        g.setColor(Color.black);
        g.drawString("Jordan Leithart", 5, 10);
        g.drawString("CS 324", 5, 20);
        g.drawString("February 14, 2014", 5, 30);
        g.drawString("Assignment 2", 5, 40);
     }

}
