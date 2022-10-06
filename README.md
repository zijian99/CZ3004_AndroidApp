# CZ3004_AndroidApp
### <b>Year 2022 NTU CZ3004 MultiDisciplinary Design Project : Android Module Controller</b>
<br>
<br>
<br>

| *Objective*                                                       | 
| ------------------------------------------------------------------|
| Develop mobile apps on an Android-powered device                  |
| Design and develop graphical user interface-based apps            |
| Implement wireless connectivity between Bluetoothenabled devices  |
| Design and implement graphical displays in your Android app       |

<br>
<br>

| *Purpose*                                                                                        | 
| -------------------------------------------------------------------------------------------------|
| Your Android tablet will be the wireless remote controller device for your team’s robotic system |
| It will issue commands to robot to begin various manoeuvres in arena during the competition      |
| It will allow the team to visualize the current status of arena and robot                        |

<br>
<br>
### <b>Task 1 for MDP </b>
<br>
Automatic movement and image recognition task <br>
Android TODO: Put the obstacle and robot to correct position and press the start button<br><br>
### <b>Task 2 for MDP </b>
<br>
Fastest car task using visual recognition<br>
Android TODO: Press start button<br><br>
<br>
<br>

### <b>App Functions </b>
<br>

|   |                                                                                    |
|---|-------------------------------------------------------------------------------------|
| 1.| 20x20 Maze                                                                          |
| 2.| Bluetooth connection to Raspberry Pi                                                |
| 3.| Sending of message to Raspberry Pi                                                  |
| 4.| Manual / Auto Reconnection of Bluetooth ( 3 times auto-reconnection for 4 seconds ) |
| 5.| Timer for calculating task execution time                                           |
| 6.| Sample Arena Auto Setup Button / Clear Map Button                                   |
| 7.| Auto Update of Robot position and obstacle image id when received message           |  
| 8.| Manual Setup Robot and Obstacle Postion and their facing ( Press and hold the obstacle on the map for choosing of direction N/S/E/W ) |

<br>
<br>
Things to improve:

|   |                                                                                     |
|---|-------------------------------------------------------------------------------------|
| 1.| Bluetooth should be run using Service or IntentService(Background Task)             |
| 2.| Generalize the setting up of Sample Arena instead of hardcoding                     |
| 3.| Make the code more loose coupling                                                   |

Developed by:<br>
[Zi Jian](https://github.com/zijian99)<br>
[Chien Hui](https://github.com/Limchienhui)<br>


