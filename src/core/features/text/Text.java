package core.features.text;

import core.Settings;
import core.math.Vector;
import core.math.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;
import tools.Context;
import tools.Program;

import java.nio.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class Text  {

    private final ByteBuffer ttf;

    private final STBTTFontinfo info;

    private STBTTBakedChar.Buffer data;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    private int width;
    private int height;

    private String pathSourceName;
    private String text = null;

    private float widthText = 0.f;
    private int lineCount;

    private int fontHeight;

    private int scale;
    private int lineOffset;
    private float lineHeight;

    private Context context;

    public Text(Context context, String pathSourceName, int fontHeight, String text) {
        this.context = context;
        this.pathSourceName = pathSourceName;

        this.fontHeight = fontHeight;
        this.lineHeight = fontHeight;

        this.ttf = InputFileReader.readFileResource(pathSourceName);
        this.info = STBTTFontinfo.create();

        if(!stbtt_InitFont(this.info, this.ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try(MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(this.info, pAscent, pDescent, pLineGap);

            this.ascent = pAscent.get(0);
            this.descent = pDescent.get(0);
            this.lineGap = pLineGap.get(0);
        }

        this.setLineOffset(this.lineOffset);

        this.setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if(text != null) {
            // text width
            this.widthText = getStringWidth(text);

            // text rows count
            this.lineCount = text.split("\n").length;
        }

        // copy text
        this.text = text;
    }

    private void setLineOffset(float offset) {
        setLineOffset(round(offset));
    }

    private void setLineOffset(int offset) {
        lineOffset = max(0, min(offset, lineCount - (int) (Settings.HEIGHT / lineHeight)));
    }

    private void setScale(int scale) {
        this.scale = max(-3, scale);
        this.lineHeight = fontHeight * (1.0f + this.scale * 0.25f);
        setLineOffset(lineOffset);
    }

    public void init(Program program) {
        this.width = Math.round(512 * context.getScaleX());
        this.height = Math.round(512 * context.getScaleY());

        // 96 characters starting from 32, only ascii characters supported and only latin letters
        this.data = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(this.width * this.height);
        stbtt_BakeFontBitmap(this.ttf, this.fontHeight * context.getScaleY(), bitmap, this.width, this.height, 32, this.data);

        program.getContext().getContextTexture().addTexture(bitmap, this.pathSourceName, "u_texture", 0, this.width, this.height, GL_CLAMP);
        program.addAttribute("a_position");
        program.addAttribute("a_texture");
    }

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    public void renderText(Program program, int offsetX, int offsetY) {
        // nothing to do
        if(this.text == null)
            return;

        // get current scale
        float scale = stbtt_ScaleForPixelHeight(this.info, this.fontHeight);

        // push the stack and pop in outer scope
        try(MemoryStack memoryStack = stackPush()) {

            // character to be drawn
            IntBuffer character = memoryStack.mallocInt(1);

            FloatBuffer x = memoryStack.floats(0.0f);
            FloatBuffer y = memoryStack.floats(0.0f);

            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(memoryStack);

            float factorX = 1.0f / context.getScaleX();
            float factorY = 1.0f / context.getScaleY();

            float lineY = 0.0f;

            List<Vector> vertices = new ArrayList<>();
            List<Vector> textures = new ArrayList<>();
            for (int i = 0, to = this.text.length(); i < to; ) {
                i += getCharacterStep(this.text, to, i, character);

                int cp = character.get(0);
                if(cp == '\n') {
                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                    x.put(0, 0.0f);

                    continue;
                } else if (cp < 32 || 128 <= cp) {
                    continue;
                }

                float cpX = x.get(0);
                stbtt_GetBakedQuad(this.data, this.width, this.height, cp - 32, x, y, quad, true);

                x.put(0, scale(cpX, x.get(0), factorX));

                float x0 = scale(cpX, quad.x0(), factorX) + offsetX - this.widthText / 2;
                float x1 = scale(cpX, quad.x1(), factorX) + offsetX - this.widthText / 2;
                float y0 = scale(lineY, quad.y0(), factorY) + offsetY;
                float y1 = scale(lineY, quad.y1(), factorY) + offsetY;

                textures.add(new Vector2f(quad.s0(), quad.t0()));
                vertices.add(new Vector2f(x0, y0));

                textures.add(new Vector2f(quad.s1(), quad.t0()));
                vertices.add(new Vector2f(x1, y0));

                textures.add(new Vector2f(quad.s1(), quad.t1()));
                vertices.add(new Vector2f(x1, y1));

                textures.add(new Vector2f(quad.s1(), quad.t1()));
                vertices.add(new Vector2f(x1, y1));

                textures.add(new Vector2f(quad.s0(), quad.t1()));
                vertices.add(new Vector2f(x0, y1));

                textures.add(new Vector2f(quad.s0(), quad.t0()));
                vertices.add(new Vector2f(x0, y0));
            }

            program.updateDataV("a_position", vertices, 2);
            program.updateDataV("a_texture", textures, 2);
        }
    }

    private float getStringWidth(String text) {
        int width = 0;
        int acc = 0;

        try(MemoryStack stack = stackPush()) {
            IntBuffer character = stack.mallocInt(1);
            IntBuffer pAdvancedWidth = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = 0;
            while (i < text.length()) {
                i += getCharacterStep(text, text.length(), i, character);
                int ch = character.get(0);

                stbtt_GetCodepointHMetrics(this.info, ch, pAdvancedWidth, pLeftSideBearing);
                if(ch == '\n') {
                    width = Math.max(width, acc);

                    acc = 0;

                    continue;
                }

                acc += pAdvancedWidth.get(0);
            }
        }

        if(acc != 0) {
            width = Math.max(width, acc);
        }

        return width * stbtt_ScaleForPixelHeight(this.info, fontHeight);
    }

    private static int getCharacterStep(String text, int to, int position, IntBuffer character) {
        char c1 = text.charAt(position);
        if(Character.isHighSurrogate(c1) && position + 1 < to) {
            char c2 = text.charAt(position + 1);
            if(Character.isLowSurrogate(c2)) {
                character.put(0, Character.toCodePoint(c1, c2));

                return 2;
            }
        }

        character.put(0, c1);

        return 1;
    }

    public void free() {
        this.data.free();
    }
}