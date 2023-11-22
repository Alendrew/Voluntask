package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentEventoBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.EventoViewModel
import com.example.voluntask.viewmodels.SharedViewModel
import java.time.LocalDate
import java.util.Calendar

class EventoFragment : Fragment() {


    private lateinit var binding: FragmentEventoBinding
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
        binding = FragmentEventoBinding.inflate(inflater, container, false)
        val evento = arguments?.getParcelable<Evento>("evento")
        val viewModel = ViewModelProvider(this)[EventoViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        var anoS = ""
        var mesS = ""
        var diaS = ""
        var anoE = ""
        var mesE = ""
        var diaE = ""
        var selectedDateStart: LocalDate? = null
        var selectedDateEnd: LocalDate? = null

        val categoria: Categorias = Categorias.CARIDADE
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI

        val optionsCategoria = arrayOf("Doação", "Limpeza", "Caridade")
        val adapterCategoria =
            ArrayAdapter(requireContext(), R.layout.list_item, optionsCategoria)
        binding.inputCategoria.setAdapter(adapterCategoria)

        if (evento != null) {
            binding.h1.text = "Editar Evento"
            binding.btnRegister.text = "Salvar"
            binding.inputStatusLayout.visibility = View.VISIBLE
            binding.btnExcluirEvento.visibility = View.VISIBLE
            binding.inputNome.setText(evento.nome)
            binding.inputLocal.setText(evento.localizacao)
            binding.inputDesc.setText(evento.descricao)
            val dataInicio = LocalDate.parse(evento.dataInicio.toString())
            val dataFim = LocalDate.parse(evento.dataFim.toString())

            val optionsStatus = listOf("Ativo", "Encerrado")
            val adapterStatus =
                ArrayAdapter(requireContext(), R.layout.list_item, optionsStatus)
            binding.inputStatus.setAdapter(adapterStatus)

            val selectedCategoriaPosition = when (evento.categoria) {
                Categorias.DOACAO -> 0
                Categorias.LIMPEZA -> 1
                Categorias.CARIDADE -> 2
            }

            val selectedStatusPosition = when (evento.status) {
                Status.ATIVO -> 0
                Status.ENCERRADO -> 1
            }

            binding.inputCategoria.setText(
                binding.inputCategoria.adapter.getItem(selectedCategoriaPosition).toString(), false
            );

            binding.inputStatus.setText(
                binding.inputStatus.adapter.getItem(selectedStatusPosition).toString(), false
            );

            diaS = dataInicio.dayOfMonth.toString()
            mesS = dataInicio.month.value .toString()
            anoS = dataInicio.year.toString()
            diaE = dataFim.dayOfMonth.toString()
            mesE = dataFim.month.value.toString()
            anoE = dataFim.year.toString()


            binding.inputDataS.setText(
                "${"%02d".format(dataInicio.dayOfMonth)}/${
                    "%02d".format(dataInicio.month.value)
                }/${dataInicio.year}"
            )


            binding.inputDataE.setText(
                "${"%02d".format(dataFim.dayOfMonth)}/${
                    "%02d".format(dataFim.month.value)
                }/${dataFim.year}"
            )
        }
        binding.h1.visibility = View.VISIBLE
        binding.btnRegister.visibility = View.VISIBLE

        binding.inputDataS.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
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
                        binding.inputDataS.setText(date)
                    },
                    mYear,
                    mMonth,
                    mDay
                )
            }?.show()
        })

        binding.inputDataE.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
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
                        binding.inputDataE.setText(date)
                    },
                    mYear,
                    mMonth,
                    mDay
                )
            }?.show()
        })


        binding.btnExcluirEvento.setOnClickListener {
            val bundle = bundleOf("evento" to evento)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_EventoFragment_to_confirmarExcluirFragment, bundle)
        }

        binding.btnRegister.setOnClickListener {
            val nome = binding.inputNome.text.toString()
            val localizacao = binding.inputLocal.text.toString()
            val descricao = binding.inputDesc.text.toString()
            val dataS = binding.inputDataS.text.toString()
            val dataE = binding.inputDataE.text.toString()
            val dataCadastro = LocalDate.now()

            if (
                nome.isBlank() ||
                localizacao.isBlank() ||
                descricao.isBlank() ||
                dataS.isBlank() ||
                binding.inputCategoria.text.toString().isBlank() ||
                dataE.isBlank()
            ) {
                customToast.showCustomToast(
                    "Todos os campos precisam ser preenchidos",
                    Types.WARNING
                )
            } else {

                var userInfo: Usuario? = null

                sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
                    userInfo = usuario
                }

                val newEvento = Evento(
                    nome,
                    localizacao,
                    descricao,
                    userInfo!!.idUsuario,
                    "$anoS-$mesS-$diaS",
                    "$anoE-$mesE-$diaE",
                    "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}",
                    categoria,
                    Status.ATIVO
                )
                if (evento != null) {
                    newEvento.status = Status.fromValue(binding.inputStatus.text.toString())
                }
                if (evento == null) {
                    loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    viewModel.createEvento(newEvento) {
                        if (it) {
                            customToast.showCustomToast("Evento criado com sucesso", Types.SUCESS)
                            val delayMillis = 2000L // 2 segundos

                            // Use um Handler para ocultar a Toast após o atraso
                            Handler(Looper.getMainLooper()).postDelayed({
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_EventoFragment_to_homeFragment)
                            }, delayMillis)
                        } else {
                            // O registro não foi bem-sucedido
                            loadingUI.loadingToBtn()
                            customToast.showCustomToast("Erro ao criar evento", Types.ERROR)
                        }
                    }
                } else {
                    loadingUI = LoadingUI(binding.btnRegister, binding.progressCircular, null)
                    loadingUI.btnToLoading()
                    newEvento.idEvento = evento.idEvento
                    viewModel.updateEvento(newEvento) {
                        if (it) {
                            customToast.showCustomToast(
                                "Evento atualizado com sucesso",
                                Types.SUCESS
                            )
                            val delayMillis = 2000L // 2 segundos

                            // Use um Handler para ocultar a Toast após o atraso
                            Handler(Looper.getMainLooper()).postDelayed({
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_EventoFragment_to_homeFragment)
                            }, delayMillis)
                        } else {
                            // O registro não foi bem-sucedido
                            loadingUI.loadingToBtn()
                            customToast.showCustomToast("Erro ao atualizar evento", Types.ERROR)
                        }
                    }
                }

            }

        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}