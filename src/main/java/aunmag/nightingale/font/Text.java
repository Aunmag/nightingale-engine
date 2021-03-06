package aunmag.nightingale.font;

import aunmag.nightingale.Application;
import aunmag.nightingale.basics.BaseQuad;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Text extends BaseQuad {

    public static final TextManager manager = new TextManager();

    final FontStyle style;
    @Nullable private TextVao vao = null;
    private Vector4f colour = new Vector4f(1f, 1f, 1f, 1f);
    private Matrix4f projection;
    private boolean isRenderingOrdered = false;
    private boolean isOnWorldRendering = false;

    public Text(float x, float y, String message, FontStyle style) {
        super(x, y, 0, 0);
        this.style = style;

        load(message);
        manager.add(this);
        updateProjection();
    }

    public void load(String message) {
        if (message.equals(getMessage())) {
            return;
        }

        removeVao();
        vao = new TextVao(message, style);

        setSize(
                vao.getWidth() * Application.getWindow().getCenterX(),
                vao.height * Application.getWindow().getCenterY()
        );
    }

    public void updateProjection() {
        float x = getPosition().x();
        float y = getPosition().y();
        Vector2f position;

        if (isOnWorldRendering) {
            position = Application.getCamera().calculateViewPosition(x, y);
        } else {
            position = Application.getWindow().calculateViewPosition(x, y);
        }

        projection = new Matrix4f().translate(position.x(), position.y(), 0);
    }

    public void orderRendering() {
        isRenderingOrdered = true;
    }

    public void render() {
        if (!isRenderingOrdered || isRemoved() || vao == null) {
            return;
        }

        if (isOnWorldRendering) {
            updateProjection();
        }

        vao.bind();

        Application.getShader().setUniformColour(colour);
        Application.getShader().setUniformProjection(projection);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.vertexCount);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        isRenderingOrdered = false;
    }

    public void remove() {
        if (isRemoved()) {
            return;
        }

        removeVao();
        super.remove();
    }

    private void removeVao() {
        if (vao != null) {
            vao.remove();
            vao = null;
        }
    }

    /* Setters */

    public void setColour(float red, float green, float blue, float alpha) {
        colour.x = red;
        colour.y = green;
        colour.z = blue;
        colour.w = alpha;
    }

    public void setColour(Vector4f colour) {
        this.colour = colour;
    }

    public void setOnWorldRendering(boolean isOnWorldRendering) {
        if (this.isOnWorldRendering == isOnWorldRendering) {
            return;
        }

        this.isOnWorldRendering = isOnWorldRendering;
        updateProjection();
    }

    /* Getters */

    @Nullable
    public String getMessage() {
        if (vao == null) {
            return null;
        } else {
            return vao.message;
        }
    }

}
