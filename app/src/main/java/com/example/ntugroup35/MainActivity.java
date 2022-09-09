package com.example.ntugroup35;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ntugroup35.Bluetooth.BluetoothFragment;

public class MainActivity extends AppCompatActivity {
    /**
     * Tag for log in Main Activity
     */
    public static final String TAG = "MainActivity";
    /**
     * Robot instance
     */
    static Robot robot = new Robot();
    private static BluetoothFragment fragment;
//    /**
//     * Tilt class
//     */
//    private Tilt tilt;
//    /**
//     * Mutable Live Data
//     */
//    MutableLiveData<String> listen = new MutableLiveData<>();
    /**
     * Text View for x coordinate
     */
    public static TextView textX;
    /**
     * Text View for y coordinate
     */
    public static TextView textY;
    /**
     * Text View for direction
     */
    public static TextView textDirection;
    /**
     * Text View for robot status
     */
    public static TextView textRobotStatus;
    /**
     * Whether tilt is on
     */
    public boolean tiltCheck = false;
    /**
     * 20x20 Maze for the robot and obstacle
     */
    private static MazeGrid mazeGrid;
    /**
     * Fragment for bluetooth message
     */
    //BluetoothFragment fragment;
    /**
     * Sensor Manager for tilting
     */
    private SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        listen.setValue("Default");
//        tilt = new Tilt(this);

        init();
        // Remove shadow of action bar
        getSupportActionBar().setElevation(0);
        // Set layout to shift up when soft keyboard is open
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothFragment();
            transaction.replace(R.id.textBox, fragment);
            transaction.commit();

        }
        setupBtn();

    }

    /**
     * Initialize variables
     */
    public void init(){
        //drawing of map grid
        mazeGrid = findViewById(R.id.maze);

        //Update Robot Pos
        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);

        //Update Robot Direction
        textDirection = findViewById(R.id.textDirection);

        //Update Robot Status
        textRobotStatus = findViewById(R.id.textRobotStatus);
    }

    /**
     * Setup button in activity
     */
    public void setupBtn(){
        //Start arena exploration
        findViewById(R.id.arenaObstacles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.setStatus("Start exploring the 20x20 arena.");
                textRobotStatus.setText(robot.getStatus());
                outgoingMessage("OB,END"); //send message to RPI to let them know obstacle placement is done
            }});

        //Start fastest robot
        findViewById(R.id.fastestRobot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.setStatus("Start fastest robot parking.");
                textRobotStatus.setText(robot.getStatus());
                outgoingMessage("STM,I"); //send message to RPI to let them know robot may start moving
            }});
        // Move forward
        findViewById(R.id.btnForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotForward();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,W";
                outgoingMessage(navigation);
                robot.setStatus("Move Forward");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Move forward",
                //   Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1) {
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                } else {
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }

            }
        });

        // Move backward
        findViewById(R.id.btnBackward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotBackward();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,S";
                outgoingMessage(navigation);
                robot.setStatus("Move Backward");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Move backward",
                //   Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1) {
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                } else {
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }

            }
        });

        // Turn left
        findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnLeft();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,D";
                outgoingMessage(navigation);
                robot.setStatus("Turn Left");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Turn Left",
                //       Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1){
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        //Turn right
        findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnRight();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,A";
                outgoingMessage(navigation);
                robot.setStatus("Turn Right");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Turn Right",
                //      Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1){
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        // Hard left
        findViewById(R.id.btnHardLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardLeft();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,Q";
                outgoingMessage(navigation);
                robot.setStatus("Hard Left");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Hard Left",
                //       Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1){
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        // Hard right
        findViewById(R.id.btnHardRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardRight();
                mazeGrid.invalidate();
                String navigation = null;
                navigation = "STM,E";
                outgoingMessage(navigation);
                robot.setStatus("Hard Right");
                textRobotStatus.setText(robot.getStatus());
                //Toast.makeText(MainActivity.this, "Hard Right",
                //      Toast.LENGTH_SHORT).show();
                if (robot.getX() != -1 && robot.getY() != -1){
                    textX.setText(String.valueOf(robot.getX()));
                    textY.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textX.setText("-");
                    textY.setText("-");
                    textDirection.setText("-");
                }
            }
        });
        findViewById(R.id.manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outgoingMessage("sendArena");
            }
        });

        Switch s = (Switch) findViewById(R.id.autoUpdateSwitch);
//        sensorManager = (SensorManager) getSystemService(AppCompatActivity.SENSOR_SERVICE);
//        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
//                    tiltCheck =true;
                    outgoingMessage("Auto Update Arena");
                    onResume();
//                    tilt.register();
                } else {
                    // The toggle is disabled
                    outgoingMessage("Manual Update Arena");
//                    tiltCheck =false;
                    onPause();
                }
            }
        });

//        tilt.setListener(new Tilt.Listener() {
//            @Override
//            public void onRotation(float rotateX, float rotateY, float rotateZ) {
//                if(rotateX <-1.0f){
//                    if(listen.getValue() !="Move" ){
//                        listen.setValue("Move");
//                    }
//                }
//                else if (rotateX >1.0f){
//                    if(listen.getValue() != "Default" ){
//                        listen.setValue("Default");
//                    }
//                }
//                else if(rotateZ <-1.0f){
//                    if(listen.getValue() !="Right" ){
//                        listen.setValue("Right");
//                    }
//                }
//                else if (rotateZ >1.0f){
//                    if(listen.getValue() !="Left" ){
//                        listen.setValue("Left");
//                    }
//                }
//            }
//        });



//        listen.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                if(s == "Move"){
//                    if (robot.getX() != -1 && robot.getY() != -1) {
//                        robot.moveRobotForward();
//                        mazeGrid.invalidate();
//                        textX.setText(String.valueOf(robot.getX()));
//                        textY.setText(String.valueOf(robot.getY()));
//                        textDirection.setText(String.valueOf(robot.getDirection()));
//                    }
//                    Log.d("MainActivity", "Move");
//                }else if (s=="Left"){
//                    if (robot.getX() != -1 && robot.getY() != -1) {
//                        robot.moveRobotTurnLeft();
//                        mazeGrid.invalidate();
//                        textX.setText(String.valueOf(robot.getX()));
//                        textY.setText(String.valueOf(robot.getY()));
//                        textDirection.setText(String.valueOf(robot.getDirection()));
//                    }
//                    Log.d("MainActivity", "Left");
//                }else{
//                    Log.d("MainActivity", "Change value: " + s);
//                }
//            }
//        });
    }
    /**
     * Send message to other device
     *
     * @param sendMsg Message need to be sent
     */
    public static void outgoingMessage(String sendMsg) {
        fragment.sendMsg(sendMsg);
    }

    /**
     * A view for selecting the obstacle facing direction
     * @param c activity context
     * @param view current view
     * @param obstacle obstacle to be changed for direction
     */
    public static void obstacleDirectionPopup(Context c, View view, Obstacle obstacle) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.obstacle_popup_direction, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        Button btnN = (Button) popupView.findViewById(R.id.obstacleN);
        Button btnS = (Button) popupView.findViewById(R.id.obstacleS);
        Button btnE = (Button) popupView.findViewById(R.id.obstacleE);
        Button btnW = (Button) popupView.findViewById(R.id.obstacleW);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.RIGHT, 55, 0);

        MazeGrid mazeGrid = view.findViewById(R.id.maze);

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeGrid.setObstacleDirection(obstacle, 'N');
                popupWindow.dismiss();
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeGrid.setObstacleDirection(obstacle, 'S');
                popupWindow.dismiss();
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeGrid.setObstacleDirection(obstacle, 'E');
                popupWindow.dismiss();
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeGrid.setObstacleDirection(obstacle, 'W');
                popupWindow.dismiss();
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    /**
     * Set the robot position
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param direction direction of robot facing
     * @return boolean
     */
    public static boolean setRobotPosition(int x, int y, char direction){
        if (1 <= x && x <= 18 && 1 <= y && y <= 18 && (direction == 'N' || direction == 'S' || direction == 'E' || direction == 'W')){
            robot.setCoordinates(x, y);
            robot.setDirection(direction);
            textX.setText(String.valueOf(robot.getX()));
            textY.setText(String.valueOf(robot.getY()));
            textDirection.setText(String.valueOf(robot.getDirection()));
            mazeGrid.invalidate();
            return true;
        }
        return false;
    }

    /**
     * Move robot forward
     */
    public static void setRobotPositionEXForward(){
        robot.moveRobotForward();
        mazeGrid.invalidate();
    }
    /**
     * Move robot backward
     */
    public static void setRobotPositionEXBackward(){
        robot.moveRobotBackward();
        mazeGrid.invalidate();
    }
    /**
     * Move robot turn to left
     */
    public static void setRobotPositionEXLeft(){
        robot.moveRobotTurnLeft();
        mazeGrid.invalidate();
    }
    /**
     * Move robot turn to right
     */
    public static void setRobotPositionEXRight(){
        robot.moveRobotTurnRight();
        mazeGrid.invalidate();
    }
    /**
     * Make robot turn hard left
     */
    public static void setRobotPositionEXHardLeft(){
        robot.moveRobotHardLeft();
        mazeGrid.invalidate();
    }
    /**
     * Make robot turn hard right
     */
    public static void setRobotPositionEXHardRight(){
        robot.moveRobotHardRight();
        mazeGrid.invalidate();
    }

    /**
     * Set current obstacle as explored
     *
     * @param obstacleNumber number of obstacle
     * @param targetID obstacle target id
     *
     * @return boolean
     */
    public static boolean exploreTarget(int obstacleNumber, int targetID){
        // if obstacle number exists in map
        if (1 <= obstacleNumber && obstacleNumber <= Maze.getInstance().getObstacles().size()){
            Obstacle obstacle = Maze.getInstance().getObstacles().get(obstacleNumber - 1);
            obstacle.explore(targetID);
            mazeGrid.invalidate();
            return true;
        }
        return false;
    }

    /**
     * Update robot status and on view
     * @param status status of the robot
     */
    public static void updateRobotStatus(String status){
        robot.setStatus(status);
        textRobotStatus.setText(robot.getStatus());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //tilt.unregister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}