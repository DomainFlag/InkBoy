#version 330

layout(location = 0) out vec4 outColor;

uniform sampler2D u_texture[10];
uniform vec3 u_light;

in vec3 v_texture;
in vec3 v_normals;

void main() {
    int texUnit = int(v_texture.z);
    if(texUnit >= 0 && texUnit <= 8) {
        outColor = texture2D(u_texture[texUnit + 1], v_texture.xy);
    } else {
        outColor = vec4(0, 0, 0, 1.0);
    }

    outColor = outColor * dot(v_normals, u_light);
}

