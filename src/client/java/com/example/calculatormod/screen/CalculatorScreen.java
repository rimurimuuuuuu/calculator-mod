package com.example.calculatormod.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class CalculatorScreen extends Screen {

    // 電卓の状態
    private String displayText = "0";
    private String expression = "";
    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean startNewNumber = true;
    private boolean hasDecimal = false;
    private List<String> history = new ArrayList<>();

    // レイアウト定数
    private static final int CALC_WIDTH = 220;
    private static final int CALC_HEIGHT = 300;
    private static final int DISPLAY_HEIGHT = 60;
    private static final int BUTTON_SIZE = 40;
    private static final int BUTTON_GAP = 5;
    private static final int PADDING = 10;

    // カラー定数 (Minecraft風ダークテーマ)
    private static final int COLOR_BG = 0xFF1A1A2E;
    private static final int COLOR_DISPLAY_BG = 0xFF16213E;
    private static final int COLOR_DISPLAY_BORDER = 0xFF0F3460;
    private static final int COLOR_NUM_BTN = 0xFF2D2D44;
    private static final int COLOR_NUM_BTN_HOVER = 0xFF3D3D5C;
    private static final int COLOR_OP_BTN = 0xFF0F3460;
    private static final int COLOR_OP_BTN_HOVER = 0xFF1A4A8A;
    private static final int COLOR_EQUALS_BTN = 0xFF533483;
    private static final int COLOR_EQUALS_BTN_HOVER = 0xFF6B46C1;
    private static final int COLOR_CLEAR_BTN = 0xFF8B0000;
    private static final int COLOR_CLEAR_BTN_HOVER = 0xFFB00000;
    private static final int COLOR_TEXT = 0xFFE2E8F0;
    private static final int COLOR_EXPR_TEXT = 0xFF94A3B8;

    public CalculatorScreen() {
        super(Text.translatable("screen.calculatormod.calculator"));
    }

    @Override
    protected void init() {
        int startX = (this.width - CALC_WIDTH) / 2;
        int startY = (this.height - CALC_HEIGHT) / 2;

        // ボタン配列の定義
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
                ButtonWidget button = ButtonWidget.builder(
                        Text.literal(label),
                        btn -> handleButtonPress(btnLabel)
                ).dimensions(btnX, currentBtnY, BUTTON_SIZE, BUTTON_SIZE).build();

                this.addDrawableChild(button);
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
            String removed = displayText.substring(displayText.length() - 1);
            if (removed.equals(".")) hasDecimal = false;
            displayText = displayText.substring(0, displayText.length() - 1);
        } else {
            displayText = "0";
            startNewNumber = true;
            hasDecimal = false;
        }
    }

    private void toggleSign() {
        if (!displayText.equals("0")) {
            if (displayText.startsWith("-")) {
                displayText = displayText.substring(1);
            } else {
                displayText = "-" + displayText;
            }
        }
    }

    private void percentage() {
        try {
            double value = Double.parseDouble(displayText);
            value /= 100.0;
            displayText = formatResult(value);
        } catch (NumberFormatException ignored) {}
    }

    private void setOperator(String op) {
        try {
            if (!pendingOperator.isEmpty() && !startNewNumber) {
                calculate();
            }
            firstOperand = Double.parseDouble(displayText);
            pendingOperator = op;
            String opSymbol = opToSymbol(op);
            expression = formatResult(firstOperand) + " " + opSymbol;
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
            double secondOperand = Double.parseDouble(displayText);
            double result = switch (pendingOperator) {
                case "+" -> firstOperand + secondOperand;
                case "-" -> firstOperand - secondOperand;
                case "*" -> firstOperand * secondOperand;
                case "/" -> {
                    if (secondOperand == 0) {
                        displayText = "エラー";
                        expression = "";
                        pendingOperator = "";
                        startNewNumber = true;
                        yield Double.NaN;
                    }
                    yield firstOperand / secondOperand;
                }
                default -> secondOperand;
            };

            if (!Double.isNaN(result)) {
                String historyEntry = expression + " " + formatResult(secondOperand) + " = " + formatResult(result);
                if (history.size() >= 5) history.remove(0);
                history.add(historyEntry);

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
            if (startNewNumber) {
                displayText = "0.";
                startNewNumber = false;
            } else {
                displayText += ".";
            }
            hasDecimal = true;
        }
    }

    private void appendDigit(String digit) {
        if (startNewNumber) {
            displayText = digit;
            startNewNumber = false;
        } else {
            if (displayText.equals("0")) {
                displayText = digit;
            } else {
                if (displayText.length() < 12) {
                    displayText += digit;
                }
            }
        }
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return "エラー";
        if (value == Math.floor(value) && Math.abs(value) < 1e12) {
            return String.valueOf((long) value);
        }
        // 小数点以下の不要なゼロを除去
        String result = String.format("%.8f", value);
        result = result.replaceAll("0+$", "").replaceAll("\\.$", "");
        return result;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景を暗くする
        this.renderBackground(context, mouseX, mouseY, delta);

        int startX = (this.width - CALC_WIDTH) / 2;
        int startY = (this.height - CALC_HEIGHT) / 2;

        // 電卓の背景
        context.fill(startX, startY, startX + CALC_WIDTH, startY + CALC_HEIGHT, COLOR_BG);

        // ディスプレイ背景
        context.fill(startX + PADDING, startY + PADDING,
                startX + CALC_WIDTH - PADDING, startY + DISPLAY_HEIGHT,
                COLOR_DISPLAY_BG);

        // ディスプレイの枠線
        drawBorder(context, startX + PADDING, startY + PADDING,
                startX + CALC_WIDTH - PADDING, startY + DISPLAY_HEIGHT,
                COLOR_DISPLAY_BORDER, 1);

        // タイトル
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("⬡ CALCULATOR"),
                startX + PADDING + 5, startY + 2,
                0xFF64748B);

        // 式の表示（小さく）
        if (!expression.isEmpty()) {
            String exprDisplay = expression.length() > 20 ?
                    "..." + expression.substring(expression.length() - 17) : expression;
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal(exprDisplay),
                    startX + CALC_WIDTH - PADDING - this.textRenderer.getWidth(exprDisplay) - 5,
                    startY + PADDING + 5,
                    COLOR_EXPR_TEXT);
        }

        // メイン表示（大きな数字）
        String displayStr = displayText.length() > 14 ?
                displayText.substring(0, 14) + "..." : displayText;

        // 数字を右寄せで大きく表示
        float scale = displayStr.length() > 10 ? 1.2f : (displayStr.length() > 7 ? 1.5f : 2.0f);
        int textWidth = (int)(this.textRenderer.getWidth(displayStr) * scale);
        int textX = startX + CALC_WIDTH - PADDING - textWidth - 5;
        int textY = startY + DISPLAY_HEIGHT - 22;

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawTextWithShadow(this.textRenderer,
                Text.literal(displayStr),
                (int)(textX / scale), (int)(textY / scale),
                COLOR_TEXT);
        context.getMatrices().pop();

        // ESCで閉じる案内
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("ESC で閉じる"),
                startX + PADDING + 5,
                startY + CALC_HEIGHT - 12,
                0xFF475569);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBorder(DrawContext context, int x1, int y1, int x2, int y2, int color, int thickness) {
        context.fill(x1, y1, x2, y1 + thickness, color);
        context.fill(x1, y2 - thickness, x2, y2, color);
        context.fill(x1, y1, x1 + thickness, y2, color);
        context.fill(x2 - thickness, y1, x2, y2, color);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // キーボード入力のサポート
        if (keyCode >= 48 && keyCode <= 57) { // 0-9
            appendDigit(String.valueOf((char) keyCode));
            return true;
        }
        switch (keyCode) {
            case 261 -> { backspace(); return true; } // Delete
            case 259 -> { backspace(); return true; } // Backspace
            case 257, 335 -> { calculate(); return true; } // Enter / Numpad Enter
            case 268 -> { clearAll(); return true; } // Home (= Clear)
            case 334 -> { setOperator("+"); return true; } // Numpad +
            case 333 -> { setOperator("-"); return true; } // Numpad -
            case 332 -> { setOperator("*"); return true; } // Numpad *
            case 331 -> { setOperator("/"); return true; } // Numpad /
            case 330 -> { addDecimal(); return true; } // Numpad .
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= '0' && chr <= '9') {
            appendDigit(String.valueOf(chr));
            return true;
        }
        switch (chr) {
            case '+' -> { setOperator("+"); return true; }
            case '-' -> { setOperator("-"); return true; }
            case '*' -> { setOperator("*"); return true; }
            case '/' -> { setOperator("/"); return true; }
            case '.' -> { addDecimal(); return true; }
            case '=' -> { calculate(); return true; }
            case 'c', 'C' -> { clearAll(); return true; }
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false; // ゲームを一時停止しない
    }
}
