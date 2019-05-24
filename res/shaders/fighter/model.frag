#version 330

layout(location = 0) out vec4 outColor;

uniform sampler2D u_textures[10];

in vec3 v_texture;
in vec3 v_normals;
in vec3 v_position;

uniform mat4 u_model;
uniform mat4 u_camera;

// max model materials
const int MAX_MATERIALS = 16;

// max lighting
const int MAX_LIGHTS = 6;

// light attenuation, gradual extinction of photons in medium to respect of distance
struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

// point lighting, light coming from every direction starting from its origin
struct PointLight {
    Attenuation attenuation;
    vec3 colour;
    vec4 position;
    float intensity;
};

// directional lighting, light coming from a direction with constant intensity
struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

// spot lighting, light coming from its origin in conic form
struct SpotLight {
    PointLight pointLight;
    vec3 direction;
    float angle;
};

// color lighting and normal to texture
struct Color {
    vec3 Kx;
    int Mx;
};

// ambient/diffuse/specular colors
struct Material {
    Color Kx[3];
    vec3 Tf;

    float Ni;
    float Ns;
};

uniform vec3 u_ambient_light;
uniform float u_specular_power;

uniform Material u_materials[MAX_MATERIALS];

/* Point Light */
uniform PointLight u_point_light[MAX_LIGHTS];

/* Directional Light */
uniform DirectionalLight u_directional_light[MAX_LIGHTS];

/* Spot Light */
uniform SpotLight u_spot_light[MAX_LIGHTS];

vec4 ambientColor, diffuseColor, specularColor;

vec4 getColor(Material material, int colorIndex) {
    Color color = material.Kx[colorIndex];

    if(color.Mx != -1) {
        return texture2D(u_textures[color.Mx], v_texture.xy);
    }

    return vec4(color.Kx, 1.0);
}

void setupColors(Material material) {
    ambientColor = getColor(material, 0);
    diffuseColor = getColor(material, 1);
    specularColor = getColor(material, 2);
}

vec4 setupLightColor(Material material, vec3 to_light_direction, vec3 light_colour, float light_intensity) {

    /* Diffuse Light */
    if(diffuseColor.a == 0.0)
        discard;

    // retain factor when both normal and the direction from position to light source are in the same direction -90 <-> 90
    float diffuseFactor = max(dot(v_normals, to_light_direction), 0.0);

    // diffuse light
    vec4 diffuse_color = diffuseColor * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;


    /* Specular Light */
    vec3 to_camera_direction = normalize(- v_position);
    vec3 to_position_direction = - to_light_direction;

    // direction of reflected light from the surface
    vec3 reflected_light = normalize(reflect(to_position_direction, v_normals));

    // retain factor when both direction of reflected light and surface to camera are in the same direction
    float specularFactor = max(dot(to_camera_direction, reflected_light), 0.0);

    // control the intensity of light as the derivative from 0 to 1 is an increasing positive function
    specularFactor = pow(specularFactor, u_specular_power);

    // specular light
    vec4 specular_color = specularColor * specularFactor * light_intensity * material.Ns * vec4(light_colour, 1.0);

    return (diffuse_color + specular_color);
}

vec4 setPointLight(PointLight point_light, Material material) {
    // similar to no light
    if(point_light.intensity == 0)
        return vec4(0, 0, 0, 0);

    /* point light position */
    vec3 point_light_position = (u_camera * vec4(point_light.position.xyz - v_position, 1)).xyz + v_position;

    /* Diffuse Light */
    vec3 to_light_source = point_light_position - v_position;
    vec3 to_light_direction = normalize(to_light_source);

    /* calculate light color */
    vec4 light_color = setupLightColor(material, to_light_direction, point_light.colour, point_light.intensity);

    /* Attenuation coefficient */
    float distance = length(to_light_source);
    float attenuationInv = point_light.attenuation.constant + point_light.attenuation.linear * distance +
        point_light.attenuation.exponent * pow(distance, 2.0);

    return light_color / attenuationInv;
}

vec4 setDirectionalLight(DirectionalLight directional_light, Material material) {
    // similar to no light
    if(directional_light.intensity == 0)
        return vec4(0, 0, 0, 0);

    /* Directional light direction */
    vec3 directional_light_direction = (u_camera * vec4(directional_light.direction.xyz, 0)).xyz;

    return setupLightColor(material, normalize(directional_light_direction), directional_light.colour, directional_light.intensity);
}

vec4 setSpotLight(SpotLight spot_light, Material material) {
    /* Point light */
    PointLight point_light = spot_light.pointLight;

    // similar to no light
    if(point_light.intensity == 0)
        return vec4(0, 0, 0, 0);

    // point light position
    vec3 point_light_position = (u_camera * vec4(point_light.position.xyz - v_position, 1)).xyz + v_position;

    /* Diffuse Light */
    vec3 to_light_source = point_light_position - v_position;
    vec3 to_light_direction = normalize(to_light_source);

    // spot light direction
    vec3 spot_light_direction = normalize((u_camera * vec4(spot_light.direction, 0)).xyz);

    // spot light angle
    float light_angle = dot(-to_light_direction, spot_light_direction);

    vec4 light_color = vec4(0, 0, 0, 0);

    if(light_angle > spot_light.angle) {
        // calculate light color
        light_color = setPointLight(point_light, material);

        light_color *= (1.0 - (1.0 - light_angle) / (1.0 - spot_light.angle));
    }

    return light_color;
}

void main() {
    Material material = u_materials[int(v_texture.z)];

    setupColors(material);

    vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);

    for(int g = 0; g < MAX_LIGHTS; g++) {
        /* Point Light */
        diffuseSpecularComp += setPointLight(u_point_light[g], material);

        //* Directional Light */
        diffuseSpecularComp += setDirectionalLight(u_directional_light[g], material);

        /* Spot Light */
        diffuseSpecularComp += setSpotLight(u_spot_light[g], material);
    }

    outColor = ambientColor * vec4(u_ambient_light, 1) + diffuseSpecularComp;
}