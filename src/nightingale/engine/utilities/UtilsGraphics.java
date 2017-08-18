package nightingale.engine.utilities;

import nightingale.engine.Application;
import nightingale.engine.basics.BaseQuad;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class UtilsGraphics {

    public static void drawPrepare() {
        GL20.glUseProgram(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static void drawLine(Vector2f a, Vector2f b, boolean isOnWorld) {
        if (isOnWorld) {
            a = Application.getCamera().calculateViewPosition(a);
            b = Application.getCamera().calculateViewPosition(b);
        } else {
            a = Application.getWindow().calculateViewPosition(a);
            b = Application.getWindow().calculateViewPosition(b);
        }

        drawLine(a, b);
    }

    public static void drawLine(Vector2f a, Vector2f b) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(a.x(), a.y());
        GL11.glVertex2f(b.x(), b.y());
        GL11.glEnd();
    }

    public static void drawQuad(BaseQuad quad, boolean isFilled, boolean isOnWorld) {
        drawQuad(
                quad.getPointA(),
                quad.getPointB(),
                quad.getPointC(),
                quad.getPointD(),
                isFilled,
                isOnWorld
        );
    }

    public static void drawQuad(
            Vector2f a,
            Vector2f b,
            Vector2f c,
            Vector2f d,
            boolean isFilled,
            boolean isOnWorld
    ) {
        if (isOnWorld) {
            a = Application.getCamera().calculateViewPosition(a);
            b = Application.getCamera().calculateViewPosition(b);
            c = Application.getCamera().calculateViewPosition(c);
            d = Application.getCamera().calculateViewPosition(d);
        } else {
            a = Application.getWindow().calculateViewPosition(a);
            b = Application.getWindow().calculateViewPosition(b);
            c = Application.getWindow().calculateViewPosition(c);
            d = Application.getWindow().calculateViewPosition(d);
        }

        if (isFilled) {
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2f(a.x(), a.y()); // 1. a - top left
            GL11.glVertex2f(d.x(), d.y()); // 1. d - down left
            GL11.glVertex2f(b.x(), b.y()); // 1. b - top right
            GL11.glVertex2f(b.x(), b.y()); // 2. b - top right
            GL11.glVertex2f(d.x(), d.y()); // 2. d - down left
            GL11.glVertex2f(c.x(), c.y()); // 2. c - down right
            GL11.glEnd();
        } else {
            drawLine(a, b);
            drawLine(b, c);
            drawLine(c, d);
            drawLine(d, a);
        }
    }

    public static void drawCircle(
            Vector2f position,
            float radius,
            boolean isFilled,
            boolean isOnWorld
    ) {
        if (isOnWorld) {
            position = Application.getCamera().calculateViewPosition(position);
        }

        // TODO: Learn difference between GL_TRIANGLE_FAN and GL_POLYGON
        GL11.glBegin(isFilled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_LOOP);
        final float accuracy = 0.4f;
        for (float radians = 0; radians <= UtilsMath.PIx2; radians += accuracy) {
            float x = (float) (position.x() + radius * Math.cos(radians));
            float y = (float) (position.y() + radius * Math.sin(radians));

            if (isOnWorld) {
                Vector2f viewPosition = Application.getCamera().calculateViewPosition(
                        new Vector2f(x, y)
                );
                x = viewPosition.x();
                y = viewPosition.y();
            }

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();
    }

    public static void drawFinish() {
        Application.getShader().bind();
    }

}