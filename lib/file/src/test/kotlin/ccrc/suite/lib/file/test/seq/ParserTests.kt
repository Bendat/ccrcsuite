package ccrc.suite.lib.file.test.seq

import ccrc.suite.commons.BadChar
import ccrc.suite.commons.Error
import ccrc.suite.commons.extensions.type
import ccrc.suite.commons.logger.KLog
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.File

object ParserTests : Spek({
    Feature("SeqParser") {
        val log = KLog(ParserTests)

        Scenario("We want to break a fasta into its title and body") {
            lateinit var seqfasta: File
            lateinit var read: SeqFile

            Given("A seq.fasta file with 3 sequences") {
                seqfasta = File(javaClass.getResource("/single.fasta").file)
            }

            When("We parse the file contents") {
                read = SeqParser.parse(seqfasta)
            }

            Then("The size should be 1") {
                read.size.should.equal(1)
            }

            And("The description should be 'Hello Fasta'") {
                read[0].description.should.equal("Hello Fasta")
            }

            And("Its body should be correct") {
                read[0].body.value.should.equal(single.filter { it != '\n' })
            }
        }
        
        Scenario("We want to break ensure validation fails correctly") {
            lateinit var seqfasta: File
            lateinit var read: SeqFile

            Given("A seq.fasta file with 3 sequences") {
                seqfasta = File(javaClass.getResource("/error.fasta").file)
            }

            When("We parse the file contents") {
                read = SeqParser.parse(seqfasta)
            }

            Then("The size should be 1") {
                read.size.should.equal(1)
            }

            And("The description should be 'BAD SEQUENCE'") {
                read[0].description.should.equal("BAD SEQUENCE")
            }

            And("Its body should be correct") {
                read[0].body.value.should.equal(error.filter { it != '\n' })
            }

            And("It should be invalid") {
                read[0].body.isValid.should.be.of.type<arrow.data.Validated.Invalid<BadChar>>()
            }
        }

        Scenario("I want deserialize a 'fasta' file into its sequences") {
            lateinit var seqfasta: File
            lateinit var read: SeqFile

            Given("A seq.fasta file with 3 sequences") {
                seqfasta = File(javaClass.getResource("/seq.fasta").file)
            }

            When("We parse the file contents") {
                read = SeqParser.parse(seqfasta)
            }

            Then("We verify the number of sequences") {
                read.size.should.equal(3)
            }

            And("We verify the descriptions do not start with null") {
                read.sequences.map { it.description.should.not.startWith("null") }
            }

            And("We Verify the bodies are not empty") {
                read.sequences.map { it.body.value.isBlank().should.be.`false` }
            }

            And("We verify the first sequence is valid") {
                read[0].body.isValid.should.be.type<Validated>()
            }

            And("We verify the second sequence is valid") {
                read[1].body.isValid.should.be.type<Validated>()
            }

            And("We verify the final sequence is invalid") {
                read[2].body.isValid.should.be.type<arrow.data.Validated.Invalid<Error.SequenceError>>()
            }
        }
    }
})

val single = "GGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT\n" +
        "ATAAACAGTGCTGGAGGCTGGCGGGGCAGGCCAGCTGAGTCCTGAGCAGCAGCCCAGCGCAGCCACCGAGACACC\n" +
        "ATGAGAGCCCTCACACTCCTCGCCCTATTGGCCCTGGCCGCACTTTGCATCGCTGGCCAGGCAGGTGAGTGCCCC\n" +
        "CACCTCCCCTCAGGCCGCATTGCAGTGGGGGCTGAGAGGAGGAAGCACCATGGCCCACCTCTTCTCACCCCTTTG\n" +
        "GCTGGCAGTCCCTTTGCAGTCTAACCACCTTGTTGCAGGCTCAATCCATTTGCCCCAGCTCTGCCCTTGCAGAGG\n" +
        "GAGAGGAGGGAAGAGCAAGCTGCCCGAGACGCAGGGGAAGGAGGATGAGGGCCCTGGGGATGAGCTGGGGTGAAC\n" +
        "CAGGCTCCCTTTCCTTTGCAGGTGCGAAGCCCAGCGGTGCAGAGTCCAGCAAAGGTGCAGGTATGAGGATGGACC\n" +
        "TGATGGGTTCCTGGACCCTCCCCTCTCACCCTGGTCCCTCAGTCTCATTCCCCCACTCCTGCCACCTCCTGTCTG\n" +
        "GCCATCAGGAAGGCCAGCCTGCTCCCCACCTGATCCTCCCAAACCCAGAGCCACCTGATGCCTGCCCCTCTGCTC\n" +
        "CACAGCCTTTGTGTCCAAGCAGGAGGGCAGCGAGGTAGTGAAGAGACCCAGGCGCTACCTGTATCAATGGCTGGG\n" +
        "GTGAGAGAAAAGGCAGAGCTGGGCCAAGGCCCTGCCTCTCCGGGATGGTCTGTGGGGGAGCTGCAGCAGGGAGTG\n" +
        "GCCTCTCTGGGTTGTGGTGGGGGTACAGGCAGCCTGCCCTGGTGGGCACCCTGGAGCCCCATGTGTAGGGAGAGG\n" +
        "AGGGATGGGCATTTTGCACGGGGGCTGATGCCACCACGTCGGGTGTCTCAGAGCCCCAGTCCCCTACCCGGATCC\n" +
        "CCTGGAGCCCAGGAGGGAGGTGTGTGAGCTCAATCCGGACTGTGACGAGTTGGCTGACCACATCGGCTTTCAGGA\n" +
        "GGCCTATCGGCGCTTCTACGGCCCGGTCTAGGGTGTCGCTCTGCTGGCCTGGCCGGCAACCCCAGTTCTGCTCCT\n" +
        "CTCCAGGCACCCTTCTTTCCTCTTCCCCTTGCCCTTGCCCTGACCTCCCAGCCCTATGGATGTGGGGTCCCCATC\n" +
        "ATCCCAGCTGCTCCCAAATAAACTCCAGAAG"
val error = "C000CACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGGCGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAGCTGCGGCGCGGCGCCCCCTGGAGGCCGCGGGACCCCTG\n" +
        "GCCGGTCCGCGCAGGCGCAGCGGGGTCGCAGGGCGCGGCGGGTTCCAGCGCGGGGATGGCGCTGTCCGCGGAGGA\n" +
        "CCGGGCGCTGGTGCGCGCCCTGTGGAAGAAGCTGGGCAGCAACGTCGGCGTCTACACGACAGAGGCCCTGGAAAG\n" +
        "GTGCGGCAGGCTGGGCGCCCCCGCCCCCAGGGGCCCTCCCTCCCCAAGCCCCCCGGACGCGCCTCACCCACGTTC\n" +
        "CTCTCGCAGGACCTTCCTGGCTTTCCCCGCCACGAAGACCTACTTCTCCCACCTGGACCTGAGCCCCGGCTCCTC\n" +
        "ACAAGTCAGAGCCCACGGCCAGAAGGTGGCGGACGCGCTGAGCCTCGCCGTGGAGCGCCTGGACGACCTACCCCA\n" +
        "CGCGCTGTCCGCGCTGAGCCACCTGCACGCGTGCCAGCTGCGAGTGGACCCGGCCAGCTTCCAGGTGAGCGGCTG\n" +
        "CCGTGCTGGGCCCCTGTCCCCGGGAGGGCCCCGGCGGGGTGGGTGCGGGGGGCGTGCGGGGCGGGTGCAGGCGAG\n" +
        "TGAGCCTTGAGCGCTCGCCGCAGCTCCTGGGCCACTGCCTGCTGGTAACCCTCGCCCGGCACTACCCCGGAGACT\n" +
        "TCAGCCCCGCGCTGCAGGCGTCGCTGGACAAGTTCCTGAGCCACGTTATCTCGGCGCTGGTTTCCGAGTACCGCT\n" +
        "GAACTGTGGGTGGGTGGCCGCGGGATCCCCAGGCGACCTTCCCCGTGTTTGAGTAAAGCCTCTCCCAGGAGCAGC\n" +
        "CTTCTTGCCGTGCTCTCTCGAGGTCAGGACGCGAGAGGAAGGCGC"