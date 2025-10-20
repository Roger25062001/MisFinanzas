package com.misfinanzas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, "MisFinanzas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE gastos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                monto REAL NOT NULL,
                categoria TEXT NOT NULL,
                fecha TEXT NOT NULL,
                descripcion TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS gastos")
        onCreate(db)
    }

    fun insertarGasto(monto: Double, categoria: String, fecha: String, descripcion: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("monto", monto)
            put("categoria", categoria)
            put("fecha", fecha)
            put("descripcion", descripcion)
        }
        db.insert("gastos", null, values)
        db.close()
    }

    fun obtenerTotalMes(categoria: String, fecha: String): Double {
        val db = readableDatabase
        val partes = fecha.split("/")
        val mes = partes[1].padStart(2, '0')
        val anio = partes[2]

        val query = """
            SELECT SUM(monto) as total 
            FROM gastos 
            WHERE categoria = ? 
              AND strftime('%m', fecha) = ? 
              AND strftime('%Y', fecha) = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(categoria, mes, anio))
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        db.close()
        return total
    }
}

    fun obtenerTodosLosGastos(): List<Gasto> {
        val lista = mutableListOf<Gasto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM gastos ORDER BY fecha DESC", null)

        while (cursor.moveToNext()) {
            val gasto = Gasto(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            )
            lista.add(gasto)
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarGasto(id: Int) {
        val db = writableDatabase
        db.delete("gastos", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun obtenerTotalGeneral(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(monto) as total FROM gastos", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        db.close()
        return total
    }
