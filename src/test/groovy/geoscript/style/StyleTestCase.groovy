package geoscript.style

import geoscript.feature.Field
import geoscript.filter.Filter
import geoscript.layer.Shapefile
import org.geotools.styling.Style as GtStyle
import java.awt.Color
import org.junit.Test
import static org.junit.Assert.*


/**
 * The Style UnitTest
 * @author Jared Erickson
 */
class StyleTestCase {

    @Test void styleFromSldFile() {
        File sldFile = new File(getClass().getClassLoader().getResource("states.sld").toURI())
        Style style = new Style(sldFile)
        assertEquals(1, style.rules.size())
        assertEquals(1, style.rules[0].symbolizers.size())
        def sym = style.rules[0].symbolizers[0]
        assertEquals("#E6E6E6", sym.fillColor)
        assertEquals(0.5, sym.fillOpacity, 0.1)
        assertEquals("#4C4C4C", sym.strokeColor)
        assertEquals("butt", sym.strokeLineCap)
        assertEquals("miter", sym.strokeLineJoin)
        assertEquals(1.0, sym.strokeOpacity, 0.1)
        assertEquals(0.0, sym.strokeWidth, 0.1)
        assertEquals(0.0, sym.strokeDashOffset, 0.1)
    }

    @Test void styleFromSymbolizer() {
        Style style = new Style(new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeOpacity: 0
        ))
        assertEquals(1, style.rules.size())
        assertEquals(1, style.rules[0].symbolizers.size())
    }

    @Test void styleFromSymbolizers() {
        Style style = new Style([
            new PointSymbolizer(
                shape: "circle",
                fillColor: "#FF0000",
                size: 6,
                strokeOpacity: 0
            ),
            new TextSymbolizer(
                label: "name",
                color: "#000000"
            )
        ])
        assertEquals(1, style.rules.size())
        assertEquals(2, style.rules[0].symbolizers.size())
    }

    @Test void styleFromRule() {
        Style style = new Style(new Rule(
            symbolizers: [
                new PointSymbolizer(
                    shape: "circle",
                    size: 8,
                    fillColor: "#0033CC",
                    strokeOpacity: 0
                )
            ],
            filter: new Filter("pop < 5000")
        ))
        assertEquals(1, style.rules.size())
        assertEquals(1, style.rules[0].symbolizers.size())
        assertEquals("pop < 5000", style.rules[0].filter.cql)
    }

    @Test void styleFromRules() {
        Style style = new Style([
            new Rule(
                symbolizers: [
                    new PointSymbolizer(
                        shape: "circle",
                        fillColor: "#FF0000",
                        size: 6,
                        strokeOpacity: 0
                    )
                ]
            ),
            new Rule(
                symbolizers: [
                    new TextSymbolizer(
                        label: "name",
                        color: "#000000"
                    )
                ]
            )
        ])
        assertEquals(2, style.rules.size())
        assertEquals(1, style.rules[0].symbolizers.size())
        assertTrue(style.rules[0].symbolizers[0] instanceof PointSymbolizer)
        assertEquals(1, style.rules[1].symbolizers.size())
        assertTrue(style.rules[1].symbolizers[0] instanceof TextSymbolizer)
    }

    @Test void styleWithZindices() {
        Style style = new Style([
            new LineSymbolizer(
                strokeColor: "#333333",
                strokeWidth: 5,
                strokeLineCap: "round",
                zIndex: 0
            ),
            new LineSymbolizer(
                strokeColor: "#6699FF",
                strokeWidth: 3,
                strokeLineCap: "round",
                zIndex: 1
            )
        ])
        assertEquals(1, style.rules.size())
        assertEquals(2, style.rules[0].symbolizers.size())
        GtStyle gtStyle = style.gtStyle
        assertNotNull(gtStyle)
    }

    @Test void getDefaultStyleForGeometryType() {
        def style = Style.getDefaultStyleForGeometryType("Point")
        assertTrue(style.rules[0].symbolizers[0] instanceof PointSymbolizer)
        style = Style.getDefaultStyleForGeometryType("lineString")
        assertTrue(style.rules[0].symbolizers[0] instanceof LineSymbolizer)
        style = Style.getDefaultStyleForGeometryType("POLYGON")
        assertTrue(style.rules[0].symbolizers[0] instanceof PolygonSymbolizer)
    }

    @Test void getRandomColor() {
        def c = Style.getRandomColor()
        assertNotNull(c)
    }

    @Test void convertColorToHex() {
        assertEquals("#000000", Style.convertColorToHex(Color.black))
        assertEquals("#ffffff", Style.convertColorToHex(Color.white))
        assertEquals("#ff0000", Style.convertColorToHex(Color.red))
        assertEquals("#00ff00", Style.convertColorToHex(Color.green))
        assertEquals("#0000ff", Style.convertColorToHex(Color.blue))
    }

    @Test void getHexColor() {
        assertEquals("#f0ffff", Style.getHexColor("#f0ffff"))
        assertEquals("#f0ffff", Style.getHexColor("240,255,255"))
        assertEquals("#f0ffff", Style.getHexColor("azure"))
    }

    @Test void getColor() {

        // Expected Color
        Color expected = new Color(240,255,255)

        // By CSS Name
        Color actual = Style.getColor("azure")
        assertColorsEqual(expected, actual)

        // By RGB
        actual = Style.getColor("240,255,255")
        assertColorsEqual(expected, actual)

        // By Hex
        actual = Style.getColor("#f0ffff")
        assertColorsEqual(expected, actual)

        // Color name not found
        assertNull(Style.getColor("ASDASDA"))
    }

    private void assertColorsEqual(Color expected, Color actual) {
        assertNotNull(expected)
        assertNotNull(actual)
        assertEquals(expected.red, actual.red)
        assertEquals(expected.green, actual.green)
        assertEquals(expected.blue, actual.blue)
    }

    @Test void getPaletteNames() {

        // All
        List names = Style.getPaletteNames()
        assertTrue(names.size() > 0)

        // Qualitative
        names = Style.getPaletteNames("qualitative")
        assertTrue(names.size() > 0)

        // Sequential
        names = Style.getPaletteNames("sequential")
        assertTrue(names.size() > 0)

        // Diverging
        names = Style.getPaletteNames("diverging")
        assertTrue(names.size() > 0)
    }

    @Test void getPaletteColors() {

        // 5 Greens
        List colors = Style.getPaletteColors("Greens", 5)
        assertTrue(colors.size() == 5)

        // 11 RdYlGn
        colors = Style.getPaletteColors("RdYlGn", 999)
        assertTrue(colors.size() == 11)

        // Empty
        colors = Style.getPaletteColors("NOT A REAL PALETTE", 5)
        assertTrue(colors.isEmpty())

    }

    @Test void createUniqueValuesStyle() {

        // Get states shapefile
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)

        // Default is random colors
        Style style1 = Style.createUniqueValuesStyle(shapefile, "STATE_ABBR")
        assertNotNull(style1)
        assertEquals(49, style1.rules.size())

        // Color palette
        Style style2 = Style.createUniqueValuesStyle(shapefile, "STATE_ABBR", "Greens")
        assertNotNull(style2)
        assertEquals(49, style2.rules.size())

        // Color list
        Style style3 = Style.createUniqueValuesStyle(shapefile, "STATE_ABBR", ["teal","slateblue","tan","wheat","salmon"])
        assertNotNull(style3)
        assertEquals(49, style3.rules.size())

        // Color Closure
        Style style4 = Style.createUniqueValuesStyle(shapefile, "STATE_ABBR", {i,v -> Style.getRandomColor()})
        assertNotNull(style4)
        assertEquals(49, style4.rules.size())
    }

    @Test void createGraduatedStyle() {

        // Get states shapefile
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)

        Style style1 = Style.createGraduatedStyle(shapefile, "WORKERS", "Quantile", 5, "Greens")
        assertNotNull(style1)
        assertEquals(5, style1.rules.size())

        Style style2 = Style.createGraduatedStyle(shapefile, "WORKERS", "EqualInterval", 7, ["#eee8aa","#98fb98","#afeeee","#d87093","#ffefd5","#ffdab9","#cd853f"])
        assertNotNull(style2)
        assertEquals(7, style2.rules.size())
    }

}

