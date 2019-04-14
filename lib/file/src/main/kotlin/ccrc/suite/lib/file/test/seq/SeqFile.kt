package ccrc.suite.lib.file.test.seq

import arrow.data.Invalid
import arrow.data.Validated
import ccrc.suite.commons.BadChar
import ccrc.suite.commons.Error

typealias  Validated = Validated.Valid<True>

data class SeqFile(val sequences: List<SequenceWrapper>) {
    val size get() = sequences.size
    operator fun get(index: Int): SequenceWrapper {
        return sequences[index]
    }
}

data class SequenceWrapper(val description: String, val body: Sequence)

inline class Sequence(val value: String) {
    val size get() = value.length
    val isValid get() = mapValidity()

    private fun mapValidity(): Validated<Error.SequenceError, True> {
        val badChars = value.toSet().minus(AminoAcids.values().map { it.code }.toSet())
        return when (badChars.any()) {
            true -> Invalid(BadChar("The sequence contains invalid characters [$badChars]"))
            false -> Validated.Valid(True)
        }
    }
}

object True
enum class AminoAcids(val code: Char, val abbreviation: String, val aminoacid: String) {
    A('A', "ALA", "alanine"),
    B('B', "ASX", "asparagine"),
    C('C', "CYS", "cystine"),
    D('D', "ASP", "aspartate"),
    E('E', "GLU", "glutamate  "),
    F('F', "PHE", "phenylalanine"),
    G('G', "GLY", "glycine"),
    H('H', "HIS", "histidine"),
    I('I', "ILE", "isoleucine"),
    K('K', "LYS", "lysine"),
    L('L', "LEU", "leucine"),
    M('M', "MET", "methionine"),
    N('N', "ASN", "asparagine "),
    P('P', "PRO", "proline"),
    Q('Q', "GLN", "glutamine"),
    R('R', "ARG", "arginine"),
    S('S', "SER", "serine"),
    T('T', "THR", "threonine"),
    V('V', "VAl", "valine"),
    W('W', "TRP", "tryptophan"),
    U('U', "", "selenocysteine"),
    Y('Y', "TYR", "tyrosine"),
    Z('Z', "GLX", "glutamine"),
    X('X', "", "Any"),
    Stop('*', "", "Translation stop"),
    Gap('-', "", "Gap of unknown length")
}
