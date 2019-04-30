#version 430

// Triangles is the domain the PG will work on.
// Segments of equal lengths
// Counter-clockwise order
layout(triangles, fractional_odd_spacing, cw) in;

in vec2 normal_map_coord_te[];

out vec2 normal_map_coord_gs;

uniform float u_scale;
uniform float u_height;

uniform sampler2D u_texture;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;

    vec4 position = u * gl_in[0].gl_Position +
        v * gl_in[1].gl_Position +
        w * gl_in[2].gl_Position;

    normal_map_coord_gs = u * normal_map_coord_te[0] +
        v * normal_map_coord_te[1] +
        w * normal_map_coord_te[2];

    position.y = texture(u_texture, normal_map_coord_gs).r * u_height;

    gl_Position = position;
}