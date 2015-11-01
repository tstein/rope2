#version 330                                                                                                                                                                        

layout (location = 0) in vec3 Position;
layout (location = 1) in vec3 Color;

uniform float gScale;
out vec4 color;

void main()
{
    color = vec4(Color, 1.0);
    gl_Position = vec4(gScale * Position.x, gScale * Position.y, Position.z, 1.0);
}