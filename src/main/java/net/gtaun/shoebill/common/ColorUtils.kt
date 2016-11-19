package net.gtaun.shoebill.common

import net.gtaun.shoebill.data.Color

object ColorUtils {

    @JvmStatic
    fun colorBlend(mainColor: Color, destColor: Color, alphaValue: Int): Color {
        var alpha = alphaValue
        if (alpha < 0) alpha = 0
        else if (alpha > 255) alpha = 255

        var r = mainColor.r
        var g = mainColor.g
        var b = mainColor.b
        var a = mainColor.a

        r += (destColor.r - r) * alpha / 255
        g += (destColor.g - g) * alpha / 255
        b += (destColor.b - b) * alpha / 255
        a += (destColor.a - a) * alpha / 255

        return Color(r, g, b, a)
    }

    @JvmStatic
    fun hsbColor(hue: Float, saturation: Float, brightness: Float, alpha: Int): Color =
            Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness) shl 8 or (alpha and 0xFF))
}
