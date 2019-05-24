#version 430

// Quads is the domain the PG will work on.
// Segments of equal lengths
// Counter-clockwise order
layout(quads, fractional_odd_spacing, cw) in;

in vec2 normal_map_coord_te[];
out vec2 normal_map_coord_gs;

uniform float u_scale;
uniform float u_height;

uniform sampler2D u_texture;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

    vec4 position = (1 - u) * (1 - v) * gl_in[0].gl_Position
    + u * (1 - v) * gl_in[1].gl_Position
    + (1 - u) * v * gl_in[2].gl_Position
    + u * v * gl_in[3].gl_Position;

    normal_map_coord_gs = (1 - u) * (1 - v) * normal_map_coord_te[0]
    + u * (1 - v) * normal_map_coord_te[1]
    + (1 - u) * v * normal_map_coord_te[2]
    + u * v * normal_map_coord_te[3];

    position.y = texture(u_texture, normal_map_coord_gs).r * u_height;

    gl_Position = position;
}