#version 430

// Triangles is the domain the PG will work on.
// Segments of equal lengths
// Counter-clockwise order
layout(triangles, fractional_odd_spacing, cw) in;

uniform mat4 u_projection;
uniform mat4 u_camera;
uniform mat4 u_model;

uniform float u_scale;

uniform sampler2D u_texture;

in vec3 v_tecolor[];
out vec3 v_gcolor;
out float v_gheight;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;

    vec4 position = u * gl_in[0].gl_Position +
        v * gl_in[1].gl_Position +
        w * gl_in[2].gl_Position;

    position.y = texture(u_texture, position.xz).r * 300;
    position.xz *= u_scale;

    v_gcolor = v_tecolor[0];
    v_gheight = position.y / 300;

    gl_Position = u_projection * u_camera * u_model * position;
}