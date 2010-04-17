package geoscript.workspace

import org.geotools.data.memory.MemoryDataStore

/**
 * A Memory Workspace stores it's Layers in memory
 * @author Jared Erickson
 */
class Memory extends Workspace {

    /**
     * Create a new Memory Workspace
     */
    Memory() {
        super(new MemoryDataStore())
    }

}