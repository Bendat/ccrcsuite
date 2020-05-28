package ccrc.suite.commons

enum class Parameter(
    val str: String,
    vararg val range: Any?,
    val default: (() -> Any)? = null
) {
    DataDir("-datadir", true, false),
    SeqName("-seqname"),
    RunStyle("-runstyle", "serial", "parallel", "gnuparallel", { "gnuparallel" }),
    PkgDir("-pkgdir"),
    Username("-username"),
    IdCut("-idcut", 0F, 1F, { 0.3.toString() }),
    NTemp("-ntemp", 1, 5, { 20.toString() }),
    NModel("-nmodel", 1, 10, { 5.toString() }),
    Restraint1("-restraint1"),
    Restraint2("-restraint2"),
    Restraint3("-restraint3"),
    Restraint4("-restraint4"),
    HomoFlag("-homoflag", "real", "benchmark", { "real" }),
    LBS("-LBS", true, false, { false }),
    GO("-GO", true, false, (false)),
    EC("-EC", true, false, { true }),
    Traj("-traj"),
    Hours("-hours"),
}