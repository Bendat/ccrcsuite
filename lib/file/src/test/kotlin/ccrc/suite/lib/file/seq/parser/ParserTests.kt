package ccrc.suite.lib.file.seq.parser

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import ccrc.suite.commons.BadChar
import ccrc.suite.commons.EmptyFile
import ccrc.suite.commons.Error
import ccrc.suite.commons.Error.SequenceError.IOError
import ccrc.suite.commons.extensions.remove
import ccrc.suite.commons.extensions.type
import ccrc.suite.commons.utils.uuid
import ccrc.suite.lib.file.seq.SeqFile
import ccrc.suite.lib.file.seq.SeqParser
import ccrc.suite.lib.file.seq.Sequence
import ccrc.suite.lib.file.seq.SequenceChain
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

object ParserTests : Spek({
    group("Functions") {
        group("Get Description Indices") {
            group("Single Sequence fastas") {
                describe("Determine description index of short fasta") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC"
                    )
                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }
                    it("Should verify the size is 1") {
                        index.size.should.equal(1)
                    }
                    it("Should verify the index is '0'") {
                        index.first().should.equal(0)
                    }
                }
            }
            group("Multi Sequence fastas") {
                describe("Determine description index of longer fasta") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC",
                        ">description 2",
                        "AAABBB"
                    )

                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }

                    it("Should verify the index is '0'") {
                        index.first().should.equal(0)
                    }

                    it("Should verify the second index is '2'") {
                        index[1].should.equal(2)
                    }
                }
                describe("Determine description index of longer fasta with a missing body") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC",
                        ">description 2"
                    )

                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }

                    it("Should verify the index is '0'") {
                        index.first().should.equal(0)
                    }

                    it("Should verify the second index is '2'") {
                        index[1].should.equal(2)
                    }
                }
            }
        }
        group("Map Character Validity") {
            describe("Should validate with no bad characters") {
                val sequent = "AAGGCC"
                lateinit var badchars: Option<BadChar>
                it("Should parse the sequence") {
                    badchars = SeqParser.mapCharValidity(sequent)
                }

                it("Should verify bad chars is $None") {
                    badchars.should.equal(None)
                }
            }

            describe("Should validate with bad characters") {
                val sequent = "]AAG,GC{C-G*l"
                val expected = hashSetOf('l', ',', '{', ']', 'l')
                lateinit var badcharsOpt: Option<BadChar>
                lateinit var badChars: BadChar

                it("Should parse the sequence") {
                    badcharsOpt = SeqParser.mapCharValidity(sequent)
                }

                it("Should verify bad chars is $None") {
                    badcharsOpt.should.be.of.type<Some<BadChar>>()
                }
                it("Should extract the BadCharError") {
                    val right = badcharsOpt as Some
                    badChars = right.t
                }

                expected.forEach {
                    it("Should verify [$it] is in the returned list of bad characters") {
                        badChars.badChars.contains(it).should.be.`true`
                    }
                }

            }


        }
        group("Determine Sequence Type") {
            describe("An Invalid Sequence") {
                val sequence = arrayListOf("GGC[")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Invalid Sequence"
                    )
                }

                it("Should verify the result is type InvalidSequence") {
                    sequenceResult.body.should.be.of.type<SequenceChain.InvalidSequenceChain>()
                }
            }
            describe("A valid Sequence") {
                val sequence = arrayListOf("GGCG")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Valid Sequence"
                    )
                }

                it("Should verify the result is type ValidSequence") {
                    sequenceResult.body.should.be.of.type<SequenceChain.ValidSequenceChain>()
                }
            }
            describe("An Empty Sequence") {
                val sequence = arrayListOf("")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Empty Sequence"
                    )
                }

                it("Should verify the result is type EmptySequence") {
                    sequenceResult.body.should.be.of.type<SequenceChain.EmptySequenceChain>()
                }
            }
        }

        group("Between") {
            describe("between") {
                val fasta = listOf(
                    ">1",
                    "A",
                    ">2",
                    "B"
                )
                it("Tests between") {
                    val res = fasta.joinToString("\n").split(">")
                    print(res)
                }
            }
        }
        group("Map Descriptions to bodies") {
            describe("Single fasta") {
                val fasta = ">title 1\nAAGGCC"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta).toList()
                    print(sequences)
                }

                it("Should verify the size of the result") {
                    sequences.size.should.equal(1)
                }

                it("Should verify the sequence type") {
                    sequences[0].body.should.be.of.type<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.should.equal(">title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain.should.equal("AAGGCC")
                }
            }
            describe("Longer Single fasta") {
                val fasta = ">title 1\nAAGGCC\nBCGA"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta).toList()
                    print(sequences)
                }

                it("Should verify the size of the result") {
                    sequences.size.should.equal(1)
                }

                it("Should verify the sequence type") {
                    sequences[0].body.should.be.of.type<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.should.equal(">title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain.should.equal("AAGGCCBCGA")
                }
            }

            describe("Double fasta") {
                val fasta = ">title 1\nAAGGCC\n>2\nBBGGCC"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size.should.equal(2)
                }

                it("Should verify the sequence type") {
                    sequences[0].body.should.be.of.type<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain.should.equal("AAGGCC")
                }

                it("Should verify the sequence description") {
                    sequences[1].description.should.equal(">2")
                }

                it("Should verify the sequence body") {
                    sequences[1].body.chain.should.equal("BBGGCC")
                }
            }

            describe("Longer Double fasta") {
                val fasta = ">title 1\nAAGGCC\nGGGEEE\n>2\nBBGGCCG\nGGHHH"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta).toList()
                    print(sequences)
                }

                it("Should verify the size of the result") {
                    sequences.size.should.equal(2)
                }

                it("Should verify the sequence type") {
                    sequences[0].body.should.be.of.type<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.should.equal(">title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain.should.equal("AAGGCCGGGEEE")
                }

                it("Should verify the sequence description") {
                    sequences[1].description.should.equal(">2")
                }

                it("Should verify the sequence body") {
                    sequences[1].body.chain.should.equal("BBGGCCGGGHHH")
                }
            }

            describe("Empty fasta") {
                val fasta = ""
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size.should.equal(0)
                }
            }
        }
    }

    group("Single Sequence Fasta File Tests") {
        group("Bad IO tests") {
            describe("Empty file") {
                val file = empty
                lateinit var parsed: Either<IOError, SeqFile>

                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed.should.be.of.type<Either.Left<IOError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a.should.be.of.type<IOError.NoStartingDescriptionError>()
                }
            }

            describe("Null file") {
                val file = nullStart
                lateinit var parsed: Either<IOError, SeqFile>

                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed.should.be.of.type<Either.Left<IOError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a.should.be.of.type<EmptyFile>()
                }
            }

            describe("Sequence with no starting description") {
                val file = missingDescriptionpPart
                lateinit var parsed: Either<IOError, SeqFile>

                it("Should to parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed.should.be.of.type<Either.Left<IOError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a.should.be.of.type<IOError.NoStartingDescriptionError>()
                }
            }

            describe("Bad file") {
                val file = File("/nowhere$uuid")
                lateinit var parse: Either<IOError, SeqFile>
                it("Should parse the imaginary file [$file]") {
                    parse = SeqParser.parse(file)
                }

                it("Should verify that parse result is type [${Either.Left::class.qualifiedName}]") {
                    parse.should.be.of.type<Either.Left<IOError>>()
                }

                it("Should verify the IOError type") {
                    val right = parse
                    right as Either.Left
                    right.a.should.be.of.type<IOError.FileReadError>()
                }

            }
        }
        group("Valid parse with syntax errors") {
            describe("Description with no sequence body") {
                val file = missingSequencePart
                lateinit var parsed: Either<IOError, SeqFile>
                lateinit var seqFile: SeqFile
                lateinit var first: Sequence
                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed.should.be.of.type<Either.Right<SeqFile>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val right = parsed
                    right as Either.Right
                    seqFile = right.b
                }

                it("Should get the sequence") {
                    first = seqFile.first()
                }

                it("Should verify the sequence is of type InvalidSequenceChain") {
                    first.body.should.be.of.type<SequenceChain.EmptySequenceChain>()
                }

                it("Should have the correct body") {
                    first.body.chain.should.equal("==This Chain Has No Body==")
                }

            }

            describe("Bad characters in sequence") {
                val file = badCharacters
                val expectedBadChar = setOf('0', '%', '{')
                lateinit var parsed: Either<IOError, SeqFile>
                lateinit var seqFile: SeqFile
                lateinit var first: Sequence
                lateinit var badCharError: Error.SequenceError.ParseError
                it("Should to parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should that parse result is type [${Either.Left::class.qualifiedName}]") {
                    parsed.should.be.of.type<Either.Right<SeqFile>>()
                }

                it("Should extract the SeqFile}") {
                    val right = parsed
                    right as Either.Right
                    seqFile = right.b
                }

                it("Should get the first sequence") {
                    first = seqFile.first()
                }
                it("Should verify the sequence is of type InvalidSequenceChain") {
                    first.body.should.be.of.type<SequenceChain.InvalidSequenceChain>()
                }

                it("Should retrieve the error") {
                    val chain = first.body
                    chain as SequenceChain.InvalidSequenceChain
                    badCharError = chain.error
                }

                it("Should verify the error is of type BadChar") {
                    badCharError.should.be.of.type<BadChar>()
                }

                it("Should compare the char sets") {
                    val set = badCharError
                    set as BadChar
                    expectedBadChar.should.equal(set.badChars)
                }
            }
        }
        describe("Valid parse test") {
            val file = validSequence
            lateinit var parsed: Either<IOError, SeqFile>
            lateinit var seqFile: SeqFile
            lateinit var sequence: SequenceChain.ValidSequenceChain
            it("Should to parse the file [$file]") {
                println(file)
                parsed = SeqParser.parse(file)
            }

            it("Should verify parsing resulted in IOError") {
                parsed.should.be.of.type<Either.Right<SeqFile>>()
            }

            it("Should extract the SeqFile") {
                val right = parsed
                right as Either.Right
                seqFile = right.b
            }

            it("Should verify the seqFile has 1 element") {
                seqFile.size.should.equal(1)
            }

            it("Should verify it contains a ValidSequenceChain") {
                seqFile.first().body.should.be.of.type<SequenceChain.ValidSequenceChain>()
            }

            it("Should extract the sequence") {
                sequence = seqFile
                    .first()
                    .body as SequenceChain.ValidSequenceChain
            }

            it("Should verify the chain body") {
                sequence.chain.should.equal(validBody.remove("\n", ",", " "))
            }
        }
    }

    group("Parser test") {
        describe("Valid Multi Sequence Fasta") {
            val file = ">HSBGPG Human gene for bone gla protein (BGP)\nGGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT\n>HSGLTH1 Human theta 1-globin gene\nCCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA"
            lateinit var parsed: Either<IOError, SeqFile>
            lateinit var seqFile: SeqFile
            it("Should to parse the file [$file]") {
                parsed = SeqParser.parse(file)
            }

            it("Should verify parsing resulted in IOError") {
                parsed.should.be.of.type<Either.Right<SeqFile>>()
            }

            it("Should extract the SeqFile") {
                val right = parsed
                right as Either.Right
                seqFile = right.b
            }

            it("Should verify the seqFile has 1 element") {
                seqFile.size.should.equal(2)
            }

            it("Should verify the first description") {
                seqFile.first().description.should.equal(">HSBGPG Human gene for bone gla protein (BGP)")
            }
            it("Should verify the first body") {
                println(seqFile.first().body.chain)
                seqFile.first().body.chain.should.equal("GGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT")
            }

            it("Should verify the first description") {
                seqFile[1].description.should.equal(">HSGLTH1 Human theta 1-globin gene")
            }

            it("Should verify the first description") {
                seqFile[1].body.chain.should.equal("CCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA")
            }
        }
        describe("Invalid Multi Sequence Fasta") {
            val file = ">HSBGPG Human gene for bone gla protein (BGP)\nGGCAGA'T\n>HSGLTH1 Human theta 1-globin gene\nCCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA"
            lateinit var parsed: Either<IOError, SeqFile>
            lateinit var seqFile: SeqFile
            it("Should to parse the file [$file]") {
                parsed = SeqParser.parse(file)
            }

            it("Should verify parsing resulted in IOError") {
                parsed.should.be.of.type<Either.Right<SeqFile>>()
            }

            it("Should extract the SeqFile") {
                val right = parsed
                right as Either.Right
                seqFile = right.b
            }

            it("Should verify the seqFile has 1 element") {
                seqFile.size.should.equal(2)
            }

            it("Should verify the first validity"){
                seqFile.first().body.should.be.of.type<SequenceChain.InvalidSequenceChain>()
            }

            it("Should verify the first description") {
                seqFile.first().description.should.equal(">HSBGPG Human gene for bone gla protein (BGP)")
            }
            it("Should verify the first body") {
                println(seqFile.first().body.chain)
                seqFile.first().body.chain.should.equal("GGCAGA'T")
            }

            it("Should verify the first description") {
                seqFile[1].description.should.equal(">HSGLTH1 Human theta 1-globin gene")
            }

            it("Should verify the first description") {
                seqFile[1].body.chain.should.equal("CCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA")
            }
        }
        describe("Empty Second Body Fasta") {
            val file = ">HSBGPG Human gene for bone gla protein (BGP)\n\n>HSGLTH1 Human theta 1-globin gene\nCCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA"
            lateinit var parsed: Either<IOError, SeqFile>
            lateinit var seqFile: SeqFile
            it("Should to parse the file [$file]") {
                parsed = SeqParser.parse(file)
            }

            it("Should verify parsing resulted in IOError") {
                parsed.should.be.of.type<Either.Right<SeqFile>>()
            }

            it("Should extract the SeqFile") {
                val right = parsed
                right as Either.Right
                seqFile = right.b
            }

            it("Should verify the seqFile has 1 element") {
                seqFile.size.should.equal(2)
            }

            it("Should verify the first validity"){
                seqFile[1].body.should.be.of.type<SequenceChain.EmptySequenceChain>()
            }

            it("Should verify the first description") {
                seqFile.first().description.should.equal(">HSBGPG Human gene for bone gla protein (BGP)")
            }

            it("Should verify the first description") {
                seqFile[1].description.should.equal(">HSGLTH1 Human theta 1-globin gene")
            }
        }
    }
})