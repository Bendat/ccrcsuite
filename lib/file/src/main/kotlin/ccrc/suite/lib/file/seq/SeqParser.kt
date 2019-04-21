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
import java.io.File

/**
 * Singleton for parsing fasta files and fasta strings
 */
object SeqParser : Logger {

    fun parse(file: File): Either<IOError, SeqFile> = SeqParser.parse(file.readLines())

    fun parse(input: List<String>?): Either<IOError, SeqFile> {
        return if (input == null || input.isEmpty()) Left(EmptyFile("The input data was null"))
        else {
            val verify = verifyFirstLine(input.getOrNull(0), "${input.firstOrNull()}")
            if (verify is Some) Left(verify.t)
            else Right(mapDescriptionToBodies(input.joinToString("\n")))
        }
    }

    internal fun getDescriptionIndices(input: List<String>): List<Int> {
        return input.mapIndexed { index, item ->
            if (item.startsWith(">")) index
            else null
        }.filterNotNull()
    }

    internal fun mapDescriptionToBodies(fasta: String): List<Sequence> {
        val seqs = fasta.split(">.*".toRegex())
        val descriptions = fasta.split("\n").filter { it.startsWith(">") }
        val bodies = seqs.filter { !it.isBlank() }.map { it.replace("\n", "") }
        return descriptions.mapIndexed { index, description ->
            determineSequenceType(bodies.getOrNull(index), description)
        }
    }

    fun parse(input: String?) = parse(input?.lines())

    internal fun flatten(bodies: List<String>) =
        bodies.joinToString().remove("\n", ",", " ")

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
        return when (badChars?.any()) {
            true -> Some(BadChar("The sequence contains invalid characters", value, badChars))
            false, null -> None
        }
    }

}