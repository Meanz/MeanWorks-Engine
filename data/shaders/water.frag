#version 410

out vec4 out_diffuse;

void main()
{

	vec3 _normal = vec3(0.0, 1.0, 0.0);
	vec3 tColor = vec3(0.2, 0.4, 0.4);
	vec3 lightDirection = vec3(0.6f, 0.3, 0.6f); //<- Is normalized
	float diffuse = clamp(dot(lightDirection, normalize(_normal)), 0.1, 1.0);
	vec4 outColor = vec4((tColor.xyz * diffuse) + (tColor.xyz * vec3(0.4f, 0.4f, 0.4f)), 0.85);
	out_diffuse = outColor;
}