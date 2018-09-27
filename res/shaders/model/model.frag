#version 150

out vec4 outColor;

uniform sampler2D u_texture[8];

varying vec3 v_texture;
varying vec3 v_normals;
varying vec3 v_lighting_pos;

void main() {
    int texUnit = int(v_texture.z);
    if(texUnit == 0)
        outColor = texture2D(u_texture[0], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 1)
        outColor = texture2D(u_texture[1], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 2)
        outColor = texture2D(u_texture[2], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 3)
        outColor = texture2D(u_texture[3], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 4)
        outColor = texture2D(u_texture[4], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 5)
        outColor = texture2D(u_texture[5], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 6)
        outColor = texture2D(u_texture[6], vec2(1, 1)-v_texture.xy);
    else if(texUnit == 7)
        outColor = texture2D(u_texture[7], vec2(1, 1)-v_texture.xy);
    else  outColor = texture2D(u_texture[4], vec2(1, 1)-v_texture.xy);

    outColor = outColor * dot(v_normals, v_lighting_pos);
}

