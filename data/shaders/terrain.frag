#version 410 core
#extension GL_EXT_texture_array : enable


in vec3 _texCoord;
in vec3 _normal;
in vec4 _tileTransitions;

out vec3 out_diffuse;

//Uniforms
uniform sampler2DArray tColorMap;

void main()
{
	vec4 tColor = texture2DArray(tColorMap, _texCoord);
	
	float mixVal = 0.4;
	float enableTT = 1.0;
	
	if(enableTT == 1.0) {
		//Up
		if(_tileTransitions.x != 0.0) {
			if(_texCoord.y < 0.2) {
				mixVal = 0.8 - _texCoord.y / 0.2;
				tColor = mix(tColor, texture2DArray(tColorMap, vec3(_texCoord.xy, _tileTransitions.x - 1.0)), mixVal);
			}
		}
		//Right
		if(_tileTransitions.y != 0.0) {
			if(_texCoord.x > 0.8) {
				mixVal = _texCoord.x / 4.0;
				tColor = mix(tColor, texture2DArray(tColorMap, vec3(_texCoord.xy, _tileTransitions.y - 1.0)), mixVal);
			}
		}	
		//Bottom
		if(_tileTransitions.z != 0.0) {
			if(_texCoord.y > 0.8) {
				mixVal = _texCoord.y / 4.0;
				tColor = mix(tColor, texture2DArray(tColorMap, vec3(_texCoord.xy, _tileTransitions.z - 1.0)), mixVal);
			}
		}		
		//Left
		if(_tileTransitions.w != 0.0) {
			if(_texCoord.x < 0.2) {
				mixVal = 0.8 - _texCoord.x / 0.2;
				tColor = mix(tColor, texture2DArray(tColorMap, vec3(_texCoord.xy, _tileTransitions.w - 1.0)), mixVal);
			}
		}			
	}
	
	vec3 lightDirection = vec3(0.6f, -0.6, 0.6f); //<- Is normalized
	float diffuse = clamp(dot(lightDirection, normalize(_normal)), 0.1, 1.0);
	vec4 outColor = vec4((tColor.xyz * diffuse) + (tColor.xyz * vec3(0.4f, 0.4f, 0.4f)), 1.0);
	
	out_diffuse = outColor.xyz;
}
