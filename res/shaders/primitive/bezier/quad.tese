#version 430

layout (quads, fractional_odd_spacing, cw) in;

vec4 quadratic_bezier(vec4 P0, vec4 P1, vec4 P2, float t) {
    /* P0 & P2 points; P1 control point */

    // M(x, y) -> Px * (1 - t) + Py * t
    vec4 M0 = mix(P0, P1, t);
    vec4 M1 = mix(P1, P2, t);

    // [P0 * (1 - t) ^ 2] + [2 * P1 * (1 - t) * t] + [P2 * t ^ 2]
    return mix(M0, M1, t);
}

vec4 cubic_bezier(vec4 P0, vec4 P1, vec4 P2, vec4 P3, float t) {
    /* P0 & P3 points; P1 & P2 control points */

    // M(x, y) -> Px * (1 - t) + Py * t
    vec4 M0 = mix(P0, P1, t);
    vec4 M1 = mix(P1, P2, t);
    vec4 M2 = mix(P2, P3, t);

    // [P0 * (1 - t) ^ 3] + [3 * P1 * (1 - t) ^ 2 * t] + [3 * P2 * (1 - t) * t ^ 2] + [P3 * t ^ 3]
    return quadratic_bezier(M0, M1, M2, t);
}

vec4 patch_evaluation(float u, float v) {
    // y ˄
    // + |
    //   o -- >
    //      + x

    // Domain
    // 12 -- 13 -- 14 -- 15
    // |     |     |    |
    //         ...
    // |     |     |    |
    // 0 --  1 --  2 -- 3

    vec4 M[4];
    for(int g = 0; g < 4; g++) {
        M[g] = cubic_bezier(
            gl_in[g + 0].gl_Position,
            gl_in[g + 4].gl_Position,
            gl_in[g + 8].gl_Position,
            gl_in[g + 12].gl_Position,
            v
        );
    }

    return cubic_bezier(M[0], M[1], M[2], M[3], u);
}

void main(void) {
    gl_Position = patch_evaluation(gl_TessCoord.x, gl_TessCoord.y);
}