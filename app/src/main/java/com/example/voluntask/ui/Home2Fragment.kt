package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voluntask.R
import com.example.voluntask.adapters.EventoAdapter
import com.example.voluntask.databinding.FragmentHome2Binding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Evento.Companion.toEvento
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.viewmodels.EventoViewModel
import com.example.voluntask.viewmodels.InscricaoViewModel
import com.example.voluntask.viewmodels.SharedViewModel
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.util.Calendar

class Home2Fragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val viewModel = ViewModelProvider(this)[InscricaoViewModel::class.java]

//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            Navigation.findNavController(binding.root)
//                .navigate(R.id.action_home2Fragment_to_homeFragment)
//        }

        binding.btnDisponiveis.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_home2Fragment_to_homeFragment)
        }

        val adapter = EventoAdapter {
            val bundle = bundleOf("evento" to it)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_home2Fragment_to_inscricaoFragment, bundle)
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            binding.swipeRefreshLayout.isRefreshing = true

            // Chame a função assíncrona para obter os dados
            viewModel.getInscricoesAndEventos(usuario.idUsuario) { inscricoesAndEventos ->
                // Este código será executado quando a função assíncrona for concluída

                // Atualize o RecyclerView com os dados
                adapter.setEventoList(inscricoesAndEventos)

                // Oculte a tela de carregamento depois de atualizar o RecyclerView
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        var selectedCategoriaPosition: Int = -1
        var selectedStatusPosition: Int = -1
        var selectedName: String = ""

        var anoS = ""
        var mesS = ""
        var diaS = ""
        var anoE = ""
        var mesE = ""
        var diaE = ""
        var selectedDateStart: LocalDate? = null
        var selectedDateEnd: LocalDate? = null
        var selectedCategoria: Categorias? = null
        var selectedStatus: Status? = null

        binding.swipeRefreshLayout.setOnRefreshListener {
            val eventos: List<Evento> = adapter.getItems()
            val filteredList = eventos.filter {
                (selectedCategoria == null || it.categoria == selectedCategoria) &&
                        (selectedStatus == null || it.status == selectedStatus) &&
                        (selectedDateStart == null || it.dataInicio == selectedDateStart.toString()) &&
                        (selectedDateEnd == null || it.dataFim == selectedDateEnd.toString()) &&
                        (selectedName == "" || it.nome == selectedName)
            }

            adapter.setEventoList(filteredList)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.filterDialog.setOnClickListener {

            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null)

            val spinnerStatus = dialogView.findViewById<AutoCompleteTextView>(R.id.filterStatus)

            val nameEvento = dialogView.findViewById<TextInputEditText>(R.id.filterName)
            val spinnerCategoria =
                dialogView.findViewById<AutoCompleteTextView>(R.id.filterCategoria)
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
                            selectedDateStart = LocalDate.parse("$anoS-$mesS-$diaS")
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
                            selectedDateEnd = LocalDate.parse("$anoE-$mesE-$diaE")
                            dataPickerEnd.setText(date)
                        },
                        mYear,
                        mMonth,
                        mDay
                    )
                }?.show()
            })


            if (selectedDateStart != null) {
                dataPickerStart.setText(
                    "${"%02d".format(selectedDateStart!!.dayOfMonth)}/${
                        "%02d".format(selectedDateStart!!.month.value + 1)
                    }/${selectedDateStart!!.year}"
                )
            }
            if (selectedDateEnd != null) {
                dataPickerEnd.setText(
                    "${"%02d".format(selectedDateEnd!!.dayOfMonth)}/${
                        "%02d".format(selectedDateEnd!!.month.value + 1)
                    }/${selectedDateEnd!!.year}"
                )
            }

            if (selectedName != "") {
                nameEvento.setText(selectedName)
            }


            if (selectedCategoriaPosition != -1) {
                spinnerCategoria.setText(
                    spinnerCategoria.adapter.getItem(selectedCategoriaPosition).toString(), false
                );
            }

            if (selectedStatusPosition != -1) {
                spinnerStatus.setText(
                    spinnerStatus.adapter.getItem(selectedStatusPosition).toString(), false
                );
            }

            spinnerCategoria.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = when (position) {
                    0 -> "DOACAO"
                    1 -> "LIMPEZA"
                    2 -> "CARIDADE"
                    else -> null
                }

                if (selectedItem != null) {
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

                if (selectedItem != null) {
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
                    selectedName = nameEvento.text.toString()
                    val eventos: List<Evento> = adapter.getItems()
                    val filteredList = eventos.filter {
                        (selectedCategoria == null || it.categoria == selectedCategoria) &&
                                (selectedStatus == null || it.status == selectedStatus) &&
                                (selectedDateStart == null || it.dataInicio == selectedDateStart.toString()) &&
                                (selectedDateEnd == null || it.dataFim == selectedDateEnd.toString()) &&
                                (selectedName == "" || it.nome == selectedName)
                    }

                    adapter.setEventoList(filteredList)
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
                    selectedName = ""
                    sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
                        binding.swipeRefreshLayout.isRefreshing = true

                        // Chame a função assíncrona para obter os dados
                        viewModel.getInscricoesAndEventos(usuario.idUsuario) { inscricoesAndEventos ->
                            // Este código será executado quando a função assíncrona for concluída

                            // Atualize o RecyclerView com os dados
                            adapter.setEventoList(inscricoesAndEventos)

                            // Oculte a tela de carregamento depois de atualizar o RecyclerView
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
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