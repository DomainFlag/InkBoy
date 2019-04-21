#version 430

// Triangles is the domain the PG will work on.
// Segments of equal lengths
// Counter-clockwise order
layout(triangles, fractional_odd_spacing, cw) in;

in vec2 mapCoord_TE[];

out vec2 mapCoord_GS;

uniform mat4 u_projection;
uniform mat4 u_camera;
uniform mat4 u_model;

uniform float u_scale;

uniform sampler2D u_texture;

out float v_gheight;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;

    vec4 position = u * gl_in[0].gl_Position +
        v * gl_in[1].gl_Position +
        w * gl_in[2].gl_Position;

    vec2 mapCoord_GS = u * mapCoord_TE[0] +
        v * mapCoord_TE[1] +
        w * mapCoord_TE[2];

    float height = texture(u_texture, mapCoord_GS).r;
    height *= 500;

    position.y = height;

    v_gheight = height / 500;

    gl_Position = position;
}