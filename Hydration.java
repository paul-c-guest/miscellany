
public class Hydration {

	public static void main(String[] args) {
		
		double targetWeight = 0.0;
		double hydrationPercent = 0.0; 
		double salt = 0.0;

		// test for 'help' command in first argument, or too many args
		if (args.length == 0 || args[0].contains("h") || args.length > 3) {
			printHelpText();
			return;
		}

		if (args.length > 1) {
			try {
				targetWeight = Double.parseDouble(args[0]);
				hydrationPercent = formatPercentageInput(Double.parseDouble(args[1]));
			} catch (Exception e) {
				System.out.println("Something is wrong with the first two numbers.");
				printHelpText();
				return;
			}
		}

		if (args.length == 3) {
			try {
				salt = formatPercentageInput(Double.parseDouble(args[2]));
				if (salt > 0.05) {
					System.out.println("Heads up! Did you intend to calculate for " + (int) (salt * 100) + "% salt?");
				}
			} catch (Exception e) {
				System.out.println("Something is wrong with the third input number.");
				printHelpText();
				return;
			}
		}

		double[] results = calculate(targetWeight, hydrationPercent, salt);

		System.out.format("dry:  %1$.1f%nwet:  %2$.1f", results[0], results[1]);
		System.out.println();
		if (results[2] > 0) {
			System.out.format("salt: %1$.1f", results[2]);
			System.out.println();
		}
	}

	public static double[] calculate(double targetWeight, double splitPercent, double saltPercent,
			double... extraWeights) {

		double[] mainSplit = calc(targetWeight, splitPercent);
		double[] saltSplit = calc(mainSplit[0] * saltPercent, splitPercent);

		mainSplit[0] -= saltSplit[0];
		mainSplit[1] -= saltSplit[1];

		return new double[] { mainSplit[0], mainSplit[1], saltSplit[0] + saltSplit[1] };
	}

	/**
	 * get the weight of the two components for a given total target weight, and the
	 * required percentage split. Note: this returns results in the form of "Baker's
	 * percentages"; where the reference weight is regarded as '100%' and other
	 * weights are defined as percentages of that reference weight.
	 * <p>
	 * The results are returned in a double[2]. array[0] is the reference weight,
	 * array[1] is the derived percentage weight. this method will not take into
	 * account any other components such as yeast or salt.
	 * 
	 * @param targetWeight the required reference weight
	 * @param splitPercent the required weight of the reference percentage
	 * @return an array containing two double values
	 */
	private static double[] calc(double targetWeight, double splitPercent) {
		double result = targetWeight / (1 + splitPercent);
		double remainder = targetWeight - result;

		return new double[] { result, remainder };
	}

	// check whether wetness was given as a non-fractional number, eg '68', convert
	// if necessary to form "zero point [input]"
	private static double formatPercentageInput(double input) {
		if (input >= 1)
			input /= 100;

		return input;
	}

	private static void printHelpText() {
		System.out.println("Provide a total dough weight, and hydration percentage.\n"
				+ "Optionally, provide a salt percentage.\n\nThese are all valid inputs:\njava Hydration 800 68\n"
				+ "java Hydration 800 68 3.5\njava Hydration 800 0.68\njava Hydration 800 68 .02");
		System.out.println();
	}
}
