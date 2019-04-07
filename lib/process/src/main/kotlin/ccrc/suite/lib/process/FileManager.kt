@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ccrc.suite.lib.process

import arrow.core.Either
import arrow.core.Try
import ccrc.suite.commons.PerlProcess
import ccrc.suite.commons.User
import java.io.File

class FileManager(path: String) {
    val folder = File(path)
    val processFolder = File(path, "processes")
    private val userFolders = HashMap<User, File>()

    init {
        processFolder.mkdirs()
    }

    fun subFolderFor(user: User, process: PerlProcess): Either<FileError, File> {
        return Try {
            if (userFolders.contains(user)) {
                val file = userFolders[user]!!
                createUserSubdirectory(user, file, process)
                userFolders[user] = File(file, "${process.name}-${process.id}")
                userFolders[user]?.mkdirs()
            } else {
                val file = File(processFolder, user.name.value)
                createUserSubdirectory(user, file, process)
            }
            userFolders[user]!!
        }.toEither(onLeft = { FileError.CreateFolderError(it.message) })
    }

    private fun createUserSubdirectory(
        user: User,
        file: File,
        process: PerlProcess
    ) {
        userFolders[user] = File(file, "${process.name}-${process.id}")
        userFolders[user]?.mkdirs()
    }
}

sealed class FileError {
    abstract val message: Any?

    data class CreateFolderError(override val message: Any?) : FileError()
}