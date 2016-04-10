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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import pacman.Main;

/**
 *
 * @author melvin
 */
public final class Pacman extends Creature {
    private final MotionPath motionPath;
    private MotionEvent motionEvent;
    private Geometry pacman;
    public int cellsEaten;
    
    public Pacman(AssetManager assetManager){
        super(assetManager);
        motionPath = new MotionPath();
        createCreature();
    }

    @Override
    protected void createCreature() {
//        Vector3f vec = pacman.getWorldTranslation();
//        pacman.lookAt(vec, vec);
//        pacman.rotate(vec.x, queueDistance, vec.z);
        Sphere sphere = new Sphere(32,32, 0.4f);
        pacman = new Geometry("Pacman", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        pacman.setMaterial(mat);
        this.attachChild(pacman);        
    }
    
    @Override
    public void onWayPointReach(MotionEvent me, int i) {
    }

    @Override
    public void moveToWaypoint(Vector3f location, float secTime) {
        //move to the location specified by key
        motionPath.clearWayPoints();
        motionPath.addWayPoint(new Vector3f((int)Math.round(getWorldTranslation().x),Main.BLOCKHEIGHT,(int)Math.round(getWorldTranslation().z)));
        motionPath.addWayPoint(location);
        motionPath.addListener(this);
        if(motionEvent != null && motionEvent.isEnabled())
            motionEvent.stop();
        motionEvent = new MotionEvent(this,motionPath, secTime);
        motionEvent.setDirectionType(MotionEvent.Direction.Path);
        motionEvent.play();
        Quaternion quat = motionEvent.getRotation();
    }
    
    public Vector3f getPosition() {
        return getWorldTranslation();
    }
}
