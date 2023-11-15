package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voluntask.R
import com.example.voluntask.adapters.EventoAdapter
import com.example.voluntask.databinding.FragmentHomeBinding
import com.example.voluntask.databinding.FragmentLoginBinding
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.viewmodels.HomeViewModel
import com.google.android.material.textfield.TextInputEditText
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
        val userInfo = arguments?.getParcelable<Usuario>("userInfo")
        viewModel.userInfo = userInfo
        val adapter = EventoAdapter {
//            openLink(it.link)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.getAllEventos { eventos ->
            adapter.setEventoList(eventos)
        }

        binding.filterDialog.setOnClickListener{
            // Inflar o layout personalizado
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null)

//            if (viewModel.userInfo!!.tipoConta != TipoConta.VOLUNTARIO){
//                dialogView.findViewById<FrameLayout>(R.id.layoutMeus).visibility = View.GONE
//            }

            // Configurar o Spinner com opções
            var ano = ""; var mes = ""; var dia = ""; var idade = 0
            val spinnerCategoria = dialogView.findViewById<AutoCompleteTextView>(R.id.filterCategoria)
            val spinnerStatus = dialogView.findViewById<AutoCompleteTextView>(R.id.filterStatus)
            val dataPicker = dialogView.findViewById<TextInputEditText>(R.id.filterData)
            val optionsCategoria = arrayOf("Doação", "Limpeza", "Caridade")
            val optionsStatus = arrayOf("Ativo", "Encerrado")
            val adapterCategoria = ArrayAdapter(requireContext(), R.layout.list_item, optionsCategoria)
            val adapterStatus = ArrayAdapter(requireContext(), R.layout.list_item, optionsStatus)
            spinnerCategoria.setAdapter(adapterCategoria)
            spinnerStatus.setAdapter(adapterStatus)

            dataPicker.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
                val c: Calendar = Calendar.getInstance()
                val mYear = Calendar.getInstance().get(Calendar.YEAR)
                val mMonth = Calendar.getInstance().get(Calendar.MONTH)
                val mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                context?.let { it1 ->
                    DatePickerDialog(
                        it1,
                        { _, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                            idade = mYear - year
                            ano = year.toString()
                            mes = "%02d".format(monthOfYear + 1)
                            dia = "%02d".format(dayOfMonth)
                            val date = "$dia/$mes/$ano"
                            dataPicker.setText(date)
                        },
                        mYear,
                        mMonth,
                        mDay
                    )
                }?.show()
            })


            // Criar o AlertDialog
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Filtrar")
                .setView(dialogView)
                .setPositiveButton("Aplicar") { dialog, _ ->
                    // Obter valores selecionados
                    val selectedOptionCategoria = spinnerCategoria.listSelection
                    val selectedOptionStatus = spinnerStatus.listSelection
                    val selectedDate = "$ano-$mes-$dia"

                    //aplicar filtros
                }
                .setNegativeButton("Cancelar", null)
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