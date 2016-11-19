package net.gtaun.shoebill.common

/**
 * Created by marvin on 17.07.15 in project shoebill-common.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
object StringUtils {

    private val original = intArrayOf(192, 193, 194, 196, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 210, 211,
            212, 214, 217, 218, 219, 220, 223, 224, 225, 226, 228, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
            242, 243, 244, 246, 249, 250, 251, 252, 209, 241, 191, 161, 176)

    private val fixed = intArrayOf(128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
            145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165,
            166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 94, 124)

    /**
     * Converts umlaute to GTA SA charset (e.g. À Á Â Ä Æ Ç È É Ê Ë Ì Í Î Ï Ò Ó Ô Ö Ù Ú Û Ü ß à á â ä æ ç è é ê ë ì í
     * î ï ò ó ô ö ù ú û ü Ñ ñ ¿ ¡ °)
     * Credits to mooman from sa-mp.com
     * @param input The string to convert
     * *
     * @return The converted string
     */
    @JvmStatic
    fun convertStringForTextdraw(input: String): String {
        val stringBuilder = StringBuilder()
        input.forEach {
            val index = original.indexOf(it.toInt())
            if (index != -1)
                stringBuilder.append(fixed[index].toChar())
            else
                stringBuilder.append(it)
        }
        return stringBuilder.toString()
    }
}
