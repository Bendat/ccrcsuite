package ccrc.suite.lib.file.test.seq

import arrow.data.Validated
import ccrc.suite.commons.Error.SequenceError.ParseError

typealias  Validated = Validated.Valid<True>

data class SeqFile(val sequences: List<Sequence>) {
    val size get() = sequences.size
    operator fun get(index: Int): Sequence{
        return sequences[index]
    }
}

data class Sequence(val description: String, val body: SequenceChain)



sealed class SequenceChain {
    abstract val chain: String

    data class ValidSequenceChain(override val chain: String) : SequenceChain()
    data class InvalidSequenceChain(
        override val chain: String,
        val errors: ParseError
    ) : SequenceChain()

    class EmptySequenceChain() : SequenceChain() {
        override val chain: String = "==This Chain Has No Body=="
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
