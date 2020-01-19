  
package Tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataStructure.DGraph;
import dataStructure.NodeData;
import gameClient.KML_Logger;
import utils.Point3D;

class testKML_Logger {
	static DGraph dg =new DGraph();
	static NodeData dn=new NodeData(new Point3D(2,4));
	static KML_Logger  kml = new KML_Logger(1,dg);
	
	@BeforeEach
	 void param() {
		dg.addNode(dn);
		
	}
	@Test
	void testBaseKML() {
		kml.basicKML(1);
		
	}
	@Test
	void testKMLtoFile() {
		kml.KMLtoFile();
	}

}
