package com.bblauncher.data.iconpack

import android.content.ComponentName
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

/**
 * Parses appfilter.xml files into a map of ComponentName → drawable name.
 * Handles the standard icon pack format:
 *   <item component="ComponentInfo{pkg/activity}" drawable="name" />
 */
object AppFilterParser {

    /** Extracts package and activity from ComponentInfo{pkg/activity} format. */
    private val componentRegex = Regex("""ComponentInfo\{([^/]+)/([^}]+)\}""")

    /**
     * Parses an appfilter.xml from an [InputStream].
     * Used for assets/ based appfilter files.
     */
    fun parse(inputStream: InputStream): Map<ComponentName, String> {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(inputStream, "UTF-8")
        return parse(parser)
    }

    /**
     * Parses an appfilter.xml from an [XmlPullParser].
     * Used for both InputStream-backed and compiled res/xml/ parsers.
     */
    fun parse(parser: XmlPullParser): Map<ComponentName, String> {
        val mappings = mutableMapOf<ComponentName, String>()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                val componentStr = parser.getAttributeValue(null, "component")
                val drawableName = parser.getAttributeValue(null, "drawable")

                if (componentStr != null && !drawableName.isNullOrBlank()) {
                    val match = componentRegex.find(componentStr)
                    if (match != null) {
                        val (pkg, activity) = match.destructured
                        mappings[ComponentName(pkg, activity)] = drawableName
                    }
                }
            }
            eventType = parser.next()
        }

        return mappings
    }
}
