@file:Suppress("SpellCheckingInspection")

package ccrc.suite.lib.file.seq.parser

val badCharacters = ">Description and sequence with disallowed charactersE\n" +
        "C000CACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGG%%CGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAG{{{CTGCGGCGCGGCGCCCCCTGGAGGCCGCGGGACCCCTG\n"
val empty = ""
val nullStart: String? = null
val missingSequencePart = ">Description with no body"
val missingDescriptionpPart = "CAACACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGGCGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAGCTGCGGCGCGGCGCCCCCTGGAGGCCGCGGGACCCCTG\n"

val validHead = ">Hello Fasta"
val validBody = "GGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT\n" +
        "GGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT\n"
val validSequence
    get() = "$validHead\n$validBody"
val valid2PartHeader1 = ">HSBGPG Human gene for bone gla protein (BGP)"
val valid2PartHeader2 = ">HSGLTH1 Human theta 1-globin gene"

val valid2PartSequence get() = "$valid2PartHeader1\n$validBody\n$valid2PartHeader2"
val valid2sequence = ">HSBGPG Human gene for bone gla protein (BGP)\n" +
        "GGCAGATTCCCCCTAGACCCGCCCGCACCATGGTCAGGCATGCCCCTCCTCATCGCTGGGCACAGCCCAGAGGGT\n" +
        ">HSGLTH1 Human theta 1-globin gene\n" +
        "CCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n"
val invalid2sequence = ">HSGLTH1 Human theta 1-globin gene\n" +
        "CCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGGCGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAGCTGCGGCGCGGCGCCCCCTGGAGGCCGCGGGACCCCTG\n" +
        ">BAD SEQUENCE\n" +
        "C0CACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGGCGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAGCTGCGGCGCGGCGCCC"
val emptySecondBody = ">HSGLTH1 Human theta 1-globin gene\n" +
        "CCACTGCACTCACCGCACCCGGCCAATTTTTGTGTTTTTAGTAGAGACTAAATACCATATAGTGAACACCTAAGA\n" +
        "CGGGGGGCCTTGGATCCAGGGCGATTCAGAGGGCCCCGGTCGGAGCTGTCGGAGATTGAGCGCGCGCGGTCCCGG\n" +
        "GATCTCCGACGAGGCCCTGGACCCCCGGGCGGCGAAGCTGCGGCGCGGCGCCCCCTGGAGGCCGCGGGACCCCTG\n" +
        ">Empty Second Body\n"


