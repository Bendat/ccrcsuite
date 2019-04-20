@file:Suppress("NAME_SHADOWING")

package ccrc.suite.lib.file.seq

import arrow.core.*
import ccrc.suite.commons.BadChar
import ccrc.suite.commons.EmptyFile
import ccrc.suite.commons.Error.SequenceError.IOError
import ccrc.suite.commons.Error.SequenceError.ParseError.InvalidCharError
import ccrc.suite.commons.NoStartingHeader
import ccrc.suite.commons.extensions.isFalse
import ccrc.suite.commons.extensions.remove
import ccrc.suite.commons.logger.Logger
import ccrc.suite.lib.file.seq.SequenceChain.InvalidSequenceChain
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object SeqParser : Logger {
    fun parse(file: File): Either<IOError, SeqFile> {
        return Try {
            BufferedReader(FileReader(file))
        }.toEither { exception ->
            IOError.FileReadError(file, exception.message)
        }.flatMap { reader ->
            reader.use { reader ->
                val line = reader.readLine()
                val fileVerify = verifyFirstLine(line, file.absolutePath)
                if (fileVerify is Some)
                    Left(fileVerify.t)
                else parse(reader.readLines())

            }
        }
    }

    fun parse(input: List<String>?): Either<IOError, SeqFile> {
        return if (input == null || input.isEmpty()) Left(EmptyFile("The input data was null"))
        else {
            val verify = verifyFirstLine(input.getOrNull(0), "${input.firstOrNull()}")
            if (verify is Some) Left(verify.t)
            else mapDescriptionToBody(
                indices = getDescriptionIndices(input),
                input = input,
                titles = input.filter { it.startsWith(">") }.map { it.replaceFirst(">", "") })
                .right()
        }.also { debug { it } }
    }


    internal fun getDescriptionIndices(input: List<String>): List<Int> {
        return input.mapIndexed { index, item ->
            if (item.startsWith(">"))
                index
            else null
        }.filterNotNull()
    }

    internal fun x(fasta: String): List<Sequence> {
        val seqs = fasta.split(">.*".toRegex())
        val descriptions = fasta.split("\n").filter { it.startsWith(">") }
        val bodies = seqs.filter { !it.isBlank() }.map { it.replace("\n", "") }
        return descriptions.mapIndexed { index, description ->
            determineSequenceType(bodies[index], description)
        }
    }

    private fun mapDescriptionToBody(
        indices: List<Int>,
        input: List<String>,
        titles: List<String>
    ): List<Sequence> {
        return indices.mapIndexed { index, _ ->
            when {
                indices.size - index > 1 -> input.subList(index, indices[index + 1])
                indices.size == 1 -> input
                    .subList(index, input.size - 1)
                    .filter { it.startsWith(">").isFalse }
                else -> listOf()
            }
        }.mapIndexed { index, item ->
            determineSequenceType(flatten(item), titles[index])
        }
    }

    fun parse(input: String?) = parse(input?.lines())

    internal fun flatten(bodies: List<String>) = bodies.joinToString().remove("\n", ",", " ")

    internal fun determineSequenceType(
        body: String?,
        title: String
    ): Sequence {
        val chars = mapCharValidity(body)
        return Sequence(
            title,
            when {
                body.isNullOrBlank() -> SequenceChain.EmptySequenceChain()
                chars is Some -> InvalidSequenceChain(
                    chars.t.sequence ?: "No title provided",
                    InvalidCharError("Invalid chars for [$title]", body, chars.t.badChars)
                )
                else -> SequenceChain.ValidSequenceChain(body)
            }
        )
//        val string = bodies.joinToString().remove("\n", ",", " ")
//        return if (bodies.size > index) {
//            debug { "Bodies [$bodies]" }
//            val chars = mapCharValidity(bodies.subList(index, bodies.size).joinToString())
//            if (chars is Some)
//                Sequence(title, SequenceChain.InvalidSequenceChain(string, chars.t))
//            else Sequence(
//                title,
//                SequenceChain.ValidSequenceChain(string)
//            )
//        } else Sequence(title, SequenceChain.EmptySequenceChain())

    }


    private fun verifyFirstLine(line: String?, filename: String): Option<IOError> {
        return when {
            line == null -> Some(EmptyFile("File [$filename] has no contents"))
            line.startsWith(">").isFalse -> Some(NoStartingHeader("File [$filename] does not start with '>' : [$line]"))
            else -> None
        }
    }

    internal fun mapCharValidity(value: String?): Option<BadChar> {
        val badChars = value
            ?.trim()
            ?.toSet()
            ?.minus(AminoAcids.values().map { it.code }.toSet())
        info { value }
        info { AminoAcids.values().map { it.code }.toSet() }
        return when (badChars?.any()) {
            true -> Some(BadChar("The sequence contains invalid characters", value, badChars))
            false, null -> None
        }.also { info { "Bad chars [$badChars]" } }
    }

}