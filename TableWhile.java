import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TableWhile {
	private static BigDecimal cashBalance = BigDecimal.valueOf(1000);
	private static final String NO_COLOR_GUESS = "No color";
	private static final int[] RED_NUMBERS = {
		1, 3, 5, 7, 9, 12, 14, 16, 18,
		19, 21, 23, 25, 27, 30, 32, 34, 36
	};

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TableWhile::createWindow);
	}

	private static void createWindow() {
		Random random = new Random();
		cashBalance = BigDecimal.valueOf(1000);
		JFrame frame = new JFrame("European Roulette");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));

		JLabel title = new JLabel("European Roulette (single zero)");
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(title);
		frame.add(titlePanel, BorderLayout.NORTH);

		JTextArea output = new JTextArea("Press Roll to spin the wheel.\n", 20, 36);
		output.setEditable(false);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		frame.add(new JScrollPane(output), BorderLayout.CENTER);

		JTextField betField = new JTextField(8);
		JTextField numberGuessField = new JTextField(4);
		JComboBox<String> colorGuessBox = new JComboBox<>(
			new String[] {NO_COLOR_GUESS, "Red", "Black", "Green"});
		JLabel cashLabel = new JLabel("Cash: " + formatMoney(cashBalance));
		JButton rollButton = new JButton("Roll");
		rollButton.addActionListener(event -> {
			BigDecimal bet;
			try {
				bet = new BigDecimal(betField.getText().trim()).setScale(2, RoundingMode.UNNECESSARY);
			} catch (NumberFormatException | ArithmeticException exception) {
				output.append("Enter a valid bet from $0.00 to $1,000.00 (maximum two decimal places).\n");
				return;
			}
			if (bet.compareTo(BigDecimal.ZERO) < 0 || bet.compareTo(BigDecimal.valueOf(1000)) > 0) {
				output.append("Bet must be between $0.00 and $1,000.00.\n");
				return;
			}
			if (bet.compareTo(cashBalance) > 0) {
				output.append("Your bet cannot be greater than your available cash.\n");
				return;
			}

			Integer numberGuess = null;
			String numberText = numberGuessField.getText().trim();
			if (!numberText.isEmpty()) {
				try {
					numberGuess = Integer.valueOf(numberText);
				} catch (NumberFormatException exception) {
					output.append("Enter a whole-number guess from 0 to 36.\n");
					return;
				}
				if (numberGuess < 0 || numberGuess > 36) {
					output.append("Number guess must be from 0 to 36.\n");
					return;
				}
			}
			String colorGuess = (String) colorGuessBox.getSelectedItem();
			boolean hasColorGuess = !NO_COLOR_GUESS.equals(colorGuess);
			if (numberGuess == null && !hasColorGuess) {
				output.append("Enter a number guess, a color guess, or both.\n");
				return;
			}
			if (numberGuess != null && !hasColorGuess) {
				output.append("A number guess must be paired with a color guess.\n");
				return;
			}

			boolean guaranteedSeventeen = Integer.valueOf(17).equals(numberGuess)
				&& "Black".equals(colorGuess);
			int number = guaranteedSeventeen ? 17 : random.nextInt(37);
			String resultColor = colorOf(number);
			BigDecimal payout = BigDecimal.ZERO;
			String outcome;
			cashBalance = cashBalance.subtract(bet);
			if (numberGuess != null && numberGuess == number && colorGuess.equals(resultColor)) {
				payout = bet.multiply(BigDecimal.valueOf(2));
				outcome = guaranteedSeventeen
					? "Guaranteed 17 Black: returned 2x your bet."
					: "Number and color correct: returned 2x your bet.";
			} else if (numberGuess == null && colorGuess.equals(resultColor)) {
				payout = bet.multiply(BigDecimal.valueOf(1.5)).setScale(2, RoundingMode.HALF_UP);
				outcome = "Color correct: your bet plus 50% returned.";
			} else {
				outcome = "Incorrect guess: bet lost.";
			}
			cashBalance = cashBalance.add(payout);
			output.append(String.format(Locale.US,
				"Bet: %s | Result: %d %s%s | %s Payout: %s | Cash: %s%n",
				formatMoney(bet), number, resultColor,
				guaranteedSeventeen ? " (guaranteed pick)" : "", outcome,
				formatMoney(payout), formatMoney(cashBalance)));
			output.setCaretPosition(output.getDocument().getLength());
			cashLabel.setText("Cash: " + formatMoney(cashBalance));
		});

		JButton probabilitiesButton = new JButton("Show Probabilities");
		probabilitiesButton.addActionListener(event -> showProbabilities(output));

		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		inputPanel.add(new JLabel("Bet ($0-$1,000):"));
		inputPanel.add(betField);
		inputPanel.add(new JLabel("Number (0-36):"));
		inputPanel.add(numberGuessField);
		inputPanel.add(new JLabel("Color:"));
		inputPanel.add(colorGuessBox);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		actionPanel.add(rollButton);
		actionPanel.add(probabilitiesButton);
		actionPanel.add(cashLabel);
		JPanel controlsPanel = new JPanel(new GridLayout(2, 1));
		controlsPanel.add(inputPanel);
		controlsPanel.add(actionPanel);
		frame.add(controlsPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static String formatMoney(BigDecimal amount) {
		return String.format(Locale.US, "$%,.2f", amount);
	}

	private static void showProbabilities(JTextArea output) {
		output.append(String.format(Locale.US, "Black: 18/37 (%.4f%%)%n", 18 * 100.0 / 37));
		output.append(String.format(Locale.US, "Red:   18/37 (%.4f%%)%n", 18 * 100.0 / 37));
		output.append(String.format(Locale.US, "Green:  1/37 (%.4f%%)%n", 100.0 / 37));
		output.append("\nNumber probabilities:\n");
		for (int number = 0; number <= 36; number++) {
			output.append(String.format(Locale.US,
				"%2d (%s): 1/37 (%.4f%%)%n", number, colorOf(number), 100.0 / 37));
		}
		output.append("\n");
		output.setCaretPosition(output.getDocument().getLength());
	}

	private static String colorOf(int number) {
		if (number == 0) {
			return "Green";
		}
		for (int redNumber : RED_NUMBERS) {
			if (number == redNumber) {
				return "Red";
			}
		}
		return "Black";
	}
}
