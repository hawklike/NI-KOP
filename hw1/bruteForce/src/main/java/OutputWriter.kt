import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class OutputWriter(private val base: String, private val filename: String) {
    fun appendToEnd(what: String) {
        Files.createDirectories(Paths.get(base))
        FileOutputStream(File("$base/$filename"), true).bufferedWriter().use { writer ->
            writer.appendln(what)
        }
    }
}