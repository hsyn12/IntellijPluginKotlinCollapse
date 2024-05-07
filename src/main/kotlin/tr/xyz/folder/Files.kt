package tr.xyz.folder

import java.io.File

object Files {

    const val DEFAULT_FOLDER = "collapses"

    fun getOrCreate(name: String): File? {
        val file = File(DEFAULT_FOLDER, "$name.txt")
        if (!file.exists())
            if (file.createNewFile()) {
                return file
            }
        return null
    }

    fun save(file: String, elementType: String, name: String) {
        val content = getOrCreate(file)
        if (content == null) {
            println("file not found: $file")
            return
        }
        content.writeText(name)
    }

}