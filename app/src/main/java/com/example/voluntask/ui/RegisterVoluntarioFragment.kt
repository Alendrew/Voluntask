package com.example.voluntask.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentRegisterVoluntarioBinding
import com.example.voluntask.models.Voluntario
import com.example.voluntask.models.enums.Generos
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.util.CustomToast
import com.example.voluntask.util.LoadingUI
import com.example.voluntask.util.Types
import com.example.voluntask.viewmodels.AuthViewModel
import java.sql.Date.valueOf
import java.time.Instant
import java.util.Calendar
import java.util.Date


class RegisterVoluntarioFragment : Fragment() {

    private lateinit var binding: FragmentRegisterVoluntarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterVoluntarioBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val customToast = CustomToast(binding.customToast, requireContext())
        val items = listOf("Masculino", "Feminino", "Prefiro não dizer")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        var ano = ""; var mes = ""; var dia = ""; var idade = 0
        var genero: Generos = Generos.PREFIRO_NAO_DIZER
        var loadingUI: LoadingUI
        (binding.inputGeneroLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.inputData.setOnClickListener(View.OnClickListener { // calender class's instance and get current date , month and year from calender
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
                        binding.inputData.setText(date)
                    },
                    mYear,
                    mMonth,
                    mDay
                )
            }?.show()
        })

        binding.inputGenero.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            genero = Generos.fromValue(position)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val senha = binding.inputSenha.text.toString()
            val nome = binding.inputNome.text.toString()
            val telefone = binding.inputTelefone.unMasked
            val cpf = binding.inputCpf.unMasked
            val dataNascimento = binding.inputData.text.toString()
            val confirmarSenha = binding.inputConfirmarSenha.text.toString()
            val dataCadastro = Date.from(Instant.now())
            val tipoConta = TipoConta.VOLUNTARIO

            if (
                nome.isBlank() ||
                email.isBlank() ||
                telefone.isBlank() ||
                dataNascimento.isBlank() ||
                cpf.isBlank() ||
                senha.isBlank() ||
                confirmarSenha.toString().isBlank()
            ) {
                customToast.showCustomToast("Todos os campos precisam ser preenchidos",Types.WARNING)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                    .matches()
            ) {
                customToast.showCustomToast("E-mail inválido",Types.ERROR)
            } else if (senha != confirmarSenha) {
                customToast.showCustomToast("As senhas não conferem",Types.ERROR)
            } else if (senha.length < 6) {
                customToast.showCustomToast("Senha menor que 6 caracteres",Types.ERROR)
            } else if (!binding.inputCpf.isDone) {
                customToast.showCustomToast("Cpf inválido",Types.ERROR)
            } else if (!binding.inputTelefone.isDone) {
                customToast.showCustomToast("Número de telefone inválido",Types.ERROR)
            } else if (idade < 18) {
                customToast.showCustomToast("Idade menor que 18 anos",Types.ERROR)
            } else {

                val voluntario =
                    Voluntario(nome, telefone, dataCadastro, tipoConta, "", valueOf("$ano-$mes-$dia"), cpf, genero)

                loadingUI = LoadingUI(binding.btnRegister,binding.progressCircular,null)
                loadingUI.btnToLoading()
                viewModel.register(voluntario, email, senha) {
                    if (it.result) {
                        customToast.showCustomToast(it.msg, Types.SUCESS)
                        val delayMillis = 2000L // 2 segundos

                        // Use um Handler para ocultar a Toast após o atraso
                        Handler(Looper.getMainLooper()).postDelayed({
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.action_registerVoluntarioFragment_to_loginFragment)
                        }, delayMillis)
                    } else {
                        // O registro não foi bem-sucedido
                        loadingUI.loadingToBtn()
                        customToast.showCustomToast(it.msg,Types.ERROR)
                    }
                }
            }
        }

        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterVoluntarioFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
