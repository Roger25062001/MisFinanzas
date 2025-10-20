package com.misfinanzas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ListaGastosActivity : AppCompatActivity() {

    private lateinit var rvGastos: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var db: SQLiteHelper
    private lateinit var adapter: GastoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_gastos)

        rvGastos = findViewById(R.id.rvGastos)
        tvTotal = findViewById(R.id.tvTotal)
        fabAgregar = findViewById(R.id.fabAgregar)
        db = SQLiteHelper(this)

        adapter = GastoAdapter(db.obtenerTodosLosGastos(), ::mostrarOpciones)
        rvGastos.layoutManager = LinearLayoutManager(this)
        rvGastos.adapter = adapter

        actualizarTotal()

        fabAgregar.setOnClickListener {
            startActivity(Intent(this, RegistrarGastoActivity::class.java))
        }
    }

    private fun mostrarOpciones(gasto: Gasto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar gasto")
            .setMessage("¿Eliminar este gasto de $${"%.2f".format(gasto.monto)}?")
            .setPositiveButton("Eliminar") { _, _ ->
                db.eliminarGasto(gasto.id)
                adapter.actualizarLista(db.obtenerTodosLosGastos())
                actualizarTotal()
                Snackbar.make(rvGastos, "🗑 Gasto eliminado", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarTotal() {
        val total = db.obtenerTotalGeneral()
        tvTotal.text = "Total: -$${"%.2f".format(total)}"
    }
}