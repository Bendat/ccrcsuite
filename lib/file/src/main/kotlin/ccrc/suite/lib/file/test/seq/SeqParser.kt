package ccrc.suite.lib.file.test.seq

import arrow.core.*
import arrow.data.Ior
import arrow.data.Valid
import ccrc.suite.commons.*
import ccrc.suite.commons.Error.SequenceError.IOError
import ccrc.suite.commons.extensions.isFalse
import ccrc.suite.commons.logger.Logger
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object SeqParser : Logger {

    fun parse(file: File): SeqFile {
        val reader = BufferedReader(FileReader(file))
        var line: String? = reader.readLine()
        val body: ArrayList<String> = arrayListOf()
        val seqs = arrayListOf<Sequence_old>()
        var first = true
        while (line != null && first.isFalse) {
            first = false
            debug { "Parsing all [$line]" }
            var des: String? = null
            if (line.startsWith(">")) {
                des = line
                des = des.removePrefix(">")
                line = reader.readLine()
                while (line != null && line.startsWith(">").isFalse) {
                    debug { "Parsing body" }
                    body += line
                    line = reader.readLine()
                }

            }
            des?.let {
                seqs += Sequence_old(
                    des,
                    SequenceChain_old(body.joinToString(""))
                ).also { debug { "Parser SequenceChain [$it]" } }
            }
            body.clear()
        }

        reader.close()
        return SeqFile(seqs)
    }

//    fun safeParse(file: File): Validated<Error.SequenceError, SeqFile> {
//        val read = Try { BufferedReader(FileReader(file)) }
////        when (read) {
////            is Failure -> SeqReadError("Reading file [$file] failed with error [${read.exception.message}]")
////            is Success ->
////        }
//
//    }

    private fun readAndParselines(
        filename: String,
        reader: BufferedReader
    ): Either<IOError, SeqFile> {
        val errors = arrayListOf<Error.SequenceError>()
        reader.use { reader ->
            val line = reader.readLine()
            val fileVerify = verifyFile(line, filename)

            if (fileVerify is Some)
                return Left(fileVerify.t)
            val lines = reader.readLines()

            val titles = lines.filter { it.startsWith(">") }
            val bodies = lines.filter { it.startsWith(">").isFalse }
            val sequenceList = arrayListOf<Sequence>()
            titles.forEachIndexed { index, title ->
                sequenceList += determineSequenceType(bodies, index, title)
            }
            return Right(SeqFile(sequenceList))
        }
    }

    private fun determineSequenceType(
        bodies: List<String>,
        index: Int,
        title: String
    ): Sequence {
        return if (bodies.size >= index) {
            val chars = mapCharValidity(bodies[index])
            if (chars is Some)
                Sequence(title, SequenceChain.InvalidSequenceChain(bodies[index], chars.t))
            else
                Sequence(title, SequenceChain.ValidSequenceChain(bodies[index]))
        } else
            Sequence(title, SequenceChain.EmptySequenceChain())
    }


    private fun verifyFile(line: String?, filename: String): Option<IOError> {
        return when {
            line == null -> Some(EmptyFile("File [$filename] has no contents"))
            line.startsWith(">").isFalse -> Some(NoStartingHeader("File [$filename] does not start with '>' : [$line]"))
            else -> None
        }
    }

    private fun mapCharValidity(value: String): Option<BadChar> {
        val badChars = value.toSet().minus(AminoAcids.values().map { it.code }.toSet())
        return when (badChars.any()) {
            true -> Some(BadChar("The sequence contains invalid characters", value, badChars))
            false -> None
        }
    }

}