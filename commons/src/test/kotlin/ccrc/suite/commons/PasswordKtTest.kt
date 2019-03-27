package ccrc.suite.commons

import arrow.data.Invalid
import arrow.data.Valid
import ccrc.suite.commons.logger.KLog
import com.winterbe.expekt.should
import org.spekframework.spek2.Spek


class PasswordKtTest : Spek({
    val log = KLog(this)
    group("Password Validation Tests") {
        group("Invalid Test") {
            test("Too short") {
                val password = "hi12".validPassword()
                password.isValid.should.be.`false`
                password as Invalid
                val err = password.e
                err as ShortPassword
            }

            test("Missing Uppercase Characters") {
                val password = "qwertylo;".validPassword()
                password.isValid.should.be.`false`
                password as Invalid
                val err = password.e
                err as MissingUpper
            }

            test("Missing Special Characters") {
                val password = "Qwertylol".validPassword()
                password.isValid.should.be.`false`
                password as Invalid
                val err = password.e
                err as MissingCharset
            }

            test("Missing Lowercase Characters") {
                val password = "QWERTYLO;".validPassword()
                password.isValid.should.be.`false`
                password as Invalid
                val err = password.e
                err as MissingLower
            }
        }

        test("Valid Password"){
            val pass = "ValidPassword1;"
            val password = pass.validPassword()
            password.isValid.should.be.`true`
            password as Valid
            password.a.should.equal(pass)
        }
    }
})