#version 330                                                                                                                                                                        


in vec2 fragUV;
uniform sampler2D tex;


out vec4 fragColor;

void main()                                                                        
{                                                                                  
 fragColor = texture(tex, fragUV);
}