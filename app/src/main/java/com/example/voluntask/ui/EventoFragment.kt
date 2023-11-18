package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentEventoBinding
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
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
        val viewModel = ViewModelProvider(this)[EventoViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        var anoS = ""
        var mesS = ""
        var diaS = ""
        var anoE = ""
        var mesE = ""
        var diaE = ""
        var categoria: Categorias = Categorias.CARIDADE
        val customToast = CustomToast(binding.customToast, requireContext())
        var loadingUI: LoadingUI

        val optionsCategoria = arrayOf("Doação", "Limpeza", "Caridade")
        val adapterCategoria =
            ArrayAdapter(requireContext(), R.layout.list_item, optionsCategoria)
        binding.inputCategoria.setAdapter(adapterCategoria)

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


        binding.inputCategoria.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = when (position) {
                0 -> "DOACAO"
                1 -> "LIMPEZA"
                2 -> "CARIDADE"
                else -> ""
            }
            categoria = Categorias.fromValue(selectedItem)
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
                customToast.showCustomToast("Todos os campos precisam ser preenchidos", Types.WARNING)
            } else {

                var userInfo: Usuario? = null

                sharedViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
                    userInfo = usuario
                }

                val evento =
                    Evento(nome, localizacao, descricao, userInfo!!.idUsuario, "$anoS-$mesS-$diaS", "$anoE-$mesE-$diaE", "${dataCadastro.year}-${dataCadastro.month.value}-${dataCadastro.dayOfMonth}", categoria, Status.ATIVO)

                loadingUI = LoadingUI(binding.btnRegister,binding.progressCircular,null)
                loadingUI.btnToLoading()
                viewModel.createEvento(evento) {
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