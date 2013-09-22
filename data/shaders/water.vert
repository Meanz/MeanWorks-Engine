#version 410

uniform mat4 mProjectionView;
uniform mat4 mModelMatrix;

//Input data
layout(location = 0) in vec3 position;

void main()
{
	gl_Position = (mProjectionView * mModelMatrix) * (vec4(position, 1.0));
}