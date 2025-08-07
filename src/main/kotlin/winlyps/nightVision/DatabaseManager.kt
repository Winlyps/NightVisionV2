package winlyps.nightVision

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID

class DatabaseManager(private val dataFolder: File) {

    private var connection: Connection? = null

    init {
        try {
            connect()
            createTable()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize the database.", e)
        }
    }

    private fun connect() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        val dbFile = File(dataFolder, "players.db")
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.absolutePath)
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun createTable() {
        val sql = "CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY NOT NULL);"
        try {
            connection?.createStatement()?.execute(sql)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun isNightVisionUser(uuid: UUID): Boolean {
        val sql = "SELECT uuid FROM players WHERE uuid = ?;"
        try {
            connection?.prepareStatement(sql)?.use { pstmt ->
                pstmt.setString(1, uuid.toString())
                pstmt.executeQuery().use { rs ->
                    return rs.next()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    fun addNightVisionUser(uuid: UUID) {
        val sql = "INSERT INTO players(uuid) VALUES(?);"
        try {
            connection?.prepareStatement(sql)?.use { pstmt ->
                pstmt.setString(1, uuid.toString())
                pstmt.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun removeNightVisionUser(uuid: UUID) {
        val sql = "DELETE FROM players WHERE uuid = ?;"
        try {
            connection?.prepareStatement(sql)?.use { pstmt ->
                pstmt.setString(1, uuid.toString())
                pstmt.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getNightVisionUsers(): Set<UUID> {
        val users = mutableSetOf<UUID>()
        val sql = "SELECT uuid FROM players;"
        try {
            connection?.createStatement()?.use { stmt ->
                stmt.executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        users.add(UUID.fromString(rs.getString("uuid")))
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return users
    }

    fun closeConnection() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}