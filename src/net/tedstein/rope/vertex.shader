#version 330                                                                                                                                                                        

layout (location = 0) in vec3 position;

out vec4 outColor;
uniform mat4 gWorld;

void main()
{
   gl_Position = gWorld * vec4(position, 1.0);
   outColor = vec4(clamp(position, 0.0, 1.0), 1.0);

}