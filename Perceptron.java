//multilevel neural net framework
//you need to fill in the getRawPrediction and train functions

public class Perceptron {
	// private static final double ALPHA = 0.05;
	// private static final double ALPHA = 0.1;
	private static final double ALPHA = 0.1;
	private static final double NOISEMAX = 0.4;
	public static final long CUT_OFF = 100000;

	// weights from hidden to output layers
	double[] outputweight;
	// weights from input to hidden layers
	double[][] hiddenweight;

	// temporary space for caching hidden layer values
	double[] hidden;

	// number of nodes in input and hidden layers
	int size;

	// constructor. Called with the number of inputs: new Perceptron(3,2) makes a
	// three input, two output perceptron.
	Perceptron(int size) {
		this.size = size;
		// make an array of weights from each hidden, plus a bias, to each output node
		outputweight = new double[size + 1];
		// make a 2D array of weights from each input, plus a bias, to each hidden node
		hiddenweight = new double[size][size + 1];
		for (int i = 0; i < size + 1; i++)
			outputweight[i] = Math.random() * NOISEMAX - NOISEMAX / 2;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size + 1; j++)
				hiddenweight[i][j] = Math.random() * NOISEMAX - NOISEMAX / 2;
		// create the array for caching, but don't bother initializing it
		hidden = new double[size];
	}

	// returns whether the raw prediction is a 1 or 0
	int getPrediction(int[] inputs) {
		return getRawPrediction(inputs) >= 0.5 ? 1 : 0;
	}

	// takes an array of inputs in range 0 to 1, feeds them to the perceptron, saves
	// a guess in range 0 to 1 in array "outputs"
	double getRawPrediction(int[] inputs) {
		// TODO:
		// this.hidden = new double[this.size];

		int total;

		for (int h = 0; h < this.size; h++) {
			total = 0;
			// for each input
			for (int i = 0; i < inputs.length; i++) {
				// convert to -1 and 1
				int theInput = inputs[i] == 0 ? -1 : 1;
				total += this.hiddenweight[h][i] * theInput; // dot product
			}
			// bias
			total += this.hiddenweight[h][this.size];
			// apply threshold and save it
			this.hidden[h] = Perceptron.sigmoid(total);
		}

		// repeat this: hidden to output
		total = 0;
		for (int h = 0; h < this.size; h++) {
			total += this.outputweight[h] * this.hidden[h];
		}
		total += this.outputweight[this.size];
		double output = Perceptron.sigmoid(total);

		// 1. rescale the inputs from -1 to 1 and copy them to array inputs

		// 2. compute dot product of inputs times weights for each hidden. do sigmoid of
		// total and save it in array hidden

		// 3. compute dot product of hidden times weights for each output. do sigmoid
		// and return it
		return output; // replace this
	}

	double getTrainError(int[] inputs, int want) {
		// get the prediction
		double guess = this.getRawPrediction(inputs);
		double error = want - guess;
		double outputError = error * guess * (1 - guess);

		// get the hidden errors: the error for each hidden node
		double[] hiddenErrors = new double[this.size];
		for (int h = 0; h < this.size; h++) {
			// apply the error to each hidden node according to weight, then differentiate
			hiddenErrors[h] = this.hidden[h] * (1 - this.hidden[h]) * this.outputweight[h] * outputError;
		}

		// now, adjust the output weights based on the output error
		for (int h = 0; h < this.size; h++) {
			this.outputweight[h] += outputError * this.hidden[h] * Perceptron.ALPHA;
		}
		this.outputweight[this.size] = outputError * Perceptron.ALPHA;

		// repeat this process for each hidden node: adjust by the error
		for (int h = 0; h < this.size; h++) {
			for (int i = 0; i < this.size; i++) {
				int theInput = inputs[i] == 0 ? -1 : 1;
				this.hiddenweight[h][i] += hiddenErrors[h] * theInput * Perceptron.ALPHA;
			}
			this.hiddenweight[h][this.size] += hiddenErrors[h] * Perceptron.ALPHA;
		}

		return error;
	}

	// this trains the perceptron on an array of inputs (1/0) and desired outputs
	// (1/0)
	// the weights are adjusted and errors are saved in array "error". return TRUE
	// if training is done
	boolean train(int[] inputs, int want) {
		// TODO:

		// get the prediction
		double guess = this.getRawPrediction(inputs);
		double error = want - guess;
		double outputError = error * guess * (1 - guess);

		// get the hidden errors: the error for each hidden node
		double[] hiddenErrors = new double[this.size];

		for (int h = 0; h < this.size; h++) {
			// apply the error to each hidden node according to weight, then differentiate
			hiddenErrors[h] = this.hidden[h] * (1 - this.hidden[h]) * this.outputweight[h] * outputError;
		}

		// now, adjust the output weights based on the output error
		for (int h = 0; h < this.size; h++) {
			this.outputweight[h] += outputError * this.hidden[h] * Perceptron.ALPHA;
		}
		this.outputweight[this.size] = outputError * Perceptron.ALPHA;

		// repeat this process for each hidden node: adjust by the error
		for (int h = 0; h < this.size; h++) {
			for (int i = 0; i < this.size; i++) {
				int theInput = inputs[i] == 0 ? -1 : 1;
				this.hiddenweight[h][i] += hiddenErrors[h] * theInput * Perceptron.ALPHA;
			}
			this.hiddenweight[h][this.size] += hiddenErrors[h] * Perceptron.ALPHA;
		}

		// System.out.println("Error: " + error);

		// stop when error < .1
		if (error > -0.1 && error < 0.1) {
			return true;
		}
		// if (error > 0.1 && error < -0.1) {
		// return true;
		// }

		// 1. call getPrediction on inputs. this will put values in hidden and outputs
		// that we can use for training

		// 2. compute output error for each output and save it in "errors": error =
		// desired-predicted

		// 3. compute output training error for each output node: outtrainerror = error
		// * predicted * (1-predicted)

		// 4. compute hidden error for each hidden node: hiddenerror = sum of
		// (outtrainerror * output weight) over all outputs

		// 5. for each hidden node, apply output training error to weights: outputweight
		// += alpha * outtrainerror * hidden-value
		// don't forget to train the bias weight. it has a hidden-value of 1

		// 6. over each input, compute hidden training error: hiddentrainerror =
		// hiddenerror * hidden-value * (1-hidden-value)

		// 7. apply that error to the input weight: hiddenweight += hiddentrainingerror
		// * inputvalue * (1-inputvalue)

		// 8. go through all the errors in the array and keep track of the maximum. if
		// the max error is below some threshold (say 0.1), return TRUE. (else FALSE)

		return false; // replace this line
	}

	// implements the threshold function 1/(1+e^-x)
	// this is mathematically close to the >=0 threshold we use in the single layer
	// perceptron, but is differentiable
	static double sigmoid(double x) {
		// return 1.0 / (1.0 + Math.pow(2.71828, -x));
		return 1.0 / (1.0 + Math.exp(-x));
	}
}
