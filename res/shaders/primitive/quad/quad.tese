#version 430 core

// Quads this is the domain the PG will work on.
// Quads edges will be subdivided into segments with equal lengths
// PG will emit triangles in counter-clockwise order
layout(quads, fractional_odd_spacing, cw) in;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

    vec4 position = (1 - u) * (1 - v) * gl_in[0].gl_Position
        + u * (1 - v) * gl_in[1].gl_Position
        + (1 - u) * v * gl_in[2].gl_Position
        + u * v * gl_in[3].gl_Position;

    gl_Position = position;
}