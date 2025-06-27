import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class CalculatorUI extends JFrame {
    private JTextField display;
    private StringBuilder input = new StringBuilder();
    private boolean expectingNewInput = false;

    public CalculatorUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 238, 252));

        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        display.setEditable(false);
        display.setBackground(Color.white);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(new Color(230, 238, 252));

        String[] buttons = {
            "C", "÷", "×", "-",
            "7", "8", "9", "+",
            "4", "5", "6", "=",
            "1", "2", "3", "",
            "0", ".", "", ""
        };

        for (String text : buttons) {
            if (text.isEmpty()) {
                buttonPanel.add(new JLabel());
            } else {
                JButton btn = new JButton(text);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
                btn.setFocusPainted(false);
                btn.setBackground(getButtonColor(text));
                btn.setForeground(Color.black);
                btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                btn.addActionListener(e -> handleInput(text));
                buttonPanel.add(btn);
            }
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void handleInput(String value) {
        switch (value) {
            case "C":
                input.setLength(0);
                display.setText("0");
                expectingNewInput = false;
                break;

            case "=":
                if (input.length() == 0) {
                    display.setText("0");
                    return;
                }

                try {
                    String expression = input.toString()
                            .replace("÷", "/")
                            .replace("×", "*");

                    double result = evaluateExpression(expression);
                    display.setText(formatResult(result));
                    input = new StringBuilder(formatResult(result));
                    expectingNewInput = true;
                } catch (Exception e) {
                    display.setText("Error");
                    input.setLength(0);
                    expectingNewInput = true;
                }
                break;

            default:
                if (expectingNewInput && !"+-×÷".contains(value)) {
                    input.setLength(0);
                    expectingNewInput = false;
                }

                if ("+-×÷".contains(value) && input.length() > 0 && 
                    "+-×÷".contains(input.substring(input.length() - 1))) {
                    input.deleteCharAt(input.length() - 1);
                }

                if (display.getText().equals("0") && !"+-×÷".contains(value)) {
                    input.setLength(0);
                }

                input.append(value);
                display.setText(input.toString());
                break;
        }
    }

    private double evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
        
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numStr = new StringBuilder();
                while (i < expression.length() && 
                      (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numStr.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(numStr.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (isOperator(c)) {
                while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }
        
        while (!operators.empty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }
        
        return numbers.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': 
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%s", result);
        }
    }

    private Color getButtonColor(String text) {
        switch (text) {
            case "C": return new Color(244, 67, 54);       
            case "=": return new Color(76, 175, 80);        
            case "+": case "-": case "×": case "÷":
                return new Color(249, 178, 52);           
            default: return new Color(224, 224, 224);       
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorUI calc = new CalculatorUI();
            calc.setVisible(true);
        });
    }
}
