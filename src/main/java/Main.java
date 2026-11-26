import zero.Color;
import zero.GameApp;
import zero.Key;
import zero.ui.Button;
import zero.ui.Column;
import zero.ui.Label;
import zero.ui.Row;

public class Main extends GameApp {
    private static final double SIZE = 26;
    private double px, py, vx, vy;
    private boolean started, done;
    private int score;
    private Label info;

    // Moving space rocks & golden butter boosts on the way to the moon
    private double[] rx = {90, 430, 170, 470, 110, 380};
    private double[] ry = {320, 320, 225, 225, 135, 135};
    private double[] rvx = {95, -85, -110, 110, 130, -130};
    private double[] bx = {265, 320, 220};
    private double[] by = {300, 205, 115};
    private boolean[] got = new boolean[3];

    @Override public void settings() {
        padding(20);
        size(640, 460);
    }

    @Override public void setup() {
        info = new Label("");
        var restart = new Button("Restart");
        restart.onClick(() -> restart());
        controls(new Column(8, new Row(12, info, restart)));
        restart();
    }

    public void restart() {
        px = width() / 2.0 - SIZE / 2.0;
        py = height() - 55;
        vx = 0;
        vy = 0;
        started = false;
        done = false;
        score = 0;
        got = new boolean[3];
        info.setText("Score: 0 | Click canvas, press Space/Arrows/WASD to launch");
        showGame();
    }

    private boolean hits(double ax, double ay, double aw, double ah,
                         double bx0, double by0, double bw, double bh) {
        return ax + aw > bx0 && ax < bx0 + bw && ay + ah > by0 && ay < by0 + bh;
    }

    @Override public void update(double dt) {
        if (done) return;

        boolean l = keyDown(Key.LEFT) || keyDown(Key.A);
        boolean r = keyDown(Key.RIGHT) || keyDown(Key.D);
        boolean up = keyDown(Key.SPACE) || keyDown(Key.UP) || keyDown(Key.W);

        if (!started && (l || r || up)) {
            started = true;
            vy = -195;
        }
        if (started) {
            vx = (r ? 215 : 0) - (l ? 215 : 0);
            vy = Math.max(-240, Math.min(-75, vy + (up ? -140 : -65) * dt));
            px = Math.max(0, Math.min(width() - SIZE, px + vx * dt));
            py += vy * dt;
        }

        for (int i = 0; i < rx.length; i++) {
            rx[i] += rvx[i] * dt;
            if (rx[i] < 0 || rx[i] + 34 > width()) rvx[i] = -rvx[i];
            if (hits(px, py, SIZE, SIZE, rx[i], ry[i], 34, 34)) {
                done = true;
                info.setText("Score: " + score + " | Hit a rock! Click Restart.");
                return;
            }
        }

        for (int i = 0; i < bx.length; i++) {
            if (!got[i] && hits(px, py, SIZE, SIZE, bx[i], by[i], 16, 16)) {
                got[i] = true;
                score += 10;
                info.setText("Score: " + score + " | Butter collected (+10)!");
            }
        }

        double moonX = width() / 2.0 - 28;
        if (hits(px, py, SIZE, SIZE, moonX, 45, 56, 56)) {
            done = true;
            score += 50;
            info.setText("Score: " + score + " | Toast on the Moon (+50)! You win!");
        }
    }

    @Override public void draw() {
        background(Color.rgb(14, 20, 42));

        // Moon
        fill(Color.rgb(252, 242, 190));
        rect(width() / 2.0 - 28, 45, 56, 56);

        // Butter
        fill(Color.rgb(255, 224, 71));
        for (int i = 0; i < bx.length; i++) {
            if (!got[i]) rect(bx[i], by[i], 16, 16);
        }

        // Rocks
        fill(Color.rgb(120, 126, 142));
        for (int i = 0; i < rx.length; i++) {
            rect(rx[i], ry[i], 34, 34);
        }

        // Toast
        fill(Color.rgb(214, 154, 86));
        rect(px, py, SIZE, SIZE);
        fill(Color.rgb(245, 222, 179));
        rect(px + 4, py + 4, SIZE - 8, SIZE - 8);
    }

    public static void main(String[] args) { launch(args); }
}