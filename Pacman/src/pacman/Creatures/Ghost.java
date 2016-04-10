/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.Creatures;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.awt.Point;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import pacman.Main;

/**
 *
 * @author melvin
 */
public final class Ghost extends Creature {
    Pacman pacman;
    public boolean readyToMove = true;
    MotionPath path;
    Stack<Vector3f> wayPointQueue = new Stack();
    int id;
    Geometry ghost;
    float tileDuration = 0.35f;
    List<Point> cellLocations;
    
    public Ghost(AssetManager assetManager, Pacman pacman, List<Point> cellLocations,int id){
        super(assetManager);
        this.pacman = pacman;
        this.id = id;
        this.cellLocations = cellLocations;
        createCreature();
    }

    @Override
    protected void createCreature() {
        Sphere sphere = new Sphere(32,32, 0.4f);
        ghost = new Geometry("Ghost " + id, sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        switch(id) {
            case 0:
                mat.setColor("Color", ColorRGBA.Orange);
                break;
            case 1:
                mat.setColor("Color", ColorRGBA.Cyan);
                break;
            case 2:
                mat.setColor("Color", ColorRGBA.Pink);
                break;
            case 3:
                mat.setColor("Color", ColorRGBA.Red);
                break;
        }
        ghost.setMaterial(mat);
        this.attachChild(ghost);
    }
    
    @Override
    public void onWayPointReach(MotionEvent me, int wayPointIndex) {
        readyToMove = true;
        if(path.getNbWayPoints() == wayPointIndex + 1){
            System.out.println("clearing...");
            path.clearWayPoints();
            moveToPacman();
        }
    }

    @Override
    public void moveToWaypoint(Vector3f location, float secTime) {
    }
    
    public void moveToPacman() {
        if(readyToMove) {
            readyToMove = false;
            System.out.println("searching new path...");
            int ghostx = (int)Math.round(getPosition().x),
                ghostz = (int)Math.round(getPosition().z);
            int pacmanx = (int)pacman.getPosition().x,
                pacmanz = (int)pacman.getPosition().z;
            List<Point> waypoints;
            if(Math.random() < 0.75)
                waypoints = astar.computeShortestPath(new Point(ghostx, ghostz), new Point(pacmanx, pacmanz));
            else{
                Random r = new Random();
                int randomLocation = r.nextInt(cellLocations.size());
                waypoints = astar.computeShortestPath(new Point(ghostx, ghostz), cellLocations.get(randomLocation));
            }
            if(waypoints != null) {
                for(int i = 0; i < waypoints.size();i++){
                    Vector3f wp = new Vector3f(waypoints.get(i).x, 0.5f, waypoints.get(i).y);
                    wayPointQueue.add(wp);
                }
                if(wayPointQueue.size() > 0) {
                    path = new MotionPath();
                    path.addListener(this);
                    int wayPointSize = wayPointQueue.size();
                    for(int i = 0; i < wayPointSize; i++) {
                        path.addWayPoint(wayPointQueue.get(0));
                        wayPointQueue.remove(0);
                    }
                    
                    //speed calculation
                    float route = 0;
                    for(int i = 0; i < path.getSpline().getControlPoints().size(); i++) {
                        if(i+1 < path.getSpline().getControlPoints().size()) {
                            float differenceX = Math.abs(path.getSpline().getControlPoints().get(i).x - path.getSpline().getControlPoints().get(i+1).x);
                            float differenceZ = Math.abs(path.getSpline().getControlPoints().get(i).z - path.getSpline().getControlPoints().get(i+1).z);
                            route = route + differenceX + differenceZ;
                        }
                    }
                    final float initDur = this.tileDuration * route;
                    
                    MotionEvent motionEvent = new MotionEvent(this, path, initDur);
                    motionEvent.play();
                }
                readyToMove = true;
            }
            else {
                System.out.println("ghost "+ Main.BOARD[ghostz][ghostx] + " pacman " + Main.BOARD[pacmanz][pacmanx] );
                System.out.println("Ghost " + id + " geeft een error bij het maken van een pad.");
            }
        }
    }
    
    public Vector3f getPosition() {
        return getWorldTranslation();
    }
}
