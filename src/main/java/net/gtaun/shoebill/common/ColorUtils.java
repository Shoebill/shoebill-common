package net.gtaun.shoebill.common;

import net.gtaun.shoebill.data.Color;

public class ColorUtils
{
	public static Color colorBlend(Color mainColor, Color destColor, int alpha)
	{
		if (alpha < 0) alpha = 0;
		else if (alpha > 255) alpha = 255;
		
		int r = mainColor.getR();
		int g = mainColor.getG();
		int b = mainColor.getB();
		int a = mainColor.getA();

		r = r + (destColor.getR() - r) * alpha / 255;
		g = g + (destColor.getG() - g) * alpha / 255;
		b = b + (destColor.getB() - b) * alpha / 255;
		a = a + (destColor.getA() - a) * alpha / 255;
		
		return new Color(r, g, b, a);
	}
	
	private ColorUtils()
	{

	}
}
