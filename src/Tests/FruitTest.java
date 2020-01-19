package Tests;

import gameClient.Fruit_c;
import utils.Point3D;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import Server.game_service;
import dataStructure.DGraph;
import dataStructure.EdgeData;
import dataStructure.NodeData;

import static org.junit.Assert.*;

public class FruitTest {

/**
 * testing the getTag function of a fruit 
 */
    @Test
    public void TestGetTag() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
        f1.setTag(1);
        f2.setTag(2);
       
        if (f1.getTag()!=1 || f2.getTag()!=2)
        	fail("getTag function isn't working");
    }

    /**
     * testing the getType function of a fruit 
     */
    @Test
    public void TestGetType() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
       if (f1.getType()!=1 || f2.getType()!=-1)
    	   fail("getType function isn't working");
    }
    /**
     * testing the getSrc function of a fruit 
     */
    @Test
    public void TestGetSrc() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
      if(f1.getSrc()!=12 || f2.getSrc()!=13)
   	   fail("getSrc function isn't working");

    }
    /**
     * testing the getDest function of a fruit 
     */
    @Test
    public void TestGetDest() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
    	 if(f1.getDest()!=13 || f2.getDest()!=12)
    	   	   fail("getDest function isn't working");
    }
    
    /**
     * testing the getValue function of a fruit 
     */
    @Test
    public void testGetValue() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
    	 if(f1.getValue()!=10 || f2.getValue()!=5)
    	   	   fail("getValue function isn't working");
    }
    
}