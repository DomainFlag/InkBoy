#version 330

layout(location = 0) out vec4 outColor;

in vec2 normal_map_coord_fs;

uniform mat4 u_camera;
uniform sampler2D u_normal_map;

// max lighting
const int MAX_LIGHTS = 6;

// directional lighting, light coming from a direction with constant intensity
struct DirectionalLight {
	vec3 colour;
	vec3 direction;
	float intensity;
};

// ambient dominant colour light
uniform vec3 u_ambient_light;

/* Directional Light */
uniform DirectionalLight u_directional_light[MAX_LIGHTS];

vec4 setupLightColor(vec3 to_light_direction, vec3 normal, vec3 light_colour, float light_intensity) {
	// retain factor when both normal and the direction from position to light source are in the same direction -90 <-> 90
	float diffuseFactor = max(dot(normal, to_light_direction), 0.01);

	// diffuse light
	vec4 diffuse_color = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

	return diffuse_color;
}

vec4 setDirectionalLight(DirectionalLight directional_light, vec3 normal) {
	// similar to no light
	if(directional_light.intensity == 0)
		return vec4(0, 0, 0, 0);

	/* Directional light direction */
	vec3 directional_light_direction = vec4(directional_light.direction.xyz, 0).xyz;

	return setupLightColor(-normalize(directional_light_direction), normal, directional_light.colour, directional_light.intensity);
}

void main() {
	vec3 normal = normalize(texture(u_normal_map, normal_map_coord_fs).rgb * 2.0 - 1.0);

	vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);

	for(int g = 0; g < MAX_LIGHTS; g++) {
		//* Directional Light */
		diffuseSpecularComp += setDirectionalLight(u_directional_light[g], normal);
	}

	outColor = vec4(u_ambient_light, 1) + diffuseSpecularComp;
}