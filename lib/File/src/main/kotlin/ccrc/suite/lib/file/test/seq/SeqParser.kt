package ccrc.suite.lib.file.test.seq

import ccrc.suite.commons.logger.Logger
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

object SeqParser : Logger {

    fun parse(file: File): SeqFile {
        val reader = BufferedReader(FileReader(file))
        var line: String? = reader.readLine()
        val body: ArrayList<String> = arrayListOf()
        val seqs = arrayListOf<SequenceWrapper>()
        while (line != null) {
            debug { "Parsing all" }
            var des: String? = null
            if (line.startsWith(">")) {
                des = line
                des = des.removePrefix(">")
                line = reader.readLine()
                while (line != null && !line.startsWith(">")) {
                    debug { "Parsing body" }
                    body += line
                    line = reader.readLine()
                }

            }
            des?.let {
                seqs += SequenceWrapper(des, Sequence(body.joinToString(""))).also { debug { "Parser Sequence [$it]" } }
            }
            body.clear()
        }

        reader.close()
        return SeqFile(seqs)
    }
}