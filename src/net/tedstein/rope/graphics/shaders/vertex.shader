#version 330                                                                                                                                                                        

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 vertexUV;

out vec2 fragUV;

uniform mat4 projection;
uniform mat4 camera;
uniform mat4 model;

void main()
{
   gl_Position = projection * camera * model * vec4(position, 1.0);
   fragUV = vec2(vertexUV.x, 1.0 - vertexUV.y);
  // fragUV = vertexUV;

}
