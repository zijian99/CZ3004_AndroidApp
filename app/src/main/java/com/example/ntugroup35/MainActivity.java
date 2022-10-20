package com.example.ntugroup35;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import com.example.ntugroup35.Bluetooth.BluetoothFragment;
import com.example.ntugroup35.MazeObject.Maze;
import com.example.ntugroup35.MazeObject.Obstacle;
import com.example.ntugroup35.MazeObject.Robot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /**
     * Tag for log in Main Activity
     */
    public static final String TAG = "MainActivity";
    /**
     * Robot instance
     */
    static Robot robot = new Robot();
    /**
     * Bluetooth fragment
     */
    @SuppressLint("StaticFieldLeak")
    private static BluetoothFragment fragment;
    /**
     * Text View for x coordinate
     */
    @SuppressLint("StaticFieldLeak")
    public static TextView textXCoord;
    /**
     * Text View for y coordinate
     */
    @SuppressLint("StaticFieldLeak")
    public static TextView textYCoord;
    /**
     * Text View for robot direction
     */
    @SuppressLint("StaticFieldLeak")
    public static TextView textDirection;
    /**
     * Text View for robot status
     */
    @SuppressLint("StaticFieldLeak")
    public static TextView textRobotStatus;
    /**
     * Text view for timer
     */
    @SuppressLint("StaticFieldLeak")
    public static TextView textTimer;
    /**
     * Timer
     */
    public static Timer timer;
    /**
     * Timer task
     */
    public static TimerTask timerTask;
    /**
     * Time
     */
    public static Double time=0.0;
    /**
     * Boolean of timer has started
     */
    public static Boolean timeStarted=false;
    /**
     * 20x20 Maze for the robot and obstacle
     */
    private static MazeMapView mazeMapView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        // Remove shadow of action bar
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
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
        mazeMapView = findViewById(R.id.maze);

        //Update Robot Pos
        textXCoord = findViewById(R.id.textX);
        textYCoord = findViewById(R.id.textY);

        //Update Robot Direction
        textDirection = findViewById(R.id.textDirection);

        //Update Robot Status
        textRobotStatus = findViewById(R.id.textRobotStatus);
        textTimer = findViewById(R.id.textTimer);
        timer = new Timer();
    }

    /**
     * Setup button in activity
     */
    public void setupBtn(){
        //Start arena exploration
        findViewById(R.id.arenaObstacles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time!=0.0)
                {
                    Toast.makeText(MainActivity.this, "Please reset timer to start the task", Toast.LENGTH_SHORT).show();
                }
                else {
                    robot.setStatus("Sending List of Obstacle Coordinates");
                    textRobotStatus.setText(robot.getStatus());
                    //send message to RPI to let them know obstacle placement is done
                    btSendMessage("taskOne"+getObstacleCoords());
                    startTimer();
                }

            }});

        //Start fastest robot
        findViewById(R.id.fastestRobot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time!=0.0)
                {
                    Toast.makeText(MainActivity.this, "Please reset timer to start the task", Toast.LENGTH_SHORT).show();
                }
                else {
                    robot.setStatus("Start fastest robot parking.");
                    textRobotStatus.setText(robot.getStatus());
                    //send message to RPI to let them know robot may start moving
                    btSendMessage("taskTwo");
                    resetTimer();
                    startTimer();
                }
            }});

        // Move forward
        findViewById(R.id.btnForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotForward();
                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,W";
                btSendMessage(navigation);
                robot.setStatus("Move Forward");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1) {
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                } else {
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }

            }
        });

        // Move backward
        findViewById(R.id.btnBackward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotBackward();
                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,S";
                btSendMessage(navigation);
                robot.setStatus("Move Backward");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1) {
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                } else {
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }

            }
        });

         // Turn left
        findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnLeft();
                mazeMapView.invalidate();
                String navigation = null;
                navigation = "STM,A";
                btSendMessage(navigation);
                robot.setStatus("Turn Left");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getMiddleX()));
                    textYCoord.setText(String.valueOf(robot.getMiddleY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        //Turn right
        findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotTurnRight();
                mazeMapView.invalidate();
                String navigation = null;
                navigation = "STM,D";
                btSendMessage(navigation);
                robot.setStatus("Turn Right");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getMiddleX()));
                    textYCoord.setText(String.valueOf(robot.getMiddleY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        // Hard left
        findViewById(R.id.btnHardLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardLeft();
                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,Q";
                btSendMessage(navigation);
                robot.setStatus("Hard Left");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
        });

        // Hard right
        findViewById(R.id.btnHardRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardRight();
                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,E";
                btSendMessage(navigation);
                robot.setStatus("Hard Right");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
        });
        // Hard right
        findViewById(R.id.btnHardRightReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardRightReverse();

                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,Z";
                btSendMessage(navigation);
                robot.setStatus("Hard Right Reverse");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
        });
        // Hard right
        findViewById(R.id.btnHardLeftReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.moveRobotHardLeftReverse();
                robot.moveRobotHardRight();
                mazeMapView.invalidate();
                String navigation;
                navigation = "STM,C";
                btSendMessage(navigation);
                robot.setStatus("Hard Left Reverse");
                textRobotStatus.setText(robot.getStatus());
                if (robot.getX() != -1 && robot.getY() != -1){
                    textXCoord.setText(String.valueOf(robot.getX()));
                    textYCoord.setText(String.valueOf(robot.getY()));
                    textDirection.setText(String.valueOf(robot.getDirection()));
                }else{
                    textXCoord.setText("-");
                    textYCoord.setText("-");
                    textDirection.setText("-");
                }
            }
            });

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch s = findViewById(R.id.autoUpdateSwitch);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    startTimer();
                    onResume();
//
                } else {
                    // The toggle is disabled
                    stopTimer();
                    onPause();
                }
            }
        });

    }

    /**
     * Setup a preset map in maze grid
     */
    public static void presetMap(){
        mazeMapView.presetMap();
    }
    /**
     * Setup a preset map in maze grid
     */
    public static void smithaMap(){
        mazeMapView.smithaMap();
    }
    /**
     * Clear all the obstacle and robot in map in maze grid
     */
    public static void clearMap(){
        mazeMapView.clearMap();
    }
    /**
     * Setup a preset map in maze grid
     */
    public static void eMap(){
        mazeMapView.eMap();
    }

    /**
     * Start the timer for the task
     */
    private void startTimer() {
        if(timeStarted) {
            Toast.makeText(this, "Task has started", Toast.LENGTH_SHORT).show();
            return;
        }
        timeStarted=true;
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;

                        textTimer.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }
    /**
     * Get the time in string format
     *
     * @return string of time
     */
    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }
    /**
     * Get the time in string format
     *
     * @param seconds seconds
     * @param hours  hours
     * @param minutes minutes
     *
     * @return string of time
     */
    @SuppressLint("DefaultLocale")
    private static String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

    /**
     * Return whether timer has started
     *
     * @return boolean
     */
    public static boolean getTimeStarted(){
        return timeStarted;
    }

    /**
     * Stop the timer
     */
    public static void stopTimer(){
        if(timerTask==null)
            return;
        timerTask.cancel();
        timeStarted=false;
    }

    /**
     * Reset the timer to 0
     */
    @SuppressLint("SetTextI18n")
    public static void resetTimer()
    {
        if(timerTask==null)
            return;
        timerTask.cancel();
        textTimer.setText("00:00:00");
        time=0.0;
        timeStarted=false;

    }
    /**
     * Send message to other device
     *
     * @param sendMsg Message need to be sent
     */
    public static void btSendMessage(String sendMsg) {
        fragment.sendMessage(sendMsg);
    }

    /**
     * A view for selecting the obstacle facing direction
     * @param c activity context
     * @param view current view
     * @param obstacle obstacle to be changed for direction
     */
    @SuppressLint("RtlHardcoded")
    public static void obsDirectionPopupWindow(Context c, View view, Obstacle obstacle) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.obstacle_direction_popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        Button btnN = popupView.findViewById(R.id.obstacleN);
        Button btnS = popupView.findViewById(R.id.obstacleS);
        Button btnE = popupView.findViewById(R.id.obstacleE);
        Button btnW = popupView.findViewById(R.id.obstacleW);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.RIGHT, 55, 0);

        MazeMapView mazeMapView = view.findViewById(R.id.maze);

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeMapView.setObstacleDirection(obstacle, 'N');
                popupWindow.dismiss();
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeMapView.setObstacleDirection(obstacle, 'S');
                popupWindow.dismiss();
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeMapView.setObstacleDirection(obstacle, 'E');
                popupWindow.dismiss();
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mazeMapView.setObstacleDirection(obstacle, 'W');
                popupWindow.dismiss();
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    /**
     * Explore a certain obstacle by its coordinates
     *
     * @param x x coordinates
     * @param y y coordinates
     * @param targetID found id of the obstacles
     * @return boolean
     */
    public static boolean exploreTargetViaCoordinates(int x,int y, int targetID){
        // if obstacle number exists in map
        int obstacleNumber= Maze.getInstance().findObstacleNumber(x,y);
        if (1 <= obstacleNumber && obstacleNumber <= Maze.getInstance().getObstacles().size()){
            Obstacle obstacle = Maze.getInstance().getObstacles().get(obstacleNumber - 1);
            obstacle.explore(targetID);
            mazeMapView.invalidate();
            return true;
        }
        return false;
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
            textXCoord.setText(String.valueOf(robot.getX()));
            textYCoord.setText(String.valueOf(robot.getY()));
            textDirection.setText(String.valueOf(robot.getDirection()));
            mazeMapView.invalidate();
            return true;
        }
        return false;
    }

    /**
     * Move robot forward
     */
    public static void setRobotPositionEXForward(){
        robot.moveRobotForward();
        mazeMapView.invalidate();
    }
    /**
     * Move robot backward
     */
    public static void setRobotPositionEXBackward(){
        robot.moveRobotBackward();
        mazeMapView.invalidate();
    }
    /**
     * Move robot turn to left
     */
    public static void setRobotPositionEXLeft(){
        robot.moveRobotTurnLeft();
        mazeMapView.invalidate();
    }
    /**
     * Move robot turn to right
     */
    public static void setRobotPositionEXRight(){
        robot.moveRobotTurnRight();
        mazeMapView.invalidate();
    }
    /**
     * Make robot turn hard left
     */
    public static void setRobotPositionEXHardLeft(){
        robot.moveRobotHardLeft();
        mazeMapView.invalidate();
    }
    /**
     * Make robot turn hard right
     */
    public static void setRobotPositionEXHardRight(){
        robot.moveRobotHardRight();
        mazeMapView.invalidate();
    }
    /**
     * Make robot turn reverse hard right
     */
    public static void setRobotPositionEXHardRightReverse(){
        robot.moveRobotHardRightReverse();
        mazeMapView.invalidate();
    }
    /**
     * Make robot turn reverse hard left
     */
    public static void setRobotPositionEXHardLeftReverse(){
        robot.moveRobotHardLeftReverse();
        mazeMapView.invalidate();
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
            mazeMapView.invalidate();
            return true;
        }
        return false;
    }

    /**
     * Get the string of details of all obstacles available in map
     *
     * @return string of details of obstacles
     */
    public static String getObstacleCoords(){

        ArrayList<Obstacle> obstacleArrayListList = Maze.getInstance().getObstacles();
        StringBuilder obstacleString = new StringBuilder();
        Log.d(TAG, String.valueOf(obstacleArrayListList));
        int count=0;
        for (Obstacle obstacle : Maze.getInstance().getObstacles()) {
            if(count!=0) {
                obstacleString.append("/");
            }
            obstacleString.append(obstacle.getX()).append(",").append(obstacle.getY()).append(",").append(obstacle.getSide());
            count++;
        }
        return obstacleString.toString();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
