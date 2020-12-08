/*A character recognizer that uses neural nets

TODO: YOUR NAME HERE

Michael Black, 11/2020

TODO:  
        YOUR CODE WILL GO IN FUNCTIONS test() AND train()
        HERE STATE WHAT STEPS YOU ACCOMPLISHED

usage:
ocr sample X
        pops up a window, user draws an example of an X, user doubleclicks and the X is saved for later
ocr train
        builds a neural net for each letter type, trains each of them on the samples until they predict perfectly
ocr test
        pops up a window, user draws a letter and doubleclicks, the program tries to guess which letter was drawn
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class OCR extends JComponent implements MouseListener, MouseMotionListener {
	// global constants

	// squares wide
	public static final int GRIDWIDTH = 10;
	// squared tall
	public static final int GRIDHEIGHT = 20;
	// window dimensions
	public static final int SCREENWIDTH = 400, SCREENHEIGHT = 400;
	// flags
	public static final int SAMPLE = 1, TRAIN = 2, TEST = 3;

	// array of grid squares: true=filled, false=empty
	public boolean[][] square;

	// operation being performed: SAMPLE, TRAIN, TEST
	public int operation;
	// for sample only, letter being drawn
	public char letter;

	private JFrame window;

	// read the contents of the grid and save them to the end of ocrdata.txt
	public void saveSample() {
		try {
			PrintWriter datafile = new PrintWriter(new FileOutputStream(new File("ocrdata.txt"), true));
			datafile.print(letter + " ");
			int[] data = getSquareData();
			for (int x = 0; x < data.length; x++)
				datafile.print(data[x]);
			datafile.println();
			datafile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called immediately on "ocr train"
	// reads the images in ocrdata.txt, builds a set of neural nets, trains them,
	// and saves the weights to perceptron.txt
	public void train() {
		try {
			Scanner ocrdata = new Scanner(new FileReader("ocrdata.txt"));
			int linecount = 0; // keep track of how many samples are in the file
			int sample_types = 0; // keep track of how many different types of letters are in the file
			// go through the file and just count the samples
			while (ocrdata.hasNextLine()) {
				linecount++;
				ocrdata.nextLine();
			}
			ocrdata.close();

			// make an array to hold the samples
			int[][] sample_input = new int[linecount][GRIDWIDTH * GRIDHEIGHT];
			// make another array to hold the output letter for each sample
			char[] sample_output = new char[linecount];
			// reopen the file
			ocrdata = new Scanner(new FileReader("ocrdata.txt"));
			// for each sample,
			for (int i = 0; i < linecount; i++) {
				String line = ocrdata.nextLine();
				// the first character is the output letter
				sample_output[i] = line.charAt(0);
				// then a space, then a 1 or 0 for each square
				for (int j = 0; j < GRIDWIDTH * GRIDHEIGHT; j++)
					sample_input[i][j] = (line.charAt(j + 2) == '1' ? 1 : 0);

			}
			ocrdata.close();

			// TODO: MAKE NEURAL NET (PERCEPTRON) OBJECTS AND TRAIN THEM HERE, THEN SAVE THE
			// WEIGHTS TO perceptron.txt

			// Make a Perceptron object with enough inputs for every pixel on the screen and
			// a single output.
			Perceptron neuron = new Perceptron(GRIDWIDTH * GRIDHEIGHT);

			// Now go through your sample array
			for (int i = 0; i < linecount; i++) {
				// Pick a letter
				int randomLetterIndex = new Random().nextInt(sample_output.length);
				char randomLetter = sample_output[randomLetterIndex];

				// If the sample matches the letter you pick, train the perceptron with a 1. If
				// it doesnt, train with a 0.
				if (sample_output[i] == randomLetter) {
					// train the perceptron with a 1
					boolean done = false;
					// Put the training operation into a while loop, and train until the perceptron
					// is answering perfectly for all inputs.
					while (!done) {
						done = neuron.train(sample_input[i], 1);
					}
				} else {
					// train with a 0
					boolean done = false;
					// Put the training operation into a while loop, and train until the perceptron
					// is answering perfectly for all inputs.
					while (!done) {
						done = neuron.train(sample_input[i], 0);
					}
				}
			}

			// make an output file perceptron.txt, and write all the hidden weights and
			// output weights
			try {
				DataOutputStream perceptronData = new DataOutputStream(new FileOutputStream("perceptron.txt"));
				for (int i = 0; i < GRIDWIDTH * GRIDHEIGHT; i++) {
					for (int j = 0; j <= GRIDWIDTH * GRIDHEIGHT; j++) {
						perceptronData.writeDouble(neuron.hiddenweight[i][j]);
					}
				}
				perceptronData.writeChars("\n");
				for (int i = 0; i < GRIDWIDTH * GRIDHEIGHT; i++) {
					perceptronData.writeDouble(neuron.outputweight[i]);
				}
				perceptronData.close();
				System.out.println("Wrote perceptron.txt file.");
			} catch (Exception e) {
				System.out.println("An error occurred writing to perceptron.txt file: ");
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void testXOR() {
		// Test code for XOR

		System.out.println("Testing for XOR...");

		int[] inputs = new int[2];
		int[] outputs = new int[2];

		Perceptron firstNeuron = new Perceptron(2);
		Perceptron secondNeuron = new Perceptron(2);

		long GIVE_UP = 10000;
		int correctCount = 0;

		for (int i = 0; i < GIVE_UP; i++) {

			correctCount = 0;

			inputs[0] = 0;
			inputs[1] = 0;
			outputs[0] = 0;
			outputs[1] = 0;
			boolean isFirstNeuronRight = firstNeuron.train(inputs, outputs[1]);
			boolean isSecondNeuronRight = secondNeuron.train(inputs, outputs[0]);
			if (isFirstNeuronRight)
				correctCount++;
			if (isSecondNeuronRight)
				correctCount++;

			inputs[0] = 1;
			inputs[1] = 0;
			outputs[0] = 1;
			outputs[1] = 0;
			isFirstNeuronRight = firstNeuron.train(inputs, outputs[1]);
			isSecondNeuronRight = secondNeuron.train(inputs, outputs[0]);
			if (isFirstNeuronRight)
				correctCount++;
			if (isSecondNeuronRight)
				correctCount++;

			inputs[0] = 0;
			inputs[1] = 1;
			outputs[0] = 1;
			outputs[1] = 0;
			isFirstNeuronRight = firstNeuron.train(inputs, outputs[1]);
			isSecondNeuronRight = secondNeuron.train(inputs, outputs[0]);
			if (isFirstNeuronRight)
				correctCount++;
			if (isSecondNeuronRight)
				correctCount++;

			inputs[0] = 1;
			inputs[1] = 1;
			outputs[0] = 0;
			outputs[1] = 1;
			isFirstNeuronRight = firstNeuron.train(inputs, outputs[1]);
			isSecondNeuronRight = secondNeuron.train(inputs, outputs[0]);
			if (isFirstNeuronRight)
				correctCount++;
			if (isSecondNeuronRight)
				correctCount++;

			if (correctCount == 8)
				break;
		}

		if (correctCount == 8) {
			System.out.println("Learned it!");
		} else {
			System.out.println("Never learned it!");
		}

		// Test it: write this four times
		inputs[0] = 0;
		inputs[1] = 0;
		System.out.println("The prediction for " + inputs[1] + " " + inputs[0] + ": "
				+ firstNeuron.getPrediction(inputs) + " " + secondNeuron.getPrediction(inputs));

		inputs[0] = 0;
		inputs[1] = 1;
		System.out.println("The prediction for " + inputs[1] + " " + inputs[0] + ": "
				+ firstNeuron.getPrediction(inputs) + " " + secondNeuron.getPrediction(inputs));

		inputs[0] = 1;
		inputs[1] = 0;
		System.out.println("The prediction for " + inputs[1] + " " + inputs[0] + ": "
				+ firstNeuron.getPrediction(inputs) + " " + secondNeuron.getPrediction(inputs));

		inputs[0] = 1;
		inputs[1] = 1;
		System.out.println("The prediction for " + inputs[1] + " " + inputs[0] + ": "
				+ firstNeuron.getPrediction(inputs) + " " + secondNeuron.getPrediction(inputs));
	}

	// called on "ocr test", after the user draws and right-clicks the mouse
	public void test() {
		// TODO: MAKE A NEURAL NET OBJECT, READ THE WEIGHTS FROM A FILE perceptron.txt,
		// USE THE NEURAL NET TO IDENTIFY THE LETTER

		// make a Perceptron object
		Perceptron neuron = new Perceptron(GRIDWIDTH * GRIDHEIGHT);

		// read the contents of perceptron.txt, and assign the Perceptron object's
		// weights accordingly
		// TODO

		// Use function getSquareData to read what the user drew on the screen as an
		// integer array
		int[] userInput = this.getSquareData();

		// Feed userInput to the perceptron, and print out whether or not it matched the
		// letter you chose.
		// TODO
	}

	// returns contents of all squares as array of 1 (filled) 0 (unfilled)
	public int[] getSquareData() {
		int[] data = new int[GRIDWIDTH * GRIDHEIGHT];
		for (int x = 0; x < GRIDWIDTH; x++)
			for (int y = 0; y < GRIDHEIGHT; y++)
				data[GRIDWIDTH * y + x] = square[x][y] ? 1 : 0;
		return data;
	}

	public OCR(int operation) {
		this.operation = operation;
		if (operation == SAMPLE || operation == TEST)
			constructWindow();
		else if (operation == TRAIN)
			// train();
			testXOR();
	}

	public OCR(int operation, char letter) {
		this.operation = operation;
		this.letter = letter;
		if (operation == SAMPLE || operation == TEST)
			constructWindow();
	}

	public void drawingCompleted() {
		if (window != null)
			window.setVisible(false);
		if (operation == SAMPLE)
			saveSample();
		else if (operation == TEST)
			test();
		System.exit(0);
	}

	public void constructWindow() {
		square = new boolean[GRIDWIDTH][GRIDHEIGHT];

		window = new JFrame("OCR");
		window.setSize(SCREENWIDTH + 10, SCREENHEIGHT + 30);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		if (operation == SAMPLE)
			window.setTitle("OCR - draw letter " + letter + " and right-click when done");
		else if (operation == TEST)
			window.setTitle("OCR - draw a letter and right-click when done");
		window.setVisible(true);
	}

	public void paintComponent(Graphics g) {
		int squarewidth = SCREENWIDTH / GRIDWIDTH;
		int squareheight = SCREENHEIGHT / GRIDHEIGHT;
		for (int x = 0; x < GRIDWIDTH; x++) {
			for (int y = 0; y < GRIDHEIGHT; y++) {
				if (square[x][y])
					g.setColor(new Color(100, 100, 0));
				else
					g.setColor(new Color(255, 255, 255));
				g.fillRect(x * squarewidth, y * squareheight, squarewidth, squareheight);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			int squarewidth = SCREENWIDTH / GRIDWIDTH;
			int squareheight = SCREENHEIGHT / GRIDHEIGHT;
			square[e.getX() / squarewidth][e.getY()
					/ squareheight] = !square[e.getX() / squarewidth][e.getY() / squareheight];
			lastx = e.getX() / squarewidth;
			lasty = e.getY() / squareheight;
			repaint();
		} else
			drawingCompleted();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private int lastx = -1, lasty = -1;

	public void mouseDragged(MouseEvent e) {
		int squarewidth = SCREENWIDTH / GRIDWIDTH;
		int squareheight = SCREENHEIGHT / GRIDHEIGHT;
		if (lastx == e.getX() / squarewidth && lasty == e.getY() / squareheight)
			return;
		// square[e.getX()/squarewidth][e.getY()/squareheight]=!square[e.getX()/squarewidth][e.getY()/squareheight];
		square[e.getX() / squarewidth][e.getY() / squareheight] = true;
		lastx = e.getX() / squarewidth;
		lasty = e.getY() / squareheight;
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println(" java OCR sample A");
		System.out.println(" java OCR train");
		System.out.println(" java OCR test");
	}

	public static void main(String[] args) {
		if (args.length < 1)
			printUsage();
		else if (args[0].equals("sample")) {
			new OCR(SAMPLE, args[1].charAt(0));
		} else if (args[0].equals("train"))
			new OCR(TRAIN);
		else if (args[0].equals("test"))
			new OCR(TEST);
		else
			printUsage();
	}
}
