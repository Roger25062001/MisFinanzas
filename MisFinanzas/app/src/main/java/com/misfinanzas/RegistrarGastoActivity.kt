package com.misfinanzas

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class RegistrarGastoActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etFecha: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button

    private val categorias = listOf(
        Categoria("Alimentación", "🍽", Color.GREEN, 800.0),
        Categoria("Transporte", "🚌", Color.BLUE, 300.0),
        Categoria("Entretenimiento", "🎬", Color.MAGENTA, 200.0),
        Categoria("Vivienda", "🏠", Color.RED, 1500.0),
        Categoria("Salud", "💊", Color.RED, 400.0),
        Categoria("Café/Bebidas", "☕", Color.rgb(139,69,19), 150.0),
        Categoria("Compras", "🛒", Color.rgb(255,165,0), 500.0),
        Categoria("Otros", "📦", Color.GRAY, 300.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_gasto)

        etMonto = findViewById(R.id.etMonto)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        etFecha = findViewById(R.id.etFecha)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias.map { "${it.icono} ${it.nombre}" })
        spinnerCategoria.adapter = adapter

        val calendar = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFecha.setText(formatoFecha.format(calendar.time))

        etFecha.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                val fecha = "$day/${month + 1}/$year"
                etFecha.setText(fecha)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnGuardar.setOnClickListener {
            guardarGasto()
        }
    }

    private fun guardarGasto() {
        val monto = etMonto.text.toString().toDoubleOrNull()
        val categoriaIndex = spinnerCategoria.selectedItemPosition
        val fecha = etFecha.text.toString()
        val descripcion = etDescripcion.text.toString()

        if (monto == null || monto <= 0) {
            mostrarAlerta("El monto debe ser mayor a 0")
            return
        }

        if (categoriaIndex == AdapterView.INVALID_POSITION) {
            mostrarAlerta("Debes seleccionar una categoría")
            return
        }

        val categoria = categorias[categoriaIndex]
        val db = SQLiteHelper(this)
        db.insertarGasto(monto, categoria.nombre, fecha, descripcion)

        val totalMes = db.obtenerTotalMes(categoria.nombre, fecha)
        if (totalMes > categoria.limiteMensual) {
            mostrarSnackbar("⚠ Has excedido el límite de ${categoria.nombre}: $${"%.2f".format(totalMes)} de $${categoria.limiteMensual}", Color.parseColor("#FFA500"))
        } else {
            mostrarSnackbar("✓ Gasto guardado correctamente", Color.parseColor("#4CAF50"))
        }

        limpiarCampos()
        // Navegar a Pantalla 2 (no implementada aún)
    }

    private fun mostrarAlerta(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Validación")
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun mostrarSnackbar(mensaje: String, color: Int) {
        val snackbar = Snackbar.make(btnGuardar, mensaje, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(color)
        snackbar.show()
    }

    private fun limpiarCampos() {
        etMonto.text.clear()
        etDescripcion.text.clear()
        spinnerCategoria.setSelection(0)
        etFecha.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }
}

data class Categoria(val nombre: String, val icono: String, val color: Int, val limiteMensual: Double)
