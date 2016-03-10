#version 330


in vec2 fragUV;
uniform sampler2D tex;


out vec4 fragColor;

void main()
{
 //fragColor = texture(tex, fragUV);
 fragColor = vec4(1.0, 1.0, 0.0, 1.0f);
}