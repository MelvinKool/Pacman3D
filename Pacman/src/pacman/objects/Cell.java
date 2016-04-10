/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.objects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author melvin
 */
public class Cell extends Node {
    AssetManager assetManager;
    private int cellId;
    public Cell(AssetManager assetManager, int cellId){
        this.assetManager = assetManager;
        createCell();
    }
    
    private void createCell(){
        Sphere sphere = new Sphere(32,32, 0.1f);
        Geometry cellGeom = new Geometry("Cell", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        cellGeom.setMaterial(mat);
        this.attachChild(cellGeom);
    }
    
    public int getCellId(){
        return cellId;
    }
}
