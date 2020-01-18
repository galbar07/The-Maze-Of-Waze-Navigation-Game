package Tests;

import gameClient.Robot;
import dataStructure.NodeData;
import dataStructure.node_data;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import utils.Point3D;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class RobotTest {

   private Robot rob;
   
   @BeforeEach
   public void bulidRobot() {
	   this.rob.list_to_go_through=new ArrayList<>();
	   this.rob=new Robot(this.rob.list_to_go_through);
	   
   }
   
   @Test
   public void testGetSize() {
	  this.rob.list_to_go_through.add(new NodeData(new Point3D(1,1,1)));
	   if (this.rob.list_to_go_through.size()!=1)
		   fail("getSize function isn't working");
   }
   
   @Test
   public void testGetNodeList() {
	  this.rob.list_to_go_through.add(new NodeData(new Point3D(1,1,1)));
	   if (!this.rob.getNodeList().contains(new NodeData(new Point3D(1,1,1))))
		   fail("getNodeList function isn't working");
   }
   
   @Test
   public void testSetNodeList() {
	  this.rob.SetNodeList(null);
	   if (this.rob.list_to_go_through!=null)
		   fail("setNodeList function isn't working");
   }

   
   
}