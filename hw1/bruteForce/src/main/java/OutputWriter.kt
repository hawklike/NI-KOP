import java.io.File
import java.io.FileOutputStream

class OutputWriter(private val where: String) {
    fun appendToEnd(what: String) {
        FileOutputStream(File(where), true).bufferedWriter().use { writer ->
            writer.appendln(what)
        }
    }
}