package edu.cmu.cs.eyetrack.state;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

import edu.cmu.cs.eyetrack.gui.shapes.BoxStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.CircleStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.CrescentStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.CrossStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.DiamondStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.HeartStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.OctagonStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.StarStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.StimulusFactory;
import edu.cmu.cs.eyetrack.gui.shapes.TriangleStimulus;
import edu.cmu.cs.eyetrack.gui.shapes.StimulusFactory.StimulusType;
import edu.cmu.cs.eyetrack.helper.Util;
import edu.cmu.cs.eyetrack.io.TestRecord;

public class GameState {

	private Settings settings;
	private History history;

	private FileWriter file;
	private LinkedList<String[]> outputBuffer;

	private Trial activeTrial;
	
	private ArrayList<ImageIcon> backgroundImages;

	// Maintain a single Random instance, always draw from this;
	// given a unique seed, should be able to reproduce experiment exactly
	private Random random;
	private RandomGen randomGen;
	
	private int currentTrialCount = 0;
	              
	public GameState() {
		outputBuffer = new LinkedList<String[]>();
	}

	public void registerStimuli(int blockWidth, int blockHeight) {
		
		// Kill everything that's been inserted before.
		StimulusFactory.getInstance().reset();
		
		// Color each stimulus a different, randomly generated (someday...) color
		int totalNumberOfStimuli = 9, colorIdx = 0;
		
		List<Color> stimColors;
		if(getRandomGen() == null) {
			stimColors = new ArrayList<Color>();
			for(int idx=0; idx<totalNumberOfStimuli; idx++) {
				stimColors.add(Color.BLACK);
			}
		} else {
			stimColors = getRandomGen().getRandomColors( totalNumberOfStimuli );
		}
		
		// Register some Stimuli as targets, some as distractors
		double stimScale = 0.5;
		double minBlockDim = Math.min(blockHeight, blockWidth) * stimScale;
		StimulusFactory.getInstance().registerStimulus(new BoxStimulus("Box", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new TriangleStimulus("Triangle", stimColors.get(colorIdx++), (int) minBlockDim), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new CrossStimulus("Cross", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new OctagonStimulus("Octagon", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new CircleStimulus("Circle", stimColors.get(colorIdx++),(int) minBlockDim), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new HeartStimulus("Heart", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new StarStimulus("Star", stimColors.get(colorIdx++), 5, minBlockDim / 4.0, minBlockDim / 2.0), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new DiamondStimulus("Diamond", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
		StimulusFactory.getInstance().registerStimulus(new CrescentStimulus("Crescent", stimColors.get(colorIdx++), (int) (blockWidth * stimScale), (int) (blockHeight * stimScale)), StimulusType.BOTH);
	}
	
	public void registerStimuli() {
		registerStimuli(1,1);
	}
	
	public boolean hasMoreToWrite() {
		return !outputBuffer.isEmpty();
	}

	public String[] nextDataLine() {
		return outputBuffer.removeFirst();
	}

	public void addTrial(Trial trial) {
		// If we've never added a Trial before, we need to tack on CSV headers
		if(history == null) {
			history = new History();
		}

		// Add the trial to our history of all trials, and push its data to the write buffer
		history.addTrial(trial);
		outputBuffer.add(trial.getCSVHeader());
		
		// Assume that this is our current trial now
		activeTrial = trial;
	}

	public void endActiveTrial() {
		outputBuffer.addAll(activeTrial.getCSVData());
		
		try {
			TestRecord.getInstance().updateLog(this);
		} catch(IOException ex) {
			Util.dPrintln("ERROR: Cannot write data to file!");
			ex.printStackTrace();
		}
		
	}
	
	public RandomGen getRandomGen() {
		return randomGen;
	}
	
	public void setBackgroundImages(ArrayList<ImageIcon> backgroundImages) {
		this.backgroundImages = backgroundImages;
	}

	public ArrayList<ImageIcon> getBackgroundImages() {
		return backgroundImages;
	}

	public Settings getSettings() {
		return settings;
	}

	public Random getRandom() {
		return random;
	}

	public Trial getActiveTrial() {
		return activeTrial;
	}

	public void setActiveTrial(Trial activeTrial) {
		this.activeTrial = activeTrial;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;

		// Set up the global random counter
		random = new Random(settings.getExperiment().getSeed());
		StimulusFactory.getInstance().setRandom(random);
		randomGen = new RandomGen(random, settings.getExperiment());
		
		// We only add Settings once, so add the header and the data to the write buffer
		outputBuffer.add(settings.getCSVHeader());

		List<String[]> settingsData = settings.getCSVData();
		for(String[] row : settingsData) {
			outputBuffer.add(row);
		}
	}

	public void setIO(FileWriter file) {
		this.file = file;
	}

	public FileWriter getIO() {
		return file;
	}

	public int getCurrentTrialCount() {
		return currentTrialCount;
	}

	public void incCurrentTrialCount() {
		this.currentTrialCount++;
	}

}
