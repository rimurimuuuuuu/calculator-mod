package com.example.calculatormod.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class CalculatorScreen extends Screen {

    private String displayText = "0";
    private String expression = "";
    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean startNewNumber = true;
    private boolean hasDecimal = false;

    private static final int CALC_WIDTH = 220;
    private static final int CALC_HEIGHT = 290;
    private static final int DISPLAY_HEIGHT = 55;
    private static final int BUTTON_SIZE = 40;
    private static final int BUTTON_GAP = 5;
    private static final int PADDING = 10;

    private static final int COLOR_BG = 0xFF1A1A2E;
    private static final int COLOR_DISPLAY_BG = 0xFF16213E;
    private static final int COLOR_TEXT = 0xFFE2E8F0;
    private static final int COLOR_EXPR_TEXT = 0xFF94A3B8;

    public CalculatorScreen() {
        super(Text.literal("電卓"));
    }

    @Override
    protected void init() {
        int startX = (this.width - CALC_WIDTH) / 2;
        int startY = (this.height - CALC_HEIGHT) / 2;

        String[][] buttons = {
                {"C", "±", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"0", ".", "⌫", "="}
        };

        int btnY = startY + DISPLAY_HEIGHT + PADDING * 2;

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String label = buttons[row][col];
                int btnX = startX + PADDING + col * (BUTTON_SIZE + BUTTON_GAP);
                int currentBtnY = btnY + row * (BUTTON_SIZE + BUTTON_GAP);
                final String btnLabel = label;
                this.addDrawableChild(ButtonWidget.builder(
                        Text.literal(label),
                        btn -> handleButtonPress(btnLabel)
                ).dimensions(btnX, currentBtnY, BUTTON_SIZE, BUTTON_SIZE).build());
            }
        }
    }

    private void handleButtonPress(String label) {
        switch (label) {
            case "C" -> clearAll();
            case "⌫" -> backspace();
            case "±" -> toggleSign();
            case "%" -> percentage();
            case "÷" -> setOperator("/");
            case "×" -> setOperator("*");
            case "−" -> setOperator("-");
            case "+" -> setOperator("+");
            case "=" -> calculate();
            case "." -> addDecimal();
            default -> appendDigit(label);
        }
    }

    private void clearAll() {
        displayText = "0";
        expression = "";
        firstOperand = 0;
        pendingOperator = "";
        startNewNumber = true;
        hasDecimal = false;
    }

    private void backspace() {
        if (displayText.length() > 1) {
            if (displayText.endsWith(".")) hasDecimal = false;
            displayText = displayText.substring(0, displayText.length() - 1);
        } else {
            displayText = "0";
            startNewNumber = true;
            hasDecimal = false;
        }
    }

    private void toggleSign() {
        if (!displayText.equals("0")) {
            displayText = displayText.startsWith("-") ? displayText.substring(1) : "-" + displayText;
        }
    }

    private void percentage() {
        try {
            double value = Double.parseDouble(displayText) / 100.0;
            displayText = formatResult(value);
        } catch (NumberFormatException ignored) {}
    }

    private void setOperator(String op) {
        try {
            if (!pendingOperator.isEmpty() && !startNewNumber) calculate();
            firstOperand = Double.parseDouble(displayText);
            pendingOperator = op;
            expression = formatResult(firstOperand) + " " + opToSymbol(op);
            startNewNumber = true;
            hasDecimal = false;
        } catch (NumberFormatException ignored) {}
    }

    private String opToSymbol(String op) {
        return switch (op) {
            case "/" -> "÷";
            case "*" -> "×";
            case "-" -> "−";
            default -> op;
        };
    }

    private void calculate() {
        if (pendingOperator.isEmpty()) return;
        try {
            double second = Double.parseDouble(displayText);
            double result = switch (pendingOperator) {
                case "+" -> firstOperand + second;
                case "-" -> firstOperand - second;
                case "*" -> firstOperand * second;
                case "/" -> {
                    if (second == 0) { displayText = "エラー"; expression = ""; pendingOperator = ""; startNewNumber = true; yield Double.NaN; }
                    yield firstOperand / second;
                }
                default -> second;
            };
            if (!Double.isNaN(result)) {
                displayText = formatResult(result);
                expression = "";
                pendingOperator = "";
                firstOperand = result;
                startNewNumber = true;
                hasDecimal = displayText.contains(".");
            }
        } catch (NumberFormatException ignored) {}
    }

    private void addDecimal() {
        if (!hasDecimal) {
            displayText = startNewNumber ? "0." : displayText + ".";
            startNewNumber = false;
            hasDecimal = true;
        }
    }

    private void appendDigit(String digit) {
        if (startNewNumber) {
            displayText = digit;
            startNewNumber = false;
        } else {
            displayText = displayText.equals("0") ? digit : (displayText.length() < 12 ? displayText + digit : displayText);
        }
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return "エラー";
        if (value == Math.floor(value) && Math.abs(value) < 1e12) return String.valueOf((long) value);
        return String.format("%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int startX = (this.width - CALC_WIDTH) / 2;
        int startY = (this.height - CALC_HEIGHT) / 2;

        context.fill(startX, startY, startX + CALC_WIDTH, startY + CALC_HEIGHT, COLOR_BG);
        context.fill(startX + PADDING, startY + PADDING, startX + CALC_WIDTH - PADDING, startY + DISPLAY_HEIGHT, COLOR_DISPLAY_BG);

        // 式の表示
        if (!expression.isEmpty()) {
            context.drawText(this.textRenderer, Text.literal(expression),
                    startX + CALC_WIDTH - PADDING - this.textRenderer.getWidth(expression) - 5,
                    startY + PADDING + 5, COLOR_EXPR_TEXT, true);
        }

        // メイン数字表示
        String displayStr = displayText.length() > 14 ? displayText.substring(0, 14) : displayText;
        context.drawText(this.textRenderer, Text.literal(displayStr),
                startX + CALC_WIDTH - PADDING - this.textRenderer.getWidth(displayStr) - 5,
                startY + DISPLAY_HEIGHT - 18, COLOR_TEXT, true);

        // ESCで閉じる
        context.drawText(this.textRenderer, Text.literal("ESC で閉じる"),
                startX + PADDING + 5, startY + CALC_HEIGHT - 12, 0xFF475569, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode >= 48 && keyCode <= 57) { appendDigit(String.valueOf((char) keyCode)); return true; }
        return switch (keyCode) {
            case 259, 261 -> { backspace(); yield true; }
            case 257, 335 -> { calculate(); yield true; }
            case 268 -> { clearAll(); yield true; }
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= '0' && chr <= '9') { appendDigit(String.valueOf(chr)); return true; }
        return switch (chr) {
            case '+' -> { setOperator("+"); yield true; }
            case '-' -> { setOperator("-"); yield true; }
            case '*' -> { setOperator("*"); yield true; }
            case '/' -> { setOperator("/"); yield true; }
            case '.' -> { addDecimal(); yield true; }
            case '=' -> { calculate(); yield true; }
            default -> super.charTyped(chr, modifiers);
        };
    }

    @Override
    public boolean shouldPause() { return false; }
}
