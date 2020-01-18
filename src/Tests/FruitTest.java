package Tests;

import gameClient.Fruit_c;
import utils.Point3D;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import Server.game_service;
import dataStructure.EdgeData;

import static org.junit.Assert.*;

public class FruitTest {

	
	private game_service game;


    @Test
    public void TestGetTag() {
    	  Fruit_c f1 = new Fruit_c(10,1,12,13);
    	  Fruit_c f2 = new Fruit_c(5,-1,13,12);
    	System.out.println("hi");
        f1.setTag(1);
        f2.setTag(2);
       
        if (f1.getTag()!=1 || f2.getTag()!=2)
        	fail("getTag function isn't working");
    }


    @Test
    public void TestGetType() {
       if (this.f1.getType()!=1 || this.f2.getType()!=-1)
    	   System.out.println(this.f1.getType());
    	   fail("getType function isn't working");
    }

    @Test
    public void TestGetSrc() {
      if(this.f1.getSrc()!=12 || this.f2.getSrc()!=13)
   	   fail("getSrc function isn't working");

    }
    @Test
    public void TestGetDest() {
    	 if(this.f1.getDest()!=12 || this.f2.getDest()!=13)
    	   	   fail("getDest function isn't working");
    }
    @Test
    public void testGetValue() {
    	System.out.println(f1.getValue());
    	 if(this.f1.getValue()!=10 || this.f2.getValue()!=5)
    	   	   fail("getValue function isn't working");
    }
    
    @Test
    public void testAssos() {
    	Point3D pForApple=new Point3D(12,13,0);
    	Point3D pForBanana=new Point3D(13,12,0);
       EdgeData edgeForApple=new EdgeData(12, 13, 1);
       EdgeData edgeForBanana=new EdgeData(13, 12, 1);
       if (!this.f1.assos(pForApple, this.game, this.f1.getType()).equals(edgeForApple)|| (!this.f2.assos(pForBanana, this.game, this.f2.getType()).equals(edgeForBanana)))
			fail("getSrc function isn't working");
       
    }
}