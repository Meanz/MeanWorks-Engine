#version 410

//TODO: Calculating MVP on CPU or sending all three mats? Hmm
uniform mat4 mProjectionView;
uniform mat4 mModelMatrix;

//Input data
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

out vec2 _texCoord;
out vec3 _normal;

void main()
{
	_texCoord = texCoord;
	_normal = normalize((vec4(normal, 1.0))).xyz;

	gl_Position = (mProjectionView * mModelMatrix) * (vec4(position, 1.0));
}