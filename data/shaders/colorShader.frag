#version 410 core

in vec2 _texCoord;
in vec3 _normal;

out vec3 out_diffuse;

//Uniforms
uniform sampler2D tColorMap;

void main()
{
	vec4 tColor = texture2D(tColorMap, _texCoord);
	vec3 lightDirection = vec3(0.6f, -0.6, 0.6f); //<- Is normalized
	float diffuse = clamp(dot(lightDirection, normalize(_normal)), 0.1, 1.0);
	vec4 outColor = vec4((tColor.xyz * diffuse) + (tColor.xyz * vec3(0.4f, 0.4f, 0.4f)), 1.0);
	
	out_diffuse = outColor.xyz;
}
