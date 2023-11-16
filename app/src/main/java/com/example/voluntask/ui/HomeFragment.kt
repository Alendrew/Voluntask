package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voluntask.R
import com.example.voluntask.adapters.EventoAdapter
import com.example.voluntask.databinding.FragmentHomeBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputEditText
import java.sql.Date
import java.time.Instant
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.searchView.isIconified = false;
        binding.searchView.isIconifiedByDefault = false
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val userInfo = arguments?.getParcelable<Usuario>("usuario")
        viewModel.userInfo = userInfo
        val adapter = EventoAdapter {
//            openLink(it.link)
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.getAllEventos { eventos ->
            adapter.setEventoList(eventos)
        }

        var selectedCategoriaPosition: Int = -1
        var selectedStatusPosition: Int = -1


        var anoS = ""
        var mesS = ""
        var diaS = ""
        var anoE = ""
        var mesE = ""
        var diaE = ""
        var selectedDateStart: Date? = null
        var selectedDateEnd: Date? = null
        var selectedCategoria: Categorias? = null
        var selectedStatus: Status? = null

        binding.filterDialog.setOnClickListener {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null)

            if (viewModel.userInfo!!.tipoConta != TipoConta.VOLUNTARIO) {
                dialogView.findViewById<FrameLayout>(R.id.layoutMeus).visibility = View.GONE
            }

            val spinnerCategoria =
                dialogView.findViewById<AutoCompleteTextView>(R.id.filterCategoria)
            val spinnerStatus = dialogView.findViewById<AutoCompleteTextView>(R.id.filterStatus)
            val dataPickerStart = dialogView.findViewById<TextInputEditText>(R.id.filterDataStart)
            val dataPickerEnd = dialogView.findViewById<TextInputEditText>(R.id.filterDataEnd)
            val optionsCategoria = arrayOf("Doação", "Limpeza", "Caridade")
            val optionsStatus = arrayOf("Ativo", "Encerrado")
            val adapterCategoria =
                ArrayAdapter(requireContext(), R.layout.list_item, optionsCategoria)
            val adapterStatus = ArrayAdapter(requireContext(), R.layout.list_item, optionsStatus)
            spinnerCategoria.setAdapter(adapterCategoria)
            spinnerStatus.setAdapter(adapterStatus)

            dataPickerStart.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
                val c: Calendar = Calendar.getInstance()
                val mYear = Calendar.getInstance().get(Calendar.YEAR)
                val mMonth = Calendar.getInstance().get(Calendar.MONTH)
                val mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                context?.let { it1 ->
                    DatePickerDialog(
                        it1,
                        { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                            anoS = year.toString()
                            mesS = "%02d".format(monthOfYear + 1)
                            diaS = "%02d".format(dayOfMonth)
                            val date = "$diaS/$mesS/$anoS"
                            dataPickerStart.setText(date)
                        },
                        mYear,
                        mMonth,
                        mDay
                    )
                }?.show()
            })

            dataPickerEnd.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
                val c: Calendar = Calendar.getInstance()
                val mYear = Calendar.getInstance().get(Calendar.YEAR)
                val mMonth = Calendar.getInstance().get(Calendar.MONTH)
                val mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                context?.let { it1 ->
                    DatePickerDialog(
                        it1,
                        { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                            anoE = year.toString()
                            mesE = "%02d".format(monthOfYear + 1)
                            diaE = "%02d".format(dayOfMonth)
                            val date = "$diaE/$mesE/$anoE"
                            dataPickerEnd.setText(date)
                        },
                        mYear,
                        mMonth,
                        mDay
                    )
                }?.show()
            })


            if (anoS != "") {
                selectedDateStart = Date.valueOf("$anoS-$mesS-$diaS")
            }
            if (anoE != "") {
                selectedDateEnd = Date.valueOf("$anoE-$mesE-$diaE")
            }

            if (selectedDateStart != null) {
                val calendar = Calendar.getInstance()
                calendar.time = selectedDateStart!!
                dataPickerStart.setText("${"%02d".format(calendar.get(Calendar.DAY_OF_MONTH))}/${"%02d".format(calendar.get(Calendar.MONTH) + 1)}/${calendar.get(Calendar.YEAR)}")
            }

            if (selectedDateEnd != null) {
                val calendar = Calendar.getInstance()
                calendar.time = selectedDateEnd!!
                dataPickerEnd.setText("${"%02d".format(calendar.get(Calendar.DAY_OF_MONTH))}/${"%02d".format(calendar.get(Calendar.MONTH) + 1)}/${calendar.get(Calendar.YEAR)}")
            }


            if (selectedCategoriaPosition != -1) {
                spinnerCategoria.setText(spinnerCategoria.adapter.getItem(selectedCategoriaPosition).toString(), false);
            }

            if (selectedStatusPosition != -1) {
                spinnerStatus.setText(spinnerStatus.adapter.getItem(selectedStatusPosition).toString(), false);
            }

            spinnerCategoria.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = when (position) {
                    0 -> "DOACAO"
                    1 -> "LIMPEZA"
                    2 -> "CARIDADE"
                    else -> null
                }

                if (selectedItem != null){
                    selectedCategoria = Categorias.fromValue(selectedItem)
                    selectedCategoriaPosition = position
                }
            }

            spinnerStatus.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = when (position) {
                    0 -> "ATIVO"
                    1 -> "ENCERRADO"
                    else -> null
                }

                if (selectedItem != null){
                    selectedStatus = Status.fromValue(selectedItem)
                    selectedStatusPosition = position
                }
            }

            // Criar o AlertDialog
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Filtrar")
                .setView(dialogView)
                .setPositiveButton("Aplicar") { _, _ ->
                    // Obter valores selecionados
                    var filteredList: List<Evento>
                    viewModel.getAllEventos { eventos ->
                        filteredList = eventos.filter {
                            selectedCategoria == null || it.categoria == selectedCategoria &&
                            selectedStatus == null || it.status == selectedStatus &&
                            selectedDateStart == null || it.dataInicio == selectedDateStart &&
                            selectedDateEnd == null || it.dataFim == selectedDateEnd
                        }
                        adapter.setEventoList(filteredList)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar") { _, _ ->
                    anoS = ""
                    mesS = ""
                    diaS = ""
                    anoE = ""
                    mesE = ""
                    diaE = ""
                    selectedDateStart = null
                    selectedDateEnd = null
                    selectedCategoria = null
                    selectedStatus = null
                    selectedCategoriaPosition = -1
                    spinnerCategoria.dismissDropDown()
                    spinnerStatus.dismissDropDown()
                    selectedStatusPosition = -1
                    dataPickerStart.setText("")
                    dataPickerEnd.setText("")
                    viewModel.getAllEventos { eventos ->
                        adapter.setEventoList(eventos)
                    }
                }
                .create()

            alertDialog.show()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}