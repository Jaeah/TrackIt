package edu.cmu.cs.eyetrack.gui.shapes;

import java.awt.Color;
import java.awt.Polygon;

public class OctagonStimulus extends Stimulus {
	
	private Polygon polygon;
	
	private double hScale = 1.0/3.0;
	private double vScale = 1.0/3.0;
	
	public OctagonStimulus(String name, Color color, int width, int height) {

		super(name, color, width, height);
		
		polygon = new Polygon();
		polygon.addPoint((int) (width * hScale),0);
		polygon.addPoint((int) (width * (1.0 - hScale)),0);
		polygon.addPoint(width, (int) (height * vScale));
		polygon.addPoint(width, (int) (height * (1.0 - vScale)));
		polygon.addPoint((int) (width * (1.0 - hScale)),height);
		polygon.addPoint((int) (width * hScale),height);
		polygon.addPoint(0, (int) (height * (1.0 - vScale)));
		polygon.addPoint(0, (int) (height * vScale));
		
		shape = polygon;
	}

	@Override
	public Stimulus factoryClone(Color color) {
		if(color==null) {
			return new OctagonStimulus(name, this.color, width, height);
		} else {
			return new OctagonStimulus(name, color, width, height);
		}
	}
	
	@Override
	public void move(int newX, int newY) {
		//polygon.translate(-oldX + newX - (int) (0.5 * width), -oldY + newY - (int) (0.5 * height));
		polygon.translate(-((int) polygon.getBounds().getX()) + newX - (int) (0.5 * width), 
				-((int) polygon.getBounds().getY()) + newY - (int) (0.5 * height));
	}
}
