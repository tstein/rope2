#version 330                                                                                                                                                                        

layout (location = 0) in vec3 Position;
layout (location = 1) in vec3 Color;

uniform mat4 gWorld;
out vec4 color;

void main()
{
   gl_Position = gWorld * vec4(Position, 1.0);
   color = vec4(Color, 1.0);

}