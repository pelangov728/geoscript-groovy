package geoscript.layer

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * The ArcGrid Unit Test
 * @author Jared Erickson
 */
class ArcGridTestCase {

    @Test void readFromFile() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file)
        assertNotNull(raster)
    }

    @Test void readFromGrassFile() {
        File file = new File(getClass().getClassLoader().getResource("grass.arx").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file)
        assertNotNull(raster)
    }

    @Test void readFromString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file.text)
        assertNotNull(raster)
    }

    @Test void writeToFile() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file)
        assertNotNull(raster)
        File destFile = File.createTempFile("raster",".asc")
        arcGrid.write(raster, destFile)
        String str = destFile.text
        assertEquals("""NCOLS 4
NROWS 6
XLLCORNER 0.0
YLLCORNER 0.0
CELLSIZE 50.0
NODATA_VALUE -9999.0
-9999.0 -9999.0 5.0 2.0
-9999.0 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 -9999.0
""", str)

    }

    @Test void writeToString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file)
        assertNotNull(raster)
        String str = arcGrid.writeToString(raster)
        assertNotNull(str)
        assertEquals("""NCOLS 4
NROWS 6
XLLCORNER 0.0
YLLCORNER 0.0
CELLSIZE 50.0
NODATA_VALUE -9999.0
-9999.0 -9999.0 5.0 2.0
-9999.0 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 -9999.0
""", str)
    }

    @Test void writeToGrassString() {
        File file = new File(getClass().getClassLoader().getResource("raster.asc").toURI())
        assertNotNull(file)
        ArcGrid arcGrid = new ArcGrid()
        Raster raster = arcGrid.read(file)
        assertNotNull(raster)
        String str = arcGrid.writeToString(raster, "grass")
        assertNotNull(str)
        assertEquals("""NORTH: 300.0
SOUTH: 0.0
EAST: 200.0
WEST: 0.0
ROWS: 6
COLS: 4
-9999.0 -9999.0 5.0 2.0
-9999.0 20.0 100.0 36.0
3.0 8.0 35.0 10.0
32.0 42.0 50.0 6.0
88.0 75.0 27.0 9.0
13.0 5.0 1.0 -9999.0
""", str)
    }
}
