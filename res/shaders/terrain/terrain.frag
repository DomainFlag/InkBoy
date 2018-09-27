#version 150

out vec4 outColor;

varying float v_depth;
varying float v_far;

uniform vec3 u_gradients[3];

uniform float u_threshold;

//RGB to HSV convert
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;

    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

//HSV to RGB convert
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);

    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

//HSV interpolation
vec3 colorInterpolation() {
    float part;
    // v_depth -> [0, 1.0]
    // for u_threshold = 0.7 we have the following
    if(v_depth < u_threshold) {
        part = v_depth / u_threshold;

        // part is normalized -> [0, 1.0]
        return mix(u_gradients[0], u_gradients[1], part);
    } else {
        // part is in between [0.7, 1.0] thus [0, 0.3] / (1.0f - 0.7f)
        part = (v_depth - u_threshold) / (1.0f - u_threshold);

        // part is normalized -> [0, 1.0]
        return mix(u_gradients[1], u_gradients[2], part);
    }
}

void main() {
    vec3 color = hsv2rgb(colorInterpolation());
    vec4 depth_color = vec4(color.rgb, v_depth); //-> [0, 0.2]

    // As we go further away the opacity starts to slowly fade then increasing exponentially in fade
//    vec4 far_color = vec4(depth_color.rgb, depth_color.a - pow(sqrt(exp(v_far) - 1.0), 2.0) / 100.0); //->(sqrt(e^x)-1)^2

    outColor = vec4(color, 1.0);
}