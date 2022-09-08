package com.example.ntugroup35;

import android.content.Context;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.example.ntugroup35.Bluetooth.BluetoothFragment;

public class MazeGrid extends View {
    // dimensions of canvas
    private int width;
    private int height;
    private float cellHeight;
    private float cellWidth;
    private float adjustX;
    private float adjustY;
    private float toolbar;
    private float obstacleSideWidth = 5;

    // grid properties
    private static final int NUMCOL = 20; // no. of columns
    private static final int NUMROW = 20; //no. of rows
    private static final int padding = 20;
    private static final int border = 5;

    // Paint - coloring
    private final Paint whitePaint = new Paint();
    private final Paint grayPaint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint coordinatesPaint = new Paint();
    private final Paint whiteNumber = new Paint();
    private final Paint yellowPaint = new Paint();
    private final Paint exploredWhiteNumber = new Paint();

    // Images
    private final Bitmap robotBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.robot);
    private final Bitmap robotBoxBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.robot2);
    private final Bitmap obstacleBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.obstacleblock);

    // Handle motion
    private int firstX;
    private int firstY;
    private Coordinates dragObject;

    // Toolbar buttons
    public static final int toolBarLeft = 22;
    public static final int toolBarRight = 22 + 2;
    public static final int robotBoxBottom = 18;
    public static final int robotBoxTop = 18 + 2;
    public static final int obstacleBoxBottom = 16 - 2;
    public static final int obstacleBoxTop = 16;

    public MazeGrid(Context context){
        this(context, null);
    }

    public MazeGrid(Context context, AttributeSet attrs){
        super(context, attrs);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setShadowLayer(border, 0, 0, Color.GRAY);
        grayPaint.setColor(Color.DKGRAY);
        blackPaint.setColor(Color.BLACK);
        coordinatesPaint.setColor(Color.DKGRAY);
        coordinatesPaint.setTextSize(20);
        coordinatesPaint.setTextAlign(Paint.Align.CENTER);
        whiteNumber.setColor(Color.WHITE);
        whiteNumber.setTextSize(20);
        whiteNumber.setTextAlign(Paint.Align.CENTER);
        yellowPaint.setColor(Color.YELLOW);
        exploredWhiteNumber.setColor(Color.WHITE);
        exploredWhiteNumber.setTextSize(22);
        exploredWhiteNumber.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        calculateDimensions();

        // draw white area of canvas
        canvas.drawRoundRect(border, border, width - border - toolbar, height - border,
                10, 10, whitePaint);

        // draw obstacle box
        drawObstacleBox(canvas);

        // draw obstacles
        drawObstacles(canvas);

        // draw grid lines and coordinates
        drawCoordinates(canvas);

        // draw robot
        drawRobot(canvas, MainActivity.robot.getX(), MainActivity.robot.getY(), MainActivity.robot.getDirection());
    }

    private void calculateDimensions(){
        int toolbarNumOfCells = 4;
        this.width = getWidth();
        this.height = getHeight();
//        this.cellWidth = (float) (width - padding*2 - border*2) / (NUMCOL + 1);
        this.cellWidth = (float) (width - padding*2 - border*2) / (NUMCOL + toolbarNumOfCells + 1);
        this.cellHeight = (float) (height - padding*2 - border*2) / (NUMROW + 1);
        this.adjustX = padding + border + cellWidth;
        this.adjustY = padding + border;
        this.toolbar = toolbarNumOfCells * cellWidth;
    }

    private void drawCoordinates(Canvas canvas){
        float offsetX = padding + border + cellWidth;
        float offsetY = padding + border;
        for (int i = 0; i <= NUMCOL; i++){
            canvas.drawLine(offsetX + i * cellWidth, offsetY, offsetX + i * cellWidth,
                    offsetY + cellHeight * NUMROW, grayPaint);
        }

        for (int i = 0; i <= NUMROW; i++){
            canvas.drawLine(offsetX, offsetY + i * cellHeight, offsetX + cellWidth * (NUMCOL),
                    offsetY + i * cellHeight, grayPaint);
        }

        float textSize = this.coordinatesPaint.getTextSize();
        for (int i = 1; i <= NUMCOL; i++){
            canvas.drawText(String.valueOf(i), (float) (offsetX + this.cellWidth * (i - 0.5)), offsetY +
                    this.cellHeight * (float) (NUMROW + 0.7), this.coordinatesPaint);
        }
        for (int i = 1; i <= NUMROW; i++){
            canvas.drawText(String.valueOf(i), offsetX - this.cellWidth/2, (float) (offsetY +
                    this.cellHeight * (NUMROW - i + 0.5) + textSize/2), this.coordinatesPaint);
        }
    }

    private void drawRobot(Canvas canvas, int col, int row, char direction){
        Matrix robotBoxMatrix = new Matrix();
        Bitmap robotBoxBitmap = Bitmap.createBitmap(this.robotBoxBitmap,0,0, this.robotBoxBitmap.getWidth(),
                this.robotBoxBitmap.getHeight(), robotBoxMatrix, true);
        canvas.drawBitmap(robotBoxBitmap, null, new RectF(adjustX + (toolBarLeft - 1) * cellWidth,
                adjustY + (NUMROW - robotBoxTop) * cellHeight, adjustX + toolBarRight * cellWidth,
                adjustY + (NUMROW - robotBoxBottom + 1) * cellHeight), null);

        if (row == -1 || col == -1){
            row = 18;
            col = 22;
        }
        int deg = 0;
        if (direction == 'S'){
            deg = 180;
        }else if (direction == 'E'){
            deg = 90;
        }else if (direction == 'W'){
            deg = 270;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(deg);

        Bitmap rotatedBitmap = Bitmap.createBitmap(robotBitmap,0,0, robotBitmap.getWidth(),
                robotBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap, null, new RectF(adjustX + (col - 1) * cellWidth,
                adjustY + (NUMROW - row- 2) * cellHeight, adjustX + (col + 2) * cellWidth,
                adjustY + (NUMROW - row + 1) * cellHeight), null);

    }

    private void drawObstacles(Canvas canvas){
        float textSize = this.whiteNumber.getTextSize();
        Maze maze = Maze.getInstance();
        for (Obstacle obstacle: maze.getObstacles()){
            int x = obstacle.getX();
            int y = obstacle.getY();
            canvas.drawRect(adjustX + (x - 1) * cellWidth, adjustY + cellHeight * (NUMROW - y), adjustX + x * cellWidth, adjustY + cellHeight * (NUMROW - y + 1), blackPaint);
            if (obstacle.isExplored()){
                canvas.drawText(String.valueOf(obstacle.getTargetID()), adjustX + (float) (x - 1 + 0.5) * cellWidth, adjustY + cellHeight * (NUMROW - y) + (cellHeight - textSize)/2 + textSize, exploredWhiteNumber);
            } else{
                canvas.drawText(String.valueOf(obstacle.getNumberObs()), adjustX + (float) (x - 1 + 0.5) * cellWidth, adjustY + cellHeight * (NUMROW - y) + (cellHeight - textSize)/2 + textSize, whiteNumber);
            }
            switch (obstacle.getSide()){
                case 'N':
                    canvas.drawRect(adjustX + (x - 1) * cellWidth, adjustY + cellHeight * (NUMROW - y),
                            adjustX + x * cellWidth, adjustY + cellHeight * (NUMROW - y) + obstacleSideWidth, yellowPaint);
                    break;
                case 'S':
                    canvas.drawRect(adjustX + (x - 1) * cellWidth, adjustY + cellHeight * (NUMROW - y + 1) - obstacleSideWidth,
                            adjustX + x * cellWidth, adjustY + cellHeight * (NUMROW - y + 1), yellowPaint);
                    break;
                case 'E':
                    canvas.drawRect(adjustX + x * cellWidth - obstacleSideWidth, adjustY + cellHeight * (NUMROW - y),
                            adjustX + x * cellWidth, adjustY + cellHeight * (NUMROW - y + 1), yellowPaint);
                    break;
                case 'W':
                    canvas.drawRect(adjustX + (x - 1) * cellWidth, adjustY + cellHeight * (NUMROW - y),
                            adjustX + (x - 1) * cellWidth + obstacleSideWidth, adjustY + cellHeight * (NUMROW - y + 1), yellowPaint);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawObstacleBox(Canvas canvas){
        Matrix matrix = new Matrix();
        Bitmap bm = Bitmap.createBitmap(obstacleBitmap,0,0, obstacleBitmap.getWidth(), obstacleBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(bm, null, new RectF(adjustX + (toolBarLeft - 1) * cellWidth, adjustY + (NUMROW - obstacleBoxTop) * cellHeight,
                adjustX + toolBarRight * cellWidth, adjustY + (NUMROW - obstacleBoxBottom + 1) * cellHeight), null);

    }

    // Gesture detector for handling long presses
    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent event) {
            int selectedX = (int) (((event.getX() - adjustX)/cellWidth) + 1);
            int selectedY = (int) (NUMROW - ((event.getY() - adjustY)/cellHeight) + 1);
            Coordinates obstacle = Maze.getInstance().findObstacle(selectedX, selectedY);
            if (obstacle != null){
                MainActivity.obstacleDirectionPopup(getContext(), getRootView(), (Obstacle) obstacle);
            }
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragObject = null;
                firstX = (int) (((event.getX() - adjustX)/cellWidth) + 1);
                firstY = (int) (NUMROW - ((event.getY() - adjustY)/cellHeight) + 1);

                // Touch robot on map
                if (MainActivity.robot.containsCoordinate(firstX, firstY)){
                    dragObject = MainActivity.robot;
                    // Robot not on map and touch robot button
                } else if ((MainActivity.robot.getX() == -1 || MainActivity.robot.getY() == -1) && (toolBarLeft <= firstX && firstX <= toolBarRight &&
                        robotBoxBottom <= firstY && firstY <= robotBoxTop)){
                    dragObject = MainActivity.robot;
                    // Touch obstacle button
                } else if (toolBarLeft <= firstX && firstX <= toolBarRight && obstacleBoxBottom <= firstY && firstY <= obstacleBoxTop){
                    dragObject = Maze.getInstance().addObstacle();
                } else {
                    dragObject = Maze.getInstance().findObstacle(firstX, firstY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int movingX = (int) (((event.getX() - adjustX)/cellWidth) + 1);
                int movingY = (int) (NUMROW - ((event.getY() - adjustY)/cellHeight) + 1);
                if (dragObject instanceof Robot){
                    if ((movingX >= 1 && movingX <= 18) && (movingY >= 1 && movingY <= 18)){
                        MainActivity.robot.setCoordinates(movingX, movingY);
                        MainActivity.textX.setText(String.valueOf(MainActivity.robot.getX()));
                        MainActivity.textY.setText(String.valueOf(MainActivity.robot.getY()));
                        MainActivity.textDirection.setText(String.valueOf(MainActivity.robot.getDirection()));
                        invalidate();
                    }
                } else if (dragObject instanceof Obstacle){
                    if ((movingX >= 1 && movingX <= NUMCOL) && (movingY >= 1 && movingY <= NUMROW)){
                        if (Maze.getInstance().findObstacle(movingX, movingY) == null){
                            dragObject.setCoordinates(movingX, movingY);
                        }
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int finalX = (int) (((event.getX() - adjustX)/cellWidth) + 1);
                int finalY = (int) (NUMROW - ((event.getY() - adjustY)/cellHeight) + 1);
                // when robot is drag and drop
                if (dragObject instanceof Robot){
                    MainActivity.outgoingMessage("PC,RP," + MainActivity.robot.getX() + "," + MainActivity.robot.getY());
                    //fragment.sendMsg1("PC,RP," + MainActivity.robot.getX() + "," + MainActivity.robot.getY());
                    if ((firstX == MainActivity.robot.getX() && firstY == MainActivity.robot.getY())
                            || (firstX == MainActivity.robot.getX() && firstY == MainActivity.robot.getY()+1)
                            || (firstX == MainActivity.robot.getX() && firstY == MainActivity.robot.getY()+2)
                            || (firstX == MainActivity.robot.getX()+1 && firstY == MainActivity.robot.getY())
                            || (firstX == MainActivity.robot.getX()+1 && firstY == MainActivity.robot.getY()+1)
                            || (firstX == MainActivity.robot.getX()+1 && firstY == MainActivity.robot.getY()+2)
                            || (firstX == MainActivity.robot.getX()+2 && firstY == MainActivity.robot.getY())
                            || (firstX == MainActivity.robot.getX()+2 && firstY == MainActivity.robot.getY()+1)
                            || (firstX == MainActivity.robot.getX()+2 && firstY == MainActivity.robot.getY()+2)){
                        if ((finalX < 1 || finalX > 18) || (finalY < 1 || finalY > 18)){
                            MainActivity.robot.reset();
                            MainActivity.textX.setText("-");
                            MainActivity.textY.setText("-");
                            MainActivity.textDirection.setText("-");
                        }
                        else{
                            MainActivity.robot.setCoordinates(finalX, finalY);
                            MainActivity.textX.setText(String.valueOf(MainActivity.robot.getX()));
                            MainActivity.textY.setText(String.valueOf(MainActivity.robot.getY()));
                            MainActivity.textDirection.setText(String.valueOf(MainActivity.robot.getDirection()));
                        }
                        invalidate();
                    }
                } else if (dragObject instanceof Obstacle){
                    if ((finalX < 1 || finalX > NUMCOL) || (finalY < 1 || finalY > NUMROW)){
                        Maze.getInstance().removeObstacle((Obstacle) dragObject);
                        MainActivity.outgoingMessage("Removed Obstacle No. " + ((Obstacle) dragObject).getNumberObs());
                    } else {
                        // If finger is released at a square
                        if (!Maze.getInstance().isOccupied(finalX, finalY, (Obstacle) dragObject)) {
                            dragObject.setCoordinates(finalX, finalY);
                        }
                        MainActivity.outgoingMessage("OB" + ((Obstacle) dragObject).getNumberObs() + "," + dragObject.getX() + "," + dragObject.getY() + ",");
                    }
                    invalidate();
                }
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void setObstacleDirection(Obstacle obstacle, char side){
        obstacle.setSide(side);
        char direction = obstacle.getSide();
        // send to RPI to send to algo
        MainActivity.outgoingMessage("PC," + obstacle.getNumberObs() + "," + obstacle.getX() +
                "," + obstacle.getY() + "," + directionTo1234(direction, "0"));
        invalidate();
    }

    public String directionTo1234(char direction, String num){
        if (direction == 'N')
            num = "1";
        else if (direction == 'S')
            num = "2";
        else if (direction == 'E')
            num = "3";
        else if (direction == 'W')
            num = "4";
        return num;
    }

}
