#version 330                                                                                                                                                                        


in vec2 fragUV;
uniform sampler2D tex;
uniform float red;

out vec4 fragColor;

void main()                                                                        
{                                                                                  
 fragColor = texture(tex, fragUV) + vec4(red, 0.0f, 0.0f, 0.0f);
}